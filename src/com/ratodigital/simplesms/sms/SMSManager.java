package com.ratodigital.simplesms.sms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import android.content.Context;
import android.telephony.SmsManager;

import com.ratodigital.simplesms.SimpleSMSLogger;
import com.ratodigital.simplesms.service.ConfirmMessages;
import com.ratodigital.simplesms.service.MessagesJSONResponse;
import com.ratodigital.simplesms.service.PullMessages;

public class SMSManager {
	
	
	@SuppressWarnings("unchecked")
	public List<String> pushMessages(Context context, String messageId){
		List<String> sentMessages = new ArrayList<String>();
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("id", messageId);
		 
		MessagesJSONResponse messages;
		SmsManager sms = SmsManager.getDefault();
		try {
			messages = new PullMessages().execute(params).get();
			for (Message msg : messages.getMessages()) {
				for (String number : msg.getPhoneNumbers()) {
					//sms.sendTextMessage(number, null, msg.getMessage(), null, null);
					SimpleSMSLogger.log(context, "\n Enviou msg para: "+number+ " -> "+msg.getMessage());
				}
				sentMessages.add(msg.getId());
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return sentMessages;
	}

}
