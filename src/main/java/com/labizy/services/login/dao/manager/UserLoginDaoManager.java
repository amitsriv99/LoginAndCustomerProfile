package com.labizy.services.login.dao.manager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.labizy.services.login.dao.util.DatabaseConnection;
import com.labizy.services.login.exceptions.DataIntegrityException;
import com.labizy.services.login.exceptions.DataNotFoundException;
import com.labizy.services.login.exceptions.DatabaseConnectionException;
import com.labizy.services.login.exceptions.QueryExecutionException;
import com.labizy.services.login.exceptions.UniqueKeyViolationException;
import com.labizy.services.login.utils.CommonUtils;

public class UserLoginDaoManager {
	private static Logger logger = LoggerFactory.getLogger("com.labizy.services.login.AppLogger");
	
	private CommonUtils commonUtils;
	private String databaseName;
	private UserProfileDaoManager userProfileDaoManager;
	
	public void setCommonUtils(CommonUtils commonUtils) {
		this.commonUtils = commonUtils;
	}
	
	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	public void setUserProfileDaoManager(UserProfileDaoManager userProfileDaoManager) {
		this.userProfileDaoManager = userProfileDaoManager;
	}

	public Map<String, String> validateToken(String oauthToken, String tokenType) 
			throws DataNotFoundException, QueryExecutionException, DatabaseConnectionException {
		if(logger.isDebugEnabled()){
			logger.debug("Inside {}", "UserLoginDaoManager.validateToken(String, String)");
		}
		
		Map<String, String> result = null;
		Connection connection = DatabaseConnection.getDatabaseConnection(databaseName);
		PreparedStatement preparedStatement = null;
		try{
			String sqlQuery = "SELECT user_tb.user_id AS user_id, oauth_token, oauth_token_issued_on, "
									+ "is_guest, token_type, email_id, password, status, last_logged_in, "
									+ "suspended_on, locked_on, comments, is_real_user "
									+ "FROM user_tb, user_oauth_tb, user_activity_tb "
									+ "WHERE user_tb.user_id = user_oauth_tb.user_id "
									+ "AND user_tb.user_id = user_activity_tb.user_id "
									+ "AND (STATUS IS NULL OR STATUS <> 'DELETED') "
									+ "AND oauth_token = ? AND token_type = ? ";

			preparedStatement = connection.prepareStatement(sqlQuery);
			preparedStatement.setNString(1, oauthToken);
			preparedStatement.setNString(2, tokenType);
			
			ResultSet rs = preparedStatement.executeQuery();
			
			result = new HashMap<String, String>();
			while(rs.next()){
				String userId = rs.getNString("user_id");
				result.put("userId", userId);
				result.put("clientId", userId);
				
				result.put("oauthToken", oauthToken);
				
				result.put("tokenType", tokenType);

				java.sql.Timestamp oauthTokenIssuedOnTS = rs.getTimestamp("oauth_token_issued_on");
				result.put("oauthTokenIssuedOn", commonUtils.getTimestampAsDateString(oauthTokenIssuedOnTS));
				
				boolean isGuest = rs.getBoolean("is_guest");
				result.put("isGuest", Boolean.toString(isGuest));
				
				String emailId = rs.getNString("email_id");
				result.put("emailId", emailId);
				
				String password = rs.getNString("password");
				result.put("password", password);

				String status = rs.getNString("status");
				result.put("status", status);

				String comments = rs.getNString("comments");
				result.put("comments", comments);

				java.sql.Timestamp lastLoggedInTS = rs.getTimestamp("last_logged_in");
				result.put("lastLoggedIn", commonUtils.getTimestampAsDateString(lastLoggedInTS));
				
				java.sql.Timestamp suspendedOnTS = rs.getTimestamp("suspended_on");
				result.put("suspendedOn", commonUtils.getTimestampAsDateString(suspendedOnTS));
				
				java.sql.Timestamp lockedOnTS = rs.getTimestamp("locked_on");
				result.put("lockedOn", commonUtils.getTimestampAsDateString(lockedOnTS));
				
				boolean isRealUser = rs.getBoolean("is_real_user");
				result.put("isRealUser", Boolean.toString(isRealUser));
				
				break;
			}
		
			if(result.isEmpty()){
				throw new DataNotFoundException("User token is not valid/expired. The user needs to login/re-login..");
			}
		}catch (SQLException e){
			logger.error(e.getMessage());
			throw new QueryExecutionException(e);
		}finally{
			try {
				preparedStatement.close();
				connection.close();
			} catch (SQLException e) {
				logger.warn(e.getMessage());
			}
			preparedStatement = null;
			connection = null;
		}
		
		return result; 
	}
	
	public Map<String, String> issueToken(String emailId, String password)
			throws UniqueKeyViolationException, DataIntegrityException, QueryExecutionException, DatabaseConnectionException {
		
		if(logger.isDebugEnabled()){
			logger.debug("Inside {}", "UserProfileDaoManager.issueToken(String, String)");
		}
		
		Map<String, String> oauthResultMap = null;
		
		String userId = null;
		boolean userAlreadyExists = false;
		boolean isRealUser = false;
		try{
			Map<String, String> result = userProfileDaoManager.getUserId(emailId, password);
			userId = result.get("userId");
			userAlreadyExists = !(StringUtils.isEmpty(userId));
			isRealUser = Boolean.parseBoolean(result.get("isRealUser"));
		}catch(Exception e){
			logger.error(e.getMessage());
		}
		
		String oauthToken = null; 
		String tokenType = null;
		
		if(userAlreadyExists){

		}

		userId = commonUtils.getUniqueGeneratedId("OAUTH", "U");
		PreparedStatement preparedStatement = null;
		Connection connection = DatabaseConnection.getDatabaseConnection(databaseName);
		
		String sqlQuery = "INSERT INTO user_tb(user_id, email_id, password, status) VALUES (?, ?, ?, ?)";
		
		try{
			connection.setAutoCommit(false);
			preparedStatement = connection.prepareStatement(sqlQuery);
			
			preparedStatement.setNString(1, userId);
			preparedStatement.setNString(2, emailId);
			preparedStatement.setNString(3, password);
			preparedStatement.setNString(4, null);
			
			preparedStatement.execute();

			connection.commit();

			oauthResultMap = validateToken(oauthToken, tokenType);
		}catch(DataNotFoundException e){
			throw new QueryExecutionException(e);
		}catch(SQLException e){
			try{
				userId = null;
				connection.rollback();
			} catch (SQLException e1) {
				logger.warn(e1.getMessage());
			}
			throw new QueryExecutionException(e);
		}finally{
			try {
				preparedStatement.close();
				connection.close();
			} catch (SQLException e) {
				logger.warn(e.getMessage());
			}
			preparedStatement = null;
			connection = null;
		}
		
		return oauthResultMap;
	}

	public void expireToken(String clientId, String outhToken, String tokenType) 
			throws DataNotFoundException, QueryExecutionException, DatabaseConnectionException{
		if(logger.isDebugEnabled()){
			logger.debug("Inside {}", "UserProfileDaoManager.updateUserStatus(String, String)");
		}
		
		Connection connection = DatabaseConnection.getDatabaseConnection(databaseName);
		Statement statement = null;
		try{
			connection.setAutoCommit(false);
			String sqlQuery = null;//"UPDATE user_tb SET status = '" + status + "' WHERE user_id = '" + userId + "'";
			//System.out.println("Update Query : " + sqlQuery);
			statement = connection.createStatement();
			
			statement.execute(sqlQuery);
			connection.commit();
		}catch (SQLException e){
			try {
				connection.rollback();
			} catch (SQLException e1) {
				logger.warn(e1.getMessage());
			}
			logger.error(e.getMessage());
			throw new QueryExecutionException(e);
		}finally{
			try {
				statement.close();
				connection.close();
			} catch (SQLException e) {
				logger.warn(e.getMessage());
			}
			statement = null;
			connection = null;
		}
	}
}
