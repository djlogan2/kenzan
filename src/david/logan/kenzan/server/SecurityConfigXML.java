package david.logan.kenzan.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import david.logan.kenzan.jwt.JwtAuthFilter;
import david.logan.kenzan.jwt.JwtAuthenticationFailureHandler;
import david.logan.kenzan.jwt.JwtAuthenticationProvider;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfigXML extends WebSecurityConfigurerAdapter {
	
	@Autowired private AuthenticationManager authenticationManager;
	@Autowired private AuthenticationFailureHandler failureHandler;
	
	@Autowired
	@Override
	public void configure/*Global*/(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(getAuthenticationProvider());
	}

	@Bean
	public AuthenticationFailureHandler getAuthenticationFailureHandler()
	{
		return new JwtAuthenticationFailureHandler();
	}
	
	protected AuthenticationProvider getAuthenticationProvider()
	{
		JwtAuthenticationProvider jp = new JwtAuthenticationProvider();
		return jp;
	}
	
	@Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
	
	protected JwtAuthFilter buildFilter() throws Exception {
		JwtAuthFilter filter = new JwtAuthFilter(failureHandler);
		filter.setAuthenticationManager(this.authenticationManager);
		return filter;
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
        .csrf().disable() // We don't need CSRF for JWT based authentication
        .exceptionHandling()
        //.authenticationEntryPoint(this.authenticationEntryPoint)
        .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
            .addFilterBefore(buildFilter(), UsernamePasswordAuthenticationFilter.class);

	}
}
