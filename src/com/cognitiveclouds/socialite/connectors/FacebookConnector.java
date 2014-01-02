package com.cognitiveclouds.socialite.connectors;

import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.cognitiveclouds.socialite.activity.SocialActivity;
import com.cognitiveclouds.socialite.activity.SocialSliderActivity;
import com.cognitiveclouds.socialite.interfaces.SocialConnector;
import com.cognitiveclouds.socialite.interfaces.SocialSessionListener;
import com.cognitiveclouds.socialite.listeners.FacebookRequestListener;
import com.cognitiveclouds.socialite.utils.SessionEvents;
import com.cognitiveclouds.socialite.utils.SessionListenerType;
import com.cognitiveclouds.socialite.utils.SharedPreferencesCredentialStore;
import com.cognitiveclouds.socialite.utils.SocialConnectorType;
import com.cognitiveclouds.socialite.utils.SocialConstants;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;

public class FacebookConnector implements SocialConnector {

	private final String TAG = getClass().getName();
	private Facebook facebook = null;
	private String[] permissions;
	private Handler mHandler;
	private Activity activity;

	public FacebookConnector(String appId, String[] permissions, SocialActivity socialActivity) {

		this.facebook = new Facebook(appId);
		SharedPreferencesCredentialStore.restoreFacebookSession(facebook, socialActivity.getApplicationContext());
		this.permissions=permissions;
		this.mHandler = new Handler();
		this.activity = socialActivity;
		socialActivity.registerSocialConnector(this);

	}

	public FacebookConnector(String appId, String[] permissions, SocialSliderActivity socialActivity) {

		this.facebook = new Facebook(appId);
		SharedPreferencesCredentialStore.restoreFacebookSession(facebook, socialActivity.getApplicationContext());
		this.permissions=permissions;
		this.mHandler = new Handler();
		this.activity = socialActivity;
		socialActivity.registerSocialConnector(this);

	}
	
	@Override
	public void authenticate() {
		facebook.authorize(activity, permissions, new LoginDialogListener());
	}

	public void forceAuthenticate() {
		facebook.authorize(this.activity, this.permissions, Facebook.FORCE_DIALOG_AUTH,new LoginDialogListener());
	}

	@Override
	public void logout() {
		SessionEvents.onLogoutBegin(SessionListenerType.FACEBOOK_SESSION_LISTENER);
		AsyncFacebookRunner asyncRunner = new AsyncFacebookRunner(this.facebook);
		asyncRunner.logout(activity.getApplicationContext(), new LogoutRequestListener());
	}

	@Override
	public void postMessage(String message) {
		if (facebook.isSessionValid()) {
			Bundle parameters = new Bundle();
			parameters.putString(SocialConstants.FACEBOOK_MESSAGE_KEY, message);
			try {
				String response = facebook.request(SocialConstants.FACEBOOK_MYFEED_GRAPHPATH, parameters, SocialConstants.POST);
				//Log.i(TAG, response);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void publishFeedDialog(String name, String caption, String description, String linkUrl, String pictureUrl, OnCompleteListener onPostCompleteListener) {
		if(facebook.isSessionValid()) {
			Bundle params = new Bundle();
			params.putString("name", name);
			params.putString("caption", caption);
			params.putString("description", description);
			params.putString("link", linkUrl);
			params.putString("picture", pictureUrl);

			WebDialog feedDialog = (
					new WebDialog.FeedDialogBuilder(activity,
							facebook.getSession(),
							params))
							.setOnCompleteListener(onPostCompleteListener)
							.build();
			feedDialog.show();
		}
	}

	public Facebook getFacebook() {
		return this.facebook;
	}

	@Override
	public boolean isAuthenticated() {
		return SharedPreferencesCredentialStore.restoreFacebookSession(facebook, activity);
	}

	@Override
	public String[] getAccessToken() {
		SharedPreferencesCredentialStore.restoreFacebookSession(facebook, activity.getApplicationContext());
		return new String [] { facebook.getAccessToken() };
	}

	@Override
	public SocialConnectorType getType() {
		return SocialConnectorType.FACEBOOK_CONNECTOR;
	}

	@Override
	public void addSocialSessionListener(
			SocialSessionListener socialSessionListener) {
		SessionEvents.addSocialSessionListener(socialSessionListener);
	}

	@Override
	public void removeSocialSessionListener(
			SocialSessionListener socialSessionListener) {
		SessionEvents.removeSocialSessionListener(socialSessionListener);
	}

	private final class LoginDialogListener implements DialogListener {

		public void onComplete(Bundle values) {
			SessionEvents.onLoginSuccess(SessionListenerType.FACEBOOK_SESSION_LISTENER);
		}

		public void onFacebookError(FacebookError error) {
			SessionEvents.onLoginError(error.getMessage(),SessionListenerType.FACEBOOK_SESSION_LISTENER);
		}

		public void onError(DialogError error) {
			SessionEvents.onLoginError(error.getMessage(),SessionListenerType.FACEBOOK_SESSION_LISTENER);
		}

		public void onCancel() {
			SessionEvents.onLoginError("Action Canceled",SessionListenerType.FACEBOOK_SESSION_LISTENER);
		}

	}

	public class LogoutRequestListener extends FacebookRequestListener {
		public void onComplete(String response, final Object state) {
			// callback should be run in the original thread, 
			// not the background thread
			mHandler.post(new Runnable() {
				public void run() {
					SessionEvents.onLogoutFinish(SessionListenerType.FACEBOOK_SESSION_LISTENER);
				}
			});
		}
	}

}
