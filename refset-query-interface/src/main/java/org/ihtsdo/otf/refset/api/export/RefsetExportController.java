/**
 * 
 */
package org.ihtsdo.otf.refset.api.export;

import java.util.Map;

import javax.annotation.Resource;

import org.ihtsdo.otf.refset.common.Result;
import org.ihtsdo.otf.refset.service.RefsetBrowseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
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
@Api(value="RefsetExport", description="Service export refset and their members in RF2 format")
@RequestMapping("/v1.0/refsets")
public class RefsetExportController {
	
	private static final Logger logger = LoggerFactory.getLogger(RefsetExportController.class);

	private static final String SUCESS = "Success";
	
	@Resource(name = "browseGraphService")
	private RefsetBrowseService bService;

	@RequestMapping( method = RequestMethod.POST, value = "/{refsetId}/export",  produces = "application/json")
	@ApiOperation( value = "Export a Refset" )
	@PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Result< Map<String, Object>>> exportRF2( @PathVariable String refsetId) {
		
		logger.debug("Exporting an existing refset {}", refsetId);

		Result<Map<String, Object>> response = new Result<Map<String, Object>>();
		
		return null;
     
    }
	

}
