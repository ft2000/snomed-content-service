/**
 * 
 */
package org.ihtsdo.otf.snomed.controller;

import java.util.HashMap;
import java.util.Map;

import org.ihtsdo.otf.refset.common.Result;
import org.ihtsdo.otf.refset.service.termserver.TermServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

/**
 * @author 
 *
 */
@RestController
@Api(value="Terminology Meta Data", description="Service to get terminology meta data ")
@RequestMapping("/v2/snomed")
public class SnomedMetaDataController {
	
	private static final Logger logger = LoggerFactory.getLogger(SnomedMetaDataController.class);

	@Autowired
	private TermServer tService;

	
	@RequestMapping( method = RequestMethod.GET, value="/releases/", produces = "application/json" )
	@ApiOperation( value = "Retrieves list of available releases from terminology server", 
		notes = "Retrieves list of concepts from terminology server for given version and set of concept ids" )
    public ResponseEntity<Result< Map<String, Object>>> getReleases() throws Exception {
		
		logger.debug("Existing releases");

		Result<Map<String, Object>> response = new Result<Map<String, Object>>();

		Map<String, String> releases = tService.getReleases();

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("releases", releases);
		response.setData(data);
		
		return new ResponseEntity<Result<Map<String,Object>>>(response, HttpStatus.OK);
		
	         
    }
	
	

	
}
