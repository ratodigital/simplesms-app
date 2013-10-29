package com.ratodigital.simplesms.service;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ratodigital.simplesms.sms.Message;


public class MessagesJSONResponse extends JSONResponse{
	
	private List<Message> messages = new ArrayList<Message>();
	
	
	public List<Message> getMessages() {
		return messages;
	}


	public MessagesJSONResponse(String jsonTxt) {
		super(jsonTxt);
		
		try {
			JSONArray arrayMessages = json.getJSONArray("messages");
			for (int i = 0; i < arrayMessages.length(); i++) {
				JSONObject m = (JSONObject) arrayMessages.get(i);
				Message message = new Message(m.getString("text"));
				JSONArray arrayNumber = m.getJSONArray("devices");
				for (int j = 0; j < arrayNumber.length(); j++) {
					message.addNumber(arrayNumber.getString(j));
				}
				messages.add(message);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			status = "ERROR";
			message = jsonTxt;
		}
		
	}

}
