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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.ihtsdo.otf.refset.exception.ValidationException;
import org.ihtsdo.otf.refset.service.diffreport.DiffReportRecord;
import org.ihtsdo.otf.refset.service.diffreport.DiffReportService;
import org.ihtsdo.otf.refset.service.diffreport.DiffReportServiceV2;
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
import org.springframework.web.multipart.MultipartFile;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

/**
 *
 */
@RestController
@Api(value="Refset", description="Service to generate a diff report based on refset and suggest release data")
@RequestMapping("/v1.0/refsets")
public class DiffReportController {

	private static final Logger logger = LoggerFactory.getLogger(DiffReportController.class);
	private static final String OCTMAP = "octmap";
	private static final String OCTMAP_FULL = "octmapfull";
	private static final String REFSET_IDS = "refsetids";
	private static final Map<String, String> validReleaseDates = new HashMap<String, String>();
	
	static {
		
		validReleaseDates.put("20150131", "2015-01-31");
		validReleaseDates.put("20140131", "2014-01-31");
		validReleaseDates.put("20140731", "2014-07-31");

		
	}
	
	@Autowired
	private DiffReportService service;
	
	@Autowired
	private DiffReportServiceV2 serviceV2;
	
	@RequestMapping( method = RequestMethod.POST, value = "/generateDiffReport",  
			produces = {MediaType.APPLICATION_JSON_VALUE})
	@ApiOperation( value = "Generate a refset diff report",
			notes = "This api call generates refset diff report in xlsx foramt based on following input files"
					+ " \n 1. Refset simple full file e.g der2_Refset_GPFPSimpleSnapshot_INT_20140930.txt"
					+ " \n 2. Refset simple snapshot file e.g. der2_Refset_SimpleFull_INT_20140731.txt"
					)
	public @ResponseBody String generate(
			@RequestParam("file_refset_full") MultipartFile file_refset_full,
			@RequestParam("file_refset_gpfp") MultipartFile file_refset_gpfp,
			@RequestParam("releaseDate") String releaseDate,
			HttpServletResponse resp) throws IOException {
		long suffix = System.currentTimeMillis();

		String location = System.getProperty("catalina.home") + File.separator + "refset_diff_report_" + suffix;

		logger.debug("Generate refset diff report for suffix : {} and files location : {}", suffix, location);

		StringBuilder sb = new StringBuilder();
		OutputStream os = null;
		try {
			
			if (!validReleaseDates.keySet().contains(releaseDate)) {
				
				throw new ValidationException("Please select correct release date");
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

				service.loadFileToTable(location + File.separator + file_refset_full.getOriginalFilename(), "octmapfull_"+suffix);
				service.loadFileToTable(location + File.separator + file_refset_gpfp.getOriginalFilename(), "octmap_"+suffix);

				//this is after above file upload
				service.loadRefsetIdentifier(Long.toString(suffix), releaseDate);

				logger.debug("Getting report data");

				List<DiffReportRecord> drs = service.getDiffReportRecords(Long.toString(suffix), releaseDate);
				String gFileName= "Refset_Diff_Report_"+ System.currentTimeMillis();
				String absFileName = System.getProperty("catalina.home") + File.separator + "refset_diff_report_generated_" + gFileName;

				os = new FileOutputStream(new File(absFileName));;

			    //generate report
				logger.debug("Writing report");

				service.writeDiffReport(drs, os);
				
				sb.append(gFileName);
				
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
		
		return sb.toString();
    }
	
	
	@RequestMapping( method = RequestMethod.GET, value = "/downloadDiffReport/{fileName}",  
			produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE, MediaType.APPLICATION_JSON_VALUE})
	@ApiOperation( value = "Download a refset diff report",
			notes = "Download a given file as refset diff report in xlsx format")
	public @ResponseBody String generate(@PathVariable(value = "fileName") String fileName,
			HttpServletResponse resp) throws IOException {

		/* file name coming with " added*/
		if (fileName != null & (fileName.startsWith("\"") || fileName.endsWith("\""))) {
			
			fileName = fileName.replaceAll("\"", "");
			
		}
		String absFileName = System.getProperty("catalina.home") + File.separator + "refset_diff_report_generated_" + fileName;

		logger.debug("Downloading refset diff report for {}", absFileName);

		OutputStream os = null;
		try {
			

			
			os = resp.getOutputStream();
					
			resp.setHeader("content-type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8");
		    resp.setHeader("Content-Disposition", "attachment; filename=\"Refset Diff Report.xlsx\"");

		    //generate report
			logger.debug("downloading report");

			
			IOUtils.copy(new FileInputStream(new File(absFileName)), os);
			
			os.flush();
			
		} catch (Exception e) {
			
			logger.error("Exception in report generation", e);

			return "Unknown error while downloading diff report";
			
		} finally {
			
			//remove directory
			try {
				
				logger.debug("Removing temp files {}", absFileName);

				FileUtils.forceDeleteOnExit(new File(absFileName));
				
			} catch (IOException e) {

				logger.error("IO exception while deleting temp directory and files", e);
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
		
		return "";
    }
	
	
	
	@RequestMapping( method = RequestMethod.POST, value = "/diffReport",  
			produces = {MediaType.APPLICATION_JSON_VALUE})
	@ApiOperation( value = "Generate a refset diff report",
			notes = "This api call generates refset diff report in xlsx foramt based on following input files"
					+ " \n 1. Refset simple full file e.g der2_Refset_GPFPSimpleSnapshot_INT_20140930.txt"
					+ " \n 2. Refset simple snapshot file e.g. der2_Refset_SimpleFull_INT_20140731.txt"
					)
	public @ResponseBody String generate(
			@RequestParam("file_refset_full") MultipartFile file_refset_full,
			@RequestParam("releaseDate") String releaseDate,
			@RequestParam("email") String email) throws IOException {
		long suffix = System.currentTimeMillis();

		String location = System.getProperty("catalina.home") + File.separator + "refset_diff_report_" + suffix;

		logger.debug("Generate refset diff report for suffix : {} and files location : {}", suffix, location);

		StringBuilder sb = new StringBuilder();

		try {
			
			if (!validReleaseDates.keySet().contains(releaseDate)) {
				
				throw new ValidationException("Please select correct release date");
			}
			
			
			if(!file_refset_full.isEmpty()) {
				
				//check if required headers are available
				String [] headers = getHeaders(file_refset_full);
				
				//above will never be null
				if (headers.length == 6) {
					
					//write file to disk
					writeFileToDisk(file_refset_full, location);
	                
				} else {
					
					sb.append("Invalid refset file. please check and try again \n");

					
				}
				
			} else {
				
				sb.append("Invalid refset file. please check and try again \n");

			}

			
			if (sb.toString().isEmpty()) {
				
				//call service. an Async call. Report is sent over email
				serviceV2.generateReport(location + File.separator + file_refset_full.getOriginalFilename(),
						email, validReleaseDates.get(releaseDate));
				sb.append("Refset Diff request is successfully received. "
						+ "We are processing your request and you will shortly recieve a notification in email");

			}
			
		} catch (ValidationException e) {
			
			logger.error("Validation exception in report generation", e);

			sb.append("\n" + e.getMessage());
			
		} catch (Exception e) {
			
			logger.error("Exception in report generation", e);

			sb.append("\n" + "Unknown error while generating diff report");
			
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
