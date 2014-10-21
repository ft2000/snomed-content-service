/**
 * 
 */
package org.ihtsdo.otf.refset.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.token.Token;
import org.springframework.security.core.token.TokenService;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.util.StringUtils;

/**
 * @author Episteme Partners
 *
 */
public class RefsetSecurityContextRepository implements
		SecurityContextRepository {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RefsetSecurityContextRepository.class);
	private static final String X_REFSET_TOKEN = "X-REFSET-AUTH-TOKEN";
	
	private String preAuthTokenKey;
	private String userKey;
	
	  
	private TokenService service;

	private AuthenticationManager mgr;
	
	
	
	public RefsetSecurityContextRepository(AuthenticationManager mgr, TokenService service) {
		
		this.mgr = mgr;
		this.service = service;
		
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.web.context.SecurityContextRepository#loadContext(org.springframework.security.web.context.HttpRequestResponseHolder)
	 */
	@Override
	public SecurityContext loadContext(
			HttpRequestResponseHolder rh) {

		HttpServletRequest request = rh.getRequest();
		HttpServletResponse response = rh.getResponse();
		SecurityContext ctx = getContextFromHeaders(request);
		
		if (ctx == null) {
			
			ctx = SecurityContextHolder.createEmptyContext();
			rh.setResponse(new TokenResponseWrapper(response, false, service));
			
		}
		
		return ctx;
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.web.context.SecurityContextRepository#saveContext(org.springframework.security.core.context.SecurityContext, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public void saveContext(SecurityContext context,
			HttpServletRequest request, HttpServletResponse response) {
		
		new TokenResponseWrapper(response, false, service).saveContext(context);
		
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.web.context.SecurityContextRepository#containsContext(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public boolean containsContext(HttpServletRequest request) {

		return  getContextFromHeaders(request) != null;
	}
	
	
	private SecurityContext getContextFromHeaders(HttpServletRequest req) {
		final SecurityContext ctx;
		
		
		String tokenKey = req.getHeader(X_REFSET_TOKEN);
		if (!StringUtils.isEmpty(tokenKey)) {
			
			final Token token;
			try {
				
				token = service.verifyToken(tokenKey);
				
				if (token != null) {
					
					//set details in Authentication objects
					String info = token.getExtendedInformation();
					String [] details = StringUtils.split(info, ":");

					final User user = new User();
					user.setPassword(details[1]);
					user.setUsername(details[0]);
					//need to do authentication every time as we are not storing user details
					Authentication auth = mgr.authenticate(new UsernamePasswordAuthenticationToken(user, details[1]));
					ctx = SecurityContextHolder.createEmptyContext();
					ctx.setAuthentication(auth);
					
					 
				} else {
					
					//do the authentication again;
					Authentication auth = mgr.authenticate(new UsernamePasswordAuthenticationToken(getUser(req), getToken(req)));
					ctx = SecurityContextHolder.createEmptyContext();
					ctx.setAuthentication(auth);
				}
					
			} catch (Exception e) {
				
				/*perform authentication or simply say to user unauthorized?*/
				LOGGER.error("Error during authentication {}", e);
				throw new AccessDeniedException("Unable to complete access check, try after some time");
				
			}
			
		} else {
			
			
			//do the authentication again;
			Authentication auth = mgr.authenticate(new UsernamePasswordAuthenticationToken(getUser(req), getToken(req)));
			ctx = SecurityContextHolder.createEmptyContext();
			ctx.setAuthentication(auth);
			
		}
		
		return ctx;
	}
	
	private String getToken(HttpServletRequest req) {
		
		return req.getHeader(preAuthTokenKey);
	}
	
	private User getUser(HttpServletRequest req) {
		
		String preAuthToken = req.getHeader(preAuthTokenKey);
		String userName = req.getHeader(userKey);
		
		final User user = new User();
		user.setPassword(preAuthToken);
		user.setUsername(userName);
		return user;
	}
	


	/**
	 * @param userKey the userKey to set
	 */
	public void setUserKey(String userKey) {
		this.userKey = userKey;
	}

	/**
	 * @param preAuthTokenKey the preAuthTokenKey to set
	 */
	public void setPreAuthTokenKey(String preAuthTokenKey) {
		this.preAuthTokenKey = preAuthTokenKey;
	}

}