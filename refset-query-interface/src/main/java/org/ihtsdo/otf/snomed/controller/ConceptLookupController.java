/**
 * 
 */
package org.ihtsdo.otf.snomed.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.ihtsdo.otf.refset.common.Meta;
import org.ihtsdo.otf.refset.common.Result;
import org.ihtsdo.otf.refset.exception.EntityNotFoundException;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

/**
 * @author Episteme Partners
 *
 */
@RestController
@Api(value="Concept details look up service", description="Service to lookup concept details", position = 6)
@RequestMapping("/v1.0/snomed")
public class ConceptLookupController {
	
	private static final Logger logger = LoggerFactory.getLogger(ConceptLookupController.class);

	private static final String SUCESS = "Success";

	
	@Resource(name = "conceptLookService")
	private ConceptLookupService cService;
	
	@RequestMapping( method = RequestMethod.POST, value = "/concepts", produces = MediaType.APPLICATION_JSON_VALUE , 
			consumes = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation( value = "Retrieves concept details for given concept ids in good faith ie all null or empty will be ignored" )
    public ResponseEntity<Result< Map<String, Object>>> getConceptDetails( 
    		@RequestBody( required = true) Set<String> conceptIds ) throws Exception {
		
		logger.debug("getConceptDetails");

		Result<Map<String, Object>> response = new Result<Map<String, Object>>();
		Meta m = new Meta();
		m.add( linkTo( methodOn( ConceptLookupController.class ).getConceptDetails( conceptIds) ).withSelfRel() );
		
		response.setMeta(m);


		
		Map<String, Concept> cs =  cService.getConcepts(conceptIds );

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("concepts", cs);
		response.setData(data);
		
		m.setMessage(SUCESS);
		m.setStatus(HttpStatus.OK);

		return new ResponseEntity<Result<Map<String,Object>>>(response, HttpStatus.OK);
		
	
         
    }
	
	@RequestMapping( method = RequestMethod.GET, value = "/concepts/{conceptId}", produces = "application/json" )
	@ApiOperation( value = "Api to get details of a concept for given concept id." )
    public ResponseEntity<Result< Map<String, Object>>> getConcept( @PathVariable( value = "conceptId") String conceptId ) throws Exception {
		
		logger.debug("Getting concept details for {}", conceptId);

		Result<Map<String, Object>> response = new Result<Map<String, Object>>();
		
		Meta m = new Meta();

		m.add( linkTo( methodOn( ConceptLookupController.class ).getConcept( conceptId ) ).withSelfRel() );
		response.setMeta(m);


		
		Concept c =  cService.getConcept(conceptId);

		if (c == null) {
			
			throw new EntityNotFoundException("No data available for given id " + conceptId);
			
		}
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("concept", c);
		
		response.setData(data);
		m.setMessage(SUCESS);
		m.setStatus(HttpStatus.OK);

		return new ResponseEntity<Result<Map<String,Object>>>(response, HttpStatus.OK);
		
	     
    }
	
	
	@RequestMapping( method = RequestMethod.GET, value = "/concepts", produces = "application/json" )
	@ApiOperation( value = "Api to get paged list of concept ids available in the systems." )
    public ResponseEntity<Result< Map<String, Object>>> getConceptIds( @RequestParam( value = "page", defaultValue = "0", required = false) int page, 
    		@RequestParam( value = "size", defaultValue = "10", required = false) int size) throws Exception {
		
		logger.debug("Getting concept id list with offset as {} and limit as {}", page, size);

		Result<Map<String, Object>> response = new Result<Map<String, Object>>();
		
		Meta m = new Meta();

		m.add( linkTo( methodOn( ConceptLookupController.class ).getConceptIds( page, size ) ).withSelfRel() );
		response.setMeta(m);


		
		Set<String> ids =  cService.getConceptIds(page, size);

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("ids", ids);
		
		response.setData(data);
		m.setMessage(SUCESS);
		m.setStatus(HttpStatus.OK);

		return new ResponseEntity<Result<Map<String,Object>>>(response, HttpStatus.OK);
		
	      
    }
	
}
