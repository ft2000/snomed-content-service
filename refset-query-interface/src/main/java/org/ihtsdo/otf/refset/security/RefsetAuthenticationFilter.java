/**
 * 
 */
package org.ihtsdo.otf.refset.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * @author Episteme Partners
 *
 */
public class RefsetAuthenticationFilter extends
		AbstractAuthenticationProcessingFilter {

	protected RefsetAuthenticationFilter(
			RequestMatcher requiresAuthenticationRequestMatcher) {
		
		super(requiresAuthenticationRequestMatcher);
		
		// TODO Auto-generated constructor stub
	}
		

	@Override
	public Authentication attemptAuthentication(HttpServletRequest req,
			HttpServletResponse arg1) throws AuthenticationException,
			IOException, ServletException {
		// TODO Auto-generated method stub
		

        
		return null;
	}
	
	@Override
	protected void successfulAuthentication(HttpServletRequest request,
			HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {

		super.successfulAuthentication(request, response, chain, authResult);
		chain.doFilter(request, response);
		
	}
	
	@Override
	public void doFilter(ServletRequest req, ServletResponse resp,
			FilterChain filter) throws IOException, ServletException {
		// TODO Auto-generated method stub
		super.doFilter(req, resp, filter);
	}

}
