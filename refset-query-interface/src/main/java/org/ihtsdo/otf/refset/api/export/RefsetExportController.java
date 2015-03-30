/**
 * 
 */
package org.ihtsdo.otf.refset.api.export;

import static org.ihtsdo.otf.refset.common.Utility.getUserDetails;

import javax.servlet.http.HttpServletResponse;

import org.ihtsdo.otf.refset.common.Utility;
import org.ihtsdo.otf.refset.service.export.ExportService;
import org.ihtsdo.otf.refset.service.matrix.ActivityMatrixService;
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
 *Controller to support download of a RF2 file refset
 */
@RestController
@Api(value="Refset", description="Service to export refset and its members in RF2 format", position = 3)
@RequestMapping("/v1/refsets")
public class RefsetExportController {
	
	private static final Logger logger = LoggerFactory.getLogger(RefsetExportController.class);
	
	@Autowired
	private ExportService eService;
	
	@Autowired
	private ActivityMatrixService matrixService;

	@RequestMapping( method = RequestMethod.GET, value = "/{refsetId}/export", produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE})
	@ApiOperation( value = "Export a Refset in RF2 format", notes = "Export a refset of a given refset id in RF2 format" )
    public @ResponseBody void exportRF2( @PathVariable String refsetId, HttpServletResponse resp) throws Exception {
		
		logger.debug("Exporting an existing refset {} in rf2 format", refsetId);
		resp.setHeader("content-type", "application/csv;charset=UTF-8");
	    resp.setHeader("Content-Disposition", "attachment; filename=\"rel2_Refset_SimpleDelta_INT_" + Utility.getDate(new DateTime()) + ".txt\"");
	    
	    final CsvPreference RF2_PREF = new CsvPreference.Builder('"', '\t', "\r\n").build();

	    final ICsvListWriter csvWriter = new CsvListWriter(resp.getWriter(), RF2_PREF);
 
	    eService.getRF2Payload(csvWriter, refsetId);

		//capture user event. This is Async call
		matrixService.addViewEvent(refsetId, getUserDetails().getUsername());
		
	    csvWriter.close();

        
        
     
        return;
    }
	

}
