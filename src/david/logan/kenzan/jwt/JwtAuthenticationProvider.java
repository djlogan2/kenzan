package david.logan.kenzan.jwt;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

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
