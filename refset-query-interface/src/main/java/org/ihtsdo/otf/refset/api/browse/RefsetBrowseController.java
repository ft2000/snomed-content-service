/**
 * 
 */
package org.ihtsdo.otf.refset.api.browse;

import static org.ihtsdo.otf.refset.common.Utility.getResult;
import static org.ihtsdo.otf.refset.common.Utility.getUserDetails;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.ihtsdo.otf.refset.common.Direction;
import org.ihtsdo.otf.refset.common.Result;
import org.ihtsdo.otf.refset.common.SearchCriteria;
import org.ihtsdo.otf.refset.common.SearchField;
import org.ihtsdo.otf.refset.domain.MemberDTO;
import org.ihtsdo.otf.refset.domain.RefsetDTO;
import org.ihtsdo.otf.refset.service.browse.RefsetBrowseService;
import org.ihtsdo.otf.refset.service.matrix.ActivityMatrixService;
import org.ihtsdo.otf.refset.service.matrix.ViewActivityMatrixService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

/**
 *@author 
 */
@RestController
@Api(value="Refset", description="Service to retrieve existing refset and their member details" , position = 2)
@RequestMapping("/v1/refsets")
public class RefsetBrowseController {
	
	private static final Logger logger = LoggerFactory.getLogger(RefsetBrowseController.class);
	
	@Resource(name = "browseGraphService")
	private RefsetBrowseService bService;
	
	@Autowired
	private ActivityMatrixService matrixService;
	
	@Autowired
	private ViewActivityMatrixService viewMatrixService;
	
	@RequestMapping( method = RequestMethod.GET, produces = "application/json", value = "/myRefsets" )
	@ApiOperation( value = "Retrieves list of existing refsets owned by logged in user", notes = "Returns existing refsets owned by logged in user save their members. "
			+ "By default it returns 10 refset and thereafter another 10 or desired range" )
	@PreAuthorize("hasRole('ROLE_ihtsdo-users')")
    public ResponseEntity<Result< Map<String, Object>>> getMyRefsets( @RequestParam( value = "from", defaultValue = "0" ) int from, 
    		@RequestParam( value = "to", defaultValue = "10" ) int to ) throws Exception {
		
		logger.debug("get my refsets");

		Result<Map<String, Object>> r = getResult();
		r.getMeta().add( linkTo( methodOn( RefsetBrowseController.class ).getRefsets( from, to ) ).withRel("Refsets"));		
		
		
		SearchCriteria criteria = new SearchCriteria();
		criteria.addSortBy(SearchField.modifiedDate, Direction.desc);
		
		criteria.setFrom(from);
		criteria.setTo(to);
		
		String userName = getUserDetails().getUsername();//getUserDetails().getUsername();
		criteria.addSearchField(SearchField.createdBy, userName);
		List<RefsetDTO> refSets =  bService.getRefsets(criteria);

		r.getData().put("refsets", refSets);
				
		Long totalNoOfRefsets = bService.totalNoOfRefset(criteria);
		
		r.getData().put("totalNoOfRefsets", totalNoOfRefsets);
		
		return new ResponseEntity<Result<Map<String,Object>>>(r, HttpStatus.OK);

	         
    }
	
	@RequestMapping( method = RequestMethod.GET, value = "checkDescription/{description}", produces = "application/json" )
	@ApiOperation( value = "Api to check if provided description already exist in the system", notes = "It matches description "
			+ "of existing refset to avoid duplicate descriptions " )
    public ResponseEntity<Result< Map<String, Object>>> isDescriptionExist( @PathVariable( value = "description" ) String description ) throws Exception {
		
		logger.debug("validating description {}", description);

		Result<Map<String, Object>> response = getResult();

		boolean isExist = bService.isDescriptionExist(description);

		response.getData().put("outcome", isExist);
		
		return new ResponseEntity<Result<Map<String,Object>>>(response, HttpStatus.OK);
		
	       
    }
	
	
	@RequestMapping( method = RequestMethod.GET, produces = "application/json", value = "/{refsetId}/members")
	@ApiOperation( value = "Retrieves members of an existing refsets", notes = "Retrieves members of existing refset identified "
			+ "by refset id in the path and given range ")
    public ResponseEntity<Result< Map<String, Object>>> getRefsetMembers( @RequestParam( value = "from", defaultValue = "0" ) int from, 
    		@RequestParam( value = "to", defaultValue = "15" ) int to,  
    		@PathVariable( value = "refsetId" ) String refsetId, @RequestParam( value = "version", defaultValue = "-1") Integer version) throws Exception {
		
		logger.debug("Getting members of {} and version ", refsetId, version);
		
		RefsetDTO refSet =  bService.getRefset(refsetId, from, to, version);

		if (SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken && !refSet.isPublished()) {
			
			throw new AccessDeniedException("Please login to see work in progress refsets members");
			
		}

		List<MemberDTO> ms = refSet.getMembers();
		
		logger.debug("returning {} members", ms.size());

		Result<Map<String, Object>> response = getResult();
		response.getMeta().add( linkTo( methodOn( RefsetBrowseController.class ).getRefsetMembers( from, to, refsetId, version) ).withRel("Members") );

		response.getData().put("members", ms);

		return new ResponseEntity<Result<Map<String,Object>>>(response, HttpStatus.OK);
		
	         
    }
	
	
	@RequestMapping( method = RequestMethod.GET, value = "/{refsetId}/header", produces = "application/json")
	@ApiOperation( value = "Api to get details of a refset excluding its members.", 
		notes = "Get refset header details for given refset uuid. "
				+ "A Lightweight service for user who are only intrested in getting refset details not their members" )
    public ResponseEntity<Result< Map<String, Object>>> getRefsetHeader( @PathVariable( value = "refsetId" ) String refsetId, 
    		@RequestParam( value = "version", defaultValue = "-1") Integer version ) throws Exception {
		
		logger.debug("Getting refset details");

		Result<Map<String, Object>> response = getResult();
		
		response.getMeta().add( linkTo( methodOn( RefsetBrowseController.class, refsetId).getRefsetHeader(refsetId, version) ).withRel("RefsetHeader") );


		
		RefsetDTO refset =  bService.getRefsetHeader(refsetId, version);

		if (SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken && !refset.isPublished()) {
			
			throw new AccessDeniedException("Please login to see work in progress refsets");
			
		} else if(!refset.isPublished() && (StringUtils.isEmpty(getUserDetails().getUsername()) 
				|| !getUserDetails().getUsername().equalsIgnoreCase(refset.getCreatedBy()))){
			
			throw new AccessDeniedException("Only owner is allowed to access their work in progress refset");

		}
		//capture user event. This is Async call
		matrixService.addViewEvent(refsetId, getEligibleUser());

		response.getData().put("refset", refset);
		
		return new ResponseEntity<Result<Map<String,Object>>>(response, HttpStatus.OK);
		
	       
    }
	
	
	/**Shortcut to get guest user if user is not logged in
	 * @return
	 */
	private String getEligibleUser() {

		try {
			
			return getUserDetails().getUsername();
			
		}catch (AccessDeniedException e) {
			
			return "guest";
		}
	}

	@RequestMapping( method = RequestMethod.GET, produces = "application/json" )
	@ApiOperation( value = "Retrieves list of existing refsets", notes = "Returns existing refsets save their members. "
			+ "If user is authorized then all refsets are returned however if user is not logged in then only "
			+ "published refsets are returned. "
			+ "By default it returns 10 refset and thereafter another 10 or desired range" )
    public ResponseEntity<Result< Map<String, Object>>> getRefsets( @RequestParam( value = "from", defaultValue = "0" ) int from, 
    		@RequestParam( value = "to", defaultValue = "10" ) int to ) throws Exception {
		
		logger.debug("Existing refsets");

		Result<Map<String, Object>> r = getResult();
		r.getMeta().add( linkTo( methodOn( RefsetBrowseController.class ).getRefsets( from, to ) ).withRel("Refsets"));

		SearchCriteria criteria = new SearchCriteria();
		criteria.addSortBy(SearchField.expectedPublishDate, Direction.desc);
		
		criteria.setFrom(from);
		criteria.setTo(to);
		
		criteria.addSearchField(SearchField.published, true);//always retrieve published refsets
		List<RefsetDTO> refSets =  bService.getRefsets(criteria);	

		r.getData().put("refsets", refSets);
		
		Long totalNoOfRefsets = bService.totalNoOfRefset(criteria);
		
		r.getData().put("totalNoOfRefsets", totalNoOfRefsets);
		
		return new ResponseEntity<Result<Map<String,Object>>>(r, HttpStatus.OK);
		
	         
    }
	
	
	@RequestMapping( method = RequestMethod.GET, produces = "application/json", value = "/mostviewed" )
	@ApiOperation( value = "Retrieves list of 10 most viewed published refsets", 
		notes = "Returns 10 most viewed published refset sorted by most viewed count where most viewed at top" )
    public ResponseEntity<Result< Map<String, Object>>> getMostViewedRefset( @RequestParam( value = "from", defaultValue = "0" ) int from, 
    		@RequestParam( value = "to", defaultValue = "10" ) int to ) throws Exception {
		
		logger.debug("get most viewed refsets");

		Result<Map<String, Object>> r = getResult();
		r.getMeta().add( linkTo( methodOn( RefsetBrowseController.class ).getRefsets( from, to ) ).withRel("Refsets"));		
		List<RefsetDTO> refSets =  viewMatrixService.getMostViewedPublishedRefsets(to);

		r.getData().put("refsets", refSets);


		return new ResponseEntity<Result<Map<String,Object>>>(r, HttpStatus.OK);
		
	         
    }
	
	@RequestMapping( method = RequestMethod.GET, produces = "application/json", value = "/latestPublished" )
	@ApiOperation( value = "Retrieves list of 10 most recently published refsets", 
		notes = "Returns 10 most recently published refset sorted by published date where latest at top" )
    public ResponseEntity<Result< Map<String, Object>>> getLatestPublishedRefset( @RequestParam( value = "from", defaultValue = "0" ) int from, 
    		@RequestParam( value = "to", defaultValue = "10" ) int to ) throws Exception {
		
		logger.debug("get most latest published refsets");

		Result<Map<String, Object>> r = getResult();
		r.getMeta().add( linkTo( methodOn( RefsetBrowseController.class ).getRefsets( from, to ) ).withRel("Refsets"));
		
		SearchCriteria criteria = new SearchCriteria();
		criteria.addSortBy(SearchField.expectedPublishDate, Direction.desc);
		
		criteria.setFrom(from);
		criteria.setTo(to);
		
		criteria.addSearchField(SearchField.published, true);
		List<RefsetDTO> refSets =  bService.getRefsets(criteria);

		r.getData().put("refsets", refSets);


		return new ResponseEntity<Result<Map<String,Object>>>(r, HttpStatus.OK);
		
	         
    }
	
	@RequestMapping( method = RequestMethod.GET, value = "/{refsetId}/versions", produces = "application/json")
	@ApiOperation( value = "Api to get all available published/released version of a refset.", 
		notes = "Get all available published/released version of a given refset uuid. "
				+ "A Lightweight service for user who are only intrested in getting refset details not their members" )
    public ResponseEntity<Result< Map<String, Object>>> getRefsetVersions( @PathVariable( value = "refsetId" ) String refsetId ) throws Exception {
		
		logger.debug("Getting refset details");

		Result<Map<String, Object>> response = getResult();
		
		response.getMeta().add( linkTo( methodOn( RefsetBrowseController.class, refsetId).getRefsetVersions(refsetId) ).withRel("versions") );


		
		Set<Integer> versions =  bService.getRefsetVersions(refsetId);
		
		response.getData().put("versions", versions);
		
		return new ResponseEntity<Result<Map<String,Object>>>(response, HttpStatus.OK);
		
	       
    }

	
}
