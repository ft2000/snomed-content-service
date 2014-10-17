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
import com.thinkaurelius.titan.core.schema.Mapping;
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

	private static final String SEARCH = "search";
	
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
			if(!mgmt.containsVertexLabel(relationship)) {
				
				LOGGER.debug("Creating vertex label {}" , relationship);
			
				mgmt.makeVertexLabel(relationship).make();

			}
			
			String module = Types.module.toString();
			if(!mgmt.containsVertexLabel(module)) {
				
				LOGGER.debug("Creating vertex label {}" , module);
			
				mgmt.makeVertexLabel(module).make();

			}
			

			String definition = Types.definition.toString();
			if(!mgmt.containsVertexLabel(definition)) {
				
				LOGGER.debug("Creating vertex label {}" , definition);
			
				mgmt.makeVertexLabel(definition).make();

			}
			
			String caseSensitive = Types.caseSensitive.toString();
			
			if(!mgmt.containsRelationType(caseSensitive)) {
				
				LOGGER.debug("Creating vertex label {}" , caseSensitive);
			
				mgmt.makeVertexLabel(caseSensitive).make();
			
			}
			
			String type = Types.type.toString();
			
			if(!mgmt.containsRelationType(type)) {
				
				LOGGER.debug("Creating vertex label {}" , type);
			
				mgmt.makeVertexLabel(type).make();
			
			}
			
			String modifier = Types.modifier.toString();
			if(!mgmt.containsRelationType(modifier)) {
				
				LOGGER.debug("Creating vertex label {}" , modifier);
			
				mgmt.makeVertexLabel(modifier).make();
			
			}
			
			String fsn = DescriptionType.fsn.toString();
			if(!mgmt.containsRelationType(fsn)) {
				
				LOGGER.debug("Creating edge label {}" , fsn);
			
				mgmt.makeEdgeLabel(fsn).make();

			}
			String hasModule = Relationship.hasModule.toString();
			
			if(!mgmt.containsRelationType(hasModule)) {
						
				LOGGER.debug("Creating edge label {}" , hasModule);
			
				mgmt.makeEdgeLabel(hasModule).make();
			
			}
			
			String hasCaseSignificance = Relationship.hasCaseSignificance.toString();
			if(!mgmt.containsRelationType(hasCaseSignificance)) {
				
				LOGGER.debug("Creating edge label {}" , hasCaseSignificance);
			
				mgmt.makeEdgeLabel(hasCaseSignificance).make();
			
			}
			
			String hasType = Relationship.hasType.toString();
			if(!mgmt.containsRelationType(hasType)) {
				
				LOGGER.debug("Creating edge label {}" , hasType);
			
				mgmt.makeEdgeLabel(hasType).make();
			
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
			
			String synonym = DescriptionType.synonym.toString();
			if(!mgmt.containsRelationType(synonym)) {
				
				LOGGER.debug("Creating edge label {}" , synonym);
			
				mgmt.makeEdgeLabel(synonym).make();
			
			}
			
			

			
			String hasModifier = Relationship.hasModifier.toString();
			if(!mgmt.containsRelationType(hasModifier)) {
				
				LOGGER.debug("Creating edge label {}" , hasModifier);
			
				mgmt.makeEdgeLabel(hasModifier).make();
			
			}
			
			String statd = Relationship.stated.toString();
			if(!mgmt.containsRelationType(statd)) {
				
				LOGGER.debug("Creating edge label {}" , statd);
			
				mgmt.makeEdgeLabel(statd).make();
			
			}
			
			String ifr = Relationship.inferred.toString();
			if(!mgmt.containsRelationType(ifr)) {
				
				LOGGER.debug("Creating edge label {}" , ifr);
			
				mgmt.makeEdgeLabel(ifr).make();
			
			}
			
			String qualifying = Relationship.qualifying.toString();
			if(!mgmt.containsRelationType(qualifying)) {
				
				LOGGER.debug("Creating edge label {}" , qualifying);
			
				mgmt.makeEdgeLabel(qualifying).make();
			
			}
			
			String additional = Relationship.additional.toString();
			if(!mgmt.containsRelationType(additional)) {
				
				LOGGER.debug("Creating edge label {}" , additional);
			
				mgmt.makeEdgeLabel(additional).make();
			
			}
			
			//titan doc advise that property key index creation should be done as part same transaction in which these keys are being created

	    	if(!mgmt.containsGraphIndex(CompositeIndex.bySctId.toString())) {
		    	
				LOGGER.debug("Creating index on key {}" , CompositeIndex.bySctId.toString());

		    	mgmt.buildIndex(CompositeIndex.bySctId.toString(), Vertex.class)
		    	.addKey(mgmt.getPropertyKey(sctid))
		    	.buildCompositeIndex();
		    	
		    }
	    	
	    	if(!mgmt.containsGraphIndex(Properties.sctid.toString())) {
		    	
				LOGGER.debug("Creating index on key {}" , Properties.sctid.toString());

		    	mgmt.buildIndex(Properties.sctid.toString(), Vertex.class)
		    	.addKey(mgmt.getPropertyKey(sctid))
		    	.unique()
		    	.buildCompositeIndex();
		    	
		    }
	    	
	    	if(!mgmt.containsGraphIndex(CompositeIndex.conceptBySctId.toString())) {
		    	
				LOGGER.debug("Creating index on key {}" , CompositeIndex.conceptBySctId.toString());

		    	mgmt.buildIndex(CompositeIndex.conceptBySctId.toString(), Vertex.class)
		    	.addKey(mgmt.getPropertyKey(sctid))
		    	.indexOnly(mgmt.getVertexLabel(Types.concept.toString())).buildCompositeIndex();
		    	
		    }
	    	
	    	if(!mgmt.containsGraphIndex(title)) {
		    	
				LOGGER.debug("Creating index on key {}" , title);

		    	mgmt.buildIndex(title, Vertex.class).addKey(mgmt.getPropertyKey(title)).buildCompositeIndex();
		    	
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


	public void createIndex() {
		
		LOGGER.debug("Creating Snomed Index");
		
		TitanGraph g = openGraph(config);
	    //management api
	    TitanManagement mgmt = g.getManagementSystem();
	    
	    try {

	    	
	    	
			String typeId = Properties.typeId.toString();
			if(!mgmt.containsGraphIndex(typeId)) {
		    	
				LOGGER.debug("Creating index on key {}" , typeId);

		    	mgmt.buildIndex(typeId, Edge.class).addKey(mgmt.getPropertyKey(typeId)).buildMixedIndex(SEARCH);
		    	
		    }
	    		    	
	    	String fsn = DescriptionType.fsn.toString(); 
	    	if(!mgmt.containsGraphIndex(fsn)) {
		    	
				LOGGER.debug("Creating index on edge {}" , fsn);

		    	mgmt.buildEdgeIndex(mgmt.getEdgeLabel(fsn), fsn, Direction.BOTH, Order.DESC, mgmt.getPropertyKey(Properties.title.toString()));

		    }
	    	
	    	String inferred = Relationship.inferred.toString(); 
	    	if(!mgmt.containsGraphIndex(inferred)) {
		    	
				LOGGER.debug("Creating index on edge {}" , inferred);

				mgmt.buildIndex(inferred, Edge.class)
	    		.indexOnly(mgmt.getRelationType(inferred))
	    		.addKey(mgmt.getPropertyKey(Properties.sctid.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.typeId.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.status.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.effectiveTime.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.group.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.created.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.createdBy.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.modifiedDate.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.modifiedBy.toString()))

	    		.buildMixedIndex(SEARCH);

		    	
		    }
	    	
	    	String stated = Relationship.stated.toString(); 
	    	if(!mgmt.containsGraphIndex(stated)) {
		    	
				LOGGER.debug("Creating index on edge {}" , stated);

				mgmt.buildIndex(stated, Edge.class)
	    		.indexOnly(mgmt.getRelationType(stated))
	    		.addKey(mgmt.getPropertyKey(Properties.sctid.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.typeId.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.status.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.effectiveTime.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.group.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.created.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.createdBy.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.modifiedDate.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.modifiedBy.toString()))

	    		.buildMixedIndex(SEARCH);		    	
		    }
	    	
	    	String additional = Relationship.additional.toString(); 
	    	if(!mgmt.containsGraphIndex(additional)) {
		    	
				LOGGER.debug("Creating index on edge {}" , additional);

				mgmt.buildIndex(additional, Edge.class)
	    		.indexOnly(mgmt.getRelationType(additional))
	    		.addKey(mgmt.getPropertyKey(Properties.sctid.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.typeId.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.status.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.effectiveTime.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.group.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.created.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.createdBy.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.modifiedDate.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.modifiedBy.toString()))

	    		.buildMixedIndex(SEARCH);		    	
		    	
		    }
	    	
	    	String qualifying = Relationship.qualifying.toString(); 
	    	if(!mgmt.containsGraphIndex(qualifying)) {
		    	
				LOGGER.debug("Creating index on edge {}" , qualifying);

				mgmt.buildIndex(qualifying, Edge.class)
	    		.indexOnly(mgmt.getRelationType(qualifying))
	    		.addKey(mgmt.getPropertyKey(Properties.sctid.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.typeId.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.status.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.effectiveTime.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.group.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.created.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.createdBy.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.modifiedDate.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.modifiedBy.toString()))

	    		.buildMixedIndex(SEARCH);		    	
		    	
		    }
	    	
	    	String synonym = DescriptionType.synonym.toString(); 
	    	if(!mgmt.containsGraphIndex(synonym)) {
		    	
				LOGGER.debug("Creating index on edge {}" , synonym);

		    	mgmt.buildEdgeIndex(mgmt.getEdgeLabel(synonym), synonym, Direction.BOTH, Order.DESC, mgmt.getPropertyKey(Properties.title.toString()));
		    	
		    }
	    	
	    	String description = Types.description.toString(); 
	    	if(!mgmt.containsGraphIndex(description)) {
		    	
				LOGGER.debug("Creating index on vertex {}" , description);

				mgmt.buildIndex(description, Vertex.class)
	    		.indexOnly(mgmt.getVertexLabel(description))
	    		.addKey(mgmt.getPropertyKey(Properties.sctid.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.effectiveTime.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.title.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.status.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.languageCode.toString()))

	    		.buildMixedIndex(SEARCH);
		    	
		    }
	    	
	    	String concept = Types.concept.toString(); 
	    	if(!mgmt.containsGraphIndex(concept)) {
		    	
				LOGGER.debug("Creating index on vertex {}" , concept);

		    	mgmt.buildIndex(concept, Vertex.class)
		    		.indexOnly(mgmt.getVertexLabel(concept))
		    		.addKey(mgmt.getPropertyKey(Properties.sctid.toString()))
		    		.addKey(mgmt.getPropertyKey(Properties.title.toString()))
		    		.addKey(mgmt.getPropertyKey(Properties.status.toString()))
		    		.addKey(mgmt.getPropertyKey(Properties.effectiveTime.toString()))

		    		.buildMixedIndex(SEARCH);
		    	
		    }
	    	
	    	String relationship = Types.relationship.toString(); 
	    	if(!mgmt.containsGraphIndex(relationship)) {
		    	
				LOGGER.debug("Creating index on vertex {}" , relationship);

				mgmt.buildIndex(relationship, Vertex.class)
	    		.indexOnly(mgmt.getVertexLabel(relationship))
	    		.addKey(mgmt.getPropertyKey(Properties.sctid.toString()))
	    		.buildMixedIndex(SEARCH);
		    }

	    	//generic index
	    	if(!mgmt.containsGraphIndex("bySctIdStatus")) {
			    	
				LOGGER.debug("Creating index on vertex {}" , "bySctIdStatus");

				mgmt.buildIndex("bySctIdStatus", Vertex.class)
	    		.addKey(mgmt.getPropertyKey(Properties.sctid.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.status.toString()))
	    		.buildCompositeIndex();
		    }
	    	String bySctIdTimeStatus = MixedIndex.bySctIdTimeStatus.toString();
	    	if(!mgmt.containsGraphIndex(bySctIdTimeStatus)) {
		    	
				LOGGER.debug("Creating index on vertex {}" , bySctIdTimeStatus);

				mgmt.buildIndex(bySctIdTimeStatus, Vertex.class)
	    		.addKey(mgmt.getPropertyKey(Properties.sctid.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.status.toString()))
	    		.addKey(mgmt.getPropertyKey(Properties.effectiveTime.toString()), Parameter.of("effectiveTime", Mapping.STRING))
	    		.buildMixedIndex(SEARCH);
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
