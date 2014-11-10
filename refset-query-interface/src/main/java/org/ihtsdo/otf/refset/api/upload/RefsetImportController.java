/**
 * 
 */
package org.ihtsdo.otf.refset.api.upload;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.ihtsdo.otf.refset.common.Meta;
import org.ihtsdo.otf.refset.common.Result;
import org.ihtsdo.otf.refset.controller.RefsetBrowseController;
import org.ihtsdo.otf.refset.service.upload.ImportService;
import org.ihtsdo.otf.refset.service.upload.Rf2VerificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

@RestController
@Api(value="RefsetImport", description="Service to import refset and their members in RF2 format", position = 4)
@RequestMapping("/v1.0/refsets")
public class RefsetImportController {
	
	private static final Logger logger = LoggerFactory.getLogger(RefsetImportController.class);
	private static final String SUCCESS = "Success";

	@Autowired
	private ImportService iService;
	
	@Autowired
	private Rf2VerificationService vService;

	@RequestMapping( method = RequestMethod.POST, value = "/{refsetId}/import", produces = {MediaType.APPLICATION_JSON_VALUE})
	@ApiOperation( value = "Import a Refset in RF2 format" )
	@PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Result< Map<String, Object>>> importRF2( @RequestParam("file") MultipartFile file, @PathVariable String refsetId) throws Exception {
		
		logger.debug("Importing an existing refset {} in rf2 format");
		
		Result<Map<String, Object>> result = new Result<Map<String, Object>>();
		Meta m = new Meta();
		m.add( linkTo( methodOn( RefsetBrowseController.class ).getRefsetHeader(refsetId)).withSelfRel());
		
		result.setMeta(m);

		//call verify service
		final InputStream is;
		if (file.isEmpty()) {
			
			throw new IllegalArgumentException("Please check file supplied.");
			
		} else if(file.getOriginalFilename().endsWith(".gz")) {
			
			is = new GZIPInputStream(file.getInputStream());
	        
		} else {
			
			is = file.getInputStream();
		}
		
		
		//call import service
		Map<String, String> outcome = iService.importFile(is, refsetId);
	    
	    is.close();
	    		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("outcome", outcome);
		
		result.setData(data);
		
		m.setMessage(SUCCESS);
		m.setStatus(HttpStatus.OK);
		
		return new ResponseEntity<Result<Map<String,Object>>>(result, HttpStatus.OK);


	    
		
    }
	

}
