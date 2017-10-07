package david.logan.kenzan.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;

import javax.sql.DataSource;

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

	private DataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(properties.getProperty("datasource.driver.class.name")); // sigh :)
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
			URL[] urls = ((URLClassLoader)classLoader).getURLs();

	        for(URL url: urls){
	        	System.out.println(url.getFile());
	        }

			input = classLoader.getResourceAsStream("kenzan.properties");
			properties.load(input);
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
	
	private Properties additionalProperties() {
		Properties properties = new Properties();
		properties.setProperty("hibernate.hbm2ddl.auto", "update");
		properties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");
		return properties;
	}
}
