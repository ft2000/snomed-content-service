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

import static org.ihtsdo.otf.snomed.loader.RF2ImportHelper.getEdgeByIdEffectiveTime;
import static org.ihtsdo.otf.snomed.loader.RF2ImportHelper.getEdgeByIdType;
import static org.ihtsdo.otf.snomed.loader.RF2ImportHelper.getVertex;
import static org.ihtsdo.otf.snomed.loader.RF2ImportHelper.isHistoryEdge;

import java.util.ArrayList;
import java.util.List;

import org.ihtsdo.otf.snomed.domain.Properties;
import org.ihtsdo.otf.snomed.domain.Types;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thinkaurelius.titan.core.TitanGraph;
import com.tinkerpop.blueprints.Edge;

/**Processor for relationship handling.
 * All relationships are considered as edge.
 * 
 *
 */
public class RelationshipProcessor {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RelationshipProcessor.class);

	private static final DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyyMMdd");

	protected static void processRelationship(Rf2Relationship rel, TitanGraph g) {
		
		long start = System.currentTimeMillis();
		
		LOGGER.debug("Processing relationship {}", rel.getId());
		
		//duplicate or reload check//this will check both historic as well as current relationship edge as check is done on sctid and effective time.
		Edge eRel = getEdgeByIdEffectiveTime(g, rel);
		
		if (eRel != null) {
			
			LOGGER.debug("Relationship {} already exist, not processing again", rel.getId());

			return;
		}
		
		//now check if same sctid and type combination exist.(excluding effectiveTime). 
		//This is to decide if this edge needed to be marked as history or current based on its effective time
		Iterable<Edge> eERels = getEdgeByIdType(g, rel.getId(), Types.relationship.toString());
		
		List<Long> exEffectiveTimes = new ArrayList<Long>();
		
		for (Edge e : eERels) {
			
			if(e.getPropertyKeys().contains(Properties.effectiveTime.toString())) {
				
				Long existingEffectiveTime = e.getProperty(Properties.effectiveTime.toString());
				exEffectiveTimes.add(existingEffectiveTime);
				

			}
			
		}
		
		
		if (exEffectiveTimes.isEmpty()) {
			
			//case when first time this relationship edge is being added to the system. 
			Edge eR = g.addEdge(rel.getId(), 
					getVertex(g, rel.getSourceId()), 
					getVertex(g, rel.getDestinationId()), 
					Types.relationship.toString());
			
			addRelationshipProperties(eR, rel);
			return;
		}
		
		boolean isHistory = isHistoryEdge(g, rel);
		
		if (isHistory) {
			
			//is this is true then newly created edge should be a history and should have a end effective time.

			String type = Types.hRelationship.toString();
			
			Edge eR = g.addEdge(rel.getId(), 
					getVertex(g, rel.getSourceId()), 
					getVertex(g, rel.getDestinationId()), 
					type);
			rel.setVertexType(type);
			addRelationshipProperties(eR, rel);
			
			eR.setProperty(Properties.end.toString(), rel.getEffectiveTime().getMillis());
			
		} else {
			
			//add this 
			String type = Types.relationship.toString();
			Edge eR = g.addEdge(rel.getId(), 
					getVertex(g, rel.getSourceId()), 
					getVertex(g, rel.getDestinationId()), 
					type);
			rel.setVertexType(type);

			addRelationshipProperties(eR, rel);
			
			//make  sure all other edges are 'hRelationship'
			
			for (Edge e : eERels) {
				
				if(e.getPropertyKeys().contains(Properties.effectiveTime.toString()) && e.getPropertyKeys().contains(Properties.end.toString())) {
					
					Long end = e.getProperty(Properties.end.toString());
					if (end == Long.MAX_VALUE) {
						
						e.setProperty(Properties.end.toString(), rel.getEffectiveTime().getMillis());
						e.setProperty(Properties.type.toString(), Types.hRelationship.toString());
					}

				}
				
			}

		}

		LOGGER.trace("ProcessDescription total time {} sec ", (System.currentTimeMillis() - start)/1000);

	}

	protected static Rf2Relationship getRelationship(String [] columns) {
		
		Rf2Relationship rel = new Rf2Relationship();
		//id	effectiveTime	active	moduleId	sourceId	destinationId	relationshipGroup	typeId	characteristicTypeId modifierId

		rel.setId(columns[0]);
		rel.setEffectiveTime(fmt.parseDateTime(columns[1]));
		rel.setActive(columns[2]);
		rel.setModuleId(columns[3]);
		rel.setSourceId(columns[4]);
		rel.setDestinationId(columns[5]);
		rel.setRelationshipGroup(columns[6]);
		rel.setTypeId(columns[7]);
		rel.setCharacteristicTypeId(columns[8]);
		rel.setModifierId(columns[9]);
		rel.setVertexType(Types.relationship.toString());
		rel.setLoadedBy("system");

		return rel;
	}
	
	private static Edge addRelationshipProperties(Edge eR, Rf2Relationship rel) {
		
		eR.setProperty(Properties.sctid.toString(), rel.getId());
		eR.setProperty(Properties.effectiveTime.toString(), rel.getEffectiveTime().getMillis());
		eR.setProperty(Properties.status.toString(), rel.getActive());
		eR.setProperty(Properties.moduleId.toString(), rel.getLoadedBy());
		eR.setProperty(Properties.group.toString(), rel.getRelationshipGroup());
		eR.setProperty(Properties.typeId.toString(), rel.getTypeId());
		eR.setProperty(Properties.characteristicId.toString(), rel.getCharacteristicTypeId());
		eR.setProperty(Properties.modifierId.toString(), rel.getModifierId());

		eR.setProperty(Properties.created.toString(), new DateTime().getMillis());
		eR.setProperty(Properties.createdBy.toString(), rel.getLoadedBy());
		eR.setProperty(Properties.type.toString(), rel.getVertexType());
		eR.setProperty(Properties.start.toString(), rel.getEffectiveTime().getMillis());
		eR.setProperty(Properties.end.toString(), Long.MAX_VALUE);


		return eR;
	}

}
