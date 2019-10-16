package com.automic.hygieia.exception;

public class CollectorException extends RuntimeException {

	private static final long serialVersionUID = 1097999791074098491L;

	public CollectorException(String message) {
		super(message);
	}

	public CollectorException(String message, Throwable throwable) {
		super(message, throwable);
	}

	public CollectorException(Throwable throwable) {
		super(throwable);
	}
}
