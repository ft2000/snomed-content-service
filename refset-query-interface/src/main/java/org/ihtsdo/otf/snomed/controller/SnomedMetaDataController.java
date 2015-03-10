/**
 * 
 */
package org.ihtsdo.otf.snomed.controller;

import static org.ihtsdo.otf.refset.common.Utility.getResult;

import java.util.Map;
import java.util.Set;

import org.ihtsdo.otf.refset.common.Result;
import org.ihtsdo.otf.refset.service.termserver.TermServer;
import org.ihtsdo.otf.snomed.service.RefsetMetadataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

/**
 * Controller to retrieve SNOMED® CT meta data.
 *
 */
@RestController
@Api(value="SNOMEDCT", 
	description="Services to lookup SNOMED®CT Terminology data as well as meta data", 
	produces = MediaType.APPLICATION_JSON_VALUE,
	consumes = MediaType.APPLICATION_JSON_VALUE)
@RequestMapping("/v1/snomed")
public class SnomedMetaDataController {
	
	private static final Logger logger = LoggerFactory.getLogger(SnomedMetaDataController.class);

	@Autowired
	private TermServer tService;
	
	@Autowired
	private RefsetMetadataService mdService;

	
	@RequestMapping( method = RequestMethod.GET, value="/releases/")
	@ApiOperation( value = "Retrieves list of available releases from terminology server", 
		notes = "Retrieves list of available releases from terminology server" )
    public ResponseEntity<Result< Map<String, Object>>> getReleases() throws Exception {
		
		logger.debug("Existing releases");

		Result<Map<String, Object>> response = getResult();

		Map<String, String> releases = tService.getReleases();
		
		Set<String> versions = releases.keySet();

		response.getData().put("releases", versions);
		
		return new ResponseEntity<Result<Map<String,Object>>>(response, HttpStatus.OK);
		
	         
    }
	
	@RequestMapping( method = RequestMethod.GET, value="/releases/latest" )
	@ApiOperation( value = "Get latest release", 
		notes = "Get latest release out of all available releases in terminology server" )
    public ResponseEntity<Result< Map<String, Object>>> getLatestRelease() throws Exception {
		
		logger.debug("Latest  release");

		Result<Map<String, Object>> response = getResult();

		String latestRls = tService.getLatestRelease();
		
		response.getData().put("latestRelease", latestRls);
		
		return new ResponseEntity<Result<Map<String,Object>>>(response, HttpStatus.OK);
		
	         
    }
	
	@RequestMapping( method = RequestMethod.GET, value="/clinicalDomains/" )
	@ApiOperation( value = "Retrieves list of available clinical domains", 
		notes = "Retrieves list of available clinical domains" )
    public ResponseEntity<Result< Map<String, Object>>> getClinicalDomain() throws Exception {
		
		logger.debug("Clinical Domains");

		Result<Map<String, Object>> response = getResult();

		Map<String, String> clincaldomains = mdService.getClinicalDomains();
		response.getData().put("clinicalDomains", clincaldomains);
		
		return new ResponseEntity<Result<Map<String,Object>>>(response, HttpStatus.OK);
		
	         
    }
	
	@RequestMapping( method = RequestMethod.GET, value="/extensions/" )
	@ApiOperation( value = "Retrieves map of available SNOMED® CT extensions", 
		notes = "Retrieves map of available SNOMED® CT extensions. Extension is are mapped with system generated code."
				+ "This code should be used to retrieve extension name" )
    public ResponseEntity<Result< Map<String, Object>>> getExtensions() throws Exception {
		
		logger.debug("SNOMED® CT extensions");
		Result<Map<String, Object>> response = getResult();


		Map<String, String> extensions = mdService.getExtensions();
		
		response.getData().put("extensions", extensions);
		
		return new ResponseEntity<Result<Map<String,Object>>>(response, HttpStatus.OK);
		
	         
    }
	
	@RequestMapping( method = RequestMethod.GET, value="/originCountries/" )
	@ApiOperation( value = "Retrieves iso 3166 countries with 2 digit code and country display name as key", 
		notes = "Retrieves iso 3166 countries with 2 digit code as value and country display name as key" )
    public ResponseEntity<Result< Map<String, Object>>> getOriginCountries() throws Exception {
		
		logger.debug("Refset origin countries");
		Result<Map<String, Object>> response = getResult();


		Map<String, String> countries = mdService.getISOCountries();
		
		response.getData().put("originCountries", countries);
		
		return new ResponseEntity<Result<Map<String,Object>>>(response, HttpStatus.OK);
		
	         
    }
	
	@RequestMapping( method = RequestMethod.GET, value="/languages/" )
	@ApiOperation( value = "Retrieves ISO-639 Languages", 
		notes = "Retrieves ISO-639 Languages" )
    public ResponseEntity<Result< Map<String, Object>>> getLanguages() throws Exception {
		
		logger.debug("Refset origin countries");
		Result<Map<String, Object>> response = getResult();


		Map<String, String> languages = mdService.getISOLanguages();
		
		response.getData().put("languages", languages);
		
		return new ResponseEntity<Result<Map<String,Object>>>(response, HttpStatus.OK);
		
	         
    }
	
	

	
}
