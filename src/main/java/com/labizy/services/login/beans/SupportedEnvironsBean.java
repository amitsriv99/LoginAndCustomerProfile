package com.labizy.services.login.beans;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Amit Srivastava
 *
 */
public class SupportedEnvironsBean {
	private static Logger logger = LoggerFactory.getLogger("com.labizy.services.login.AppLogger");

	private static Set<String> environsSet;

	public static synchronized void setSupportedEnvirons(String[] environs) {
		if(logger.isDebugEnabled()){
			logger.debug("Inside {} method", "SupportedEnvironsBean.setSupportedEnvirons()");
		}

		environsSet = new HashSet<String>();
		
		if(environs != null){
			if(logger.isDebugEnabled()){
				logger.debug("Initializing {} for speeding up the validation of the environment...", "environsSet");
			}
			for(int i = 0; i < environs.length; i++){
				environsSet.add(environs[i]);
			}
		}
	}
	
	public static String getSupportedEnvirons(){
		if(logger.isDebugEnabled()){
			logger.debug("Inside {} method", "SupportedEnvironsBean.getSupportedEnvirons()");
		}

		return environsSet.toString();
	}
	
	public static boolean isEnvironSupported(String environ){
		if(logger.isDebugEnabled()){
			logger.debug("Inside {} method", "SupportedEnvironsBean.isEnvironSupported(String)");
		}

		return environsSet.contains(environ);
	}
}
