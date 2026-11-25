package com.mx.chedraui.vtexPromo.config;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.mx.chedraui.vtexPromo.repository.sql", entityManagerFactoryRef = "sqlserverEntityManagerFactory", transactionManagerRef = "sqlserverTransactionManager")
public class SqlServerConfig {

	private static final String DATABASE_DIALECT = "org.hibernate.dialect.SQLServerDialect";

	private static final String ENTITY_PACKAGES = "com.mx.chedraui.vtexPromo.entity.sql";

	@Autowired
	private Environment env;

	@Bean(name = "sqlserverDataSource")
	public DataSource sqlserverDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setUrl(env.getProperty("spring.datasource.sql.jdbcUrl"));
		dataSource.setUsername(env.getProperty("spring.datasource.sql.username"));
		dataSource.setPassword(env.getProperty("spring.datasource.sql.password"));
		dataSource.setDriverClassName(env.getProperty("spring.datasource.sql.driverClassName"));
		return dataSource;
	}

	@Bean(name = "sqlserverEntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(sqlserverDataSource());
		em.setPersistenceUnitName("sqlserverPU");
		em.setPackagesToScan(ENTITY_PACKAGES);

		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		em.setJpaVendorAdapter(vendorAdapter);

		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("hibernate.dialect", DATABASE_DIALECT);
		em.setJpaPropertyMap(properties);
		return em;
	}

	@Bean(name = "sqlserverTransactionManager")
	public PlatformTransactionManager transactionManager() {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
		return transactionManager;
	}

}
