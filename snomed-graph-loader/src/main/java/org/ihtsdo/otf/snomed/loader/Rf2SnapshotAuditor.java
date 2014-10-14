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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

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
		ICsvBeanReader beanReader = null;
		InputStreamReader reader = null;

        try {
        	
        		if (StringUtils.isBlank(file)) {
					
        			throw new IllegalArgumentException("Please check file supplied.");
        			
				} else if(file.endsWith(".gz")) {
					
			        reader = new InputStreamReader(new GZIPInputStream(new FileInputStream(file)));
			        
				} else {
					
					reader = new FileReader(file);
				}
        		
        		LOGGER.debug("Starting to load file {}", file);

                beanReader = new CsvBeanReader(reader, CsvPreference.TAB_PREFERENCE);
                
                final String[] header = beanReader.getHeader(true);
                
                int noOfColumns = 0;
                if (header != null) {
					
                	noOfColumns = header.length;
                	
				}
                CellProcessor[] processors = null;
                @SuppressWarnings("rawtypes")
				Class rf2Base = null;

                LOGGER.debug("noOfColumns {} in the file", noOfColumns);

                switch (noOfColumns) {
                

				case 9:
					
                	LOGGER.debug("Processing description file");

					processors = RF2CellProcessor.getDescriptionCellProcessor();
					rf2Base = Rf2Description.class;
					
					break;
					
				case 10:
					
                	LOGGER.debug("Processing relationship file");

					processors = RF2CellProcessor.getRelationshipCellProcessor();
					rf2Base = Rf2Relationship.class;

					break;
					
				case 5:

					LOGGER.debug("Processing concept file");

					processors = RF2CellProcessor.getConceptCellProcessor();
					rf2Base = Rf2Concept.class;

					break;

				default:
					LOGGER.debug("Nothing to load");
					break;
				}
                Rf2Base bean;

                beginTx();
                while( (bean = (Rf2Base)beanReader.read(rf2Base, header, processors)) != null ) {
                  
                	int row = beanReader.getRowNumber();
                	LOGGER.debug("Processing lineNo={}, rowNo={} ", beanReader.getLineNumber(), row);
                

                	switch (noOfColumns) {
					case 9:
						//process description
						Rf2Description desc = (Rf2Description) bean;
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
						break;
					default:
						break;
					}
                	
                	commit(beanReader.getRowNumber());
                	
				
                	
                }
        		LOGGER.info("Commiting remaining data");
                g.commit();//remaining operation commit
            	totalRow = beanReader.getRowNumber();

        } catch (IOException e) {
			// TODO Auto-generated catch block
        	e.printStackTrace();
			throw new RuntimeException("Can not process file", e);
		} catch (Exception e) {
    		LOGGER.error("Transaction rolledback");
			g.rollback();
			e.printStackTrace();
		}
        finally {
        	
            if( beanReader != null ) {
            	
                try {
					LOGGER.info("Closing IO resources");
					beanReader.close();	
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
                LOGGER.info("Total concept procesed {}", rowNumber);
               
            } catch (TitanException e) {
            	
            	try {
					
                	g.commit();//retry
                    beginTx();                
                    LOGGER.info("Total concept procesed {}", rowNumber);

				} catch (TitanException e2) {
					
                    LOGGER.error("Error commiting transaction {}", rowNumber);

					throw e2;
				}

            }
        }
    }
	
	private void beginTx() {
        
		LOGGER.info("Starting a new transaction");

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
			
			LOGGER.debug("auditDescription description {} does not exist adding", desc.getId());

			//add this description
			processDescription(desc);
			
		} else {
			
			LOGGER.debug("auditDescription description {} exist auditing further", desc.getId());

			boolean hasModule = existingVD.getEdges(Direction.OUT, Relationship.hasModule.toString()).iterator().hasNext();
			boolean hasCaseSignificance = existingVD.getEdges(Direction.OUT, Relationship.hasCaseSignificance.toString()).iterator().hasNext();
			boolean hasType = existingVD.getEdges(Direction.OUT, Relationship.hasType.toString()).iterator().hasNext();
			boolean fsnOrSynnonym = existingVD.getEdges(Direction.IN, descMap.get(desc.getTypeId()).toString()).iterator().hasNext();
			
			if (!(hasModule && hasCaseSignificance && hasType && fsnOrSynnonym)) {
				
				LOGGER.debug("auditDescription description {} does not have full data. "
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
						
						LOGGER.debug("auditDescription concept {} title does not exist adding it ", desc.getConceptId());
						
						vC.addEdge(descMap.get(desc.getTypeId()).toString(), existingVD);
						vC.setProperty(Properties.title.toString(), desc.getTerm());
						
					} else {
						
						LOGGER.debug("auditDescription concept {} title exist  or it is a {} ", desc.getConceptId(), descMap.get(desc.getTypeId()).toString());

					}
				}
			}

		}
		LOGGER.trace("auditDescription total time {} sec ", (System.currentTimeMillis() - start)/1000);

	
	}
	
	
	private boolean isConceptTitleExist(Rf2Description desc) {

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
					
					LOGGER.debug("auditDescription concept {} title {} exist", desc.getConceptId(), title);
					isExist = true;

				}
				
				LOGGER.debug("Tital for concept {} exist? - {}", desc.getConceptId(), isExist);

			}

		} else {
		

			isExist = true;
			LOGGER.debug("Not a fsn skip hence retrun  {} ", isExist);

		}

		LOGGER.trace("auditDescription total time {} sec ", (System.currentTimeMillis() - start)/1000);

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
			LOGGER.debug("is a fsn skip hence retrun  {} ", isExist);//fsn is being processed as part of concept title. So skip

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
