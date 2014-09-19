package org.ihtsdo.otf.refset.security;

import org.ihtsdo.otf.refset.security.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

/**
 * @author http://spring.io/blog/2014/05/07/preview-spring-security-test-method-security
 *
 */
public class MockRefsetUserSecurityContextFactory implements WithSecurityContextFactory<MockRefsetUser>{

	@Override
	public SecurityContext createSecurityContext(MockRefsetUser annotation) {
		// TODO Auto-generated method stub
		
        SecurityContext ctx = SecurityContextHolder.createEmptyContext();

        User p = new User();
        p.setPassword("junit");
        p.setUsername("junit");
        
        Authentication auth = new UsernamePasswordAuthenticationToken(p, p.getPassword(), p.getAuthorities());
        ctx.setAuthentication(auth);
        return ctx;
        
	}

}
