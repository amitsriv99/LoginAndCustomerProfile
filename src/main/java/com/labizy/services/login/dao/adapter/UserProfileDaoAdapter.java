package com.labizy.services.login.dao.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.labizy.services.login.beans.UserAddressBean;
import com.labizy.services.login.beans.UserAddressDetailsBean;
import com.labizy.services.login.beans.UserContactBean;
import com.labizy.services.login.beans.UserContactsDetailsBean;
import com.labizy.services.login.beans.UserCredentialsBean;
import com.labizy.services.login.beans.UserProfileBean;
import com.labizy.services.login.beans.UserProfileDetailsBean;
import com.labizy.services.login.beans.UserProfileDetailsResultBean;
import com.labizy.services.login.dao.manager.UserProfileDaoManager;
import com.labizy.services.login.exceptions.DataIntegrityException;
import com.labizy.services.login.exceptions.DataNotFoundException;
import com.labizy.services.login.exceptions.DatabaseConnectionException;
import com.labizy.services.login.exceptions.QueryExecutionException;
import com.labizy.services.login.exceptions.ServiceException;
import com.labizy.services.login.exceptions.UniqueKeyViolationException;
import com.labizy.services.login.exceptions.UserDoesNotExistException;
import com.labizy.services.login.utils.CommonUtils;
import com.labizy.services.login.utils.FieldComparator;

public class UserProfileDaoAdapter {
	private static Logger appLogger = LoggerFactory.getLogger("com.labizy.services.login.AppLogger");
	
	private UserProfileDaoManager userProfileDaoManager;
	private CommonUtils commonUtils;

	public String createUser(UserCredentialsBean userCredentials) throws ServiceException{
		String userId = null;
		try {
			userId = userProfileDaoManager.createUser(userCredentials.getEmailId(), userCredentials.getPassword());
		} catch (UniqueKeyViolationException e) {
			appLogger.error(e.getMessage());
			appLogger.error(e.getCause().getMessage());
			throw new ServiceException(e);
		} catch (DataIntegrityException e) {
			appLogger.error(e.getMessage());
			appLogger.error(e.getCause().getMessage());
			throw new ServiceException(e);
		} catch (QueryExecutionException e) {
			appLogger.error(e.getMessage());
			appLogger.error(e.getCause().getMessage());
			throw new ServiceException(e);
		} catch (DatabaseConnectionException e) {
			appLogger.error(e.getMessage());
			appLogger.error(e.getCause().getMessage());
			throw new ServiceException(e);
		}
		
		return  userId;
	}
	
	public UserProfileBean createUserProfile(String userId, UserProfileBean userProfileBean) throws ServiceException{
		UserProfileBean createdUserProfileBean = null;
		Map<String, String> userProfileMap = null;
		
		try {
			userProfileMap = userProfileDaoManager.createUserProfile(userId, 
																		userProfileBean.getTitle(), 
																		userProfileBean.getFirstName(), 
																		userProfileBean.getMiddleName(), 
																		userProfileBean.getLastName(), 
																		userProfileBean.getSex(), 
																		commonUtils.getStringAsDate(userProfileBean.getDateOfBirth()), 
																		userProfileBean.getMaritalStatus(), 
																		userProfileBean.getProfilePicture(), 
																		Boolean.parseBoolean(userProfileBean.getIsPrimaryProfile()),
																		null);
			createdUserProfileBean = getUserProfileDetails(userProfileMap);
		} catch (DataIntegrityException e) {
			appLogger.error(e.getMessage());
			appLogger.error(e.getCause().getMessage());
			throw new ServiceException(e);
		} catch (QueryExecutionException e) {
			appLogger.error(e.getMessage());
			appLogger.error(e.getCause().getMessage());
			throw new ServiceException(e);
		} catch (DatabaseConnectionException e) {
			appLogger.error(e.getMessage());
			appLogger.error(e.getCause().getMessage());
			throw new ServiceException(e);
		}
		
		return createdUserProfileBean;
	}	
	
	public UserContactsDetailsBean createUserContact(String userId, UserContactsDetailsBean userContacts) throws ServiceException{
		UserContactsDetailsBean createdContacts = null;
		List<Map<String, String>> userContactsList = null;
		if((userContacts.getContactDetail() != null) && (userContacts.getContactDetail().size() > 0)){
			userContactsList = new ArrayList<Map<String, String>>();
			
			for(UserContactBean userContact : userContacts.getContactDetail()){
				Map<String, String> contactMap = null;
				try {
					contactMap = userProfileDaoManager.createUserContact(userId, 
															userContact.getContactType(), 
															userContact.getContactDetail(), 
															Boolean.parseBoolean(userContact.getIsPrimaryContact()));
				} catch (DataIntegrityException e) {
					appLogger.error(e.getMessage());
					appLogger.error(e.getCause().getMessage());
					throw new ServiceException(e);
				} catch (QueryExecutionException e) {
					appLogger.error(e.getMessage());
					appLogger.error(e.getCause().getMessage());
					throw new ServiceException(e);
				} catch (DatabaseConnectionException e) {
					appLogger.error(e.getMessage());
					appLogger.error(e.getCause().getMessage());
					throw new ServiceException(e);
				}
			
				userContactsList.add(contactMap);
			}
		}
		createdContacts = getUserContactDetails(userContactsList);
		
		return createdContacts;
	}

	public UserAddressDetailsBean createUserAddress(String userId, UserAddressDetailsBean addressDetails) throws ServiceException{
		UserAddressDetailsBean createdAddressDetails = null;

		List<Map<String, String>> userAddressList = null;
		if((addressDetails.getAddressDetail() != null) && (addressDetails.getAddressDetail().size() > 0)){
			userAddressList = new ArrayList<Map<String, String>>();
			
			for(UserAddressBean userAddress : addressDetails.getAddressDetail()){
				Map<String, String> addressMap = null;
				try {
					addressMap = userProfileDaoManager.createUserAddress(userId, 
											userAddress.getHouseOrFlatNumber(), 
											userAddress.getHouseOrApartmentName(), 
											userAddress.getStreetAddress(), 
											userAddress.getLocalityName(), 
											userAddress.getCity(), 
											userAddress.getState(), 
											userAddress.getCountry(), 
											userAddress.getPinCode(), 
											userAddress.getLandmark(), 
											(userAddress.getLatitude() == null) ? null : Double.parseDouble(userAddress.getLatitude()), 
											(userAddress.getLongitude() == null) ? null : Double.parseDouble(userAddress.getLongitude()), 
											Boolean.parseBoolean(userAddress.getIsPrimaryAddress()), 
											Boolean.parseBoolean(userAddress.getIsBillingAddress()));
							
				} catch (DataIntegrityException e) {
					appLogger.error(e.getMessage());
					appLogger.error(e.getCause().getMessage());
					throw new ServiceException(e);
				} catch (QueryExecutionException e) {
					appLogger.error(e.getMessage());
					appLogger.error(e.getCause().getMessage());
					throw new ServiceException(e);
				} catch (DatabaseConnectionException e) {
					appLogger.error(e.getMessage());
					appLogger.error(e.getCause().getMessage());
					throw new ServiceException(e);
				}
			
				userAddressList.add(addressMap);
			}
		}
		createdAddressDetails = getUserAddressDetails(userAddressList);
		
		return createdAddressDetails;
	}
	
	public UserProfileDetailsResultBean updateUserPassword(UserProfileDetailsBean userProfileDetailsBean) throws ServiceException, UserDoesNotExistException {
		UserProfileDetailsResultBean userProfileDetailsResultBean = null;
		Map<String, String> userProfileMap = null;
		
		if(appLogger.isDebugEnabled()){
			appLogger.debug("Inside {}", "UserProfileDaoAdapter.updateUserPassword()");
		}
		
		String userId = userProfileDetailsBean.getUserProfile().getUserId();
		String emailId = userProfileDetailsBean.getUserLogin().getEmailId();
		String password = userProfileDetailsBean.getUserLogin().getPassword();
		
		try {
			userProfileMap = userProfileDaoManager.updateUserPassword(userId, emailId, password, null);
		} catch (DataNotFoundException e) {
			throw new UserDoesNotExistException(e);			
		} catch (Exception e) {
			throw new ServiceException(e);
		}
		
		userProfileDetailsResultBean = new UserProfileDetailsResultBean();
		UserProfileBean userProfileBean = getUserProfileDetails(userProfileMap);
		userProfileDetailsResultBean.setUserProfile(userProfileBean);
		
		UserCredentialsBean userLoginCredentialsBean = getUserLoginDetails(userProfileMap);
		userProfileDetailsResultBean.setUserLogin(userLoginCredentialsBean);
		
		return userProfileDetailsResultBean;
	}
	
	public UserProfileDetailsResultBean updateUserStatus(UserProfileDetailsBean userProfileDetailsBean) throws ServiceException, UserDoesNotExistException {
		UserProfileDetailsResultBean userProfileDetailsResultBean = null;
		Map<String, String> userProfileMap = null;
		
		if(appLogger.isDebugEnabled()){
			appLogger.debug("Inside {}", "UserProfileDaoAdapter.updateUserStatus()");
		}
		
		String userId = userProfileDetailsBean.getUserProfile().getUserId();
		String status = userProfileDetailsBean.getUserLogin().getStatus();

		try {
			userProfileMap = userProfileDaoManager.updateUserStatus(userId, status, null);
		} catch (DataNotFoundException e) {
			throw new UserDoesNotExistException(e);			
		} catch (Exception e) {
			throw new ServiceException(e);
		}
		
		userProfileDetailsResultBean = new UserProfileDetailsResultBean();
		UserProfileBean userProfileBean = getUserProfileDetails(userProfileMap);
		userProfileDetailsResultBean.setUserProfile(userProfileBean);
		
		UserCredentialsBean userLoginCredentialsBean = getUserLoginDetails(userProfileMap);
		userProfileDetailsResultBean.setUserLogin(userLoginCredentialsBean);
		
		return userProfileDetailsResultBean;
	}

	public UserProfileDetailsResultBean updateUserProfile(UserProfileDetailsBean userProfileDetailsBean) throws ServiceException, UserDoesNotExistException {
		UserProfileDetailsResultBean userProfileDetailsResultBean = null;
		Map<String, String> userProfileMap = null;
		
		if(appLogger.isDebugEnabled()){
			appLogger.debug("Inside {}", "UserProfileDaoAdapter.updateUserProfile()");
		}
		
		String userId = userProfileDetailsBean.getUserProfile().getUserId();
		
		boolean isPrimaryProfile = true;
		try{
			isPrimaryProfile = Boolean.parseBoolean(userProfileDetailsBean.getUserProfile().getIsPrimaryProfile());
		} catch(Exception e){
			//Assume it's for the primary profile
			isPrimaryProfile = true;
		}
		
		String title = userProfileDetailsBean.getUserProfile().getTitle();
		String firstName = userProfileDetailsBean.getUserProfile().getFirstName();
		String middleName = userProfileDetailsBean.getUserProfile().getMiddleName();
		String lastName = userProfileDetailsBean.getUserProfile().getLastName();
		
		String dob = userProfileDetailsBean.getUserProfile().getDateOfBirth();
		String dobFormat = userProfileDetailsBean.getUserProfile().getDateOfBirthFormat();
		java.util.Date dateOfBirth = commonUtils.getStringAsDate(dob, dobFormat);
		
		String maritalStatus = userProfileDetailsBean.getUserProfile().getMaritalStatus();
		String profilePictureUrl = userProfileDetailsBean.getUserProfile().getProfilePicture();
		String sex = userProfileDetailsBean.getUserProfile().getSex();
		
		try {
			userProfileMap = userProfileDaoManager.updateUserProfile(userId, title, firstName, middleName, lastName, dateOfBirth, 
																			maritalStatus, profilePictureUrl, sex, isPrimaryProfile, null);
		} catch (DataNotFoundException e) {
			throw new UserDoesNotExistException(e);			
		} catch (Exception e) {
			throw new ServiceException(e);
		}
		
		userProfileDetailsResultBean = new UserProfileDetailsResultBean();
		UserProfileBean userProfileBean = getUserProfileDetails(userProfileMap);
		userProfileDetailsResultBean.setUserProfile(userProfileBean);
		
		UserCredentialsBean userLoginCredentialsBean = getUserLoginDetails(userProfileMap);
		userProfileDetailsResultBean.setUserLogin(userLoginCredentialsBean);
		
		return userProfileDetailsResultBean;
	}
	
	private boolean isUserProfileQualifiedForUpdate(UserProfileBean newUserProfileBean, UserProfileBean existingUserProfileBean){
		boolean result = false;
		
		if((newUserProfileBean != null) && (existingUserProfileBean != null)){
			FieldComparator fieldComparator = new FieldComparator();
			List<String> fieldsToCompare = fieldComparator.getDeclaredFields(newUserProfileBean);
			for (String field : fieldsToCompare) {
				boolean isFieldValueSame = fieldComparator.isFieldValueSame(field, newUserProfileBean, existingUserProfileBean);
				
				if(! isFieldValueSame){
					result = true;
					break;
				}
			}
		}
		
		return result;
	}
	
	private boolean compareAndUpdateProfileDetails(UserProfileDetailsBean newUserProfileDetailsBean, UserProfileDetailsResultBean existingUserProfileDetailsBean){
		boolean isProfileUpdated = false;
		
		boolean isProfileQualifiedForUpdate = isUserProfileQualifiedForUpdate(newUserProfileDetailsBean.getUserProfile(), existingUserProfileDetailsBean.getUserProfile());
		
		return isProfileUpdated;
	}
	
	public UserProfileDetailsResultBean getUserProfileDetails(String userId, boolean isPrimaryProfile) throws ServiceException, UserDoesNotExistException {
		UserProfileDetailsResultBean userProfileDetailsResultBean = null;
		
		if(appLogger.isDebugEnabled()){
			appLogger.debug("Inside {}", "UserProfileDaoAdapter.getUserProfileDetails()");
		}
		
		if(appLogger.isInfoEnabled()){
			appLogger.info("Retrieving Profile of UserId : {}", userId);
		}

		try {
			if(appLogger.isDebugEnabled()){
				appLogger.debug("Fetching User Profile Info..");
			}
			Map<String, String> userProfileMap = userProfileDaoManager.getUserProfileDetails(userId, isPrimaryProfile, null);
			userProfileDetailsResultBean = new UserProfileDetailsResultBean();
			UserProfileBean userProfileBean = getUserProfileDetails(userProfileMap);
			userProfileDetailsResultBean.setUserProfile(userProfileBean);
			
			UserCredentialsBean userLoginCredentialsBean = getUserLoginDetails(userProfileMap);
			userProfileDetailsResultBean.setUserLogin(userLoginCredentialsBean);

			if(appLogger.isDebugEnabled()){
				appLogger.debug("Fetching User Contact Info..");
			}
			List<Map<String, String>> contactList = userProfileDaoManager.getUserContactDetails(userId);
			UserContactsDetailsBean userContactsDetailsBean = getUserContactDetails(contactList);
			userProfileDetailsResultBean.setContactDetails(userContactsDetailsBean);
			
			if(appLogger.isDebugEnabled()){
				appLogger.debug("Fetching User Address Info..");
			}
			List<Map<String, String>> addressList = userProfileDaoManager.getUserAddressDetails(userId);
			UserAddressDetailsBean userAddressDetailsBean = getUserAddressDetails(addressList);
			userProfileDetailsResultBean.setAddressDetails(userAddressDetailsBean);

		} catch (DataNotFoundException e) {
			appLogger.error(e.getMessage());
			throw new UserDoesNotExistException(e);
		} catch (QueryExecutionException e) {
			appLogger.error(e.getMessage());
			throw new ServiceException(e);
		} catch (DatabaseConnectionException e) {
			appLogger.error(e.getMessage());
			throw new ServiceException(e);
		}
		
		return userProfileDetailsResultBean;
	}

	private UserProfileBean getUserProfileDetails(Map<String, String> userProfileMap) {
		UserProfileBean userProfileBean = new UserProfileBean();
		for(Map.Entry<String, String> entry : userProfileMap.entrySet()){
			if(entry.getKey().equals("userId")){
				userProfileBean.setUserId(entry.getValue());
			}
			if(entry.getKey().equals("title")){
				userProfileBean.setTitle(entry.getValue());
			}
			if(entry.getKey().equals("firstName")){
				userProfileBean.setFirstName(entry.getValue());
			}
			if(entry.getKey().equals("middleName")){
				userProfileBean.setMiddleName(entry.getValue());
			}
			if(entry.getKey().equals("lastName")){
				userProfileBean.setLastName(entry.getValue());
			}
			if(entry.getKey().equals("sex")){
				userProfileBean.setSex(entry.getValue());
			}
			if(entry.getKey().equals("dateOfBirth")){
				userProfileBean.setDateOfBirth(entry.getValue());
			}
			if(entry.getKey().equals("maritalStatus")){
				userProfileBean.setMaritalStatus(entry.getValue());
			}
			if(entry.getKey().equals("profilePictureUrl")){
				userProfileBean.setProfilePicture(entry.getValue());
			}
			if(entry.getKey().equals("isPrimaryProfile")){
				userProfileBean.setTitle(entry.getValue());
			}
		}

		return userProfileBean;
	}

	private UserCredentialsBean getUserLoginDetails(Map<String, String> userProfileMap){
		UserCredentialsBean userLoginCredentialsBean = new UserCredentialsBean();
		
		for(Map.Entry<String, String> entry : userProfileMap.entrySet()){
			if(entry.getKey().equals("emailId")){
				userLoginCredentialsBean.setEmailId(entry.getValue());
			}
			if(entry.getKey().equals("status")){
				userLoginCredentialsBean.setStatus(entry.getValue());
			}
			/*
			if(entry.getKey().equals("password")){
				userLoginCredentialsBean.setPassword(entry.getValue());
			}
			*/
		}
		
		return userLoginCredentialsBean;
	}
	
	private UserAddressDetailsBean getUserAddressDetails(List<Map<String, String>> addressList) {
		ArrayList<UserAddressBean> userAddressList = new ArrayList<UserAddressBean>(); 
		
		for (Map<String, String> addressMap : addressList) {
			UserAddressBean userAddressBean = new UserAddressBean(); 
			for(Map.Entry<String, String> entry : addressMap.entrySet()){
				if(entry.getKey().equals("addressId")){
					userAddressBean.setAddressId(entry.getValue());
				}
				if(entry.getKey().equals("houseOrFlatNumber")){
					userAddressBean.setHouseOrFlatNumber(entry.getValue());
				}
				if(entry.getKey().equals("houseOrApartmentName")){
					userAddressBean.setHouseOrApartmentName(entry.getValue());
				}
				if(entry.getKey().equals("streetAddress")){
					userAddressBean.setStreetAddress(entry.getValue());
				}
				if(entry.getKey().equals("localityName")){
					userAddressBean.setLocalityName(entry.getValue());
				}
				if(entry.getKey().equals("cityTownOrVillage")){
					userAddressBean.setCity(entry.getValue());
				}
				if(entry.getKey().equals("state")){
					userAddressBean.setState(entry.getValue());
				}
				if(entry.getKey().equals("country")){
					userAddressBean.setCountry(entry.getValue());
				}
				if(entry.getKey().equals("pinCode")){
					userAddressBean.setPinCode(entry.getValue());
				}
				if(entry.getKey().equals("landmark")){
					userAddressBean.setLandmark(entry.getValue());
				}
				if(entry.getKey().equals("latitude")){
					userAddressBean.setLatitude(entry.getValue());
				}
				if(entry.getKey().equals("longitude")){
					userAddressBean.setLongitude(entry.getValue());
				}
				if(entry.getKey().equals("isPrimaryAddress")){
					userAddressBean.setIsPrimaryAddress(entry.getValue());
				}
				if(entry.getKey().equals("isBillingAddress")){
					userAddressBean.setIsBillingAddress(entry.getValue());
				}
			}
			userAddressList.add(userAddressBean);
		}
		
		UserAddressDetailsBean userAddressDetailsBean = new UserAddressDetailsBean();
		userAddressDetailsBean.setAddressDetail(userAddressList);
		
		return userAddressDetailsBean;
	}

	private UserContactsDetailsBean getUserContactDetails(List<Map<String, String>> contactList) {
		ArrayList<UserContactBean> userContactList = new ArrayList<UserContactBean>(); 
		
		for (Map<String, String> contactMap : contactList) {
			UserContactBean userContactBean = new UserContactBean(); 
			for(Map.Entry<String, String> entry : contactMap.entrySet()){
				if(entry.getKey().equals("contactId")){
					userContactBean.setContactId(entry.getValue());
				}
				if(entry.getKey().equals("contactType")){
					userContactBean.setContactType(entry.getValue());
				}
				if(entry.getKey().equals("contactDetail")){
					userContactBean.setContactDetail(entry.getValue());
				}
				if(entry.getKey().equals("isPrimaryContact")){
					userContactBean.setIsPrimaryContact(entry.getValue());
				}
			}
			userContactList.add(userContactBean);
		}
		
		UserContactsDetailsBean userContactsDetailsBean = new UserContactsDetailsBean();
		userContactsDetailsBean.setContactDetail(userContactList);
		
		return userContactsDetailsBean;
	}
	
	public void deleteUserAndProfile(String userId) throws ServiceException {
		if(appLogger.isDebugEnabled()){
			appLogger.debug("Inside {}", "UserProfileDaoAdapter.deleteUserAndProfile()");
		}
		
		try {
			if(appLogger.isInfoEnabled()){
				appLogger.info("Deleting UserId : {}", userId);
			}

			userProfileDaoManager.deleteUser(userId);
		} catch (DataNotFoundException e) {
			appLogger.warn(e.getMessage());
		} catch (QueryExecutionException e) {
			appLogger.error(e.getMessage());
			throw new ServiceException(e);
		} catch (DatabaseConnectionException e) {
			appLogger.error(e.getMessage());
			throw new ServiceException(e);
		}
		
		return;
	}

	public void setCommonUtils(CommonUtils commonUtils) {
		this.commonUtils = commonUtils;
	}

	public void setUserProfileDaoManager(UserProfileDaoManager userProfileDaoManager) throws ServiceException{
		this.userProfileDaoManager = userProfileDaoManager;
	}
}