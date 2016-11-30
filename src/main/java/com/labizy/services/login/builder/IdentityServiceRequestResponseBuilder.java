package com.labizy.services.login.builder;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;

import com.labizy.services.login.beans.HttpRequestBean;
import com.labizy.services.login.beans.HttpResponseBean;
import com.labizy.services.login.exceptions.EnvironNotDefPropertiesBuilderException;
import com.labizy.services.login.exceptions.InvalidServiceRequestException;
import com.labizy.services.login.exceptions.InvalidServiceResponseException;
import com.labizy.services.login.utils.CommonUtils;

public class IdentityServiceRequestResponseBuilder implements IServiceRequestResponseBuilder {
	private static Logger logger = LoggerFactory.getLogger("com.labizy.services.login.AppLogger");
	
	private PropertiesBuilder propertiesBuilder;
	private CommonUtils commonUtils;
	
	public PropertiesBuilder getPropertiesBuilder() {
		return propertiesBuilder;
	}
	public void setPropertiesBuilder(PropertiesBuilder propertiesBuilder) {
		this.propertiesBuilder = propertiesBuilder;
	}

	public CommonUtils getCommonUtils() {
		return commonUtils;
	}
	public void setCommonUtils(CommonUtils commonUtils) {
		this.commonUtils = commonUtils;
	}

	public HttpRequestBean buildHttpRequest() throws InvalidServiceRequestException {
		if(logger.isDebugEnabled()){
			logger.debug("Inside IdentityServiceRequestResponseBuilderTest.buildHttpRequest() method");
		}

		HttpRequestBean httpRequestBean = new HttpRequestBean();

		try {
			String endpointURL = propertiesBuilder.getEnvironProperties().getIdentityServiceEndpointURL();
			if(logger.isInfoEnabled()){
				logger.info("Endpoint URL : {}", endpointURL);
			}
			httpRequestBean.setEndpointURL(endpointURL);
			
			String requestMethod = HttpMethod.POST.toString();
			
			if(logger.isInfoEnabled()){
				logger.info("Request Method : {}", requestMethod);
			}
			httpRequestBean.setRequestMethod(requestMethod);
			
			boolean useStubs = propertiesBuilder.getEnvironProperties().isUseStubs();
			if(logger.isInfoEnabled()){
				logger.info("Using Stubbed Request : {}", useStubs);
			}
			
			String payload = null;
			if(useStubs){
				String payloadTemplate = propertiesBuilder.getCommonProperties().getStubbedIdentityServiceRequestBody();
				String[] placeholderValues = {"s3cr3t", "str0ng"};
				payload = commonUtils.getMessageFromTemplate(payloadTemplate, placeholderValues);
			}else{
				String payloadTemplate = propertiesBuilder.getEnvironProperties().getIdentityServiceRequestBodyTemplate();

				String secret = System.getProperty(propertiesBuilder.getCommonProperties().getIdentitySecretSystemPropertyName());
				String password = System.getProperty(propertiesBuilder.getCommonProperties().getIdentityPasswordSystemPropertyName());
				
				String[] placeholderValues = {secret, password};
				payload = commonUtils.getMessageFromTemplate(payloadTemplate, placeholderValues);
			}
			if(logger.isDebugEnabled()){
				logger.debug("Request Payload : \n {}", payload);
			}
			httpRequestBean.setPayload(payload);
		} catch (EnvironNotDefPropertiesBuilderException e) {
			logger.error("Exception Occurred {}", e);
			throw new InvalidServiceRequestException(e);
		}
		
		return httpRequestBean;
	}

	private HttpResponseBean buildHttpResponse(){
		if(logger.isDebugEnabled()){
			logger.debug("Inside IdentityServiceRequestResponseBuilderTest.buildHttpResponse() method");
		}
		
		try {
			/*
			 * This stubbed response will be set only in local or test environments.
			 * Let's mimic a service call with network latency by inducing some delays.. 
			 */
			long sleepTimeInMilliSec = -1L;
			try {
				sleepTimeInMilliSec = propertiesBuilder.getEnvironProperties().getInducedResponseDelayInMilliSec();
			} catch (EnvironNotDefPropertiesBuilderException e) {
				sleepTimeInMilliSec = 300;
				logger.warn("Caught Harmless Error {}", e);
			}
			logger.warn("Setting induced response delay to {} millisec", sleepTimeInMilliSec);
			if(sleepTimeInMilliSec > 0){
				TimeUnit.MILLISECONDS.sleep(sleepTimeInMilliSec);
			}
		} catch (InterruptedException e) {
			logger.warn("Caught Harmless Error {}", e);
		}

		HttpResponseBean httpResponseBean = new HttpResponseBean();

		httpResponseBean.setResponseCode(propertiesBuilder.getCommonProperties().getStubbedResponseCode());
		httpResponseBean.setResponseMessage(propertiesBuilder.getCommonProperties().getStubbedResponseStatus());
		httpResponseBean.setResponseBody(propertiesBuilder.getCommonProperties().getStubbedIdentityServiceResponseBody());

		return httpResponseBean;
	}
	
	public HttpResponseBean buildHttpResponse(int responseCode, String responseMessage, String responseBody) throws InvalidServiceResponseException{
		HttpResponseBean httpResponseBean;
		if(logger.isDebugEnabled()){
			logger.debug("Inside IdentityServiceRequestResponseBuilderTest.buildHttpResponse(int, String, String) method");
		}

		boolean useStubs = false;
		try {
			useStubs = propertiesBuilder.getEnvironProperties().isUseStubs();
			if(logger.isInfoEnabled()){
				logger.info("Using Stubbed Response : {}", useStubs);
			}
		} catch (EnvironNotDefPropertiesBuilderException e) {
			logger.error("Caught Exception {}", e);
			throw new InvalidServiceResponseException(e);
		}
		
		if(useStubs){
			httpResponseBean = buildHttpResponse();
		}else{
			httpResponseBean = new HttpResponseBean();
			httpResponseBean.setResponseCode(responseCode);
			httpResponseBean.setResponseMessage(responseMessage);
			httpResponseBean.setResponseBody(responseBody);
		}
		
		return httpResponseBean;
	}
}