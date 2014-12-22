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
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.commons.lang3.StringUtils;
import org.ihtsdo.otf.snomed.domain.DescriptionType;
import org.ihtsdo.otf.snomed.domain.Properties;
import org.ihtsdo.otf.snomed.domain.Relationship;
import org.ihtsdo.otf.snomed.domain.Types;
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
public class RF2ImportHelper {

	private static final Logger LOGGER = LoggerFactory.getLogger(RF2ImportHelper.class);
	//map to 
	protected static Map<String, Vertex> vMap = new HashMap<String, Vertex>();
	protected static Map<String, DescriptionType> descMap = new HashMap<String, DescriptionType>();
	protected static Map<String, Properties> characteristicsMap = new HashMap<String, Properties>();
	protected static Map<String, Relationship> relTypeMap = new HashMap<String, Relationship>();

	static {
		descMap.put("900000000000013009", DescriptionType.synonym);
		descMap.put("900000000000550004", DescriptionType.definition);
		descMap.put("900000000000003001", DescriptionType.fsn);

		characteristicsMap.put("900000000000011006", Properties.inferred);
		characteristicsMap.put("900000000000010007", Properties.stated);
		characteristicsMap.put("900000000000227009", Properties.additional);
		characteristicsMap.put("900000000000225001", Properties.qualifying);

		relTypeMap.put("116680003", Relationship.isA);
		relTypeMap.put("261583007", Relationship.using);
		relTypeMap.put("260686004", Relationship.method);
		relTypeMap.put("405813007", Relationship.ps);
		relTypeMap.put("363698007", Relationship.fs);
		
		
	}
	
	/**Get a vertex associated with a sctid
	 * @param g
	 * @param sctid
	 * @return
	 */
	protected static Vertex getVertex(TitanGraph g, String sctid) {
		long start = System.currentTimeMillis();
		
		Iterable<Vertex> vs = g.getVertices(Properties.sctid.toString(), sctid);

		for (Vertex vertex : vs) {
			
			LOGGER.trace("getVertex - returning vertex as {} in total time {} sec ", vertex, (System.currentTimeMillis() - start)/1000);
			return vertex;
		}

		return null;
	}
	
	protected static Vertex processCaseSinificance(TitanGraph g, String caseSensitiveId) {
		long start = System.currentTimeMillis();
		Vertex v = null;
		
		v = vMap.get(caseSensitiveId);
		LOGGER.trace("Vertex from local cache {}", v);

		if (v == null) {
			
			v = getVertex(g, caseSensitiveId);
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
	
	protected static Vertex processModule(TitanGraph g, String moduleId) {
		
		long start = System.currentTimeMillis();
		Vertex v = null;
		
		v = vMap.get(moduleId);
		LOGGER.trace("Vertex from local cache {}", v);
		
		if (v == null) {
			
			v = getVertex(g, moduleId);
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
	
	/**
	 * @param dsId
	 * @return
	 */
	protected static Vertex processDefinitionStatus(TitanGraph g, String dsId) {
		
		long start = System.currentTimeMillis();
		Vertex v = null;
		
		v = vMap.get(dsId);
		LOGGER.trace("Vertex from local cache {}", v);

		if (v == null) {
			
			v = getVertex(g, dsId);
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
	
	/**
	 * @param typeId
	 * @return
	 */
	protected static Vertex processTypeId(TitanGraph g, String typeId) {

		long start = System.currentTimeMillis();
		Vertex v = null;
		
		v = vMap.get(typeId);
		LOGGER.trace("Vertex from local cache {}", v);

		if (v == null) {
			
			v = getVertex(g, typeId);
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
	
	/**
	 * @param modifierId
	 * @return
	 */
	protected static Vertex processModifier(TitanGraph g, String modifierId) {

		long start = System.currentTimeMillis();
		Vertex v = null;
		
		v = vMap.get(modifierId);
		LOGGER.trace("Vertex from local cache {}", v);

		if (v == null) {
			
			v = getVertex(g, modifierId);
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
	 * @param rowNumber
	 * @param bufferSize
	 * @param g
	 */
	protected static void commit(int rowNumber, int bufferSize, TitanGraph g) {
		
		
        if (rowNumber % bufferSize == 0) {
        	
    		LOGGER.info("Committing running transaction");
            try {
                g.commit();
                beginTx(g);                
                LOGGER.info("Total concept processed {}", rowNumber);

            } catch (TitanException e) {
            	
                LOGGER.error("Error commiting transaction, retrying {}, {}", rowNumber, e);

            	e.printStackTrace();
            	try {
					
                	g.commit();//retry
                    beginTx(g);                
                    LOGGER.info("Total concept processed {}", rowNumber);

				} catch (TitanException e2) {
					e2.printStackTrace();
                    LOGGER.error("Error commiting transaction during retry {}, {}", e, rowNumber);

					throw e2;
				}

            }
        }
    }
	
	/**
	 * @param g
	 */
	protected static void beginTx(TitanGraph g) {
        
		LOGGER.info("Starting a new transaction");
		try {
			Thread.sleep(1000);
			LOGGER.info("Sleep done");

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        g.buildTransaction().enableBatchLoading();
        g.newTransaction();
        vMap = new HashMap<String, Vertex>();//refresh map as vertex are stale after commit
	}
	
	/**Get a vertex associated with a sctid and given effective time
	 * @param g
	 * @param sctid
	 * @return
	 */
	protected static Vertex getVertexByIdEffectiveTime(TitanGraph g, Rf2Base rf2Base) {

		//check to if given sctid and effective time exist.
		Iterable<Vertex> vs = g.query()
					.has(Properties.sctid.toString(), rf2Base.getId())
					.has(Properties.effectiveTime.toString(), rf2Base.getEffectiveTime().getMillis()).vertices();

		for (Vertex vertex : vs) {
			
			LOGGER.trace("getVertex - returning vertex as {} ", vertex);
			return vertex;
		}

		return null;
	}
	
	/**Get a vertex associated with a sctid and give type
	 * @param g
	 * @param sctid
	 * @return
	 */
	protected static Iterable<Vertex> getVertexByIdType(TitanGraph g, String sctId, String type) {

		//check to if given sctid and effective time exist.
		Iterable<Vertex> vs = g.query()
					.has(Properties.sctid.toString(), sctId)
					.has(Properties.type.toString(), type).vertices();

		return vs;
	}
	
	
	/**Get a edge associated with a sctid and given effective time
	 * @param g
	 * @param sctid
	 * @return
	 */
	protected static Edge getEdgeByIdEffectiveTime(TitanGraph g, Rf2Relationship rel) {

		//check to if given sctid and effective time exist.
		Iterable<Edge> es = g.query()
					.has(Properties.sctid.toString(), rel.getId())
					.has(Properties.effectiveTime.toString(), rel.getEffectiveTime().getMillis()).edges();

		for (Edge e : es) {
			
			String sourceId = e.getVertex(Direction.OUT) != null ? (String)e.getVertex(Direction.OUT).getProperty(Properties.sctid.toString()) : null;
			String destinationId = e.getVertex(Direction.IN) != null ? (String)e.getVertex(Direction.IN).getProperty(Properties.sctid.toString()) : null;

			if(rel.getSourceId().equals(sourceId) && rel.getDestinationId().equals(destinationId)) {
				
				LOGGER.trace("getEdgeByIdEffectiveTime - returning edge as {} ", e);
				return e;

			}
		}

		return null;
	}
	
	/**Get a edge associated with a sctid and given effective time
	 * @param g
	 * @param sctid
	 * @return
	 */
	protected static Iterable<Edge> getEdgeByIdType(TitanGraph g, String sctid, String type) {

		//check to if given sctid and effective time exist.
		Iterable<Edge> es = g.query()
					.has(Properties.sctid.toString(), sctid)
					.has(Properties.type.toString(), type).edges();
		return es;
	}
	
	/**
	 * @param millis
	 * @return
	 */
	protected static boolean isHistoryEdge(TitanGraph g, Rf2Relationship rel) {

		//get the nearest existing effective time to current effective time
		Long currentEt = rel.getEffectiveTime().getMillis();

		//check to if given sctid and effective time exist.
		Iterable<Edge> es = g.query()
					.has(Properties.sctid.toString(), rel.getId())
					.has(Properties.type.toString(), Types.relationship.toString())
					.has(Properties.end.toString(), Long.MAX_VALUE).edges();
		for (Edge edge : es) {
			
			Long existing = edge.getProperty(Properties.effectiveTime.toString());
			if (currentEt.compareTo(existing) > 0) {
				
				return true;
			}
		}
		return false;
	
				
	}
	
	protected static boolean verifyTSV(String file) throws Exception {
		
		LOGGER.debug("verifyTSV Verifying file");
		if (StringUtils.isBlank(file)) {
			
			throw new IllegalArgumentException("Please check, input file is mandatory");
			
		}
		
		BufferedReader reader = null;
		
		try {
			
			if(file.endsWith(".gz")) {
				
		        reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(file)), "utf-8"));
		        
			} else {
				
				reader = new BufferedReader(new FileReader(file));
			}
			
			LOGGER.debug("Starting to load file {}", file);
	        
	        String line;

	        int noOfColumn = 0;
	        	        
	        while( (line = reader.readLine()) != null ) {
	        	
            	
	        	String [] column = line.split("\t");
	        	if (noOfColumn == 0) {
					
		        	noOfColumn = column.length;
		        	//going forward every line in file should match header length
				}
	        	
	        	if ( !(noOfColumn == column.length && ( noOfColumn == 5 
	        			|| noOfColumn == 9 
	        			|| noOfColumn == 10))) {

	        		LOGGER.debug("Check line ={} ", line);

	        		throw new IllegalArgumentException("Supplied tsv file does not conform to concept or description or relationship rf file. "
	        				+ "data within check, input file is mandatory");
				}
	        	
	        }
	        
	        
	        
		} catch (Exception e) {
			
			throw e;
		} finally {
			
			if (reader != null) {
				
				reader.close();
			}
		}

		return true;
		
	}
	

	
	
}
