/**
 * 
 */
package org.ihtsdo.otf.refset.security;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**Used to get user details so that these can be used in 
 * Refset authoring
 * @author Episteme Partners
 *
 */
public class User implements UserDetails {
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String username;
	
	private Collection<? extends GrantedAuthority> authorities;
	
	private boolean authenticated;


	

	protected User() {
		
		authorities = new ArrayList<GrantedAuthority>();
		
    }

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	private String password;
	

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return authorities;
	}

	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isAccountNonLocked() {

		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {

		return true;
	}

	@Override
	public boolean isEnabled() {

		return true;
	}
	
	public void setAuthorities(Collection<? extends GrantedAuthority> roles) {
        this.authorities = roles;
    }
 
	/**
	 * @return the authenticated
	 */
	public boolean isAuthenticated() {
		return authenticated;
	}

	/**
	 * @param authenticated the authenticated to set
	 */
	public void setAuthenticated(boolean authenticated) {
		this.authenticated = authenticated;
	}
}
