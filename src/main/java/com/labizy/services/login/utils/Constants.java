package com.labizy.services.login.utils;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Constants {
	private static Logger logger = LoggerFactory.getLogger("com.labizy.services.login.AppLogger");

	private Map<String, String> defaultMap;
	public void setDefaultMap(Map<String, String> defaultMap) {
		this.defaultMap = defaultMap;
	}

	public static String DefaultValue;
	private static synchronized void setDefaultValue(String value){
		if(logger.isDebugEnabled()){
			logger.debug("Inside {} method", "Constants.setDefault()");
		}
		
		DefaultValue = value;
	}
	
	public static final String DEFAULT_CACHE_KEY_TYPE = "DEFAULT";
	public static final String EMAIL_ID_CACHE_KEY_TYPE = "EMAIL_ID";
}
