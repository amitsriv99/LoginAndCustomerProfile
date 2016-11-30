package com.labizy.services.login.beans;

import java.util.Map;

public class HttpResponseBean {
	private int responseCode;
	private String responseMessage;
	private String responseBody;
	private Map<String, String> responseCacheControls;
	public int getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}
	public String getResponseMessage() {
		return responseMessage;
	}
	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}
	public String getResponseBody() {
		return responseBody;
	}
	public void setResponseBody(String responseBody) {
		this.responseBody = responseBody;
	}
}