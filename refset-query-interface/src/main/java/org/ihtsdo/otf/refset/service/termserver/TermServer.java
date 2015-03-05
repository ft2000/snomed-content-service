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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.ihtsdo.otf.refset.exception.RefsetServiceException;
import org.ihtsdo.otf.terminology.domain.SnomedConcept;
import org.ihtsdo.otf.terminology.domain.SnomedRefset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**Class to front Terminology server calls. For refset service it is defacto Terminology Server.
 */
@Service
public class TermServer {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TermServer.class);

	@Autowired
	private ComponentService cService;
	
	@Autowired
	private RefsetService rService;
	
	@Autowired
	private TerminologyMetaService mService;

	/**Retrieves concept details and preferred term of concept for given ids.
	 * 
	 * @param ids - valid snomed concept ids
	 * @param releaseDate must be in the format of yyyy-mm-dd
	 * @return collection of {@link SnomedConcept} only where concept details exist for an id
	 */
	public Map<String, SnomedConcept> getConcepts(List<String> ids, String releaseDate) {
		
		return cService.getConcepts(ids, releaseDate);
	}
	
	
	/**A convenient method to retrieve a single concept details and preferred term of concept for given id.
	 * 
	 * @param id - valid snomed concept id
	 * @param releaseDate must be in the format of yyyy-mm-dd
	 * @return collection of {@link SnomedConcept} only where concept details exist for an id
	 */
	public SnomedConcept getConcept(String id, String releaseDate) {
		
		List<String> ids = new ArrayList<String>();
		ids.add(id);
		
		return cService.getConcepts(ids, releaseDate).get(id);
		
	}
	
	
	/** Returns Refset header details excluding its members
	 * @param id valid refset id
	 * @param releaseDate must be in the format of yyyy-mm-dd
	 * @return {@link SnomedRefset}
	 */
	@Cacheable(value = { "ts.releases" })
	public SnomedRefset getRefset(String id, String releaseDate) {
		
		return rService.getRefset(id, releaseDate);
	}
	
	/**Returns available SNOMED CT versions and effectiveDate
	 * @return
	 * @throws RefsetServiceException 
	 */
	@Cacheable(value = { "ts.releases" })
	public Map<String, String> getReleases() throws RefsetServiceException {
		
		return mService.getReleases();
	}
	
	/**Convenient method to return a greatest and latest release
	 * @return
	 * @throws RefsetServiceException 
	 */
	@Cacheable(value = { "ts.latest.release" })
	public String getLatestRelease() throws RefsetServiceException {
		
		 Map<String, String> releases = getReleases();
		 
		 DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");
		 		 
		 SortedMap<Date, String> sortedEffectiveDate = new TreeMap<Date, String>();
		 
		 Set<String> versions = releases.keySet();
		 
		 for (String version : versions) {
			
			 try {
				String et = releases.get(version) != null ? releases.get(version).replace("Z", "GMT+00:00") : null;
				Date dt = df.parse(et);
				LOGGER.debug("Effective Time {} and Version {}", dt, version);
				sortedEffectiveDate.put(dt, version);
				
			} catch (ParseException e) {
				
				LOGGER.error("Error while parsing effective date", e);
				//continue;
				
			}
		}

		return sortedEffectiveDate.lastKey() != null ? sortedEffectiveDate.get(sortedEffectiveDate.lastKey()) : null;
	}
	
	
}
