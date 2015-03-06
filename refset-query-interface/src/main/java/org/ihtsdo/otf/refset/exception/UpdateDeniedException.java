/**
 * 
 */
package org.ihtsdo.otf.refset.exception;

/**
 * Generic exception to handle unidentified service request
 *
 */
public class UpdateDeniedException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UpdateDeniedException(String message) {
		super(message);
	}
}
