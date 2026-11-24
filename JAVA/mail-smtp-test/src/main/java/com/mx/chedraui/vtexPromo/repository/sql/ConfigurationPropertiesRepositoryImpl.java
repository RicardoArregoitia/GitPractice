package com.mx.chedraui.vtexPromo.repository.sql;

import java.util.Collections;
import java.util.List;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;

import com.mx.chedraui.vtexPromo.entity.sql.ConfigurationProperties;

/**
 * En vtex promo esto es un JpaRepository sobre dbo.CONFIGURATIONS.
 * Aqui las mismas llaves salen de application-local.yml para poder probar el correo sin la base.
 */
@Repository
public class ConfigurationPropertiesRepositoryImpl implements ConfigurationPropertiesRepository {

	private final Environment environment;

	public ConfigurationPropertiesRepositoryImpl(Environment environment) {
		this.environment = environment;
	}

	@Override
	public List<ConfigurationProperties> getByName(String name) {
		ConfigurationProperties item = new ConfigurationProperties();
		item.setName(name);
		item.setDescription(environment.getProperty(name));
		return Collections.singletonList(item);
	}

}
