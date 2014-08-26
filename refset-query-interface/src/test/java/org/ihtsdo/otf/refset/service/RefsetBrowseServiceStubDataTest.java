package org.ihtsdo.otf.refset.service;

import static org.junit.Assert.*;


import java.util.List;

import org.ihtsdo.otf.refset.domain.Refset;
import org.ihtsdo.otf.refset.exception.RefsetServiceException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class RefsetBrowseServiceStubDataTest {
	
	private RefsetBrowseServiceStubData data;

	@Before
	public void setUp() throws Exception {
		
		ApplicationContext ctx = new FileSystemXmlApplicationContext("src/main/webapp/WEB-INF/spring/appServlet/spring-refset-browse-service-stub-data.xml");
		data = ctx.getBean("refsetBrosweServiceStubData", RefsetBrowseServiceStubData.class);
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetRefsets() throws RefsetServiceException {
		
		List<Refset> refsets = data.getRefSets();
		
		assertNotNull(refsets);
		assertTrue(!refsets.isEmpty());
		assertEquals(59, refsets.size());
		
		
	}
	
	@Test
	public void testGetRefset() throws RefsetServiceException {
		
		Refset refset = data.getRefSet("450971017");
		
		assertNotNull(refset);
		assertEquals(300, refset.getMembers().size());
		
		
	}

}
