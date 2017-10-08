package david.logan.kenzan.jwt;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

//
//	This is springs authentication class. It has a whole bunch of stuff
//	we don't really care about. We return the JWT token from the getCredentials()
//	call, and we load up the authorities from the database, but otherwise
//	none of it is used.
//
public class JwtAuthentication implements Authentication {
	private String token;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<GrantedAuthority> authorities;
	private String username;

	public JwtAuthentication(String token)
	{
		this.token = token;
	}
	
	public void setAuthenticationFromToken(JwtToken token)
	{
		authorities = new ArrayList<GrantedAuthority>();
		for(String role : token.getRoles())
			authorities.add(new SimpleGrantedAuthority(role));
	}

	@Override
	public String getName() {
		return username;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public Object getCredentials() {
		return token;
	}

	@Override
	public Object getDetails() {
		return null;
	}

	@Override
	public Object getPrincipal() {
		return null;
	}

	@Override
	public boolean isAuthenticated() {
		return (authorities != null && authorities.size() != 0);
	}

	@Override
	public void setAuthenticated(boolean arg0) throws IllegalArgumentException {
		if(!arg0) authorities = null;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
