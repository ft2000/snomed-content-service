/**
 * 
 */
package org.ihtsdo.otf.refset.exception;

/**
 * Generic exception to handle unidentified service request
 *
 */
public class InvalidServiceException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidServiceException(String message) {
		super(message);
	}
}
