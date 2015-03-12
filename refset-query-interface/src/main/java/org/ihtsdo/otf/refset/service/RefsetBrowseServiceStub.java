/**
 * 
 */
package org.ihtsdo.otf.refset.service;

import java.util.List;

import org.ihtsdo.otf.refset.common.SearchCriteria;
import org.ihtsdo.otf.refset.domain.Refset;
import org.ihtsdo.otf.refset.exception.EntityNotFoundException;
import org.ihtsdo.otf.refset.exception.RefsetServiceException;
import org.ihtsdo.otf.refset.graph.RefsetGraphAccessException;
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

	/* (non-Javadoc)
	 * @see org.ihtsdo.otf.refset.service.RefsetBrowseService#isDescriptionExist(java.lang.String)
	 */
	@Override
	public boolean isDescriptionExist(String descrition)
			throws RefsetServiceException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.ihtsdo.otf.refset.service.RefsetBrowseService#getRefset(java.lang.String, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public Refset getRefset(String refsetId, Integer from, Integer to)
			throws RefsetServiceException, EntityNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.ihtsdo.otf.refset.service.RefsetBrowseService#getRefsetHeader(java.lang.String)
	 */
	@Override
	public Refset getRefsetHeader(String refSetId)
			throws RefsetServiceException, EntityNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.ihtsdo.otf.refset.service.RefsetBrowseService#getRefsetForExport(java.lang.String)
	 */
	@Override
	public Refset getRefsetForExport(String refsetId)
			throws RefsetServiceException, EntityNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.ihtsdo.otf.refset.service.RefsetBrowseService#isOwner(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean isOwner(String refsetId, String userName)
			throws RefsetGraphAccessException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.ihtsdo.otf.refset.service.RefsetBrowseService#getRefsets(org.ihtsdo.otf.refset.common.SearchCriteria)
	 */
	@Override
	public List<Refset> getRefsets(SearchCriteria criteria) throws RefsetServiceException {
		// TODO Auto-generated method stub
		return null;
	}

}
