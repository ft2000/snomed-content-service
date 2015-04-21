/**
 * 
 */
package org.ihtsdo.otf.refset.service.browse;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.ihtsdo.otf.refset.common.SearchCriteria;
import org.ihtsdo.otf.refset.domain.RefsetDTO;
import org.ihtsdo.otf.refset.exception.EntityNotFoundException;
import org.ihtsdo.otf.refset.exception.RefsetServiceException;
import org.ihtsdo.otf.refset.graph.RefsetGraphAccessException;
import org.ihtsdo.otf.refset.graph.gao.RefsetExportGAO;
import org.ihtsdo.otf.refset.graph.gao.RefsetGAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * This service supports 
 * 1. Refset retrieval  
 * 2. Member retrieval
 * 3. Refset Header retrieval
 * 4. Data retrieval for export
 * Roughly all read operation required by APIs
 *
 */
@Service(value = "browseGraphService")
public class RefsetGraphService implements RefsetBrowseService {

	private static final Logger LOGGER = LoggerFactory.getLogger(RefsetGraphService.class);
	
	@Autowired
	private RefsetGAO gao;
	
	@Autowired
	private RefsetExportGAO export;
	
	public boolean isDescriptionExist(String descrition) throws  RefsetServiceException{
		
		LOGGER.debug("checking description name {}", descrition);

		try {
			
			return gao.isDescriptionExist(descrition);
			
		} catch (RefsetGraphAccessException e) {

			LOGGER.error("Error in graph db call", e);
			throw new RefsetServiceException(e.getMessage());
			
		}
	}


	/* (non-Javadoc)
	 * @see org.ihtsdo.otf.refset.service.browse.RefsetBrowseService#getRefset(java.lang.String, java.lang.Integer, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public RefsetDTO getRefset(String refsetId, Integer from, Integer to, Integer version)
			throws RefsetServiceException {

		LOGGER.debug("getRefset for range from {}, to {}", from, to);

		if (from < 0 | to < 0 | (from == 0 && to == 0) | from > to) {
			
			String msg = String.format("No data available for, Invalid range from - %s to - %s", from, to);
			
			throw new EntityNotFoundException(msg);
		}
		try {
			
			return gao.getRefset(refsetId, from, to, version);
			
		} catch (RefsetGraphAccessException e) {
			
			LOGGER.error("Error in graph db call", e);
			throw new RefsetServiceException(e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see org.ihtsdo.otf.refset.service.RefsetBrowseService#getRefsetHeader(java.lang.String)
	 */
	@Override
	public RefsetDTO getRefsetHeader(String refSetId, Integer version)
			throws RefsetServiceException {

		LOGGER.debug("getRefsetHeader for refSetId {}", refSetId);

		try {
			
			return gao.getRefsetHeader(refSetId, version);
			
		} catch (RefsetGraphAccessException e) {
			
			LOGGER.error("Error in graph db call", e);
			throw new RefsetServiceException(e.getMessage());
		}
	}
	
	
	/* (non-Javadoc)
	 * @see org.ihtsdo.otf.refset.service.RefsetBrowseService#getRefset(java.lang.String)
	 */
	@Override
	public RefsetDTO getRefsetForExport(String refsetId, Integer version) throws RefsetServiceException {

		LOGGER.debug("getRefsetForExport for {}", refsetId);

		try {
			
			return export.getRefset(refsetId, version);
			
		} catch (RefsetGraphAccessException e) {

			LOGGER.error("Error in graph db call", e);
			throw new RefsetServiceException(e.getMessage());
			
		}
	}

	/* (non-Javadoc)
	 * @see org.ihtsdo.otf.refset.service.RefsetBrowseService#isOwner(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean isOwner(String refsetId, String userName) throws RefsetGraphAccessException {

		RefsetDTO r = gao.getRefsetHeader(refsetId, -1);
		
		return !StringUtils.isEmpty(userName) ? userName.equalsIgnoreCase(r.getCreatedBy()) : false;
	}

	/* (non-Javadoc)
	 * @see org.ihtsdo.otf.refset.service.RefsetBrowseService#getRefsets(org.ihtsdo.otf.refset.common.SearchCriteria)
	 */
	@Override
	public List<RefsetDTO> getRefsets(SearchCriteria criteria) throws RefsetServiceException {

		LOGGER.debug("getRefsets with criteria");
		
		List<RefsetDTO> refsets = Collections.emptyList();
		try {
			
			refsets = gao.getRefSets(criteria);
			
		} catch (RefsetGraphAccessException e) {
			
			LOGGER.error("Error in graph db call", e);
			throw new RefsetServiceException(e.getMessage());
		}
		
		return refsets;
		
	}

	/* (non-Javadoc)
	 * @see org.ihtsdo.otf.refset.service.browse.RefsetBrowseService#totalNoOfRefset(org.ihtsdo.otf.refset.common.SearchCriteria)
	 */
	@Override
	public Long totalNoOfRefset(SearchCriteria criteria)
			throws RefsetServiceException {
		
		LOGGER.debug("totalNoOfRefset with criteria {}", criteria);
		
		return gao.totalNoOfRefset(criteria);

	}

	/* (non-Javadoc)
	 * @see org.ihtsdo.otf.refset.service.browse.RefsetBrowseService#getRefsetVersions(java.lang.String)
	 */
	@Override
	public Set<Integer> getRefsetVersions(String refSetId)
			throws RefsetServiceException {
		LOGGER.debug("getRefsetVersions for refsetId {}", refSetId);

		return gao.getRefsetVersions(refSetId);
	}


	/* (non-Javadoc)
	 * @see org.ihtsdo.otf.refset.service.browse.RefsetBrowseService#getRefsetHeaderByCoceptId(java.lang.String, java.lang.Integer)
	 */
	@Override
	public RefsetDTO getRefsetHeaderByCoceptId(String conceptId, Integer version)
			throws RefsetServiceException {

		try {
			
			return gao.getRefsetHeaderByCoceptId(conceptId, version);
			
		} catch (RefsetGraphAccessException e) {

			LOGGER.error("Error in graph db call", e);
			throw new RefsetServiceException(e.getMessage());
			
		}
	}
}
