/**
 * 
 */
package org.ihtsdo.otf.refset.service;

import org.ihtsdo.otf.refset.domain.BaseObj;
import org.ihtsdo.otf.refset.domain.SearchResult;
import org.ihtsdo.otf.refset.exception.RefsetServiceException;
import org.ihtsdo.otf.refset.graph.RefsetGraphAccessException;
import org.ihtsdo.otf.refset.graph.gao.SearchGao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 
 *
 */
@Service(value = "searchService")
public class RefsetSearchService {

	private static final Logger LOGGER = LoggerFactory.getLogger(RefsetSearchService.class);
	
	@Autowired
	private SearchGao gao;


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
	public SearchResult<BaseObj> getSearchResult(String query, int from, int to) throws RefsetServiceException {

		LOGGER.debug("getSearchResult {}", query);
		
		try {
			
			return gao.getSearchResult(query, from, to);
			
		} catch (RefsetGraphAccessException e) {
			
			LOGGER.error("Error in graph db call", e);
			throw new RefsetServiceException(e.getMessage());
		}
				
	}

}
