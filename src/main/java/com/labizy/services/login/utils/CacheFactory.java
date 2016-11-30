package com.labizy.services.login.utils;

import java.util.Map;
import java.util.WeakHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.labizy.services.login.beans.AuthenticationBean;

public class CacheFactory {
	private static Logger logger = LoggerFactory.getLogger("com.labizy.services.login.AppLogger");
	
	private long maxAge;
	private Map<String, CacheFactory.CacheObject> cacheStore;
	
	private CommonUtils commonUtils;
	
	public void setCommonUtils(CommonUtils commonUtils) {
		this.commonUtils = commonUtils;
	}

	public CacheFactory(int cacheMaxAgeInMinutes) {
		if(logger.isInfoEnabled()){
			logger.info("Inside IdentityOAuthCacheFactory c'tor");
		}

		if((cacheMaxAgeInMinutes <= 0) || (cacheMaxAgeInMinutes > 60)){
			cacheMaxAgeInMinutes = 1000 * 60 * 60;
		}
		
		if(logger.isInfoEnabled()){
			logger.info("The max age of cached objects is set to " + cacheMaxAgeInMinutes + " minutes");
		}

		this.maxAge = 1000 * 60 * cacheMaxAgeInMinutes;
		this.cacheStore = new WeakHashMap<String, CacheFactory.CacheObject>();
	}

	public AuthenticationBean getCachedObject(String cacheKey, String cacheKeyType){
		if(logger.isDebugEnabled()){
			logger.debug("Inside {}", "IdentityOAuthCacheFactory.get(String)");
		}
		
		AuthenticationBean authenticationBean = null;
		CacheObject cacheObject = null;
		
		if(! StringUtils.isEmpty(cacheKey)){
			cacheObject = cacheStore.get(cacheKey);
			
			if(cacheObject == null){
				if(logger.isInfoEnabled()){
					logger.info("Cache store was empty for cache key : {}", cacheKey);
				}
	
				synchronized(this){
					cacheObject = cacheStore.get(cacheKey);
					if(cacheObject == null){
						authenticationBean = getDefaultAuthenticationBean(cacheKeyType);
						
						cacheObject = new CacheObject(authenticationBean);
						cacheKey = authenticationBean.getClientId();
						cacheStore.put(cacheKey, cacheObject);
					}
				}
			} else if((System.currentTimeMillis() - cacheObject.birthTimestamp) > this.maxAge){
				if(logger.isInfoEnabled()){
					logger.info("Cache store has expired the cache key : {}", cacheKey);
				}
	
				synchronized(this){
					cacheObject = cacheStore.get(cacheKey);
					if((System.currentTimeMillis() - cacheObject.birthTimestamp) > this.maxAge){
						cacheStore.remove(cacheKey);
	
						authenticationBean = getDefaultAuthenticationBean(cacheKeyType); 
						cacheObject = new CacheObject(authenticationBean);
						
						cacheStore.put(cacheKey, cacheObject);
					}
				}
			} 
			else{
				if(logger.isInfoEnabled()){
					logger.info("Cache store has a valid item for cache key : {}", cacheKey);
				}
	
				authenticationBean = cacheObject.authenticationBean;
			}
		}else{
			authenticationBean = getDefaultAuthenticationBean(cacheKeyType);
			
			cacheObject = new CacheObject(authenticationBean);
			cacheKey = authenticationBean.getClientId();
			cacheStore.put(cacheKey, cacheObject);
		}
		
		return authenticationBean;
	}

	private AuthenticationBean getDefaultAuthenticationBean(String cacheKeyType){
		AuthenticationBean authenticationBean = new AuthenticationBean();

		authenticationBean.setToken(commonUtils.getUniqueGeneratedId("AUTH", null));
		authenticationBean.setClientId(commonUtils.getUniqueGeneratedId("CLIENT", null));
		authenticationBean.setExpires("3600");
		
		if(Constants.EMAIL_ID_CACHE_KEY_TYPE.equals(cacheKeyType)){
			authenticationBean.setGrantType("advanced");
		}else{
			authenticationBean.setGrantType("basic");
		}
		
		return authenticationBean;
	}
	
	private class CacheObject{
		private long birthTimestamp;
		private AuthenticationBean authenticationBean;
		
		public CacheObject(AuthenticationBean authenticationBean){
			this.authenticationBean = authenticationBean;
			this.birthTimestamp = System.currentTimeMillis();
		}
	}
}