package david.logan.kenzan.jwt;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

//
//	The authentiation provider. It takes the token that was loaded into the Authentication object
//	from the JwtAuthFilter class, and creates a JwtToken in order to parse and validate the token
//	JwtToken really does the work. It's just that spring security calls this.
//
@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		JwtToken token = new JwtToken((String) authentication.getCredentials());
        ((JwtAuthentication)authentication).setAuthenticationFromToken(token);
		return authentication;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return (JwtAuthentication.class.isAssignableFrom(authentication));
	}

}
