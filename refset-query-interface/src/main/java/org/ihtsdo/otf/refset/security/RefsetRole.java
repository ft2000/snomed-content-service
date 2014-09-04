/**
 * 
 */
package org.ihtsdo.otf.refset.security;

import org.springframework.security.core.GrantedAuthority;

/**
 * @author Episteme Partners
 *
 */
public class RefsetRole implements GrantedAuthority {

	
	private static final long serialVersionUID = 1L;
	String role;
	
	protected RefsetRole() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getAuthority() {
		return role;
	}

	public void setAuthority(String role) {
		this.role = role;
	}

}
