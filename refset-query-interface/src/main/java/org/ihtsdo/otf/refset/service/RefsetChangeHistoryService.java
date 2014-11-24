/**
 * 
 */
package org.ihtsdo.otf.refset.service;

import java.util.Map;

import org.ihtsdo.otf.refset.domain.ChangeRecord;
import org.ihtsdo.otf.refset.domain.Member;
import org.ihtsdo.otf.refset.domain.Refset;
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
	 * @param from
	 * @param to
	 * @return
	 * @throws RefsetServiceException
	 */
	public ChangeRecord<Member> getMemberHistory(String refsetId, String referenceComponentId, DateTime fromDate, DateTime toDate, int from, int to) throws RefsetServiceException {

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
	 * @param from
	 * @param to
	 * @return
	 * @throws RefsetServiceException
	 */
	public Map<String, ChangeRecord<Member>> getAllMembersHistory(String refsetId, DateTime fromDate, DateTime toDate, int from, int to) throws RefsetServiceException {

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
	 * @param from
	 * @param to
	 * @return
	 * @throws RefsetServiceException
	 */
	public ChangeRecord<Refset> getRefsetHeaderHistory(String refsetId, DateTime fromDate, DateTime toDate, int from, int to) throws RefsetServiceException {

		LOGGER.debug("getRefsetHeaderHistory");
		
		try {
			
			return gao.getRefsetHeaderHistory(refsetId, fromDate, toDate, from, to);
			
		} catch (RefsetGraphAccessException e) {
			
			LOGGER.error("Error in graph db call", e);
			throw new RefsetServiceException(e.getMessage());
		}
				
	}
}
