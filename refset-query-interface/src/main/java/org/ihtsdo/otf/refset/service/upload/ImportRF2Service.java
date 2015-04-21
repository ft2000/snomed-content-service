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

import static org.ihtsdo.otf.refset.common.Utility.getToDate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.ihtsdo.otf.refset.domain.RefsetDTO;
import org.ihtsdo.otf.refset.exception.EntityAlreadyExistException;
import org.ihtsdo.otf.refset.exception.EntityNotFoundException;
import org.ihtsdo.otf.refset.exception.RefsetServiceException;
import org.ihtsdo.otf.refset.service.authoring.RefsetAuthoringService;
import org.ihtsdo.otf.refset.service.termserver.TermServer;
import org.ihtsdo.otf.snomed.domain.Concept;
import org.ihtsdo.otf.snomed.exception.ConceptServiceException;
import org.ihtsdo.otf.snomed.service.ConceptLookupService;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public class ImportRF2Service implements ImportService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ImportRF2Service.class);
	private static final DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyyMMdd");
	
	private RefsetProcessor srp;
	
	@Resource
	private RefsetAuthoringService aService;
	
	@Autowired
	private ConceptLookupService lService;
	
	@Autowired
	private TermServer tService;

	/**
	 * @param srp the srp to set
	 */
	@Autowired
	public void setSrp(RefsetProcessor srp) {
		this.srp = srp;
	}


	/* (non-Javadoc)
	 * @see org.ihtsdo.otf.refset.service.upload.ImportService#importFile(org.springframework.web.multipart.MultipartFile, java.lang.String)
	 */
	@Override
	public Map<String, String> importFile(InputStream is, String refsetId, String user) throws RefsetServiceException, EntityNotFoundException {
		
		
		if (is == null || StringUtils.isBlank(refsetId)) {
			
			throw new IllegalArgumentException("Mandatory data ie file or refsetId is missing in request");
		}
		
		BufferedReader reader = null;
		try {

			reader = new BufferedReader(new InputStreamReader(is));
			
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
        			
        			rf2r.setId(columns[0]);//Per Robert preserve uuid. No need to generate refset tool uuid instead of using RF2 file.
        			rf2r.setEffectiveTime(fmt.parseDateTime(columns[1]));
        			rf2r.setActive(columns[2]);
        			rf2r.setModuleId(columns[3]);
        			rf2r.setRefsetId(columns[4]);
        			rf2r.setReferencedComponentId(columns[5]);
        			rf2r.setCreatedBy(user);
        			rf2r.setModifiedBy(user);
        			rf2RLst.add(rf2r);
        			
        		} else {
        			
        			throw new RefsetServiceException("Insufficient data, no further processing is possible");
        			
        		}

            }
            
        	return srp.process(rf2RLst, refsetId, user);

			
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
	
	
	public String createRefsetHeader(String conceptId) throws RefsetServiceException, EntityAlreadyExistException, ConceptServiceException {
		
		RefsetDTO dto = new RefsetDTO();
		dto.setUuid(conceptId);
		dto.setActive(true);
		dto.setPublished(false);//initially it should be unpublished
		
		String release = tService.getLatestRelease();
		Concept concept = lService.getConcept(conceptId, release);
		dto.setDescription(concept.getLabel());
		dto.setCreated(new DateTime());
		dto.setCreatedBy("system");
		dto.setScope(String.format("Refset released in latest SNOMED® CT Version %s", release));
		dto.setLanguageCode("en");
		dto.setPublishedDate(getToDate(release));
		dto.setExpectedReleaseDate(getToDate(release));
		dto.setComponentTypeId("900000000000461009");//Tool supporting only concept type component
		dto.setModuleId(concept.getModuleId());//
		dto.setTypeId("446609009");//Tool supporting only simple type refsets
		dto.setSnomedCTVersion(release);
		LOGGER.debug("Creating refset header {}", dto);
		return aService.addRefset(dto);
		
	}
	
	public Map<String, String> processRf2Records(List<Rf2Record> rf2RLst, String refsetId, String user) throws RefsetServiceException {
		
		LOGGER.debug("Importing rf2 records of refset {}", refsetId);

    	return srp.process(rf2RLst, refsetId, user);

	}

}
