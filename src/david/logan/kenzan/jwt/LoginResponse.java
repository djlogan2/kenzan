package david.logan.kenzan.jwt;

import david.logan.kenzan.server.ErrorNumber;

//
//	The JSON response object from the login endpoint
//
public class LoginResponse {
	public String error;
	public ErrorNumber errorcode;
	public String jwt;
}
