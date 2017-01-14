package com.labizy.services.login.dao.adapter;

import com.labizy.services.login.dao.manager.UserLoginDaoManager;
import com.labizy.services.login.utils.CommonUtils;

public class UserLoginDaoAdapter {
	private UserLoginDaoManager userLoginDaoManager;
	private CommonUtils commonUtils;

	public CommonUtils getCommonUtils() {
		return commonUtils;
	}

	public void setCommonUtils(CommonUtils commonUtils) {
		this.commonUtils = commonUtils;
	}

	public UserLoginDaoManager getUserLoginDaoManager() {
		return userLoginDaoManager;
	}

	public void setUserLoginDaoManager(UserLoginDaoManager userLoginDaoManager) {
		this.userLoginDaoManager = userLoginDaoManager;
	}
}
