package david.logan.kenzan.jwt;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

public class IgnoreLoginMatcher implements RequestMatcher {

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
