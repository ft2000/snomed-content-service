package org.ihtsdo.otf.refset.security;

import org.springframework.security.test.context.support.WithSecurityContext;

@WithSecurityContext(factory = MockRefsetUserDetailsSecurityContextFactory.class)
public @interface  MockRefsetUser {
	
    String username() default "junit";

    String name() default "junit test";
}
