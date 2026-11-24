package com.mx.chedraui.vtexPromo.repository.sql;

import java.util.List;

import com.mx.chedraui.vtexPromo.entity.sql.ConfigurationProperties;

public interface ConfigurationPropertiesRepository {

	List<ConfigurationProperties> getByName(String name);

}
