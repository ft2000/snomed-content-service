/**
 * 
 */
package org.ihtsdo.otf.refset.api.export;

import static org.ihtsdo.otf.refset.common.Utility.getUserDetails;

import javax.servlet.http.HttpServletResponse;

import org.ihtsdo.otf.refset.common.Utility;
import org.ihtsdo.otf.refset.domain.RefsetDTO;
import org.ihtsdo.otf.refset.service.export.ExportService;
import org.ihtsdo.otf.refset.service.matrix.ActivityMatrixService;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

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

	@RequestMapping( method = RequestMethod.GET, value = "/{refsetId}/export", 
			produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE, 
				MediaType.APPLICATION_JSON_VALUE})
	@ApiOperation( value = "Export a Refset in RF2 format or given format", 
		notes = "Export a current version of refset for a given refset id in RF2 format by default."
				+ " If version is provided then export is for that version."
				+ " Export format can be json or rf2(tsv) or tsv. "
				+ " Default format is rf2 where in only eligible members are being exported."
				+ "tsv and json format's export have all the members of refset" )
    public @ResponseBody void exportRF2( @PathVariable String refsetId, 
    		@RequestParam(required = false) Integer version,
    		@ApiParam(name = "format") @RequestParam(defaultValue = "rf2") String format,
    		HttpServletResponse resp) throws Exception {
		
		int v = version == null ? -1 : version;
		
		logger.debug("Exporting an existing refset {} in {} format", refsetId, format);
		logger.debug("Exported version {}", v);

		//capture user event. This is Async call
		matrixService.addExportEvent(refsetId, getUserDetails().getUsername());

		if ("json".equalsIgnoreCase(format)) {
			//this is needed to export refset header + all members (pubished or not published, active or inactive) in json format.

			resp.setHeader("content-type", "application/json;charset=UTF-8");
		    resp.setHeader("Content-Disposition", "attachment; filename=\"rel2_Refset_SimpleDelta_INT_" + Utility.getDate(new DateTime()) + ".json\"");
		    RefsetDTO dto = eService.getJsonPayload(refsetId, v);
		    	        
	        JsonFactory f = new JsonFactory();
	        JsonGenerator g = f.createGenerator(resp.getOutputStream());
	        ObjectMapper om = new ObjectMapper();
	        
	        om.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
	        om.configure(SerializationFeature.INDENT_OUTPUT, true);
	        om.setSerializationInclusion(Include.NON_NULL);
	        om.registerModule(new JodaModule());
	        
	        g.setCodec(om);
	        g.writeObject(dto);
	        
	        g.flush();
	        g.close();

		}  else if("tsv".equalsIgnoreCase(format)) {
			//this is needed to export all members (pubished or not published, active or inactive) in tsv format.
			resp.setHeader("content-type", "application/csv;charset=UTF-8");
		    resp.setHeader("Content-Disposition", "attachment; filename=\"rel2_Refset_SimpleDelta_INT_" + Utility.getDate(new DateTime()) + ".txt\"");

		    final CsvPreference RF2_PREF = new CsvPreference.Builder('"', '\t', "\r\n").build();

		    final ICsvListWriter csvWriter = new CsvListWriter(resp.getWriter(), RF2_PREF);
	 
		    eService.getTSVPayload(csvWriter, refsetId, v);
		    csvWriter.flush();
		    csvWriter.close();

		} else {
			//this is needed to export unpublished members in tsv format for release.

			resp.setHeader("content-type", "application/csv;charset=UTF-8");
		    resp.setHeader("Content-Disposition", "attachment; filename=\"rel2_Refset_SimpleDelta_INT_" + Utility.getDate(new DateTime()) + ".txt\"");

		    final CsvPreference RF2_PREF = new CsvPreference.Builder('"', '\t', "\r\n").build();

		    final ICsvListWriter csvWriter = new CsvListWriter(resp.getWriter(), RF2_PREF);
	 
		    eService.getRF2Payload(csvWriter, refsetId, v);
		    csvWriter.flush();
		    csvWriter.close();

		}
	    
        return;
    }
	

}
