/**
 * 
 */
package org.ihtsdo.otf.refset.controller;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.ihtsdo.otf.refset.api.browse.RefsetBrowseController;
import org.ihtsdo.otf.refset.domain.Refset;
import org.ihtsdo.otf.refset.exception.RefsetServiceException;
import org.ihtsdo.otf.refset.service.RefsetBrowseService;
import org.ihtsdo.otf.refset.service.RefsetBrowseServiceStubData;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

/**
 * @author Episteme Partners
 *
 */
public class RefsetBrowseControllerTest {
	
    @Autowired
    private WebApplicationContext ctx;
    
    @Mock
	private RefsetBrowseService service;
	
	private List<Refset> refsets;
	
	@Mock
	private Refset refset;
	
	@InjectMocks
	private RefsetBrowseController controller;

    private MockMvc mockMvc;
    
    private ApplicationContext ctxa;
    
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		
		MockitoAnnotations.initMocks(this);

	    this.mockMvc = standaloneSetup(controller).build();
	    
	    
	    ctxa = new FileSystemXmlApplicationContext("src/main/webapp/WEB-INF/spring/appServlet/spring-refset-browse-service-stub-data.xml");
	    RefsetBrowseServiceStubData data = ctxa.getBean("refsetBrosweServiceStubData", RefsetBrowseServiceStubData.class);
	    refsets = data.getRefSets();
		when(service.getRefsets(1, 10, false)).thenReturn(refsets.subList(0, 10));
		
		when(refset.getUuid()).thenReturn("Junit_1");
		when(refset.getDescription()).thenReturn("Junit Refset"); 
		when(refset.getModuleId()).thenReturn("Junit_module_1");
		when(refset.getMembers()).thenReturn(null);
		when(refset.getCreated()).thenReturn(new DateTime());
		when(refset.getCreatedBy()).thenReturn("Junit author");
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		
		service = null;
		mockMvc = null;
		controller = null; 
		
	}

	/**
	 * Test method for {@link org.ihtsdo.otf.refset.api.browse.RefsetBrowseController#getRefsets(int, int)}.
	 * @throws Exception 
	 */
	@Test
	public void testGetRefsets() throws Exception {
		
		this.mockMvc.perform(get("/v1/refsets").accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content.refsets[0].moduleId").value("900000000000012004"));
	}
	
	/**
	 * Test method for {@link org.ihtsdo.otf.refset.api.browse.RefsetBrowseController#getRefsets(int, int)}.
	 * @throws Exception 
	 */
	@Test(expected = Exception.class)
	public void testGetRefsetsError() throws Exception {
		
		doThrow(new RefsetServiceException()).when(service).getRefsets(anyInt(), anyInt(), anyBoolean());

		
		this.mockMvc.perform(get("/v1/refsets").accept(MediaType.APPLICATION_JSON))
        .andDo(print());
	}
	
	/**
	 * Test method for {@link org.ihtsdo.otf.refset.api.browse.RefsetBrowseController#getRefsets(int, int)}.
	 * @throws Exception 
	 */
	@Test(expected = Exception.class)
	public void testGetRefsetsMetaDataOnError() throws Exception {
		
		doThrow(new RefsetServiceException("Junit Error Checking")).when(service).getRefsets(anyInt(), anyInt(), anyBoolean());

		
		this.mockMvc.perform(get("/v1/refsets").accept(MediaType.APPLICATION_JSON))
        .andDo(print());
		
	}
	
	/**
	 * Test method for {@link org.ihtsdo.otf.refset.api.browse.RefsetBrowseController#getRefsets(int, int)}.
	 * @throws Exception 
	 */
	@Test
	public void testGetRefsetsMetaData() throws Exception {
		
		this.mockMvc.perform(get("/v1/refsets").accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.meta.message").value("Success"))
        .andExpect(jsonPath("$.meta.status").value("OK"))
        .andExpect(jsonPath("$.meta.links[0].rel").value("self"));

	}

	/**
	 * Test method for {@link org.ihtsdo.otf.refset.api.browse.RefsetBrowseController#getRefsetDetails(java.lang.String)}.
	 * @throws Exception 
	 */
	@Test(expected = Exception.class)
	public void testGetRefsetDetailsError() throws Exception {
		
	    doThrow(new RefsetServiceException("junit Error Checking")).when(service).getRefset(anyString());

		this.mockMvc.perform(
				get("/v1/refsets/{refSetId}", "0")
				.accept(MediaType.APPLICATION_JSON))
        .andDo(print());

	}
	

}