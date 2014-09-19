package org.ihtsdo.otf.refset.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.stereotype.Component;

public class MockRefsetUserDetailsSecurityContextFactory implements WithSecurityContextFactory<WithUserDetails>{

    private UserDetailsService service;

	
	
	@Override
	public SecurityContext createSecurityContext(WithUserDetails uDetails) {
		// TODO Auto-generated method stub
		String username = uDetails.value();
        UserDetails principal = service.loadUserByUsername(username);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, 
        		principal.getPassword(), principal.getAuthorities());
        SecurityContext ctx = SecurityContextHolder.createEmptyContext();
        ctx.setAuthentication(authentication);
        return ctx;
	}
	

    @Autowired
    public MockRefsetUserDetailsSecurityContextFactory(UserDetailsService uService) {
        this.service = uService;
    }

}
