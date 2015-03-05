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
import java.util.Map;

import org.ihtsdo.otf.terminology.domain.SnomedRefset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public class RefsetService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RefsetService.class);

	@Autowired(required = true)
	private TermClient termClient;
	
	@Value(value = "${ts.refset.endpoint}")
	private String refsetEndpoint = "/refset/{version}/header/{id}";

	/**
	 * @param id
	 * @param releaseDate
	 * @return
	 */
	protected SnomedRefset getRefset(String id, String releaseDate) {

		LOGGER.debug("Calling term server with id as {} and release date as {}", id, releaseDate);
		
        Map<String, String> var = new HashMap<String, String>();
        var.put("version", releaseDate);
        var.put("id", id);
        SnomedRefset refset = termClient.getForObject(termClient.getHost() + refsetEndpoint, SnomedRefset.class, var);
		return refset;
	}

}
