package com.cognitiveclouds.socialite.interfaces;

import com.cognitiveclouds.socialite.utils.SocialRequestCodes;

public interface SocialResponseListener {

   public void onComplete(String response, SocialRequestCodes requestCode);
   public void onError(String error, SocialRequestCodes requestCode);

}