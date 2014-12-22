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

import static org.ihtsdo.otf.snomed.loader.RF2ImportHelper.getVertexByIdEffectiveTime;
import static org.ihtsdo.otf.snomed.loader.RF2ImportHelper.getVertexByIdType;

import org.ihtsdo.otf.snomed.domain.Properties;
import org.ihtsdo.otf.snomed.domain.Relationship;
import org.ihtsdo.otf.snomed.domain.Types;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thinkaurelius.titan.core.TitanGraph;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;

/**Class to process Concepts from Concept RF2 file.
 * It does not differentiate between RF2 files and does all the relevent checks before 
 * adding in database
 *
 */
public class ConceptProcessor {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ConceptProcessor.class);

	private static final DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyyMMdd");

	protected static void processConcept(Rf2Concept c, TitanGraph g) {
		
		long start = System.currentTimeMillis();
		
		LOGGER.debug("Processing concept {}", c.getId());
		
		//duplicate or reload check//this will check both historic as well as current vertex as check is done on sctid and effective time.
		Vertex vDe = getVertexByIdEffectiveTime(g, c);
		if (vDe != null) {
			
			LOGGER.debug("concept {} already exist, not processing again", c.getId());

			return;
		}
		
		//now check if same sctid and type combination exist.(excluding effectiveTime). 
		//This is to decide if this vertex needed to be history or current based on its effective time
		Iterable<Vertex> vECs = getVertexByIdType(g, c.getId(), Types.concept.toString());
		long existingEffectiveTime = 0;
		Vertex vEC = null;
		for (Vertex v : vECs) {
			
			if(v.getPropertyKeys().contains(Properties.effectiveTime.toString())) {
				
				existingEffectiveTime = v.getProperty(Properties.effectiveTime.toString());
				vEC = v;
				break;
			}
			
		}
		
		String type = Types.concept.toString();
		
		if (existingEffectiveTime == 0) {
			
			//case when first time this concept vertex is being added to the system. 
			Vertex vC = g.addVertexWithLabel(type);
			addConceptProperties(vC, c);

			return;//do not do any more check as this is first time concept is being loaded
			
		} else if (existingEffectiveTime > c.getEffectiveTime().getMillis()) {
			
			//if this is true then newly created vertex should be a history and should have a 'hasState' relation with existing vertex.
			type = Types.hConcept.toString();
			Vertex vHC = g.addVertexWithLabel(type);
			c.setVertexType(type);
			addConceptProperties(vHC, c);
			
			Edge state = vEC.addEdge(Relationship.hasState.toString(), vHC);
			state.setProperty(Properties.start.toString(), vEC.getProperty(Properties.effectiveTime.toString()));
			state.setProperty(Properties.end.toString(), c.getEffectiveTime().getMillis());

			
		} else if (existingEffectiveTime < c.getEffectiveTime().getMillis()) {
			
			//is this is true then existing data should be copied to a historical state vertex and existing vertex should be updated with latest values.
			type = Types.hConcept.toString();
			Vertex vHC = g.addVertexWithLabel(type);
			Rf2Concept hc = getRF2Concept(vEC);
			
			hc.setVertexType(type);

			addConceptProperties(vHC, hc);
			
			Edge state = vEC.addEdge(Relationship.hasState.toString(), vHC);
			state.setProperty(Properties.start.toString(), existingEffectiveTime);
			state.setProperty(Properties.end.toString(), c.getEffectiveTime().getMillis());
			
			//refresh existing concept with new values to make it current
			addConceptProperties(vEC, c);
			
			

		}
		
		

		
		LOGGER.trace("Concept  total process time {} sec ", (System.currentTimeMillis() - start)/1000);

	}
	

	

	/**
	 * @param vED
	 * @return
	 */
	private static Rf2Concept getRF2Concept(Vertex vED) {
		Rf2Concept c = new Rf2Concept();
		
		String status = vED.getProperty(Properties.status.toString());
		c.setActive(status);
		
		String id = vED.getProperty(Properties.sctid.toString());
		c.setId(id);

		long effectiveTime = vED.getProperty(Properties.effectiveTime.toString());
		c.setEffectiveTime(new DateTime(effectiveTime));

		long created = vED.getProperty(Properties.created.toString());
		c.setLoadedDate(new DateTime(created));

		String createdBy = vED.getProperty(Properties.createdBy.toString());
		c.setLoadedBy(createdBy);


		String type = vED.getProperty(Properties.type.toString());
		c.setVertexType(type);

		String moduleId = vED.getProperty(Properties.moduleId.toString());
		c.setModuleId(moduleId);

		String definitionStatusId = vED.getProperty(Properties.definitionStatusId.toString());
		c.setDefinitionStatusId(definitionStatusId);

		return c;
	}

	protected static Rf2Concept getConcept(String [] columns) {
		
		Rf2Concept c = new Rf2Concept();
		//id	effectiveTime	active	moduleId	definitionStatusId

		c.setId(columns[0]);
		c.setEffectiveTime(fmt.parseDateTime(columns[1]));
		c.setActive(columns[2]);
		c.setModuleId(columns[3]);
		c.setDefinitionStatusId(columns[4]);
		c.setVertexType(Types.concept.toString());
		c.setLoadedBy("system");
		return c;
	}
	
	private static Vertex addConceptProperties(Vertex vD, Rf2Concept c) {
		
		vD.setProperty(Properties.sctid.toString(), c.getId());
		vD.setProperty(Properties.effectiveTime.toString(), c.getEffectiveTime().getMillis());
		vD.setProperty(Properties.status.toString(), c.getActive());
		vD.setProperty(Properties.created.toString(), new DateTime().getMillis());
		vD.setProperty(Properties.createdBy.toString(), c.getLoadedBy());
		vD.setProperty(Properties.type.toString(), c.getVertexType());
		vD.setProperty(Properties.moduleId.toString(), c.getModuleId());
		vD.setProperty(Properties.definitionStatusId.toString(), c.getDefinitionStatusId());

		
		return vD;
	}

}
