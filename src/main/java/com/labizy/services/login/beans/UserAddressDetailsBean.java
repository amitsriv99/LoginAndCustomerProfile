package com.labizy.services.login.beans;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserAddressDetailsBean {

	@JsonProperty("addressDetail")
	private ArrayList<UserAddressBean> addressDetail;

	public ArrayList<UserAddressBean> getAddressDetail() {
		return addressDetail;
	}

	public void setAddressDetail(ArrayList<UserAddressBean> addressDetail) {
		this.addressDetail = addressDetail;
	}
}
