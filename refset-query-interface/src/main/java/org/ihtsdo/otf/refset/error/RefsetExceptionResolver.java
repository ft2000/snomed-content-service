package org.ihtsdo.otf.refset.error;

import java.util.Map;

import org.ihtsdo.otf.refset.common.Meta;
import org.ihtsdo.otf.refset.common.Result;
import org.ihtsdo.otf.refset.exception.EntityNotFoundException;
import org.ihtsdo.otf.refset.exception.ExportServiceException;
import org.ihtsdo.otf.refset.exception.InvalidServiceException;
import org.ihtsdo.otf.refset.exception.RefsetServiceException;
import org.ihtsdo.otf.snomed.exception.ConceptServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class RefsetExceptionResolver {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RefsetExceptionResolver.class);
	//TODO need to formalize
	private static final String ERROR_CODE_SERVER = "55011";
	private static final String ERROR_CODE_GEN = "44011";
	
	final Result<Map<String, Object>> response = new Result<Map<String, Object>>();


	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	@ExceptionHandler(EntityNotFoundException.class)
	@ResponseBody Result<Map<String, Object>> handleEntityNotFoundException(EntityNotFoundException e) {
		
		LOGGER.error("Exception details \n {}", e);
		ErrorInfo errorInfo = new ErrorInfo(e.getMessage(), Integer.toString(org.apache.commons.httpclient.HttpStatus.SC_NOT_FOUND));

		Meta m = new Meta();
		m.setStatus(HttpStatus.NOT_FOUND);
		m.setMessage(e.getMessage());
		m.setErrorInfo(errorInfo);

		response.setMeta(m);

		return response;
	} 
	
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	@ExceptionHandler(InvalidServiceException.class)
	@ResponseBody Result<Map<String, Object>> handleInvalidService(InvalidServiceException e) {
		
		LOGGER.error("Exception details \n {}", e);
		
		ErrorInfo errorInfo = new ErrorInfo(e.getMessage(), Integer.toString(org.apache.commons.httpclient.HttpStatus.SC_NOT_FOUND));
	    
		Meta m = new Meta();
		m.setStatus(HttpStatus.NOT_FOUND);
		m.setErrorInfo(errorInfo);
		response.setMeta(m);

		return response;
	} 
	
	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler(ExportServiceException.class)
	@ResponseBody Result<Map<String, Object>> handleExportException(ExportServiceException e) {
		
		LOGGER.error("Exception details \n {}", e);

		ErrorInfo errorInfo = new ErrorInfo("Error occurred during export. Try after sometime", ERROR_CODE_SERVER);
	    
		Meta m = new Meta();
		m.setStatus(HttpStatus.OK);
		m.setErrorInfo(errorInfo);
		response.setMeta(m);

		return response;

	} 
	
	
	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler
	@ResponseBody Result<Map<String, Object>> handleGlobalException(Exception e) {
		
		LOGGER.error("Exception details \n {}", e);

		ErrorInfo errorInfo = new ErrorInfo("An unknown error occurred in service call, try after sometime", ERROR_CODE_GEN);
	    
		Meta m = new Meta();
		m.setStatus(HttpStatus.OK);
		m.setErrorInfo(errorInfo);
		response.setMeta(m);

		return response;

	}
	
	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler(RefsetServiceException.class)
	@ResponseBody Result<Map<String, Object>> handleRefsetServiceException(RefsetServiceException e) {
		
		LOGGER.error("Exception details \n {}", e);

		ErrorInfo errorInfo = new ErrorInfo("An unknown error occurred in service call, try after sometime", ERROR_CODE_SERVER);
	    
		Meta m = new Meta();
		m.setStatus(HttpStatus.OK);
		m.setErrorInfo(errorInfo);
		response.setMeta(m);

		return response;

	}
	
	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler(ConceptServiceException.class)
	@ResponseBody Result<Map<String, Object>> handleConceptServiceException(ConceptServiceException e) {
		
		LOGGER.error("Exception details \n {}", e);

		ErrorInfo errorInfo = new ErrorInfo("An unknown error occurred in service call, try after sometime", ERROR_CODE_SERVER);
	    
		Meta m = new Meta();
		m.setStatus(HttpStatus.OK);
		m.setErrorInfo(errorInfo);
		response.setMeta(m);

		return response;

	}
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	@ExceptionHandler(AccessDeniedException.class)
	@ResponseBody Result<Map<String, Object>> handleAccessDeniedException(AccessDeniedException e) {
		
		LOGGER.error("Exception details \n {}", e);

		ErrorInfo errorInfo = new ErrorInfo("You are not authorized to call this service with provided credentials", Integer.toString(org.apache.commons.httpclient.HttpStatus.SC_UNAUTHORIZED));
	    
		Meta m = new Meta();
		m.setStatus(HttpStatus.UNAUTHORIZED);
		m.setErrorInfo(errorInfo);
		response.setMeta(m);

		return response;

	}

}
