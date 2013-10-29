package com.ratodigital.simplesms.service;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONResponse {
	protected String status;
	protected String code;
	protected String message;
	
	protected JSONObject json;

	public JSONResponse(String jsonTxt) {
		System.out.println(jsonTxt);
		try {
			json = new JSONObject(jsonTxt);
			status = json.getString("status");
			code = json.getString("code");
			message = json.getString("message");
		} catch (JSONException e) {
			e.printStackTrace();
			status = "ERROR";
			message = jsonTxt;
		}
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
