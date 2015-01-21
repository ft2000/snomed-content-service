/**
* Copyright 2014 IHTSDO
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.ihtsdo.otf.refset.api.diff.report;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.ihtsdo.otf.refset.exception.ValidationException;
import org.ihtsdo.otf.refset.service.diffreport.DiffReportRecord;
import org.ihtsdo.otf.refset.service.diffreport.DiffReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

/**
 *
 */
@RestController
@Api(value="Refset Diff Report", description="Service to create a diff report based on refset data")
@RequestMapping("/v1.0/refsets")
public class DiffReportController {

	private static final Logger logger = LoggerFactory.getLogger(DiffReportController.class);
	private static final String OCTMAP = "octmap";
	private static final String OCTMAP_FULL = "octmapfull";
	private static final String REFSET_IDS = "refsetids";
	
	@Autowired
	private DiffReportService service;
	
	@RequestMapping( method = RequestMethod.POST, value = "/generateDiffReport",  
			produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE})
	@ApiOperation( value = "Generate a refset diff report",
			notes = "This api call generates refset diff report in xlsx foramt based on following input files"
					+ "1. Concept snapshot e.g sct2_Concept_Snapshot_INT_20150131.txt"
					+ "2. Refset full file e.g. Refset_GPFPSimpleFull_INT_20140930.txt"
					+ "3. Concept full file e.g. sct2_Concept_Full_INT_20150131.txt"
					+ "4. Refset attribute value full file e.g. der2_cRefset_AttributeValueFull_INT_20150131.txt"
					+ "5. Description snapshot file e.g. sct2_Description_Snapshot-en_INT_20150131.txt"
					+ "6. Association reference snapshot file e.g. der2_cRefset_AssociationReferenceSnapshot_INT_20150131.txt"
					+ "7. Refset identifier file e.g. refset-identifiers.txt"
					)
	public @ResponseBody String generate(
			@RequestParam("file_refset_full") MultipartFile file_refset_full,
			@RequestParam("file_refset_identifiers") MultipartFile file_refset_identifiers,
			@RequestParam("file_refset_gpfp") MultipartFile file_refset_gpfp,
			@RequestParam("releaseDate") String releaseDate,
			HttpServletResponse resp) {
		long suffix = System.currentTimeMillis();

		String location = System.getProperty("catalina.home") + File.separator + "refset_diff_report_" + suffix;

		logger.debug("Generate refset diff report for suffix : {} and files location : {}", suffix, location);

		StringBuilder sb = new StringBuilder();
		OutputStream os = null;
		try {
			
			if (!"20150131".equals(releaseDate)) {
				
				throw new ValidationException("Please select correct release date");
			}
			
			if(!file_refset_identifiers.isEmpty()) {
				
				//check if required headers are available
				String [] headers = getHeaders(file_refset_identifiers);
				
				//above will never be null
				if (headers.length == 2) {
					
					//write file to disk
					writeFileToDisk(file_refset_identifiers, location);
	                
				} else {
					
					sb.append("Invalid refset identifier file. please check and try again \n");

				}
				
			} else {
				
				sb.append("Invalid refset identifier file. please check and try again \n");

			}
			
			
			if(!file_refset_full.isEmpty()) {
				
				//check if required headers are available
				String [] headers = getHeaders(file_refset_full);
				
				//above will never be null
				if (headers.length == 6) {
					
					//write file to disk
					writeFileToDisk(file_refset_full, location);
	                
				} else {
					
					sb.append("Invalid refset full file. please check and try again \n");

					
				}
				
			} else {
				
				sb.append("Invalid refset full file. please check and try again \n");

			}
			
			
			if(!file_refset_gpfp.isEmpty()) {
				
				//check if required headers are available
				String [] headers = getHeaders(file_refset_gpfp);
				
				//above will never be null
				if (headers.length == 6) {
					
					//write file to disk
					writeFileToDisk(file_refset_gpfp, location);
	                
				} else {
					
					sb.append("Invalid refset gpfp snapshot file. please check and try again \n");

					
				}
				
			} else {
				
				sb.append("Invalid refset gpfp  snapshot file. please check and try again");

				
			}
			
			if (sb.toString().isEmpty()) {
				
				//drop existing tables
				service.dropTables(OCTMAP, Long.toString(suffix));
				service.dropTables(OCTMAP_FULL, Long.toString(suffix));
				service.dropTables(REFSET_IDS, Long.toString(suffix));


				logger.debug("creating tables");

				service.createTables(OCTMAP, Long.toString(suffix));
				service.createTables(OCTMAP_FULL, Long.toString(suffix));
				service.createTables(REFSET_IDS, Long.toString(suffix));
				
				//load data
				logger.debug("Loading data from {} temp files to db", location);

				service.loadRefsetIdentifier(location + File.separator + file_refset_identifiers.getOriginalFilename(), "refsetids_"+suffix);
				service.loadFileToTable(location + File.separator + file_refset_full.getOriginalFilename(), "octmapfull_"+suffix);
				service.loadFileToTable(location + File.separator + file_refset_gpfp.getOriginalFilename(), "octmap_"+suffix);

				logger.debug("Getting report data");

				List<DiffReportRecord> drs = service.getDiffReportRecords(Long.toString(suffix), releaseDate);
				
				os = resp.getOutputStream();
						
				resp.setHeader("content-type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8");
			    resp.setHeader("Content-Disposition", "attachment; filename=\"GPFP Report.xlsx\"");

			    //generate report
				logger.debug("Writing report");

				service.writeDiffReport(drs, os);
				
			}
			
		} catch (ValidationException e) {
			
			logger.error("Validation exception in report generation", e);

			sb.append("\n" + e.getMessage());
			
		} catch (Exception e) {
			
			logger.error("Exception in report generation", e);

			sb.append("\n" + "Unknown error while generating diff report");
			
		} finally {
			
			//remove directory
			try {
				
				logger.debug("Removing temp files {}", location);

				FileUtils.deleteDirectory(new File(location));
				
			} catch (IOException e) {

				logger.error("IO exception while deleting temp directory and files", e);
			}
			
			try {
				
				service.dropTables(OCTMAP, Long.toString(suffix));
				service.dropTables(OCTMAP_FULL, Long.toString(suffix));
				service.dropTables(REFSET_IDS, Long.toString(suffix));
				
			} catch (Exception e2) {
				
				logger.error("Error while cleaning db", e2);

			}
			
			if (os != null) {
				
				try {
					
					os.close();
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					logger.error("Error closing io resources ", e);
				}
			}
		}
		
		if (sb.toString().isEmpty()) {
			
			return "";
		}

		return sb.toString();
    }
	
	

	/**
	 * @param tsv
	 * @return
	 * @throws ValidationException
	 */
	private String[] getHeaders(MultipartFile tsv) throws ValidationException {
		
		logger.debug("Processing {} for headers", tsv.getOriginalFilename());

		BufferedReader br = null;
		String line = "";  

		try { 
			
			br = new BufferedReader(new InputStreamReader(tsv.getInputStream()));
			while ((line = br.readLine()) != null) { 
				
				String[] headers = StringUtils.splitByWholeSeparator(line, "\t");
  
				
				if (headers == null) {
					
					throw new ValidationException("Invalid file provided : " + tsv);

				}
				return headers;
			} 
			  
			   
		} catch (FileNotFoundException e) {
			
			logger.error("File not found {}", tsv, e);

		} catch (IOException e) {

			logger.error("IO exception for file {}", tsv, e);
			
		} finally {
  
			if(br != null) {
				
				try {
					
					br.close();
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					logger.error("Error closing IO resource", e);
				}
			}
		}
		
		throw new ValidationException("Invalid file provided : " + tsv);
			   
	}
	
	
	/**
	 * @param iFile
	 * @throws ValidationException
	 */
	private void writeFileToDisk(MultipartFile iFile, String location) throws ValidationException {
		
		logger.debug("Writing file {} to disk ", iFile.getOriginalFilename());

		BufferedOutputStream writer = null;

		try {
			
			byte[] bytes = iFile.getBytes();

	        File dir = new File(location);
	        
	        if (!dir.exists()) {
				
		        dir.mkdirs();

			}
	        
	        File file = new File(dir.getAbsolutePath()
	                + File.separator + iFile.getOriginalFilename());
	        writer =
	                new BufferedOutputStream(new FileOutputStream(file));
	        writer.write(bytes);
	        writer.flush();

	        
		} catch (IOException e) {
			
			logger.error("Error closing IO resource", e);
			throw new ValidationException("Invalid file provided : " + iFile.getOriginalFilename());

			
		} finally {
			
			if (writer != null) {
				
				try {
					
					writer.close();
					
				} catch (IOException e) {

					logger.error("Error closing IO resource", e);
				}
			}
		}
		        

	}
		    
	

}
