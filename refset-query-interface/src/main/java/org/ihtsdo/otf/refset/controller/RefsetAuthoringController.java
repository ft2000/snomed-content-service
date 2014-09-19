/**
 * 
 */
package org.ihtsdo.otf.refset.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Resource;

import org.ihtsdo.otf.refset.common.Meta;
import org.ihtsdo.otf.refset.common.Result;
import org.ihtsdo.otf.refset.domain.Member;
import org.ihtsdo.otf.refset.domain.Refset;
import org.ihtsdo.otf.refset.domain.RefsetType;
import org.ihtsdo.otf.refset.exception.EntityNotFoundException;
import org.ihtsdo.otf.refset.exception.RefsetServiceException;
import org.ihtsdo.otf.refset.service.RefsetAuthoringService;
import org.ihtsdo.otf.refset.service.RefsetBrowseService;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
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
@Api(value="RefsetAuthoring", description="Service to author refset and their member details", position = 1)
@RequestMapping("/v1.0/refsets")
public class RefsetAuthoringController {
	
	private static final Logger logger = LoggerFactory.getLogger(RefsetAuthoringController.class);

	private static final String SUCESS = "Success";
	
	@Resource(name = "browseGraphService")
	private RefsetBrowseService bService;

	@Resource
	private RefsetAuthoringService aService;

	@RequestMapping( method = RequestMethod.POST, value = "/new",  produces = "application/json", consumes = "application/json")
	@ApiOperation( value = "Add a Refset) " )
	@PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Result< Map<String, Object>>> addRefset( @RequestBody Refset r, 
    		@RequestHeader("X-REFSET-PRE-AUTH-TOKEN") String authToken, 
    		@RequestHeader("X-REFSET-PRE-AUTH-USERNAME") String userName) {
		
		logger.debug("Adding refsets {}", r);

		Result<Map<String, Object>> result = new Result<Map<String, Object>>();
		Meta m = new Meta();
		
		result.setMeta(m);

    	try {
    		
    		addMetaDetails(r);
    		aService.addRefset(r);
			

			Map<String, Object> data = new HashMap<String, Object>();
			data.put("id", r.getId());
    		m.add( linkTo( methodOn( RefsetBrowseController.class ).getRefsetDetails(r.getId())).withRel("Refset"));

			result.setData(data);

			m.setMessage(SUCESS);
			m.setStatus(HttpStatus.CREATED);

			return new ResponseEntity<Result<Map<String,Object>>>(result, HttpStatus.CREATED);
			
		} catch (RefsetServiceException e) {
			
			// TODO Filter error. Only Pass what is required
			String message = String.format("Error occurred during service call : %s", e.getMessage());
			logger.error(message);
			m.setMessage(message); 
			m.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);

			return new ResponseEntity<Result<Map<String,Object>>>(result, HttpStatus.INTERNAL_SERVER_ERROR);
	    	
		}         
    }
	
	/**Adds meta details in new {@link Refset}
	 * @param r
	 */
	private void addMetaDetails(Refset r) {
		
		// TODO Auto-generated method stub
		String id = UUID.randomUUID().toString();
		r.setId(id);
		r.setCreated(new DateTime());
		
		if(StringUtils.isEmpty(r.getType()))
			r.setType(RefsetType.simple);
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if (auth instanceof UserDetails) {
			
			UserDetails user = (UserDetails) auth.getPrincipal();
			r.setCreatedBy(user.getUsername());
			
		}
		
		
		r.setActive(r.isActive());
		if(StringUtils.isEmpty(r.getModuleId()))
			r.setModuleId("900000000000012000");//TODO need appropriate input from front end
		
		
	}

	@RequestMapping( method = RequestMethod.POST, value = "/{refSetId}/add/member", produces = "application/json", consumes = "application/json" )
	@ApiOperation( value = "Shortcut method to add a member to an existing refset." )
	@PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Result< Map<String, Object>>> addMember(@PathVariable(value = "refSetId") String refsetId, 
    		@RequestBody( required = true) Member member, 
    		@RequestHeader("X-REFSET-PRE-AUTH-TOKEN") String authToken, 
    		@RequestHeader("X-REFSET-PRE-AUTH-USERNAME") String userName) {
		
		logger.debug("Adding member {} to refset {}", member, refsetId);

		Result<Map<String, Object>> result = new Result<Map<String, Object>>();
		
		Meta m = new Meta();

    	try {
    		
			aService.addMember(refsetId, member);
			
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("id", refsetId);
    		m.add( linkTo( methodOn( RefsetBrowseController.class ).getRefsetDetails(refsetId)).withRel("Refset"));

			result.setData(data);
			
			m.setMessage(SUCESS);
			m.setStatus(HttpStatus.OK);
			
			return new ResponseEntity<Result<Map<String,Object>>>(result, HttpStatus.OK);
			
		} catch (RefsetServiceException e) {
			
			String message = String.format("Error occurred during service call : %s", e.getMessage());
			logger.error(message);
			m.setMessage(message); 
			m.setStatus(HttpStatus.OK);

			return new ResponseEntity<Result<Map<String,Object>>>(result, HttpStatus.OK);
	    	
		} catch (EntityNotFoundException e) {
			
			String message = String.format("Error occurred during service call : %s", e.getMessage());
			logger.error(message);
			m.setMessage(message); 
			m.setStatus(HttpStatus.NOT_FOUND);

			return new ResponseEntity<Result<Map<String,Object>>>(result, HttpStatus.NOT_FOUND);

		}       
    }
	
	@RequestMapping( method = RequestMethod.POST, value = "/update",  produces = "application/json", consumes = "application/json")
	@ApiOperation( value = "Update a Refset" )
	@PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Result< Map<String, Object>>> updateRefset( @RequestBody Refset r, 
    		@RequestHeader("X-REFSET-PRE-AUTH-TOKEN") String authToken, 
    		@RequestHeader("X-REFSET-PRE-AUTH-USERNAME") String userName) {
		
		logger.debug("Updating an existing refsets {}", r);

		Result<Map<String, Object>> response = new Result<Map<String, Object>>();
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

			return new ResponseEntity<Result<Map<String,Object>>>(response, HttpStatus.OK);
			
		} catch (RefsetServiceException e) {
			
			// TODO Filter error. Only Pass what is required
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
	
	
	@RequestMapping( method = RequestMethod.DELETE, value = "/delete/{refsetId}",  produces = "application/json", consumes = "application/json")
	@ApiOperation( value = "Remove a unpublished refset, it will delete refset as well as its members" )
	@PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Result< Map<String, Object>>> removeRefset( @PathVariable String refsetId, 
    		@RequestHeader("X-REFSET-PRE-AUTH-TOKEN") String authToken, 
    		@RequestHeader("X-REFSET-PRE-AUTH-USERNAME") String userName) {
		
		logger.debug("Removing an existing refsets {}", refsetId);

		Result<Map<String, Object>> response = new Result<Map<String, Object>>();
		Meta m = new Meta();
		
		response.setMeta(m);

    	try {

    		Assert.notNull(refsetId, "Required refset id is not available in request");
    		
    		aService.remove(refsetId);

    		m.add( linkTo( methodOn( RefsetBrowseController.class ).getRefsets(1, 10)).withRel("Refset"));

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
	    	
		} catch (EntityNotFoundException e) {
			
			String message = String.format("Error occurred during service call : %s", e.getMessage());
			logger.error(message);
			m.setMessage(message); 
			m.setStatus(HttpStatus.NOT_FOUND);

			return new ResponseEntity<Result<Map<String,Object>>>(response, HttpStatus.NOT_FOUND);
		}         
    }
	
	
	
	@RequestMapping( method = RequestMethod.POST, value = "/{refSetId}/add/members", produces = "application/json", consumes = "application/json" )
	@ApiOperation( value = "Add no of members in one call by providing valid conceptIds to an existing refset. "
			+ "System will find member details for given member id i.e. concept id" )
	@PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Result< Map<String, Object>>> addMembers(@PathVariable(value = "refSetId") String refsetId, 
    		@RequestBody( required = true) Set<String> memberIds, 
    		@RequestHeader("X-REFSET-PRE-AUTH-TOKEN") String authToken, 
    		@RequestHeader("X-REFSET-PRE-AUTH-USERNAME") String userName) {
		
		logger.debug("Adding member {} to refset {}", memberIds, refsetId);

		Result<Map<String, Object>> result = new Result<Map<String, Object>>();
		
		Meta m = new Meta();

    	try {
    		
			Map<String, String> outcome = aService.addMembers(refsetId, memberIds);
			
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("outcome", outcome);
    		m.add( linkTo( methodOn( RefsetBrowseController.class ).getRefsetDetails(refsetId)).withRel("Refset"));

			result.setData(data);
			
			m.setMessage(SUCESS);
			m.setStatus(HttpStatus.OK);
			
			return new ResponseEntity<Result<Map<String,Object>>>(result, HttpStatus.OK);
			
		} catch (RefsetServiceException e) {
			
			String message = String.format("Error occurred during service call : %s", e.getMessage());
			logger.error(message);
			m.setMessage(message); 
			m.setStatus(HttpStatus.OK);

			return new ResponseEntity<Result<Map<String,Object>>>(result, HttpStatus.OK);
	    	
		} catch (EntityNotFoundException e) {

			String message = String.format("Error occurred during service call : %s", e.getMessage());
			logger.error(message);
			m.setMessage(message); 
			m.setStatus(HttpStatus.NOT_FOUND);

			return new ResponseEntity<Result<Map<String,Object>>>(result, HttpStatus.NOT_FOUND);

		}       
    }	
	

}
