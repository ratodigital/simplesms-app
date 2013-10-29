package com.ratodigital.simplesms.sms;

import java.util.ArrayList;
import java.util.List;

public class Message {

	private String message;
	
	private List<String> phoneNumbers;
	
	public Message(String msg) {
		this(msg,new ArrayList<String>());
	}
	
	public Message(String msg, List<String> numbers) {
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
	
	@Override
	public String toString() {
		StringBuffer s = new StringBuffer();
		s.append("Message: ").append(this.message);
		s.append("Numbers: ").append(this.phoneNumbers.toString());
		return s.toString();
	}
	
}
