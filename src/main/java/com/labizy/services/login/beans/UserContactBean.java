package com.labizy.services.login.beans;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserContactBean {
	private String contactId; 
	private String isPrimaryContact;
	private String contactType;
	private String contactDetail;

	public String getContactId() {
		return contactId;
	}
	public void setContactId(String contactId) {
		this.contactId = contactId;
	}
	public String getIsPrimaryContact() {
		return isPrimaryContact;
	}
	public void setIsPrimaryContact(String isPrimaryContact) {
		this.isPrimaryContact = isPrimaryContact;
	}
	public String getContactType() {
		return contactType;
	}
	public void setContactType(String contactType) {
		this.contactType = contactType;
	}
	public String getContactDetail() {
		return contactDetail;
	}
	public void setContactDetail(String contactDetail) {
		this.contactDetail = contactDetail;
	}
}
