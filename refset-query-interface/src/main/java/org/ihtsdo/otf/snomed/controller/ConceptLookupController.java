/**
 * 
 */
package org.ihtsdo.otf.snomed.controller;

import static org.ihtsdo.otf.refset.common.Utility.getResult;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.ihtsdo.otf.refset.common.Result;
import org.ihtsdo.otf.snomed.domain.Concept;
import org.ihtsdo.otf.snomed.service.ConceptLookupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

/**
 *
 */
@RestController
@Api(value="SNOMED", description="Service to lookup concept details")
@RequestMapping("/v1.0/snomed")
public class ConceptLookupController {
	
	private static final Logger logger = LoggerFactory.getLogger(ConceptLookupController.class);
	
	@Resource(name = "conceptLookService")
	private ConceptLookupService cService;
	
	@RequestMapping( method = RequestMethod.POST, value = "/concepts", produces = MediaType.APPLICATION_JSON_VALUE , 
			consumes = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation( value = "Retrieves concept details for given concept ids", notes = "Retrieves concept details for given concept ids " )
    public ResponseEntity<Result< Map<String, Object>>> getConceptDetails( 
    		@RequestBody( required = true) Set<String> conceptIds ) throws Exception {
		
		logger.debug("getConceptDetails");

		Result<Map<String, Object>> response = getResult();
		response.getMeta().add( linkTo( methodOn( ConceptLookupController.class ).getConceptDetails( conceptIds) ).withSelfRel() );

		
		Map<String, Concept> cs =  cService.getConcepts(conceptIds);

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("concepts", cs);
		response.setData(data);
		
		return new ResponseEntity<Result<Map<String,Object>>>(response, HttpStatus.OK);
		
	
         
    }
	
	@RequestMapping( method = RequestMethod.GET, value = "/concepts/{conceptId}", produces = "application/json" )
	@ApiOperation( value = "Api to get details of a concept for given concept id.", notes = "Api to get details of a concept for given concept id" )
    public ResponseEntity<Result< Map<String, Object>>> getConcept( @PathVariable( value = "conceptId") String conceptId ) throws Exception {
		
		logger.debug("Getting concept details for {}", conceptId);

		Result<Map<String, Object>> response = getResult();
		
		response.getMeta().add( linkTo( methodOn( ConceptLookupController.class ).getConcept( conceptId ) ).withSelfRel() );
		
		Concept c =  cService.getConcept(conceptId);

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("concept", c);
		response.setData(data);

		return new ResponseEntity<Result<Map<String,Object>>>(response, HttpStatus.OK);
		
	     
    }
	
}
