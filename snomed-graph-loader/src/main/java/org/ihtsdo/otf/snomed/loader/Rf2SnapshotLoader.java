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

import static org.ihtsdo.otf.snomed.loader.RF2ImportHelper.characteristicsMap;
import static org.ihtsdo.otf.snomed.loader.RF2ImportHelper.descMap;
import static org.ihtsdo.otf.snomed.loader.RF2ImportHelper.relTypeMap;
import static org.ihtsdo.otf.snomed.loader.RF2ImportHelper.vMap;
import static org.ihtsdo.otf.snomed.loader.RF2ImportHelper.getVertex;
import static org.ihtsdo.otf.snomed.loader.RF2ImportHelper.processCaseSinificance;
import static org.ihtsdo.otf.snomed.loader.RF2ImportHelper.processDefinitionStatus;
import static org.ihtsdo.otf.snomed.loader.RF2ImportHelper.processModifier;
import static org.ihtsdo.otf.snomed.loader.RF2ImportHelper.processModule;
import static org.ihtsdo.otf.snomed.loader.RF2ImportHelper.processType;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import com.thinkaurelius.titan.core.TitanException;
import com.thinkaurelius.titan.core.TitanGraph;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;

/**
 *
 */
public class Rf2SnapshotLoader {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Rf2SnapshotLoader.class);
	
	private static final String SNAPSHOT_USER = "system";
	
	private TitanGraph g;
	private int bufferSize = 1000;
	
	

	boolean isReload = false;
	

	/**all files together
	 * 
	 */
	public Rf2SnapshotLoader(TitanGraph g) {
		// TODO Auto-generated constructor stub
		LOGGER.info("Initializing graph {}" , g);
		if (g == null) {
			
			throw new IllegalArgumentException("Graph instance is required for snapshot loading. Can not continue");
		}
		this.g = g;
		
		
	}
	
	
	@SuppressWarnings("unchecked")
	public void load(String file) {
		
		LOGGER.debug("Starting to load file {}", file);
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

        	    final CsvPreference RF2_PREF = new CsvPreference.Builder('"', '\t', "\r\n").build();

                beanReader = new CsvBeanReader(reader, RF2_PREF);
                
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
                  
            		if (isReload) {
            			
            			Vertex v = getVertex(g, bean.getId());
            			
            			if (v != null) {
            				
            				LOGGER.debug("Not Processing lineNo={}, rowNo={} as record already loaded", beanReader.getLineNumber(),
                                    beanReader.getRowNumber());
            				continue;
            			}
            		}
            		
            		LOGGER.debug("Processing lineNo={}, rowNo={} ", beanReader.getLineNumber(),
                            beanReader.getRowNumber());
            		switch (noOfColumns) {
					case 9:
						//process description
						processDescription((Rf2Description) bean);
						break;
					case 10:
						//process relationship
						processRelationship((Rf2Relationship) bean);
						break;
					case 5:
						//process concept
						processConcept((Rf2Concept)bean);
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
			throw new RuntimeException("Can not process file", e);
 
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
                LOGGER.info("Total concept processed {}", rowNumber);
               
            } catch (TitanException e) {
            	
            	try {
					
                	g.commit();//retry
                    beginTx();                
                    LOGGER.info("Total concept processed {}", rowNumber);

				} catch (TitanException e2) {
					
                    LOGGER.error("Error commiting transaction {}", rowNumber);

					throw e2;
				}

            }
        }
    }
	
	private void beginTx() {
        
		LOGGER.info("Starting a new transaction");
		
		try {
			
			Thread.sleep(2000);
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        g.buildTransaction().enableBatchLoading();
        g.newTransaction();
        vMap = new HashMap<String, Vertex>();//refresh map as vertex are stale after commit
	}

	/**
	 * @param bean
	 */
	private void processConcept(Rf2Concept bean) {
		long start = System.currentTimeMillis();
		
		LOGGER.debug("Processing concept {}", bean.getId());
		
		Vertex vC = g.addVertexWithLabel(Types.concept.toString());
		vC.setProperty(g.getRelationType(Properties.sctid.toString()).toString(), bean.getId());
		vC.setProperty(Properties.effectiveTime.toString(), bean.getEffectiveTime().getMillis());
		vC.setProperty(Properties.status.toString(), bean.getActive());
		vC.setProperty(Properties.created.toString(), new DateTime().getMillis());
		vC.setProperty(Properties.createdBy.toString(), SNAPSHOT_USER);

		//add module
		
		Vertex vM = processModule(g, bean.getModuleId());
		vC.addEdge(Relationship.hasModule.toString(), vM);

		//definition status
		Vertex vDs = processDefinitionStatus(g, bean.getDefinitionStatusId());
		vC.addEdge(Relationship.ds.toString(), vDs);

		LOGGER.trace("processConcept total time {} sec ", (System.currentTimeMillis() - start)/1000);

		
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
		Vertex vT = processType(g, desc.getTypeId());
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
	
	
	private void processRelationship(Rf2Relationship rel) {
		long start = System.currentTimeMillis();
		LOGGER.debug("Processing relationship {}", rel.getId());
		
		//this is special vertex only required to get to have special relationships
		
		Vertex vR = g.addVertexWithLabel(Types.relationship.toString());
		vR.setProperty(Properties.sctid.toString(), rel.getId());
		vR.setProperty(Properties.characteristicId.toString(), rel.getCharacteristicTypeId());
		//add module
		Vertex vM = processModule(g, rel.getModuleId());
		g.addEdge(rel.getModuleId(), vR, vM, Relationship.hasModule.toString());

		//type
		Vertex vT = processType(g, rel.getTypeId());
		g.addEdge(rel.getTypeId(), vR, vT, Relationship.hasType.toString());
		
		//modifier
		Vertex vMo = processModifier(g, rel.getModifierId());
		g.addEdge(rel.getModifierId(), vR, vMo, Relationship.hasModifier.toString());

		//concept
		Vertex vSource = getVertex(g, rel.getSourceId());
		Vertex vDest = getVertex(g, rel.getDestinationId());
		LOGGER.trace("Source concept {} - vertex {}", rel.getSourceId(), vSource);
		LOGGER.trace("Destination concept {} - vertex {}", rel.getDestinationId(), vDest);
		
		if (vSource != null && vDest != null) {
			
			Properties relName = characteristicsMap.get(rel.getCharacteristicTypeId());
			String nature = relName != null ? relName.toString() : null;

			Relationship relType = relTypeMap.get(rel.getTypeId());
			String type = relType != null ? relType.toString() : Relationship.generic.toString();

			LOGGER.trace("Relationship edge label {} ", type);

			Edge eR = g.addEdge(rel.getId(), vSource, vDest, type);
			eR.setProperty(Properties.sctid.toString(), rel.getId());
			eR.setProperty(Properties.effectiveTime.toString(), rel.getEffectiveTime().getMillis());
			eR.setProperty(Properties.status.toString(), rel.getActive());
			eR.setProperty(Properties.created.toString(), new DateTime().getMillis());
			eR.setProperty(Properties.createdBy.toString(), SNAPSHOT_USER);
			eR.setProperty(Properties.group.toString(), rel.getRelationshipGroup());
			eR.setProperty(Properties.characteristic.toString(), nature);
			vR.setProperty(Properties.characteristicId.toString(), rel.getCharacteristicTypeId());

			//these are special relationship edge properties in a sense that they should represent as v --> v "relationship" if relationship is a vertex.
			eR.setProperty(Properties.typeId.toString(), rel.getTypeId());
			eR.setProperty(Properties.moduleId.toString(), rel.getModuleId());
			eR.setProperty(Properties.modifierId.toString(), rel.getModifierId());
		}
		
		LOGGER.trace("processRelationship total time {} sec ", (System.currentTimeMillis() - start)/1000);


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
