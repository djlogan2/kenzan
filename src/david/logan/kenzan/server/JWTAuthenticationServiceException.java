package david.logan.kenzan.server;

import org.springframework.security.authentication.AuthenticationServiceException;

public class JWTAuthenticationServiceException extends AuthenticationServiceException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public ErrorNumber errorcode;
	public JWTAuthenticationServiceException(ErrorNumber errorcode) {
		super("Access denied: Invalid token");
		this.errorcode = errorcode;
	}

	public JWTAuthenticationServiceException(ErrorNumber errorcode, String msg) {
		super(msg);
		this.errorcode = errorcode;
	}
}
