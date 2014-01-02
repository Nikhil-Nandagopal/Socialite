package com.cognitiveclouds.socialite.asynctasks;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.WebView;

import com.cognitiveclouds.socialite.activity.WebViewActivity;
import com.cognitiveclouds.socialite.utils.SessionEvents;
import com.cognitiveclouds.socialite.utils.SessionListenerType;
import com.cognitiveclouds.socialite.utils.SocialConstants;

public class OAuthRequestTokenTask extends AsyncTask<Void, Void, String> {

	private final String TAG = getClass().getName();
	private OAuthProvider provider;
	private OAuthConsumer consumer;
	private ProgressDialog dialog;
	private WebView webView;
	private Context context;
	
	public OAuthRequestTokenTask(Context context, OAuthConsumer consumer,OAuthProvider provider, WebView webView) {
		this.consumer = consumer;
		this.provider = provider;
		this.dialog = new ProgressDialog(context);
		this.webView = webView;
	}
	
	public OAuthRequestTokenTask(Context context, OAuthConsumer consumer,OAuthProvider provider) {
		this.consumer = consumer;
		this.provider = provider;
		this.dialog = new ProgressDialog(context);
		this.context = context;
	}

	@Override
	public void onPreExecute() {
		this.dialog.setMessage("Generating Request");
		this.dialog.show();
	}
	
	@Override
	protected String doInBackground(Void... params) {
		try {
			//Log.i(TAG, "Retrieving request token from Google servers");
			final String url = provider.retrieveRequestToken(consumer, SocialConstants.TWITTER_OAUTH_CALLBACK_URL);
			//Log.i(TAG, "Popping a browser with the authorize URL : " + url);
			return url;
		} catch (Exception e) {
			//Log.e(TAG, "Error during OAUth retrieve request token", e);
			SessionEvents.onLoginError("Error", SessionListenerType.TWITTER_SESSION_LISTENER);
			return null;
		}
	}
	
	@Override
	public void onPostExecute(String url) {
		if(this.dialog.isShowing())
			this.dialog.dismiss();
		if(webView == null) {
			Intent webViewIntent = new Intent(context, WebViewActivity.class);
			webViewIntent.putExtra(SocialConstants.TWITTER_URL, url);
			context.startActivity(webViewIntent);
		} else {
			webView.loadUrl(url);
		}
	}

}