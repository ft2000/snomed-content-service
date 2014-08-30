/**
 * 
 */
package org.ihtsdo.otf.refset.controller;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;


import org.ihtsdo.otf.refset.domain.Refset;
import org.ihtsdo.otf.refset.domain.RefsetType;
import org.ihtsdo.otf.refset.exception.EntityNotFoundException;
import org.ihtsdo.otf.refset.exception.RefsetServiceException;
import org.ihtsdo.otf.refset.service.RefsetAuthoringService;
import org.ihtsdo.otf.refset.service.RefsetBrowseService;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

/**
 * @author Episteme Partners
 *
 */
public class RefsetAuthoringControllerTest {
	 private final static String REFSET =
		     "{" 
	    		 + "      \"description\": \"Junit Test\""                  
		   + "}";
	 
	 private final static String UPDATE_REFSET =
		     "{" 
	    		 + "      \"id\": \"someid\","                  

	   			 + "      \"description\": \"Junit Test\""                  
		   + "}";
	
	@Autowired
    private WebApplicationContext ctx;
    
    @Mock
	private RefsetBrowseService bService;
    
    @Mock
	private RefsetAuthoringService aService;
	
	@Mock
	private Refset refset;
	
	@InjectMocks
	private RefsetAuthoringController controller;

    private MockMvc mockMvc;
    

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		
		MockitoAnnotations.initMocks(this);

	    this.mockMvc = standaloneSetup(controller)
	    				.setMessageConverters(new MappingJackson2HttpMessageConverter()).build();
	    
		when(refset.getId()).thenReturn("Junit_1");
		when(refset.getDescription()).thenReturn("Junit Refset"); 
		when(refset.getModuleId()).thenReturn("Junit_module_1");
		when(refset.getMembers()).thenReturn(null);
		when(refset.getCreated()).thenReturn( new DateTime() );
		when(refset.getCreatedBy()).thenReturn("Junit author");
		when(refset.getType()).thenReturn(RefsetType.simple);

	    
		when(aService.addRefset(any(Refset.class))).thenReturn("1000003");
		when(aService.updateRefset(any(Refset.class))).thenReturn("1000003");

		
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link org.ihtsdo.otf.refset.controller.RefsetAuthoringController#addRefset(org.ihtsdo.otf.refset.domain.Refset)}.
	 * @throws Exception 
	 */
	@Test
	public void testAddRefset() throws Exception {
		
		this.mockMvc.perform(post("/v1.0/refsets/new").contentType(MediaType.APPLICATION_JSON).content(REFSET).accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content.id").exists());

	}
	
	/**
	 * Test method for {@link org.ihtsdo.otf.refset.controller.RefsetAuthoringController#updateRefset(org.ihtsdo.otf.refset.domain.Refset)}.
	 * @throws Exception 
	 */
	@Test
	public void testUpdateRefset() throws Exception {
		
		this.mockMvc.perform(post("/v1.0/refsets/update").contentType(MediaType.APPLICATION_JSON).content(UPDATE_REFSET).accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content.id").exists());

	}
	
	/**
	 * Test method for {@link org.ihtsdo.otf.refset.controller.RefsetAuthoringController#updateRefset(org.ihtsdo.otf.refset.domain.Refset)}.
	 * @throws Exception 
	 */
	@Test
	public void testUpdateRefsetException() throws Exception {
		
		doThrow(new RefsetServiceException("Can not add junit driven refset")).when(aService).updateRefset(any(Refset.class));

		this.mockMvc.perform(post("/v1.0/refsets/update").contentType(MediaType.APPLICATION_JSON).content(UPDATE_REFSET).accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.meta.message").value("Error occurred during service call : Can not add junit driven refset"));

	}
	
	/**
	 * Test method for {@link org.ihtsdo.otf.refset.controller.RefsetAuthoringController#updateRefset(org.ihtsdo.otf.refset.domain.Refset)}.
	 * @throws Exception 
	 */
	@Test
	public void testUpdateRefsetEntityNotFoundException() throws Exception {
		
		doThrow(new EntityNotFoundException("Can not add junit driven refset")).when(aService).updateRefset(any(Refset.class));

		this.mockMvc.perform(post("/v1.0/refsets/update").contentType(MediaType.APPLICATION_JSON).content(UPDATE_REFSET).accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content.id").doesNotExist());

	}
	
	/**
	 * Test method for {@link org.ihtsdo.otf.refset.controller.RefsetAuthoringController#addRefset(org.ihtsdo.otf.refset.domain.Refset)}.
	 * @throws Exception 
	 */
	@Test
	public void testAddRefsetException() throws Exception {
		
		doThrow(new RefsetServiceException("Can not add junit driven refset")).when(aService).addRefset(any(Refset.class));

		this.mockMvc.perform(post("/v1.0/refsets/new").contentType(MediaType.APPLICATION_JSON).content(REFSET).accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().is5xxServerError())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content.id").doesNotExist());

	}

	/**
	 * Test method for {@link org.ihtsdo.otf.refset.controller.RefsetAuthoringController#addMember(java.lang.String, org.ihtsdo.otf.refset.domain.Member)}.
	 */
	@Test
	public void testAddMember() {
		//fail("Not yet implemented"); // TODO
	}
	
}