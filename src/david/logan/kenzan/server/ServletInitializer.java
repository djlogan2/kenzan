package david.logan.kenzan.server;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

@SpringBootApplication
public class ServletInitializer extends SpringBootServletInitializer {
	@Override
	public SpringApplicationBuilder configure(SpringApplicationBuilder application) 
	{
		return application.sources(AppConfigXML.class); //.sources(WebXML.class);
	}
	
//	public static void main(String[] args) throws Exception {
 //       SpringApplication.run(ServletInitializer.class, args);
  //  }
}
