/**
 * 
 */
package org.ihtsdo.otf.snomed.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.ihtsdo.otf.refset.common.Meta;
import org.ihtsdo.otf.refset.common.Result;
import org.ihtsdo.otf.snomed.service.ConceptLookupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@Api(value="Type  look up service", description="Service to lookup type name and id", position = 4)
@RequestMapping("/v1.0/snomed")
public class TypeLookupController {
	
	private static final Logger logger = LoggerFactory.getLogger(TypeLookupController.class);

	private static final String SUCESS = "Success";

	
	@Resource(name = "conceptLookService")
	private ConceptLookupService cService;
	
	@RequestMapping( method = RequestMethod.GET, value = "/componentTypes", produces = "application/json" )
	@ApiOperation( value = "Api to get allowed component types - a collection of ids and names." )
    public ResponseEntity<Result< Map<String, Object>>> getComponentTypes() {
		
		logger.debug("Getting getComponentTypes");

		Result<Map<String, Object>> response = new Result<Map<String, Object>>();
		
		Meta m = new Meta();

		m.add( linkTo( methodOn( TypeLookupController.class).getComponentTypes() ).withSelfRel() );
		response.setMeta(m);

		//try {
			
			Map<String, String> types = new TreeMap<String, String>(); //cService.getTypes("900000000000460005");
			types.put("900000000000461009", "Concept type component (foundation metadata concept)");
			types.put("900000000000462002", "Description type component (foundation metadata concept)");
			types.put("900000000000464001", "Reference set member type component (foundation metadata concept)");
			types.put("900000000000463007", "Relationship type component (foundation metadata concept)");

			Map<String, Object> data = new HashMap<String, Object>();
			data.put("componentTypes", types);
			
			response.setData(data);
			m.setMessage(SUCESS);
			m.setStatus(HttpStatus.OK);
			m.setNoOfRecords(types.size());

		/*} catch (ConceptServiceException e) {

			
			String message = String.format("Error occurred during service call : could not get component types");
			logger.error(message, e);
			m.setMessage(message); 
			m.setStatus(HttpStatus.OK);

			return new ResponseEntity<Result<Map<String,Object>>>(response, HttpStatus.OK);
	    	
		}*/

		return new ResponseEntity<Result<Map<String,Object>>>(response, HttpStatus.OK);
      
    }
	
	@RequestMapping( method = RequestMethod.GET, value = "/refsetTypes", produces = "application/json" )
	@ApiOperation( value = "Api to get allowed refset types - a collection of ids and names." )
    public ResponseEntity<Result< Map<String, Object>>> getRefseTypes() {
		
		logger.debug("Getting getRefseTypes");

		Result<Map<String, Object>> response = new Result<Map<String, Object>>();
		
		Meta m = new Meta();

		m.add( linkTo( methodOn( TypeLookupController.class).getRefseTypes() ).withSelfRel() );
		response.setMeta(m);

		Map<String, String> types;
		//try {
			
			types = new TreeMap<String, String>();//cService.getTypes("900000000000455006");
			types.put("900000000000516008", "Annotation type reference set (foundation metadata concept)");
			types.put("900000000000521006", "Association type reference set (foundation metadata concept)");
			types.put("900000000000480006", "Attribute value type reference set (foundation metadata concept)");
			types.put("447250001", "Complex map type reference set (foundation metadata concept)");
			types.put("609430003", "Concept model reference set (foundation metadata concept)");
			types.put("900000000000538005", "Description format reference set (foundation metadata concept)");
			types.put("609331003", "Extended map type reference set (foundation metadata concept)");
			types.put("900000000000506000", "Language type reference set (foundation metadata concept)");
			types.put("900000000000534007", "Module dependency reference set (foundation metadata concept)");
			types.put("447258008", "Ordered type reference set (foundation metadata concept)");
			types.put("900000000000512005", "Query specification type reference set (foundation metadata concept)");
			types.put("900000000000456007", "Reference set descriptor reference set (foundation metadata concept)");
			types.put("900000000000496009", "Simple map type reference set (foundation metadata concept)");
			types.put("446609009", "Simple type reference set (foundation metadata concept)");
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("refsetTypes", types);
			
			response.setData(data);
			m.setMessage(SUCESS);
			m.setStatus(HttpStatus.OK);
			m.setNoOfRecords(types.size());

			/*} catch (ConceptServiceException e) {

			
			String message = String.format("Error occurred during service call : could not get refset types");
			logger.error(message, e);
			m.setMessage(message); 
			m.setStatus(HttpStatus.OK);

			return new ResponseEntity<Result<Map<String,Object>>>(response, HttpStatus.OK);
	    	
		}*/
		
		return new ResponseEntity<Result<Map<String,Object>>>(response, HttpStatus.OK);
      
    }
	
}
