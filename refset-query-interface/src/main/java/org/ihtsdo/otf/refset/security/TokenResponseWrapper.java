/**
 * 
 */
package org.ihtsdo.otf.refset.security;

import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.token.Token;
import org.springframework.security.core.token.TokenService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.SaveContextOnUpdateOrErrorResponseWrapper;

/**
 * @author Episteme Partners
 *
 */
public final class TokenResponseWrapper extends  
				SaveContextOnUpdateOrErrorResponseWrapper {
	
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
		 if(auth!=null && auth.isAuthenticated()) {
			 
			 UserDetails uDetails = (UserDetails)auth.getPrincipal();
			 String userId = uDetails.getUsername();
			 String password = uDetails.getPassword();
			 String info = userId  + ":" + password;
			 Token token = service.allocateToken(info);
			 addHeader(X_REFSET_TOKEN, token.getKey());
		 }
		
	}

}
