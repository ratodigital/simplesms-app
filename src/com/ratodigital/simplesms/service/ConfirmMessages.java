package com.ratodigital.simplesms.service;

import java.util.HashMap;

import android.os.AsyncTask;

import com.ratodigital.simplesms.HttpUtil;

public class ConfirmMessages extends AsyncTask<HashMap<String, String>, Void, Boolean> {
 
    private static final String HTTP_SMSERVER_PULL_MSGs = "http://simplesmserver.appspot.com/gcm/messages/";

	@Override
    protected Boolean doInBackground(HashMap<String, String>... params) {
		boolean result = false;
		String response;
		try {
			String id = params[0].get("id");
			String URL = HTTP_SMSERVER_PULL_MSGs+id+"/sent";
			System.out.println(URL);
			response = HttpUtil.performGet(URL);
			result = (response.indexOf("OK")>=0);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return result;
    }
	
	
    
}