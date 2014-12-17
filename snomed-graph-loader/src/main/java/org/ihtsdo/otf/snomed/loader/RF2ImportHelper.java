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

import java.util.HashMap;
import java.util.Map;

import org.ihtsdo.otf.snomed.domain.DescriptionType;
import org.ihtsdo.otf.snomed.domain.Properties;
import org.ihtsdo.otf.snomed.domain.Relationship;
import org.ihtsdo.otf.snomed.domain.Types;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thinkaurelius.titan.core.TitanGraph;
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
	protected static Vertex processType(TitanGraph g, String typeId) {

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
	
	
}
