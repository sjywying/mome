package com.mome.core.exception;

public class FieldNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 6200297715048148015L;

	public FieldNotFoundException(String message){
		super(message);
	}
	
	public FieldNotFoundException(String message,Throwable cause){
		super(message,cause);
	}
	
	public FieldNotFoundException(Throwable cause){
		super(cause);
	}
}
