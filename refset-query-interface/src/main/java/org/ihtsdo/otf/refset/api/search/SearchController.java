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
package org.ihtsdo.otf.refset.api.search;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.HashMap;
import java.util.Map;

import org.ihtsdo.otf.refset.common.Result;
import org.ihtsdo.otf.refset.common.Utility;
import org.ihtsdo.otf.refset.domain.SearchResult;
import org.ihtsdo.otf.refset.service.search.RefsetSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

/**
 *
 */
@RestController
@Api(value="RefsetSearch", description="Service to search members for desired search criteria")
@RequestMapping("/v1.0/refsets")
public class SearchController {
	
	private static final Logger logger = LoggerFactory.getLogger(SearchController.class);

	private static final String SUCCESS = "Success";
	

	@Autowired
	private RefsetSearchService service;
	
	@RequestMapping( method = RequestMethod.GET, value = "/search",  produces = "application/json", consumes = "application/json")
	@ApiOperation( value = "Get a member history ",
			notes = "This api searches desired text and returns a paginated list of refset uuid")
	@PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Result< Map<String, Object>>> getSearchResults( @RequestParam(value = "q") String query,
    		@RequestParam(required = false, defaultValue = "0") int from, @RequestParam(required = false, defaultValue = "10") int to) throws Exception {
		
		logger.debug("Searching for {}", query);

		Result<Map<String, Object>> r = Utility.getResult();

		SearchResult<String> result = service.getSearchResult(query, from, to);
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("result", result);
		r.getMeta().add( linkTo( methodOn( SearchController.class ).getSearchResults(query, from, to)).withRel("Search Result"));

		r.setData(data);

		r.getMeta().setMessage(SUCCESS);
		r.getMeta().setStatus(HttpStatus.OK);

		return new ResponseEntity<Result<Map<String,Object>>>(r, HttpStatus.OK);
	}

}
