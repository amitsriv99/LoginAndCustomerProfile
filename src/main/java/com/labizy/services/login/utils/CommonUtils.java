package com.labizy.services.login.utils;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.labizy.services.login.beans.PropertiesBean;
import com.labizy.services.login.exceptions.EnvironNotDefPropertiesBuilderException;

public class CommonUtils {
	private static Logger logger = LoggerFactory.getLogger("com.labizy.services.login.AppLogger");
	private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
	private static SimpleDateFormat simpleDateTimeFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:SS");
	
	private PropertiesBean commonProperties;
	private List<Integer> listOfNumbers = null;
	private long seed;

	public void setCommonProperties(PropertiesBean commonProperties) {
		this.commonProperties = commonProperties;
	}

	public void setSeed(long seed) {
		this.seed = seed;
	}

	public final java.util.Date getStringAsDate(String dateString){
		java.util.Date date = null;
		
		if(StringUtils.isEmpty(dateString)){
			return null;
		}
		
		try {
			date = simpleDateFormat.parse(dateString);
		} catch (ParseException e) {
			logger.error(e.toString());
		}
		
		return date;
	}

	public final java.sql.Timestamp getCurrentDateTimeAsSqlTimestamp(){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		
		return new java.sql.Timestamp(calendar.getTimeInMillis());
	}

	public final String getCurrentTimestampAsString(){
		return simpleDateTimeFormat.format(new java.util.Date(System.currentTimeMillis()));
	}
	
	public final String getTimestampAsDateString(java.sql.Timestamp timestamp, boolean onlyDatePart){
		if(timestamp == null){
			return null;
		}
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timestamp.getTime());
		
		return ((onlyDatePart) ? simpleDateFormat.format(calendar.getTime()) : simpleDateTimeFormat.format(calendar.getTime()));
	}
	
	public final String generateUniquePassword(){
		String password = null;
		password = "Hell0L@bizy";
		
		return password;
	}
	
	public final String getUniqueGeneratedId(String prefix, String suffix){
		if(logger.isDebugEnabled()){
			logger.debug("Inside UniqueIdGenerator.getUniqueGeneratedId() method..");
		}
		
		if(listOfNumbers == null){
			listOfNumbers = new ArrayList<Integer>();  
			
	        for (int i = 1000; i <= 9999; i++) {
	        	listOfNumbers.add(new Integer(i));
	        }
		}
		
        Collections.shuffle(listOfNumbers);
        
        int lowerIndex = 0;
        int upperIndex = (listOfNumbers.size() - 1);
        int randomIndex = ThreadLocalRandom.current().nextInt(lowerIndex, upperIndex);
        
        int randomNumber = -1;
        try{
        	randomNumber = listOfNumbers.get(randomIndex);
        }catch(RuntimeException e){
        	//Do nothing..
        } 
        
        StringBuffer buffer = new StringBuffer();
        
        if(! StringUtils.isEmpty(prefix)){
        	buffer.append(prefix).append("-");
        }
        
       	buffer.append(System.currentTimeMillis()).append("-").append(randomIndex).append("-").append(randomNumber);

       	if(! StringUtils.isEmpty(suffix)){
        	buffer.append("-").append(suffix);
        }
        
        return buffer.toString();
	}

	
	public final String getMessageFromTemplate(String template, String[] placeHolderValues){
		MessageFormat messageFormat = new MessageFormat(template);
		String message = messageFormat.format(placeHolderValues);
		
		return message;
	} 
	
	public final String getEnviron() throws EnvironNotDefPropertiesBuilderException {
		if(logger.isDebugEnabled()){
			logger.debug("Inside {} method", "CommonUtils.getEnviron()");
		}
		
		String environ = System.getProperty(commonProperties.getEnvironSystemPropertyName());
		if(logger.isInfoEnabled()){
			logger.info("***** Environment : {} *****", environ);
		}
		
		if((environ == null) || ("".equals(environ.trim())) || (! commonProperties.getSupportedEnvirons().contains(environ))){
			throw new EnvironNotDefPropertiesBuilderException("System variable '" + commonProperties.getEnvironSystemPropertyName() + "' is not set to point to one of " + commonProperties.getSupportedEnvirons().toString());
		}
		
		return environ;
	} 
}