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
package org.ihtsdo.otf.snomed.schema;

import org.apache.commons.lang.StringUtils;
import org.ihtsdo.otf.snomed.domain.DescriptionType;
import org.ihtsdo.otf.snomed.domain.Properties;
import org.ihtsdo.otf.snomed.domain.Relationship;
import org.ihtsdo.otf.snomed.domain.Types;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thinkaurelius.titan.core.Order;
import com.thinkaurelius.titan.core.RelationType;
import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;
import com.thinkaurelius.titan.core.VertexLabel;
import com.thinkaurelius.titan.core.schema.Parameter;
import com.thinkaurelius.titan.core.schema.TitanGraphIndex;
import com.thinkaurelius.titan.core.schema.TitanManagement;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
/**
 *
 */
public class SnomedConceptSchema {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SnomedConceptSchema.class);

	private static final String SEARCH = "snomed";

	private static final String MAPPED = "mapped-name";
	
	private String config;

	/**
	 * 
	 */
	public SnomedConceptSchema(String config) {
		// TODO Auto-generated constructor stub
		
	    this.config = config;
	    if (StringUtils.isBlank(config)) {
			
	    	throw new IllegalArgumentException("No graph configuration provided to conncet graph db");
		}

	}
	
	
	public void createSchema() {
		
		LOGGER.debug("Creating Snomed Schema");
		
		TitanGraph g = openGraph(config);
	    //management api
	    TitanManagement mgmt = g.getManagementSystem();
	    
	    try {
			
	    	String status = Properties.status.toString();
	    	if(!mgmt.containsRelationType(status)) {
		    	
				LOGGER.debug("Creating property key {}" , status);

		    	mgmt.makePropertyKey(status).dataType(String.class).make();
		    	
		    }
		    
	    	String lang = Properties.languageCode.toString();
		    if(!mgmt.containsRelationType(lang)) {
		    	
				LOGGER.debug("Creating property key {}" , lang);

		    	mgmt.makePropertyKey(lang).dataType(String.class).make();
		    	
		    }
		    
		    String group = Properties.group.toString();
		    if(!mgmt.containsRelationType(group)) {
		    	
				LOGGER.debug("Creating property key {}" , group);

		    	mgmt.makePropertyKey(group).dataType(String.class).make();
		    	
		    }
		    
		    String effectiveTime = Properties.effectiveTime.toString();
		    if(!mgmt.containsRelationType(effectiveTime)) {
		    	
				LOGGER.debug("Creating property key {}" , effectiveTime);

		    	mgmt.makePropertyKey(effectiveTime).dataType(Long.class).make();
		    	
		    }
		    
		    String sctid = Properties.sctid.toString();
		    if(!mgmt.containsRelationType(sctid)) {
		    	
				LOGGER.debug("Creating property key {}" , sctid);

		    	mgmt.makePropertyKey(sctid).dataType(String.class).make();
		    	
		    }
		    
		    String created = Properties.created.toString();
			if(!mgmt.containsRelationType(created)) {
				    	
				LOGGER.debug("Creating property key {}" , created);

		    	mgmt.makePropertyKey(created).dataType(Long.class).make();
		    	
		    }
			
			String createdBy = Properties.createdBy.toString();
			if(!mgmt.containsRelationType(createdBy)) {
				
				LOGGER.debug("Creating property key {}" , createdBy);
			
				mgmt.makePropertyKey(createdBy).dataType(String.class).make();
				
			}
			
			String modifiedDate = Properties.modifiedDate.toString();
			if(!mgmt.containsRelationType(modifiedDate)) {
				
				LOGGER.debug("Creating property key {}" , modifiedDate);
			
				mgmt.makePropertyKey(modifiedDate).dataType(Long.class).make();
				
			}
			
			
			String modifiedBy = Properties.modifiedBy.toString();
			if(!mgmt.containsRelationType(modifiedBy)) {
				
				LOGGER.debug("Creating property key {}" , modifiedBy);
			
				mgmt.makePropertyKey(modifiedBy).dataType(String.class).make();
				
			}
			
			String typeId = Properties.typeId.toString();
			if(!mgmt.containsRelationType(typeId)) {
				
				LOGGER.debug("Creating property key {}" , typeId);
			
				mgmt.makePropertyKey(typeId).dataType(String.class).make();
				
			}
			
			String title = Properties.title.toString();
			if(!mgmt.containsRelationType(title)) {
				
				LOGGER.debug("Creating property key {}" , title);
			
				mgmt.makePropertyKey(title).dataType(String.class).make();
				
			}
			
			String modifierId = Properties.modifierId.toString();
			if(!mgmt.containsRelationType(modifierId)) {
				
				LOGGER.debug("Creating property key {}" , modifierId);
			
				mgmt.makePropertyKey(modifierId).dataType(String.class).make();
				
			}
			
			
			String characteristic = Properties.characteristic.toString();
			if(!mgmt.containsRelationType(characteristic)) {
				
				LOGGER.debug("Creating property key {}" , characteristic);
			
				mgmt.makePropertyKey(characteristic).dataType(String.class).make();
				
			}
			
			String characteristicId = Properties.characteristicId.toString();
			if(!mgmt.containsRelationType(characteristicId)) {
				
				LOGGER.debug("Creating property key {}" , characteristicId);
			
				mgmt.makePropertyKey(characteristicId).dataType(String.class).make();
				
			}
			
			String inferred = Properties.inferred.toString();
			if(!mgmt.containsRelationType(inferred)) {
				
				LOGGER.debug("Creating property key {}" , inferred);
			
				mgmt.makePropertyKey(inferred).dataType(String.class).make();
			
			}
			

			String additional = Properties.additional.toString();
			if(!mgmt.containsRelationType(additional)) {
				
				LOGGER.debug("Creating property key {}" , additional);
			
				mgmt.makePropertyKey(additional).dataType(String.class).make();
			
			}
			
			String qualifying = Properties.qualifying.toString();
			if(!mgmt.containsRelationType(qualifying)) {
				
				LOGGER.debug("Creating property key {}" , qualifying);
			
				mgmt.makePropertyKey(qualifying).dataType(String.class).make();
			
			}
			
			
			String stated = Properties.stated.toString();
			if(!mgmt.containsRelationType(stated)) {
				
				LOGGER.debug("Creating property key {}" , stated);
			
				mgmt.makePropertyKey(stated).dataType(String.class).make();
			
			}
			
			String moduleId = Properties.moduleId.toString();
			if(!mgmt.containsRelationType(moduleId)) {
				
				LOGGER.debug("Creating property key {}" , moduleId);
			
				mgmt.makePropertyKey(moduleId).dataType(String.class).make();
			
			}
			
			String caseSignificanceId = Properties.caseSignificanceId.toString();
			if(!mgmt.containsRelationType(caseSignificanceId)) {
				
				LOGGER.debug("Creating property key {}" , caseSignificanceId);
			
				mgmt.makePropertyKey(caseSignificanceId).dataType(String.class).make();
			
			}
			
			String definitionStatusId = Properties.definitionStatusId.toString();
			if(!mgmt.containsRelationType(definitionStatusId)) {
				
				LOGGER.debug("Creating property key {}" , definitionStatusId);
			
				mgmt.makePropertyKey(definitionStatusId).dataType(String.class).make();
			
			}
			
			
			String start = Properties.start.toString();
			if(!mgmt.containsRelationType(start)) {
				
				LOGGER.debug("Creating property key {}" , start);
			
				mgmt.makePropertyKey(start).dataType(Long.class).make();
			
			}
			
			String end = Properties.end.toString();
			if(!mgmt.containsRelationType(end)) {
				
				LOGGER.debug("Creating property key {}" , end);
			
				mgmt.makePropertyKey(end).dataType(Long.class).make();
			
			}
			
			
			String type = Properties.type.toString();
			if(!mgmt.containsRelationType(type)) {
				
				LOGGER.debug("Creating property key {}" , type);
			
				mgmt.makePropertyKey(type).dataType(String.class).make();
			
			}
			
			
			//make necessary labels
			String concept = Types.concept.toString();
			if(!mgmt.containsVertexLabel(concept)) {
				
				LOGGER.debug("Creating vertex label {}" , concept);
			
				mgmt.makeVertexLabel(concept).make();

			}
			
			String description = Types.description.toString();
			if(!mgmt.containsVertexLabel(description)) {
				
				LOGGER.debug("Creating vertex label {}" , description);
			
				mgmt.makeVertexLabel(description).make();

			}
			
			String relationship = Types.relationship.toString();
			if(!mgmt.containsRelationType(relationship)) {
				
				LOGGER.debug("Creating edge label {}" , relationship);
			
				mgmt.makeEdgeLabel(relationship).make();

			}
			
			
			//make necessary history labels
			String hConcept = Types.hConcept.toString();
			if(!mgmt.containsVertexLabel(hConcept)) {
				
				LOGGER.debug("Creating vertex label {}" , hConcept);
			
				mgmt.makeVertexLabel(hConcept).make();

			}
			
			String hDescription = Types.hDescription.toString();
			if(!mgmt.containsVertexLabel(hDescription)) {
				
				LOGGER.debug("Creating vertex label {}" , hDescription);
			
				mgmt.makeVertexLabel(hDescription).make();

			}
			
			String hRelationship = Types.hRelationship.toString();
			if(!mgmt.containsRelationType(hRelationship)) {
				
				LOGGER.debug("Creating edge label {}" , hRelationship);
			
				mgmt.makeEdgeLabel(hRelationship).make();

			}			
			
			
			
			String fsn = DescriptionType.fsn.toString();
			if(!mgmt.containsRelationType(fsn)) {
				
				LOGGER.debug("Creating edge label {}" , fsn);
			
				mgmt.makeEdgeLabel(fsn).make();

			}

			
			String ds = Relationship.ds.toString();
			if(!mgmt.containsRelationType(ds)) {
				
				LOGGER.debug("Creating edge label {}" , ds);
			
				mgmt.makeEdgeLabel(ds).make();
			
			}
			
			String subClassOf = Relationship.isA.toString();
			
			if(!mgmt.containsRelationType(subClassOf)) {
				
				LOGGER.debug("Creating edge label {}" , subClassOf);
			
				mgmt.makeEdgeLabel(subClassOf).make();
			
			}
			
			String fs = Relationship.fs.toString();
			
			if(!mgmt.containsRelationType(fs)) {
				
				LOGGER.debug("Creating edge label {}" , fs);
			
				mgmt.makeEdgeLabel(fs).make();
			
			}
			
			String ps = Relationship.ps.toString();
			
			if(!mgmt.containsRelationType(ps)) {
				
				LOGGER.debug("Creating edge label {}" , ps);
			
				mgmt.makeEdgeLabel(ps).make();
			
			}
			
			String using = Relationship.using.toString();
			
			if(!mgmt.containsRelationType(using)) {
				
				LOGGER.debug("Creating edge label {}" , using);
			
				mgmt.makeEdgeLabel(using).make();
			
			}
			
			String generic = Relationship.generic.toString();
			
			if(!mgmt.containsRelationType(generic)) {
				
				LOGGER.debug("Creating edge label {}" , generic);
			
				mgmt.makeEdgeLabel(generic).make();
			
			}
			
			String method = Relationship.method.toString();
			
			if(!mgmt.containsRelationType(method)) {
				
				LOGGER.debug("Creating edge label {}" , method);
			
				mgmt.makeEdgeLabel(method).make();
			
			}
			
			
			
			String synonym = DescriptionType.synonym.toString();
			if(!mgmt.containsRelationType(synonym)) {
				
				LOGGER.debug("Creating edge label {}" , synonym);
			
				mgmt.makeEdgeLabel(synonym).make();
			
			}
			
			
			
			String hasState = Relationship.hasState.toString();
			if(!mgmt.containsRelationType(hasState)) {
				
				LOGGER.debug("Creating edge label {}" , hasState);
			
				mgmt.makeEdgeLabel(hasState).make();
			
			}
			
			
			
			
			//titan doc advise that property key index creation should be done as part same transaction in which these keys are being created

	    	if(!mgmt.containsGraphIndex(CompositeIndex.bySctId.toString())) {
		    	
				LOGGER.debug("Creating index on key {}" , CompositeIndex.bySctId.toString());

		    	mgmt.buildIndex(CompositeIndex.bySctId.toString(), Vertex.class)
		    	.addKey(mgmt.getPropertyKey(sctid))
		    	.buildCompositeIndex();
		    	
		    }
	    	
	    	if(!mgmt.containsGraphIndex(CompositeIndex.bySctIdAndType.toString())) {
		    	
				LOGGER.debug("Creating index on key {}" , CompositeIndex.bySctIdAndType.toString());

		    	mgmt.buildIndex(CompositeIndex.bySctIdAndType.toString(), Vertex.class)
		    	.addKey(mgmt.getPropertyKey(sctid))
		    	.addKey(mgmt.getPropertyKey(type))
		    	.buildCompositeIndex();
		    	
		    }
	    	
	    	if(!mgmt.containsGraphIndex(CompositeIndex.conceptBySctId.toString())) {
		    	
				LOGGER.debug("Creating index on key {}" , CompositeIndex.conceptBySctId.toString());

		    	mgmt.buildIndex(CompositeIndex.conceptBySctId.toString(), Vertex.class)
		    	.addKey(mgmt.getPropertyKey(sctid))
		    	.addKey(mgmt.getPropertyKey(effectiveTime))
		    	.indexOnly(mgmt.getVertexLabel(Types.concept.toString())).buildCompositeIndex();
		    	
		    }
	    	
	    	if(!mgmt.containsGraphIndex(CompositeIndex.bySctIdAndEffectiveTime.toString())) {
		    	
				LOGGER.debug("Creating index on key {}" , CompositeIndex.bySctIdAndEffectiveTime.toString());

		    	mgmt.buildIndex(CompositeIndex.bySctIdAndEffectiveTime.toString(), Vertex.class)
		    	.addKey(mgmt.getPropertyKey(sctid))
		    	.addKey(mgmt.getPropertyKey(effectiveTime))
		    	.buildCompositeIndex();
		    	
		    }
	    	
	    	if(!mgmt.containsGraphIndex(title)) {
		    	
				LOGGER.debug("Creating index on key {}" , title);

		    	mgmt.buildIndex(title, Vertex.class).addKey(mgmt.getPropertyKey(title)).buildCompositeIndex();
		    	
		    }
	    	
	    	if(!mgmt.containsGraphIndex(CompositeIndex.relation.toString())) {
		    	
				LOGGER.debug("Creating index on key {}" , CompositeIndex.relation.toString());

		    	mgmt.buildIndex(CompositeIndex.relation.toString(), Edge.class)
		    		.addKey(mgmt.getPropertyKey(title))
		    		.addKey(mgmt.getPropertyKey(typeId))
		    		.addKey(mgmt.getPropertyKey(characteristic))
		    		.addKey(mgmt.getPropertyKey(effectiveTime)).buildCompositeIndex();
		    	
		    }

	    	
	    	if(!mgmt.containsGraphIndex(synonym)) {
		    	
				LOGGER.debug("Creating index on edge {}" , synonym);

		    	mgmt.buildEdgeIndex(mgmt.getEdgeLabel(synonym), synonym, Direction.BOTH, Order.DESC, mgmt.getPropertyKey(Properties.title.toString()));
		    	
		    }
	    	
	    	if(!mgmt.containsGraphIndex(fsn)) {
		    	
				LOGGER.debug("Creating index on edge {}" , fsn);

		    	mgmt.buildEdgeIndex(mgmt.getEdgeLabel(fsn), fsn, Direction.BOTH, Order.DESC, mgmt.getPropertyKey(Properties.title.toString()));

		    }
	    	
	    	//to lookup relationship edge by sctid and effective time
	    	
	    	if(!mgmt.containsGraphIndex(CompositeIndex.edgeBySctIdAndEffectiveTime.toString())) {
		    	
				LOGGER.debug("Creating index on key {}" , CompositeIndex.edgeBySctIdAndEffectiveTime.toString());

		    	mgmt.buildIndex(CompositeIndex.edgeBySctIdAndEffectiveTime.toString(), Edge.class)
		    	.addKey(mgmt.getPropertyKey(sctid))
		    	.addKey(mgmt.getPropertyKey(effectiveTime))
		    	.buildCompositeIndex();
		    	
		    }
	    	
	    	
	    	if(!mgmt.containsGraphIndex(CompositeIndex.edgeBySctIdAndType.toString())) {
		    	
				LOGGER.debug("Creating index on key {}" , CompositeIndex.edgeBySctIdAndType.toString());

		    	mgmt.buildIndex(CompositeIndex.edgeBySctIdAndType.toString(), Edge.class)
		    	.addKey(mgmt.getPropertyKey(sctid))
		    	.addKey(mgmt.getPropertyKey(type))
		    	.buildCompositeIndex();
		    	
		    }

			mgmt.commit();
			
		    LOGGER.info("Finished Schema Creation.");

		} catch (Exception e) {
			
			LOGGER.error("Management Transaction Rolledback");

			e.printStackTrace();
			mgmt.rollback();
			// TODO: handle exception
			throw new RuntimeException(e);
			
		} finally {
			
			g.shutdown();
		}
	    
	    
	    
	}
	
	/**
	 * @param config2
	 * @return
	 */
	private TitanGraph openGraph(String config) {


		TitanGraph g = TitanFactory.open(config);
		
		if (g == null) {
			
	    	throw new IllegalArgumentException("Could not initialize graph database. Check db connection configurations");
		}
		
		return g;
	}


	public void createIndex(String backingIndex) {
		
		
		LOGGER.debug("Creating {} Index", backingIndex);
		
		if (StringUtils.isEmpty(backingIndex)) {
			
			backingIndex = SEARCH;
		}
		TitanGraph g = openGraph(config);
	    //management api
	    TitanManagement mgmt = g.getManagementSystem();
	    
	    try {

	    	String isA = Relationship.isA.toString(); 
	    	if(!mgmt.containsGraphIndex(isA)) {
		    	
				LOGGER.debug("Creating index on edge {}" , isA);

				mgmt.buildIndex(isA, Edge.class)
	    		.indexOnly(mgmt.getRelationType(isA))
	    		.addKey(mgmt.getPropertyKey(Properties.sctid.toString()),
	    				Parameter.of(MAPPED, Properties.sctid.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.typeId.toString()),
	    				Parameter.of(MAPPED, Properties.typeId.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.status.toString()),
	    				Parameter.of(MAPPED, Properties.status.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.effectiveTime.toString()),
	    				Parameter.of(MAPPED, Properties.effectiveTime.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.group.toString()),
	    				Parameter.of(MAPPED, Properties.group.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.created.toString()),
	    				Parameter.of(MAPPED, Properties.created.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.createdBy.toString()),
	    				Parameter.of(MAPPED, Properties.createdBy.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.modifiedDate.toString()),
	    				Parameter.of(MAPPED, Properties.modifiedDate.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.modifiedBy.toString()),
	    				Parameter.of(MAPPED, Properties.modifiedBy.toString()))
				.addKey(mgmt.getPropertyKey(Properties.characteristic.toString()),
	    				Parameter.of(MAPPED, Properties.characteristic.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.type.toString()),
	    				Parameter.of(MAPPED, Properties.type.toString()))
	    		.buildMixedIndex(backingIndex);

		    	
		    }
	    	
	    	String fs = Relationship.fs.toString(); 
	    	if(!mgmt.containsGraphIndex(fs)) {
		    	
				LOGGER.debug("Creating index on edge {}" , fs);

				mgmt.buildIndex(fs, Edge.class)
	    		.indexOnly(mgmt.getRelationType(fs))
	    		.addKey(mgmt.getPropertyKey(Properties.sctid.toString()),
	    				Parameter.of(MAPPED, Properties.sctid.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.typeId.toString()),
	    				Parameter.of(MAPPED, Properties.typeId.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.status.toString()),
	    				Parameter.of(MAPPED, Properties.status.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.effectiveTime.toString()),
	    				Parameter.of(MAPPED, Properties.effectiveTime.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.group.toString()),
	    				Parameter.of(MAPPED, Properties.group.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.created.toString()),
	    				Parameter.of(MAPPED, Properties.created.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.createdBy.toString()),
	    				Parameter.of(MAPPED, Properties.createdBy.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.modifiedDate.toString()),
	    				Parameter.of(MAPPED, Properties.modifiedDate.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.modifiedBy.toString()),
	    				Parameter.of(MAPPED, Properties.modifiedBy.toString()))
				.addKey(mgmt.getPropertyKey(Properties.characteristic.toString()),
	    				Parameter.of(MAPPED, Properties.characteristic.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.type.toString()),
	    				Parameter.of(MAPPED, Properties.type.toString()))

	    		.buildMixedIndex(backingIndex);		    	
		    }
	    	
	    	String ps = Relationship.ps.toString(); 
	    	if(!mgmt.containsGraphIndex(ps)) {
		    	
				LOGGER.debug("Creating index on edge {}" , ps);

				mgmt.buildIndex(ps, Edge.class)
	    		.indexOnly(mgmt.getRelationType(ps))
	    		.addKey(mgmt.getPropertyKey(Properties.sctid.toString()),
	    				Parameter.of(MAPPED, Properties.sctid.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.typeId.toString()),
	    				Parameter.of(MAPPED, Properties.typeId.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.status.toString()),
	    				Parameter.of(MAPPED, Properties.status.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.effectiveTime.toString()),
	    				Parameter.of(MAPPED, Properties.effectiveTime.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.group.toString()),
	    				Parameter.of(MAPPED, Properties.group.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.created.toString()),
	    				Parameter.of(MAPPED, Properties.created.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.createdBy.toString()),
	    				Parameter.of(MAPPED, Properties.createdBy.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.modifiedDate.toString()),
	    				Parameter.of(MAPPED, Properties.modifiedDate.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.modifiedBy.toString()),
	    				Parameter.of(MAPPED, Properties.modifiedBy.toString()))
				.addKey(mgmt.getPropertyKey(Properties.characteristic.toString()),
	    				Parameter.of(MAPPED, Properties.characteristic.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.type.toString()),
	    				Parameter.of(MAPPED, Properties.type.toString()))
	    				
	    		.buildMixedIndex(backingIndex);		    	
		    	
		    }
	    	
	    	String method = Relationship.method.toString(); 
	    	if(!mgmt.containsGraphIndex(method)) {
		    	
				LOGGER.debug("Creating index on edge {}" , method);

				mgmt.buildIndex(method, Edge.class)
	    		.indexOnly(mgmt.getRelationType(method))
	    		.addKey(mgmt.getPropertyKey(Properties.sctid.toString()),
	    				Parameter.of(MAPPED, Properties.sctid.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.typeId.toString()),
	    				Parameter.of(MAPPED, Properties.typeId.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.status.toString()),
	    				Parameter.of(MAPPED, Properties.status.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.effectiveTime.toString()),
	    				Parameter.of(MAPPED, Properties.effectiveTime.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.group.toString()),
	    				Parameter.of(MAPPED, Properties.group.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.created.toString()),
	    				Parameter.of(MAPPED, Properties.created.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.createdBy.toString()),
	    				Parameter.of(MAPPED, Properties.createdBy.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.modifiedDate.toString()),
	    				Parameter.of(MAPPED, Properties.modifiedDate.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.modifiedBy.toString()),
	    				Parameter.of(MAPPED, Properties.modifiedBy.toString()))
				.addKey(mgmt.getPropertyKey(Properties.characteristic.toString()),
	    				Parameter.of(MAPPED, Properties.characteristic.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.type.toString()),
	    				Parameter.of(MAPPED, Properties.type.toString()))
	    		.buildMixedIndex(backingIndex);		    	
		    	
		    }
	    	

	    	
	    	String description = Types.description.toString(); 
	    	if(!mgmt.containsGraphIndex(description)) {
		    	
				LOGGER.debug("Creating index on vertex {}" , description);

				mgmt.buildIndex(description, Vertex.class)
	    		.indexOnly(mgmt.getVertexLabel(description))
	    		.addKey(mgmt.getPropertyKey(Properties.sctid.toString()),
	    				Parameter.of(MAPPED, Properties.sctid.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.effectiveTime.toString()),
	    				Parameter.of(MAPPED, Properties.effectiveTime.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.title.toString()),
	    				Parameter.of(MAPPED, Properties.title.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.status.toString()),
	    				Parameter.of(MAPPED, Properties.status.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.languageCode.toString()),
	    				Parameter.of(MAPPED, Properties.languageCode.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.type.toString()),
	    				Parameter.of(MAPPED, Properties.type.toString()))
				.addKey(mgmt.getPropertyKey(Properties.moduleId.toString()),
	    				Parameter.of(MAPPED, Properties.moduleId.toString()))
				.addKey(mgmt.getPropertyKey(Properties.caseSignificanceId.toString()),
	    				Parameter.of(MAPPED, Properties.caseSignificanceId.toString()))
				.addKey(mgmt.getPropertyKey(Properties.typeId.toString()),
	    				Parameter.of(MAPPED, Properties.typeId.toString()))
				.addKey(mgmt.getPropertyKey(Properties.created.toString()), 
				Parameter.of(MAPPED, Properties.created.toString()))
				
				.addKey(mgmt.getPropertyKey(Properties.createdBy.toString()), 
				Parameter.of(MAPPED, Properties.createdBy.toString()))
	    				
	    				
				.buildMixedIndex(backingIndex);
		    	
		    }
	    	
	    	String concept = Types.concept.toString(); 
	    	if(!mgmt.containsGraphIndex(concept)) {
		    	
				LOGGER.debug("Creating index on vertex {}" , concept);

		    	mgmt.buildIndex(concept, Vertex.class)
		    		.indexOnly(mgmt.getVertexLabel(concept))
		    		.addKey(mgmt.getPropertyKey(Properties.sctid.toString()),
		    				Parameter.of(MAPPED, Properties.sctid.toString()))
		    		.addKey(mgmt.getPropertyKey(Properties.title.toString()),
		    				Parameter.of(MAPPED, Properties.title.toString()))
		    		.addKey(mgmt.getPropertyKey(Properties.status.toString()),
		    				Parameter.of(MAPPED, Properties.status.toString()))
		    		.addKey(mgmt.getPropertyKey(Properties.effectiveTime.toString()),
		    				Parameter.of(MAPPED, Properties.effectiveTime.toString()))
		    		.addKey(mgmt.getPropertyKey(Properties.type.toString()),
		    				Parameter.of(MAPPED, Properties.type.toString()))
    				.addKey(mgmt.getPropertyKey(Properties.moduleId.toString()),
		    				Parameter.of(MAPPED, Properties.moduleId.toString()))
		    		.addKey(mgmt.getPropertyKey(Properties.definitionStatusId.toString()),
		    				Parameter.of(MAPPED, Properties.definitionStatusId.toString()))
					.addKey(mgmt.getPropertyKey(Properties.created.toString()), 
					Parameter.of(MAPPED, Properties.created.toString()))
					
					.addKey(mgmt.getPropertyKey(Properties.createdBy.toString()), 
					Parameter.of(MAPPED, Properties.createdBy.toString()))
		    				
		    		.buildMixedIndex(backingIndex);
		    	
		    }
	    	
	    	String relationship = Types.relationship.toString(); 
	    	if(!mgmt.containsGraphIndex(relationship)) {
		    	
				LOGGER.debug("Creating index on vertex {}" , relationship);
				mgmt.buildIndex(relationship, Edge.class)
	    		.indexOnly(mgmt.getEdgeLabel(relationship))
	    		.addKey(mgmt.getPropertyKey(Properties.sctid.toString()), 
	    				Parameter.of(MAPPED, Properties.sctid.toString()))
	    				
				.addKey(mgmt.getPropertyKey(Properties.effectiveTime.toString()), 
				Parameter.of(MAPPED, Properties.effectiveTime.toString()))
				
				.addKey(mgmt.getPropertyKey(Properties.status.toString()), 
				Parameter.of(MAPPED, Properties.status.toString()))
				
				.addKey(mgmt.getPropertyKey(Properties.moduleId.toString()), 
				Parameter.of(MAPPED, Properties.moduleId.toString()))
				
				.addKey(mgmt.getPropertyKey(Properties.group.toString()), 
				Parameter.of(MAPPED, Properties.group.toString()))
				
				.addKey(mgmt.getPropertyKey(Properties.typeId.toString()), 
				Parameter.of(MAPPED, Properties.typeId.toString()))
				
				.addKey(mgmt.getPropertyKey(Properties.characteristicId.toString()), 
				Parameter.of(MAPPED, Properties.characteristicId.toString()))
				
				.addKey(mgmt.getPropertyKey(Properties.modifierId.toString()), 
				Parameter.of(MAPPED, Properties.modifierId.toString()))
				
				.addKey(mgmt.getPropertyKey(Properties.created.toString()), 
				Parameter.of(MAPPED, Properties.created.toString()))
				
				.addKey(mgmt.getPropertyKey(Properties.createdBy.toString()), 
				Parameter.of(MAPPED, Properties.createdBy.toString()))
				
				.addKey(mgmt.getPropertyKey(Properties.start.toString()), 
				Parameter.of(MAPPED, Properties.start.toString()))
				
				.addKey(mgmt.getPropertyKey(Properties.end.toString()), 
				Parameter.of(MAPPED, Properties.end.toString()))
				
	    		.addKey(mgmt.getPropertyKey(Properties.type.toString()),
	    				Parameter.of(MAPPED, Properties.type.toString()))
	    				
	    				
	    		.buildMixedIndex(backingIndex);
		    }
	    	
	    	//history vertex
	    	
	    	String hDescription = Types.hDescription.toString(); 
	    	if(!mgmt.containsGraphIndex(hDescription)) {
		    	
				LOGGER.debug("Creating index on vertex {}" , hDescription);

				mgmt.buildIndex(hDescription, Vertex.class)
	    		.indexOnly(mgmt.getVertexLabel(hDescription))
	    		.addKey(mgmt.getPropertyKey(Properties.sctid.toString()),
	    				Parameter.of(MAPPED, Properties.sctid.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.effectiveTime.toString()),
	    				Parameter.of(MAPPED, Properties.effectiveTime.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.title.toString()),
	    				Parameter.of(MAPPED, Properties.title.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.status.toString()),
	    				Parameter.of(MAPPED, Properties.status.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.languageCode.toString()),
	    				Parameter.of(MAPPED, Properties.languageCode.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.type.toString()),
	    				Parameter.of(MAPPED, Properties.type.toString()))
				.addKey(mgmt.getPropertyKey(Properties.moduleId.toString()),
		    				Parameter.of(MAPPED, Properties.moduleId.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.caseSignificanceId.toString()),
		    				Parameter.of(MAPPED, Properties.caseSignificanceId.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.typeId.toString()),
		    				Parameter.of(MAPPED, Properties.typeId.toString()))
				.addKey(mgmt.getPropertyKey(Properties.created.toString()), 
				Parameter.of(MAPPED, Properties.created.toString()))
				
				.addKey(mgmt.getPropertyKey(Properties.createdBy.toString()), 
				Parameter.of(MAPPED, Properties.createdBy.toString()))
		    				
		    				
		    	.buildMixedIndex(backingIndex);
		    	
		    }
	    	
	    	String hConcept = Types.hConcept.toString(); 
	    	if(!mgmt.containsGraphIndex(hConcept)) {
		    	
				LOGGER.debug("Creating index on vertex {}" , hConcept);

		    	mgmt.buildIndex(hConcept, Vertex.class)
		    		.indexOnly(mgmt.getVertexLabel(hConcept))
		    		.addKey(mgmt.getPropertyKey(Properties.sctid.toString()),
		    				Parameter.of(MAPPED, Properties.sctid.toString()))
		    		.addKey(mgmt.getPropertyKey(Properties.title.toString()),
		    				Parameter.of(MAPPED, Properties.title.toString()))
		    		.addKey(mgmt.getPropertyKey(Properties.status.toString()),
		    				Parameter.of(MAPPED, Properties.status.toString()))
		    		.addKey(mgmt.getPropertyKey(Properties.effectiveTime.toString()),
		    				Parameter.of(MAPPED, Properties.effectiveTime.toString()))
		    		.addKey(mgmt.getPropertyKey(Properties.type.toString()),
		    				Parameter.of(MAPPED, Properties.type.toString()))
    				.addKey(mgmt.getPropertyKey(Properties.moduleId.toString()),
		    				Parameter.of(MAPPED, Properties.moduleId.toString()))
		    		.addKey(mgmt.getPropertyKey(Properties.definitionStatusId.toString()),
		    				Parameter.of(MAPPED, Properties.definitionStatusId.toString()))
					.addKey(mgmt.getPropertyKey(Properties.created.toString()), 
					Parameter.of(MAPPED, Properties.created.toString()))
					
					.addKey(mgmt.getPropertyKey(Properties.createdBy.toString()), 
					Parameter.of(MAPPED, Properties.createdBy.toString()))
		    				
		    				
		    		.buildMixedIndex(backingIndex);
		    	
		    }
	    	
	    	String hRelationship = Types.hRelationship.toString(); 
	    	if(!mgmt.containsGraphIndex(hRelationship)) {
		    	
				LOGGER.debug("Creating index on vertex {}" , hRelationship);

				mgmt.buildIndex(hRelationship, Edge.class)
	    		.indexOnly(mgmt.getEdgeLabel(hRelationship))
	    		.addKey(mgmt.getPropertyKey(Properties.sctid.toString()), 
	    				Parameter.of(MAPPED, Properties.sctid.toString()))
	    				
				.addKey(mgmt.getPropertyKey(Properties.effectiveTime.toString()), 
				Parameter.of(MAPPED, Properties.effectiveTime.toString()))
				
				.addKey(mgmt.getPropertyKey(Properties.status.toString()), 
				Parameter.of(MAPPED, Properties.status.toString()))
				
				.addKey(mgmt.getPropertyKey(Properties.moduleId.toString()), 
				Parameter.of(MAPPED, Properties.moduleId.toString()))
				
				.addKey(mgmt.getPropertyKey(Properties.group.toString()), 
				Parameter.of(MAPPED, Properties.group.toString()))
				
				.addKey(mgmt.getPropertyKey(Properties.typeId.toString()), 
				Parameter.of(MAPPED, Properties.typeId.toString()))
				
				.addKey(mgmt.getPropertyKey(Properties.characteristicId.toString()), 
				Parameter.of(MAPPED, Properties.characteristicId.toString()))
				
				.addKey(mgmt.getPropertyKey(Properties.modifierId.toString()), 
				Parameter.of(MAPPED, Properties.modifierId.toString()))
				
				.addKey(mgmt.getPropertyKey(Properties.created.toString()), 
				Parameter.of(MAPPED, Properties.created.toString()))
				
				.addKey(mgmt.getPropertyKey(Properties.createdBy.toString()), 
				Parameter.of(MAPPED, Properties.createdBy.toString()))
				
				.addKey(mgmt.getPropertyKey(Properties.start.toString()), 
				Parameter.of(MAPPED, Properties.start.toString()))
				
				.addKey(mgmt.getPropertyKey(Properties.end.toString()), 
				Parameter.of(MAPPED, Properties.end.toString()))
				
	    		.addKey(mgmt.getPropertyKey(Properties.type.toString()),
	    				Parameter.of(MAPPED, Properties.type.toString()))
	    		.buildMixedIndex(backingIndex);
		    }

			LOGGER.debug("commiting index created");

	    	mgmt.commit();
	    	
		} catch (Exception e) {
			
			LOGGER.error("Management Transaction Rolledback");

			e.printStackTrace();
			mgmt.rollback();
			// TODO: handle exception
			throw new RuntimeException(e);

		} finally {
			
			g.shutdown();
		}
	}
	
	public void printSchema() {
		

		
	    LOGGER.info("Schema has following relations");
	    LOGGER.info("==============================");
		
		TitanGraph g = openGraph(config);
	    //management api
	    TitanManagement mgmt = g.getManagementSystem();
	    
	    try {
			
		    Iterable<RelationType> rts = mgmt.getRelationTypes(RelationType.class);
		    
		    for (RelationType rt : rts) {
				
		    	LOGGER.info("Relation Name {}", rt.getName());
		    	
			}
		    Iterable<VertexLabel> vLs = mgmt.getVertexLabels();
		    for (VertexLabel vL : vLs) {
				
		    	LOGGER.info("Vertex Label {}", vL.getName());
		    	
			}
		    LOGGER.info("==============================");
		    
		    mgmt.commit();

		} catch (Exception e) {
			
			LOGGER.error("Management Transaction Rolledback");

			e.printStackTrace();
			mgmt.rollback();
			// TODO: handle exception
			throw new RuntimeException(e);

		} finally {
			
			g.shutdown();
		}
	    
	    
	    
	
	}
	
	public void printIndexes() {

		

		
	    LOGGER.info("Schema has following indexes");
	    LOGGER.info("==============================");
		
		TitanGraph g = openGraph(config);
	    //management api
	    TitanManagement mgmt = g.getManagementSystem();
	    
	    try {
			
		    Iterable<TitanGraphIndex> vis = mgmt.getGraphIndexes(Vertex.class);
		    
		    for (TitanGraphIndex gi : vis) {
				
		    	LOGGER.info("Vertex Index  {}", gi.getName());
		    	
			}
		    Iterable<TitanGraphIndex> eis = mgmt.getGraphIndexes(Edge.class);
		    for (TitanGraphIndex gi : eis) {
				
		    	LOGGER.info("Edge Index  {}", gi.getName());
		    	
			}
		    LOGGER.info("==============================");
		    
		    mgmt.commit();

		} catch (Exception e) {
			
			LOGGER.error("Management Transaction Rolledback");

			e.printStackTrace();
			mgmt.rollback();
			// TODO: handle exception
			throw new RuntimeException(e);

		} finally {
			
			g.shutdown();
		}
	    
	}

}
