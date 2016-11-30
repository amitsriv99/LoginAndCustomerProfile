package com.labizy.services.login.beans;

public class PropertiesBean {
	private String identityServiceEndpointURL;
	private String identityServiceRequestBodyTemplate;
	private String identitySecretSystemPropertyName;
	private String identityPasswordSystemPropertyName;
	private long inducedResponseDelayInMilliSec;

	private String environSystemPropertyName;
	
	private boolean useStubs;
	private String stubbedIdentityServiceRequestBody;
	private String stubbedIdentityServiceResponseBody;
	private int stubbedResponseCode;
	private String stubbedResponseStatus;
	
	public String getIdentityServiceEndpointURL() {
		return identityServiceEndpointURL;
	}
	public void setIdentityServiceEndpointURL(String identityServiceEndpointURL) {
		this.identityServiceEndpointURL = identityServiceEndpointURL;
	}
	public String getIdentityServiceRequestBodyTemplate() {
		return identityServiceRequestBodyTemplate;
	}
	public void setIdentityServiceRequestBodyTemplate(String identityServiceRequestBodyTemplate) {
		this.identityServiceRequestBodyTemplate = identityServiceRequestBodyTemplate;
	}
	public long getInducedResponseDelayInMilliSec() {
		return inducedResponseDelayInMilliSec;
	}
	public void setInducedResponseDelayInMilliSec(long inducedResponseDelayInMilliSec) {
		this.inducedResponseDelayInMilliSec = inducedResponseDelayInMilliSec;
	}
	public String getEnvironSystemPropertyName() {
		return environSystemPropertyName;
	}
	public void setEnvironSystemPropertyName(String environSystemPropertyName) {
		this.environSystemPropertyName = environSystemPropertyName;
	}
	public boolean isUseStubs() {
		return useStubs;
	}
	public void setUseStubs(boolean useStubs) {
		this.useStubs = useStubs;
	}
	public String getStubbedIdentityServiceRequestBody() {
		return stubbedIdentityServiceRequestBody;
	}
	public void setStubbedIdentityServiceRequestBody(
			String stubbedIdentityServiceRequestBody) {
		this.stubbedIdentityServiceRequestBody = stubbedIdentityServiceRequestBody;
	}
	public String getStubbedIdentityServiceResponseBody() {
		return stubbedIdentityServiceResponseBody;
	}
	public void setStubbedIdentityServiceResponseBody(
			String stubbedIdentityServiceResponseBody) {
		this.stubbedIdentityServiceResponseBody = stubbedIdentityServiceResponseBody;
	}
	public int getStubbedResponseCode() {
		return stubbedResponseCode;
	}
	public void setStubbedResponseCode(int stubbedResponseCode) {
		this.stubbedResponseCode = stubbedResponseCode;
	}
	public String getStubbedResponseStatus() {
		return stubbedResponseStatus;
	}
	public void setStubbedResponseStatus(String stubbedResponseStatus) {
		this.stubbedResponseStatus = stubbedResponseStatus;
	}
	public String getIdentitySecretSystemPropertyName() {
		return identitySecretSystemPropertyName;
	}
	public void setIdentitySecretSystemPropertyName(
			String identitySecretSystemPropertyName) {
		this.identitySecretSystemPropertyName = identitySecretSystemPropertyName;
	}
	public String getIdentityPasswordSystemPropertyName() {
		return identityPasswordSystemPropertyName;
	}
	public void setIdentityPasswordSystemPropertyName(
			String identityPasswordSystemPropertyName) {
		this.identityPasswordSystemPropertyName = identityPasswordSystemPropertyName;
	}
}