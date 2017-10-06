package david.logan.kenzan.jwt;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {

	//@Value("{$kenzan.token.issuer}")
	//private static String tokenIssuer = "DavidLogan";
	//@Value("{$kenzan.token.signingkey}")
	//private static String tokenSigningKey = "davids_signing_key";
	//@Value("{$kenzan.token.expire}")
	//private static int tokenExpirationMinutes = 60;
	
	//public static String getTokenIssuer() { return tokenIssuer; }
	//public static String getTokenSigningKey() { return tokenSigningKey; }
	//public static int getTokenExpirationMinutes() { return tokenExpirationMinutes; }
	
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
