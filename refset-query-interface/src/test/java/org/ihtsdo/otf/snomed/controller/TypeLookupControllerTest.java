package org.ihtsdo.otf.snomed.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

public class TypeLookupControllerTest {
	
    @Autowired
    private WebApplicationContext ctx;
    
	
	@InjectMocks
	private TypeLookupController controller;

    private MockMvc mockMvc;
    
	@Before
	public void setUp() throws Exception {
		
		MockitoAnnotations.initMocks(this);

	    this.mockMvc = standaloneSetup(controller).build();

	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetComponentTypes() throws Exception {

		
		this.mockMvc.perform(
				get("/v1.0/snomed/componentTypes")
				.accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.meta.message").value("Success"))
        .andExpect(jsonPath("$.meta.status").value("OK"))
        .andExpect(jsonPath("$.content.componentTypes.900000000000464001").value("Reference set member type component (foundation metadata concept)"))
        .andExpect(jsonPath("$.content.componentTypes.900000000000462002").value("Description type component (foundation metadata concept)"))
        .andExpect(jsonPath("$.content.componentTypes.900000000000463007").value("Relationship type component (foundation metadata concept)"))
        .andExpect(jsonPath("$.content.componentTypes.900000000000461009").value("Concept type component (foundation metadata concept)"));
	
	}

	/**types.put("Annotation type reference set (foundation metadata concept)", "900000000000516008");
			types.put("Association type reference set (foundation metadata concept)",  "900000000000521006");
			types.put("Attribute value type reference set (foundation metadata concept)",  "900000000000480006");
			types.put("Complex map type reference set (foundation metadata concept)",  "447250001");
			types.put("Concept model reference set (foundation metadata concept)",  "609430003");
			types.put("Description format reference set (foundation metadata concept)",  "900000000000538005");
			types.put("Extended map type reference set (foundation metadata concept)",  "609331003");
			types.put("Language type reference set (foundation metadata concept)",  "900000000000506000");
			types.put("Module dependency reference set (foundation metadata concept)",  "900000000000534007");
			types.put("Ordered type reference set (foundation metadata concept)",  "447258008");
			types.put("Query specification type reference set (foundation metadata concept)",  "900000000000512005");
			types.put("Reference set descriptor reference set (foundation metadata concept)",  "900000000000456007");
			types.put("Simple map type reference set (foundation metadata concept)",  "900000000000496009");
			types.put("Simple type reference set (foundation metadata concept)", "446609009");
	 * @throws Exception
	 */
	@Test
	public void testGetRefseTypes() throws Exception {
		
		this.mockMvc.perform(
				get("/v1.0/snomed/refsetTypes")
				.accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.meta.message").value("Success"))
        .andExpect(jsonPath("$.meta.status").value("OK"))
        .andExpect(jsonPath("$.content.refsetTypes.900000000000496009").value("Simple map type reference set (foundation metadata concept)"))
        .andExpect(jsonPath("$.content.refsetTypes.446609009").value("Simple type reference set (foundation metadata concept)"));
;


	}

}
