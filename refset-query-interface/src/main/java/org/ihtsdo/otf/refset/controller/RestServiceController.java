/**
 * 
 */
package org.ihtsdo.otf.refset.controller;

import java.util.Map;

import javax.annotation.Resource;

import org.ihtsdo.otf.refset.common.UriFormatter;
import org.ihtsdo.otf.refset.service.RefsetQueryException;
import org.ihtsdo.otf.refset.service.RefsetQueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.mangofactory.swagger.annotations.ApiIgnore;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

/**
 * @author Episteme Partners
 *
 */
@RestController
@ApiIgnore
@Api(value="SNOMED Data Service", description="Service to read SNOMED data to create refset")
@RequestMapping("/module/{moduleid}/release/{releaseid}")
public class RestServiceController {
	
	private static final Logger logger = LoggerFactory.getLogger(RestServiceController.class);
	
	private static final String OUTPUT_TYPE = "json";
	private static final String CONCEPT_QUERY = "concept.query";

	private static final String EFFECTIVE_DATE_QUERY = "effective.date.query";;

	private static final String STATUS_QUERY = "status.query";
	
	private static final String NAMED_GRAPH_KEYWORD = "FROM NAMED";
	
	@Resource(name = "sparql.queries")
	private Map<String, String> qMap;
		
	@Resource(name = "fusekiRefsetQueryService")
	private RefsetQueryService qService;
	
	@RequestMapping(method=RequestMethod.GET, value="/{sctid}", produces = "application/json")
	@ResponseStatus(value = HttpStatus.OK)
	@ApiOperation(value = "Retrieves details of concept for given sct id")
    public ResponseEntity<String> getItemDetails(
    		@ApiParam(name="moduleid", value="The id of a module", required=true) @PathVariable String moduleid, 
    		@ApiParam(name="releaseid", value="The id of a release e.g. 20120131", required=true) @PathVariable String releaseid, 
    		@ApiParam(name="sctid", value="The id of a concept", required=true) @PathVariable String sctid ) {

		String query = String.format(qMap.get(CONCEPT_QUERY), NAMED_GRAPH_KEYWORD + UriFormatter.getNamedGraphUri(moduleid, releaseid),
				UriFormatter.getConceptUri(sctid));
		logger.debug(String.format("Executing Query %s", query));

    	try {
    		
			String details =  qService.executeQuery(query, OUTPUT_TYPE);
			
			return new ResponseEntity<String>(details, HttpStatus.OK);
			
		} catch (RefsetQueryException e) {
			
			// TODO Auto-generated catch block
			String message = String.format("Error occurred during query :\n %s ", e.getMessage());
			logger.error(message);
			return new ResponseEntity<String>(message, HttpStatus.INTERNAL_SERVER_ERROR); //need further refinement
	    	
		}         
    }
	
	@RequestMapping(method=RequestMethod.GET, value="/{sctid}/status", produces = "application/json")
	@ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<String> getStatus(@PathVariable String moduleid, @PathVariable String releaseid, @PathVariable String sctid ) {

		String query = String.format(qMap.get(STATUS_QUERY), NAMED_GRAPH_KEYWORD + UriFormatter.getNamedGraphUri(moduleid, releaseid),
				UriFormatter.getConceptUri(sctid));

		logger.debug(String.format("Executing Query %s", query));

    	try {
    		
			String details =  qService.executeQuery(query, OUTPUT_TYPE);
			
			return new ResponseEntity<String>(details, HttpStatus.OK);
			
		} catch (RefsetQueryException e) {
			
			// TODO Auto-generated catch block
			String message = String.format("Error occurred during query :\n %s ", e.getMessage());
			logger.error(message);
			return new ResponseEntity<String>(message, HttpStatus.INTERNAL_SERVER_ERROR); //need further refinement
	    	
		}         
    }
	
	@RequestMapping(method=RequestMethod.GET, value="/{sctid}/effectivedate", produces = "application/json")
	@ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<String> getEffectiveDate(@PathVariable String moduleid, @PathVariable String releaseid, @PathVariable String sctid ) {

		String query = String.format(qMap.get(EFFECTIVE_DATE_QUERY), NAMED_GRAPH_KEYWORD + UriFormatter.getNamedGraphUri(moduleid, releaseid),
				UriFormatter.getConceptUri(sctid));
		logger.debug(String.format("Executing Query %s", query));

    	try {
    		
			String details =  qService.executeQuery(query, OUTPUT_TYPE);
			
			return new ResponseEntity<String>(details, HttpStatus.OK);
			
		} catch (RefsetQueryException e) {
			
			// TODO Auto-generated catch block
			String message = String.format("Error occurred during query :\n %s ", e.getMessage());
			logger.error(message);
			return new ResponseEntity<String>(message, HttpStatus.INTERNAL_SERVER_ERROR); //need further refinement
	    	
		}         
    }
	
	

}
