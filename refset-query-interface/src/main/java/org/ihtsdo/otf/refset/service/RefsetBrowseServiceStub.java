/**
 * 
 */
package org.ihtsdo.otf.refset.service;

import java.util.List;

import org.ihtsdo.otf.refset.domain.Refset;
import org.ihtsdo.otf.refset.exception.RefsetServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Episteme Partners
 *
 */
@Service(value = "browseServiceStub")
public class RefsetBrowseServiceStub implements RefsetBrowseService {

	private static final Logger LOGGER = LoggerFactory.getLogger(RefsetBrowseServiceStub.class);
	
	@Autowired
	private RefsetBrowseServiceStubData dataService;

	/* (non-Javadoc)
	 * @see org.ihtsdo.otf.refset.service.RefsetBrowseService#getRefsets(java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public List<Refset> getRefsets(Integer page, Integer size, boolean published) throws RefsetServiceException {

		LOGGER.debug("getRefsets");
		if( page == 1 && size == 10) { return dataService.getRefSets();}
		
		List<Refset> refsets = dataService.getRefSets();
		
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
	public Refset getRefset(String refsetId) throws RefsetServiceException {

		LOGGER.debug("getRefset for %s", refsetId);

		return dataService.getRefSet(refsetId);
	}

}
