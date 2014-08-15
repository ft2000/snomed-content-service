/**
 * 
 */
package org.ihtsdo.otf.refset.controller;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.util.Map;

import org.ihtsdo.otf.refset.common.UriFormatter;
import org.ihtsdo.otf.refset.service.RefsetQueryException;
import org.ihtsdo.otf.refset.service.RefsetQueryService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * @author Episteme Partners
 *
 */
@RunWith( PowerMockRunner.class )
@PrepareForTest( UriFormatter.class )
public class RestServiceIntegrationTest {
	
	private static final String CONCEPT_QUERY = "concept.query";
	
	private static final String EFFECTIVE_DATE_QUERY = "effective.date.query";;

	private static final String STATUS_QUERY = "status.query";;


	
	private static final String CONCEPT_ID = "106877009";
	
	private static final String MODULE_ID = "106877009";

	private static final String RELEASE_ID = "201401";
	
	private static final String CONCEPT_LABEL = "Phylum Mollusca (organism)";
	
	private static final String BASE_URI = "http://snomed.info/sct";
	
	private static final String STATUS_ID = BASE_URI + "/version/1/concept/rdfs/900000000000074008";

	private   MockMvc mockMvc;
	
	@InjectMocks
	private RestServiceController controller;

	@Mock
	private RefsetQueryService service;
	
	@Mock
	private Map<String, String> qMap;


	
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

	    this.mockMvc = standaloneSetup(controller).build();
	    when(qMap.get(CONCEPT_QUERY)).thenReturn("some sparql %s");
	    when(qMap.get(STATUS_QUERY)).thenReturn("some sparql %s");
	    when(qMap.get(EFFECTIVE_DATE_QUERY)).thenReturn("some sparql %s");
	    
	    mockStatic(UriFormatter.class, new Answer<String>() {

			@Override
			public String answer(InvocationOnMock invocation) throws Throwable {
				// TODO Auto-generated method stub
				return "Default URI";
			}
		});
	    when(UriFormatter.getNamedGraphUri(anyString(), anyString())).thenReturn("some string");
	    when(UriFormatter.getConceptUri(anyString())).thenReturn("some string");

	}
	
	@Test
	public void testGetItemDetails() throws Exception {

		when(service.executeQuery(anyString(), anyString())).thenReturn(RestData.getConceptDetails());

		this.mockMvc.perform(
	            get("/module/{moduleid}/release/{releaseid}/{sctid}",  MODULE_ID, RELEASE_ID, CONCEPT_ID)
	                    .accept(MediaType.APPLICATION_JSON))
	            .andDo(print())
	            .andExpect(status().isOk());
	}
	
	@Test
	public void testGetItemDetailsNotFound() throws Exception {

		doThrow(new RefsetQueryException()).when(service).executeQuery(anyString(), anyString());

		this.mockMvc.perform(
	            get("/module/{moduleid}/release/{releaseid}/{sctid}",  MODULE_ID, RELEASE_ID, CONCEPT_ID)
	                    .accept(MediaType.APPLICATION_JSON))
	            .andDo(print())
	            .andExpect(status().is5xxServerError());
	}
	
	@Test
	public void testGetItemDetailsVerifyJson() throws Exception {

		when(service.executeQuery(anyString(), anyString())).thenReturn(RestData.getConceptDetails());

		 this.mockMvc.perform(
		            get("/module/{moduleid}/release/{releaseid}/{sctid}",  MODULE_ID, RELEASE_ID, CONCEPT_ID)
		                    .accept(MediaType.APPLICATION_JSON))
		            .andExpect(jsonPath("$.@graph[0].label.@value").value(CONCEPT_LABEL));
	}
	
	
	@Test
	public void testGetStatus() throws Exception {

		when(service.executeQuery(anyString(), anyString())).thenReturn(RestData.getStatus());

		this.mockMvc.perform(
	            get("/module/{moduleid}/release/{releaseid}/{sctid}/status",  MODULE_ID, RELEASE_ID, CONCEPT_ID)
	                    .accept(MediaType.APPLICATION_JSON))
	            .andDo(print())
	            .andExpect(status().isOk());
	}
	
	@Test
	public void testGetStatusNotFound() throws Exception {

		doThrow(new RefsetQueryException()).when(service).executeQuery(anyString(), anyString());

		this.mockMvc.perform(
	            get("/module/{moduleid}/release/{releaseid}/{sctid}/status",  MODULE_ID, RELEASE_ID, CONCEPT_ID)
	                    .accept(MediaType.APPLICATION_JSON))
	            .andDo(print())
	            .andExpect(status().is5xxServerError());
	}
	
	@Test
	public void testGetStatusVerifyJson() throws Exception {

		when(service.executeQuery(anyString(), anyString())).thenReturn(RestData.getStatus());

		 this.mockMvc.perform(
		            get("/module/{moduleid}/release/{releaseid}/{sctid}/status",  MODULE_ID, RELEASE_ID, CONCEPT_ID)
		                    .accept(MediaType.APPLICATION_JSON))
		            .andExpect(jsonPath("$.@graph[1].status").value(STATUS_ID));
	}
	
	@Test
	public void testGetEffectiveDateOK() throws Exception {

		when(service.executeQuery(anyString(), anyString())).thenReturn(RestData.getEffectiveDate());

		this.mockMvc.perform(
	            get("/module/{moduleid}/release/{releaseid}/{sctid}/status",  MODULE_ID, RELEASE_ID, CONCEPT_ID)
	                    .accept(MediaType.APPLICATION_JSON))
	            .andDo(print())
	            .andExpect(status().isOk());
	}
	
	@Test
	public void testGetEffectivDate500() throws Exception {

		doThrow(new RefsetQueryException()).when(service).executeQuery(anyString(), anyString());

		this.mockMvc.perform(
	            get("/module/{moduleid}/release/{releaseid}/{sctid}/effectivedate",  MODULE_ID, RELEASE_ID, CONCEPT_ID)
	                    .accept(MediaType.APPLICATION_JSON))
	            .andDo(print())
	            .andExpect(status().is5xxServerError());
	}
	
	@Test
	public void testGetEffectiveDateVerifyJson() throws Exception {

		when(service.executeQuery(anyString(), anyString())).thenReturn(RestData.getEffectiveDate());

		 this.mockMvc.perform(
		            get("/module/{moduleid}/release/{releaseid}/{sctid}/effectivedate",  MODULE_ID, RELEASE_ID, CONCEPT_ID)
		                    .accept(MediaType.APPLICATION_JSON))
		            .andExpect(jsonPath("$.results.bindings[0].effectiveTime.value").value("2002-01-31"));
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

}
