package com.ratodigital.simplesms.sms;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import android.telephony.SmsManager;

import com.ratodigital.simplesms.service.MessagesJSONResponse;
import com.ratodigital.simplesms.service.PullMessages;

public class SMSManager {
	
	
	@SuppressWarnings("unchecked")
	public void pushMessages(){
		HashMap<String, String> params = new HashMap<String, String>();
		MessagesJSONResponse messages;
		SmsManager sms = SmsManager.getDefault();
		try {
			messages = new PullMessages().execute(params).get();
			for (Message msg : messages.getMessages()) {
				for (String number : msg.getPhoneNumbers()) {
					sms.sendTextMessage(number, null, msg.getMessage(), null, null);
					System.out.println("Enviou msg para: "+number);
				}
				System.out.println(msg.toString());
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

}
