/**
 * 
 */
package org.ihtsdo.otf.refset.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import org.ihtsdo.otf.refset.common.Meta;
import org.ihtsdo.otf.refset.common.Response;
import org.ihtsdo.otf.refset.domain.Member;
import org.ihtsdo.otf.refset.domain.Refset;
import org.ihtsdo.otf.refset.exception.EntityNotFoundException;
import org.ihtsdo.otf.refset.exception.RefsetServiceException;
import org.ihtsdo.otf.refset.service.RefsetAuthoringService;
import org.ihtsdo.otf.refset.service.RefsetBrowseService;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

/**
 * @author Episteme Partners
 *
 */
@RestController
@Api(value="RefsetAuthoring", description="Service to author refset and their member details")
@RequestMapping("/v1.0/refsets")
public class RefsetAuthoringController {
	
	private static final Logger logger = LoggerFactory.getLogger(RefsetAuthoringController.class);

	private static final String SUCESS = "Success";
	
	@Resource(name = "browseGraphService")
	private RefsetBrowseService bService;

	@Resource
	private RefsetAuthoringService aService;

	@RequestMapping( method = RequestMethod.POST, value = "/new",  produces = "application/json")
	@ApiOperation( value = "Add a Refset) " )
    public ResponseEntity<Response< Map<String, Object>>> addRefset( @RequestBody Refset r) {
		
		logger.debug("Adding refsets {}", r);

		Response<Map<String, Object>> response = new Response<Map<String, Object>>();
		Meta m = new Meta();
		
		response.setMeta(m);

    	try {
    		
    		addMetaDetails(r);
    		aService.addRefset(r);
			

			Map<String, Object> data = new HashMap<String, Object>();
			data.put("id", r.getId());
    		m.add( linkTo( methodOn( RefsetBrowseController.class ).getRefsetDetails(r.getId())).withRel("Refset"));

			response.setData(data);

			m.setMessage(SUCESS);
			m.setStatus(HttpStatus.CREATED);

			return new ResponseEntity<Response<Map<String,Object>>>(response, HttpStatus.CREATED);
			
		} catch (RefsetServiceException e) {
			
			// TODO Filter error. Only Pass what is required
			String message = String.format("Error occurred during service call : %s", e.getMessage());
			logger.error(message);
			m.setMessage(message); 
			m.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);

			return new ResponseEntity<Response<Map<String,Object>>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	    	
		}         
    }
	
	/**Adds meta details in new {@link Refset}
	 * @param r
	 */
	private void addMetaDetails(Refset r) {
		// TODO Auto-generated method stub
		String id = UUID.randomUUID().toString();
		r.setId(id);
		r.setCreated(new DateTime().toString());
		r.setCreatedBy("Refset Author");
		r.setModuleId("900000000000012000");
	}

	@RequestMapping( method = RequestMethod.POST, value = "/{refSetId}/add/member", produces = "application/json" )
	@ApiOperation( value = "Shortcut method to add a member to an existing refset." )
    public ResponseEntity<Response< Map<String, Object>>> addMember(@PathVariable(value = "refSetId") String refsetId, 
    		@RequestBody( required = true) Member member) {
		
		logger.debug(String.format("Getting refset details"));

		Response<Map<String, Object>> response = new Response<Map<String, Object>>();
		
		Meta m = new Meta();

    	try {
    		
			aService.addMember(refsetId, member);
			
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("id", refsetId);
    		m.add( linkTo( methodOn( RefsetBrowseController.class ).getRefsetDetails(refsetId)).withRel("Refset"));

			response.setData(data);
			
			m.setMessage(SUCESS);
			m.setStatus(HttpStatus.OK);
			
			return new ResponseEntity<Response<Map<String,Object>>>(response, HttpStatus.OK);
			
		} catch (RefsetServiceException e) {
			
			String message = String.format("Error occurred during service call : %s", e.getMessage());
			logger.error(message);
			m.setMessage(message); 
			m.setStatus(HttpStatus.OK);

			return new ResponseEntity<Response<Map<String,Object>>>(response, HttpStatus.OK);
	    	
		}       
    }
	
	@RequestMapping( method = RequestMethod.POST, value = "/update",  produces = "application/json")
	@ApiOperation( value = "Update a Refset" )
    public ResponseEntity<Response< Map<String, Object>>> updateRefset( @RequestBody Refset r) {
		
		logger.debug(String.format("Existing refsets"));

		Response<Map<String, Object>> response = new Response<Map<String, Object>>();
		Meta m = new Meta();
		
		response.setMeta(m);

    	try {

    		aService.updateRefset(r);
			

			Map<String, Object> data = new HashMap<String, Object>();
			data.put("id", r.getId());
    		m.add( linkTo( methodOn( RefsetBrowseController.class ).getRefsetDetails(r.getId())).withRel("Refset"));

			response.setData(data);

			m.setMessage(SUCESS);
			m.setStatus(HttpStatus.OK);

			return new ResponseEntity<Response<Map<String,Object>>>(response, HttpStatus.OK);
			
		} catch (RefsetServiceException e) {
			
			// TODO Filter error. Only Pass what is required
			String message = String.format("Error occurred during service call : %s", e.getMessage());
			logger.error(message);
			m.setMessage(message); 
			m.setStatus(HttpStatus.OK);

			return new ResponseEntity<Response<Map<String,Object>>>(response, HttpStatus.OK);
	    	
		} catch (EntityNotFoundException e) {
			
			String message = String.format("Error occurred during service call : %s", e.getMessage());
			logger.error(message);
			m.setMessage(message); 
			m.setStatus(HttpStatus.NOT_FOUND);

			return new ResponseEntity<Response<Map<String,Object>>>(response, HttpStatus.NOT_FOUND);
		}         
    }
	
	

}
