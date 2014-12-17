package org.ihtsdo.otf.refset.error;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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

public class DefaultControllerTest {

    @Autowired
    private WebApplicationContext ctx;
    
	
	@InjectMocks
	private DefaultController controller;

    private MockMvc mockMvc;
    
	@Before
	public void setUp() throws Exception {
		
		MockitoAnnotations.initMocks(this);

	    this.mockMvc = standaloneSetup(controller).build();

	}


	@After
	public void tearDown() throws Exception {
	}

	@Test(expected = Exception.class)
	public void testHandleUnknownRequest() throws Exception {
		
		this.mockMvc.perform(
				get("/v1.0/U")
				.accept(MediaType.APPLICATION_JSON))
        .andDo(print());

	}

}
