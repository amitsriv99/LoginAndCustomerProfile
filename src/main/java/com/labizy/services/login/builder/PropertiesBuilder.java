package com.labizy.services.login.builder;

import com.labizy.services.login.beans.PropertiesBean;
import com.labizy.services.login.exceptions.EnvironNotDefPropertiesBuilderException;
import com.labizy.services.login.utils.CommonUtils;

public class PropertiesBuilder implements IPropertiesBuilder {
	private CommonUtils commonUtils;
	private PropertiesBean commonProperties;
	
	private PropertiesBean localProperties;
	private PropertiesBean testProperties;
	private PropertiesBean ppeProperties;
	private PropertiesBean lnpProperties;
	private PropertiesBean prodProperties;
	
	public CommonUtils getCommonUtils() {
		return commonUtils;
	}
	public void setCommonUtils(CommonUtils commonUtils) {
		this.commonUtils = commonUtils;
	}
	public PropertiesBean getCommonProperties() {
		return commonProperties;
	}
	public void setCommonProperties(PropertiesBean commonProperties) {
		this.commonProperties = commonProperties;
	}
	public PropertiesBean getPpeProperties() {
		return ppeProperties;
	}
	public void setPpeProperties(PropertiesBean ppeProperties) {
		this.ppeProperties = ppeProperties;
	}
	public PropertiesBean getLnpProperties() {
		return lnpProperties;
	}
	public void setLnpProperties(PropertiesBean lnpProperties) {
		this.lnpProperties = lnpProperties;
	}
	public PropertiesBean getProdProperties() {
		return prodProperties;
	}
	public void setProdProperties(PropertiesBean prodProperties) {
		this.prodProperties = prodProperties;
	}
	public PropertiesBean getLocalProperties() {
		return localProperties;
	}
	public void setLocalProperties(PropertiesBean localProperties) {
		this.localProperties = localProperties;
	}
	public PropertiesBean getTestProperties() {
		return testProperties;
	}
	public void setTestProperties(PropertiesBean testProperties) {
		this.testProperties = testProperties;
	}

	public PropertiesBean getEnvironProperties() throws EnvironNotDefPropertiesBuilderException {
		PropertiesBean propertiesBean = null;
		String environ = commonUtils.getEnviron();
		
		if("prod".equals(environ)){
			propertiesBean = prodProperties;
		} else if ("ppe".equals(environ)){
			propertiesBean = ppeProperties;
		}else if ("lnp".equals(environ)){
			propertiesBean = lnpProperties;
		}else if("test".equals(environ)){
			propertiesBean = testProperties;
		}else{
			propertiesBean = localProperties;
		}
		
		return propertiesBean;
	}
}
