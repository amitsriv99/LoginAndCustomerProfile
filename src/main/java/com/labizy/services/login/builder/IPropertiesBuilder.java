package com.labizy.services.login.builder;

import com.labizy.services.login.beans.PropertiesBean;
import com.labizy.services.login.exceptions.EnvironNotDefPropertiesBuilderException;

public interface IPropertiesBuilder {
	public PropertiesBean getCommonProperties();
	public PropertiesBean getEnvironProperties() throws EnvironNotDefPropertiesBuilderException;
}
