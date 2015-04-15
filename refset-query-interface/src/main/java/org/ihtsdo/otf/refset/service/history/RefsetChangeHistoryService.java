/**
 * 
 */
package org.ihtsdo.otf.refset.service.history;

import java.util.Map;

import org.ihtsdo.otf.refset.domain.ChangeRecord;
import org.ihtsdo.otf.refset.domain.MemberDTO;
import org.ihtsdo.otf.refset.domain.RefsetDTO;
import org.ihtsdo.otf.refset.exception.EntityNotFoundException;
import org.ihtsdo.otf.refset.exception.RefsetServiceException;
import org.ihtsdo.otf.refset.graph.RefsetGraphAccessException;
import org.ihtsdo.otf.refset.graph.gao.HistoryGao;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 
 *
 */
@Service(value = "Service")
public class RefsetChangeHistoryService {

	private static final Logger LOGGER = LoggerFactory.getLogger(RefsetChangeHistoryService.class);
	
	@Autowired
	private HistoryGao gao;


	/**
	 * @param refsetId
	 * @param referenceComponentId
	 * @param fromDate
	 * @param toDate
	 * @param from
	 * @param to
	 * @return
	 * @throws RefsetServiceException
	 */
	public ChangeRecord<MemberDTO> getMemberHistory(String refsetId, String referenceComponentId, DateTime fromDate, DateTime toDate, int from, int to) throws RefsetServiceException {

		LOGGER.debug("getMemberHistory");
		
		try {
			
			return gao.getMemberHistory(refsetId, referenceComponentId, fromDate, toDate, from, to);
			
		} catch (RefsetGraphAccessException e) {
			
			LOGGER.error("Error in graph db call", e);
			throw new RefsetServiceException(e.getMessage());
		}
				
	}


	/**
	 * @param refsetId
	 * @param fromDate
	 * @param toDate
	 * @param from
	 * @param to
	 * @return
	 * @throws RefsetServiceException
	 */
	public Map<String, ChangeRecord<MemberDTO>> getAllMembersHistory(String refsetId, DateTime fromDate, DateTime toDate, int from, int to) throws RefsetServiceException {

		LOGGER.debug("getAllMembersHistory");
		
		try {
			
			return gao.getAllMembersHistory(refsetId, fromDate, toDate, from, to);
			
		} catch (RefsetGraphAccessException e) {
			
			LOGGER.error("Error in graph db call", e);
			throw new RefsetServiceException(e.getMessage());
		}
				
	}
	
	/**
	 * @param refsetId
	 * @param fromDate
	 * @param toDate
	 * @param from
	 * @param to
	 * @return
	 * @throws RefsetServiceException
	 */
	public ChangeRecord<RefsetDTO> getRefsetHeaderHistory(String refsetId, DateTime fromDate, DateTime toDate, int from, int to) throws RefsetServiceException {

		LOGGER.debug("getRefsetHeaderHistory");
		
		try {
			
			return gao.getRefsetHeaderHistory(refsetId, fromDate, toDate, from, to);
			
		} catch (RefsetGraphAccessException e) {
			
			LOGGER.error("Error in graph db call", e);
			throw new RefsetServiceException(e.getMessage());
		}
				
	}

	/**
	 * @param refsetId
	 * @param memberId
	 * @param fromDate
	 * @param toDate
	 * @param from
	 * @param to
	 * @return
	 * @throws RefsetServiceException 
	 */
	public ChangeRecord<MemberDTO> getMemberStateHistory(String refsetId,
			String memberId, DateTime fromDate, DateTime toDate, int from,
			int to) throws RefsetServiceException {
		
		LOGGER.debug("getMemberStateHistory");
		
		try {
			
			return gao.getMemberStateHistory(refsetId, memberId, fromDate, toDate, from, to);
			
		} catch (RefsetGraphAccessException e) {
			
			LOGGER.error("Error in graph db call", e);
			throw new RefsetServiceException(e.getMessage());
		}

	}

	/**
	 * @param refsetId
	 * @param fromDate
	 * @param toDate
	 * @param from
	 * @param to
	 * @return
	 * @throws RefsetServiceException 
	 */
	public Map<String, ChangeRecord<MemberDTO>> getAllMembersStateHistory(
			String refsetId, DateTime fromDate, DateTime toDate, int from,
			int to) throws RefsetServiceException {
		
		LOGGER.debug("getAllMembersStateHistory");
		
		try {
			
			return gao.getAllMembersStateHistory(refsetId, fromDate, toDate, from, to);
			
		} catch (RefsetGraphAccessException e) {
			
			LOGGER.error("Error in graph db call", e);
			throw new RefsetServiceException(e.getMessage());
		}
	}

	/**
	 * @param refsetId
	 * @param fromDate
	 * @param toDate
	 * @param from
	 * @param to
	 * @return
	 * @throws RefsetServiceException 
	 */
	public ChangeRecord<RefsetDTO> getRefseHeaderStateHistory(String refsetId,
			DateTime fromDate, DateTime toDate, int from, int to) throws RefsetServiceException {
		
		LOGGER.debug("getRefseHeaderStateHistory");
		
		try {
			
			return gao.getRefsetHeaderStateHistory(refsetId, fromDate, toDate, from, to);
			
		} catch (RefsetGraphAccessException e) {
			
			LOGGER.error("Error in graph db call", e);
			throw new RefsetServiceException(e.getMessage());
		}
	}
	
	/**
	 * @param memberId
	 * @return
	 * @throws RefsetServiceException 
	 * @throws EntityNotFoundException 
	 */
	public ChangeRecord<MemberDTO> getMemberStateHistory(String memberId, String refsetId) throws RefsetServiceException, EntityNotFoundException {
		
		LOGGER.debug("getMemberStateHistory all");
		
		try {
			
			return gao.getMemberStateHistory(memberId, refsetId);
			
		} catch (RefsetGraphAccessException e) {
			
			LOGGER.error("Error in graph db call", e);
			throw new RefsetServiceException(e.getMessage());
		}

	}
}
