package com.mx.chedraui.vtexPromo.repository.sql;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.mx.chedraui.vtexPromo.entity.sql.ConfigurationProperties;

@Repository
@Transactional(transactionManager = "sqlserverTransactionManager")
public interface ConfigurationPropertiesRepository extends JpaRepository<ConfigurationProperties, String> {

	@Query(value = "select * from [dbo].[CONFIGURATIONS] where name = :name", nativeQuery = true)
	List<ConfigurationProperties> getByName(@Param("name") String name);

}
