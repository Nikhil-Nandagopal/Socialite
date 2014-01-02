package com.cognitiveclouds.socialite.utils;

import oauth.signpost.OAuth;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.facebook.android.Facebook;

public class SharedPreferencesCredentialStore {

	private static final String FACEBOOK_TOKEN = "access_token";
	private static final String TWITTER_TOKEN = OAuth.OAUTH_TOKEN;
	private static final String TWITTER_TOKEN_SECRET = OAuth.OAUTH_TOKEN_SECRET;
	private static final String EXPIRES = "expires_in";
	private static final String FACEBOOK_KEY = "facebook-session";
	private static final String TWITTER_KEY = "twitter-session";

	public static String[] getTwitterTokens(Context context) {
		
		SharedPreferences savedSession = context.getSharedPreferences(TWITTER_KEY, Context.MODE_PRIVATE);
		String[] tokens = new String[2];
		tokens[0] = savedSession.getString(TWITTER_TOKEN, "");
		tokens[1]=savedSession.getString(TWITTER_TOKEN_SECRET, "");
		return tokens;
	}

	public static boolean saveTwitterTokens(String[] tokens, Context context) {
		Editor editor = context.getSharedPreferences(TWITTER_KEY, Context.MODE_PRIVATE).edit();
		editor.putString(TWITTER_TOKEN,tokens[0]);
		editor.putString(TWITTER_TOKEN_SECRET,tokens[1]);
		return editor.commit();
	}

	public static void clearTwitterCredentials(Context context) {
		Editor editor = context.getSharedPreferences(TWITTER_KEY, Context.MODE_PRIVATE).edit();
		editor.remove(TWITTER_TOKEN);
		editor.remove(TWITTER_TOKEN_SECRET);
		editor.commit();
	}

	public static boolean restoreFacebookSession(Facebook facebook, Context context) {	
		SharedPreferences savedSession = context.getSharedPreferences(FACEBOOK_KEY, Context.MODE_PRIVATE);
		facebook.setAccessToken(savedSession.getString(FACEBOOK_TOKEN, null));
		facebook.setAccessExpires(savedSession.getLong(EXPIRES, 0));
		return facebook.isSessionValid();
	}

	public static boolean saveFacebookToken(Facebook session, Context context) {
		Editor editor =	context.getSharedPreferences(FACEBOOK_KEY, Context.MODE_PRIVATE).edit();
		editor.putString(FACEBOOK_TOKEN, session.getAccessToken());
		editor.putLong(EXPIRES, session.getAccessExpires());
		return editor.commit();
	}

	public static void clearFacebookCredentials(Facebook facebook, Context context) {	
		if(facebook.isSessionValid())
			facebook.getSession().close();
		Editor editor = context.getSharedPreferences(FACEBOOK_KEY, Context.MODE_PRIVATE).edit();
		editor.remove(FACEBOOK_TOKEN);
		editor.commit();
	}

}

