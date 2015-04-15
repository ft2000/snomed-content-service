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
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.ihtsdo.otf.refset.common.Result;
import org.ihtsdo.otf.refset.common.SearchCriteria;
import org.ihtsdo.otf.refset.common.SearchCriteriaDTO;
import org.ihtsdo.otf.refset.common.SearchField;
import org.ihtsdo.otf.refset.common.Utility;
import org.ihtsdo.otf.refset.domain.RefsetDTO;
import org.ihtsdo.otf.refset.domain.SearchCriteriaValidator;
import org.ihtsdo.otf.refset.domain.SearchResult;
import org.ihtsdo.otf.refset.exception.ValidationException;
import org.ihtsdo.otf.refset.service.browse.RefsetBrowseService;
import org.ihtsdo.otf.refset.service.search.RefsetSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestBody;
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
@Api(value="Search", description="Service to search members for desired search criteria")
@RequestMapping("/v1/refsets")
public class SearchController {
	
	private static final Logger logger = LoggerFactory.getLogger(SearchController.class);
	

	@Autowired
	private RefsetSearchService service;
	
	@Resource(name = "browseGraphService")
	private RefsetBrowseService bService;

	
	@Autowired
	private SearchCriteriaValidator validator;
	
	@RequestMapping( method = RequestMethod.GET, value = "/search",  produces = "application/json", consumes = "application/json")
	@ApiOperation( value = "Retrieves refset uuids for given search query ",
			notes = "This api searches desired text and returns a paginated list of refset uuid")
    public ResponseEntity<Result< Map<String, Object>>> getSearchResults( @RequestParam(value = "q") String query,
    		@RequestParam(required = false, defaultValue = "0") int from, @RequestParam(required = false, defaultValue = "10") int to) throws Exception {
		
		logger.debug("Searching for {}", query);


		SearchResult<String> result = service.getSearchResult(query, from, to);
		

		Result<Map<String, Object>> r = Utility.getResult();
		r.getData().put("result", result);
		r.getMeta().add( linkTo( methodOn( SearchController.class ).getSearchResults(query, from, to)).withRel("Search Result"));

		return new ResponseEntity<Result<Map<String,Object>>>(r, HttpStatus.OK);
	}
	
	@RequestMapping( method = RequestMethod.GET, value = "/searchAll",  produces = "application/json", consumes = "application/json")
	@ApiOperation( value = "Retrieves refset uuids for given search query. ",
			notes = "This api searches desired text and returns a paginated list of refset uuid. Search is performed against all indexed fields")
    public ResponseEntity<Result< Map<String, Object>>> searchAll( @RequestParam(value = "q") String query,
    		@RequestParam(required = false, defaultValue = "0") int from, @RequestParam(required = false, defaultValue = "10") int to) throws Exception {
		
		logger.debug("Searching for {}", query);


		SearchResult<String> result = service.searchAll(query, from, to);
		
		Result<Map<String, Object>> r = Utility.getResult();
		r.getData().put("result", result);
		r.getMeta().add( linkTo( methodOn( SearchController.class ).searchAll(query, from, to)).withRel("Search Result"));

		return new ResponseEntity<Result<Map<String,Object>>>(r, HttpStatus.OK);
	}
	
	@RequestMapping( method = RequestMethod.POST, value = "/searchRefsets",  produces = "application/json", consumes = "application/json")
	@ApiOperation( value = "Retrieves refsets for given search criteria ",
			notes = "This api searches desired text and returns a paginated list of refset's header")
    public ResponseEntity<Result< Map<String, Object>>> searchRefsets( @RequestBody SearchCriteriaDTO criteria, 
    		 @RequestParam(required = false, defaultValue = "0") int from, @RequestParam(required = false, defaultValue = "10") int to) throws Exception {
		
		logger.debug("Searching for {}", criteria);

		validateCriteria(criteria);
		
		SearchCriteria sc = new SearchCriteria();
		sc.setFrom(from);
		sc.setTo(to);
		
		Set<String> keys = criteria.getFields().keySet();
		for (String key : keys) {
			
			SearchField f = SearchField.valueOf(key);
			
			sc.addSearchField(f, criteria.getFields().get(key));
			
		}
		List<RefsetDTO> refsets = service.getRefsetsSearchCriteria(sc);
		
		
		Result<Map<String, Object>> result = Utility.getResult();

		result.getData().put("refsets", refsets);
		Long totalNoOfRefsets = bService.totalNoOfRefset(sc);
		
		result.getData().put("totalNoOfRefsets", totalNoOfRefsets);
		result.getMeta().add( linkTo( methodOn( SearchController.class ).searchRefsets(criteria, from, to)).withRel("Search Result"));


		return new ResponseEntity<Result<Map<String,Object>>>(result, HttpStatus.OK);
	}
	
	private void validateCriteria(SearchCriteriaDTO dto) throws ValidationException {
		
		Map<Object, List<FieldError>> errors = new HashMap<Object, List<FieldError>>();

		Errors e = new BeanPropertyBindingResult(dto, "criteria");
		validator.validate(dto, e );
		
		if (e.hasErrors()) {
			
			errors.put(dto, e.getFieldErrors());
		}

		if (!errors.isEmpty()) {
			
			throw new ValidationException(errors);
		}
		
	}

}
