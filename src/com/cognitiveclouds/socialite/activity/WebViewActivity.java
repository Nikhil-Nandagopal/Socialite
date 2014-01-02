package com.cognitiveclouds.socialite.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.cognitiveclouds.socialite.asynctasks.RetrieveAccessTokenTask;
import com.cognitiveclouds.socialite.connectors.TwitterConnector;
import com.cognitiveclouds.socialite.utils.SessionEvents;
import com.cognitiveclouds.socialite.utils.SessionListenerType;
import com.cognitiveclouds.socialite.utils.SocialConstants;

public class WebViewActivity extends Activity {

	private final String TAG = getClass().getName();
	private String url;
	private int noPagesStarted = 0;
	private int noPagesFinished = 0;
	/* 
	 * WebViewClient must be set BEFORE calling loadUrl! 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();
		if(extras != null)
			url = extras.getString(SocialConstants.TWITTER_URL);
		WebView webview = new WebView(this);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.setVisibility(View.VISIBLE);
		setContentView(webview);
		webview.setWebViewClient(twitterWebViewClient);
		webview.loadUrl(url);
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
						finish();
						return;

					} else if(url.indexOf("denied") != -1) {
						SessionEvents.onLoginError("Error", SessionListenerType.TWITTER_SESSION_LISTENER);
						finish();
						return;
					}
				} catch (Exception e) {
					e.printStackTrace();
					SessionEvents.onLoginError("Error", SessionListenerType.TWITTER_SESSION_LISTENER);
					finish();
				}
			}
		}
	};
	
	private void goBack(String url){
		final Uri uri = Uri.parse(url);
		if (uri != null && uri.getScheme().equals(SocialConstants.TWITTER_OAUTH_CALLBACK_SCHEME)) {
			new RetrieveAccessTokenTask(this, TwitterConnector.getOAuthConsumer(), TwitterConnector.getOAuthProvider()).execute(uri);
			finish();
		}
	}
}