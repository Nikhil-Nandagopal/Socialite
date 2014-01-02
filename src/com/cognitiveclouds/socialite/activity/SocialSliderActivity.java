package com.cognitiveclouds.socialite.activity;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.cognitiveclouds.socialite.R;
import com.cognitiveclouds.socialite.connectors.FacebookConnector;
import com.cognitiveclouds.socialite.interfaces.SocialConnector;
import com.cognitiveclouds.socialite.interfaces.SocialResponseListener;
import com.cognitiveclouds.socialite.utils.SocialConnectorType;
import com.cognitiveclouds.socialite.utils.SocialRequestCodes;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

public class SocialSliderActivity extends SlidingFragmentActivity implements SocialResponseListener {

	private final String TAG = getClass().getName();
	private List<SocialConnector> facebookConnectorList = new ArrayList<SocialConnector>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		for(SocialConnector facebookConnector : facebookConnectorList) {
			((FacebookConnector) facebookConnector).getFacebook().authorizeCallback(requestCode, resultCode, data);
		}
	}

	public void registerSocialConnector(SocialConnector socialConnector) {
		if(socialConnector.getType() == SocialConnectorType.FACEBOOK_CONNECTOR) {
			facebookConnectorList.add(socialConnector);
		}
	}

	@Override
	public void onComplete(String response, SocialRequestCodes requestCode) {
		//Log.i(TAG, response);
	}

	@Override
	public void onError(String error, SocialRequestCodes requestCode) {
	}
	
}
