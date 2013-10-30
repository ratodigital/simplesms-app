package com.ratodigital.simplesms.service;

import java.util.HashMap;

import android.os.AsyncTask;

import com.ratodigital.simplesms.HttpUtil;

public class Logar extends AsyncTask<HashMap<String, String>, Void, JSONResponse> {
 
    private static final String HTTP_SIMPLESMSERVER_REGISTER = "http://simplesmserver.appspot.com/gcm/register";

    public static final String PARAM_EMAIL = "email";
	public static final String PARAM_PASSWORD = "password";
	public static final String PARAM_NAME = "name";
	public static final String PARAM_REGID = "regId";
    
	@Override
    protected JSONResponse doInBackground(HashMap<String, String>... params) {
    	String result = HttpUtil.performPost(HTTP_SIMPLESMSERVER_REGISTER, params[0]);
    	System.out.println(result);
        return new JSONResponse(result);
    }

	
    
}