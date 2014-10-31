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
import org.ihtsdo.otf.refset.domain.Member;
import org.ihtsdo.otf.refset.domain.Refset;
import org.ihtsdo.otf.refset.service.RefsetBrowseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
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
	@ApiOperation( value = "Retrieves list of existing refsets. By default it returns 10 refset and thereafter another 10 or desired range" )
    public ResponseEntity<Result< Map<String, Object>>> getRefsets( @RequestParam( value = "page", defaultValue = "0" ) int from, 
    		@RequestParam( value = "size", defaultValue = "10" ) int to ) throws Exception {
		
		logger.debug("Existing refsets");

		Result<Map<String, Object>> response = new Result<Map<String, Object>>();
		Meta m = new Meta();
		m.add( linkTo( methodOn( RefsetBrowseController.class ).getRefsets( from, to ) ).withSelfRel() );
		
		response.setMeta(m);


		boolean published = false;
		
		if (SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken) {
			
			published = true;
			logger.debug("Geting only published ? = {}", published);
			
		}
		List<Refset> refSets =  bService.getRefsets( from, to, published );

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("refsets", refSets);
		response.setData(data);
		
		m.setMessage(SUCESS);
		m.setStatus(HttpStatus.OK);

		return new ResponseEntity<Result<Map<String,Object>>>(response, HttpStatus.OK);
		
	         
    }
	
	@RequestMapping( method = RequestMethod.GET, value = "/{refSetId}", produces = "application/json" )
	@ApiOperation( value = "Api to get details of a refset for given refset id." )
    public ResponseEntity<Result< Map<String, Object>>> getRefsetDetails( @PathVariable( value = "refSetId" ) String refSetId ) throws Exception {
		
		logger.debug("Getting refset details");

		Result<Map<String, Object>> response = new Result<Map<String, Object>>();
		
		Meta m = new Meta();

		m.add( linkTo( methodOn( RefsetBrowseController.class, refSetId).getRefsetDetails(refSetId) ).withSelfRel() );
		response.setMeta(m);


		
		Refset refSet =  bService.getRefset(refSetId);

		if (SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken && !refSet.isPublished()) {
			
			throw new AccessDeniedException("Please login to see work in progress refsets");
			
		}
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("refset", refSet);
		
		response.setData(data);
		m.setMessage(SUCESS);
		m.setStatus(HttpStatus.OK);

		return new ResponseEntity<Result<Map<String,Object>>>(response, HttpStatus.OK);
		
	       
    }
	
	@RequestMapping( method = RequestMethod.GET, value = "checkDescription/{description}", produces = "application/json" )
	@ApiOperation( value = "Api to check if provided description already exist in the system" )
    public ResponseEntity<Result< Map<String, Object>>> isDescriptionExist( @PathVariable( value = "description" ) String description ) throws Exception {
		
		logger.debug("validating description {}", description);

		Result<Map<String, Object>> response = new Result<Map<String, Object>>();

		Meta m = new Meta();
		
		boolean isExist = bService.isDescriptionExist(description);

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("exist", isExist);
		
		response.setData(data);
		m.setMessage(SUCESS);
		m.setStatus(HttpStatus.OK);

		return new ResponseEntity<Result<Map<String,Object>>>(response, HttpStatus.OK);
		
	       
    }
	
	
	@RequestMapping( method = RequestMethod.GET, produces = "application/json", value = "/{refsetId}/members")
	@ApiOperation( value = "Retrieves members of an existing refsets based on given range" )
    public ResponseEntity<Result< Map<String, Object>>> getRefsetMembers( @RequestParam( value = "from", defaultValue = "0" ) int from, 
    		@RequestParam( value = "to", defaultValue = "15" ) int to,  
    		@PathVariable( value = "refsetId" ) String refsetId) throws Exception {
		
		logger.debug("Getting members of {} ", refsetId);

		Result<Map<String, Object>> response = new Result<Map<String, Object>>();
		Meta m = new Meta();
		m.add( linkTo( methodOn( RefsetBrowseController.class ).getRefsetMembers( from, to, refsetId ) ).withSelfRel() );
		
		response.setMeta(m);
		
		bService.getRefset(refsetId, from, to);

		
		Refset refSet =  bService.getRefset(refsetId, from, to);

		if (SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken && !refSet.isPublished()) {
			
			throw new AccessDeniedException("Please login to see work in progress refsets members");
			
		}

		List<Member> ms = refSet.getMembers();
		
		logger.debug("returning {} members", ms.size());

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("members", ms);
		response.setData(data);
		
		
		m.setMessage(SUCESS);
		m.setStatus(HttpStatus.OK);

		return new ResponseEntity<Result<Map<String,Object>>>(response, HttpStatus.OK);
		
	         
    }
	
	
	@RequestMapping( method = RequestMethod.GET, value = "/{refSetId}/header", produces = "application/json")
	@ApiOperation( value = "Api to get details of a refset excluding members for given refset id." )
    public ResponseEntity<Result< Map<String, Object>>> getRefsetHeader( @PathVariable( value = "refSetId" ) String refSetId ) throws Exception {
		
		logger.debug("Getting refset details");

		Result<Map<String, Object>> response = new Result<Map<String, Object>>();
		
		Meta m = new Meta();

		m.add( linkTo( methodOn( RefsetBrowseController.class, refSetId).getRefsetDetails(refSetId) ).withSelfRel() );
		response.setMeta(m);


		
		Refset refSet =  bService.getRefsetHeader(refSetId);

		if (SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken && !refSet.isPublished()) {
			
			throw new AccessDeniedException("Please login to see work in progress refsets");
			
		}
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("refset", refSet);
		
		response.setData(data);
		m.setMessage(SUCESS);
		m.setStatus(HttpStatus.OK);

		return new ResponseEntity<Result<Map<String,Object>>>(response, HttpStatus.OK);
		
	       
    }

	
}
