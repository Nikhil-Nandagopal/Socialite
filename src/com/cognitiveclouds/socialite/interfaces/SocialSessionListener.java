package com.cognitiveclouds.socialite.interfaces;

import com.cognitiveclouds.socialite.utils.SessionListenerType;

public interface SocialSessionListener {

	public void onAuthenticationSuccess();

	public void onAuthenticationFailure(String error);
	
	public void onLogoutBegin();

	public void onLogoutFinish();

	public SessionListenerType getType();
	
}
