/**
 * 
 */
package org.ihtsdo.otf.refset.service.search;

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
	 * @param from
	 * @param to
	 * @param query
	 * @return
	 * @throws RefsetServiceException
	 */
	public SearchResult<String> getSearchResult(String query, int from, int to) throws RefsetServiceException {

		LOGGER.debug("getSearchResult {}", query);
		
		try {
			
			return gao.getSearchResult(query, from, to);
			
		} catch (RefsetGraphAccessException e) {
			
			LOGGER.error("Error in search service call", e);
			throw new RefsetServiceException(e.getMessage());
		}
				
	}
	
	/**
	 * @param from
	 * @param to
	 * @param query
	 * @return
	 * @throws RefsetServiceException
	 */
	public SearchResult<String> searchAll(String query, int from, int to) throws RefsetServiceException {

		LOGGER.debug("getSearchResult {}", query);
		
		return gao.searchAll(query, from, to);

				
	}

}
