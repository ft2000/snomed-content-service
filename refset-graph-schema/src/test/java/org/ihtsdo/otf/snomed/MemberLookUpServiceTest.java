package org.ihtsdo.otf.snomed;


import org.ihtsdo.otf.snomed.MemberLookUpService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.sail.SailException;

public class MemberLookUpServiceTest {
	
	private MemberLookUpService service;

	@Before
	public void setUp() throws Exception {
		
		service = new MemberLookUpService();
		
		System.setProperty("org.openrdf.repository.debug", "true");
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSelect() throws SailException, MalformedQueryException, QueryEvaluationException {
		
		service.select();
	}
	
	
	@Test
	public void testConstruct() throws SailException, MalformedQueryException, QueryEvaluationException {
		
		service.construct();
	}
	
	

}
