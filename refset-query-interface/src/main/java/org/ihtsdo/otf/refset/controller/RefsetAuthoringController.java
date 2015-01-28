/**
 * 
 */
package org.ihtsdo.otf.refset.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static org.ihtsdo.otf.refset.common.Utility.getUserDetails;
import static org.ihtsdo.otf.refset.common.Utility.getResult;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Resource;

import org.ihtsdo.otf.refset.common.Meta;
import org.ihtsdo.otf.refset.common.Result;
import org.ihtsdo.otf.refset.domain.Member;
import org.ihtsdo.otf.refset.domain.MemberValidator;
import org.ihtsdo.otf.refset.domain.Refset;
import org.ihtsdo.otf.refset.domain.RefsetValidator;
import org.ihtsdo.otf.refset.exception.ValidationException;
import org.ihtsdo.otf.refset.service.RefsetAuthoringService;
import org.ihtsdo.otf.refset.service.RefsetBrowseService;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.Assert;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

/**
 * Controller class to facilitate {@link Refset} authoring.It also check required authorization  
 *
 */
@RestController
@Api(value="Refset Authoring", description="Service to author refset and their member details", position = 1)
@RequestMapping("/v1.0/refsets")
public class RefsetAuthoringController {
	
	private static final Logger logger = LoggerFactory.getLogger(RefsetAuthoringController.class);

	private static final String SUCCESS = "Success";
	
	@Resource(name = "browseGraphService")
	private RefsetBrowseService bService;

	@Resource
	private RefsetAuthoringService aService;
	
	@Autowired
	private MemberValidator mValidator;
	
	@Autowired
	private RefsetValidator rValidator;

	@RequestMapping( method = RequestMethod.POST, value = "/new",  produces = "application/json", consumes = "application/json")
	@ApiOperation( value = "Add a Refset", notes = "It adds a brand new refset to database" )
	@PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Result< Map<String, Object>>> addRefset( @RequestBody Refset r) throws Exception {
		
		logger.debug("Adding refsets {}", r);
		validateRefset(r);
		Set<Member> ms = new HashSet<Member>();
		ms.addAll(r.getMembers());
		if (ms != null && !ms.isEmpty()) {
			
			validateMembers(ms);

		}
		Result<Map<String, Object>> result = getResult();

		addMetaDetails(r);
		r.setCreatedBy(getUserDetails().getUsername());
		r.setModifiedBy(getUserDetails().getUsername());

		List<Member> members = r.getMembers();
		for (Member member : members) {
			
			member.setCreatedBy(getUserDetails().getUsername());
			member.setModifiedBy(getUserDetails().getUsername());

		}
		aService.addRefset(r);
		

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("uuid", r.getUuid());
		result.getMeta().add( linkTo( methodOn( RefsetBrowseController.class ).getRefsetDetails(r.getUuid())).withRel("Refset"));

		result.setData(data);

		result.getMeta().setMessage(SUCCESS);
		result.getMeta().setStatus(HttpStatus.CREATED);

		return new ResponseEntity<Result<Map<String,Object>>>(result, HttpStatus.CREATED);
		
	
    }
	
	/**Adds meta details in new {@link Refset}
	 * @param r
	 * @throws AccessDeniedException 
	 */
	private void addMetaDetails(Refset r) throws AccessDeniedException {
		
		String id = UUID.randomUUID().toString();
		r.setUuid(id);
		r.setCreated(new DateTime());
		r.setCreatedBy(getUserDetails().getUsername());
		r.setActive(r.isActive());		
		
	}
	
	
	@RequestMapping( method = RequestMethod.POST, value = "/{refSetId}/add/member", produces = "application/json", consumes = "application/json" )
	@ApiOperation( value = "Shortcut method to add a member to an existing refset.", 
		notes = "Adds a single member to refset identified by refset id in path")
	@PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Result< Map<String, Object>>> addMember(@PathVariable(value = "refSetId") String refsetId, 
    		@RequestBody( required = true) Member member) throws Exception {
		
		logger.debug("Adding member {} to refset {}", member, refsetId);

		Set<Member> ms = new HashSet<Member>();
		ms.add(member);
		if (ms != null && !ms.isEmpty()) {
			
			validateMembers(ms);

		}
		Result<Map<String, Object>> result = new Result<Map<String, Object>>();
		
		Meta m = new Meta();
		member.setCreatedBy(getUserDetails().getUsername());
		member.setModifiedBy(getUserDetails().getUsername());


		aService.addMember(refsetId, member);
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("uuid", refsetId);
		m.add( linkTo( methodOn( RefsetBrowseController.class ).getRefsetDetails(refsetId)).withRel("Refset"));

		result.setData(data);
		
		m.setMessage(SUCCESS);
		m.setStatus(HttpStatus.OK);
		
		return new ResponseEntity<Result<Map<String,Object>>>(result, HttpStatus.OK);
		
	       
    }
	
	@RequestMapping( method = RequestMethod.POST, value = "/update",  produces = "application/json", consumes = "application/json")
	@ApiOperation( value = "Update a Refset", notes = "Updates an existing refset" )
	@PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Result< Map<String, Object>>> updateRefset( @RequestBody Refset r) throws Exception {
		
		logger.debug("Updating an existing refsets {}", r);

		validateRefset(r);
		Set<Member> ms = new HashSet<Member>();
		ms.addAll(r.getMembers());
		if (ms != null && !ms.isEmpty()) {
			
			validateMembers(ms);

		}
		Result<Map<String, Object>> response = new Result<Map<String, Object>>();
		Meta m = new Meta();
		
		response.setMeta(m);

		r.setModifiedBy(getUserDetails().getUsername());
		
		List<Member> members = r.getMembers();
		for (Member member : members) {
			
			member.setModifiedBy(getUserDetails().getUsername());

		}


		aService.updateRefset(r);
		

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("uuid", r.getUuid());
		m.add( linkTo( methodOn( RefsetBrowseController.class ).getRefsetDetails(r.getUuid())).withRel("Refset"));

		response.setData(data);

		m.setMessage(SUCCESS);
		m.setStatus(HttpStatus.OK);

		return new ResponseEntity<Result<Map<String,Object>>>(response, HttpStatus.OK);
		
	         
    }
	
	
	@RequestMapping( method = RequestMethod.DELETE, value = "/delete/{refsetId}",  produces = "application/json", consumes = "application/json")
	@ApiOperation( value = "Remove a unpublished refset", notes = "It deletes an existing refset identified by refset id in path "
			+ ", it also delete refset members")
	@PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Result< Map<String, Object>>> removeRefset( @PathVariable String refsetId) throws Exception {
		
		logger.debug("Removing an existing refsets {}", refsetId);

		Result<Map<String, Object>> response = new Result<Map<String, Object>>();
		Meta m = new Meta();
		
		response.setMeta(m);

		Assert.notNull(refsetId, "Required refset id is not available in request");
		
		aService.remove(refsetId, getUserDetails().getUsername());

		m.add( linkTo( methodOn( RefsetBrowseController.class ).getRefsets(1, 10)).withRel("Refset"));

		m.setMessage(SUCCESS);
		m.setStatus(HttpStatus.OK);

		return new ResponseEntity<Result<Map<String,Object>>>(response, HttpStatus.OK);
		
	         
    }
	
	
	
	@RequestMapping( method = RequestMethod.POST, value = "/{refSetId}/add/members", produces = "application/json", consumes = "application/json" )
	@ApiOperation( value = "Add no of members in this call", notes = "Adds no of members in single call to a refset identified by refset id in path" )
	@PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Result< Map<String, Object>>> addMembers(@PathVariable(value = "refSetId") String refsetId, 
    		@RequestBody( required = true) Set<Member> members) throws Exception {
		
		logger.debug("Adding member {} to refset {}", members, refsetId);

		validateMembers(members);
		Result<Map<String, Object>> result = new Result<Map<String, Object>>();
		
		Meta m = new Meta();

		for (Member member : members) {
			
			member.setCreatedBy(getUserDetails().getUsername());
			member.setModifiedBy(getUserDetails().getUsername());
			member.setCreated(new DateTime());
			member.setModifiedDate(new DateTime());


		}
		Map<String, String> outcome = aService.addMembers(refsetId, members, getUserDetails().getUsername());
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("outcome", outcome);
		m.add( linkTo( methodOn( RefsetBrowseController.class ).getRefsetDetails(refsetId)).withRel("Refset"));

		result.setData(data);
		
		m.setMessage(SUCCESS);
		m.setStatus(HttpStatus.OK);
		
		return new ResponseEntity<Result<Map<String,Object>>>(result, HttpStatus.OK);
		
	
    	       
    }
	
	/**
	 * @param members
	 */
	private void validateMembers(Set<Member> members) throws ValidationException {
		// TODO Auto-generated method stub
		
		Map<Object, List<FieldError>> errors = new HashMap<Object, List<FieldError>>();
		
		for (Member m : members) {
			
			Errors e = new BeanPropertyBindingResult(m, "member");
			mValidator.validate(m, e );
			
			if (e.hasErrors()) {
				
				errors.put(m, e.getFieldErrors());
			}
			
			
		}
		
		if (!errors.isEmpty()) {
			
			throw new ValidationException(errors);
		}
		
	}

	@RequestMapping( method = RequestMethod.DELETE, value = "/delete/{refsetId}/member/{referenceComponentId}", 
			produces = "application/json", consumes = "application/json")
	@ApiOperation( value = "Removes a member from refset", notes = "Removes  a member identified "
			+ "by refserence component id of member from a refset" )
	@PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Result< Map<String, Object>>> removeMember( @PathVariable String refsetId,
    		@PathVariable String referenceComponentId) throws Exception {
		
		logger.debug("Removing an existing refsets {}", refsetId);

		Result<Map<String, Object>> response = new Result<Map<String, Object>>();
		Meta m = new Meta();
		
		response.setMeta(m);

		Assert.notNull(refsetId, "Required refset id is not available in request");
		
		aService.removeMemberFromRefset(refsetId, referenceComponentId, getUserDetails().getUsername());

		m.add( linkTo( methodOn( RefsetBrowseController.class ).getRefsets(1, 10)).withRel("Refset"));

		m.setMessage(SUCCESS);
		m.setStatus(HttpStatus.OK);

		return new ResponseEntity<Result<Map<String,Object>>>(response, HttpStatus.OK);
		
	         
    }
	
	@RequestMapping( method = RequestMethod.DELETE, value = "/delete/{refsetId}/members",  produces = "application/json", consumes = "application/json")
	@ApiOperation( value = "Removes list of members from refset.", notes = "Removes number of members from a refset for given reference component id" )
	@PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Result< Map<String, Object>>> removeMembers( @PathVariable String refsetId,
    		@RequestBody Set<String> referencedComponentIds) throws Exception {
		
		logger.debug("Removing an existing refsets {}", refsetId);

		Result<Map<String, Object>> result = new Result<Map<String, Object>>();
		Meta m = new Meta();
		
		result.setMeta(m);
		
		Map<String, String> outcome = aService.removeMembers(refsetId, referencedComponentIds, getUserDetails().getUsername());

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("outcome", outcome);
		m.add( linkTo( methodOn( RefsetBrowseController.class ).getRefsetDetails(refsetId)).withRel("Refset"));

		result.setData(data);
		
		m.setMessage(SUCCESS);
		m.setStatus(HttpStatus.OK);

		return new ResponseEntity<Result<Map<String,Object>>>(result, HttpStatus.OK);
		
	         
    }


	/**
	 * @param members
	 */
	private void validateRefset(Refset r) throws ValidationException {
		// TODO Auto-generated method stub
		
		Map<Object, List<FieldError>> errors = new HashMap<Object, List<FieldError>>();

		Errors e = new BeanPropertyBindingResult(r, "refset");
		rValidator.validate(r, e );
		
		if (e.hasErrors()) {
			
			errors.put(r, e.getFieldErrors());
		}
		
		
	
		
		if (!errors.isEmpty()) {
			
			throw new ValidationException(errors);
		}
		
	}

}
