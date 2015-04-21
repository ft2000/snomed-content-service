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
package org.ihtsdo.otf.refset.service.upload;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.ihtsdo.otf.refset.domain.RefsetDTO;
import org.ihtsdo.otf.refset.domain.RefsetStatus;
import org.ihtsdo.otf.refset.exception.EntityAlreadyExistException;
import org.ihtsdo.otf.refset.exception.RefsetServiceException;
import org.ihtsdo.otf.refset.service.authoring.RefsetAuthoringService;
import org.ihtsdo.otf.refset.service.browse.RefsetBrowseService;
import org.ihtsdo.otf.refset.service.termserver.TermServer;
import org.ihtsdo.otf.snomed.exception.ConceptServiceException;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 *Release service polls new rf2 refset releases files and load them into database
 */
@Component
public class RF2ImporterScheduler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ImportRF2Service.class);
	private static final DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyyMMdd");
	private static final String USER = "system";
	private static final Set<String> processedRefsets = new HashSet<String>();
	private static final Set<String> inProgreesRefsets = new HashSet<String>();
	private static final Set<String> refsetConceptIds = new HashSet<String>();

	@Value(value = "${rf2.dir}")
	private String rf2Directory;
	
	@Resource(name = "browseGraphService")
	private RefsetBrowseService bService;
	
	@Autowired
	private ImportRF2Service service;
	
	@Resource
	private RefsetAuthoringService aService;
	
	@Autowired
	private TermServer tService;

	
	@Scheduled(cron = "0 */30 * * * *")
	public void loadRF2File() {
		
		//load file
		LOGGER.debug("Loading rf2 files");

		BufferedReader reader = null;
		Set<String> files = new HashSet<String>();
		try {
			

			files = getFileNames();
			
			for (String file : files) {
				
        		LOGGER.debug("Processing file {}", file);

				addRefsetConceptIds(file);
				
				for (String refsetConceptId : refsetConceptIds) {
					
					if (processedRefsets.contains(refsetConceptId) || inProgreesRefsets.contains(refsetConceptId)) {
						
	            		LOGGER.debug("Skipping refsets {} as it has already being processed or under process", refsetConceptId);

						continue;
					}
					
					inProgreesRefsets.add(refsetConceptId);
					reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
					
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
		        			

		        			if(!refsetConceptId.equals(columns[4])) {
								
			            		LOGGER.debug("Not processing {} for now", columns[4]);

								continue;
							}
		        			
		        			Rf2Record rf2r = new Rf2Record();
		        			rf2r.setId(columns[0]);//Per Robert preserve uuid. No need to generate refset tool uuid instead of using RF2 file.
		        			rf2r.setEffectiveTime(fmt.parseDateTime(columns[1]));
		        			rf2r.setActive(columns[2]);
		        			rf2r.setModuleId(columns[3]);
		        			rf2r.setRefsetId(columns[4]);
		        			rf2r.setReferencedComponentId(columns[5]);
		        			rf2r.setCreatedBy(USER);
		        			rf2r.setModifiedBy(USER);
		        			rf2RLst.add(rf2r);
		        			
		        		} else {
		        			
		        			throw new RefsetServiceException("Insufficient data, no further processing is possible");
		        			
		        		}

		            }
		            
		            
		    		String uuid;
					try {
						
						uuid = service.createRefsetHeader(refsetConceptId);
						
					} catch (EntityAlreadyExistException e) {
						
						RefsetDTO dto = bService.getRefsetHeaderByCoceptId(refsetConceptId, -1);
						
						dto.setPublished(false);
						dto.setStatus(RefsetStatus.inProgress.toString());
						String release = tService.getLatestRelease();
						dto.setSnomedCTVersion(release);
						aService.updateRefset(dto);
						
						uuid = dto.getUuid();

					}

		            
		            service.processRf2Records(rf2RLst, uuid , USER);
		            
		            //finally mark refset as released and published
					RefsetDTO dto = bService.getRefsetHeader(uuid, -1);
					
					dto.setPublished(true);
					dto.setStatus(RefsetStatus.released.toString());
					aService.updateRefset(dto);
					processedRefsets.add(refsetConceptId);
					
				}
			}
		
		} catch (RefsetServiceException | ConceptServiceException e) {
			// TODO: handle exception add error email code
			LOGGER.error("Can not continue ", e);
			
		} catch (IOException e) {
			
			LOGGER.error("Can not continue ", e);
			
		} finally {
			
			//cleanup all files
			if (reader != null) {
				
				try {
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					LOGGER.error("Error closing IO resources ", e);
				}
			}
		
			for (String file : files) {
				
				LOGGER.debug("removing file {} ", file);

				FileUtils.deleteQuietly(new File(file));

			}
		}
		
	}

	/**
	 * @return
	 */
	private  void addRefsetConceptIds(String file) {
		
		
		BufferedReader reader = null;
		try {
			
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			
			String line;

        	int row = 0;

        	while( (line = reader.readLine()) != null ) {
            	
            	row++;

            	if (StringUtils.isEmpty(line) || row == 1) {
                	
            		LOGGER.debug("Line {} is empty skipping", line);
            		continue;
            		
				}
            	
            	String[] columns = StringUtils.splitByWholeSeparator(line, "\t");
            	 
        		if (columns != null & columns.length == 6 && !refsetConceptIds.contains(columns[4])) {
        			
    				refsetConceptIds.add(columns[4]);
				        			
        		}

            }
		} catch (IOException e) {
			
			LOGGER.error("Error while getting refset concept ids from file ", e);
			
		} finally {
  
			if (reader != null) {
				
				try {
					reader.close();
				} catch (IOException e) {
					
					LOGGER.error("Error closing IO resources ", e);
				}
			}
		}
	}
	
	private Set<String> getFileNames() {
		
		Set<String> fileNames = new HashSet<String>();
		
		String location = System.getProperty("catalina.home") + File.separator + rf2Directory;

		LOGGER.debug("Listing files from directory {} to process ", location);

		File dir = new File(location);
		File[] files = dir.listFiles();
		if (files != null) {
			
			for (File file : files) {
				
				LOGGER.debug("Adding files {} to process ", file.getAbsoluteFile());

				fileNames.add(file.getAbsolutePath());

			}
		}

		
		return fileNames;
	}
}
