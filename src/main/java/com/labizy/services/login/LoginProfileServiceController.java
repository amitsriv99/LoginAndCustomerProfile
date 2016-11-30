package com.labizy.services.login;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.labizy.services.login.beans.AuthenticationBean;
import com.labizy.services.login.beans.StatusBean;
import com.labizy.services.login.beans.UserCredentialsBean;
import com.labizy.services.login.utils.CacheFactory;
import com.labizy.services.login.utils.Constants;

@RestController
public class LoginProfileServiceController {
	private static Logger appLogger = LoggerFactory.getLogger("com.labizy.services.login.AppLogger");
	private static Logger traceLogger = LoggerFactory.getLogger("com.labizy.services.login.TraceLogger");
	
	private CacheFactory cacheFactory;

	@RequestMapping(value = "/", method = { RequestMethod.GET, RequestMethod.POST },headers="Accept=application/json")
	public @ResponseBody StatusBean get(final HttpServletResponse httpServletResponse){
		StatusBean status = new StatusBean();
		status.setStatusCode("" + HttpServletResponse.SC_FORBIDDEN);
		status.setStatusMessage("Directory listing is forbidden..!");
		
		httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
		
		return status;
	}
	
	@RequestMapping(value = "/_status", method = { RequestMethod.GET, RequestMethod.POST },headers="Accept=application/json")
	public @ResponseBody StatusBean getStatus(final HttpServletResponse httpServletResponse){
		StatusBean status = new StatusBean();
		status.setStatusCode("" + HttpServletResponse.SC_OK);
		status.setStatusMessage("Healthy..!");
		
		httpServletResponse.setStatus(HttpServletResponse.SC_OK);
		
		return status;
	}
	
	@RequestMapping(value = "/oauth/v1/token", method = RequestMethod.POST, produces="application/json", headers = "Accept=application/json")
	public @ResponseBody AuthenticationBean getToken(@RequestParam MultiValueMap<String, String> requestParams, 
														@RequestBody(required = false) UserCredentialsBean userCredentialsBean, 
															final HttpServletResponse httpServletResponse){
		if(appLogger.isDebugEnabled()){
			appLogger.debug("Inside {}", "LoginProfileServiceController.main()");
		}
		
		long startTimestamp = System.currentTimeMillis();
		
		AuthenticationBean authBean = null;
		String cacheKey = null;
		String cacheKeyType = null;
		
		if(userCredentialsBean != null){
			cacheKey = userCredentialsBean.getEmailId();
			if(! StringUtils.isEmpty(cacheKey)){
				cacheKeyType = Constants.EMAIL_ID_CACHE_KEY_TYPE;
			}
		}
		
		String errorCode = "" + HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
		String errorDescription = "Unknown Exception. Please check the HttpServiceProxy application logs for further details.";
		try {
			authBean = cacheFactory.getCachedObject(cacheKey, cacheKeyType);
		} catch (Exception e){
			appLogger.error("Caught Unknown Exception {}", e);
			errorDescription = errorDescription + "\n" + e.getMessage();
		} finally{
			if (authBean == null){
				authBean = new AuthenticationBean();
				authBean.setErrorCode(errorCode);
				authBean.setErrorDescription(errorDescription);
				
				httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}else{
				if( StringUtils.isEmpty(authBean.getToken() )){
					httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				}else{
					httpServletResponse.setStatus(HttpServletResponse.SC_OK);
				}
			}
		}

		traceLogger.info("Inside LoginProfileServiceController.getToken(). Total Time Taken --> {} milliseconds", (System.currentTimeMillis() - startTimestamp));
		
		return authBean;
	}

	public CacheFactory getCacheFactory() {
		return cacheFactory;
	}
	public void setCacheFactory(CacheFactory cacheFactory) {
		this.cacheFactory = cacheFactory;
	}
}