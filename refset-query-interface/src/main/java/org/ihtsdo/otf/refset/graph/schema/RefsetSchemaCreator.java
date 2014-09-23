package org.ihtsdo.otf.refset.graph.schema;

import org.apache.commons.configuration.Configuration;
import org.ihtsdo.otf.refset.graph.RGC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thinkaurelius.titan.core.PropertyKey;
import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;
import com.thinkaurelius.titan.core.VertexLabel;
import com.thinkaurelius.titan.core.schema.Mapping;
import com.thinkaurelius.titan.core.schema.Parameter;
import com.thinkaurelius.titan.core.schema.TitanGraphIndex;
import com.thinkaurelius.titan.core.schema.TitanManagement;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;

public class RefsetSchemaCreator {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RefsetSchemaCreator.class);

	private TitanGraph g;
	

	private Configuration config;
	
	/**
	 * @param config the config to set
	 */
	public void setConfig(Configuration config) {
		this.config = config;
	}


	public void init() {
		
		g = TitanFactory.open(config);
	}
	
	
	public void createRefsetSchema() {
	
		LOGGER.debug("Creating Refset schema");
		
		init();
		TitanManagement mgmt = g.getManagementSystem();

		try {
			
		
				
				LOGGER.debug("Making members edge label");
				
				mgmt.makeEdgeLabel("members");
		
				if (!mgmt.containsRelationType(RGC.ACTIVE)) {
		
					mgmt.makePropertyKey(RGC.ACTIVE).dataType(Boolean.class).make();
		
				} else {
					
					LOGGER.debug("Not Creating Property {} already exist", RGC.ACTIVE);
		
				}
				
				LOGGER.debug("Creating Index  {} for property {}" , "PublishedGRefset" , RGC.PUBLISHED);
				
				/*mgmt.buildIndex("PublishedGRefset", Vertex.class).addKey(mgmt
						.makePropertyKey(RGC.PUBLISHED).dataType(Boolean.class).make(), 
						com.thinkaurelius.titan.core.schema.Parameter.of("mapped-name","published")).buildMixedIndex("search");*/

				
				mgmt.buildIndex("PublishedGRefset", Vertex.class).addKey(mgmt
						.makePropertyKey(RGC.PUBLISHED).dataType(Boolean.class).make()).buildCompositeIndex();
				
				
				if (!mgmt.containsRelationType(RGC.PUBLISHED_DATE)) {
					
					mgmt.makePropertyKey(RGC.PUBLISHED_DATE).dataType(Long.class).make();
					
				} else {
					
					LOGGER.debug("Not Creating Property {} already exist", RGC.PUBLISHED_DATE);
		
				}
				
				
				if (!mgmt.containsRelationType(RGC.CREATED))  {
		
					mgmt.makePropertyKey(RGC.CREATED).dataType(Long.class).make();
					
				} else {
					
					LOGGER.debug("Not Creating Property {} already exist", RGC.CREATED);
		
				}
				
				if (!mgmt.containsRelationType(RGC.CREATED_BY)) {
		
					mgmt.makePropertyKey(RGC.CREATED_BY).dataType(String.class).make();
					
				} else {
					
					LOGGER.debug("Not Creating Property {} already exist", RGC.CREATED_BY);
		
				}
				
				if (!mgmt.containsRelationType(RGC.DESC)) {
		
					mgmt.makePropertyKey(RGC.DESC).dataType(String.class).make();
				} else {
					
					LOGGER.debug("Not Creating Property {} already exist", RGC.DESC);
				
				}
				
				if (!mgmt.containsRelationType(RGC.EFFECTIVE_DATE)) {
		
					mgmt.makePropertyKey(RGC.EFFECTIVE_DATE).dataType(Long.class).make();
					
				} else {
					
					LOGGER.debug("Not Creating Property {} already exist", RGC.EFFECTIVE_DATE);
				
				}

				PropertyKey id = null;
				
				if ( !mgmt.containsRelationType(RGC.ID)) {
		
					id = mgmt.makePropertyKey(RGC.ID).dataType(String.class).make();
					
				} else {
					
					LOGGER.debug("Not Creating Property {} already exist", "id");
		
				}
				
				LOGGER.debug("Creating Index  {} for property {}" , "UniqueSid" , RGC.ID);
				
				
				
				/*mgmt.buildIndex("UniqueSid", Vertex.class).addKey(mgmt.makePropertyKey(RGC.ID).dataType(String.class).make(), 
						com.thinkaurelius.titan.core.schema.Parameter.of("mapped-name","sid")).buildMixedIndex("search");*/
				mgmt.buildIndex("UniqueSid", Vertex.class).addKey(mgmt.getPropertyKey(RGC.ID)).unique().buildCompositeIndex();
				
				
							

				
				if (!mgmt.containsRelationType(RGC.LANG_CODE)) {
		
					mgmt.makePropertyKey(RGC.LANG_CODE).dataType(String.class).make();
				} else {
					
					LOGGER.debug("Not Creating Property {} already exist", RGC.LANG_CODE);
				
				}
				
				if (!mgmt.containsRelationType(RGC.MODULE_ID)) {
					
					mgmt.makePropertyKey(RGC.MODULE_ID).dataType(String.class).make();
		
				} else {
					
					LOGGER.debug("Not Creating Property {} already exist", RGC.MODULE_ID);
				
				}

				
				
				
				LOGGER.debug("Creating Index  {} for property {}" , "MemberRefComponentId" , RGC.REFERENCE_COMPONENT_ID);
				
				
				/*mgmt.buildIndex("MemberRefComponentId", Vertex.class).addKey(mgmt.makePropertyKey(RGC.REFERENCE_COMPONENT_ID)
						.dataType(String.class).make(), com.thinkaurelius.titan.core.schema.Parameter.of("mapped-name","sid")
						).buildMixedIndex("search");*/
				mgmt.buildIndex("MemberRefComponentId", Vertex.class).addKey(mgmt.makePropertyKey(RGC.REFERENCE_COMPONENT_ID)
						.dataType(String.class).make()).unique().buildCompositeIndex();
				
				if (!mgmt.containsRelationType(RGC.SUPER_REFSET_TYPE_ID)) {
					
					
					mgmt.makePropertyKey(RGC.SUPER_REFSET_TYPE_ID).dataType(String.class).make();
		
				} else {
					
					LOGGER.debug("Not Creating Property {} already exist", RGC.SUPER_REFSET_TYPE_ID);
				
				}
				
				if (!mgmt.containsRelationType(RGC.TYPE)) {
					
					mgmt.makePropertyKey(RGC.TYPE).dataType(String.class).make();
		
				} else {
					
					LOGGER.debug("Not Creating Property {} already exist", RGC.TYPE);
				
				}
				
				if (!mgmt.containsRelationType(RGC.TYPE_ID)) {
		
					mgmt.makePropertyKey(RGC.TYPE_ID).dataType(String.class).make();
					
				} else {
					
					LOGGER.debug("Not Creating Property {} already exist", RGC.TYPE_ID);
				
				}
		
		
		
				
				
				//create desired index
				
				LOGGER.debug("Creating Vertex Label {}" , "GRefset");
				mgmt.makeVertexLabel("GRefset");
				
		
				VertexLabel refset = mgmt.getVertexLabel("GRefset");
				TitanGraphIndex gi = mgmt.getGraphIndex("Refset");
				
				LOGGER.debug("Creating Index  {}" , "Refset");
		
				if (gi == null) {
				
					LOGGER.debug("Creating Index  {}" , "Refset");
		
					
					//mgmt.buildIndex("Refset", Vertex.class).indexOnly(refset).buildMixedIndex("search");

		
				}
				mgmt.buildIndex("Refset", Vertex.class)
				.addKey(mgmt.getPropertyKey(RGC.ID))
				.addKey(mgmt.getPropertyKey(RGC.CREATED))
				.addKey(mgmt.getPropertyKey(RGC.EFFECTIVE_DATE))
				.addKey(mgmt.getPropertyKey(RGC.CREATED_BY))
				.addKey(mgmt.getPropertyKey(RGC.PUBLISHED_DATE)).indexOnly(refset).buildCompositeIndex();
				
				if (!mgmt.containsVertexLabel("GMember")) {
					
					mgmt.makeVertexLabel("GMember");

				}
				
		
				VertexLabel member = mgmt.getVertexLabel("GMember");
				
				gi = mgmt.getGraphIndex("Member");
		
				if (gi == null) {
					LOGGER.debug("Creating Index  {}" , "Member");
		
					//mgmt.buildIndex("Member", Vertex.class).indexOnly(member).buildMixedIndex("search");
					
					mgmt.buildIndex("Member", Vertex.class)
					.addKey(mgmt.getPropertyKey(RGC.ID))
					.addKey(mgmt.getPropertyKey(RGC.REFERENCE_COMPONENT_ID))
					.indexOnly(member).buildCompositeIndex();

		
				}
				
				mgmt.buildIndex("EdgeRefComponentId", Edge.class).addKey(mgmt.getPropertyKey(RGC.REFERENCE_COMPONENT_ID)).buildCompositeIndex();
				mgmt.buildIndex("EdgeEffectiveDate", Edge.class).addKey(mgmt.getPropertyKey(RGC.EFFECTIVE_DATE)).buildCompositeIndex();
				mgmt.buildIndex("EdgeStatus", Edge.class).addKey(mgmt.getPropertyKey(RGC.ACTIVE)).buildCompositeIndex();
				mgmt.buildIndex("EdgePublished", Edge.class).addKey(mgmt.getPropertyKey(RGC.PUBLISHED)).buildCompositeIndex();
				
				
				LOGGER.debug("commiting Index & schema  ");

				mgmt.commit();
				g.commit();
		} catch(Exception e) {
			
			e.printStackTrace();

			mgmt.rollback();
			g.rollback();
			
			
		} finally {
			
			g.shutdown();

		}
		
		
	}
	

	

	
	

}
