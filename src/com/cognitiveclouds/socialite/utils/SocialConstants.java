package com.cognitiveclouds.socialite.utils;

public class SocialConstants {

	public static final String TWITTER_URL = "twitter_url";
	public static final String TWITTER_CONSUMER_KEY = "Consumer";
	public static final String TWITTER_PROVIDER_KEY = "Provider";
	public static final String TWITTER_CONSUMERKEY_KEY = "twitter_consumer_key";
	public static final String TWITTER_CONSUMERSECRET_KEY = "twitter_consumer_secret";
	public static final String TWITTER_REQUEST_URL = "http://api.twitter.com/oauth/request_token";
	public static final String TWITTER_ACCESS_URL = "http://api.twitter.com/oauth/access_token";
	public static final String TWITTER_AUTHORIZE_URL = "http://api.twitter.com/oauth/authorize";
	public static final String TWITTER_OAUTH_CALLBACK_SCHEME = "x-oauthflow-twitter";
	public static final String TWITTER_OAUTH_CALLBACK_HOST  = "callback";
	public static final String TWITTER_OAUTH_CALLBACK_URL  = TWITTER_OAUTH_CALLBACK_SCHEME + "://" + TWITTER_OAUTH_CALLBACK_HOST;
	
	public static final String FACEBOOK_PUBLISH_PERMISSION = "publish_stream";
	public static final String FACEBOOK_MESSAGE_KEY = "message";
	public static final String FACEBOOK_MYFEED_GRAPHPATH = "me/feed";
	
	public static final String POST = "post";
	
}
