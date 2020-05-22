package com.thedeveloperfriend.asterisk.ivrdemo;

//This is a custom exception class created to handle scenarios where a user exceeds the maximum number of retry counts when providing some inputs 
public class UserReTryCountExceededException extends Exception {

	private String errorMessage;

	public UserReTryCountExceededException(String errorMessage) {
		super(errorMessage);
		this.errorMessage = errorMessage;
	}

	@Override
	public String toString() {
		return errorMessage;
	}
}
