/**
* Copyright 2014 IHTSDO
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.ihtsdo.otf.refset.graph.gao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.ihtsdo.otf.refset.domain.SearchResult;
import org.ihtsdo.otf.refset.graph.RefsetGraphAccessException;
import org.ihtsdo.otf.refset.graph.RefsetGraphFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;

/**class retrieves search results for given text search criteria. At the moment it is very basic but after demo it will be replaced 
 * by code using either elastic search java client or spring-es  
 *
 */
@Repository
public class SearchGao {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SearchGao.class);
	private static final String QUERY = "/refset/Member/_search?q=referenceComponentId,title %s&from=%s&size=%s";
	private RefsetGraphFactory f;
	private String url;
	private RestTemplate rt;

	
	@Resource(name = "searchServerUrl")
	public void setUrl(String url) {

		this.url = url;
	}
	

	/**
	 * @param rt the rt to set
	 */
	@Resource(name = "refset.auth.RestTemplate")
	public void setRt(RestTemplate rt) {
		this.rt = rt;
	}

	/**
	 * @param factory the factory to set
	 */
	@Resource(name = "refsetGraphFactory")
	public  void setFactory(RefsetGraphFactory factory) {
		
		this.f = factory;
	}
	
	/**
	 * @param refsetId
	 * @param from
	 * @param to
	 * @return
	 * @throws RefsetGraphAccessException
	 */
	public SearchResult<String> getSearchResult(String query, Integer from, Integer to) throws RefsetGraphAccessException {


		SearchResult<String> result = new SearchResult<String>();

		//TitanGraph g = null;
		
		try {
			
			//g = f.getReadOnlyGraph();
			String queryString = String.format( url + QUERY, query, from, to - from);
			LOGGER.debug("Getting getting search result for {}", queryString);

			JsonNode response = rt.getForObject(queryString, JsonNode.class);
			
			LOGGER.debug("search service call successfully returned with {} ", response);
			
			Iterator<String> fields = response.fieldNames();
			while (fields.hasNext()) {
				String field = fields.next();
				if("took".equalsIgnoreCase(field)) {
					
					result.setTime(response.get(field).asInt());
				}
				
				if ("hits".equalsIgnoreCase(field)) {
					
					Iterator<String> hitsFields = response.get(field).fieldNames();
					
					while (hitsFields.hasNext()) {

						String hitsField = hitsFields.next();
						
						if("total".equalsIgnoreCase(hitsField)) {
							
							result.setTotalNoOfResults(response.get(field).get(hitsField).asInt());
						}

						if("hits".equalsIgnoreCase(hitsField)) {

							Iterator<JsonNode> source = response.get(field).get(hitsField).elements();
							List<String> r = new ArrayList<String>();
							
							while (source.hasNext()) {
								
								String uuid = source.next().at("/_source/parentId").asText();
								if(!StringUtils.isEmpty(uuid) && !r.contains(uuid)) {

									r.add(uuid);
								}
								
							}

							//for now override total with number of records.TODO need something else 
							result.setTotalNoOfResults(r.size());
							result.setRecords(r);

						}

					}
				}

			}
			

		} catch (Exception e) {
			
			//RefsetGraphFactory.rollback(g);			
			LOGGER.error("Error getting refsets member history", e);
			throw new RefsetGraphAccessException(e.getMessage(), e);
			
		}

		return result;
	}
	
	

}
