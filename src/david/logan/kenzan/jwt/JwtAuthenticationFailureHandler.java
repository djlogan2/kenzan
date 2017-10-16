package david.logan.kenzan.jwt;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import david.logan.kenzan.server.ErrorResponse;
import david.logan.kenzan.server.JWTAuthenticationServiceException;

//
//	This is what happens when our authentication provider fails to authenticate.
//	TODO: Standardize an error response JSON object and make this match it.
//
public class JwtAuthenticationFailureHandler implements AuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException e)
			throws IOException, ServletException {
		JWTAuthenticationServiceException jase = (JWTAuthenticationServiceException)e;
		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		ErrorResponse resp = new ErrorResponse(jase.errorcode, jase.getMessage());
		ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(response.getWriter(), resp);
	}

}
