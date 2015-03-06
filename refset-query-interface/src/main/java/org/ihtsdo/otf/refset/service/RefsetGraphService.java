/**
 * 
 */
package org.ihtsdo.otf.refset.service;

import java.util.Collections;
import java.util.List;

import org.ihtsdo.otf.refset.domain.Refset;
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

	/* (non-Javadoc)
	 * @see org.ihtsdo.otf.refset.service.RefsetBrowseService#getRefsets(java.lang.Integer, java.lang.Integer)
	 * TODO change this to pass a map of query criteria decide by controller
	 */
	@Override
	public List<Refset> getRefsets(Integer page, Integer size, boolean published) throws RefsetServiceException {

		LOGGER.debug("getRefsets");
		
		List<Refset> refsets = Collections.emptyList();
		try {
			
			refsets = gao.getRefSets(published, page, size);
			
		} catch (RefsetGraphAccessException e) {
			
			LOGGER.error("Error in graph db call", e);
			throw new RefsetServiceException(e.getMessage());
		}
		
		return refsets;
		
	}

	/* (non-Javadoc)
	 * @see org.ihtsdo.otf.refset.service.RefsetBrowseService#getRefset(java.lang.String)
	 */
	@Override
	public Refset getRefset(String refsetId) throws RefsetServiceException {

		LOGGER.debug("getRefset for {}", refsetId);

		try {
			
			return gao.getRefset(refsetId);
			
		} catch (RefsetGraphAccessException e) {

			LOGGER.error("Error in graph db call", e);
			throw new RefsetServiceException(e.getMessage());
			
		}
	}
	
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
	 * @see org.ihtsdo.otf.refset.service.RefsetBrowseService#getRefset(java.lang.String, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public Refset getRefset(String refsetId, Integer from, Integer to)
			throws RefsetServiceException {

		LOGGER.debug("getRefset for range from {}, to {}", from, to);

		if (from < 0 | to < 0 | (from == 0 && to == 0) | from > to) {
			
			String msg = String.format("No data available for, Invalid range from - %s to - %s", from, to);
			
			throw new EntityNotFoundException(msg);
		}
		try {
			
			return gao.getRefset(refsetId, from, to);
			
		} catch (RefsetGraphAccessException e) {
			
			LOGGER.error("Error in graph db call", e);
			throw new RefsetServiceException(e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see org.ihtsdo.otf.refset.service.RefsetBrowseService#getRefsetHeader(java.lang.String)
	 */
	@Override
	public Refset getRefsetHeader(String refSetId)
			throws RefsetServiceException {

		LOGGER.debug("getRefsetHeader for refSetId {}", refSetId);

		try {
			
			return gao.getRefsetHeader(refSetId);
			
		} catch (RefsetGraphAccessException e) {
			
			LOGGER.error("Error in graph db call", e);
			throw new RefsetServiceException(e.getMessage());
		}
	}
	
	
	/* (non-Javadoc)
	 * @see org.ihtsdo.otf.refset.service.RefsetBrowseService#getRefset(java.lang.String)
	 */
	@Override
	public Refset getRefsetForExport(String refsetId) throws RefsetServiceException {

		LOGGER.debug("getRefsetForExport for {}", refsetId);

		try {
			
			return export.getRefset(refsetId);
			
		} catch (RefsetGraphAccessException e) {

			LOGGER.error("Error in graph db call", e);
			throw new RefsetServiceException(e.getMessage());
			
		}
	}

	/* (non-Javadoc)
	 * @see org.ihtsdo.otf.refset.service.RefsetBrowseService#getMyRefsets(java.lang.Integer, java.lang.Integer, java.lang.String)
	 */
	@Override
	public List<Refset> getMyRefsets(Integer page, Integer size, String userName)
			throws RefsetServiceException {

		LOGGER.debug("getRefsets");
		
		List<Refset> refsets = Collections.emptyList();
		try {
			
			refsets = gao.getMyRefSets(userName, page, size);
			
		} catch (RefsetGraphAccessException e) {
			
			LOGGER.error("Error in graph db call", e);
			throw new RefsetServiceException(e.getMessage());
		}
		
		return refsets;
		
	}

	/* (non-Javadoc)
	 * @see org.ihtsdo.otf.refset.service.RefsetBrowseService#isOwner(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean isOwner(String refsetId, String userName) throws RefsetGraphAccessException {

		return !StringUtils.isEmpty(userName) ? userName.equalsIgnoreCase(gao.getOwner(refsetId)) : false;
	}
	
	
	

	


}
