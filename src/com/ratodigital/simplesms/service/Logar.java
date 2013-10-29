package com.ratodigital.simplesms.service;

import java.util.HashMap;

import android.os.AsyncTask;

import com.ratodigital.simplesms.HttpUtil;

public class Logar extends AsyncTask<HashMap<String, String>, Void, JSONResponse> {
 
    private static final String HTTP_SIMPLESMSERVER_REGISTER = "http://simplesmserver.appspot.com/gcm/register";

	@Override
    protected JSONResponse doInBackground(HashMap<String, String>... params) {
    	String result = HttpUtil.performPost(HTTP_SIMPLESMSERVER_REGISTER, params[0]);
    	
        return new JSONResponse(result);
    }

	
    
}