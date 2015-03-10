/**
 * 
 */
package org.ihtsdo.otf.refset.api.maintenance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ihtsdo.otf.refset.common.Result;
import org.ihtsdo.otf.refset.service.termserver.TermServer;
import org.ihtsdo.otf.terminology.domain.SnomedConcept;
import org.ihtsdo.otf.terminology.domain.SnomedRefset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
 * @author 
 *
 */
@RestController
@Api(value="Maintenance", description="Service to perform application maintenance and troubleshooting ")
@RequestMapping("/v1/refsets")
public class RefsetTestController {
	
	private static final Logger logger = LoggerFactory.getLogger(RefsetTestController.class);

	@Autowired
	private TermServer tService;
	
	@RequestMapping( method = RequestMethod.GET, value="/refset/{version}/header/{id}", produces = "application/json" )
	@ApiOperation( value = "Retrieves refset from terminology server", 
		notes = "Retrieves refset from terminology server for a given version and refset id" )
    public ResponseEntity<Result< Map<String, Object>>> getRefset( @PathVariable( value = "id") String id, 
    		@PathVariable( value = "version") String version ) throws Exception {
		
		logger.debug("get refset");

		Result<Map<String, Object>> response = new Result<Map<String, Object>>();

		SnomedRefset refset = tService.getRefset(id, version);

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("refsets", refset);
		response.setData(data);
		
		return new ResponseEntity<Result<Map<String,Object>>>(response, HttpStatus.OK);
		
	         
    }
	
	
	@RequestMapping( method = RequestMethod.POST, value="/concepts/{version}/", produces = "application/json" )
	@ApiOperation( value = "Retrieves list concepts from terminology server", 
		notes = "Retrieves list of concepts from terminology server for given version and set of concept ids" )
    public ResponseEntity<Result< Map<String, Object>>> getConcepts( @RequestBody(required = true) List<String> ids, 
    		@PathVariable( value = "version") String version ) throws Exception {
		
		logger.debug("Existing refsets");

		Result<Map<String, Object>> response = new Result<Map<String, Object>>();

		Map<String, SnomedConcept> concepts = tService.getConcepts(ids, version);

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("concepts", concepts);
		response.setData(data);
		
		return new ResponseEntity<Result<Map<String,Object>>>(response, HttpStatus.OK);
		
	         
    }
	
	

	
}
