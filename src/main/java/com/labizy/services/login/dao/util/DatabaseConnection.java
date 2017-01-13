package com.labizy.services.login.dao.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.labizy.services.login.exceptions.DatabaseConnectionException;

public final class DatabaseConnection {
	private static Logger logger = LoggerFactory.getLogger("com.labizy.services.login.AppLogger");

    private static String DATABASE_DRIVER = "com.mysql.jdbc.Driver";
    private static String DATABASE_URL = "jdbc:mysql://localhost:3306/{0}";
    private static String DATABASE_USERNAME = "labizy_user";
    private static String DATABASE_PASSWORD = "labizy_user_007";

	public final static Connection getDatabaseConnection(String database) throws DatabaseConnectionException {
		if(logger.isDebugEnabled()){
			logger.debug("Inside {}", "DatabaseConnection.getDatabaseConnection()");
		}
		
		Connection dbConnection = null;
		
		try {
			Class.forName(DATABASE_DRIVER);
		} catch (ClassNotFoundException e) {
			logger.error(e.getMessage());
			throw new DatabaseConnectionException(e);
		}

		try {
			String databaseUrl = java.text.MessageFormat.format(DATABASE_URL, database);
			dbConnection = DriverManager.getConnection(databaseUrl, DATABASE_USERNAME, DATABASE_PASSWORD);
			dbConnection.setAutoCommit(true);
		} catch (SQLException e) {
			logger.error(e.getMessage());
			throw new DatabaseConnectionException(e);
		}

		return dbConnection;
	}
	
	public static void main(String[] args) throws Exception{
		System.out.println("Connecting to DB..");
		getDatabaseConnection("labizy_user_db");
		System.out.println("Connected to DB.....");
	}
}