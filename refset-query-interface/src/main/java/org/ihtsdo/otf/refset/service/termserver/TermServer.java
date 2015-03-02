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

import java.util.List;
import java.util.Map;

import org.ihtsdo.otf.terminology.domain.SnomedConcept;
import org.ihtsdo.otf.terminology.domain.SnomedRefset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**Class to front Terminology server calls. For refset service it is defacto Terminology Server.
 */
@Service
public class TermServer {
	

	@Autowired
	private ComponentService cService;
	
	@Autowired
	private RefsetService rService;

	/**Retrieves concept details and preferred term of concept for given ids.
	 * 
	 * @param ids - valid snomed concept ids
	 * @param releaseDate must be in the format of yyyy-mm-dd
	 * @return collection of {@link SnomedConcept} only where concept details exist for an id
	 */
	public Map<String, SnomedConcept> getConcepts(List<String> ids, String releaseDate) {
		
		return cService.getConcepts(ids, releaseDate);
	}
	
	
	/** Returns Refset header details excluding its members
	 * @param id valid refset id
	 * @param releaseDate must be in the format of yyyy-mm-dd
	 * @return {@link SnomedRefset}
	 */
	public SnomedRefset getRefset(String id, String releaseDate) {
		
		return rService.getRefset(id, releaseDate);
	}
}
