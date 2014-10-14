package org.ihtsdo.otf.refset.schema;
import static org.ihtsdo.otf.refset.domain.RGC.ACTIVE;
import static org.ihtsdo.otf.refset.domain.RGC.CREATED;
import static org.ihtsdo.otf.refset.domain.RGC.CREATED_BY;
import static org.ihtsdo.otf.refset.domain.RGC.DESC;
import static org.ihtsdo.otf.refset.domain.RGC.EFFECTIVE_DATE;
import static org.ihtsdo.otf.refset.domain.RGC.ID;
import static org.ihtsdo.otf.refset.domain.RGC.LANG_CODE;
import static org.ihtsdo.otf.refset.domain.RGC.MODIFIED_BY;
import static org.ihtsdo.otf.refset.domain.RGC.MODIFIED_DATE;
import static org.ihtsdo.otf.refset.domain.RGC.MODULE_ID;
import static org.ihtsdo.otf.refset.domain.RGC.PUBLISHED;
import static org.ihtsdo.otf.refset.domain.RGC.PUBLISHED_DATE;
import static org.ihtsdo.otf.refset.domain.RGC.REFERENCE_COMPONENT_ID;
import static org.ihtsdo.otf.refset.domain.RGC.SUPER_REFSET_TYPE_ID;
import static org.ihtsdo.otf.refset.domain.RGC.TYPE;
import static org.ihtsdo.otf.refset.domain.RGC.TYPE_ID;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thinkaurelius.titan.core.PropertyKey;
import com.thinkaurelius.titan.core.RelationType;
import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;
import com.thinkaurelius.titan.core.VertexLabel;
import com.thinkaurelius.titan.core.schema.TitanGraphIndex;
import com.thinkaurelius.titan.core.schema.TitanManagement;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;

public class RefsetSchema {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RefsetSchema.class);

	private String config;

	/**
	 * 
	 */
	public RefsetSchema(String config) {
		// TODO Auto-generated constructor stub
		
	    this.config = config;
	    if (StringUtils.isBlank(config)) {
			
	    	throw new IllegalArgumentException("No graph configuration provided to conncet graph db");
		}

	}
	
	/**
	 * @param config
	 * @return
	 */
	private TitanGraph openGraph(String config) {


		TitanGraph g = TitanFactory.open(config);
		
		if (g == null) {
			
	    	throw new IllegalArgumentException("Could not initialize graph database. Check db connection configurations");
		}
		
		return g;
	}
	
	public void createSchema() {
	
		LOGGER.debug("Creating Refset schema");
		
		TitanGraph g = openGraph(config);
		
		TitanManagement mgmt = g.getManagementSystem();

		try {
			
		
				
				LOGGER.debug("Making members edge label");
				
				mgmt.makeEdgeLabel(RefsetRelations.members.toString());
		
				if (!mgmt.containsRelationType(ACTIVE)) {
		
					mgmt.makePropertyKey(ACTIVE).dataType(Boolean.class).make();
		
				} else {
					
					LOGGER.debug("Not Creating Property {} already exist", ACTIVE);
		
				}
				
				LOGGER.debug("Creating Index  {} for property {}" , "PublishedGRefset" , PUBLISHED);
				
				/*mgmt.buildIndex("PublishedGRefset", Vertex.class).addKey(mgmt
						.makePropertyKey(PUBLISHED).dataType(Boolean.class).make(), 
						com.thinkaurelius.titan.core.schema.Parameter.of("mapped-name","published")).buildMixedIndex("search");*/

				
				mgmt.buildIndex("PublishedGRefset", Vertex.class).addKey(mgmt
						.makePropertyKey(PUBLISHED).dataType(Boolean.class).make()).buildCompositeIndex();
				
				
				if (!mgmt.containsRelationType(PUBLISHED_DATE)) {
					
					mgmt.makePropertyKey(PUBLISHED_DATE).dataType(Long.class).make();
					
				} else {
					
					LOGGER.debug("Not Creating Property {} already exist", PUBLISHED_DATE);
		
				}
				
				
				if (!mgmt.containsRelationType(CREATED))  {
		
					mgmt.makePropertyKey(CREATED).dataType(Long.class).make();
					
				} else {
					
					LOGGER.debug("Not Creating Property {} already exist", CREATED);
		
				}
				
				if (!mgmt.containsRelationType(CREATED_BY)) {
		
					mgmt.makePropertyKey(CREATED_BY).dataType(String.class).make();
					
				} else {
					
					LOGGER.debug("Not Creating Property {} already exist", CREATED_BY);
		
				}
				
				if (!mgmt.containsRelationType(MODIFIED_DATE))  {
					
					mgmt.makePropertyKey(MODIFIED_DATE).dataType(Long.class).make();
					
				} else {
					
					LOGGER.debug("Not Creating Property {} already exist", MODIFIED_DATE);
		
				}
				
				if (!mgmt.containsRelationType(MODIFIED_BY)) {
		
					mgmt.makePropertyKey(MODIFIED_BY).dataType(String.class).make();
					
				} else {
					
					LOGGER.debug("Not Creating Property {} already exist", CREATED_BY);
		
				}
				
				if (!mgmt.containsRelationType(DESC)) {
		
					mgmt.makePropertyKey(DESC).dataType(String.class).make();
				} else {
					
					LOGGER.debug("Not Creating Property {} already exist", DESC);
				
				}
				
				if (!mgmt.containsRelationType(EFFECTIVE_DATE)) {
		
					mgmt.makePropertyKey(EFFECTIVE_DATE).dataType(Long.class).make();
					
				} else {
					
					LOGGER.debug("Not Creating Property {} already exist", EFFECTIVE_DATE);
				
				}
				
				if ( !mgmt.containsRelationType(ID)) {
		
					mgmt.makePropertyKey(ID).dataType(String.class).make();
					
				} else {
					
					LOGGER.debug("Not Creating Property {} already exist", "id");
		
				}
				
				LOGGER.debug("Creating Index  {} for property {}" , "UniqueSid" , ID);
				
				
				
				/*mgmt.buildIndex("UniqueSid", Vertex.class).addKey(mgmt.makePropertyKey(ID).dataType(String.class).make(), 
						com.thinkaurelius.titan.core.schema.Parameter.of("mapped-name","sid")).buildMixedIndex("search");*/
				mgmt.buildIndex("UniqueSid", Vertex.class).addKey(mgmt.getPropertyKey(ID)).unique().buildCompositeIndex();
				
				
							

				
				if (!mgmt.containsRelationType(LANG_CODE)) {
		
					mgmt.makePropertyKey(LANG_CODE).dataType(String.class).make();
					
				} else {
					
					LOGGER.debug("Not Creating Property {} already exist", LANG_CODE);
				
				}
				
				if (!mgmt.containsRelationType(MODULE_ID)) {
					
					mgmt.makePropertyKey(MODULE_ID).dataType(String.class).make();
		
				} else {
					
					LOGGER.debug("Not Creating Property {} already exist", MODULE_ID);
				
				}

				
				
				
				LOGGER.debug("Creating Index  {} for property {}" , "MemberRefComponentId" , REFERENCE_COMPONENT_ID);
				
				
				/*mgmt.buildIndex("MemberRefComponentId", Vertex.class).addKey(mgmt.makePropertyKey(REFERENCE_COMPONENT_ID)
						.dataType(String.class).make(), com.thinkaurelius.titan.core.schema.Parameter.of("mapped-name","sid")
						).buildMixedIndex("search");*/
				
				mgmt.buildIndex("MemberRefComponentId", Vertex.class).addKey(mgmt.makePropertyKey(REFERENCE_COMPONENT_ID)
						.dataType(String.class).make()).buildCompositeIndex();
				
				if (!mgmt.containsRelationType(SUPER_REFSET_TYPE_ID)) {
					
					
					mgmt.makePropertyKey(SUPER_REFSET_TYPE_ID).dataType(String.class).make();
		
				} else {
					
					LOGGER.debug("Not Creating Property {} already exist", SUPER_REFSET_TYPE_ID);
				
				}
				
				if (!mgmt.containsRelationType(TYPE)) {
					
					mgmt.makePropertyKey(TYPE).dataType(String.class).make();
		
				} else {
					
					LOGGER.debug("Not Creating Property {} already exist", TYPE);
				
				}
				
				if (!mgmt.containsRelationType(TYPE_ID)) {
		
					mgmt.makePropertyKey(TYPE_ID).dataType(String.class).make();
					
				} else {
					
					LOGGER.debug("Not Creating Property {} already exist", TYPE_ID);
				
				}
		
		
		
				
				
				//create desired index
				
				LOGGER.debug("Creating Vertex Label {}" , "GRefset");
				mgmt.makeVertexLabel("GRefset");
				
				
				VertexLabel refset = mgmt.getVertexLabel("GRefset");
				TitanGraphIndex gi = mgmt.getGraphIndex("Refset");
				
				LOGGER.debug("Creating Index  {}" , "Refset");
		
				if (gi == null) {
				
					LOGGER.debug("Creating Index  {}" , "Refset");
		
				}
				mgmt.buildIndex("Refset", Vertex.class)
				.addKey(mgmt.getPropertyKey(ID))
				.addKey(mgmt.getPropertyKey(CREATED))
				.addKey(mgmt.getPropertyKey(EFFECTIVE_DATE))
				.addKey(mgmt.getPropertyKey(CREATED_BY))
				.addKey(mgmt.getPropertyKey(PUBLISHED_DATE))
				.addKey(mgmt.getPropertyKey(DESC))
				.addKey(mgmt.getPropertyKey(MODIFIED_BY))
				.addKey(mgmt.getPropertyKey(MODIFIED_DATE))
				.indexOnly(refset).buildCompositeIndex();
				
				if (!mgmt.containsVertexLabel("GMember")) {
					
					mgmt.makeVertexLabel("GMember");

				}
				
		
				VertexLabel member = mgmt.getVertexLabel("GMember");
				
				gi = mgmt.getGraphIndex("Member");
		
				if (gi == null) {
					LOGGER.debug("Creating Index  {}" , "Member");
		
					//mgmt.buildIndex("Member", Vertex.class).indexOnly(member).buildMixedIndex("search");
					
					mgmt.buildIndex("Member", Vertex.class)
					.addKey(mgmt.getPropertyKey(ID))
					.addKey(mgmt.getPropertyKey(REFERENCE_COMPONENT_ID))
					.indexOnly(member).buildCompositeIndex();

		
				}
				
				mgmt.buildIndex("EdgeRefComponentId", Edge.class).addKey(mgmt.getPropertyKey(REFERENCE_COMPONENT_ID)).buildCompositeIndex();
				mgmt.buildIndex("EdgeEffectiveDate", Edge.class).addKey(mgmt.getPropertyKey(EFFECTIVE_DATE)).buildCompositeIndex();
				mgmt.buildIndex("EdgeStatus", Edge.class).addKey(mgmt.getPropertyKey(ACTIVE)).buildCompositeIndex();
				mgmt.buildIndex("EdgePublished", Edge.class).addKey(mgmt.getPropertyKey(PUBLISHED)).buildCompositeIndex();
				
				
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
		} finally {
			
			g.shutdown();
		}
	    
	}

	/**
	 * @param string
	 */
	public void update(String index) {
		
	    LOGGER.info("Schema has following indexes");
	    LOGGER.info("==============================");
		
		TitanGraph g = openGraph(config);
	    //management api
	    TitanManagement mgmt = g.getManagementSystem();
	    
	    try {

			// TODO Auto-generated method stub
			if("MemberRefComponentId".equalsIgnoreCase(index)) {
				
				//mgmt.updateIndex(index, updateAction);
				mgmt.buildIndex("MemberRefComponentId", Vertex.class).addKey(mgmt.makePropertyKey(REFERENCE_COMPONENT_ID)
						.dataType(String.class).make()).buildCompositeIndex();
	
			}
			LOGGER.info("==============================");
		    
		    mgmt.commit();

	} catch (Exception e) {
		
		LOGGER.error("Management Transaction Rolledback");

		e.printStackTrace();
		mgmt.rollback();
		// TODO: handle exception
	} finally {
		
		g.shutdown();
	}

		
	}

	

	

	
	

}
