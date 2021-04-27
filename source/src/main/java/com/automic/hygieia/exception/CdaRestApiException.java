package com.automic.hygieia.exception;

import lombok.Getter;
import lombok.Setter;

public class CdaRestApiException extends RuntimeException {

	private static final long serialVersionUID = 6876376223081050970L;

	@Getter
	private ApiExceptionContent exception;

	public CdaRestApiException(ApiExceptionContent exception) {
		super(exception.toString());
		this.exception = exception;
	}

	public static class ApiExceptionContent {

		@Getter
		@Setter
		private int code;

		@Getter
		@Setter
		private String error;

		@Getter
		@Setter
		private Object details;

		@Override
		public String toString() {
			String message = String.format("Error (%s): %s", String.valueOf(getCode()), getError());

			if (details != null && !details.equals("")) {
				message += System.lineSeparator();
				message += "Details: " + details;
			}
			return message;
		}
	}
}