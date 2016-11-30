package com.labizy.services.login.builder;

import com.labizy.services.login.beans.HttpRequestBean;
import com.labizy.services.login.beans.HttpResponseBean;
import com.labizy.services.login.exceptions.InvalidServiceRequestException;
import com.labizy.services.login.exceptions.InvalidServiceResponseException;

public interface IServiceRequestResponseBuilder {
	public HttpRequestBean buildHttpRequest() throws InvalidServiceRequestException;
	public HttpResponseBean buildHttpResponse(int responseCode, String responseMessage, String responseContent) throws InvalidServiceResponseException;
}
