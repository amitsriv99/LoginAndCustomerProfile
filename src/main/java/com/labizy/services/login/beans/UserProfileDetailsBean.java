package com.labizy.services.login.beans;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserProfileDetailsBean {
	UserCredentialsBean userLogin;
	UserProfileBean userProfile;
	UserRelativesDetailsBean relativesDetails; 
	UserAddressDetailsBean addressDetails;
	UserContactsDetailsBean contactDetails;

	public UserCredentialsBean getUserLogin() {
		return userLogin;
	}
	public void setUserLogin(UserCredentialsBean userLogin) {
		this.userLogin = userLogin;
	}
	public UserProfileBean getUserProfile() {
		return userProfile;
	}
	public void setUserProfile(UserProfileBean userProfile) {
		this.userProfile = userProfile;
	}
	public UserRelativesDetailsBean getRelativesDetails() {
		return relativesDetails;
	}
	public void setRelativesDetails(UserRelativesDetailsBean relativesDetails) {
		this.relativesDetails = relativesDetails;
	}
	public UserContactsDetailsBean getContactDetails() {
		return contactDetails;
	}
	public void setContactDetails(UserContactsDetailsBean contactDetails) {
		this.contactDetails = contactDetails;
	}
	public UserAddressDetailsBean getAddressDetails() {
		return addressDetails;
	}
	public void setAddressDetails(UserAddressDetailsBean addressDetails) {
		this.addressDetails = addressDetails;
	}
}
