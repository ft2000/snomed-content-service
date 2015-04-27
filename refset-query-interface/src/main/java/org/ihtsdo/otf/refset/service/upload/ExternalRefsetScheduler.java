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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.ihtsdo.otf.refset.common.SearchCriteria;
import org.ihtsdo.otf.refset.common.SearchField;
import org.ihtsdo.otf.refset.domain.RefsetDTO;
import org.ihtsdo.otf.refset.exception.EntityAlreadyExistException;
import org.ihtsdo.otf.refset.exception.RefsetServiceException;
import org.ihtsdo.otf.refset.service.authoring.RefsetAuthoringService;
import org.ihtsdo.otf.refset.service.browse.RefsetBrowseService;
import org.ihtsdo.otf.refset.service.termserver.TermServer;
import org.joda.time.DateTime;
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
public class ExternalRefsetScheduler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ExternalRefsetScheduler.class);
	private static final DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyyMMdd");

	@Value(value = "${external.refset.dir}")
	private String externalRefsetDir;
	
	@Resource(name = "browseGraphService")
	private RefsetBrowseService bService;
	
	@Autowired
	private ImportRF2Service service;
	
	@Resource
	private RefsetAuthoringService aService;
	
	@Autowired
	private TermServer tService;

	
	@Scheduled(cron = "0 */30 * * * *")
	public void createExternalRefsetHeader() throws InterruptedException {
		
		//load file
		LOGGER.debug("Loading external refsets from spreadsheet file");

		BufferedReader reader = null;
		Set<String> files = new HashSet<String>();
		try {
			

			files = getFileNames();
			
			for (String file : files) {
				
        		LOGGER.debug("Processing file {}", file);

        		reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			    XSSFWorkbook wb = new XSSFWorkbook(file);
			    XSSFSheet sheet = wb.getSheetAt(0);

			    int rows = sheet.getPhysicalNumberOfRows();
	        	int rowNo = 0;
	        	
	        	for (int i = 0; i < rows; i++) {
					boolean isUpdate = false;
					RefsetDTO dto = new RefsetDTO();

	        		if (i == 0 ) {
						
	            		LOGGER.debug("Line {} is header or empty skipping", rowNo);
	        			continue;
					}
	        		XSSFRow row = sheet.getRow(i);
	        		for (int j = 0; j < row.getLastCellNum(); j++) {
						
	        			XSSFCell cell = row.getCell(j);
	        			cell.setCellType(Cell.CELL_TYPE_STRING);
	        			String cellValue = cell != null && !StringUtils.isEmpty(cell.getStringCellValue()) ? cell.getStringCellValue() : null;
	        			if ( j == 0 && StringUtils.isEmpty(cellValue)) {
		            		LOGGER.debug("Line {} description not available. Not processing", rowNo);

	        				break;
	        				
						} else if (!StringUtils.isEmpty(cellValue)){
							
							switch (j) {
							case 0:
								SearchCriteria sc = new SearchCriteria();
								sc.addSearchField(SearchField.description, cellValue);
								List<RefsetDTO> dtos = bService.getRefsets(sc);
								if (!dtos.isEmpty()) {
									
									for (RefsetDTO refsetDTO : dtos) {
										
										dto = refsetDTO;
										isUpdate = true;
										break;
									}
								}
								dto.setDescription(cellValue);
								break;
							case 1:
								dto.setScope(cellValue);
								break;
							case 2:
								dto.setSctId(cellValue);
								break;
							case 3:
								dto.setModuleId(cellValue);//
								break;
							case 4:
								dto.setComponentTypeId("900000000000461009");//Tool supporting only concept type component
								break;
							case 5:
								dto.setTypeId("446609009");//Tool supporting only simple type refsets
								break;
							case 6:
								dto.setClinicalDomain(cellValue);//Tool supporting only simple type refsets
								break;
							case 7:
								dto.setOriginCountryCode(cellValue);
								break;
							case 8:
								dto.setSnomedCTExtension(cellValue);
								break;
							case 9:
								break;
							case 10:
								String lang = StringUtils.isEmpty(cellValue) ? "en" : cellValue;
								
								dto.setLanguageCode(lang);
								break;
							case 11:
								dto.setImplementationDetails(cellValue);
								break;
							case 12:
								break;
							case 13:
								dto.setExternalUrl(cellValue);
								break;
							case 14:
								dto.setExternalContact(cellValue);
								break;
							case 15:
								if (isUpdate)
									dto.setContributingOrganization(cellValue);
								break;
							default:
								break;
							}
								dto.setActive(true);
								dto.setPublished(true);
						}
					}
	        		
					if (isUpdate) {
						dto.setModifiedBy("rda");
						dto.setModifiedDate(new DateTime());
						aService.updateRefset(dto);

					} else {
						
						dto.setUuid(UUID.randomUUID().toString());
						dto.setCreated(new DateTime());
						dto.setCreatedBy("rda");
						try {
							aService.addRefset(dto);
						} catch (EntityAlreadyExistException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}


					}
					Thread.sleep(3000);


	        	}
			}
		} catch (RefsetServiceException  e) {
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
	
	private Set<String> getFileNames() {
		
		Set<String> fileNames = new HashSet<String>();
		
		String location = System.getProperty("catalina.home") + File.separator + externalRefsetDir;

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
