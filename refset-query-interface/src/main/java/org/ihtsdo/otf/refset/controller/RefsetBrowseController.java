/**
 * 
 */
package org.ihtsdo.otf.refset.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.ihtsdo.otf.refset.common.Meta;
import org.ihtsdo.otf.refset.common.Result;
import org.ihtsdo.otf.refset.domain.Refset;
import org.ihtsdo.otf.refset.exception.EntityNotFoundException;
import org.ihtsdo.otf.refset.exception.RefsetServiceException;
import org.ihtsdo.otf.refset.service.RefsetBrowseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
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
@Api(value="Refset retrieval", description="Service to retrieve existing refset and their member details" , position = 2)
@RequestMapping("/v1.0/refsets")
public class RefsetBrowseController {
	
	private static final Logger logger = LoggerFactory.getLogger(RefsetBrowseController.class);

	private static final String SUCESS = "Success";

	
	@Resource(name = "browseGraphService")
	private RefsetBrowseService bService;
	
	@RequestMapping( method = RequestMethod.GET, produces = "application/json" )
	@ApiOperation( value = "Retrieves list of existing refsets. By default it returns 10 refset and theirafter another 10 or desired page size" )
    public ResponseEntity<Result< Map<String, Object>>> getRefsets( @RequestParam( value = "page", defaultValue = "1" ) int page, 
    		@RequestParam( value = "size", defaultValue = "10" ) int size ) {
		
		logger.debug("Existing refsets");

		Result<Map<String, Object>> response = new Result<Map<String, Object>>();
		Meta m = new Meta();
		m.add( linkTo( methodOn( RefsetBrowseController.class ).getRefsets( page, size ) ).withSelfRel() );
		
		response.setMeta(m);

    	try {
    		boolean published = false;
    		
    		if (SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken) {
    			
    			published = true;
    			logger.debug("Geting only published ? = {}", published);
    			
    		}
			List<Refset> refSets =  bService.getRefsets( page, size, published );

			Map<String, Object> data = new HashMap<String, Object>();
			data.put("refsets", refSets);
			m.setNoOfRecords(refSets.size());
			response.setData(data);
			
			m.setMessage(SUCESS);
			m.setStatus(HttpStatus.OK);

			return new ResponseEntity<Result<Map<String,Object>>>(response, HttpStatus.OK);
			
		} catch (RefsetServiceException e) {
			
			// TODO Filter error. Only Pass what is required
			String message = String.format("Error occurred during service call : %s", e.getMessage());
			logger.error(message);
			m.setMessage(message); 
			m.setStatus(HttpStatus.OK);

			return new ResponseEntity<Result<Map<String,Object>>>(response, HttpStatus.OK);
	    	
		}         
    }
	
	@RequestMapping( method = RequestMethod.GET, value = "/{refSetId}", produces = "application/json" )
	@ApiOperation( value = "Api to get details of a refset for given refset id." )
    public ResponseEntity<Result< Map<String, Object>>> getRefsetDetails( @PathVariable( value = "refSetId" ) String refSetId ) {
		
		logger.debug("Getting refset details");

		Result<Map<String, Object>> response = new Result<Map<String, Object>>();
		
		Meta m = new Meta();

		m.add( linkTo( methodOn( RefsetBrowseController.class, refSetId).getRefsetDetails(refSetId) ).withSelfRel() );
		response.setMeta(m);

    	try {
    		
			Refset refSet =  bService.getRefset(refSetId);

    		if (SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken && !refSet.isPublished()) {
    			
    			return new ResponseEntity<Result<Map<String,Object>>>(response, HttpStatus.FORBIDDEN);

    		}
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("refset", refSet);
			
			response.setData(data);
			m.setMessage(SUCESS);
			m.setStatus(HttpStatus.OK);
			
			return new ResponseEntity<Result<Map<String,Object>>>(response, HttpStatus.OK);
			
		} catch (RefsetServiceException e) {
			
			String message = String.format("Error occurred during service call : %s", e.getMessage());
			logger.error(message);
			m.setMessage(message); 
			m.setStatus(HttpStatus.OK);

			return new ResponseEntity<Result<Map<String,Object>>>(response, HttpStatus.OK);
	    	
		} catch (EntityNotFoundException e) {
			
			String message = String.format("Error occurred during service call : %s", e.getMessage());
			logger.error(message);
			m.setMessage(message); 
			m.setStatus(HttpStatus.NOT_FOUND);
			
			return new ResponseEntity<Result<Map<String,Object>>>(response, HttpStatus.NOT_FOUND);

		}       
    }
	
}
