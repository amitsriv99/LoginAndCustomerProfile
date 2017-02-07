package com.labizy.services.login.utils;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FieldComparator {
	private static Logger appLogger = LoggerFactory.getLogger("com.labizy.services.login.AppLogger");

	public List<String> getDeclaredFields(Object beanObject){
		List<String> fieldsList = null;
		
		if(beanObject != null){
			fieldsList = new ArrayList<String>();
			Class clazz = beanObject.getClass();
			
			Field[] allFields = clazz.getDeclaredFields();
			
			for (Field field : allFields) {
				if (Modifier.isPrivate(field.getModifiers())) {
					fieldsList.add(field.getName());
				}
			}
		}
		
	    return fieldsList;
	}
	
	public boolean isFieldValueSame(String fieldName, Object beanObject1, Object beanObject2) {
		boolean result = false;
		
		String fieldValue1 = null;
		String fieldValue2 = null;
		
		if(appLogger.isDebugEnabled()){
			appLogger.debug("Inside FieldComparator.isFieldValueSame(String, Object, Object)");
		}
		
		if((beanObject1 != null) && (beanObject2 != null)){
			try {
				Class clazz1 = beanObject1.getClass();
				Object fieldValueObject1 = new PropertyDescriptor(fieldName, clazz1).getReadMethod().invoke(beanObject1);
				fieldValue1 = (fieldValueObject1 == null) ? null : fieldValueObject1.toString();
				
				if(appLogger.isDebugEnabled()){
					appLogger.debug("Field Name (1) --> " + fieldName + "\t\t" + "Field Value --> " + fieldValue1);
				}
				
				Class clazz2 = beanObject2.getClass();
				Object fieldValueObject2 = new PropertyDescriptor(fieldName, clazz2).getReadMethod().invoke(beanObject2);
				fieldValue2 = (fieldValueObject2 == null) ? null : fieldValueObject2.toString();
				
				if(appLogger.isDebugEnabled()){
					appLogger.debug("Field Name (2) --> " + fieldName + "\t\t" + "Field Value --> " + fieldValue2);
				}
				
				if((fieldValue1 == null) || (fieldValue2 == null)){
					result = false;
				}else{
					result = fieldValue1.equals(fieldValue2);
				}
			} catch (IllegalAccessException e) {
				appLogger.warn("Inside FieldComparator.isFieldValueSame(String, Object, Object). " + e.toString() + "::" + e.getMessage());
			} catch (IllegalArgumentException e) {
				appLogger.warn("Inside FieldComparator.isFieldValueSame(String, Object, Object). " + e.toString() + "::" + e.getMessage());
			} catch (InvocationTargetException e) {
				appLogger.warn("Inside FieldComparator.isFieldValueSame(String, Object, Object). " + e.toString() + "::" + e.getMessage());
			} catch (IntrospectionException e) {
				appLogger.warn("Inside FieldComparator.isFieldValueSame(String, Object, Object). " + e.toString() + "::" + e.getMessage());
			}
		}
		
		return result;
	}

}
