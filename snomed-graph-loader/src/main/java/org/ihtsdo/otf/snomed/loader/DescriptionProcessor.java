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
import static org.ihtsdo.otf.snomed.loader.RF2ImportHelper.getVertex;
import static org.ihtsdo.otf.snomed.loader.RF2ImportHelper.getVertexByIdEffectiveTime;
import static org.ihtsdo.otf.snomed.loader.RF2ImportHelper.getVertexByIdType;

import org.ihtsdo.otf.snomed.domain.DescriptionType;
import org.ihtsdo.otf.snomed.domain.Properties;
import org.ihtsdo.otf.snomed.domain.Relationship;
import org.ihtsdo.otf.snomed.domain.Types;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thinkaurelius.titan.core.TitanGraph;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;

/**
 *
 */
public class DescriptionProcessor {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DescriptionProcessor.class);

	private static final DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyyMMdd");

	protected static void processDescription(Rf2Description desc, TitanGraph g) {
		
		long start = System.currentTimeMillis();
		
		LOGGER.debug("Processing description {}", desc.getId());
		
		//duplicate or reload check//this will check both historic as well as current vertex as check is done on sctid and effective time.
		Vertex vDe = getVertexByIdEffectiveTime(g, desc);
		if (vDe != null) {
			
			LOGGER.debug("Description {} already exist, not processing again", desc.getId());

			return;
		}
		
		//now check if same sctid and type combination exist.(excluding effectiveTime). 
		//This is to decide if this vertex needed to be history or current based on its effective time
		Iterable<Vertex> vEDs = getVertexByIdType(g, desc.getId(), Types.description.toString());
		long existingEffectiveTime = 0;
		Vertex vED = null;
		for (Vertex v : vEDs) {
			
			if(v.getPropertyKeys().contains(Properties.effectiveTime.toString())) {
				
				existingEffectiveTime = v.getProperty(Properties.effectiveTime.toString());
				vED = v;
				break;
			}
			
		}
		
		String type = Types.description.toString();
		Vertex vD = vED;
		
		if (existingEffectiveTime == 0) {
			
			//case when first time this description vertex is being added to the system. 
			vD = g.addVertexWithLabel(type);
			addDescriptionProperties(vD, desc);
			
			
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
	
				vC.addEdge(Relationship.hasDescription.toString(), vD);
	
			} else {
				
				LOGGER.error("Could not find concept for id {}", desc.getConceptId());
			}
			
		
		} else if (existingEffectiveTime > desc.getEffectiveTime().getMillis()) {
			
			//is this is true then newly created vertex should be a history and should have a 'hasState' relation with existing vertex.
			//And no refresh required in existing description vertex
			type = Types.hDescription.toString();
			Vertex vHD = g.addVertexWithLabel(type);
			desc.setVertexType(type);
			addDescriptionProperties(vHD, desc);
			
			Edge state = vED.addEdge(Relationship.hasState.toString(), vHD);
			state.setProperty(Properties.start.toString(), vED.getProperty(Properties.effectiveTime.toString()));
			state.setProperty(Properties.end.toString(), desc.getEffectiveTime().getMillis());

			
		} else if (existingEffectiveTime < desc.getEffectiveTime().getMillis()) {
			
			//is this is true then existing data should be copied to a historical state vertex and existing vertex should be updated with latest values.
			type = Types.hDescription.toString();
			Vertex vHD = g.addVertexWithLabel(type);
			Rf2Description hDesc = getRF2Descrition(vED);
			hDesc.setVertexType(type);
			addDescriptionProperties(vHD, hDesc);
			
			//refresh existing description with new values to make it current
			addDescriptionProperties(vED, desc);
			
			Edge state = vED.addEdge(Relationship.hasState.toString(), vHD);
			state.setProperty(Properties.start.toString(), existingEffectiveTime);
			state.setProperty(Properties.end.toString(), desc.getEffectiveTime().getMillis());

		}
		
		if (isConceptRelationChanged(vED, desc)) {
		
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
	
				vC.addEdge(Relationship.hasDescription.toString(), vD);
	
			} else {
				
				LOGGER.error("Could not find concept for id {}", desc.getConceptId());
			}
			
		}
	
		
		LOGGER.trace("ProcessDescription total time {} sec ", (System.currentTimeMillis() - start)/1000);

	}

	
	
	
	
	/**
	 * 
	 */
	private static boolean isConceptRelationChanged(Vertex vExistingDescription, Rf2Description desc) {

		if (vExistingDescription == null) {
			
			return false;
		}
		//check if this is the same module associated with this description vertex.
		Iterable<Vertex> vCDs = vExistingDescription.getVertices(Direction.OUT, Relationship.hasDescription.toString());
		
		for (Vertex vCD : vCDs) {
			
			String sctdID = vCD.getProperty(Properties.sctid.toString());
			if (desc.getConceptId().equals(sctdID)) {
				
				return false;
			}
		}
		
		return true;
		
	}

	/**
	 * @param vED
	 * @return
	 */
	private static Rf2Description getRF2Descrition(Vertex vED) {
		Rf2Description desc = new Rf2Description();
		
		String status = vED.getProperty(Properties.status.toString());
		desc.setActive(status);
		
		String id = vED.getProperty(Properties.sctid.toString());
		desc.setId(id);

		long effectiveTime = vED.getProperty(Properties.effectiveTime.toString());
		desc.setEffectiveTime(new DateTime(effectiveTime));

		long created = vED.getProperty(Properties.created.toString());
		desc.setLoadedDate(new DateTime(created));

		String createdBy = vED.getProperty(Properties.createdBy.toString());
		desc.setLoadedBy(createdBy);

		String languageCode = vED.getProperty(Properties.languageCode.toString());
		desc.setLanguageCode(languageCode);

		String title = vED.getProperty(Properties.title.toString());
		desc.setTerm(title);

		String type = vED.getProperty(Properties.type.toString());
		desc.setVertexType(type);

		String moduleId = vED.getProperty(Properties.moduleId.toString());
		desc.setModuleId(moduleId);

		String caseSignificanceId = vED.getProperty(Properties.caseSignificanceId.toString());
		desc.setCaseSignificanceId(caseSignificanceId);

		String typeId = vED.getProperty(Properties.typeId.toString());
		desc.setTypeId(typeId);

		return desc;
	}

	protected static Rf2Description getDescription(String [] columns) {
		
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
		desc.setVertexType(Types.description.toString());
		desc.setLoadedBy("system");

		return desc;
	}
	
	private static Vertex addDescriptionProperties(Vertex vD, Rf2Description desc) {
		
		vD.setProperty(Properties.sctid.toString(), desc.getId());
		vD.setProperty(Properties.effectiveTime.toString(), desc.getEffectiveTime().getMillis());
		vD.setProperty(Properties.status.toString(), desc.getActive());
		vD.setProperty(Properties.created.toString(), new DateTime().getMillis());
		vD.setProperty(Properties.createdBy.toString(), desc.getLoadedBy());
		vD.setProperty(Properties.languageCode.toString(), desc.getLanguageCode());
		vD.setProperty(Properties.title.toString(), desc.getTerm());
		vD.setProperty(Properties.type.toString(), desc.getVertexType());
		vD.setProperty(Properties.moduleId.toString(), desc.getModuleId());
		vD.setProperty(Properties.caseSignificanceId.toString(), desc.getCaseSignificanceId());
		vD.setProperty(Properties.typeId.toString(), desc.getTypeId());//terminology type

		return vD;
	}

}
