package org.ihtsdo.otf.refset;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.ihtsdo.otf.refset.security.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class TokenFilter implements Filter {
	
	
	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		
		HttpServletResponse response = (HttpServletResponse) res;
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			
			User u = (User)auth.getPrincipal();
			
			response.addHeader("X-REFSET-AUTH-TOKEN", u.getToken());

		}
		
		chain.doFilter(req, res);

	}

	@Override
	public void init(FilterConfig arg0In) throws ServletException {
		// TODO Auto-generated method stub

	}


}
