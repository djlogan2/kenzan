package david.logan.kenzan.jwt;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

public class IgnoreLoginMatcher implements RequestMatcher {

	//
	// This is the class that is called to determine whether our filter should be called.
	// We want spring to call our filter in all cases except for our login endpoint.
	//
	private RequestMatcher antMatcher;
	public IgnoreLoginMatcher()
	{
		antMatcher = new AntPathRequestMatcher("/rest/login");
	}
	@Override
	public boolean matches(HttpServletRequest arg0) {
		return !antMatcher.matches(arg0);
	}
}
