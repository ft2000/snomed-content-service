package org.ihtsdo.otf.refset.error;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import org.ihtsdo.otf.refset.common.Meta;
import org.ihtsdo.otf.refset.common.Result;
import org.ihtsdo.otf.refset.exception.EntityNotFoundException;
import org.ihtsdo.otf.refset.exception.ExportServiceException;
import org.ihtsdo.otf.refset.exception.InvalidServiceException;
import org.ihtsdo.otf.refset.exception.RefsetServiceException;
import org.ihtsdo.otf.snomed.exception.ConceptServiceException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.context.WebApplicationContext;

public class RefsetExceptionResolverTest {

    @Autowired
    private WebApplicationContext ctx;
    
	
	@InjectMocks
	private RefsetExceptionResolver advice;

    
	@Before
	public void setUp() throws Exception {
		
		MockitoAnnotations.initMocks(this);

	}


	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testHandleEntityNotFoundException() {
		
		Result<Map<String, Object>> result = advice.handleEntityNotFoundException(new EntityNotFoundException("Junit entity not found"));
		
		assertNotNull(result);
		
		Meta m = result.getMeta();
		
		assertNotNull(m);
		ErrorInfo error = m.getErrorInfo();

		assertNotNull(error);
		assertEquals("404", error.code);
	}

	@Test
	public void testHandleInvalidService() {
		
		Result<Map<String, Object>> result = advice.handleInvalidService(new InvalidServiceException("Junit Invalid service"));
		
		assertNotNull(result);
		
		Meta m = result.getMeta();
		
		assertNotNull(m);
		ErrorInfo error = m.getErrorInfo();

		assertNotNull(error);
		assertEquals("404", error.code);
	}

	@Test
	public void testHandleExportException() {
		
		Result<Map<String, Object>> result = advice.handleExportException(new ExportServiceException("Junit export service exception"));
		
		assertNotNull(result);
		
		Meta m = result.getMeta();
		
		assertNotNull(m);
		ErrorInfo error = m.getErrorInfo();

		assertNotNull(error);
		assertEquals("55011", error.code);

	}

	@Test
	public void testHandleGlobalException() {
		
		Result<Map<String, Object>> result = advice.handleGlobalException(new Exception("Junit general exception"));
		
		assertNotNull(result);
		
		Meta m = result.getMeta();
		
		assertNotNull(m);
		ErrorInfo error = m.getErrorInfo();

		assertNotNull(error);
		assertEquals("44011", error.code);

	}

	@Test
	public void testHandleRefsetServiceException() {
		
		Result<Map<String, Object>> result = advice.handleRefsetServiceException(new RefsetServiceException("Junit refset service exception"));
		
		assertNotNull(result);
		
		Meta m = result.getMeta();
		
		assertNotNull(m);
		ErrorInfo error = m.getErrorInfo();

		assertNotNull(error);
		assertEquals("55011", error.code);
		
	}

	@Test
	public void testHandleConceptServiceException() {

		Result<Map<String, Object>> result = advice.handleConceptServiceException(new ConceptServiceException("Junit concept service exception"));
		
		assertNotNull(result);
		
		Meta m = result.getMeta();
		
		assertNotNull(m);
		ErrorInfo error = m.getErrorInfo();

		assertNotNull(error);
		assertEquals("55011", error.code);
	}

	@Test
	public void testHandleAccessDeniedException() {

		Result<Map<String, Object>> result = advice.handleAccessDeniedException(new AccessDeniedException("Junit Access denied"));
		
		assertNotNull(result);
		
		Meta m = result.getMeta();
		
		assertNotNull(m);
		ErrorInfo error = m.getErrorInfo();

		assertNotNull(error);
		assertEquals("401", error.code);
	}

}
