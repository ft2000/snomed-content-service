/**
 * 
 */
package org.ihtsdo.otf.snomed.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ihtsdo.otf.refset.domain.ChangeRecord;
import org.ihtsdo.otf.refset.exception.EntityNotFoundException;
import org.ihtsdo.otf.refset.graph.RefsetGraphAccessException;
import org.ihtsdo.otf.snomed.domain.Concept;
import org.ihtsdo.otf.snomed.exception.ConceptServiceException;

/**
 *
 */
public interface ConceptLookupService {
	
	/**Method to get {@link Map} of supplied concept ids
	 * and their details {@link Concept}s
	 * @param release SNOMED® CT release in YYYY-mm-dd format
	 * @param conceptIds list of valid concept ids
	 * @return
	 * @throws ConceptServiceException
	 */
	public Map<String, Concept> getConcepts(Set<String> conceptIds, String release) throws ConceptServiceException ;
	
	/**
	 * @param conceptId
	 * @param release SNOMED® CT release in YYYY-mm-dd format
	 * @return
	 * @throws ConceptServiceException
	 * @throws EntityNotFoundException
	 */
	public Concept getConcept(String conceptId, String release) throws ConceptServiceException, EntityNotFoundException ;

	
	/**Method to retrieve list of  {@link Concept} details for given concept id.
	 * @param refsetId
	 * @return
	 * @throws ConceptServiceException
	 */
	public Set<String> getConceptIds(int offset, int limit) throws ConceptServiceException ;
	
	
	/**Gets first 100 child name and their ids in SNOMED database
	 * @param parentId
	 * @return
	 * @throws ConceptServiceException
	 */
	public Map<String, String> getTypes(String parentId) throws ConceptServiceException ;

	/**
	 * @param referenceComponentId
	 * @return
	 * @throws RefsetGraphAccessException
	 */
	String getMemberDescription(String referenceComponentId)
			throws RefsetGraphAccessException;

	/**
	 * @param conceptIds
	 * @return
	 * @throws ConceptServiceException
	 */
	Map<String, ChangeRecord<Concept>> getConceptHistory(Set<String> conceptIds)
			throws ConceptServiceException;

	/**Gets concept's details and its history if available
	 * @param conceptId
	 * @return
	 * @throws ConceptServiceException
	 * @throws EntityNotFoundException
	 */
	ChangeRecord<Concept> getConceptHistory(String conceptId)
			throws ConceptServiceException, EntityNotFoundException;

	/**
	 * @param rcIds
	 * @param release SNOMED® CT release in YYYY-mm-dd format
	 * @return
	 * @throws RefsetGraphAccessException
	 */
	Map<String, String> getMembersDescription(List<String> rcIds, String release)
			throws RefsetGraphAccessException;


}
