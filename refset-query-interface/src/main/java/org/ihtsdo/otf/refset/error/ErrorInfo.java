package org.ihtsdo.otf.refset.error;

import java.util.List;
import java.util.Map;

import org.springframework.validation.FieldError;

/**
 * Class to create a unified error info object
 * @author Episteme Partners
 *
 */
public class ErrorInfo {
	
	public final String message;
	public final String code;
	public final String details;
	public Map<Object, List<ErrorInfo>> failures;
	
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
	
	public ErrorInfo(String message, String code, Map<Object, List<ErrorInfo>> validationErrors) {
		
        this.message = message;
        this.code = code;
        this.details = "";
        this.failures = validationErrors;
    }

}
