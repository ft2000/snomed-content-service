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

import static org.ihtsdo.otf.snomed.loader.RF2ImportHelper.descMap;
import static org.ihtsdo.otf.snomed.loader.RF2ImportHelper.vMap;
import static org.ihtsdo.otf.snomed.loader.RF2ImportHelper.getVertex;
import static org.ihtsdo.otf.snomed.loader.RF2ImportHelper.processCaseSinificance;
import static org.ihtsdo.otf.snomed.loader.RF2ImportHelper.processModule;
import static org.ihtsdo.otf.snomed.loader.RF2ImportHelper.processTypeId;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;

import org.apache.commons.lang3.StringUtils;
import org.ihtsdo.otf.snomed.domain.DescriptionType;
import org.ihtsdo.otf.snomed.domain.Properties;
import org.ihtsdo.otf.snomed.domain.Relationship;
import org.ihtsdo.otf.snomed.domain.Types;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thinkaurelius.titan.core.TitanException;
import com.thinkaurelius.titan.core.TitanGraph;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;

/**
 *
 */
public class Rf2SnapshotAuditor {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Rf2SnapshotAuditor.class);
	
	private static final String SNAPSHOT_USER = "system";
	
	private TitanGraph g;
	private int bufferSize = 1000;
	
	private String subType;
	boolean isReload = false;

	
	

	/**
	 * @param subType the subType to set
	 */
	public void setSubType(String subType) {
		this.subType = subType;
	}


	/**all files together
	 * 
	 */
	public Rf2SnapshotAuditor(TitanGraph g) {
		// TODO Auto-generated constructor stub
		LOGGER.info("Initializing graph {}" , g);
		if (g == null) {
			
			throw new IllegalArgumentException("Graph instance is required for snapshot loading. Can not continue");
		}
		this.g = g;
		


	}
	
	
	public void audit(String file) {
		

		
		LOGGER.debug("Starting to audit file {}", file);
        long start = System.currentTimeMillis();
        long totalRow = 0;

        //need to change implementation from super csv to java io as RF2 description has buggy quotes
        BufferedReader reader = null;

        try {
        	
        		if (StringUtils.isBlank(file)) {
					
        			throw new IllegalArgumentException("Please check file supplied.");
        			
				} else if(file.endsWith(".gz")) {
					
			        reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(file)), "utf-8"));
			        
				} else {
					
					reader = new BufferedReader(new FileReader(file));
				}
        		
        		LOGGER.debug("Starting to load file {}", file);
                
                String line;

                beginTx();
                int row = -1;
        		DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyyMMdd");

                while( (line = reader.readLine()) != null ) {

                	row++;

                	LOGGER.debug("Processing rowNo={} ", row);
                	if (StringUtils.isEmpty(line)) {
                    	
                		LOGGER.debug("rowNo={} , {} is empty skipping", row, line);

                		continue;
					}
                	
                	String[] columns = StringUtils.splitByWholeSeparator(line, "\t");
                	
                	if (columns != null && columns.length == 9 && row != 0) {
                		
                		
                		if (isReload) {
                			
                			Vertex v = getVertex(g, columns[0]);
                			
                			if (v != null) {
                				
                				LOGGER.debug("Not Processing line={}, rowNo={} as record already loaded", line, row);
                				continue;
                			}
                		}

                		LOGGER.debug("Processing rowNo={} , {}", row, line);

    					Rf2Description desc = new Rf2Description();
    					//id	effectiveTime	active	moduleId	conceptId	languageCode	typeId	term	caseSignificanceId

    					desc.setId(columns[0]);
    					desc.setEffectiveTime(fmt.parseDateTime(columns[1]));
    					desc.setActive(columns[2]);
    					desc.setModuleId(columns[3]);
    					desc.setConceptId(columns[4]);
    					desc.setLanguageCode(columns[5]);
    					desc.setTypeId(columns[6]);
    					desc.setTerm(columns[7]);
    					desc.setCaseSignificanceId(columns[8]);

    					if (!isConceptTitleExist(desc)) {
    						
    						auditDescription(desc);

    					} else if (!StringUtils.isEmpty(subType) 
    							&& subType.equalsIgnoreCase(descMap.get(desc.getTypeId()).toString()) 
    							&& !isSubTypeRelationExist(desc)) {

    						LOGGER.debug("Processing row {} of subType {}", row, subType);

    						processDescription(desc);

    					} else {
    						
    		        		LOGGER.debug("Not processing row {} of description id {}", row, desc.getId());

    					}
					} else {
						
                		LOGGER.debug("rowNo={}, {}  does not have required columns, skipping", row, line);
                		continue;

					}
                	
                	commit(row);

                }
        		LOGGER.info("Commiting remaining data");
                g.commit();//remaining operation commit
                totalRow = row;
        } catch (IOException e) {
        	g.rollback();
			// TODO Auto-generated catch block
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
        
        LOGGER.info("Total row {} processed in {} minute ", totalRow, ((System.currentTimeMillis() - start)/60000));

	
	}

	private void commit(int rowNumber) {
		
		
        if (rowNumber % bufferSize == 0) {
        	
    		LOGGER.info("Committing running transaction");
            try {
                g.commit();
                beginTx();                
                LOGGER.info("Total concept processed {}", rowNumber);

            } catch (TitanException e) {
            	
                LOGGER.error("Error commiting transaction, retrying {}, {}", rowNumber, e);

            	e.printStackTrace();
            	try {
					
                	g.commit();//retry
                    beginTx();                
                    LOGGER.info("Total concept processed {}", rowNumber);

				} catch (TitanException e2) {
					e2.printStackTrace();
                    LOGGER.error("Error commiting transaction during retry {}, {}", e, rowNumber);

					throw e2;
				}

            }
        }
    }
	
	private void beginTx() {
        
		LOGGER.info("Starting a new transaction");
		try {
			Thread.sleep(2000);
			LOGGER.info("Sleep done");

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        g.buildTransaction().enableBatchLoading();
        g.newTransaction();
        vMap = new HashMap<String, Vertex>();//refresh map as vertex are stale after commit
	}
	
	private void processDescription(Rf2Description desc) {
		long start = System.currentTimeMillis();
		LOGGER.debug("Processing description {}", desc.getId());
		
		Vertex vD = g.addVertexWithLabel(Types.description.toString());
		vD.setProperty(Properties.sctid.toString(), desc.getId());
		vD.setProperty(Properties.effectiveTime.toString(), desc.getEffectiveTime().getMillis());
		vD.setProperty(Properties.status.toString(), desc.getActive());
		vD.setProperty(Properties.created.toString(), new DateTime().getMillis());
		vD.setProperty(Properties.createdBy.toString(), SNAPSHOT_USER);
		vD.setProperty(Properties.languageCode.toString(), desc.getLanguageCode());
		vD.setProperty(Properties.title.toString(), desc.getTerm());

		//add module
		Vertex vM = processModule(g, desc.getModuleId());
		vD.addEdge(Relationship.hasModule.toString(), vM);
		
		//case significance
		Vertex vCs = processCaseSinificance(g, desc.getCaseSignificanceId());
		vD.addEdge(Relationship.hasCaseSignificance.toString(), vCs);

		//type
		Vertex vT = processTypeId(g, desc.getTypeId());
		vD.addEdge(Relationship.hasType.toString(), vT);

		//concept
		Vertex vC = getVertex(g, desc.getConceptId());
		
		LOGGER.trace("Concept vertex {}", vC);
		
		if (vC != null) {
			
			if (DescriptionType.fsn.equals(descMap.get(desc.getTypeId()))) {
				
				LOGGER.debug("Adding FSN as concept title  {}", desc.getTerm());

				vC.setProperty(Properties.title.toString(), desc.getTerm());

			}
			String name = descMap.get(desc.getTypeId()).toString();
			
			LOGGER.trace("Concept vertex {}, typeId {} and resulted edge label name {}", vC, desc.getTypeId(), name);

			Edge e = vC.addEdge(name, vD);
			e.setProperty(Properties.title.toString(), desc.getTerm());

		} else {
			
			LOGGER.error("Could not find concept for id {}", desc.getConceptId());
		}
		LOGGER.trace("processDescription total time {} sec ", (System.currentTimeMillis() - start)/1000);

	}
	
	
	
	




	
	
	
	private void auditDescription(Rf2Description desc) {
		

		long start = System.currentTimeMillis();
		LOGGER.debug("auditDescription description {}", desc.getId());
		Vertex existingVD = getVertex(g, desc.getId());
		
		if (existingVD == null) {
			
			LOGGER.debug("description {} does not exist adding", desc.getId());

			//add this description
			processDescription(desc);
			
		} else {
			
			LOGGER.debug("description {} exist auditing further", desc.getId());

			boolean hasModule = existingVD.getEdges(Direction.OUT, Relationship.hasModule.toString()).iterator().hasNext();
			boolean hasCaseSignificance = existingVD.getEdges(Direction.OUT, Relationship.hasCaseSignificance.toString()).iterator().hasNext();
			boolean hasType = existingVD.getEdges(Direction.OUT, Relationship.hasType.toString()).iterator().hasNext();
			boolean fsnOrSynnonym = existingVD.getEdges(Direction.IN, descMap.get(desc.getTypeId()).toString()).iterator().hasNext();
			
			if (!(hasModule && hasCaseSignificance && hasType && fsnOrSynnonym)) {
				
				LOGGER.debug("Description {} does not have full data. "
						+ "Reprocessing", desc.getId());

				//remove this description from db and add again
				g.removeVertex(existingVD);

				//add again
				processDescription(desc);
				
			} else {
				
				LOGGER.debug("auditDescription description {} has required data. "
						+ "Auditing concept for title ", desc.getId());

				//verify if concept vertex has title
				Vertex vC = getVertex(g, desc.getConceptId());
				if (vC != null) {
					
					String title = vC.getProperty(Properties.title.toString());
					
					if (StringUtils.isBlank(title) && DescriptionType.fsn.equals(descMap.get(desc.getTypeId()))) {
						
						LOGGER.debug("concept {} title does not exist adding it ", desc.getConceptId());
						
						vC.addEdge(descMap.get(desc.getTypeId()).toString(), existingVD);
						vC.setProperty(Properties.title.toString(), desc.getTerm());
						
					} else {
						
						LOGGER.debug("concept {} title exist  or it is a {} ", desc.getConceptId(), descMap.get(desc.getTypeId()).toString());

					}
				}
			}

		}
		LOGGER.trace("auditDescription total time {} sec ", (System.currentTimeMillis() - start)/1000);

	
	}
	
	
	private boolean isConceptTitleExist(Rf2Description desc) {
		LOGGER.trace("isConceptTitleExist {}", desc.getConceptId());

		boolean isExist = false;

		long start = System.currentTimeMillis();

		if (descMap.get(desc.getTypeId()).equals(DescriptionType.fsn)) {
			
			//verify if concept vertex has title
			Vertex vC = getVertex(g, desc.getConceptId());
			if (vC != null) {
				
				String title = vC.getProperty(Properties.title.toString());
				
				if (StringUtils.isBlank(title)) {
					
					isExist = false;
					
				} else {
					
					LOGGER.debug("Concept {} title {} exist", desc.getConceptId(), title);
					isExist = true;

				}
				
				LOGGER.debug("Title for concept {} exist? - {}", desc.getConceptId(), isExist);

			}

		} else {
		

			isExist = true;
			LOGGER.debug("Not a fsn skip hence retrun  {} ", isExist);

		}

		LOGGER.trace("isConceptTitleExist total time {} sec ", (System.currentTimeMillis() - start)/1000);

		return isExist;
	
	}
	
	
	private boolean isSubTypeRelationExist(Rf2Description desc) {

		boolean isExist = false;
		
		if (!descMap.get(desc.getTypeId()).equals(DescriptionType.fsn)) {
			
			//verify if concept vertex has title
			Vertex vC = getVertex(g, desc.getConceptId());
			if (vC != null) {
				Iterable<Edge> es = vC.getEdges(Direction.OUT, descMap.get(desc.getTypeId()).toString());
				
				for (Edge e : es) {
					
					String title = e.getProperty(Properties.title.toString());
					if(!StringUtils.isBlank(title) && title.equalsIgnoreCase(desc.getTerm())) {
						
						isExist = true;
						break;
					}
				}
				
			}

		} else {
		
			
			isExist = true;
			LOGGER.debug("is a fsn skip, return  {} ", isExist);//fsn is being processed as part of concept title. So skip

		}
		LOGGER.debug("description id {} exist ? = {} ", desc.getId(), isExist);

		return isExist;
	
	}



	/**
	 * @param bufferSize the bufferSize to set
	 */
	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}


	/**
	 * @param isReload the isReload to set
	 */
	public void setReload(boolean isReload) {
		
		this.isReload = isReload;
		
	}



}
