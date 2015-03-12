/**
 * 
 */
package org.ihtsdo.otf.refset.service;

import java.util.List;

import org.ihtsdo.otf.refset.common.SearchCriteria;
import org.ihtsdo.otf.refset.domain.Member;
import org.ihtsdo.otf.refset.domain.Refset;
import org.ihtsdo.otf.refset.exception.EntityNotFoundException;
import org.ihtsdo.otf.refset.exception.RefsetServiceException;
import org.ihtsdo.otf.refset.graph.RefsetGraphAccessException;

/**
 * @author Episteme Partners
 *
 */
public interface RefsetBrowseService {
	
	/**Method to get List of published {@link Refset}s
	 * @param from
	 * @param to
	 * @return
	 * @throws RefsetServiceException
	 */
	public List<Refset> getRefsets(Integer from, Integer to, boolean published) throws RefsetServiceException ;
	
	/**Method to retrieve {@link Refset} details for given refset id.
	 * @param refsetId
	 * @return
	 * @throws RefsetServiceException
	 */
	public Refset getRefset(String refsetId) throws RefsetServiceException ;
	
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
	public Refset getRefset(String refsetId, Integer from, Integer to) throws RefsetServiceException ;

	/**
	 * @param refSetId
	 * @return a {@link Refset} with member count and excluding all member details
	 */
	public Refset getRefsetHeader(String refSetId) throws RefsetServiceException ;

	/**
	 * @param refsetId
	 * @return
	 * @throws RefsetServiceException
	 * @throws EntityNotFoundException
	 */
	Refset getRefsetForExport(String refsetId) throws RefsetServiceException,
			EntityNotFoundException;
	
	
	/** Validates if given refset id  belong to given user
	 * @param refsetId
	 * @param userName
	 * @return
	 * @throws RefsetGraphAccessException 
	 */
	public boolean isOwner(String refsetId, String userName) throws RefsetGraphAccessException;
	
	public List<Refset> getRefsets(SearchCriteria criteria) throws RefsetServiceException ;

}
