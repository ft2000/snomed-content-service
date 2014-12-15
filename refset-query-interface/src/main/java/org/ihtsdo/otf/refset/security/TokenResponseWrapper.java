/**
 * 
 */
package org.ihtsdo.otf.refset.security;

import java.util.Collection;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.token.Token;
import org.springframework.security.core.token.TokenService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.SaveContextOnUpdateOrErrorResponseWrapper;

/**Class to save X-REFSET-AUTH-TOKEN in {@link SecurityContext} so that it can be used
 * for handshake between frontend and api backend
 *
 */
public final class TokenResponseWrapper extends  
				SaveContextOnUpdateOrErrorResponseWrapper {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TokenResponseWrapper.class);
	
	private static final String X_REFSET_TOKEN = "X-REFSET-AUTH-TOKEN";

	
	TokenService service;


	public TokenResponseWrapper(HttpServletResponse response,
			boolean disableUrlRewriting, TokenService service) {
		
		super(response, disableUrlRewriting);
		this.service = service;
	}

	@Override
	protected void saveContext(SecurityContext ctx) {
		
		 Authentication auth = ctx.getAuthentication();
		 if (isUserHasRole(auth)) {
			 
			 User uDetails = (User)auth.getPrincipal();
			 String userId = uDetails.getUsername();
			 String password = uDetails.getPassword();
			 String info = userId  + ":" + password;
			 Token token = service.allocateToken(info);
			 String key = token.getKey();
			 uDetails.setToken(key);

			 LOGGER.trace("Setting {} as {}", X_REFSET_TOKEN, key);
			 addHeader(X_REFSET_TOKEN, key);
		 }
		
	}
	
	private boolean isUserHasRole(Authentication auth) {
		
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
	

}
