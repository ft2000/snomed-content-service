/**
 * 
 */
package org.ihtsdo.otf.refset.controller;

import java.util.Map;

import javax.annotation.Resource;

import org.ihtsdo.otf.refset.service.RefsetQueryException;
import org.ihtsdo.otf.refset.service.RefsetQueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.mangofactory.swagger.annotations.ApiIgnore;

/**
 * @author Episteme Partners
 *
 */
@Controller
@ApiIgnore
public class ReleaseServiceController {
	
	private static final Logger logger = LoggerFactory.getLogger(ReleaseServiceController.class);
	
	private static final String OUTPUT_TYPE = "json";
	private static final String MODULE_QUERY = "module.query";

	private static final String RELEASE_QUERY = "release.query";;
	
	@Resource(name = "sparql.queries")
	private Map<String, String> qMap;
	
	@Resource(name = "fusekiRefsetQueryService")
	private RefsetQueryService qService;
	
	@RequestMapping(method=RequestMethod.GET, value="/module/{moduleid}")
	@ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<String> getModuleDetails(@PathVariable String moduleid, @PathVariable String releaseid, @PathVariable String sctid ) {

		String query = String.format(qMap.get(MODULE_QUERY), moduleid);
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
	
	@RequestMapping(method=RequestMethod.GET, value="/module/{moduleid}/release/{releaseid}")
	@ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<String> getReleaseDetails(@PathVariable String moduleid, @PathVariable String releaseid, @PathVariable String sctid ) {

		String query = String.format(qMap.get(RELEASE_QUERY), moduleid, releaseid);
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
