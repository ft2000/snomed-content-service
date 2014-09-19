package org.ihtsdo.otf.refset.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.ihtsdo.otf.refset.domain.Refset;
import org.ihtsdo.otf.refset.domain.RefsetType;
import org.ihtsdo.otf.refset.exception.RefsetServiceException;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class RefsetBrowseServiceStubTest {
	
	@InjectMocks
	@Resource(name = "browseServiceStub")
	private RefsetBrowseServiceStub service;
	
	@Mock
	private RefsetBrowseServiceStubData dataService;
	
	@Mock
	Refset refset;
	
	@Mock
	Map<String, org.springframework.core.io.Resource> csvs;
	
	@Mock
	List<Refset> refsets;

	@Before
	public void setUp() throws Exception {
		
		MockitoAnnotations.initMocks(this);
		
		when(refset.getId()).thenReturn("Junit_1");
		when(refset.getDescription()).thenReturn("Junit Refset"); 
		when(refset.getModuleId()).thenReturn("Junit_module_1");
		when(refset.getMembers()).thenReturn(null);
		when(refset.getCreated()).thenReturn(new DateTime());
		when(refset.getCreatedBy()).thenReturn("Junit author");
		when(refset.getType()).thenReturn(RefsetType.simple);

		dataService.setCsv(csvs);

		when(dataService.getRefSet(anyString())).thenReturn(refset);
		

		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetRefset() throws RefsetServiceException {
				
		Refset result = service.getRefset("junit-1");
		
		assertEquals(refset.getId(), result.getId());
		assertEquals(refset.getModuleId(), result.getModuleId());

	}
	
	@Test(expected = RefsetServiceException.class)
	public void testGetRefsetException() throws RefsetServiceException {
				
		doThrow(new RefsetServiceException("Junit Test Exception")).when(dataService).getRefSet(anyString());
		service.getRefset("junit-1");
		
	}
	
	@Test(expected = RefsetServiceException.class)
	public void testGetRefsetsException() throws RefsetServiceException {
				
		doThrow(new RefsetServiceException("Junit Test Exception")).when(dataService).getRefSets();
		service.getRefsets(1, 10, false);
		
	}

	@Test
	public void testGetRefsets() throws RefsetServiceException {
		
		when(refsets.isEmpty()).thenReturn(false);
		when(refsets.size()).thenReturn(1);
		when(refsets.subList(anyInt(), anyInt())).thenReturn(refsets);
		
		when(dataService.getRefSets()).thenReturn(refsets);

		List<Refset> rs = service.getRefsets(1, 10, false);
		
		assertEquals(false, rs.isEmpty());
		assertEquals(1, rs.size());


	}

}
