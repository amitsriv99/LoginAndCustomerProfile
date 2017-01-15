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
import com.labizy.services.login.utils.EncryptionDecryptionUtils;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserProfileDaoManager {
	private static Logger logger = LoggerFactory.getLogger("com.labizy.services.login.AppLogger");
	
	private CommonUtils commonUtils;
	private String databaseName;
	private EncryptionDecryptionUtils encryptionDecryptionUtils;
	
	public void setEncryptionDecryptionUtils(EncryptionDecryptionUtils encryptionDecryptionUtils) {
		this.encryptionDecryptionUtils = encryptionDecryptionUtils;
	}

	public void setCommonUtils(CommonUtils commonUtils) {
		this.commonUtils = commonUtils;
	}
	
	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	public Map<String, String> createUserProfile(String userId, String title, String firstName, String middleName, 
									String lastName, String sex, java.util.Date dateOfBirth, String maritalStatus, 
										String profilePictureUrl, boolean isPrimaryProfile) 
			throws DataIntegrityException, QueryExecutionException, DatabaseConnectionException{
		if(logger.isDebugEnabled()){
			logger.debug("Inside {}", "UserProfileDaoManager.createUserProfile()");
		}
		
		Map<String, String> userProfileMap = null;
		PreparedStatement preparedStatement = null;
		Connection connection = DatabaseConnection.getDatabaseConnection(databaseName);

		try{
			connection.setAutoCommit(false);
			String sqlQuery = "INSERT INTO user_profile_tb (user_id, title, first_name, middle_name, last_name, "
									+ "sex, date_of_birth, marital_status, profile_picture, is_primary_profile) "
									+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			
			preparedStatement = connection.prepareStatement(sqlQuery);
			
			preparedStatement.setNString(1, userId);
			preparedStatement.setNString(2, title);
			preparedStatement.setNString(3, firstName);
			preparedStatement.setNString(4, middleName);
			preparedStatement.setNString(5, lastName);
			preparedStatement.setNString(6, sex);
			
			java.sql.Timestamp dateOfBirthTS = new java.sql.Timestamp(dateOfBirth.getTime());
			preparedStatement.setTimestamp(7, dateOfBirthTS);
			
			preparedStatement.setNString(8, maritalStatus);
			preparedStatement.setNString(9, profilePictureUrl);
			preparedStatement.setBoolean(10, isPrimaryProfile);
			
			preparedStatement.execute();
			connection.commit();
			
			userProfileMap = getUserProfileDetails(userId, isPrimaryProfile);
		}catch(SQLException e){
			try{
				connection.rollback();
			} catch (SQLException e1) {
				logger.warn(e1.getMessage());
			}
			throw new QueryExecutionException(e);
		}catch(DataNotFoundException e){
			logger.error(e.getMessage());
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
		
		return userProfileMap;
	}

	public Map<String, String> getUserProfileDetails(String userId, boolean isPrimaryProfile)
									throws DataNotFoundException, QueryExecutionException, DatabaseConnectionException{
		HashMap<String, String> result = null;

		if(logger.isDebugEnabled()){
			logger.debug("Inside {}", "UserProfileDaoManager.getUserProfileDetails(String, boolean)");
		}

		Connection connection = DatabaseConnection.getDatabaseConnection(databaseName);
		PreparedStatement preparedStatement = null;
		try{
			String sqlQuery = "SELECT user_tb.user_id AS user_id, title, first_name, "
								+ "middle_name, last_name, sex, date_of_birth, marital_status, "
								+ "profile_picture, is_primary_profile, email_id, password, status "
								+ "FROM user_profile_tb, user_tb "
								+ "WHERE user_tb.user_id = ? AND is_primary_profile = ? "
								+ "AND (status IS NULL OR status <> 'DELETED')"; 	
			preparedStatement = connection.prepareStatement(sqlQuery);
			preparedStatement.setNString(1, userId);
			preparedStatement.setBoolean(2, isPrimaryProfile);
			
			ResultSet rs = preparedStatement.executeQuery();
			result = new HashMap<String, String>();
			while(rs.next()){
				result.put("userId", userId);
				
				String title = rs.getNString("title");
				result.put("title", title);
				
				String firstName = rs.getNString("first_name");
				result.put("firstName", firstName);
				
				String middleName = rs.getNString("middle_name");
				result.put("middleName", middleName);
				
				String lastName = rs.getNString("last_name");
				result.put("lastName", lastName);
				
				String sex = rs.getNString("sex");
				result.put("sex", sex);
				
				java.sql.Timestamp dateOfBirthTS = rs.getTimestamp("date_of_birth");
				String dateOfBirth = commonUtils.getTimestampAsDateString(dateOfBirthTS, true);
				result.put("dateOfBirth", dateOfBirth);
				
				String maritalStatus = rs.getNString("marital_status");
				result.put("maritalStatus", maritalStatus);
				
				String profilePictureUrl = rs.getNString("profile_picture");
				result.put("profilePictureUrl", profilePictureUrl);
				
				String emailId = rs.getNString("email_id");
				result.put("emailId", emailId);
				
				String password = rs.getNString("password");
				result.put("password", password);
				
				String status = rs.getNString("status");
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

		if(logger.isDebugEnabled()){
			logger.debug("Inside {}", "UserProfileDaoManager.getUserPrimaryAddressDetails(String, boolean)");
		}

		Connection connection = DatabaseConnection.getDatabaseConnection(databaseName);
		PreparedStatement preparedStatement = null;
		try{
			String sqlQuery = "SELECT user_id, address_tb.address_id AS address_id, house_or_flat_number, "
								+ "house_or_apartment_name, street_address, locality_name, city_town_or_village, "
								+ "state, country, pin_code, landmark, latitude, longitude "
								+ "FROM address_tb, user_address_tb WHERE user_id = ? AND is_primary_address = true"; 	
			preparedStatement = connection.prepareStatement(sqlQuery);
			preparedStatement.setNString(1, userId);
			
			ResultSet rs = preparedStatement.executeQuery();
			result = new HashMap<String, String>();
			while(rs.next()){
				result.put("userId", userId);
				
				String addressId = rs.getNString("address_id");
				result.put("addressId", addressId);
				
				String houseOrFlatNumber = rs.getNString("house_or_flat_number");
				result.put("houseOrFlatNumber", houseOrFlatNumber);
				
				String houseOrApartmentName = rs.getNString("house_or_apartment_name");
				result.put("houseOrApartmentName", houseOrApartmentName);
				
				String streetAddress = rs.getNString("street_address");
				result.put("streetAddress", streetAddress);
				
				String localityName = rs.getNString("locality_name");
				result.put("localityName", localityName);
				
				String cityTownOrVillage = rs.getNString("city_town_or_village");
				result.put("cityTownOrVillage", cityTownOrVillage);
				
				String state = rs.getNString("state");
				result.put("state", state);
				
				String country = rs.getNString("country");
				result.put("country", country);
				
				String pinCode = rs.getNString("pin_code");
				result.put("pinCode", pinCode);
				
				String landmark = rs.getNString("landmark");
				result.put("landmark", landmark);
				
				Double latitude = rs.getDouble("latitude");
				if((latitude == null) || (latitude.isNaN()) || (latitude.isInfinite())){
					result.put("latitude", "");
				}else{
					result.put("latitude", Double.toString(latitude));
				}
				
				Double longitude = rs.getDouble("longitude");
				if((longitude == null) || (longitude.isNaN()) || (longitude.isInfinite())){
					result.put("longitude", "");
				}else{
					result.put("longitude", Double.toString(longitude));
				}
				
				break;
			}
			
			if(result.isEmpty()){
				throw new DataNotFoundException("User/Address either doesn't exist or has been deleted.");
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
	
	public Map<String, String> getUserBillingAddressDetails(String userId)
									throws DataNotFoundException, QueryExecutionException, DatabaseConnectionException{
		HashMap<String, String> result = null;

		if(logger.isDebugEnabled()){
			logger.debug("Inside {}", "UserProfileDaoManager.getUserBillingAddressDetails(String, boolean)");
		}

		Connection connection = DatabaseConnection.getDatabaseConnection(databaseName);
		PreparedStatement preparedStatement = null;
		try{
			String sqlQuery = "SELECT user_id, address_tb.address_id AS address_id, house_or_flat_number, "
								+ "house_or_apartment_name, street_address, locality_name, city_town_or_village, "
								+ "state, country, pin_code, landmark, latitude, longitude "
								+ "FROM address_tb, user_address_tb WHERE user_id = ? AND is_billing_address = true"; 	

			preparedStatement = connection.prepareStatement(sqlQuery);
			preparedStatement.setNString(1, userId);
			
			ResultSet rs = preparedStatement.executeQuery();
			result = new HashMap<String, String>();
			while(rs.next()){
				result.put("userId", userId);
				
				String addressId = rs.getNString("address_id");
				result.put("addressId", addressId);
				
				String houseOrFlatNumber = rs.getNString("house_or_flat_number");
				result.put("houseOrFlatNumber", houseOrFlatNumber);
				
				String houseOrApartmentName = rs.getNString("house_or_apartment_name");
				result.put("houseOrApartmentName", houseOrApartmentName);
				
				String streetAddress = rs.getNString("street_address");
				result.put("streetAddress", streetAddress);
				
				String localityName = rs.getNString("locality_name");
				result.put("localityName", localityName);
				
				String cityTownOrVillage = rs.getNString("city_town_or_village");
				result.put("cityTownOrVillage", cityTownOrVillage);
				
				String state = rs.getNString("state");
				result.put("state", state);
				
				String country = rs.getNString("country");
				result.put("country", country);
				
				String pinCode = rs.getNString("pin_code");
				result.put("pinCode", pinCode);
				
				String landmark = rs.getNString("landmark");
				result.put("landmark", landmark);
				
				Double latitude = rs.getDouble("latitude");
				if((latitude == null) || (latitude.isNaN()) || (latitude.isInfinite())){
					result.put("latitude", "");
				}else{
					result.put("latitude", Double.toString(latitude));
				}
				
				Double longitude = rs.getDouble("longitude");
				if((longitude == null) || (longitude.isNaN()) || (longitude.isInfinite())){
					result.put("longitude", "");
				}else{
					result.put("longitude", Double.toString(longitude));
				}
				
				break;
			}
			
			if(result.isEmpty()){
				throw new DataNotFoundException("User/Address either doesn't exist or has been deleted.");
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
	
	public Map<String, String> getUserAddressDetails(String userId, String addressId)
			throws DataNotFoundException, QueryExecutionException, DatabaseConnectionException{
		HashMap<String, String> result = null;

		if(logger.isDebugEnabled()){
			logger.debug("Inside {}", "UserProfileDaoManager.getUserBillingAddressDetails(String, boolean)");
		}

		Connection connection = DatabaseConnection.getDatabaseConnection(databaseName);
		PreparedStatement preparedStatement = null;
		try{
			String sqlQuery = "SELECT user_id, address_tb.address_id AS address_id, house_or_flat_number, "
								+ "house_or_apartment_name, street_address, locality_name, city_town_or_village, "
								+ "state, country, pin_code, landmark, latitude, longitude, is_primary_address, is_billing_address " 
								+ "FROM address_tb, user_address_tb WHERE user_id = ? AND address_tb.address_id = ?"; 	

			preparedStatement = connection.prepareStatement(sqlQuery);
			preparedStatement.setNString(1, userId);
			preparedStatement.setNString(2, addressId);
			
			ResultSet rs = preparedStatement.executeQuery();
			result = new HashMap<String, String>();
			while(rs.next()){
				result.put("userId", userId);

				result.put("addressId", addressId);

				String houseOrFlatNumber = rs.getNString("house_or_flat_number");
				result.put("houseOrFlatNumber", houseOrFlatNumber);

				String houseOrApartmentName = rs.getNString("house_or_apartment_name");
				result.put("houseOrApartmentName", houseOrApartmentName);

				String streetAddress = rs.getNString("street_address");
				result.put("streetAddress", streetAddress);

				String localityName = rs.getNString("locality_name");
				result.put("localityName", localityName);

				String cityTownOrVillage = rs.getNString("city_town_or_village");
				result.put("cityTownOrVillage", cityTownOrVillage);

				String state = rs.getNString("state");
				result.put("state", state);

				String country = rs.getNString("country");
				result.put("country", country);

				String pinCode = rs.getNString("pin_code");
				result.put("pinCode", pinCode);

				String landmark = rs.getNString("landmark");
				result.put("landmark", landmark);

				Double latitude = rs.getDouble("latitude");
				if((latitude == null) || (latitude.isNaN()) || (latitude.isInfinite())){
					result.put("latitude", "");
				}else{
					result.put("latitude", Double.toString(latitude));
				}

				Double longitude = rs.getDouble("longitude");
				if((longitude == null) || (longitude.isNaN()) || (longitude.isInfinite())){
					result.put("longitude", "");
				}else{
					result.put("longitude", Double.toString(longitude));
				}
				
				boolean isPrimaryAddress = rs.getBoolean("is_primary_address");
				result.put("isPrimaryAddress", Boolean.toString(isPrimaryAddress));
				
				boolean isBillingAddress = rs.getBoolean("is_billing_address");
				result.put("isBillingAddress", Boolean.toString(isBillingAddress));
				
				break;
			}

			if(result.isEmpty()){
				throw new DataNotFoundException("User/Address either doesn't exist or has been deleted.");
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
	
	public List<Map<String, String>> getUserAddressDetails(String userId)
			throws DataNotFoundException, QueryExecutionException, DatabaseConnectionException{
		List<Map<String, String>> result = null;

		if(logger.isDebugEnabled()){
			logger.debug("Inside {}", "UserProfileDaoManager.getUserBillingAddressDetails(String, boolean)");
		}

		Connection connection = DatabaseConnection.getDatabaseConnection(databaseName);
		PreparedStatement preparedStatement = null;
		try{
			String sqlQuery = "SELECT user_id, address_tb.address_id AS address_id, house_or_flat_number, "
								+ "house_or_apartment_name, street_address, locality_name, city_town_or_village, "
								+ "state, country, pin_code, landmark, latitude, longitude, is_primary_address, is_billing_address " 
								+ "FROM address_tb, user_address_tb WHERE user_id = ?"; 	

			preparedStatement = connection.prepareStatement(sqlQuery);
			preparedStatement.setNString(1, userId);
			
			ResultSet rs = preparedStatement.executeQuery();
			result = new ArrayList<Map<String, String>>();
			while(rs.next()){
				Map<String, String> addressMap = new HashMap<String, String>(); 
				addressMap.put("userId", userId);

				String addressId = rs.getNString("address_id");
				addressMap.put("addressId", addressId);

				String houseOrFlatNumber = rs.getNString("house_or_flat_number");
				addressMap.put("houseOrFlatNumber", houseOrFlatNumber);

				String houseOrApartmentName = rs.getNString("house_or_apartment_name");
				addressMap.put("houseOrApartmentName", houseOrApartmentName);

				String streetAddress = rs.getNString("street_address");
				addressMap.put("streetAddress", streetAddress);

				String localityName = rs.getNString("locality_name");
				addressMap.put("localityName", localityName);

				String cityTownOrVillage = rs.getNString("city_town_or_village");
				addressMap.put("cityTownOrVillage", cityTownOrVillage);

				String state = rs.getNString("state");
				addressMap.put("state", state);

				String country = rs.getNString("country");
				addressMap.put("country", country);

				String pinCode = rs.getNString("pin_code");
				addressMap.put("pinCode", pinCode);

				String landmark = rs.getNString("landmark");
				addressMap.put("landmark", landmark);

				Double latitude = rs.getDouble("latitude");
				if((latitude == null) || (latitude.isNaN()) || (latitude.isInfinite())){
					addressMap.put("latitude", "");
				}else{
					addressMap.put("latitude", Double.toString(latitude));
				}

				Double longitude = rs.getDouble("longitude");
				if((longitude == null) || (longitude.isNaN()) || (longitude.isInfinite())){
					addressMap.put("longitude", "");
				}else{
					addressMap.put("longitude", Double.toString(longitude));
				}
				
				boolean isPrimaryAddress = rs.getBoolean("is_primary_address");
				addressMap.put("isPrimaryAddress", Boolean.toString(isPrimaryAddress));
				
				boolean isBillingAddress = rs.getBoolean("is_billing_address");
				addressMap.put("isBillingAddress", Boolean.toString(isBillingAddress));
				
				result.add(addressMap);
			}

			if(result.isEmpty()){
				throw new DataNotFoundException("User/Address either doesn't exist or has been deleted.");
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

	public List<Map<String, String>> getUserContactDetails(String userId)
			throws DataNotFoundException, QueryExecutionException, DatabaseConnectionException{
		List<Map<String, String>> result = null;

		if(logger.isDebugEnabled()){
			logger.debug("Inside {}", "UserProfileDaoManager.getUserContactDetails(String)");
		}

		Connection connection = DatabaseConnection.getDatabaseConnection(databaseName);
		PreparedStatement preparedStatement = null;
		
		String sqlQuery = "SELECT user_id, contact_tb.contact_id AS contact_id, contact_detail, contact_type, is_primary_contact "
				+ "FROM contact_tb, user_contact_tb "
				+ "WHERE user_id = ?"; 	

		try{
			preparedStatement = connection.prepareStatement(sqlQuery);
			preparedStatement.setNString(1, userId);
			
			ResultSet rs = preparedStatement.executeQuery();
			result = new ArrayList<Map<String, String>>();
			while(rs.next()){
				Map<String, String> contactMap = new HashMap<String, String>();
				contactMap.put("userId", userId);
				
				String contactId = rs.getNString("contact_id");
				contactMap.put("contactId", contactId);
				
				boolean isPrimaryContact = rs.getBoolean("is_primary_contact");
				contactMap.put("isPrimaryContact", Boolean.toString(isPrimaryContact));
				
				String contactType = rs.getNString("contact_type");
				contactMap.put("contactType", contactType);
				
				String contactDetail = rs.getNString("contact_detail");
				contactMap.put("contactDetail", contactDetail);
				
				result.add(contactMap);
			}
			
			if(result.isEmpty()){
				throw new DataNotFoundException("User/Contact either doesn't exist or has been deleted.");
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

	public Map<String, String> getUserContactDetails(String userId, String contactType, boolean isPrimaryContact)
									throws DataNotFoundException, QueryExecutionException, DatabaseConnectionException{
		HashMap<String, String> result = null;

		if(logger.isDebugEnabled()){
			logger.debug("Inside {}", "UserProfileDaoManager.getUserContactDetails(String, String, boolean)");
		}

		Connection connection = DatabaseConnection.getDatabaseConnection(databaseName);
		PreparedStatement preparedStatement = null;
		
		String sqlQuery = "SELECT user_id, contact_tb.contact_id AS contact_id, contact_detail "
				+ "FROM contact_tb, user_contact_tb "
				+ "WHERE user_id = ? AND contact_type = ? AND is_primary_contact = ?"; 	

		try{
			preparedStatement = connection.prepareStatement(sqlQuery);
			preparedStatement.setNString(1, userId);
			preparedStatement.setNString(2, contactType);
			preparedStatement.setBoolean(3, isPrimaryContact);
			
			ResultSet rs = preparedStatement.executeQuery();
			result = new HashMap<String, String>();
			while(rs.next()){
				result.put("userId", userId);
				
				String contactId = rs.getNString("contact_id");
				result.put("contactId", contactId);
				
				result.put("isPrimaryContact", Boolean.toString(isPrimaryContact));
				
				result.put("contactType", contactType);
				
				String contactDetail = rs.getNString("contact_detail");
				result.put("contactDetail", contactDetail);
				
				break;
			}
			
			if(result.isEmpty()){
				throw new DataNotFoundException("User/Contact either doesn't exist or has been deleted.");
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

	public Map<String, String> getUserContactDetails(String userId, String contactId)
			throws DataNotFoundException, QueryExecutionException, DatabaseConnectionException{
		HashMap<String, String> result = null;

		if(logger.isDebugEnabled()){
			logger.debug("Inside {}", "UserProfileDaoManager.getUserContactDetails(String, String, boolean)");
		}
		Connection connection = DatabaseConnection.getDatabaseConnection(databaseName);
		PreparedStatement preparedStatement = null;
		
		String sqlQuery = "SELECT user_id, contact_tb.contact_id AS contact_id, contact_type, contact_detail, is_primary_contact "
				+ "FROM contact_tb, user_contact_tb "
				+ "WHERE user_id = ? AND contact_tb.contact_id = ?"; 	

		try{
			preparedStatement = connection.prepareStatement(sqlQuery);
			preparedStatement.setNString(1, userId);
			preparedStatement.setNString(2, contactId);
			
			ResultSet rs = preparedStatement.executeQuery();
			result = new HashMap<String, String>();
			while(rs.next()){
				result.put("userId", userId);
				
				result.put("contactId", contactId);
				
				String contactType = rs.getNString("contact_type");
				result.put("contactType", contactType);

				boolean isPrimaryContact = rs.getBoolean("is_primary_contact");
				result.put("isPrimaryContact", Boolean.toString(isPrimaryContact));
				
				String contactDetail = rs.getNString("contact_detail");
				result.put("contactDetail", contactDetail);
				
				break;
			}
			
			if(result.isEmpty()){
				throw new DataNotFoundException("User/Contact either doesn't exist or has been deleted.");
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
	
	public String createUser(String emailId, String password)
					throws UniqueKeyViolationException, DataIntegrityException, QueryExecutionException, DatabaseConnectionException {
		if(logger.isDebugEnabled()){
			logger.debug("Inside {}", "UserProfileDaoManager.createUser(String, String)");
		}

		String userId = null;
		boolean userAlreadyExists = false;
		try{
			Map<String, String> result = getUserId(emailId, null);
			userId = result.get("userId");
			userAlreadyExists = !(StringUtils.isEmpty(userId));
		}catch(Exception e){
			logger.error(e.getMessage());
		}
		
		if(userAlreadyExists){
			return userId;
		}else{
			userId = commonUtils.getUniqueGeneratedId("USER", null);
			PreparedStatement preparedStatement = null;
			Connection connection = DatabaseConnection.getDatabaseConnection(databaseName);
			
			String sqlQuery = "INSERT INTO user_tb(user_id, email_id, password, status, is_real_user, is_guest_user, is_internal_user) VALUES (?, ?, ?, ?, ?, ?, ?)";
			
			try{
				connection.setAutoCommit(false);
				preparedStatement = connection.prepareStatement(sqlQuery);
				
				preparedStatement.setNString(1, userId);
				preparedStatement.setNString(2, emailId);
				
				String decodedPassword = encryptionDecryptionUtils.decodeToBase64String(password);
				String hashedPassword = encryptionDecryptionUtils.hashToBase64String(decodedPassword);
				preparedStatement.setNString(3, hashedPassword);

				preparedStatement.setNString(4, null);
				preparedStatement.setBoolean(5, true);
				preparedStatement.setBoolean(6, false);
				preparedStatement.setBoolean(7, false);
				
				preparedStatement.execute();
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

	public Map<String, String> createUserAddress(String userId, String houseOrFlatNumber, String houseOrApartmentName, String streetAddress, 
									String localityName, String cityOrTownOrVillage, String state, String country, String pinCode, 
										String landmark, Double latitude, Double longitude, boolean isPrimaryAddress, boolean isBillingAddress) 
			throws DataIntegrityException, QueryExecutionException, DatabaseConnectionException {
		if(logger.isDebugEnabled()){
			logger.debug("Inside {}", "UserProfileDaoManager.createUserAddress()");
		}

		String addressId = null;
		Map<String, String> userAddressMap = null;
		PreparedStatement preparedStatement = null;
		Connection connection = DatabaseConnection.getDatabaseConnection(databaseName);

		addressId = commonUtils.getUniqueGeneratedId("ADDR", null);

		try{
			connection.setAutoCommit(false);
			String sqlQuery = "INSERT INTO address_tb (address_id, house_or_flat_number, house_or_apartment_name, street_address, locality_name, city_town_or_village, state, country, pin_code, landmark, latitude, longitude)	VALUES	(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			
			preparedStatement = connection.prepareStatement(sqlQuery);
			
			preparedStatement.setNString(1, addressId);
			preparedStatement.setNString(2, houseOrFlatNumber);
			preparedStatement.setNString(3, houseOrApartmentName);
			preparedStatement.setNString(4, streetAddress);
			preparedStatement.setNString(5, localityName);
			preparedStatement.setNString(6, cityOrTownOrVillage);
			preparedStatement.setNString(7, state);
			preparedStatement.setNString(8, country);
			preparedStatement.setNString(9, pinCode);
			preparedStatement.setNString(10, landmark);
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
			
			preparedStatement.execute();
			preparedStatement.close();
			
			sqlQuery = "INSERT INTO user_address_tb (user_id, address_id, is_primary_address, is_billing_address) VALUES (?, ?, ?, ?)";
			preparedStatement = connection.prepareStatement(sqlQuery);
			
			preparedStatement.setNString(1, userId);
			preparedStatement.setNString(2, addressId);
			preparedStatement.setBoolean(3, isPrimaryAddress);
			preparedStatement.setBoolean(4, isBillingAddress);
			
			preparedStatement.execute();
			connection.commit();
			
			userAddressMap = getUserAddressDetails(userId, addressId);
		}catch(SQLException e){
			try{
				connection.rollback();
			} catch (SQLException e1) {
				logger.warn(e1.getMessage());
			}
			throw new QueryExecutionException(e);
		}catch(DataNotFoundException e){
			logger.error(e.toString());
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
		
		return userAddressMap;
	}

	public String getContactId(String contactType, String contactDetail) 
						throws DataNotFoundException, QueryExecutionException, DatabaseConnectionException {
		if(logger.isDebugEnabled()){
			logger.debug("Inside {}", "UserProfileDaoManager.getContactId(String, String)");
		}

		String contactId = null;
		Connection connection = DatabaseConnection.getDatabaseConnection(databaseName);
		PreparedStatement preparedStatement = null;
		try{
			String sqlQuery = "SELECT contact_id, contact_type, contact_detail FROM contact_tb WHERE contact_type = ? AND contact_detail = ?";
			preparedStatement = connection.prepareStatement(sqlQuery);
			preparedStatement.setNString(1, contactType);
			preparedStatement.setNString(2, contactDetail);
			
			ResultSet rs = preparedStatement.executeQuery();
			
			while(rs.next()){
				contactId = rs.getNString("contact_id");
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
	
	public Map<String, String> createUserContact(String userId, String contactType, String contactDetail, boolean isPrimaryContact) 
					throws DataIntegrityException, QueryExecutionException, DatabaseConnectionException{
		if(logger.isDebugEnabled()){
			logger.debug("Inside {}", "UserProfileDaoManager.createUserContact(String, String, String)");
		}
		
		Map<String, String> userContactMap = null;
		PreparedStatement preparedStatement = null;
		Connection connection = DatabaseConnection.getDatabaseConnection(databaseName);

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
			
			preparedStatement.setNString(1, contactId);
			preparedStatement.setNString(2, contactType);
			preparedStatement.setNString(3, contactDetail);
			
			preparedStatement.execute();
			preparedStatement.close();
			
			sqlQuery = "INSERT INTO user_contact_tb (user_id, contact_id, is_primary_contact ) VALUES (?, ?, ?)";
			preparedStatement = connection.prepareStatement(sqlQuery);
			
			preparedStatement.setNString(1, userId);
			preparedStatement.setNString(2, contactId);
			preparedStatement.setBoolean(3, isPrimaryContact);
			
			preparedStatement.execute();
			connection.commit();
			
			userContactMap = getUserContactDetails(userId, contactId);
		}catch(SQLException e){
			try{
				connection.rollback();
			} catch (SQLException e1) {
				logger.warn(e1.getMessage());
			}
			throw new QueryExecutionException(e);
		}catch(DataNotFoundException e){
			logger.error(e.toString());
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
		
		return userContactMap;
	}

	private void updateUserStatus(String userId, String status) 
					throws DataNotFoundException, QueryExecutionException, DatabaseConnectionException{
		if(logger.isDebugEnabled()){
			logger.debug("Inside {}", "UserProfileDaoManager.updateUserStatus(String, String)");
		}

		Connection connection = DatabaseConnection.getDatabaseConnection(databaseName);
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

	public Map<String, String> getUserId(String emailId, String password) 
					throws DataNotFoundException, QueryExecutionException, DatabaseConnectionException{
		Map<String, String> result = null;
		Connection connection = DatabaseConnection.getDatabaseConnection(databaseName);
		PreparedStatement preparedStatement = null;
		try{
			String sqlQuery1 = "SELECT user_id, email_id, password, status, is_real_user, is_guest_user "
								+ "FROM user_tb "
								+ "WHERE (status IS NULL OR status <> 'DELETED') AND email_id = ?";

			String sqlQuery2 = "SELECT user_id, email_id, password, status, is_real_user, is_guest_user "
								+ "FROM user_tb "
								+ "WHERE (status IS NULL OR status <> 'DELETED') AND email_id = ? AND password = ?";

			if(StringUtils.isEmpty(password)){
				preparedStatement = connection.prepareStatement(sqlQuery1);
				preparedStatement.setNString(1, emailId);
			}else{
				preparedStatement = connection.prepareStatement(sqlQuery2);
				preparedStatement.setNString(1, emailId);
				
				String decodedPassword = encryptionDecryptionUtils.decodeToBase64String(password);
				String hashedPassword = encryptionDecryptionUtils.hashToBase64String(decodedPassword);
				preparedStatement.setNString(2, hashedPassword);
			}
			
			ResultSet rs = preparedStatement.executeQuery();
			result = new HashMap<String, String>();
			
			while(rs.next()){
				result.put("emailId", emailId);
				
				String userId = rs.getNString("user_id");
				result.put("userId", userId);
				result.put("clientId", userId);

				password = rs.getNString("password");
				result.put("password", password);
				
				String status = rs.getNString("status");
				result.put("status", status);
				
				boolean isRealUser = rs.getBoolean("is_real_user");
				result.put("isRealUser", Boolean.toString(isRealUser));

				boolean isGuestUser = rs.getBoolean("is_guest_user");
				result.put("isGuestUser", Boolean.toString(isGuestUser));

				break;
			}
			
			if(result.isEmpty()){
				if(StringUtils.isEmpty(password)){
					throw new DataNotFoundException("Either user email id doesn't exist or user has been deleted.");
				}else{
					throw new DataNotFoundException("Either user email id doesn't exist or password doesn't match or user has been deleted.");
				}
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

	public String getUserStatus(String userId) 
					throws DataNotFoundException, QueryExecutionException, DatabaseConnectionException{
		String status = null;
		String emailId = null;
		Connection connection = DatabaseConnection.getDatabaseConnection(databaseName);
		PreparedStatement preparedStatement = null;
		try{
			String sqlQuery = "SELECT user_id, email_id, password, status FROM user_tb WHERE user_id = ?";
			preparedStatement = connection.prepareStatement(sqlQuery);
			preparedStatement.setNString(1, userId);
			
			ResultSet rs = preparedStatement.executeQuery();
			
			while(rs.next()){
				status = rs.getNString("status");
				emailId = rs.getNString("email_id");
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
		CommonUtils commonUtils = new CommonUtils();
		EncryptionDecryptionUtils encryptionDecryptionUtils = new EncryptionDecryptionUtils();
		
		daoMgr.setCommonUtils(commonUtils);
		daoMgr.setDatabaseName("labizy_user_db");
		daoMgr.setEncryptionDecryptionUtils(encryptionDecryptionUtils);
		
		String emailId = "prashant5@labizy.com";
		String base64EncodedPassword = "JDNjcjN0"; //String password = "$3cr3t";
		
		String userId = null;
		String status = null;
		Map<String, String> result = null;
		
		System.out.println("Calling getUserId(" + emailId + ")");
		try{
			result = daoMgr.getUserId(emailId, null);
			userId = result.get("userId");
			System.out.println("User Id : " + userId);
		}catch(Exception e){
			System.err.println(e);
		}
		
		System.out.println("Calling createUser(" + emailId + "," + base64EncodedPassword +")");
		try{
			userId = daoMgr.createUser(emailId, base64EncodedPassword);
			System.out.println("UserId : " + userId);
		}catch(Exception e){
			System.err.println(e);
		}
		
		System.out.println("Calling getUser(" + emailId + ")");
		try{
			result = daoMgr.getUserId(emailId, null);
			userId = result.get("userId");
			System.out.println("User Id : " + userId);
		}catch(Exception e){
			System.err.println(e);
		}
		
/*
		System.out.println("Calling createUserProfile(" + userId + ")");
		Map<String, String> userProfileMap = null;
		try{
			userProfileMap = daoMgr.createUserProfile(userId, "Mr", "Prashant", null, "Kunal", "Male", 
										commonUtils.getStringAsDate("30-06-1976"), "Married", null, true);
			System.out.println("Created profile for : " + userId);
			System.out.println(userProfileMap.toString());
		}catch(Exception e){
			System.err.println(e);
		}

		System.out.println("Calling createUserContact(" + userId + ")");
		Map<String, String> userUserContactMap = null;
		try{
			System.out.println("Create Mobile Contact for : " + userId);
			userUserContactMap = daoMgr.createUserContact(userId, "Mobile", "+91 9845426646", true);
			System.out.println(userUserContactMap.toString());
			
			System.out.println("Create Secondary Mobile Contact for : " + userId);
			userUserContactMap = daoMgr.createUserContact(userId, "Mobile", "+91 9945519876", false);
			System.out.println(userUserContactMap.toString());
			
			System.out.println("Get Primary Mobile Contact of : " + userId);
			userUserContactMap = daoMgr.getUserContactDetails(userId, "Mobile", true);
			System.out.println(userUserContactMap.toString());
			
			System.out.println("Get Secondary Mobile Contact of : " + userId);
			userUserContactMap = daoMgr.getUserContactDetails(userId, "Mobile", false);
			System.out.println(userUserContactMap.toString());
		}catch(Exception e){
			System.err.println(e);
		}

		System.out.println("Calling createUserAddress(" + userId + ")");
		Map<String, String> userUserAddressMap = null;
		try{
			userUserAddressMap = daoMgr.createUserAddress(userId, "102", "Pavan Fantasy", "16th Cross, 8th Main", 
					"BEML Layout, Thubrahalli", "Bangalore", "Karnataka", "India", "560066", 
					"Near FASO Shoppe", null, null, true, true);
			System.out.println("Create address for : " + userId);
			System.out.println(userUserAddressMap.toString());
			
			System.out.println("Get Billing Address of : " + userId);
			userUserAddressMap = daoMgr.getUserBillingAddressDetails(userId);
			System.out.println(userUserAddressMap.toString());
			
			System.out.println("Get Primary Address of : " + userId);
			userUserAddressMap = daoMgr.getUserPrimaryAddressDetails(userId);
			System.out.println(userUserAddressMap.toString());
		}catch(Exception e){
			System.err.println(e);
		}
*/
		System.out.println("Calling suspendUser(" + userId + ")");
		try{
			daoMgr.suspendUser(userId);
			status = daoMgr.getUserStatus(userId);
			System.out.println("Status : " + status);
		}catch(Exception e){
			System.err.println(e);
		}
/*
		System.out.println("Calling reactivateUser(" + userId + ")");
		try{
			daoMgr.reactivateUser(userId);
			status = daoMgr.getUserStatus(userId);
			System.out.println("Status : " + status);
		}catch(Exception e){
			System.err.println(e);
		}
*/		
		System.out.println("Ok.. That's it....");
	}
}