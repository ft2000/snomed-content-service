/**
 * 
 */
package org.ihtsdo.otf.refset.domain;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ihtsdo.otf.refset.common.Meta;
import org.ihtsdo.otf.refset.common.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Episteme Partners
 *
 */
public class ResponseTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link org.ihtsdo.otf.refset.common.Response#getData()}.
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 */
	@Test
	public void testGetData() throws JsonGenerationException, JsonMappingException, IOException {
		
		List<Refset> rs = new ArrayList<Refset>();
		
		for (int i = 0; i < 11; i++) {
			
			Refset r = new Refset();
			r.setCreated( new Date().toString() );
			r.setCreatedBy( "Junit Author - " + i );
			r.setDescription( "Junit Refset" );
			r.setEffectiveTime( new Date().toString() );
			r.setId( "2000000" + i + 10 );
			
			rs.add(r);
			
		}
		
		Map<String , Object> data = new HashMap<String, Object>();
		data.put("refsets", rs);
		
		Response<Map<String , Object>> response = new Response<Map<String , Object>>();
		response.setData(data);
		Meta m = new Meta();
		//response.add(linkTo(methodOn(RefsetBrowseController.class).getRefsets(1, 10)).withSelfRel());
		m.setMessage( "Success" );
		m.setStatus( HttpStatus.OK );
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue( new File( "src/test/resources/response.json" ), response );
		

	}

}
