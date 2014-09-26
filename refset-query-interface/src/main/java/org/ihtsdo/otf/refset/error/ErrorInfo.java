package org.ihtsdo.otf.refset.error;

/**
 * Class to create a unified error info object
 * @author Episteme Partners
 *
 */
public class ErrorInfo {
	
	public final String message;
	public final String code;
	public final String details;
	
	public ErrorInfo(String message, String code, String details) {
		
        this.message = message;
        this.code = code;
        this.details = details;
    }
	
	public ErrorInfo(String message, String code) {
		
        this.message = message;
        this.code = code;
        this.details = "";
    }

}
