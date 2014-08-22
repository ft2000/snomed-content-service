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
	public List<Refset> getRefsets(Integer page, Integer size) throws RefsetServiceException {

		LOGGER.debug("getRefsets");
		
		return dataService.getRefSets().subList(0, 10);
		
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
