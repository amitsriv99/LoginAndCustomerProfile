package com.labizy.services.login.dao.adapter;

import java.util.Map;

import org.springframework.util.StringUtils;

import com.labizy.services.login.beans.AuthenticationBean;
import com.labizy.services.login.beans.UserCredentialsBean;
import com.labizy.services.login.dao.manager.UserLoginDaoManager;
import com.labizy.services.login.dao.manager.UserProfileDaoManager;
import com.labizy.services.login.exceptions.DataIntegrityException;
import com.labizy.services.login.exceptions.DataNotFoundException;
import com.labizy.services.login.exceptions.ServiceException;
import com.labizy.services.login.exceptions.UserAuthenticationException;
import com.labizy.services.login.utils.CommonUtils;

public class UserLoginDaoAdapter {
	private UserLoginDaoManager userLoginDaoManager;
	private UserProfileDaoManager userProfileDaoManager;
	private CommonUtils commonUtils;

	private AuthenticationBean populateAuthenticationBean(Map<String, String> resultMap) {
		AuthenticationBean authenticationBean = new AuthenticationBean();
		
		if((resultMap == null) || (resultMap.isEmpty())){
			authenticationBean.setToken(commonUtils.getUniqueGeneratedId("OAUTH", "U"));
			authenticationBean.setTokenType("user");
			
			authenticationBean.setExpires("3600");
			authenticationBean.setGrantType("basic");
		}else{
			authenticationBean.setClientId(resultMap.get("clientId"));
			authenticationBean.setExpires(null);
			
			boolean isGuestUser = Boolean.parseBoolean(resultMap.get("isGuestUser"));
			String grantType = (isGuestUser) ? "basic" : "advanced";
			authenticationBean.setGrantType(grantType);
			boolean isInternalUser = Boolean.parseBoolean(resultMap.get("isInternalUser"));
			authenticationBean.setGrantType((isInternalUser) ? "priviledged" : grantType);
			
			authenticationBean.setToken(resultMap.get("oauthToken"));
			authenticationBean.setTokenType(resultMap.get("tokenType"));
			
			authenticationBean.setComments(resultMap.get("comments"));
		}
		
		return authenticationBean;
	}
	
	public CommonUtils getCommonUtils() {
		return commonUtils;
	}

	public void setCommonUtils(CommonUtils commonUtils) {
		this.commonUtils = commonUtils;
	}

	public void setUserProfileDaoManager(UserProfileDaoManager userProfileDaoManager) {
		this.userProfileDaoManager = userProfileDaoManager;
	}

	public void setUserLoginDaoManager(UserLoginDaoManager userLoginDaoManager) {
		this.userLoginDaoManager = userLoginDaoManager;
	}

	public AuthenticationBean issueOauthToken(UserCredentialsBean userCredentialsBean) throws ServiceException, UserAuthenticationException{
		AuthenticationBean authenticationBean = null;
		Map<String, String> resultMap = null;
		
		if(userCredentialsBean != null){
			try {
				resultMap = userLoginDaoManager.issueToken(userCredentialsBean.getEmailId(), userCredentialsBean.getPassword());
			} catch(DataIntegrityException e){
				throw new UserAuthenticationException(e);
			}catch (Exception e) {
				throw new ServiceException(e);
			}
		}
		
		authenticationBean = populateAuthenticationBean(resultMap);
		
		return authenticationBean;
	}

	public AuthenticationBean validateOauthToken(UserCredentialsBean userCredentialsBean, String oauthToken) throws ServiceException, UserAuthenticationException {
		AuthenticationBean authenticationBean = null;
		String clientId = null;
		
		if((userCredentialsBean == null) && (StringUtils.isEmpty(oauthToken))){
			throw new UserAuthenticationException("User is unauthorized to access the authentication token..");
		}
		
		try{
			if(StringUtils.isEmpty(oauthToken)){
				Map<String, String> userMap = userProfileDaoManager.getUserId(userCredentialsBean.getEmailId(), userCredentialsBean.getPassword());
				clientId = userMap.get("clientId");
			}
			Map<String, String> resultMap = userLoginDaoManager.validateToken(clientId, oauthToken, true);
			
			authenticationBean = populateAuthenticationBean(resultMap);
		}catch(DataNotFoundException e){
			throw new UserAuthenticationException(e);
		}catch(Exception e){
			throw new ServiceException(e);
		}
		
		return authenticationBean;
	}

	public AuthenticationBean resetOauthToken(UserCredentialsBean userCredentialsBean, String oauthToken) throws ServiceException, UserAuthenticationException {
		AuthenticationBean authenticationBean = null;
		String clientId = null;
		
		if((userCredentialsBean == null) && (StringUtils.isEmpty(oauthToken))){
			throw new UserAuthenticationException("User is unauthorized to access the authentication token..");
		}
		
		try{
			Map<String, String> resultMap = userLoginDaoManager.resetToken(userCredentialsBean.getEmailId());
			
			authenticationBean = populateAuthenticationBean(resultMap);
		}catch(DataNotFoundException e){
			throw new UserAuthenticationException(e);
		}catch(Exception e){
			throw new ServiceException(e);
		}
		
		return authenticationBean;
	}

	public AuthenticationBean expireOauthToken(UserCredentialsBean userCredentialsBean, String oauthToken) throws ServiceException, UserAuthenticationException {
		AuthenticationBean authenticationBean = null;
		String clientId = null;

		if((userCredentialsBean == null) && (StringUtils.isEmpty(oauthToken))){
			throw new UserAuthenticationException("User is unauthorized to access the authentication token..");
		}

		try{
			if(oauthToken == null){
				Map<String, String> userMap = userProfileDaoManager.getUserId(userCredentialsBean.getEmailId(), userCredentialsBean.getPassword());
				clientId = userMap.get("clientId");
			}
			userLoginDaoManager.expireToken(clientId, oauthToken);

			authenticationBean = new AuthenticationBean();
			authenticationBean.setClientId(clientId);
			authenticationBean.setToken(null);
			authenticationBean.setTokenType("user");
			authenticationBean.setGrantType("basic");
		}catch(DataNotFoundException e){
			throw new UserAuthenticationException(e);
		}catch(Exception e){
			throw new ServiceException(e);
		}
		
		return authenticationBean;
	}
	
}