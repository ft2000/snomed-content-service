/**
 * 
 */
package org.ihtsdo.otf.refset.security;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * @author Episteme Partners
 *
 */
public class OTFServiceAuthenticationTest {

	private static final String OTF_SERVICE_URL = "http://usermanagement.ihtsdotools.org:8080/security-web/query";
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

	@Test
	public void test() {
		
		RestTemplate rt = new RestTemplate();
        rt.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        rt.getMessageConverters().add(new StringHttpMessageConverter());

        
        MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
        params.add("username", "pnema");
        params.add("password", "a5c527de-c77d-4b74-9186-efc3f29d65ef");
        params.add("queryName", "getUserByNameAuth");

        
        final User user = new User(); 
        		
		try {
			JsonNode obj = rt.postForObject(OTF_SERVICE_URL, params, JsonNode.class);
			JsonNode userObj = obj.get("user");
			String userName = userObj.findValue("name").asText();
			String status = userObj.findValue("status").asText();
			Assert.notNull(status, "Status can not be empty");
			user.setUsername(userName);
			
	        assertNotNull(user);
	        
	      //now check if user has access to Refset app.
	        params = new LinkedMultiValueMap<String, String>();
	        params.add("username", "pnema");
	        params.add("queryName", "getUserApps");

	        JsonNode appJson = rt.postForObject(OTF_SERVICE_URL, params, JsonNode.class);
			
	        assertNotNull(appJson);
	        
	        JsonNode apps = appJson.get("apps");
	        
	        if (apps.isArray()) {
			
		        for (Object object : apps) {
					System.out.println(object);
				}


			}

		} catch (Exception e) {
			
			e.printStackTrace();

		}
        
        
        
	}

}
