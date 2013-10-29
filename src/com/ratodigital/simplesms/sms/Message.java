package com.ratodigital.simplesms.sms;

import java.util.ArrayList;
import java.util.List;

public class Message {

	private String message;
	
	private List<String> phoneNumbers;

	private String id;
	
	public Message(String id,String msg) {
		this(id,msg,new ArrayList<String>());
	}
	
	public Message(String id,String msg, List<String> numbers) {
		this.id = id;
		this.message = msg;
		this.phoneNumbers = numbers;
	}
	
	public void addNumber(String number){
		if(phoneNumbers==null) phoneNumbers = new ArrayList<String>();
		phoneNumbers.add(number);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<String> getPhoneNumbers() {
		return phoneNumbers;
	}

	public void setPhoneNumbers(List<String> phoneNumbers) {
		this.phoneNumbers = phoneNumbers;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		StringBuffer s = new StringBuffer();
		s.append("ID: ").append(this.id);
		s.append("Message: ").append(this.message);
		s.append("Numbers: ").append(this.phoneNumbers.toString());
		return s.toString();
	}
	
}
