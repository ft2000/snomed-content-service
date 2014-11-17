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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.ihtsdo.otf.refset.exception.EntityNotFoundException;
import org.ihtsdo.otf.refset.exception.RefsetServiceException;
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
	public Map<String, String> importFile(InputStream is, String refsetId) throws RefsetServiceException, EntityNotFoundException {
		
		
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
            
        	return srp.process(rf2RLst, refsetId);

			
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
