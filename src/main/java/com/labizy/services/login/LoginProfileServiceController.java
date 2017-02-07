package com.labizy.services.login;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.labizy.services.login.beans.AuthenticationBean;
import com.labizy.services.login.beans.StatusBean;
import com.labizy.services.login.beans.UserAddressDetailsBean;
import com.labizy.services.login.beans.UserContactsDetailsBean;
import com.labizy.services.login.beans.UserCredentialsBean;
import com.labizy.services.login.beans.UserProfileBean;
import com.labizy.services.login.beans.UserProfileDetailsBean;
import com.labizy.services.login.beans.UserProfileDetailsResultBean;
import com.labizy.services.login.dao.adapter.UserLoginDaoAdapter;
import com.labizy.services.login.dao.adapter.UserProfileDaoAdapter;
import com.labizy.services.login.exceptions.ServiceException;
import com.labizy.services.login.exceptions.UserAuthenticationException;
import com.labizy.services.login.exceptions.UserDoesNotExistException;
import com.labizy.services.login.utils.CacheFactory;

@RestController
public class LoginProfileServiceController {
	private static Logger appLogger = LoggerFactory.getLogger("com.labizy.services.login.AppLogger");
	private static Logger traceLogger = LoggerFactory.getLogger("com.labizy.services.login.TraceLogger");
	
	private CacheFactory cacheFactory;
	private UserLoginDaoAdapter userLoginDaoAdapter;
	private UserProfileDaoAdapter userProfileDaoAdapter;

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
	
	@RequestMapping(value = "/oauth/v1/token/{action}", method = RequestMethod.POST, produces="application/json", headers = "Accept=application/json")
	public @ResponseBody AuthenticationBean getToken(@PathVariable("action") String action, @RequestParam MultiValueMap<String, String> requestParams, 
														@RequestBody(required = false) UserCredentialsBean userCredentialsBean, 
															@RequestHeader(value="X-OAUTH-TOKEN", required = false) String oauthToken,
																final HttpServletResponse httpServletResponse){
		if(appLogger.isDebugEnabled()){
			appLogger.debug("Inside {}", "LoginProfileServiceController.getToken()");
		}
		
		long startTimestamp = System.currentTimeMillis();
		
		AuthenticationBean authBean = null;
		
		String errorCode = "" + HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
		String errorDescription = "Unknown Exception. Please check the HttpServiceProxy application logs for further details.";
		
		try {
			if((!"issue".equals(action)) && (!"validate".equals(action)) && (!"expire".equals(action) && (!"reset".equals(action)))){
				errorCode = "" + HttpServletResponse.SC_BAD_REQUEST;
				errorDescription = "Unknown action. Please check the service api document to know the appropriate action that is supported.";
				
				httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}else{
				if("issue".equals(action)){
					authBean = userLoginDaoAdapter.issueOauthToken(userCredentialsBean);
				}
				
				if("validate".equals(action)){
					authBean = userLoginDaoAdapter.validateOauthToken(userCredentialsBean, oauthToken);
				}
				
				if("reset".equals(action)){
					authBean = userLoginDaoAdapter.resetOauthToken(userCredentialsBean, oauthToken);
				}

				if("expire".equals(action)){
					authBean = userLoginDaoAdapter.expireOauthToken(userCredentialsBean, oauthToken);
				}
			}
		} catch (UserAuthenticationException e){
			authBean = new AuthenticationBean();
		
			authBean.setErrorCode("" + HttpServletResponse.SC_UNAUTHORIZED);
			authBean.setErrorDescription("User is unauthorized to access the authentication token..");
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
				if(StringUtils.isEmpty(authBean.getToken())){
					if(StringUtils.isEmpty(authBean.getErrorCode())){
						httpServletResponse.setStatus(HttpServletResponse.SC_OK);
					}else{
						httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
					}
				}else{
					httpServletResponse.setStatus(HttpServletResponse.SC_OK);
				}
			}
		}

		traceLogger.info("Inside LoginProfileServiceController.getToken(). Total Time Taken --> {} milliseconds", (System.currentTimeMillis() - startTimestamp));
		
		return authBean;
	}

	@RequestMapping(value = "/user-profiles/v1/add", method = RequestMethod.POST, produces="application/json", headers = "Accept=application/json")
	public @ResponseBody UserProfileDetailsResultBean addUserAndProfile(@RequestParam MultiValueMap<String, String> requestParams, 
																			@RequestHeader(value="X-OAUTH-TOKEN", required = false) String oauthToken,
																			@RequestBody(required = false) UserProfileDetailsBean userProfileDetailsBean, 
																			final HttpServletResponse httpServletResponse){
		if(appLogger.isDebugEnabled()){
			appLogger.debug("Inside {}", "LoginProfileServiceController.addUserAndProfile()");
		}
		
		long startTimestamp = System.currentTimeMillis();
		
		UserProfileDetailsResultBean userProfileDetailsResultBean = null;
		
		String errorCode = "" + HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
		String errorDescription = "Unknown Exception. Please check the HttpServiceProxy application logs for further details.";

		if(userProfileDetailsBean == null){
			errorCode = "" + HttpServletResponse.SC_BAD_REQUEST;
			errorDescription = "Payload is null. User and/or its profile cannot be created.";

			userProfileDetailsResultBean = new UserProfileDetailsResultBean();
			userProfileDetailsResultBean.setErrorCode(errorCode);
			userProfileDetailsResultBean.setErrorDescription(errorDescription);
			
			httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}else{
			errorCode = "" + HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
			errorDescription = "Unknown Exception. Please check the HttpServiceProxy application logs for further details.";
			try {
				String userId = null;
				userProfileDetailsResultBean = new UserProfileDetailsResultBean();
				if(userProfileDetailsBean.getUserLogin() != null){
					userId = userProfileDaoAdapter.createUser(userProfileDetailsBean.getUserLogin());
					UserCredentialsBean userCredentials = new UserCredentialsBean();
					userCredentials.setEmailId(userProfileDetailsBean.getUserLogin().getEmailId());
					userProfileDetailsResultBean.setUserLogin(userCredentials);
				}
				
				if(userProfileDetailsBean.getUserProfile() != null){
					UserProfileBean userProfile = userProfileDaoAdapter.createUserProfile(userId, userProfileDetailsBean.getUserProfile());
					userId = userProfile.getUserId();
					userProfileDetailsResultBean.setUserProfile(userProfile);
				}
				
				if(userProfileDetailsBean.getContactDetails() != null){
					UserContactsDetailsBean contactDetails = userProfileDaoAdapter.createUserContact(userId, userProfileDetailsBean.getContactDetails());
					userProfileDetailsResultBean.setContactDetails(contactDetails);
				}
				
				if(userProfileDetailsBean.getAddressDetails() != null){
					UserAddressDetailsBean addressDetails = userProfileDaoAdapter.createUserAddress(userId, userProfileDetailsBean.getAddressDetails());
					userProfileDetailsResultBean.setAddressDetails(addressDetails);
				}
			} catch (Exception e){
				userProfileDetailsResultBean = null;
				appLogger.error("Caught Unknown Exception {}", e);
				errorDescription = errorDescription + "\n" + e.getMessage();
			} finally{
				if (userProfileDetailsResultBean == null){
					userProfileDetailsResultBean = new UserProfileDetailsResultBean();
					userProfileDetailsResultBean.setErrorCode(errorCode);
					userProfileDetailsResultBean.setErrorDescription(errorDescription);
					
					httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				}
			}
		}
		
		traceLogger.info("Inside LoginProfileServiceController.addUserAndProfile(). Total Time Taken --> {} milliseconds", (System.currentTimeMillis() - startTimestamp));
		
		return userProfileDetailsResultBean;
	}

	@RequestMapping(value = "/user-profiles/v1/{id}", method = RequestMethod.GET, produces="application/json")
	public @ResponseBody UserProfileDetailsResultBean getUserProfileDetails(@PathVariable("id") String id, @RequestParam MultiValueMap<String, String> requestParams,
															@RequestHeader(value="X-OAUTH-TOKEN", required = false) String oauthToken,
																final HttpServletResponse httpServletResponse){
		if(appLogger.isDebugEnabled()){
			appLogger.debug("Inside {}", "LoginProfileServiceController.getUserProfileDetails()");
		}
		long startTimestamp = System.currentTimeMillis();
		
		UserProfileDetailsResultBean userProfileDetailsResultBean = null;
		String errorCode = "" + HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
		String errorDescription = "Unknown Exception. Please check the HttpServiceProxy application logs for further details.";
	
		boolean isPrimaryProfile = true;
		String primaryProfile = requestParams.getFirst("primary");
		if(! StringUtils.isEmpty(primaryProfile)){
			try{
				isPrimaryProfile = Boolean.parseBoolean(primaryProfile);
			}catch(Exception e){
				//Assume it is requested for primary profile..
			}
		}
		
		if(StringUtils.isEmpty(id)){
			errorCode = "" + HttpServletResponse.SC_BAD_REQUEST;
			errorDescription = "User id is null. User and/or its profile cannot be retrieved.";

			userProfileDetailsResultBean = new UserProfileDetailsResultBean();
			userProfileDetailsResultBean.setErrorCode(errorCode);
			userProfileDetailsResultBean.setErrorDescription(errorDescription);
			
			httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}else{
			try {
				userProfileDetailsResultBean = userProfileDaoAdapter.getUserProfileDetails(id, isPrimaryProfile);
				httpServletResponse.setStatus(HttpServletResponse.SC_OK);
			} catch(UserDoesNotExistException e){
				userProfileDetailsResultBean = new UserProfileDetailsResultBean();
				userProfileDetailsResultBean.setErrorCode("" + HttpServletResponse.SC_NOT_FOUND);
				userProfileDetailsResultBean.setErrorDescription("User either doesn't exist or has been deleted.");
				
				httpServletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
			} catch (ServiceException e) {
				userProfileDetailsResultBean = new UserProfileDetailsResultBean();
				userProfileDetailsResultBean.setErrorCode("" + HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				userProfileDetailsResultBean.setErrorDescription(e.getMessage());
				
				httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			} 
		}

		traceLogger.info("Inside LoginProfileServiceController.getUserProfileDetails(). Total Time Taken --> {} milliseconds", (System.currentTimeMillis() - startTimestamp));

		return userProfileDetailsResultBean;
	}

	@RequestMapping(value = "/user-profiles/v1/update/{id}", method = RequestMethod.PUT, produces="application/json", headers = "Accept=application/json")
	public @ResponseBody UserProfileDetailsResultBean updateUserAndProfile(@PathVariable("id") String id, @RequestParam MultiValueMap<String, String> requestParams, 
																			@RequestHeader(value="X-OAUTH-TOKEN", required = false) String oauthToken,
																			@RequestBody(required = false) UserProfileDetailsBean userProfileDetailsBean, 
																			final HttpServletResponse httpServletResponse){
		if(appLogger.isDebugEnabled()){
			appLogger.debug("Inside {}", "LoginProfileServiceController.updateUserAndProfile()");
		}
		
		long startTimestamp = System.currentTimeMillis();
		
		UserProfileDetailsResultBean userProfileDetailsResultBean = null;
		
		String errorCode = "" + HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
		String errorDescription = "Unknown Exception. Please check the HttpServiceProxy application logs for further details.";

		if(userProfileDetailsBean == null){
			errorCode = "" + HttpServletResponse.SC_BAD_REQUEST;
			errorDescription = "Payload is null. User and/or its profile cannot be updated.";

			userProfileDetailsResultBean = new UserProfileDetailsResultBean();
			userProfileDetailsResultBean.setErrorCode(errorCode);
			userProfileDetailsResultBean.setErrorDescription(errorDescription);
			
			httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}else{
			errorCode = "" + HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
			errorDescription = "Unknown Exception. Please check the HttpServiceProxy application logs for further details.";
			
			try {
				String userId = null;
				boolean isPrimaryProfile = true;
				
				boolean passwordUpdateExists = (userProfileDetailsBean.getUserLogin() != null);
				boolean profileUpdateExists = (userProfileDetailsBean.getUserProfile() != null);
				
				AuthenticationBean authBean = null;
				
				if(passwordUpdateExists){
					authBean = userLoginDaoAdapter.validateOauthToken(null, oauthToken);
					userId = authBean.getClientId();
					
					if(("basic".equals(authBean.getGrantType())) && ("user".equals(authBean.getTokenType()))){
						if(userProfileDetailsBean.getUserProfile() == null){
							UserProfileBean userProfileBean = new UserProfileBean();
							userProfileBean.setUserId(userId);
							
							userProfileDetailsBean.setUserProfile(userProfileBean);
						}else{
							userProfileDetailsBean.getUserProfile().setUserId(userId);
						}
						
						userProfileDaoAdapter.updateUserPassword(userProfileDetailsBean);
					}
				}
				
				if(profileUpdateExists){
					userId = userProfileDetailsBean.getUserProfile().getUserId();
					try{
						isPrimaryProfile = Boolean.parseBoolean(userProfileDetailsBean.getUserProfile().getIsPrimaryProfile());
					} catch(Exception e){
						//Assume it's for the primary profile
						isPrimaryProfile = true;
					}
					userProfileDaoAdapter.updateUserProfile(userProfileDetailsBean);
					httpServletResponse.setStatus(HttpServletResponse.SC_OK);
				}
				
				userProfileDetailsResultBean = userProfileDaoAdapter.getUserProfileDetails(userId, isPrimaryProfile);
			} catch (UserDoesNotExistException e){
				userProfileDetailsResultBean = new UserProfileDetailsResultBean();
				
				userProfileDetailsResultBean.setErrorCode("" + HttpServletResponse.SC_NOT_FOUND);
				userProfileDetailsResultBean.setErrorDescription("User either doesn't exist or has been deleted..");
				
				httpServletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
			} catch (UserAuthenticationException e){
				userProfileDetailsResultBean = new UserProfileDetailsResultBean();
				
				userProfileDetailsResultBean.setErrorCode("" + HttpServletResponse.SC_UNAUTHORIZED);
				userProfileDetailsResultBean.setErrorDescription("User is unauthorized to access the authentication token..");
				
				httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			} catch (ServiceException e){
				appLogger.error("Caught Unknown Exception {}", e);
				errorDescription = errorDescription + "\n" + e.getMessage();
			} finally{
				if (userProfileDetailsResultBean == null){
					userProfileDetailsResultBean = new UserProfileDetailsResultBean();
					userProfileDetailsResultBean.setErrorCode(errorCode);
					userProfileDetailsResultBean.setErrorDescription(errorDescription);
					
					httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				}
			}
		}
		
		traceLogger.info("Inside LoginProfileServiceController.updateUserAndProfile(). Total Time Taken --> {} milliseconds", (System.currentTimeMillis() - startTimestamp));
		
		return userProfileDetailsResultBean;
	}

	@RequestMapping(value = "/user-profiles/v1/delete/{id}", method = RequestMethod.DELETE, produces="application/json")
	public @ResponseBody StatusBean deleteUserAndProfile(@PathVariable("id") String id, @RequestParam MultiValueMap<String, String> requestParams,
																@RequestBody(required = false) UserCredentialsBean userCredentialsBean, 												
																@RequestHeader(value="X-OAUTH-TOKEN", required = false) String oauthToken,
																final HttpServletResponse httpServletResponse){
		if(appLogger.isDebugEnabled()){
			appLogger.debug("Inside {}", "LoginProfileServiceController.deleteUserProfile()");
		}
		
		long startTimestamp = System.currentTimeMillis();		
		
		StatusBean statusBean = null;
		
		try{
			AuthenticationBean authBean = userLoginDaoAdapter.validateOauthToken(userCredentialsBean, oauthToken);
			
			userProfileDaoAdapter.deleteUserAndProfile(id);
			httpServletResponse.setStatus(HttpServletResponse.SC_OK);
		} catch (UserAuthenticationException e){
			statusBean = new StatusBean();
			statusBean.setStatusCode("" + HttpServletResponse.SC_UNAUTHORIZED);
			statusBean.setStatusMessage("User is unauthorized to access the authentication token..");
		
			httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		} catch(ServiceException e){
			statusBean = new StatusBean();
			statusBean.setStatusCode("" + HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			statusBean.setStatusMessage(e.getMessage());

			httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}

		traceLogger.info("Inside LoginProfileServiceController.deleteUserAndProfile(). Total Time Taken --> {} milliseconds", (System.currentTimeMillis() - startTimestamp));

		return statusBean;
	}

	public void setCacheFactory(CacheFactory cacheFactory) {
		this.cacheFactory = cacheFactory;
	}

	public void setUserLoginDaoAdapter(UserLoginDaoAdapter userLoginDaoAdapter) {
		this.userLoginDaoAdapter = userLoginDaoAdapter;
	}

	public void setUserProfileDaoAdapter(UserProfileDaoAdapter userProfileDaoAdapter) {
		this.userProfileDaoAdapter = userProfileDaoAdapter;
	}
}