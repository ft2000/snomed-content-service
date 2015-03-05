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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ihtsdo.otf.terminology.domain.SnomedConcept;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public class ComponentService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ComponentService.class);

	@Autowired(required = true)
	private TermClient termClient;
	
	@Value(value = "${ts.component.endpoint}")
	private String componentEndpoint = "/refset/{version}/concepts";

	/**
	 * @param ids
	 * @param releaseDate
	 * @return
	 */
	protected Map<String, SnomedConcept> getConcepts(List<String> ids, String releaseDate) {

		LOGGER.debug("Calling term server with input ids as {}, and release date as {}", ids, releaseDate);
		
        Map<String, Object> var = new HashMap<String, Object>();
        var.put("version", releaseDate);
        ResponseEntity<List<SnomedConcept>> response = termClient.exchange(termClient.getHost() + componentEndpoint, HttpMethod.POST, 
        		new HttpEntity<List<String>>(ids), new ParameterizedTypeReference<List<SnomedConcept>>() {
		}, var); 

        Map<String, SnomedConcept> concepts = new HashMap<String, SnomedConcept>();
        List<SnomedConcept> results = response.getBody();
        for (SnomedConcept snomedConcept : results) {
			
        	concepts.put(snomedConcept.getId(), snomedConcept);
        	
		}
        
        //term server service will ignore ids which has no associated concept so those ids should be returned as null
        for (String id : ids) {
			
        	if (!concepts.containsKey(id)) {
				
        		concepts.put(id, null);
			}
		}
        return concepts;
	}
}
