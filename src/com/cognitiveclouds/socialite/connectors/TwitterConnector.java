package com.cognitiveclouds.socialite.connectors;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import twitter4j.AsyncTwitter;
import twitter4j.AsyncTwitterFactory;
import twitter4j.http.AccessToken;
import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.cognitiveclouds.socialite.activity.SocialActivity;
import com.cognitiveclouds.socialite.activity.SocialSliderActivity;
import com.cognitiveclouds.socialite.asynctasks.OAuthRequestTokenTask;
import com.cognitiveclouds.socialite.asynctasks.RetrieveAccessTokenTask;
import com.cognitiveclouds.socialite.interfaces.SocialConnector;
import com.cognitiveclouds.socialite.interfaces.SocialSessionListener;
import com.cognitiveclouds.socialite.utils.SessionEvents;
import com.cognitiveclouds.socialite.utils.SessionListenerType;
import com.cognitiveclouds.socialite.utils.SharedPreferencesCredentialStore;
import com.cognitiveclouds.socialite.utils.SocialConnectorType;
import com.cognitiveclouds.socialite.utils.SocialConstants;

public class TwitterConnector implements SocialConnector {

	private final String TAG = getClass().getName();
	private String twitterConsumerKey;
	private String twitterConsumerSecret;
	private static OAuthConsumer consumer;
	private static OAuthProvider provider;
	private WebView webView;
	private int noPagesStarted = 0;
	private int noPagesFinished = 0;
	private Activity socialActivity;

	public TwitterConnector(String twitterConsumerKey, String twitterConsumerSecret, SocialActivity socialActivity, WebView webView) {
		this.twitterConsumerKey = twitterConsumerKey;
		this.twitterConsumerSecret = twitterConsumerSecret;
		this.socialActivity = socialActivity;
		this.webView = webView;
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebViewClient(twitterWebViewClient);
	}

	public TwitterConnector(String twitterConsumerKey, String twitterConsumerSecret, SocialActivity socialActivity) {
		this.twitterConsumerKey = twitterConsumerKey;
		this.twitterConsumerSecret = twitterConsumerSecret;
		this.socialActivity = socialActivity;
	}
	
	public TwitterConnector(String twitterConsumerKey, String twitterConsumerSecret, SocialSliderActivity socialActivity, WebView webView) {
		this.twitterConsumerKey = twitterConsumerKey;
		this.twitterConsumerSecret = twitterConsumerSecret;
		this.socialActivity = socialActivity;
		this.webView = webView;
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebViewClient(twitterWebViewClient);
	}

	public TwitterConnector(String twitterConsumerKey, String twitterConsumerSecret, SocialSliderActivity socialActivity) {
		this.twitterConsumerKey = twitterConsumerKey;
		this.twitterConsumerSecret = twitterConsumerSecret;
		this.socialActivity = socialActivity;
	}
	
	public static OAuthConsumer getOAuthConsumer() {
		return consumer;
	}
	
	public static OAuthProvider getOAuthProvider() {
		return provider;
	}
	
	@Override
	public void authenticate() {
		prepareRequestToken();
		if(webView == null) {
			new OAuthRequestTokenTask(socialActivity, consumer, provider).execute();
		} else {
			new OAuthRequestTokenTask(socialActivity, consumer, provider, webView).execute();
		}
	}

	@Override
	public boolean isAuthenticated() {
		String [] tokens = getAccessToken();
		if(tokens[0].length() > 0 && tokens[1].length() >1)
			return true;
		else
			return false;
	}

	@Override
	public void postMessage(String message) {		
		String[] tokens = getAccessToken();
		AccessToken a = new AccessToken(tokens[0],tokens[1]);
		AsyncTwitter twitter = new AsyncTwitterFactory().getInstance();
		twitter.setOAuthConsumer(twitterConsumerKey, twitterConsumerSecret);
		twitter.setOAuthAccessToken(a);
		twitter.updateStatus(message);
	}	

	@Override
	public void logout() {
		SessionEvents.onLogoutBegin(SessionListenerType.TWITTER_SESSION_LISTENER);
		SessionEvents.onLogoutFinish(SessionListenerType.TWITTER_SESSION_LISTENER);
	}

	@Override
	public String[] getAccessToken() {
		return SharedPreferencesCredentialStore.getTwitterTokens(socialActivity.getApplicationContext());
	}

	@Override
	public SocialConnectorType getType() {
		return SocialConnectorType.TWITTER_CONNECTOR;
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

	WebViewClient twitterWebViewClient = new WebViewClient() {

		@Override
		public void onPageStarted(WebView view, String url,Bitmap bitmap) {
			super.onPageStarted(view, url, bitmap);
			//Log.i(TAG, "On Page Started : " + url);
			noPagesStarted++;
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			return false;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			//Log.i(TAG, "On Page Finished : " + url);
			noPagesFinished++;

			if(noPagesStarted == 0)
				return;

			if(noPagesStarted != noPagesFinished) {
				noPagesStarted = 0;
				noPagesFinished = 0;
			}

			if (url.startsWith(SocialConstants.TWITTER_OAUTH_CALLBACK_URL)) {
				try {
					if (url.indexOf("oauth_token=") != -1) {
						goBack(url);
					}
					else if (url.indexOf("error=") != -1) {
						SessionEvents.onLoginError("Error", SessionListenerType.TWITTER_SESSION_LISTENER);
						webView.loadUrl("about:blank");
						return;

					} else if(url.indexOf("denied") != -1) {
						SessionEvents.onLoginError("Error", SessionListenerType.TWITTER_SESSION_LISTENER);
						webView.loadUrl("about:blank");
						return;
					}
				} catch (Exception e) {
					e.printStackTrace();
					SessionEvents.onLoginError("Error", SessionListenerType.TWITTER_SESSION_LISTENER);
					webView.loadUrl("about:blank");
				}
			}
		}
	};

	public void prepareRequestToken() {
		try {		
			consumer = new CommonsHttpOAuthConsumer(twitterConsumerKey, twitterConsumerSecret);
			provider = new CommonsHttpOAuthProvider(SocialConstants.TWITTER_REQUEST_URL,SocialConstants.TWITTER_ACCESS_URL,SocialConstants.TWITTER_AUTHORIZE_URL);
		} catch (Exception e) {
			//Log.e(TAG, "Error creating consumer / provider",e);
			SessionEvents.onLoginError("Error", SessionListenerType.TWITTER_SESSION_LISTENER);
		}
		//Log.i(TAG, "Starting task to retrieve request token.");
		provider.setOAuth10a(true);
	}

	private void goBack(String url){
		final Uri uri = Uri.parse(url);
		if (uri != null && uri.getScheme().equals(SocialConstants.TWITTER_OAUTH_CALLBACK_SCHEME)) {
			webView.loadUrl("about:blank");
			new RetrieveAccessTokenTask(socialActivity, consumer, provider).execute(uri);
		}
	}

}
