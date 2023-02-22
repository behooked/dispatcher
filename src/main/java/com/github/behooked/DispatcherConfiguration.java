package com.github.behooked;

import io.dropwizard.client.JerseyClientConfiguration;
import io.dropwizard.core.Configuration;
import io.dropwizard.db.DataSourceFactory;
import com.fasterxml.jackson.annotation.JsonProperty;


import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

public class DispatcherConfiguration extends Configuration {

	@NotEmpty
	private String adminUrl;

	@JsonProperty
	public String getAdminUrl() {
		return adminUrl;
	}
	@JsonProperty
	public void setAdminUrl(String adminUrl) {
		this.adminUrl = adminUrl;
	}

	@Valid
	@NotNull
	private DataSourceFactory database = new DataSourceFactory();

	@JsonProperty("database")
	public DataSourceFactory getDataSourceFactory() {
		return database;
	}

	@JsonProperty("database")
	public void setDataSourceFactory(DataSourceFactory dataSourceFactory) {
		this.database = dataSourceFactory;
	}
	

	// Jersey-Client

	@Valid
	@NotNull
	private JerseyClientConfiguration jerseyClient = new JerseyClientConfiguration();

	@JsonProperty("jerseyClient")
	public JerseyClientConfiguration getJerseyClientConfiguration() {
		return jerseyClient;
	}

	@JsonProperty("jerseyClient")
	public void setJerseyClientConfiguration(JerseyClientConfiguration jerseyClient) {
		this.jerseyClient = jerseyClient;
	}
}
