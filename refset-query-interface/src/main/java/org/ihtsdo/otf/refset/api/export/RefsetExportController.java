/**
 * 
 */
package org.ihtsdo.otf.refset.api.export;

import javax.servlet.http.HttpServletResponse;

import org.ihtsdo.otf.refset.common.Utility;
import org.ihtsdo.otf.refset.service.export.ExportService;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

/**
 * @author Episteme Partners
 *
 */
@RestController
@Api(value="RefsetExport", description="Service export refset and their members in RF2 format", position = 3)
@RequestMapping("/v1.0/refsets")
public class RefsetExportController {
	
	private static final Logger logger = LoggerFactory.getLogger(RefsetExportController.class);
	
	@Autowired
	private ExportService eService;

	@RequestMapping( method = RequestMethod.GET, value = "/{refsetId}/export", produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE})
	@ApiOperation( value = "Export a Refset in RF2 format" )
	@PreAuthorize("hasRole('ROLE_USER')")
    public @ResponseBody void exportRF2( @PathVariable String refsetId, HttpServletResponse resp) throws Exception {
		
		logger.debug("Exporting an existing refset {} in rf2 format", refsetId);
		resp.setHeader("content-type", "application/csv;charset=UTF-8");
	    resp.setHeader("Content-Disposition", "attachment; filename=\"rel2_Refset_SimpleDelta_INT_" + Utility.getDate(new DateTime()) + ".rf2\"");
	    
	    final CsvPreference RF2_PREF = new CsvPreference.Builder('"', '\t', "\r\n").build();

	    final ICsvListWriter csvWriter = new CsvListWriter(resp.getWriter(), RF2_PREF);
 
        eService.getRF2Payload(csvWriter, refsetId);
        
        csvWriter.close();
     
        return;
    }
	

}
