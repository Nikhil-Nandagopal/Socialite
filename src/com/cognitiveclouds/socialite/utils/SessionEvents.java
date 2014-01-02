/*
 * Copyright 2010 Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cognitiveclouds.socialite.utils;

import java.util.LinkedList;

import com.cognitiveclouds.socialite.interfaces.SocialSessionListener;

public class SessionEvents {

	private static LinkedList<SocialSessionListener> socialSessionListenerList = new LinkedList<SocialSessionListener>();

	public static void addSocialSessionListener(SocialSessionListener listener) {
		socialSessionListenerList.add(listener);
	}

	public static void removeSocialSessionListener(SocialSessionListener socialSessionListener) {
		socialSessionListenerList.remove(socialSessionListener);
	}

	public static void onLoginSuccess(SessionListenerType mSessionListenerType) {
		try {
			for (SocialSessionListener socialSessionListener : socialSessionListenerList) {
				if(socialSessionListener.getType() == mSessionListenerType)
					socialSessionListener.onAuthenticationSuccess();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void onLoginError(String error, SessionListenerType mSessionListenerType) {
		try {
			for (SocialSessionListener socialSessionListener : socialSessionListenerList) {
				if(socialSessionListener.getType() == mSessionListenerType)
					socialSessionListener.onAuthenticationFailure(error);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void onLogoutBegin(SessionListenerType mSessionListenerType) {
		try {
			for (SocialSessionListener socialSessionListener : socialSessionListenerList) {
				if(socialSessionListener.getType() == mSessionListenerType)
					socialSessionListener.onLogoutBegin();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void onLogoutFinish(SessionListenerType mSessionListenerType) {
		try {
			for (SocialSessionListener socialSessionListener : socialSessionListenerList) {
				if(socialSessionListener.getType() == mSessionListenerType)
					socialSessionListener.onLogoutFinish();
			}   
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
