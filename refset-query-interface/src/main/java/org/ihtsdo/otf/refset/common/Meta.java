package org.ihtsdo.otf.refset.common;

import org.springframework.hateoas.ResourceSupport;
import org.springframework.http.HttpStatus;

public class Meta extends ResourceSupport {
	
	private HttpStatus status;
	
	private String message;
	
	private int noOfRecords;
	

	/**
	 * @return the noOfRecords
	 */
	public int getNoOfRecords() {
		return noOfRecords;
	}

	/**
	 * @param noOfRecords the noOfRecords to set
	 */
	public void setNoOfRecords(int noOfRecords) {
		this.noOfRecords = noOfRecords;
	}

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

}
