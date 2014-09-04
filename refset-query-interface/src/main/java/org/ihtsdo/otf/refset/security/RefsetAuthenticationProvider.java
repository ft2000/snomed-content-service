package org.ihtsdo.otf.refset.security;

import java.util.ArrayList;
import java.util.Collection;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
        
        MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
        params.add("username", userName);
        params.add("password", token);
        params.add("queryName", "getUserByNameAuth");
    
        		
		try {
			
			Assert.notNull(rt, "Rest template can not be empty");
			System.err.println(otfServiceUrl);
			System.err.println(params);

			JSONObject obj = rt.postForObject(otfServiceUrl, params, JSONObject.class);
			
			System.out.println(obj);
			
			JSONObject userJson = obj.getJSONObject("user");
			String id = userJson.getString("name");
			String status = userJson.getString("status");
			Assert.notNull(status, "Status can not be empty");
			boolean authenticated =  status.equalsIgnoreCase("enabled") ? true : false;
			
			user.setUsername(id);
			user.setPassword(token);
			//now check if user has access to Refset app.
			params = new LinkedMultiValueMap<String, String>();
	        params.add("username", "pnema");
	        params.add("queryName", "getUserApps");

	        JSONObject appJson = rt.postForObject(otfServiceUrl, params, JSONObject.class);
					        
	        JSONArray apps = appJson.getJSONArray("apps");

	        for (Object object : apps) {
	        	
				if (object.equals(APP_NAME)) {
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
			
			LOGGER.error("Error during authentication {}", e);
			user = new User();
			user.setAuthenticated(false);
        	LOGGER.info("Check TODO on this code");

			//throw new AccessDeniedException(e.getLocalizedMessage()); //TODO this need a custom exception handling after spring securirty exception filter
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
