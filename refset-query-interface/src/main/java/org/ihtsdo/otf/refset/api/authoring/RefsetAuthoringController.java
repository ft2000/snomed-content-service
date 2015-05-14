/**
 * 
 */
package org.ihtsdo.otf.refset.api.authoring;

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

import org.ihtsdo.otf.refset.api.browse.RefsetBrowseController;
import org.ihtsdo.otf.refset.common.Result;
import org.ihtsdo.otf.refset.domain.MemberDTO;
import org.ihtsdo.otf.refset.domain.MemberValidator;
import org.ihtsdo.otf.refset.domain.RefsetDTO;
import org.ihtsdo.otf.refset.domain.RefsetValidator;
import org.ihtsdo.otf.refset.exception.ValidationException;
import org.ihtsdo.otf.refset.service.authoring.RefsetAuthoringService;
import org.ihtsdo.otf.refset.service.browse.RefsetBrowseService;
import org.ihtsdo.otf.refset.service.termserver.TermServer;
import org.ihtsdo.otf.snomed.service.RefsetMetadataService;
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

import com.mangofactory.swagger.annotations.ApiIgnore;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

/**
 * Controller class to facilitate {@link RefsetDTO} authoring.It also check required authorization  
 *
 */
@RestController
@Api(value="Refset Authoring", description="Service to author refset and their member details", position = 1)
@RequestMapping("/v1/refsets")
public class RefsetAuthoringController {
	
	private static final Logger logger = LoggerFactory.getLogger(RefsetAuthoringController.class);
	
	@Resource(name = "browseGraphService")
	private RefsetBrowseService bService;

	@Resource
	private RefsetAuthoringService aService;
	
	@Autowired
	private MemberValidator mValidator;
	
	@Autowired
	private RefsetValidator rValidator;
	
	@Autowired
	private TermServer tService;

	@Autowired
	private RefsetMetadataService mdService;
	

	@RequestMapping( method = RequestMethod.POST, value = "/new",  produces = "application/json", consumes = "application/json")
	@ApiOperation( value = "Add a Refset", notes = "It adds a brand new refset to database" )
	@PreAuthorize("hasRole('ROLE_ihtsdo-users')")
    public ResponseEntity<Result< Map<String, Object>>> addRefset( @RequestBody RefsetDTO r) throws Exception {
		
		logger.debug("Adding refsets {}", r);
		validateRefset(r);
		Set<MemberDTO> ms = new HashSet<MemberDTO>();
		ms.addAll(r.getMembers());
		if (ms != null && !ms.isEmpty()) {
			
			validateMembers(ms);

		}

		addMetaDetails(r);
		r.setCreatedBy(getUserDetails().getUsername());
		r.setModifiedBy(getUserDetails().getUsername());

		List<MemberDTO> members = r.getMembers();
		for (MemberDTO member : members) {
			
			member.setCreatedBy(getUserDetails().getUsername());
			member.setModifiedBy(getUserDetails().getUsername());

		}
		aService.addRefset(r);
		
		Result<Map<String, Object>> result = getResult();

		result.getData().put("uuid", r.getUuid());
		result.getMeta().add( linkTo( methodOn( RefsetBrowseController.class ).getRefsetHeader(r.getUuid(), -1)).withRel("Refset"));

		return new ResponseEntity<Result<Map<String,Object>>>(result, HttpStatus.CREATED);
		
	
    }
	
	/**Adds meta details in new {@link RefsetDTO}
	 * @param r
	 * @throws AccessDeniedException 
	 */
	private void addMetaDetails(RefsetDTO r) throws AccessDeniedException {
		
		String id = UUID.randomUUID().toString();
		r.setUuid(id);
		r.setCreated(new DateTime());
		r.setCreatedBy(getUserDetails().getUsername());
		r.setActive(r.isActive());		
		
	}
	
	
	@RequestMapping( method = RequestMethod.POST, value = "/{refSetId}/add/member", produces = "application/json", consumes = "application/json" )
	@ApiOperation( value = "Shortcut method to add a member to an existing refset.", 
		notes = "Adds a single member to refset identified by refset id in path")
	@PreAuthorize("hasRole('ROLE_ihtsdo-users')")
	@ApiIgnore
    public ResponseEntity<Result< Map<String, Object>>> addMember(@PathVariable(value = "refSetId") String refsetId, 
    		@RequestBody( required = true) MemberDTO member) throws Exception {
		
		logger.debug("Adding member {} to refset {}", member, refsetId);

		Set<MemberDTO> ms = new HashSet<MemberDTO>();
		ms.add(member);
		if (ms != null && !ms.isEmpty()) {
			
			validateMembers(ms);

		}
		
		member.setCreatedBy(getUserDetails().getUsername());
		member.setModifiedBy(getUserDetails().getUsername());


		aService.addMember(refsetId, member);
		
		Result<Map<String, Object>> result = getResult();

		result.getData().put("uuid", refsetId);
		result.getMeta().add( linkTo( methodOn( RefsetBrowseController.class ).getRefsetHeader(refsetId, -1)).withRel("Refset"));

		return new ResponseEntity<Result<Map<String,Object>>>(result, HttpStatus.OK);
		
	       
    }
	
	@RequestMapping( method = RequestMethod.POST, value = "/update",  produces = "application/json", consumes = "application/json")
	@ApiOperation( value = "Update a Refset", notes = "Updates an existing refset" )
	@PreAuthorize("hasRole('ROLE_ihtsdo-users')")
    public ResponseEntity<Result< Map<String, Object>>> updateRefset( @RequestBody RefsetDTO r) throws Exception {
		
		logger.debug("Updating an existing refsets {}", r);

		validateRefset(r);
		Set<MemberDTO> ms = new HashSet<MemberDTO>();
		ms.addAll(r.getMembers());
		if (ms != null && !ms.isEmpty()) {
			
			validateMembers(ms);

		}

		r.setModifiedBy(getUserDetails().getUsername());
		
		List<MemberDTO> members = r.getMembers();
		for (MemberDTO member : members) {
			
			member.setModifiedBy(getUserDetails().getUsername());

		}

		aService.updateRefset(r);
		
		Result<Map<String, Object>> result = getResult();

		result.getData().put("uuid", r.getUuid());
		result.getMeta().add( linkTo( methodOn( RefsetBrowseController.class ).getRefsetHeader(r.getUuid(), -1)).withRel("Refset"));

		return new ResponseEntity<Result<Map<String,Object>>>(result, HttpStatus.OK);
		
	         
    }
	
	
	@RequestMapping( method = RequestMethod.DELETE, value = "/delete/{refsetId}",  produces = "application/json", consumes = "application/json")
	@ApiOperation( value = "Remove a unpublished refset", notes = "It deletes an existing refset identified by refset id in path "
			+ ", it also delete refset members")
	@PreAuthorize("hasRole('ROLE_ihtsdo-users')")
    public ResponseEntity<Result< Map<String, Object>>> removeRefset( @PathVariable String refsetId) throws Exception {
		
		logger.debug("Removing an existing refsets {}", refsetId);

		Assert.notNull(refsetId, "Required refset id is not available in request");
		
		aService.remove(refsetId, getUserDetails().getUsername());
		
		Result<Map<String, Object>> result = getResult();

		result.getMeta().add( linkTo( methodOn( RefsetBrowseController.class ).getRefsets(1, 10)).withRel("Refset"));

		return new ResponseEntity<Result<Map<String,Object>>>(result, HttpStatus.OK);
		
	         
    }
	
	
	
	@RequestMapping( method = RequestMethod.POST, value = "/{refSetId}/add/members", produces = "application/json", consumes = "application/json" )
	@ApiOperation( value = "Add no of members in this call", notes = "Adds no of members in single call to a refset identified by refset id in path" )
	@PreAuthorize("hasRole('ROLE_ihtsdo-users')")
    public ResponseEntity<Result< Map<String, Object>>> addMembers(@PathVariable(value = "refSetId") String refsetId, 
    		@RequestBody( required = true) Set<MemberDTO> members) throws Exception {
		
		logger.debug("Adding member {} to refset {}", members, refsetId);

		validateMembers(members);

		for (MemberDTO member : members) {
			
			member.setCreatedBy(getUserDetails().getUsername());
			member.setModifiedBy(getUserDetails().getUsername());
			member.setCreated(new DateTime());
			member.setModifiedDate(new DateTime());


		}
		Map<String, String> outcome = aService.addMembers(refsetId, members, getUserDetails().getUsername());
		
		Result<Map<String, Object>> result = getResult();
		result.getData().put("outcome", outcome);
		result.getMeta().add( linkTo( methodOn( RefsetBrowseController.class ).getRefsetHeader(refsetId, -1)).withRel("Refset"));

		return new ResponseEntity<Result<Map<String,Object>>>(result, HttpStatus.OK);
		
	
    	       
    }
	
	@RequestMapping( method = RequestMethod.POST, value = "/{refSetId}/add/members", produces = "application/json", consumes = "application/json" )
	@ApiOperation( value = "Add no of members in this call", notes = "Adds no of members in single call to a refset identified by refset id in path" )
	@PreAuthorize("hasRole('ROLE_ihtsdo-users')")
    public ResponseEntity<Result< Map<String, Object>>> addConceptIds(@PathVariable(value = "refSetId") String refsetId, 
    		@RequestBody( required = true) Set<String> conceptIds) throws Exception {
		
		logger.debug("Adding member {} to refset {}", conceptIds, refsetId);


		Map<String, String> outcome = aService.addMembersByConceptIds(refsetId, conceptIds, getUserDetails().getUsername());
		
		Result<Map<String, Object>> result = getResult();
		result.getData().put("outcome", outcome);
		result.getMeta().add( linkTo( methodOn( RefsetBrowseController.class ).getRefsetHeader(refsetId, -1)).withRel("Refset"));

		return new ResponseEntity<Result<Map<String,Object>>>(result, HttpStatus.OK);
		
	
    	       
    }
	
	/**
	 * @param members
	 */
	private void validateMembers(Set<MemberDTO> members) throws ValidationException {
		// TODO Auto-generated method stub
		
		Map<Object, List<FieldError>> errors = new HashMap<Object, List<FieldError>>();
		
		for (MemberDTO m : members) {
			
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
	@PreAuthorize("hasRole('ROLE_ihtsdo-users')")
    public ResponseEntity<Result< Map<String, Object>>> removeMember( @PathVariable String refsetId,
    		@PathVariable String referenceComponentId) throws Exception {
		
		logger.debug("Removing an existing refsets {}", refsetId);

		Assert.notNull(refsetId, "Required refset id is not available in request");
		
		aService.removeMemberFromRefset(refsetId, referenceComponentId, getUserDetails().getUsername());

		Result<Map<String, Object>> result = getResult();
		result.getMeta().add( linkTo( methodOn( RefsetBrowseController.class ).getRefsets(1, 10)).withRel("Refset"));

		return new ResponseEntity<Result<Map<String,Object>>>(result, HttpStatus.OK);
		
	         
    }
	
	@RequestMapping( method = RequestMethod.DELETE, value = "/delete/{refsetId}/members",  produces = "application/json", consumes = "application/json")
	@ApiOperation( value = "Removes list of members from refset.", notes = "Removes number of members from a refset for given reference component id" )
	@PreAuthorize("hasRole('ROLE_ihtsdo-users')")
    public ResponseEntity<Result< Map<String, Object>>> removeMembers( @PathVariable String refsetId,
    		@RequestBody Set<String> referencedComponentIds) throws Exception {
		
		logger.debug("Removing an existing refsets {}", refsetId);

		Map<String, String> outcome = aService.removeMembers(refsetId, referencedComponentIds, getUserDetails().getUsername());

		Result<Map<String, Object>> result = getResult();
		result.getData().put("outcome", outcome);
		result.getMeta().add( linkTo( methodOn( RefsetBrowseController.class ).getRefsetHeader(refsetId, -1)).withRel("Refset"));
		return new ResponseEntity<Result<Map<String,Object>>>(result, HttpStatus.OK);
		
	         
    }


	/**
	 * @param members
	 */
	private void validateRefset(RefsetDTO r) throws ValidationException {
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
