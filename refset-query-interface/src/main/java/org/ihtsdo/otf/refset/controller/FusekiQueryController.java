package org.ihtsdo.otf.refset.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.ihtsdo.otf.refset.service.RefsetQueryException;
import org.ihtsdo.otf.refset.service.RefsetQueryService;
import org.ihtsdo.otf.refset.service.RefsetUpdateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import scala.annotation.meta.getter;

/**
 * Controller for endpoint calls .
 */
@Controller
public class FusekiQueryController {
	
	@Resource(name = "fusekiRefsetQueryService")
	private RefsetQueryService qService;
	
	@Resource(name = "fusekiRefsetUpdateService")
	private RefsetUpdateService uService;
	
	@Resource(name = "jena.outputMapper")
	private Map<String, String> outputMapper;
	
	private static final Logger logger = LoggerFactory.getLogger(FusekiQueryController.class);

    
    @RequestMapping(method=RequestMethod.GET, value="sparql/jena/custom")
    public @ResponseBody String getRdfResult(@RequestParam(value="query", required=true) String query, 
    		@RequestParam(value="output", defaultValue="json") String output) {
    	
    	try {
    		
			return qService.executeQuery(query, outputMapper.get(output));
			
		} catch (RefsetQueryException e) {
			
			// TODO Auto-generated catch block
			String message = String.format("Error occurred during query : %s \n Stack Trace {%s}", e.getMessage(), e.getCause());
			logger.error(message);
	    	return message;
	    	
		}         
    	
    }
    
    @RequestMapping(method=RequestMethod.GET, value="sparql/jena/stream")
    public void query(@RequestParam(value="query", required=true) String query, 
    		@RequestParam(value="output", defaultValue="json") String output, OutputStream out) {
    	
    	try {
    		
    		qService.executeQuery(query, out, outputMapper.get(output));
    		out.flush();
    		
		} catch (RefsetQueryException | IOException e) {
			// TODO Auto-generated catch block
			String message = String.format("Error occurred during query : %s \n Stack Trace {%s}", e.getMessage(), e.getCause());
			logger.error(message);
			try {
				out.write(IOUtils.toByteArray(message));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				logger.error(e1.getMessage());
			}
		}         
    	
    }
    
    @RequestMapping(method=RequestMethod.GET, value="sparql/jena/update")
    public @ResponseBody String update(@RequestParam(value="query", required=true) String query) {
    	
    	try {
    		
    		return uService.executeUpdate(query);
    		
		} catch (RefsetQueryException e) {
			// TODO Auto-generated catch block
			String message = String.format("Error occurred during query : %s \n Stack Trace {%s}", e.getMessage(), e.getCause());
			logger.error(message);
			
			return message;
		}         
    	
    }
}