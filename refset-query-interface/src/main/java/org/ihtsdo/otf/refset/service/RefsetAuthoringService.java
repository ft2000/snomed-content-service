/**
 * 
 */
package org.ihtsdo.otf.refset.service;

import org.ihtsdo.otf.refset.domain.Member;
import org.ihtsdo.otf.refset.domain.MetaData;
import org.ihtsdo.otf.refset.domain.Refset;
import org.ihtsdo.otf.refset.exception.EntityNotFoundException;
import org.ihtsdo.otf.refset.exception.RefsetServiceException;

/**Service to support {@link Refset} authoring
 * @author Episteme Partners
 *
 */
public interface RefsetAuthoringService {
	
	/**Method to add a {@link Refset}
	 * @param r a {@link Refset}
	 * @throws RefsetServiceException
	 * @return a {@link Refset} object stored in graph with {@link MetaData}
	 */
	public String addRefset(Refset r) throws RefsetServiceException ;
	
	/**Method to add a {@link Member} to given {@link Refset}.
	 * @param refsetId
	 * @throws RefsetServiceException
	 */
	public void addMember(String refsetId, Member m) throws RefsetServiceException ;
	
	
	/**Method to add a {@link Refset}
	 * @param r a {@link Refset}
	 * @throws RefsetServiceException
	 * @return a {@link Refset} object stored in graph with {@link MetaData}
	 */
	public String updateRefset(Refset r) throws RefsetServiceException, EntityNotFoundException ;
	
	/**Method to add a {@link Member} to given {@link Refset}.
	 * @param refsetId
	 * @throws RefsetServiceException
	 */
	public void remove(String refsetId) throws RefsetServiceException, EntityNotFoundException ;


}
