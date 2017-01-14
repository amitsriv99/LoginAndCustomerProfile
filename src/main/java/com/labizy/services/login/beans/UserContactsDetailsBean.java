package com.labizy.services.login.beans;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserContactsDetailsBean {

	@JsonProperty("contactDetail")
	private ArrayList<UserContactBean> contactDetail;

	public ArrayList<UserContactBean> getContactDetail() {
		return contactDetail;
	}

	public void setContactDetail(ArrayList<UserContactBean> contactDetail) {
		this.contactDetail = contactDetail;
	}
}
