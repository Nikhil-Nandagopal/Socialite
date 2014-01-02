package com.cognitiveclouds.socialite.asynctasks;
import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.cognitiveclouds.socialite.utils.SessionEvents;
import com.cognitiveclouds.socialite.utils.SessionListenerType;
import com.cognitiveclouds.socialite.utils.SharedPreferencesCredentialStore;

public class RetrieveAccessTokenTask extends AsyncTask<Uri, Void, Void> {
	
	private final String TAG = getClass().getName();
	private Context context;
	private OAuthProvider provider;
	private OAuthConsumer consumer;

	public RetrieveAccessTokenTask(Context context, OAuthConsumer consumer, OAuthProvider provider) {
		this.context = context;
		this.consumer = consumer;
		this.provider = provider;
	}

	/**
	 * Retrieve the oauth_verifier, and store the oauth and oauth_token_secret 
	 * for future API calls.
	 */
	@Override
	protected Void doInBackground(Uri...params) {
		final Uri uri = params[0];
		final String oauth_verifier = uri.getQueryParameter(OAuth.OAUTH_VERIFIER);
		try {
			provider.retrieveAccessToken(consumer, oauth_verifier);
			String [] tokens = new String [] {consumer.getToken(), consumer.getTokenSecret()};
			SharedPreferencesCredentialStore.saveTwitterTokens(tokens, context);
			String token = tokens[0];
			String secret = tokens[1];
			consumer.setTokenWithSecret(token, secret);
			//Log.i(TAG, "OAuth - Access Token Retrieved");
			SessionEvents.onLoginSuccess(SessionListenerType.TWITTER_SESSION_LISTENER);
		} catch (Exception e) {
			//Log.e(TAG, "OAuth - Access Token Retrieval Error", e);
			SessionEvents.onLoginError("Error", SessionListenerType.TWITTER_SESSION_LISTENER);
		}
		return null;
	}

} 