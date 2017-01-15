package com.labizy.services.login.beans;

import java.util.Set;

public class PropertiesBean {
	private Set<String> supportedEnvirons;
	private String environSystemPropertyName;
	private String databaseDriver;
	private String databaseUrl;
	private String databaseUser;
	private String databasePassword;
	
	
	public Set<String> getSupportedEnvirons() {
		return supportedEnvirons;
	}
	public void setSupportedEnvirons(Set<String> supportedEnvirons) {
		this.supportedEnvirons = supportedEnvirons;
	}
	public String getEnvironSystemPropertyName() {
		return environSystemPropertyName;
	}
	public void setEnvironSystemPropertyName(String environSystemPropertyName) {
		this.environSystemPropertyName = environSystemPropertyName;
	}
	public String getDatabaseDriver() {
		return databaseDriver;
	}
	public void setDatabaseDriver(String databaseDriver) {
		this.databaseDriver = databaseDriver;
	}
	public String getDatabaseUrl() {
		return databaseUrl;
	}
	public void setDatabaseUrl(String databaseUrl) {
		this.databaseUrl = databaseUrl;
	}
	public String getDatabaseUser() {
		return databaseUser;
	}
	public void setDatabaseUser(String databaseUser) {
		this.databaseUser = databaseUser;
	}
	public String getDatabasePassword() {
		return databasePassword;
	}
	public void setDatabasePassword(String databasePassword) {
		this.databasePassword = databasePassword;
	}
}