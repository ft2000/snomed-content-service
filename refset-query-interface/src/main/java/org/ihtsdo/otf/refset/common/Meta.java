package org.ihtsdo.otf.refset.common;

import org.ihtsdo.otf.refset.error.ErrorInfo;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.http.HttpStatus;

public class Meta extends ResourceSupport {
	
	private HttpStatus status;
	
	private String message;
		
	private ErrorInfo errorInfo;

	/**
	 * @return the status
	 */
	public HttpStatus getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(HttpStatus status) {
		this.status = status;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the errorInfo
	 */
	public ErrorInfo getErrorInfo() {
		return errorInfo;
	}

	/**
	 * @param errorInfo the errorInfo to set
	 */
	public void setErrorInfo(ErrorInfo errorInfo) {
		this.errorInfo = errorInfo;
	}

}
