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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
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
	
	private static final String SNAPSHOT_USER = "snomed_st_loader";
	
	private TitanGraph g;
	private int bufferSize = 100000;
	
	//map to 
	private Map<String, Vertex> vMap = new HashMap<String, Vertex>();
	private Map<String, DescriptionType> descMap = new HashMap<String, DescriptionType>();
	private Map<String, Relationship> characteristicsMap = new HashMap<String, Relationship>();
	
	private String subType;

	
	

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
		
		descMap.put("900000000000013009", DescriptionType.synonym);
		descMap.put("900000000000550004", DescriptionType.definition);
		descMap.put("900000000000003001", DescriptionType.fsn);

		characteristicsMap.put("900000000000011006", Relationship.inferred);
		characteristicsMap.put("900000000000010007", Relationship.stated);
		characteristicsMap.put("900000000000227009", Relationship.additional);
		characteristicsMap.put("900000000000225001", Relationship.qualifying);


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
		Vertex vM = processModule(desc.getModuleId());
		vD.addEdge(Relationship.hasModule.toString(), vM);
		
		//case significance
		Vertex vCs = processCaseSinificance(desc.getCaseSignificanceId());
		vD.addEdge(Relationship.hasCaseSignificance.toString(), vCs);

		//type
		Vertex vT = processType(desc.getTypeId());
		vD.addEdge(Relationship.hasType.toString(), vT);

		//concept
		Vertex vC = getVertex(desc.getConceptId(), Types.concept.toString());
		
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
	
	
	private void processRelationship(Rf2Relationship rel) {
		long start = System.currentTimeMillis();
		LOGGER.debug("Processing relationship {}", rel.getId());
		
		//this is special vertex only required to get to have special relationships
		
		Vertex vR = g.addVertexWithLabel(Types.relationship.toString());
		vR.setProperty(Properties.sctid.toString(), rel.getId());
		
		//add module
		Vertex vM = processModule(rel.getModuleId());
		g.addEdge(rel.getModuleId(), vR, vM, Relationship.hasModule.toString());

		//type
		Vertex vT = processType(rel.getTypeId());
		g.addEdge(rel.getModuleId(), vR, vT, Relationship.hasType.toString());
		
		//modifier
		Vertex vMo = processModifier(rel.getTypeId());
		g.addEdge(rel.getModifierId(), vR, vMo, Relationship.hasModifier.toString());

		//concept
		Vertex vSource = getVertex(rel.getSourceId(), Types.concept.toString());
		Vertex vDest = getVertex(rel.getDestinationId(), Types.concept.toString());
		LOGGER.trace("Source concept {} - vertex {}", rel.getSourceId(), vSource);
		LOGGER.trace("Destination concept {} - vertex {}", rel.getDestinationId(), vDest);
		
		if (vSource != null && vDest != null) {
			
			Relationship relName = characteristicsMap.get(rel.getCharacteristicTypeId());
			String label = relName != null ? relName.toString() : null;
			
			LOGGER.trace("Relationship edge label {} ", label);

			Edge eR = g.addEdge(rel.getId(), vSource, vDest, label);
			eR.setProperty(Properties.sctid.toString(), rel.getId());
			eR.setProperty(Properties.effectiveTime.toString(), rel.getEffectiveTime().getMillis());
			eR.setProperty(Properties.status.toString(), rel.getActive());
			eR.setProperty(Properties.created.toString(), new DateTime().getMillis());
			eR.setProperty(Properties.createdBy.toString(), SNAPSHOT_USER);
			eR.setProperty(Properties.group.toString(), rel.getRelationshipGroup());
			//these are special relationship edge properties in a sense that they should represent as v --> v "relationship" if relationship is a vertex.
			eR.setProperty(Properties.typeId.toString(), rel.getTypeId());
			eR.setProperty(Properties.moduleId.toString(), rel.getModuleId());
			eR.setProperty(Properties.modifierId.toString(), rel.getModifierId());
		}
		
		LOGGER.trace("processRelationship total time {} sec ", (System.currentTimeMillis() - start)/1000);


	}

	
	
	
	/**
	 * @param modifierId
	 * @return
	 */
	private Vertex processModifier(String modifierId) {

		long start = System.currentTimeMillis();
		Vertex v = null;
		
		v = vMap.get(modifierId);
		LOGGER.trace("Vertex from local cache {}", v);

		if (v == null) {
			
			v = getVertex(modifierId, Types.modifier.toString());
			LOGGER.trace("Vertex from db {}", v);

			vMap.put(modifierId, v);

		}
		
		if (v == null) {
		
			v = g.addVertexWithLabel(g.getVertexLabel(Types.modifier.toString()));
			v.setProperty(Properties.sctid.toString(), modifierId);
			vMap.put(modifierId, v);
			LOGGER.trace("Adding module vertex {}", v);

		}
		
		LOGGER.trace("processModifier total time {} sec ", (System.currentTimeMillis() - start)/1000);

		return v;
	}


	/**
	 * @param typeId
	 * @return
	 */
	private Vertex processType(String typeId) {

		long start = System.currentTimeMillis();
		Vertex v = null;
		
		v = vMap.get(typeId);
		LOGGER.trace("Vertex from local cache {}", v);

		if (v == null) {
			
			v = getVertex(typeId, Types.type.toString());
			LOGGER.trace("Vertex from db {}", v);

			vMap.put(typeId, v);

		}
		
		if (v == null) {
		
			v = g.addVertexWithLabel(g.getVertexLabel(Types.type.toString()));
			v.setProperty(Properties.sctid.toString(), typeId);
			vMap.put(typeId, v);
			LOGGER.trace("Adding module vertex {}", v);

		}
		LOGGER.trace("processType total time {} sec ", (System.currentTimeMillis() - start)/1000);

		return v;
		
	}


	private Vertex getVertex(String sctid, String label) {
		long start = System.currentTimeMillis();
		
		Iterable<Vertex> vs = g.getVertices(Properties.sctid.toString(), sctid);

		for (Vertex vertex : vs) {
			
			LOGGER.trace("getVertex - returning vertex as {} in total time {} sec ", vertex, (System.currentTimeMillis() - start)/1000);
			return vertex;
		}

		return null;
	}
	
	private Vertex processCaseSinificance(String caseSensitiveId) {
		long start = System.currentTimeMillis();
		Vertex v = null;
		
		v = vMap.get(caseSensitiveId);
		LOGGER.trace("Vertex from local cache {}", v);

		if (v == null) {
			
			v = getVertex(caseSensitiveId, Types.caseSensitive.toString());
			LOGGER.trace("Vertex from db {}", v);

			vMap.put(caseSensitiveId, v);

		}
		
		if (v == null) {
		
			v = g.addVertexWithLabel(g.getVertexLabel(Types.caseSensitive.toString()));
			v.setProperty(Properties.sctid.toString(), caseSensitiveId);
			vMap.put(caseSensitiveId, v);
			LOGGER.trace("Adding module vertex {}", v);

		}
		
		LOGGER.trace("processCaseSinificance total time {}", (System.currentTimeMillis() - start)/6000);

		return v;
	}
	
	private Vertex processModule(String moduleId) {
		
		long start = System.currentTimeMillis();
		Vertex v = null;
		
		v = vMap.get(moduleId);
		LOGGER.trace("Vertex from local cache {}", v);
		
		if (v == null) {
			
			v = getVertex(moduleId, Types.module.toString());
			LOGGER.trace("Vertex from db {}", v);
			vMap.put(moduleId, v);

		}
		
		if (v == null) {
		
			v = g.addVertexWithLabel(Types.module.toString());
			v.setProperty(Properties.sctid.toString(), moduleId);
			vMap.put(moduleId, v);
			LOGGER.trace("Adding module vertex {}", v);
			
		}
		LOGGER.trace("processModule total time {}", (System.currentTimeMillis() - start)/6000);

		return v;
	}
	
	private Vertex processDefinitionStatus(String dsId) {
		
		long start = System.currentTimeMillis();
		Vertex v = null;
		
		v = vMap.get(dsId);
		LOGGER.trace("Vertex from local cache {}", v);

		if (v == null) {
			
			v = getVertex(dsId, Types.definition.toString());
			LOGGER.trace("Vertex from db {}", v);
			vMap.put(dsId, v);

		}
		if (v == null) {
		
			v = g.addVertexWithLabel(Types.definition.toString());
			v.setProperty(Properties.sctid.toString(), dsId);
			LOGGER.trace("Adding module vertex {}", v);
			vMap.put(dsId, v);

		}
		LOGGER.trace("processDefinitionStatus total time {}", (System.currentTimeMillis() - start)/6000);

		return v;
	}
	
	private void auditDescription(Rf2Description desc) {
		

		long start = System.currentTimeMillis();
		LOGGER.debug("auditDescription description {}", desc.getId());
		Vertex existingVD = getVertex(desc.getId(), Types.description.toString());
		
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
				Vertex vC = getVertex(desc.getConceptId(), Types.concept.toString());
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
			Vertex vC = getVertex(desc.getConceptId(), Types.concept.toString());
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
			Vertex vC = getVertex(desc.getConceptId(), Types.concept.toString());
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


}
