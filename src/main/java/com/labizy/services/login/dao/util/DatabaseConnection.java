package com.labizy.services.login.dao.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.labizy.services.login.beans.PropertiesBean;
import com.labizy.services.login.builder.PropertiesBuilder;
import com.labizy.services.login.exceptions.DatabaseConnectionException;
import com.labizy.services.login.exceptions.EnvironNotDefPropertiesBuilderException;
import com.labizy.services.login.utils.CommonUtils;
import com.labizy.services.login.utils.EncryptionDecryptionUtils;

public final class DatabaseConnection {
	private static Logger logger = LoggerFactory.getLogger("com.labizy.services.login.AppLogger");

	private PropertiesBuilder propertiesBuilder;
	private EncryptionDecryptionUtils encryptionDecryptionUtils;
	
    public void setPropertiesBuilder(PropertiesBuilder propertiesBuilder) {
		this.propertiesBuilder = propertiesBuilder;
	}

    public void setEncryptionDecryptionUtils(
			EncryptionDecryptionUtils encryptionDecryptionUtils) {
		this.encryptionDecryptionUtils = encryptionDecryptionUtils;
	}

	public final Connection getDatabaseConnection(String database) throws DatabaseConnectionException {
		if(logger.isDebugEnabled()){
			logger.debug("Inside {}", "DatabaseConnection.getDatabaseConnection()");
		}
		
		Connection dbConnection = null;
		
		try {
			Class.forName(propertiesBuilder.getEnvironProperties().getDatabaseDriver());
		} catch(EnvironNotDefPropertiesBuilderException e){
			logger.error(e.getMessage());
			throw new DatabaseConnectionException(e);
		} catch (ClassNotFoundException e) {
			logger.error(e.getMessage());
			throw new DatabaseConnectionException(e);
		}

		try {
			String databaseUrl = java.text.MessageFormat.format(propertiesBuilder.getEnvironProperties().getDatabaseUrl(), database);
			String databaseUser = encryptionDecryptionUtils.decodeToBase64String(propertiesBuilder.getEnvironProperties().getDatabaseUser());
			String databasePassword = encryptionDecryptionUtils.decodeToBase64String(propertiesBuilder.getEnvironProperties().getDatabasePassword());
			dbConnection = DriverManager.getConnection(databaseUrl, databaseUser, databasePassword);
			dbConnection.setAutoCommit(true);
		} catch(EnvironNotDefPropertiesBuilderException e){
			logger.error(e.getMessage());
			throw new DatabaseConnectionException(e);
		}catch (SQLException e) {
			logger.error(e.getMessage());
			throw new DatabaseConnectionException(e);
		}

		return dbConnection;
	}
	
	public static void main(String[] args) throws Exception{
		System.out.println("Connecting to DB..");

		String DATABASE_DRIVER = "com.mysql.jdbc.Driver";
		String DATABASE_URL = "jdbc:mysql://localhost:3306/{0}";
	    String DATABASE_USERNAME = "bGFiaXp5X3VzZXI=";
	    String DATABASE_PASSWORD = "bGFiaXp5X3VzZXJfMDA3";

	    System.setProperty("environ", "local");
	    EncryptionDecryptionUtils encryptionDecryptionUtils = new EncryptionDecryptionUtils();
		PropertiesBuilder propertiesBuilder = new PropertiesBuilder();
		
		PropertiesBean commonProperties = new PropertiesBean();
		Set<String> supportedEnvirons = new HashSet<String>();
		supportedEnvirons.add("local");
		supportedEnvirons.add("prod");
		supportedEnvirons.add("ppe");
		commonProperties.setSupportedEnvirons(supportedEnvirons);
		commonProperties.setEnvironSystemPropertyName("environ");
		propertiesBuilder.setCommonProperties(commonProperties);
		
		PropertiesBean localProperties = new PropertiesBean();
		localProperties.setDatabaseDriver(DATABASE_DRIVER);
		localProperties.setDatabaseUrl(DATABASE_URL);
		localProperties.setDatabaseUser(DATABASE_USERNAME);
		localProperties.setDatabasePassword(DATABASE_PASSWORD);
		propertiesBuilder.setLocalProperties(localProperties);
		
		CommonUtils commonUtils = new CommonUtils();
		commonUtils.setCommonProperties(commonProperties);
		propertiesBuilder.setCommonUtils(commonUtils);
		
		DatabaseConnection databaseConnection = new DatabaseConnection();
		databaseConnection.setPropertiesBuilder(propertiesBuilder);
		databaseConnection.setEncryptionDecryptionUtils(encryptionDecryptionUtils);
		
		databaseConnection.getDatabaseConnection("labizy_user_db");
		System.out.println("Connected to DB.....");
	}
}