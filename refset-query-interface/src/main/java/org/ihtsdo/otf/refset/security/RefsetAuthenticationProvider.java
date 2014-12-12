package org.ihtsdo.otf.refset.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

/**Custom refset authentication provider 
 *
 */
public class RefsetAuthenticationProvider implements AuthenticationProvider {

	private static final Logger LOGGER = LoggerFactory.getLogger(RefsetAuthenticationProvider.class);
	
	
	private RefsetIdentityService service;

	@Override
	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {
		
		LOGGER.debug("Authenticate {}", authentication);
		UserDetails input = (UserDetails)authentication.getPrincipal();
		
		String userName = input.getUsername();
		String password = input.getPassword();
		final Authentication auth;
		if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(password))  {
			
			User user = service.getGuestUser();
			
	        auth = new AnonymousAuthenticationToken("guest", user, user.getAuthorities());

		} else {
			
			UserDetails user = service.authenticate(userName, password);
			
	        auth = new UsernamePasswordAuthenticationToken(user, password, user.getAuthorities());
	        
		}
		
        return auth;
        
	}

	@Override
	public boolean supports(Class<?> authentication) {

		return authentication.equals(UsernamePasswordAuthenticationToken.class);
		
	}

	/**
	 * @param service the service to set
	 */
	public void setService(RefsetIdentityService service) {
		this.service = service;
	}
	
	


}
