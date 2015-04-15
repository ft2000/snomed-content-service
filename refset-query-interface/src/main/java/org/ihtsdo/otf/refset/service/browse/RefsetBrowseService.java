/**
 * 
 */
package org.ihtsdo.otf.refset.service.browse;

import java.util.List;
import java.util.Set;

import org.ihtsdo.otf.refset.common.SearchCriteria;
import org.ihtsdo.otf.refset.domain.RefsetDTO;
import org.ihtsdo.otf.refset.exception.EntityNotFoundException;
import org.ihtsdo.otf.refset.exception.RefsetServiceException;
import org.ihtsdo.otf.refset.graph.RefsetGraphAccessException;

/**
 *
 */
public interface RefsetBrowseService {
			
	/**Validates if given description exist in the system or not
	 * @param descrition
	 * @return
	 * @throws RefsetServiceException
	 */
	public boolean isDescriptionExist(String descrition) throws RefsetServiceException;

	/**Method to get  {@link Refset} with List of limited  {@link Member}s for given refsetId. 
	 * Returned refset object will only have finite size of members enumerated by from and to range.
	 * Although, essentially this method is to get members but Refset object is returned so that 
	 * controller layer can determine its published status. Unpublished {@link Refset}'s members
	 * can not be returned for non logged in user
	 * @param from
	 * @param to
	 * @return
	 * @throws RefsetServiceException
	 */
	public RefsetDTO getRefset(String refsetId, Integer from, Integer to, Integer version) throws RefsetServiceException ;

	/**
	 * @param refSetId
	 * @return a {@link Refset} with member count and excluding all member details
	 */
	public RefsetDTO getRefsetHeader(String refSetId, Integer version) throws RefsetServiceException ;

	/**
	 * @param refsetId
	 * @return
	 * @throws RefsetServiceException
	 * @throws EntityNotFoundException
	 */
	public RefsetDTO getRefsetForExport(String refsetId) throws RefsetServiceException,
			EntityNotFoundException;
	
	
	/** Validates if given refset id  belong to given user
	 * @param refsetId
	 * @param userName
	 * @return
	 * @throws RefsetGraphAccessException 
	 */
	public boolean isOwner(String refsetId, String userName) throws RefsetGraphAccessException;
	
	/**
	 * @param criteria
	 * @return
	 * @throws RefsetServiceException
	 */
	public List<RefsetDTO> getRefsets(SearchCriteria criteria) throws RefsetServiceException ;
	
	/**Service to retrieve total no of refset for given criteria
	 * @param criteria
	 * @return
	 * @throws RefsetServiceException
	 */
	public Long totalNoOfRefset(SearchCriteria criteria) throws RefsetServiceException ;
	
	/**Service to retrieve all available published/released version
	 * @param refSetId
	 * @return
	 * @throws RefsetServiceException
	 */
	public Set<Integer> getRefsetVersions(String refSetId) throws RefsetServiceException ;


}
