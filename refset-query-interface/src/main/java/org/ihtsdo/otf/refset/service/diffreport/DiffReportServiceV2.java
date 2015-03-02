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
package org.ihtsdo.otf.refset.service.diffreport;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.constraints.NotNull;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.ihtsdo.otf.refset.exception.RefsetServiceException;
import org.ihtsdo.otf.refset.service.termserver.TermServer;
import org.ihtsdo.otf.refset.service.upload.Rf2Record;
import org.ihtsdo.otf.terminology.domain.SnomedConcept;
import org.ihtsdo.otf.terminology.domain.SnomedRefset;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import com.google.common.collect.Iterables;

/*
 *
 */
@EnableAsync
@Service
public class DiffReportServiceV2 {

	private static final Logger LOGGER = LoggerFactory.getLogger(DiffReportServiceV2.class);
	private static final DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyyMMdd");
	private static final String REFSET_DIFF_REPORT_PREFIX = "Refset_Diff_Report_";
	private static Map<String, String> associationCodeMapping = new HashMap<String, String>();
	private static Map<String, String> reasonCode = new HashMap<String, String>();
	
	//this is required as service is not sending the association ref codes
	static {
		associationCodeMapping.put("ALTERNATIVE","900000000000530003");
		associationCodeMapping.put("MOVED_FROM", "900000000000525002"); 
		associationCodeMapping.put("MOVED_TO" , "900000000000524003");
		associationCodeMapping.put("POSSIBLY_EQUIVALENT_TO", "900000000000523009");
		associationCodeMapping.put("REFERS_TO", "900000000000531004");
		associationCodeMapping.put("REPLACED_BY", "900000000000526001");
		associationCodeMapping.put("SAME_AS", "900000000000527005");
		associationCodeMapping.put("SIMILAR_TO", "900000000000529008");
		associationCodeMapping.put("WAS_A", "900000000000528000");
	}
	
	//this is required as service is not sending the reason codes

	static {
		
		reasonCode.put("AMBIGUOUS", "900000000000484002");
		reasonCode.put("DUPLICATE", "900000000000482003");
		reasonCode.put("ERRONEOUS", "900000000000485001");
		reasonCode.put("MOVED_ELSEWHERE", "900000000000487009");
	}
	
	@Autowired
	private DiffReportWriter rWriter;
	
	@Autowired
	private DiffReportSender sender;

	@Autowired
	private TermServer tServer;
	
	@Value("${diffreport.download.service}")
	private String downloadService;
	
	@Value("${diffreport.mail.msg.success}")
	private String successMsg;
	
	@Value("${diffreport.mail.msg.error}")
	private String errorMsg;
	

	/**Generate diff report and write to disk. After writing to disk user is notified. 
	 * User is also notified when there is failure in report generation. 
	 * @param fileLocation
	 * @param email
	 * @param releaseDate
	 * @throws FileNotFoundException
	 */
	@Async
	public void generateReport(String fileLocation, String email, String releaseDate) throws FileNotFoundException {
		
		FileOutputStream os = null;
		
		try {
			
			final List<Rf2Record> sourceRecords = loadRefset(fileLocation);
			List<String> conceptIds = getConceptIds(sourceRecords);
			
			//get all concept by reading file. then create a list of inactive concept by looking into term server.
			Map<String, SnomedConcept> concepts = getConceptDetails(conceptIds, releaseDate);
			
			List<DiffReportRecord> reportRecords = getDiffReport(concepts, sourceRecords, releaseDate);
			
			
			String gFileName= REFSET_DIFF_REPORT_PREFIX + System.currentTimeMillis();
			String absFileName = System.getProperty("catalina.home") + File.separator + "refset_diff_report_generated_" + gFileName;

			LOGGER.debug("writing diff report to disk {}", absFileName);

			os = new FileOutputStream(new File(absFileName));;

			//once computation done write to disk and send an email to user with link to download file
			rWriter.writeDiffReport(reportRecords, os);
			
			//send a success email with a link to download report
			
			String downloadLink = String.format(downloadService, gFileName);
			String msg = String.format(successMsg, downloadLink);
			sender.send(msg, email);
			
		} catch (RefsetServiceException e) {

			LOGGER.error("Error during report generation", e);
			//send an error email
			
			String msg = String.format(errorMsg, e.getLocalizedMessage());
			
			try {
				
				sender.send(msg, email);
				
			} catch (RefsetServiceException e1) {

				//just log the error
				LOGGER.error("Error sending in mail", e);
			}


		} finally {
			
			if (os !=  null) {
				
				try {
					
					os.close();
					
				} catch (IOException e) {

					LOGGER.error("Error closing io resources", e);
				}
			}
			
			//remove directory
			try {
				
				LOGGER.debug("Removing temp files {}", fileLocation);

				FileUtils.deleteDirectory(new File(fileLocation));
				
			} catch (IOException e) {

				LOGGER.error("IO exception while deleting temp directory and files", e);
			}
		}

		
		
		


	}

	/** Get concept details for given ids by looking into terminology server.
	 * @param conceptIds
	 * @return
	 */
	private Map<String, SnomedConcept> getConceptDetails(List<String> conceptIds, String releaseDate) {

		
		Map<String, SnomedConcept> concepts = new HashMap<String, SnomedConcept>();
		
		for (List<String> ids : Iterables.partition(conceptIds, 20)) {
			
			Map<String, SnomedConcept> concetps = tServer.getConcepts(ids, releaseDate);
			
			concepts.putAll(concetps);;

		}

		return concepts;
	}
	
	
	/**Populates {@link DiffReportRecord} which is basically a row of diff report.
	 * @param concepts
	 * @param sourceData
	 * @param releaseDate
	 * @return
	 */
	private List<DiffReportRecord> getDiffReport(Map<String, SnomedConcept> concepts, List<Rf2Record> sourceData, String releaseDate) {
		
		Map<String, SnomedRefset> refsets = getRefset(sourceData, releaseDate);
		
		List<DiffReportRecord> records = new ArrayList<DiffReportRecord>();
		
		for (Rf2Record rf2Record : sourceData) {
			
			SnomedConcept concept = concepts.get(rf2Record.getReferencedComponentId());
			
			if (concept != null && !concept.isActive()) {

				Map<String, List<String>> associtations = concept.getAssociationTargets();
				Set<String> keys = associtations.keySet();
				
				boolean hasAssociation = false;
				for (String key : keys) {
					
					List<String> ids = associtations.get(key);

					Map<String, SnomedConcept> refComponents = tServer.getConcepts(ids, releaseDate);
					for (String id : ids) {
						
						DiffReportRecord rRecord = getBasicDiffReportRecord(concept);

						rRecord.setAssociationDescription(key);
						rRecord.setAssociationRefCode(associationCodeMapping.get(key));
						rRecord.setRefsetId(refsets.get(rf2Record.getRefsetId()).getId());
						rRecord.setRefsetDescription(refsets.get(rf2Record.getRefsetId()).getTerm());
						
						SnomedConcept refComponent = refComponents.get(id);
						LOGGER.debug("New new associtation component {}", refComponent);

						rRecord.setReferenceComponentDescription(refComponent.getTerm());
						rRecord.setReferenceComponentId(refComponent.getId());
						records.add(rRecord);
						LOGGER.debug("Added new report record {}", rRecord);
						
						hasAssociation = true;
					}
				}
				
				//even if no association exist add that concept to report
				if (!hasAssociation) {
					
					DiffReportRecord rRecord = getBasicDiffReportRecord(concept);
					rRecord.setRefsetId(refsets.get(rf2Record.getRefsetId()).getId());
					rRecord.setRefsetDescription(refsets.get(rf2Record.getRefsetId()).getTerm());
					records.add(rRecord);

				}
			}
			
		}
		
		return records;
		
	}
	
	/**Create minimal {@link DiffReportRecord} object out of {@link SnomedConcept} object
	 * @param concept
	 * @return
	 */
	private DiffReportRecord getBasicDiffReportRecord(SnomedConcept concept) {

		DiffReportRecord rRecord = new DiffReportRecord();
		rRecord.setActive(concept.isActive() ? "1" : "0");
		rRecord.setConceptId(concept.getId());
		rRecord.setConceptDescription(concept.getTerm());
		rRecord.setReasonDescription(concept.getInactivationIndicator());
		rRecord.setReasonCode(reasonCode.get(concept.getInactivationIndicator()));
		
		return rRecord;
	}

	/** Retrieves refset details for given refset ids
	 * @param sourceData
	 * @param releaseDate
	 * @return
	 */
	private Map<String, SnomedRefset> getRefset(List<Rf2Record> sourceData, String releaseDate) {
		
		Map<String, SnomedRefset> refsets = new HashMap<String, SnomedRefset>();
		
		for (Rf2Record rf2Record : sourceData) {
			
			String id = rf2Record.getRefsetId();
			if (!refsets.containsKey(id)) {
				
				SnomedRefset refset = tServer.getRefset(id, releaseDate);
				if (refset == null) {
					
					refset = new SnomedRefset();
					refset.setTerm("Not Data available in terminology server");
					refset.setId(id);
				} 
				
				refsets.put(id, refset);

			}

		}
		
		return refsets;
	}

	/**Populate list of concept ids from {@link Rf2Record}s
	 * @param fileLocation
	 * @return
	 */
	private List<String> getConceptIds(@NotNull List<Rf2Record> refset) {

		List<String> conceptIds = new ArrayList<String>();
		
		for (Rf2Record rf2Record : refset) {
			
			conceptIds.add(rf2Record.getReferencedComponentId());
			
		}
	
		return conceptIds;
	}
	
	/**Populate list of {@link Rf2Record}s from user provided refset file
	 * @param fileLocation
	 * @return
	 * @throws RefsetServiceException
	 */
	private List<Rf2Record> loadRefset(String fileLocation) throws RefsetServiceException {
		
		BufferedReader reader = null;
		try {

			reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileLocation)));
			
			String line;

        	int row = 0;
        	
        	List<Rf2Record> rf2RLst = new ArrayList<Rf2Record>();
        	
            while( (line = reader.readLine()) != null ) {
            	
            	row++;

            	if (StringUtils.isEmpty(line) || row == 1) {
                	
            		LOGGER.debug("Line {} is empty skipping", line);
            		continue;
            		
				}
            	
            	String[] columns = StringUtils.splitByWholeSeparator(line, "\t");
            	
        		if (columns != null & columns.length == 6) {
        			
        			Rf2Record rf2r = new Rf2Record();
        			
        			rf2r.setId(columns[0]);
        			rf2r.setEffectiveTime(fmt.parseDateTime(columns[1]));
        			rf2r.setActive(columns[2]);
        			rf2r.setModuleId(columns[3]);
        			rf2r.setRefsetId(columns[4]);
        			rf2r.setReferencedComponentId(columns[5]);
        			rf2RLst.add(rf2r);
        			
        		} else {
        			
        			throw new RefsetServiceException("Insufficient data, no further processing is possible");
        			
        		}

            }
            
            return rf2RLst;
			
		} catch (IOException e) {
			
			throw new RefsetServiceException(e);
			
		} finally {
			
			try {
				
				reader.close();
				
			} catch (IOException e) {
				
				LOGGER.info("Could not close IO resources");
			}
		}
	}
	
}
