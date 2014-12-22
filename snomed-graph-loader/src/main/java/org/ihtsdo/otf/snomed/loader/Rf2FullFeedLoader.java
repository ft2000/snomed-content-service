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
package org.ihtsdo.otf.snomed.loader;

import static org.ihtsdo.otf.snomed.loader.DescriptionProcessor.getDescription;
import static org.ihtsdo.otf.snomed.loader.DescriptionProcessor.processDescription;
import static org.ihtsdo.otf.snomed.loader.ConceptProcessor.processConcept;
import static org.ihtsdo.otf.snomed.loader.ConceptProcessor.getConcept;
import static org.ihtsdo.otf.snomed.loader.RelationshipProcessor.getRelationship;

import static org.ihtsdo.otf.snomed.loader.RelationshipProcessor.processRelationship;


import static org.ihtsdo.otf.snomed.loader.RF2ImportHelper.beginTx;
import static org.ihtsdo.otf.snomed.loader.RF2ImportHelper.commit;
import static org.ihtsdo.otf.snomed.loader.RF2ImportHelper.verifyTSV;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thinkaurelius.titan.core.TitanGraph;

/**
 *At the of start loading insert a record for load audit with timestamp and status - in_progress
	
	//load a concept, check if this concept & effective time combination already exist. if not just insert it.
	// if exist then check effective time of this concept is greater then existing concept. if 
		//YES - update concept with greater effective time
		//NO - then create a history concept with a relationship which has START - as existing effective time
			// and END date is current effective time
 *once loading completes mark load as finished 
 */
public class Rf2FullFeedLoader {

	
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Rf2FullFeedLoader.class);
		
	private TitanGraph g;
	
	boolean isReload = false;
	private int bufferSize = 1000;

	public Rf2FullFeedLoader(TitanGraph g) {

		LOGGER.info("Initializing graph {}" , g);
		
		if (g == null) {
			
			throw new IllegalArgumentException("Graph instance is required for loading. Can not continue");
		}
		
		this.g = g;

	}
	
	public void load(String file) {
		
		LOGGER.debug("Starting to audit file {}", file);
        long start = System.currentTimeMillis();
        long totalRow = 0;
        
        //need to change implementation from super csv to java io as RF2 description has buggy quotes
        BufferedReader reader = null;

        try {
        	
        		
        		
        		verifyTSV(file);
        		
        		if(file.endsWith(".gz")) {
					
			        reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(file)), "utf-8"));
			        
				} else {
					
					reader = new BufferedReader(new FileReader(file));
				}
        		
        		LOGGER.debug("Starting to load file {}", file);
                
                String line;

                beginTx(g);
                int row = -1;

                while( (line = reader.readLine()) != null ) {

                	row++;

                	LOGGER.debug("Processing rowNo={} ", row);
                	if (StringUtils.isEmpty(line)) {
                    	
                		LOGGER.debug("rowNo={} , {} is empty skipping", row, line);

                		continue;
					}
                	
                	String[] columns = line.split("\t");
                	
                	LOGGER.debug("Processing rowNo {}, noOfColumns {}", row, columns != null ? columns.length : 0);
                	
                	if (columns != null && columns.length == 9 && row != 0) {
                		
                  		LOGGER.debug("Processing line={}", line);
                		Rf2Description desc = getDescription(columns);
    					processDescription(desc, g);

					} else if (columns != null && columns.length == 10 && row != 0) {
                		
                  		LOGGER.debug("Processing line={}", line);
                		Rf2Relationship rel = getRelationship(columns);
    					processRelationship(rel, g);

					} else if (columns != null && columns.length == 5 && row != 0) {
                		
                  		LOGGER.debug("Processing line={}", line);
                		Rf2Concept c = getConcept(columns);
    					processConcept(c, g);

					} else {
						
                		LOGGER.debug("rowNo={}, {}  does not have required columns, skipping. "
                				+ "No of columns in current row {}", row, line, columns != null ? columns.length : 0);
                		continue;

					}
                	
                	commit(row, bufferSize, g);

                }
        		LOGGER.info("Commiting remaining data");
                g.commit();//remaining operation commit
                totalRow = row;
        		LOGGER.debug("Total records processed {} in {} sec", totalRow, (System.currentTimeMillis() - start)/1000);
      
        } catch (IOException e) {
        	
        	g.rollback();
        	e.printStackTrace();
			throw new RuntimeException("Can not process file", e);
			
		} catch (Exception e) {
			
    		LOGGER.error("Transaction rolledback");
			g.rollback();
			e.printStackTrace();
			throw new RuntimeException(e);
			
		}
        finally {
        	
            if( reader != null ) {
            	
                try {
                	
					LOGGER.info("Closing IO resources");
					reader.close();
					
				} catch (IOException e) {
		    		
					LOGGER.error("Error closing bean reader");
					e.printStackTrace();
					
				}
            }
        }


	}

	/**
	 * @param bufferSize the bufferSize to set
	 */
	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}
}
