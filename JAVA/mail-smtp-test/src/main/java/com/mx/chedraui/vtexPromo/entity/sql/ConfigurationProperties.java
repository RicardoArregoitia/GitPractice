package com.mx.chedraui.vtexPromo.entity.sql;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(schema = "dbo", name = "CONFIGURATIONS")
public class ConfigurationProperties implements Serializable {

	private static final long serialVersionUID = 6482191940127913726L;

	@Id
	@Column(name = "name")
	private String name;

	@Column(name = "description")
	private String description;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
