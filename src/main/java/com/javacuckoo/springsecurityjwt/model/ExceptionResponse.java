package com.javacuckoo.springsecurityjwt.model;

import java.time.LocalDateTime;

public class ExceptionResponse {

	private LocalDateTime dateTime;
	private String[] messages;

	public ExceptionResponse(LocalDateTime dateTime, String[] messages) {
		super();
		this.dateTime = dateTime;
		this.messages = messages;
	}
	
	public ExceptionResponse() {
	}


	public LocalDateTime getDateTime() {
		return dateTime;
	}

	public void setDateTime(LocalDateTime dateTime) {
		this.dateTime = dateTime;
	}

	public String[] getMessages() {
		return messages;
	}

	public void setMessages(String[] messages) {
		this.messages = messages;
	}

}
