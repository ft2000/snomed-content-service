/**
 * 
 */
package org.ihtsdo.otf.refset.service;

import java.util.List;
import java.util.Set;

import org.ihtsdo.otf.refset.common.SearchCriteria;
import org.ihtsdo.otf.refset.domain.RefsetDTO;
import org.ihtsdo.otf.refset.exception.EntityNotFoundException;
import org.ihtsdo.otf.refset.exception.RefsetServiceException;
import org.ihtsdo.otf.refset.graph.RefsetGraphAccessException;
import org.ihtsdo.otf.refset.service.browse.RefsetBrowseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *It was being used to test services
 */
@Service(value = "browseServiceStub")
public class RefsetBrowseServiceStub implements RefsetBrowseService {

	
	@Autowired
	private RefsetBrowseServiceStubData dataService;


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
	public RefsetDTO getRefset(String refsetId, Integer from, Integer to, Integer version)
			throws RefsetServiceException, EntityNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.ihtsdo.otf.refset.service.RefsetBrowseService#getRefsetHeader(java.lang.String)
	 */
	@Override
	public RefsetDTO getRefsetHeader(String refSetId, Integer version)
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
	public List<RefsetDTO> getRefsets(SearchCriteria criteria) throws RefsetServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.ihtsdo.otf.refset.service.browse.RefsetBrowseService#totalNoOfRefset(org.ihtsdo.otf.refset.common.SearchCriteria)
	 */
	@Override
	public Long totalNoOfRefset(SearchCriteria criteria)
			throws RefsetServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.ihtsdo.otf.refset.service.browse.RefsetBrowseService#getRefsetVersions(java.lang.String)
	 */
	@Override
	public Set<Integer> getRefsetVersions(String refSetId)
			throws RefsetServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.ihtsdo.otf.refset.service.browse.RefsetBrowseService#getRefsetForExport(java.lang.String, java.lang.Integer)
	 */
	@Override
	public RefsetDTO getRefsetForExport(String refsetId, Integer version)
			throws RefsetServiceException, EntityNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

}
