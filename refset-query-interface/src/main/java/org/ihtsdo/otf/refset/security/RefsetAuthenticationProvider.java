package org.ihtsdo.otf.refset.security;

import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;

/**Custom refset authentication provider 
 * @author Episteme Partners
 *
 */
public class RefsetAuthenticationProvider implements AuthenticationProvider {

	private static final Logger LOGGER = LoggerFactory.getLogger(RefsetAuthenticationProvider.class);

	private static final String ROLE_USER = "ROLE_USER";
	private static final String ROLE_ANONYMOUS = "ROLE_ANONYMOUS";

	private static final String APP_NAME = "Refset";
	private String otfServiceUrl;
	

	
	private RestTemplate rt;

	private boolean authenticationEnabled;
	
	
	/**
	 * @param authenticationEnabled the authenticationEnabled to set
	 */
	public void setAuthenticationEnabled(boolean authenticationEnabled) {
		this.authenticationEnabled = authenticationEnabled;
	}

	@Override
	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {
		
		UserDetails input = (UserDetails)authentication.getPrincipal();
		
		String userName = input.getUsername();
		String token = input.getPassword();
		final Authentication auth;
		if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(token))  {
			
			User user = getUser();
			
	        auth = new AnonymousAuthenticationToken("guest", user, user.getAuthorities());

		} else {
		
			UserDetails user = authenticate(userName, token);
			
	        auth = new UsernamePasswordAuthenticationToken(user, token, user.getAuthorities());

	        //TODO Remove this after custom exception translator filter
	        if (user.getAuthorities().isEmpty()) {
				
	        	LOGGER.info("Remove this code");
	        	auth.setAuthenticated(false);
			}
		}
		
        return auth;
        
	}

	@Override
	public boolean supports(Class<?> authentication) {

		return authentication.equals(UsernamePasswordAuthenticationToken.class);
		
	}
	
	
	private UserDetails authenticate(String userName, String token) {

		LOGGER.debug("Authenticating user {} ", userName);

		
		User user = getUser();
		
		if (!authenticationEnabled) {
			
			user.setAuthenticated(true);
			Collection<RefsetRole> roles = new ArrayList<RefsetRole>();			        
	        RefsetRole role = new RefsetRole();
	        role.setAuthority(ROLE_USER);
	        roles.add(role);
			user.setAuthorities(roles);
			if (!StringUtils.isEmpty(userName)) {
				
				user.setUsername(userName);

			}
			return user;
		}
        
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

			JsonNode userJson = obj.get("user");
			String id = userJson.findValue("name").asText();
			String status = userJson.findValue("status").asText();

			boolean authenticated = !StringUtils.isEmpty(status) && status.equalsIgnoreCase("enabled") ? true : false;
			
			user.setUsername(id);
			user.setPassword(token);
			//now check if user has access to Refset app.
			params = new LinkedMultiValueMap<String, String>();
	        params.add("username", userName);
	        params.add("queryName", "getUserApps");

			LOGGER.debug("Calling authentication service with URL {}, User {} and Parameters {} ", otfServiceUrl, userName);

			JsonNode appJson = rt.postForObject(otfServiceUrl, params, JsonNode.class);

			LOGGER.debug("authentication service call successfully returned with {} ", appJson);

			JsonNode apps = appJson.get("apps");

	        for (JsonNode object : apps) {
	        	
				if (object != null && object.asText().equals(APP_NAME)) {
					//TODO need revisiting as we will need to cater specific roles
					user.setAuthenticated(authenticated);
					Collection<RefsetRole> roles = new ArrayList<RefsetRole>();			        
			        RefsetRole role = new RefsetRole();
			        role.setAuthority(ROLE_USER);
			        roles.add(role);
					user.setAuthorities(roles);
					
					break;
				}
			}


		} catch (Exception e) {
			
			LOGGER.error("Error during authentication for user:password - {} ", userName + ":" + token, e);

			//throw new AccessDeniedException("User is unauthorized. Please check user name and password");
		}
	
		

		return user;
	
	}
	
	/**TODO need 
	 * Gets a brand new user with default role assigned
	 * @return
	 */
	private User getUser() {
		
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

}
