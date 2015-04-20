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
package org.ihtsdo.otf.snomed.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ihtsdo.otf.refset.domain.ChangeRecord;
import org.ihtsdo.otf.refset.exception.EntityNotFoundException;
import org.ihtsdo.otf.refset.graph.RefsetGraphAccessException;
import org.ihtsdo.otf.refset.service.termserver.TermServer;
import org.ihtsdo.otf.snomed.domain.Concept;
import org.ihtsdo.otf.snomed.exception.ConceptServiceException;
import org.ihtsdo.otf.terminology.domain.SnomedConcept;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.util.StringUtils;

import com.google.common.collect.Iterables;


/**
 * Service to look up SNOMED® Terminology data from term server
 *
 */
public class ConceptLookUpServiceImplv1_1 implements ConceptLookupService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ConceptLookUpServiceImplv1_1.class);
		
	@Autowired
	private TermServer server;


	/* (non-Javadoc)
	 * @see org.ihtsdo.otf.snomed.service.ConceptLookupService#getConcepts(java.util.List)
	 */
	@Override
	@Cacheable(value = { "concepts" })
	public Map<String, Concept> getConcepts(Set<String> conceptIds, String release)
			throws ConceptServiceException {

		LOGGER.debug("getting concepts details for {}", conceptIds);
		
		Map<String, Concept> concepts = new HashMap<String, Concept>();
		
		try {
			
			for (List<String> ids : Iterables.partition(conceptIds, 20)) {
				
				Map<String, SnomedConcept> tsConcepts = server.getConcepts(ids, release);
				
				for (String id : ids) {
					
					SnomedConcept tsConcept = tsConcepts.get(id);
					Concept concept = ConceptConvertor.getConcept(tsConcept);
					concepts.put(id, concept);
				}
			}


			
		} catch (Exception e) {
			
			LOGGER.error("Error duing concept details for concept map fetch", e);
			
			throw new ConceptServiceException(e);
			
		}
		
		LOGGER.debug("returning total {} concepts ", concepts.size());

		return Collections.unmodifiableMap(concepts);
	}

	/* (non-Javadoc)
	 * @see org.ihtsdo.otf.snomed.service.ConceptLookupService#getConcept(java.lang.String)
	 */
	@Override
	@Cacheable(value = { "concept" })
	public Concept getConcept(String conceptId, String release) throws ConceptServiceException,
			EntityNotFoundException {
		
		LOGGER.debug("getting concept details for {} and SNOMED release {} ", conceptId, release);

		if (StringUtils.isEmpty(conceptId)) {
			
			throw new EntityNotFoundException(String.format("Invalid concept id", conceptId));
		}
		
		
		try {
			
			SnomedConcept tsConcept = server.getConcept(conceptId, release);
			
			Concept c = ConceptConvertor.getConcept(tsConcept);
			
			if (c == null) {
				
				throw new EntityNotFoundException(String.format("Invalid concept id %s", conceptId));

			}
			
			return c;
				
		} catch (EntityNotFoundException e) {
			
			throw e;
			
		} catch (Exception e) {
		
			
			LOGGER.error("Error duing concept details fetch", e);

			throw new ConceptServiceException(e);
			
		} 


	}

	/* (non-Javadoc)
	 * @see org.ihtsdo.otf.snomed.service.ConceptLookupService#getConceptIds(int, int)
	 */
	@Override
	@Cacheable(value = { "conceptIds" })
	public Set<String> getConceptIds(int offset, int limit)
			throws ConceptServiceException {
		
		return null;
	}	
	
	/* (non-Javadoc)
	 * @see org.ihtsdo.otf.snomed.service.ConceptLookupService#getTypes(String)
	 */
	@Override
	public Map<String, String> getTypes(String id)
			throws ConceptServiceException {
		return null;
	}
	
	
	/**returns referenceComponentDescription for given referenceComponentId
	 * @param referenceComponentId
	 * @return
	 * @throws RefsetGraphAccessException
	 */
	@Override
	@Cacheable(value = { "referenceComponentDescription" })
	public String getMemberDescription(String referenceComponentId) throws RefsetGraphAccessException  {
		
		return null;
	}

	
	/** Returns {@link Map} of referenceComponentId as key and their description as value
	 * @param referenceComponentId
	 * @return
	 * @throws RefsetGraphAccessException
	 */
	@Override
	@Cacheable(value = { "referenceComponentDescriptions" })
	public Map<String, String> getMembersDescription(List<String> rcIds, String release) throws RefsetGraphAccessException  {
		
		LOGGER.trace("getting members description for {} and snomed release {} ", rcIds, release);

		Map<String, String> descMap = new HashMap<String, String>();
		
		if (rcIds == null || rcIds.isEmpty()) {
			
			return descMap;
		}

		try {
					
			Set<String> ids = new HashSet<String>();
			ids.addAll(rcIds);
			
			Map<String, Concept> tsConcepts = getConcepts(ids, release);
			
			for (String id : rcIds) {
				
				descMap.put(id, tsConcepts.get(id) != null ? tsConcepts.get(id).getLabel() : null);
			}
			
		} catch (Exception e) {
			
			LOGGER.error("Error duing concept details fetch", e);
			
			throw new RefsetGraphAccessException(e.getMessage(), e);
			
		}
		
		return descMap;
	}
	

	/* (non-Javadoc)
	 * @see org.ihtsdo.otf.snomed.service.ConceptLookupService#getConceptHistory(java.util.Set)
	 */
	@Override
	public Map<String, ChangeRecord<Concept>> getConceptHistory(Set<String> conceptIds)
			throws ConceptServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.ihtsdo.otf.snomed.service.ConceptLookupService#getConceptHistory(java.lang.String)
	 */
	@Override
	public ChangeRecord<Concept> getConceptHistory(String conceptId)
			throws ConceptServiceException, EntityNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

}
