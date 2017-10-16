package david.logan.kenzan.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

//
// Spring configuration, replacing the old XML configuration file
//
@Configuration
@EnableWebMvc
@EnableTransactionManagement
@Import({ SecurityConfigXML.class })
@ComponentScan("david.logan.kenzan.server")
@ComponentScan("david.logan.kenzan.jwt")
@ComponentScan("david.logan.kenzan.db")
//It should work to prevent us from needing a DataSource bean, but it isn't working.
//@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
public class AppConfigXML {
	@Bean
	public JpaTransactionManager jpaTransMan(){
		JpaTransactionManager jtManager = new JpaTransactionManager(entityManager().getObject());
		return jtManager;
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManager() {
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(dataSource());
		em.setPackagesToScan(new String[] { "david.logan.kenzan" });
		JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		em.setJpaVendorAdapter(vendorAdapter);
		em.setJpaProperties(additionalProperties());
		return em;
	}

	@Bean
	public DriverManagerDataSource dataSource() {
		//System.out.println("DL: Setting data source");
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(properties.getProperty("datasource.driver.class.name"));
		dataSource.setUrl(properties.getProperty("datasource.url"));
		dataSource.setUsername(properties.getProperty("datasource.username"));
		dataSource.setPassword(properties.getProperty("datasource.password"));
		return dataSource;
	}

	private static Properties properties;
	
	public static Properties getProperties() { return properties; }
	
	public AppConfigXML()
	{
		properties = new Properties();
		InputStream input = null;
		try {
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

			input = classLoader.getResourceAsStream("kenzan.properties");
			properties.load(input);
			//for(Object key : properties.keySet())
			//	System.out.println("DL: kenzan.properties: " + key + ": " + properties.getProperty((String) key));
			
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private Properties additionalProperties() { return properties; }
}
