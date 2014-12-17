/**
* Copyright 2014 IHTSDO
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.ihtsdo.otf.refset.security;

import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.token.Token;
import org.springframework.security.core.token.TokenService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;

/**
 *
 */
public class RefsetIdentityService implements UserDetailsService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RefsetIdentityService.class);
	
	private static final String ROLE_USER = "ROLE_USER";
	//private static final String ROLE_ADMIN = "ROLE_ADMIN";

	private static final String ROLE_ANONYMOUS = "ROLE_ANONYMOUS";

	private static final String APP_NAME = "Refset";
	private String otfServiceUrl;
	

	
	private RestTemplate rt;

	TokenService service;


	/* (non-Javadoc)
	 * @see org.springframework.security.core.userdetails.UserDetailsService#loadUserByUsername(java.lang.String)
	 */
	@Override
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {

		User u = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		if (u.getUsername().equals(username)) {
			
			return u;
			
		} else {
			String msg = String.format("User with given user name {} does not exist", username);
			throw new UsernameNotFoundException(msg);
		}
	}
	
	
	
	protected UserDetails authenticate(String userName, String token) {

		LOGGER.debug("Authenticating user {} ", userName);

		
		User user = getGuestUser();
		
		
        MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
        params.add("username", userName);
        params.add("password", token);
        params.add("queryName", "getUserByNameAuth");
        
        		
		try {
			
			if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(token)) {
				
				throw new AccessDeniedException("User is unauthorized. Please check user name and password");
			}
			Assert.notNull(rt, "Rest template can not be empty");
			
			LOGGER.debug("Calling authentication service with URL {}, User {} and Parameters {} ", otfServiceUrl, userName);

			JsonNode obj = rt.postForObject(otfServiceUrl, params, JsonNode.class);

			LOGGER.debug("authentication service call successfully returned with {} ", obj);

			//populate user with other user details
			populateUser(user, obj);

			//now check if user has access to Refset app.
			params = new LinkedMultiValueMap<String, String>();
	        params.add("username", userName);
	        params.add("queryName", "getUserApps");

			LOGGER.debug("Calling autorization service with URL {}, User {} and Parameters {} ", otfServiceUrl, userName);

			JsonNode appJson = rt.postForObject(otfServiceUrl, params, JsonNode.class);

			LOGGER.debug("autorization service call successfully returned with {} ", appJson);

			JsonNode apps = appJson.get("apps");
			Collection<RefsetRole> roles = new ArrayList<RefsetRole>();			        

	        for (JsonNode object : apps) {
	        	
				if (object != null && object.asText().equals(APP_NAME)) {

			        RefsetRole role = new RefsetRole();
			        role.setAuthority(ROLE_USER);
			        roles.add(role);
			        break;
				}
			}
			user.setAuthorities(roles);

			if (isUserHasRole(user)) {
				
				 String info = userName  + ":" + token;
				 Token key = service.allocateToken(info);
				 user.setToken(key.getKey());
			}


		} catch (Exception e) {
			
			LOGGER.error("Error during authentication for user:password - {} ", userName + ":" + token, e);

			throw new AccessDeniedException("User is unauthorized. Please check user name and password");
		}
	
		

		return user;
	
	}
	
	/**
	 * @param obj
	 */
	private User populateUser(User user, JsonNode obj) {

		//mandatory values
		JsonNode userJson = obj.get("user");
		String id = userJson.findValue("name").asText();
		String status = userJson.findValue("status").asText();

		boolean authenticated = !StringUtils.isEmpty(status) && status.equalsIgnoreCase("enabled") ? true : false;
		
		user.setUsername(id);
		user.setPassword(userJson.findValue("token").asText());
		user.setAuthenticated(authenticated);
		user.setEnabled(authenticated);
		//other details
		JsonNode email = userJson.findValue("email");
		
		if(email != null) {
			
			user.setEmail(email.asText());
			
		}
		JsonNode middleName = userJson.findValue("middleName");
		
		if(middleName != null) {
			
			user.setMiddlename(middleName.asText());
			
		}

		JsonNode givenName = userJson.findValue("givenName");
		
		if(givenName != null) {
			
			user.setGivenname(givenName.asText());
			
		}

		
		JsonNode surname = userJson.findValue("surname");
		
		if(surname != null) {
			
			user.setSurname(surname.asText());
			
		}

		
		
		return user;
	}



	/**TODO need 
	 * Gets a brand new user with default role assigned
	 * @return
	 */
	public User getGuestUser() {
		
        Collection<RefsetRole> roles = new ArrayList<RefsetRole>();
        User user = new User(); 
        
        RefsetRole role = new RefsetRole();
        role.setAuthority(ROLE_ANONYMOUS);
        roles.add(role);
        user.setAuthorities(roles);
        user.setUsername("guest");
        user.setPassword("guest");
        return user;
	}
	
	/**
	 * Gets a minimal {@link Authentication} object from {@link Token}. 
	 * It is only possible if that Token exist. At the moment ROLE_USER is defaulted
	 * @return
	 */
	protected Authentication getPrincipal(Token token) {
		
		String info = token.getExtendedInformation();
		String [] details = StringUtils.split(info, ":");

		User user = new User();
		user.setPassword(details[1]);
		user.setUsername(details[0]);
		user.setAuthenticated(true);
		user.setEnabled(true);
        Collection<RefsetRole> roles = new ArrayList<RefsetRole>();

        RefsetRole role = new RefsetRole();
        role.setAuthority(ROLE_USER);
        roles.add(role);

        user.setAuthorities(roles);

        Authentication auth = new UsernamePasswordAuthenticationToken(user, details[1], roles);

		return auth;
	}
	
	
	private boolean isUserHasRole(User auth) {
		
		boolean isUserHasRole = false;
		
		if (auth!=null && auth.isAuthenticated()) {
			
			 Collection<? extends GrantedAuthority> roles = auth.getAuthorities();
			 for (GrantedAuthority role : roles) {
				
				 isUserHasRole = "ROLE_USER".equals(role.getAuthority()) ? true : false;
				 
				 if (isUserHasRole) {
					
					 break;
				}
			}

		}
		
		return isUserHasRole;
	}


	/**
	 * @param otfServiceUrl the otfServiceUrl to set
	 */
	public void setOtfServiceUrl(String otfServiceUrl) {
		this.otfServiceUrl = otfServiceUrl;
	}

	/**
	 * @param rt the rt to set
	 */
	public void setRt(RestTemplate rt) {
		this.rt = rt;
	}



	/**
	 * @param service the service to set
	 */
	public void setService(TokenService service) {
		this.service = service;
	}

}
