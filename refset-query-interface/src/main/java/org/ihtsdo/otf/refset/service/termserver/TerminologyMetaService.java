/**
* Copyright 2014 IHTSDO
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.ihtsdo.otf.refset.service.termserver;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ihtsdo.otf.refset.exception.RefsetServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public class TerminologyMetaService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TerminologyMetaService.class);

	@Autowired(required = true)
	private TermClient termClient;
	
	@Value(value = "${ts.versions.endpoint}")
	private String versionsEndpoint = "/codesystems/SNOMEDCT/versions";

	/** Returns released version of SNOMED® CT terminology and corresponding effective dates available in Term server.
	 * @return
	 * @throws RefsetServiceException 
	 */
	protected Map<String, String> getReleases() throws RefsetServiceException {

		LOGGER.debug("Calling term server to get releases");
		Map<String, String> releases = new HashMap<String, String>();
		
        ResponseEntity<GenericTermServerResponse<String>> response = termClient.exchange(termClient.getHost() + versionsEndpoint, HttpMethod.GET, 
        		null, new ParameterizedTypeReference<GenericTermServerResponse<String>>() {
		}); 
        
        if (response.getStatusCode() == HttpStatus.OK) {
			
        	GenericTermServerResponse<String>  obj = response.getBody();
    		LOGGER.debug("Response from server {}", obj.getItems());

    		List<Map<String, String>> items = obj.getItems();
    		
    		for (Map<String, String> item : items) {
    			
    			if (item.containsKey("version") && item.containsKey("effectiveDate")) {
    				
    				String version = item.get("version");
    	        	String effectiveDate = item.get("effectiveDate");
    	        	releases.put(version, effectiveDate);

    			}
    		}
		} else {
			
			//log error and throw to user
    		LOGGER.error("Error in term service call", response.getStatusCode());

    		String msg = String.format("Term server service call resuted in error - %s", response.getStatusCode().getReasonPhrase());
    		throw new RefsetServiceException(msg);
		}
        
        
		return Collections.unmodifiableMap(releases);
	}

}
