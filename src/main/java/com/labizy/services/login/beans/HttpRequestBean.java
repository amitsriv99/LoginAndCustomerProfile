package com.labizy.services.login.beans;

import java.util.Map;

public class HttpRequestBean {
	private String endpointURL;
	private Map<String, String> cookies;
	private Map<String, String> headers;
	private String requestMethod;
	private String payload;
	
	public HttpRequestBean(){
		
	}
	
	public String getEndpointURL() {
		return endpointURL;
	}
	public void setEndpointURL(String endpointURL) {
		this.endpointURL = endpointURL;
	}

	public String getRequestMethod() {
		return requestMethod;
	}
	public void setRequestMethod(String requestMethod) {
		this.requestMethod = requestMethod;
	}

	public String getPayload() {
		return payload;
	}
	public void setPayload(String payload) {
		this.payload = payload;
	}
}
