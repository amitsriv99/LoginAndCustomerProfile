package com.labizy.services.login.dao.manager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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

import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Map;

public class UserProfileDaoManager {
	private static Logger logger = LoggerFactory.getLogger("com.labizy.services.login.AppLogger");
	
	private CommonUtils commonUtils;
	public void setCommonUtils(CommonUtils commonUtils) {
		this.commonUtils = commonUtils;
	}
	
	public void createUserProfile(String userId, String title, String firstName, String middleName, 
									String lastName, String sex, java.util.Date dateOfBirth, String maritalStatus, 
										String profilePictureUrl, boolean isPrimaryProfile) 
			throws DataIntegrityException, QueryExecutionException, DatabaseConnectionException{
		if(logger.isDebugEnabled()){
			logger.debug("Inside {}", "UserProfileDaoManager.createUserProfile()");
		}
		
		PreparedStatement preparedStatement = null;
		Connection connection = DatabaseConnection.getDatabaseConnection("labizy_db");

		try{
			connection.setAutoCommit(false);
			String sqlQuery = "INSERT INTO user_profile_tb (user_id, title, first_name, middle_name, last_name, sex, date_of_birth, marital_status, profile_picture, is_primary_profile) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			
			preparedStatement = connection.prepareStatement(sqlQuery);
			
			preparedStatement.setString(1, userId);
			preparedStatement.setString(2, title);
			preparedStatement.setString(3, firstName);
			preparedStatement.setString(4, middleName);
			preparedStatement.setString(5, lastName);
			preparedStatement.setString(6, sex);
			
			java.sql.Timestamp dateOfBirthTS = new java.sql.Timestamp(dateOfBirth.getTime());
			preparedStatement.setTimestamp(7, dateOfBirthTS);
			
			preparedStatement.setString(8, maritalStatus);
			preparedStatement.setString(9, profilePictureUrl);
			preparedStatement.setBoolean(10, isPrimaryProfile);
			
			connection.commit();
		}catch(SQLException e){
			try{
				connection.rollback();
			} catch (SQLException e1) {
				logger.warn(e1.getMessage());
			}
			throw new QueryExecutionException(e);
		}finally{
			try {
				preparedStatement.close();
				connection.setAutoCommit(true);

				connection.close();
			} catch (SQLException e) {
				logger.warn(e.getMessage());
			}
			preparedStatement = null;
			connection = null;
		}
	}

	public Map<String, String> getUserProfileDetails(String userId, boolean isPrimaryProfile)
									throws DataNotFoundException, QueryExecutionException, DatabaseConnectionException{
		HashMap<String, String> result = null;

		if(logger.isDebugEnabled()){
			logger.debug("Inside {}", "UserProfileDaoManager.getContactId(String, String)");
		}

		Connection connection = DatabaseConnection.getDatabaseConnection("labizy_db");
		PreparedStatement preparedStatement = null;
		try{
			String sqlQuery = "SELECT user_tb.user_id AS user_id, title, first_name, "
					+ "middle_name, last_name, sex, date_of_birth, marital_status, profile_picture, is_primary_profile, email_id, password, status FROM user_profile_tb, user_tb WHERE user_tb.user_id = ? AND is_primary_profile = ? AND (status IS NULL OR status <> 'DELETED')"; 	
			preparedStatement = connection.prepareStatement(sqlQuery);
			preparedStatement.setString(1, userId);
			preparedStatement.setBoolean(2, isPrimaryProfile);
			
			ResultSet rs = preparedStatement.executeQuery();
			result = new HashMap<String, String>();
			while(rs.next()){
				result.put("userId", userId);
				
				String title = rs.getString("title");
				result.put("title", title);
				
				String firstName = rs.getString("first_name");
				result.put("firstName", firstName);
				
				String middleName = rs.getString("middle_name");
				result.put("middleName", middleName);
				
				String lastName = rs.getString("last_name");
				result.put("lastName", lastName);
				
				String sex = rs.getString("sex");
				result.put("sex", sex);
				
				java.sql.Timestamp dateOfBirthTS = rs.getTimestamp("date_of_birth");
				String dateOfBirth = commonUtils.getTimestampAsDateString(dateOfBirthTS);
				result.put("dateOfBirth", dateOfBirth);
				
				//, profile_picture, , , , 
				String maritalStatus = rs.getString("marital_status");
				result.put("maritalStatus", maritalStatus);
				
				String profilePictureUrl = rs.getString("profile_picture");
				result.put("profilePictureUrl", profilePictureUrl);
				
				String emailId = rs.getString("email_id");
				result.put("emailId", emailId);
				
				String password = rs.getString("password");
				result.put("password", password);
				
				String status = rs.getString("status");
				result.put("status", status);
				
				result.put("isPrimaryProfile", Boolean.toString(isPrimaryProfile));
				
				break;
			}
			
			if(result.isEmpty()){
				throw new DataNotFoundException("User either doesn't exist or has been deleted.");
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

	public Map<String, String> getUserPrimaryAddressDetails(String userId)
									throws DataNotFoundException, QueryExecutionException, DatabaseConnectionException{
		HashMap<String, String> result = null;
		String sqlQuery = "SELECT user_id, address_tb.address_id AS address_id, is_primary_address, is_billing_address, house_or_flat_number, house_or_apartment_name, street_address, locality_name, city_town_or_village, state, country, pin_code, landmark, latitude, longitude FROM address_tb, user_address_tb WHERE user_id = ? AND is_primary_address = ?"; 	
		
		return result;
	}
	
	public Map<String, String> getUserBillingAddressDetails(String userId)
									throws DataNotFoundException, QueryExecutionException, DatabaseConnectionException{
		HashMap<String, String> result = null;
		String sqlQuery = "SELECT user_id, address_tb.address_id AS address_id, is_primary_address, is_billing_address, house_or_flat_number, house_or_apartment_name, street_address, locality_name, city_town_or_village, state, country, pin_code, landmark, latitude, longitude FROM address_tb, user_address_tb WHERE user_id = ? AND is_billing_address = ?"; 	
		
		return result;
	}
	
	public Map<String, String> getUserContactDetails(String userId, String contactType, boolean isPrimaryAddress)
									throws DataNotFoundException, QueryExecutionException, DatabaseConnectionException{
		HashMap<String, String> result = null;
		String sqlQuery = "SELECT user_id, contact_tb.contact_id AS contact_id, is_primary_contact, contact_type, contact_detail FROM contact_tb, user_contact_tb WHERE user_id = ? AND contact_type = ? AND is_primary_contact = ?"; 	
		
		return result;
	}
	
	public String createUser(String emailId, String password)
					throws UniqueKeyViolationException, DataIntegrityException, QueryExecutionException, DatabaseConnectionException {
		if(logger.isDebugEnabled()){
			logger.debug("Inside {}", "UserProfileDaoManager.createUser(String, String)");
		}

		String userId = null;
		boolean userAlreadyExists = false;
		try{
			userId = getUserId(emailId);
			userAlreadyExists = !(StringUtils.isEmpty(userId));
		}catch(Exception e){
			logger.error(e.getMessage());
		}
		
		if(userAlreadyExists){
			return userId;
		}else{
			userId = commonUtils.getUniqueGeneratedId("USER", null);
			PreparedStatement preparedStatement = null;
			Connection connection = DatabaseConnection.getDatabaseConnection("labizy_db");
			
			String sqlQuery = "INSERT INTO user_tb(user_id, email_id, password, status) VALUES (?, ?, ?, ?)";
			
			try{
				preparedStatement = connection.prepareStatement(sqlQuery);
				
				preparedStatement.setString(1, userId);
				preparedStatement.setString(2, emailId);
				preparedStatement.setString(3, password);
				preparedStatement.setString(4, null);
				
				boolean isUserCreated = preparedStatement.execute();
				if(! isUserCreated){
					throw new DataIntegrityException("Unable to create Contact due todata integrity issues.");
				}
				connection.commit();
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
			
			return userId;
		}
	}

	public void createUserAddress(String userId, String houseOrFlatNumber, String houseOrApartmentName, String streetAddress, 
									String localityName, String cityOrTownOrVillage, String state, String country, String pinCode, 
										String landmark, Double latitude, Double longitude, boolean isPrimaryAddress, boolean isBillingAddress) 
			throws DataIntegrityException, QueryExecutionException, DatabaseConnectionException {
		if(logger.isDebugEnabled()){
			logger.debug("Inside {}", "UserProfileDaoManager.createUserAddress()");
		}

		String addressId = null;
		PreparedStatement preparedStatement = null;
		Connection connection = DatabaseConnection.getDatabaseConnection("labizy_db");

		addressId = commonUtils.getUniqueGeneratedId("ADDR", null);

		try{
			connection.setAutoCommit(false);
			String sqlQuery = "INSERT INTO address_tb (address_id, house_or_flat_number, house_or_apartment_name, street_address, locality_name, city_town_or_village, state, country, pin_code, landmark, latitude, longitude)	VALUES	(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			
			preparedStatement = connection.prepareStatement(sqlQuery);
			
			preparedStatement.setString(1, addressId);
			preparedStatement.setString(2, houseOrFlatNumber);
			preparedStatement.setString(3, houseOrApartmentName);
			preparedStatement.setString(4, streetAddress);
			preparedStatement.setString(5, localityName);
			preparedStatement.setString(6, cityOrTownOrVillage);
			preparedStatement.setString(7, state);
			preparedStatement.setString(8, country);
			preparedStatement.setString(9, pinCode);
			preparedStatement.setString(10, landmark);
			if((latitude == null) || (latitude.isNaN()) || (latitude.isInfinite())){
				preparedStatement.setNull(11, java.sql.Types.DOUBLE);
			}else{
				preparedStatement.setDouble(11, latitude);
			}
			if((longitude == null) || (longitude.isNaN()) || (longitude.isInfinite())){
				preparedStatement.setNull(12, java.sql.Types.DOUBLE);
			}else{
				preparedStatement.setDouble(12, longitude);
			}
			
			boolean isAddressCreated = preparedStatement.execute();
			if(! isAddressCreated){
				throw new DataIntegrityException("Unable to create Address due to data integrity issues.");
			}else{
				preparedStatement.close();
			}
			
			sqlQuery = "INSERT INTO user_address_tb (user_id, address_id, is_primary_address, is_billing_address) VALUES (?, ?, ?, ?)";
			preparedStatement = connection.prepareStatement(sqlQuery);
			
			preparedStatement.setString(1, userId);
			preparedStatement.setString(2, addressId);
			preparedStatement.setBoolean(3, isPrimaryAddress);
			preparedStatement.setBoolean(4, isBillingAddress);
			
			connection.commit();
		}catch(SQLException e){
			try{
				connection.rollback();
			} catch (SQLException e1) {
				logger.warn(e1.getMessage());
			}
			throw new QueryExecutionException(e);
		}finally{
			try {
				preparedStatement.close();
				connection.setAutoCommit(true);

				connection.close();
			} catch (SQLException e) {
				logger.warn(e.getMessage());
			}
			preparedStatement = null;
			connection = null;
		}
	}

	public String getContactId(String contactType, String contactDetail) 
						throws DataNotFoundException, QueryExecutionException, DatabaseConnectionException {
		if(logger.isDebugEnabled()){
			logger.debug("Inside {}", "UserProfileDaoManager.getContactId(String, String)");
		}

		String contactId = null;
		Connection connection = DatabaseConnection.getDatabaseConnection("labizy_db");
		PreparedStatement preparedStatement = null;
		try{
			String sqlQuery = "SELECT contact_id, contact_type, contact_detail FROM contact_tb WHERE contact_type = ? AND contact_detail = ?";
			preparedStatement = connection.prepareStatement(sqlQuery);
			preparedStatement.setString(1, contactType);
			preparedStatement.setString(2, contactDetail);
			
			ResultSet rs = preparedStatement.executeQuery();
			
			while(rs.next()){
				contactId = rs.getString("contact_id");
				break;
			}
			
			if(StringUtils.isEmpty(contactId)){
				throw new DataNotFoundException("User either doesn't exist or has been deleted.");
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
		
		return contactId; 
	}
	
	public void createUserContact(String userId, String contactType, String contactDetail, boolean isPrimaryContact) 
					throws DataIntegrityException, QueryExecutionException, DatabaseConnectionException{
		if(logger.isDebugEnabled()){
			logger.debug("Inside {}", "UserProfileDaoManager.createUserContact(String, String, String)");
		}
		
		PreparedStatement preparedStatement = null;
		Connection connection = DatabaseConnection.getDatabaseConnection("labizy_db");

		String contactId = null;
		try{
			contactId = getContactId(contactType, contactDetail);
		}catch(Exception e){
			contactId = commonUtils.getUniqueGeneratedId("CNTCT", null);
		}

		try{
			connection.setAutoCommit(false);
			String sqlQuery = "INSERT INTO contact_tb (contact_id, contact_type, contact_detail) VALUES (?, ?, ?)";
			
			preparedStatement = connection.prepareStatement(sqlQuery);
			
			preparedStatement.setString(1, contactId);
			preparedStatement.setString(2, contactType);
			preparedStatement.setString(3, contactDetail);
			
			boolean isContactCreated = preparedStatement.execute();
			if(! isContactCreated){
				throw new DataIntegrityException("Unable to create Contact due to data integrity issues.");
			}else{
				preparedStatement.close();
			}
			
			sqlQuery = "INSERT INTO user_contact_tb (user_id, contact_id, is_primary_contact ) VALUES (?, ?, ?)";
			preparedStatement = connection.prepareStatement(sqlQuery);
			
			preparedStatement.setString(1, userId);
			preparedStatement.setString(2, contactId);
			preparedStatement.setBoolean(3, isPrimaryContact);
			
			connection.commit();
		}catch(SQLException e){
			try{
				connection.rollback();
			} catch (SQLException e1) {
				logger.warn(e1.getMessage());
			}
			throw new QueryExecutionException(e);
		}finally{
			try {
				preparedStatement.close();
				connection.setAutoCommit(true);

				connection.close();
			} catch (SQLException e) {
				logger.warn(e.getMessage());
			}
			preparedStatement = null;
			connection = null;
		}
	}

	private void updateUserStatus(String userId, String status) 
					throws DataNotFoundException, QueryExecutionException, DatabaseConnectionException{
		if(logger.isDebugEnabled()){
			logger.debug("Inside {}", "UserProfileDaoManager.updateUserStatus(String, String)");
		}

		Connection connection = DatabaseConnection.getDatabaseConnection("labizy_db");
		Statement statement = null;
		try{
			connection.setAutoCommit(false);
			String sqlQuery = "UPDATE user_tb SET status = '" + status + "' WHERE user_id = '" + userId + "'";
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
	
	public void reactivateUser(String userId) 
					throws DataNotFoundException, QueryExecutionException, DatabaseConnectionException{
		if(logger.isDebugEnabled()){
			logger.debug("Inside {}", "UserProfileDaoManager.reactivateUser(String)");
		}

		updateUserStatus(userId, null);

		if(logger.isDebugEnabled()){
			logger.debug("User {} re-activated successfully..", userId);
		}
	}

	public void suspendUser(String userId) 
					throws DataNotFoundException, QueryExecutionException, DatabaseConnectionException{
		if(logger.isDebugEnabled()){
			logger.debug("Inside {}", "UserProfileDaoManager.suspendUser(String)");
		}

		updateUserStatus(userId, "SUSPENDED");

		if(logger.isDebugEnabled()){
			logger.debug("User {} suspended successfully..", userId);
		}
	}

	public void deleteUser(String userId) 
					throws DataNotFoundException, QueryExecutionException, DatabaseConnectionException{
		if(logger.isDebugEnabled()){
			logger.debug("Inside {}", "UserProfileDaoManager.suspendUser(String)");
		}

		updateUserStatus(userId, "DELETED");

		if(logger.isDebugEnabled()){
			logger.debug("User {} deleted successfully..", userId);
		}
	}

	public String getUserId(String emailId) 
					throws DataNotFoundException, QueryExecutionException, DatabaseConnectionException{
		String userId = null;
		Connection connection = DatabaseConnection.getDatabaseConnection("labizy_db");
		PreparedStatement preparedStatement = null;
		try{
			String sqlQuery = "SELECT user_id, email_id, password, status FROM user_tb WHERE (status IS NULL OR status <> 'DELETED') AND email_id = ?";
			preparedStatement = connection.prepareStatement(sqlQuery);
			preparedStatement.setString(1, emailId);
			
			ResultSet rs = preparedStatement.executeQuery();
			
			while(rs.next()){
				userId = rs.getString("user_id");
				break;
			}
			
			if(StringUtils.isEmpty(userId)){
				throw new DataNotFoundException("User either doesn't exist or has been deleted.");
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
		
		return userId; 
	}

	public String getUserStatus(String userId) 
					throws DataNotFoundException, QueryExecutionException, DatabaseConnectionException{
		String status = null;
		String emailId = null;
		Connection connection = DatabaseConnection.getDatabaseConnection("labizy_db");
		PreparedStatement preparedStatement = null;
		try{
			String sqlQuery = "SELECT user_id, email_id, password, status FROM user_tb WHERE user_id = ?";
			preparedStatement = connection.prepareStatement(sqlQuery);
			preparedStatement.setString(1, userId);
			
			ResultSet rs = preparedStatement.executeQuery();
			
			while(rs.next()){
				status = rs.getString("status");
				emailId = rs.getString("email_id");
				break;
			}
			
			if(StringUtils.isEmpty(emailId)){
				throw new DataNotFoundException("User doesn't exist.");
			}else{
				status = (StringUtils.isEmpty(status)) ? "ACTIVE" : status.toUpperCase();
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
		
		return status; 
	}

	public static void main(String[] args) throws Exception{
		System.out.println("Let's start..");
		
		UserProfileDaoManager daoMgr = new UserProfileDaoManager();
		daoMgr.setCommonUtils(new CommonUtils());
		
		String emailId = "prashant@labizy.com";
		String password = "$3cr3t";
		String userId = null;
		String status = null;
		System.out.println("Calling getUserId(" + emailId + ")");
		
		try{
			userId = daoMgr.getUserId(emailId);
			System.out.println("User Id : " + userId);
		}catch(Exception e){
			
		}
/*		
		System.out.println("Calling getUserStatus(" + userId + ")");
		try{
			status = daoMgr.getUserStatus(userId);
			System.out.println("Status : " + status);
		}catch(Exception e){
			
		}

*/
		System.out.println("Calling createUser(" + emailId + "," + password +")");
		try{
			userId = daoMgr.createUser(emailId, password);
			System.out.println("UserId : " + userId);
		}catch(Exception e){
			
		}

		System.out.println("Calling suspendUser(" + userId + ")");
		try{
			daoMgr.suspendUser(userId);
			status = daoMgr.getUserStatus(userId);
			System.out.println("Status : " + status);
		}catch(Exception e){
			
		}
		/*
		System.out.println("Calling reactivateUser(" + userId + ")");
		try{
			daoMgr.reactivateUser(userId);
			status = daoMgr.getUserStatus(userId);
			System.out.println("Status : " + status);
		}catch(Exception e){
			
		}
		*/		
		System.out.println("Ok.. That's it....");
	}
}