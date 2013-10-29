package com.ratodigital.simplesms.service;

import java.util.HashMap;

import android.os.AsyncTask;

import com.ratodigital.simplesms.HttpUtil;

public class PullMessages extends AsyncTask<HashMap<String, String>, Void, MessagesJSONResponse> {
 
    private static final String HTTP_SIMPLESMSERVER_PULL_MSGs = "http://simplesmserver.appspot.com/gcm/messages";

	@Override
    protected MessagesJSONResponse doInBackground(HashMap<String, String>... params) {
		String id = params[0].get("id");
		String URL = HTTP_SIMPLESMSERVER_PULL_MSGs+"/"+id;
		System.out.println(URL);
    	String response = HttpUtil.performGet(URL);
        return new MessagesJSONResponse(response);
    }
	
	
    
}