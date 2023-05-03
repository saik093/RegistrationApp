package com.company.registration.response;

import java.util.ArrayList;
import java.util.List;

public class MSPResponse {
	
	private Object body;
	
	private String message;
	
	private List<String> errors = new ArrayList<>();

	public Object getBody() {
		return body;
	}

	public void setBody(Object body) {
		this.body = body;
	}

	public List<String> getErrors() {
		return errors;
	}

	public void setErrors(List<String> errors) {
		this.errors = errors;
	}
	
	public void addError(String error) {
		errors.add(error);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
