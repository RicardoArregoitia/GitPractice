package com.mx.chedraui.vtexPromo.entity.sql;

import java.io.Serializable;

public class ConfigurationProperties implements Serializable {

	private static final long serialVersionUID = 6482191940127913726L;

	private String name;

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
