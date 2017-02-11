package com.labizy.services.login.beans;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserProfileBean {
	private String userId;
	private String title;
	private String firstName;
	private String middleName;
	private String lastName;
	private String sex;
	private String dateOfBirth;
	private String dateOfBirthFormat;
	private String maritalStatus;
	private String profilePicture;
	private String isPrimaryProfile;
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getMiddleName() {
		return middleName;
	}
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getDateOfBirth() {
		return dateOfBirth;
	}
	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	public String getDateOfBirthFormat() {
		return dateOfBirthFormat;
	}
	public void setDateOfBirthFormat(String dateOfBirthFormat) {
		this.dateOfBirthFormat = dateOfBirthFormat;
	}
	public String getMaritalStatus() {
		return maritalStatus;
	}
	public void setMaritalStatus(String maritalStatus) {
		this.maritalStatus = maritalStatus;
	}
	public String getProfilePicture() {
		return profilePicture;
	}
	public void setProfilePicture(String profilePicture) {
		this.profilePicture = profilePicture;
	}
	public String getIsPrimaryProfile() {
		return isPrimaryProfile;
	}
	public void setIsPrimaryProfile(String isPrimaryProfile) {
		this.isPrimaryProfile = isPrimaryProfile;
	}
}
