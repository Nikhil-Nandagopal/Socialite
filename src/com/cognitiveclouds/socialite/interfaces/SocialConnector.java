package com.cognitiveclouds.socialite.interfaces;

import com.cognitiveclouds.socialite.utils.SocialConnectorType;

public interface SocialConnector {

	public void authenticate();
	public void postMessage(String message);
	public void logout();
	public boolean isAuthenticated();
	public String[] getAccessToken();
	public SocialConnectorType getType();
	public void addSocialSessionListener(SocialSessionListener socialSessionListener);
	public void removeSocialSessionListener(SocialSessionListener socialSessionListener);
	
}
