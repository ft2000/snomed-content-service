/**
 * 
 */
package org.ihtsdo.otf.refset.service;

import java.util.List;

import org.ihtsdo.otf.refset.domain.Refset;
import org.ihtsdo.otf.refset.exception.RefsetServiceException;

/**
 * @author Episteme Partners
 *
 */
public interface RefsetBrowseService {
	
	/**Method to get List of published {@link Refset}s
	 * @param page
	 * @param size
	 * @return
	 * @throws RefsetServiceException
	 */
	public List<Refset> getRefsets(Integer page, Integer size) throws RefsetServiceException ;
	
	/**Method to retrieve {@link Refset} details for given refset id.
	 * @param refsetId
	 * @return
	 * @throws RefsetServiceException
	 */
	public Refset getRefset(String refsetId) throws RefsetServiceException ;


}
