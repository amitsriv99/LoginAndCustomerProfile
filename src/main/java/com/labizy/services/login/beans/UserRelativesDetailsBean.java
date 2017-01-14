package com.labizy.services.login.beans;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserRelativesDetailsBean {

	@JsonProperty("relativeDetails")
	private ArrayList<UserRelativeProfileBean> relativeDetails;
}
