/**
 * 
 */
package org.ihtsdo.otf.refset.service;

import java.util.List;

import org.ihtsdo.otf.refset.domain.Refset;
import org.ihtsdo.otf.refset.exception.EntityNotFoundException;
import org.ihtsdo.otf.refset.exception.RefsetServiceException;
import org.ihtsdo.otf.refset.graph.RefsetGraphAccessException;
import org.ihtsdo.otf.refset.graph.gao.RefsetGAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Episteme Partners
 *
 */
@Service(value = "browseGraphService")
public class RefsetGraphService implements RefsetBrowseService {

	private static final Logger LOGGER = LoggerFactory.getLogger(RefsetGraphService.class);
	
	@Autowired
	private RefsetGAO gao;

	/* (non-Javadoc)
	 * @see org.ihtsdo.otf.refset.service.RefsetBrowseService#getRefsets(java.lang.Integer, java.lang.Integer)
	 * TODO change this to pass a map of query criteria decide by controller
	 */
	@Override
	public List<Refset> getRefsets(Integer page, Integer size, boolean published) throws RefsetServiceException {

		LOGGER.debug("getRefsets");
		
		/*TODO Temporary for front end*/
		if( page == 1 && size == 10) { 
			
			try {
				
				return gao.getRefSets(published);
				
			} catch (RefsetGraphAccessException e) {
				
				throw new RefsetServiceException(e.getMessage());
			}
		}
		
		
		List<Refset> refsets;
		try {
			refsets = gao.getRefSets(published);
		} catch (RefsetGraphAccessException e) {
			
			throw new RefsetServiceException(e.getMessage());
		}
		
		int total = refsets.size();
		
		int from_temp = page >= 1 ? (Math.min(total, Math.abs(page * size)) - size) : 0;
		
		int from = from_temp >= 0 ? from_temp : 0;
		
		int temp = Math.min(total, Math.abs(page * size));
		int to = temp <= total ? temp : total;
		return refsets.subList(from, to);
		
	}

	/* (non-Javadoc)
	 * @see org.ihtsdo.otf.refset.service.RefsetBrowseService#getRefset(java.lang.String)
	 */
	@Override
	public Refset getRefset(String refsetId) throws RefsetServiceException, EntityNotFoundException {

		LOGGER.debug("getRefset for {}", refsetId);

		try {
			
			return gao.getRefset(refsetId);
			
		} catch (RefsetGraphAccessException e) {

			throw new RefsetServiceException(e.getMessage());
			
		}
	}

}
