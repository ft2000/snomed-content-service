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
import static org.ihtsdo.otf.refset.domain.RGC.SCTID;
import static org.ihtsdo.otf.refset.domain.RGC.MEMBER_TYPE_ID;
import static org.ihtsdo.otf.refset.domain.RGC.EXPECTED_PUBLISH_DATE;
import static org.ihtsdo.otf.refset.domain.RGC.START;
import static org.ihtsdo.otf.refset.domain.RGC.END;
import static org.ihtsdo.otf.refset.domain.RGC.E_EFFECTIVE_TIME;
import static org.ihtsdo.otf.refset.domain.RGC.L_EFFECTIVE_TIME;
import static org.ihtsdo.otf.refset.domain.RGC.PARENT_ID;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.ihtsdo.otf.snomed.domain.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thinkaurelius.titan.core.PropertyKey;
import com.thinkaurelius.titan.core.RelationType;
import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;
import com.thinkaurelius.titan.core.VertexLabel;
import com.thinkaurelius.titan.core.schema.Parameter;
import com.thinkaurelius.titan.core.schema.TitanGraphIndex;
import com.thinkaurelius.titan.core.schema.TitanManagement;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;

public class RefsetSchema {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RefsetSchema.class);
	private static final String SEARCH = "refset";

	private static final String MAPPED = "mapped-name";
	
	private String config;

	/**
	 * 
	 */
	public RefsetSchema(String config) {
		
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
			
			LOGGER.debug("creating required property keys");

			makePropertyKeys(mgmt);
			
			LOGGER.debug("Making members edge label");
			mgmt.makeEdgeLabel(RefsetRelations.members.toString());
	
			createRefsetVertexLabel(mgmt);
			createMemberVertexLabel(mgmt);

			createCompositIndex(mgmt);			
			
			LOGGER.debug("commiting Index & schema  ");

			mgmt.commit();	
			g.commit();
		} catch(Exception e) {
			

			mgmt.rollback();
			e.printStackTrace();

			
		} finally {
			
			g.shutdown();

		}
		
		
	}
	
	/**
	 * @param mgmt
	 */
	private void createCompositIndex(TitanManagement mgmt) {
		
		TitanGraphIndex byIdAndCreatedBy = mgmt.getGraphIndex(CompositeIndex.byIdAndCreatedBy.toString());
		
		if (byIdAndCreatedBy == null) {
		
			LOGGER.debug("Creating Index  {}" , CompositeIndex.byIdAndCreatedBy.toString());
			mgmt.buildIndex(CompositeIndex.byIdAndCreatedBy.toString(), Vertex.class)
			.addKey(mgmt.getPropertyKey(ID))
			.addKey(mgmt.getPropertyKey(CREATED_BY))
			.addKey(mgmt.getPropertyKey(TYPE))
			.buildCompositeIndex();
		}
		
		TitanGraphIndex byId = mgmt.getGraphIndex(CompositeIndex.byId.toString());
		
		if (byId == null) {
		
			LOGGER.debug("Creating Index  {}" , CompositeIndex.byId.toString());
			mgmt.buildIndex(CompositeIndex.byId.toString(), Vertex.class)
			.addKey(mgmt.getPropertyKey(ID))
			.addKey(mgmt.getPropertyKey(TYPE))
			.buildCompositeIndex();
		}
		
		TitanGraphIndex byIdAndCreated = mgmt.getGraphIndex(CompositeIndex.byIdAndCreated.toString());
		
		if (byIdAndCreated == null) {
		
			LOGGER.debug("Creating Index  {}" , CompositeIndex.byIdAndCreated.toString());
			mgmt.buildIndex(CompositeIndex.byIdAndCreated.toString(), Vertex.class)
			.addKey(mgmt.getPropertyKey(ID))
			.addKey(mgmt.getPropertyKey(CREATED))
			.addKey(mgmt.getPropertyKey(TYPE))
			.buildCompositeIndex();
		}
		
		TitanGraphIndex byDescription = mgmt.getGraphIndex(CompositeIndex.byDescription.toString());
		
		if (byDescription == null) {
		
			LOGGER.debug("Creating Index  {}" , CompositeIndex.byDescription.toString());
			mgmt.buildIndex(CompositeIndex.byDescription.toString(), Vertex.class)
			.addKey(mgmt.getPropertyKey(DESC))
			.addKey(mgmt.getPropertyKey(TYPE))
			.buildCompositeIndex();
		}
		
		TitanGraphIndex byPublished = mgmt.getGraphIndex(CompositeIndex.byPublished.toString());
		
		if (byPublished == null) {
		
			LOGGER.debug("Creating Index  {}" , CompositeIndex.byPublished.toString());
			mgmt.buildIndex(CompositeIndex.byPublished.toString(), Vertex.class)
			.addKey(mgmt.getPropertyKey(TYPE))
			.addKey(mgmt.getPropertyKey(PUBLISHED)).buildCompositeIndex();
		}
		
		TitanGraphIndex byType = mgmt.getGraphIndex(CompositeIndex.byType.toString());
		
		if (byType == null) {
		
			LOGGER.debug("Creating Index  {}" , CompositeIndex.byType);
			mgmt.buildIndex(CompositeIndex.byType.toString(), Vertex.class)
			.addKey(mgmt.getPropertyKey(TYPE))
			.buildCompositeIndex();
		}
		
		TitanGraphIndex byRefComponentId = mgmt.getGraphIndex(CompositeIndex.byRefComponentId.toString());

		if (byRefComponentId == null) {
			
			LOGGER.debug("Creating Index  {}" , CompositeIndex.byRefComponentId);
			mgmt.buildIndex(CompositeIndex.byRefComponentId.toString(), Edge.class)
			.addKey(mgmt.getPropertyKey(REFERENCE_COMPONENT_ID))
			.indexOnly(mgmt.getEdgeLabel(RefsetRelations.members.toString()))
			.buildCompositeIndex();
		}
		
		
		TitanGraphIndex byIdEndDate = mgmt.getGraphIndex(CompositeIndex.byIdAndEndDate.toString());
		
		if (byIdEndDate == null) {
		
			LOGGER.debug("Creating Index  {}" , CompositeIndex.byIdAndEndDate);
			mgmt.buildIndex(CompositeIndex.byIdAndEndDate.toString(), Vertex.class)
			.addKey(mgmt.getPropertyKey(ID))
			.addKey(mgmt.getPropertyKey(TYPE))
			.addKey(mgmt.getPropertyKey(END))
			.buildCompositeIndex();
		}
		
		TitanGraphIndex byIdStartDate = mgmt.getGraphIndex(CompositeIndex.byIdAndStartDate.toString());
		
		if (byIdStartDate == null) {
		
			LOGGER.debug("Creating Index  {}" , CompositeIndex.byIdAndStartDate);
			mgmt.buildIndex(CompositeIndex.byIdAndStartDate.toString(), Vertex.class)
			.addKey(mgmt.getPropertyKey(ID))
			.addKey(mgmt.getPropertyKey(TYPE))
			.addKey(mgmt.getPropertyKey(START))
			.buildCompositeIndex();
		}
		
		TitanGraphIndex byIdEndStartDate = mgmt.getGraphIndex(CompositeIndex.byIdAndEndAndStartDate.toString());
		
		if (byIdEndStartDate == null) {
		
			LOGGER.debug("Creating Index  {}" , CompositeIndex.byIdAndEndAndStartDate);
			mgmt.buildIndex(CompositeIndex.byIdAndEndAndStartDate.toString(), Vertex.class)
			.addKey(mgmt.getPropertyKey(ID))
			.addKey(mgmt.getPropertyKey(TYPE))
			.addKey(mgmt.getPropertyKey(START))
			.addKey(mgmt.getPropertyKey(END))
			.buildCompositeIndex();
		}
		
		TitanGraphIndex byEndDateAndPublished = mgmt.getGraphIndex(CompositeIndex.byEndDateAndPublished.toString());
		
		if (byEndDateAndPublished == null) {
		
			LOGGER.debug("Creating Index  {}" , CompositeIndex.byEndDateAndPublished);
			mgmt.buildIndex(CompositeIndex.byEndDateAndPublished.toString(), Vertex.class)
			.addKey(mgmt.getPropertyKey(TYPE))
			.addKey(mgmt.getPropertyKey(PUBLISHED))
			.addKey(mgmt.getPropertyKey(END))
			.buildCompositeIndex();
		}
		
		TitanGraphIndex byEndDateAndType = mgmt.getGraphIndex(CompositeIndex.byEndDateAndType.toString());
		
		if (byEndDateAndType == null) {
		
			LOGGER.debug("Creating Index  {}" , CompositeIndex.byEndDateAndType);
			mgmt.buildIndex(CompositeIndex.byEndDateAndType.toString(), Vertex.class)
			.addKey(mgmt.getPropertyKey(TYPE))
			.addKey(mgmt.getPropertyKey(END))
			.buildCompositeIndex();
		}
		
		TitanGraphIndex bySctIdType = mgmt.getGraphIndex(CompositeIndex.bySctIdType.toString());
		
		if (bySctIdType == null) {
		
			LOGGER.debug("Creating Index  {}" , CompositeIndex.bySctIdType);
			mgmt.buildIndex(CompositeIndex.bySctIdType.toString(), Vertex.class)
			.addKey(mgmt.getPropertyKey(TYPE))
			.addKey(mgmt.getPropertyKey(SCTID))
			.buildCompositeIndex();
		}

		
	}

	/**
	 * @param mgmt
	 */
	private void createRefsetVertexLabel(TitanManagement mgmt) {

		if (!mgmt.containsVertexLabel("GRefset")) {
			
			LOGGER.debug("Creating Vertex Label {}" , "GRefset");
			mgmt.makeVertexLabel("GRefset");

		}
		
	}
	
	/**
	 * @param mgmt
	 */
	private void createMemberVertexLabel(TitanManagement mgmt) {
		
		if (!mgmt.containsVertexLabel("GMember")) {
			
			
			LOGGER.debug("Creating Vertex Label {}" , "GMember");
			mgmt.makeVertexLabel("GMember");

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
			mgmt.rollback();
			e.printStackTrace();

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
				
		    	LOGGER.info("Vertex Index Name -->  {} Backing Index --> {}  Indexed Element -->{}", gi.getName(), gi.getBackingIndex(), gi.getIndexedElement());
		    	PropertyKey[] keys = gi.getFieldKeys();
		    	for (PropertyKey prop : keys) {
					
			    	LOGGER.info("Vertex Index  {} indexes --> key {}", gi.getName(), prop);

		    	}
		    	
			}
		    Iterable<TitanGraphIndex> eis = mgmt.getGraphIndexes(Edge.class);
		    for (TitanGraphIndex gi : eis) {
				
		    	LOGGER.info("Edge Index Name -->  {} Backing Index --> {}  Indexed Element -->{}", gi.getName(), gi.getBackingIndex(), gi.getIndexedElement());
		    	PropertyKey[] keys = gi.getFieldKeys();
		    	for (PropertyKey prop : keys) {
					
			    	LOGGER.info("Vertex Index  {} indexes --> key {}", gi.getName(), prop);

		    	}
			}
		    LOGGER.info("==============================");
		    
		    mgmt.commit();

		} catch (Exception e) {
			
			LOGGER.error("Management Transaction Rolledback");
			mgmt.rollback();
			e.printStackTrace();

		} finally {
			
			g.shutdown();
		}
	    
	}

	
	private void makePropertyKeys(TitanManagement mgmt) {
		
		
		
		if (!mgmt.containsRelationType(ACTIVE)) {
			
			LOGGER.debug("Creating Property {}", ACTIVE);

			mgmt.makePropertyKey(ACTIVE).dataType(Integer.class).make();

		} 
		
		if (!mgmt.containsRelationType(CREATED))  {

			LOGGER.debug("Creating Property {}", CREATED);
			mgmt.makePropertyKey(CREATED).dataType(Long.class).make();
			
		}
		
		
		if (!mgmt.containsRelationType(CREATED_BY)) {

			LOGGER.debug("Creating Property {}", CREATED_BY);

			mgmt.makePropertyKey(CREATED_BY).dataType(String.class).make();
			
		}
		
		if (!mgmt.containsRelationType(DESC)) {

			LOGGER.debug("Creating Property {}", DESC);
			mgmt.makePropertyKey(DESC).dataType(String.class).make();
			
		} 
		
		if (!mgmt.containsRelationType(EFFECTIVE_DATE)) {

			LOGGER.debug("Creating Property {}", EFFECTIVE_DATE);
			mgmt.makePropertyKey(EFFECTIVE_DATE).dataType(Long.class).make();
			
		} 
		
		if (!mgmt.containsRelationType(EXPECTED_PUBLISH_DATE)) {
			
			LOGGER.debug("Creating Property {}", EXPECTED_PUBLISH_DATE);
			mgmt.makePropertyKey(EXPECTED_PUBLISH_DATE).dataType(Long.class).make();

		} 
		
		if ( !mgmt.containsRelationType(ID)) {

			LOGGER.debug("Creating Property {}", ID);
			mgmt.makePropertyKey(ID).dataType(String.class).make();
			
		} 
		
		if (!mgmt.containsRelationType(LANG_CODE)) {
			
			LOGGER.debug("Creating Property {}", LANG_CODE);
			mgmt.makePropertyKey(LANG_CODE).dataType(String.class).make();
			
		} 
		
		if (!mgmt.containsRelationType(MEMBER_TYPE_ID)) {
			
			LOGGER.debug("Creating Property {}", MEMBER_TYPE_ID);
			mgmt.makePropertyKey(MEMBER_TYPE_ID).dataType(String.class).make();

		} 
		
		if (!mgmt.containsRelationType(MODULE_ID)) {
			
			LOGGER.debug("Creating Property {}", MODULE_ID);
			mgmt.makePropertyKey(MODULE_ID).dataType(String.class).make();

		} 
		
		if (!mgmt.containsRelationType(MODIFIED_DATE))  {
			
			LOGGER.debug("Creating Property {}", MODIFIED_DATE);
			mgmt.makePropertyKey(MODIFIED_DATE).dataType(Long.class).make();
			
		} 
		
		if (!mgmt.containsRelationType(MODIFIED_BY)) {

			LOGGER.debug("Creating Property {}", MODIFIED_BY);
			mgmt.makePropertyKey(MODIFIED_BY).dataType(String.class).make();
			
		} 
		
		if (!mgmt.containsRelationType(PUBLISHED_DATE)) {
			
			LOGGER.debug("Creating Property {}", PUBLISHED_DATE);
			mgmt.makePropertyKey(PUBLISHED_DATE).dataType(Long.class).make();
			
		} 
		
		if (!mgmt.containsRelationType(PUBLISHED)) {
			
			LOGGER.debug("Creating Property {}", PUBLISHED);
			mgmt.makePropertyKey(PUBLISHED).dataType(Integer.class).make();
			
		} 
		
		if (!mgmt.containsRelationType(REFERENCE_COMPONENT_ID)) {
			
			LOGGER.debug("Creating Property {}", REFERENCE_COMPONENT_ID);
			mgmt.makePropertyKey(REFERENCE_COMPONENT_ID).dataType(String.class).make();

		} 
		
		
		if (!mgmt.containsRelationType(SUPER_REFSET_TYPE_ID)) {
			
			LOGGER.debug("Creating Property {}", SUPER_REFSET_TYPE_ID);
			mgmt.makePropertyKey(SUPER_REFSET_TYPE_ID).dataType(String.class).make();

		} 
		
		if (!mgmt.containsRelationType(SCTID)) {
			
			LOGGER.debug("Creating Property {}", SCTID);
			mgmt.makePropertyKey(SCTID).dataType(String.class).make();

		} 
		
		if (!mgmt.containsRelationType(TYPE)) {
			
			LOGGER.debug("Creating Property {}", TYPE);
			mgmt.makePropertyKey(TYPE).dataType(String.class).make();

		} 
		
		if (!mgmt.containsRelationType(TYPE_ID)) {

			LOGGER.debug("Creating Property {}", TYPE_ID);
			mgmt.makePropertyKey(TYPE_ID).dataType(String.class).make();
			
		} 
		
		if (!mgmt.containsRelationType(START)) {

			LOGGER.debug("Creating Property {}", START);
			mgmt.makePropertyKey(START).dataType(Long.class).make();
			
		} 
		
		if (!mgmt.containsRelationType(END)) {

			LOGGER.debug("Creating Property {}", END);
			mgmt.makePropertyKey(END).dataType(Long.class).make();
			
		} 
		
		if (!mgmt.containsRelationType(E_EFFECTIVE_TIME)) {

			LOGGER.debug("Creating Property {}", E_EFFECTIVE_TIME);
			mgmt.makePropertyKey(E_EFFECTIVE_TIME).dataType(Long.class).make();
			
		}
		
		if (!mgmt.containsRelationType(L_EFFECTIVE_TIME)) {

			LOGGER.debug("Creating Property {}", L_EFFECTIVE_TIME);
			mgmt.makePropertyKey(L_EFFECTIVE_TIME).dataType(Long.class).make();
			
		}
		if (!mgmt.containsRelationType(PARENT_ID)) {

			LOGGER.debug("Creating Property {}", PARENT_ID);
			mgmt.makePropertyKey(PARENT_ID).dataType(String.class).make();
			
		}

	}
	
	/**
	 * @param mgmt
	 */
	public void createMixedIndex(String backingIndexName) {
		
		if (StringUtils.isEmpty(backingIndexName)) {
			
			backingIndexName = SEARCH;
		}
		TitanGraph g = openGraph(config);
	    //management api
	    TitanManagement mgmt = g.getManagementSystem();
	    
	    try {
		
		
			TitanGraphIndex giRefset = mgmt.getGraphIndex(MixedIndex.Refset.toString());
			
			if (giRefset == null) {
			
				LOGGER.debug("Creating Index  {}" , "Refset mixed index");
				mgmt.buildIndex(MixedIndex.Refset.toString(), Vertex.class)
				.addKey(mgmt.getPropertyKey(ACTIVE), 
						Parameter.of(MAPPED, Properties.status.toString()))
				.addKey(mgmt.getPropertyKey(ID), 
						Parameter.of(MAPPED, Properties.sid.toString()))
				.addKey(mgmt.getPropertyKey(CREATED), 
						Parameter.of(MAPPED, Properties.created.toString()))
				.addKey(mgmt.getPropertyKey(EFFECTIVE_DATE), 
						Parameter.of(MAPPED, Properties.effectiveTime.toString()))
				.addKey(mgmt.getPropertyKey(CREATED_BY), 
						Parameter.of(MAPPED, Properties.createdBy.toString()))
				.addKey(mgmt.getPropertyKey(PUBLISHED_DATE), 
						Parameter.of(MAPPED, Properties.publishedDate.toString()))
				.addKey(mgmt.getPropertyKey(PUBLISHED), 
						Parameter.of(MAPPED, Properties.published.toString()))
				.addKey(mgmt.getPropertyKey(DESC), 
						Parameter.of(MAPPED, Properties.title.toString()))
				.addKey(mgmt.getPropertyKey(MODIFIED_BY), 
						Parameter.of(MAPPED, Properties.modifiedBy.toString()))
				.addKey(mgmt.getPropertyKey(MODIFIED_DATE), 
						Parameter.of(MAPPED, Properties.modifiedDate.toString()))
				.addKey(mgmt.getPropertyKey(MODULE_ID), 
						Parameter.of(MAPPED, Properties.moduleId.toString()))
				.addKey(mgmt.getPropertyKey(LANG_CODE), 
						Parameter.of(MAPPED, Properties.languageCode.toString()))
				.addKey(mgmt.getPropertyKey(REFERENCE_COMPONENT_ID), 
						Parameter.of(MAPPED, Properties.referenceComponentId.toString()))
				.addKey(mgmt.getPropertyKey(SCTID), 
						Parameter.of(MAPPED, Properties.sctid.toString()))
				.addKey(mgmt.getPropertyKey(SUPER_REFSET_TYPE_ID), 
						Parameter.of(MAPPED, SUPER_REFSET_TYPE_ID))
				.addKey(mgmt.getPropertyKey(TYPE), 
						Parameter.of(MAPPED, TYPE))
				.addKey(mgmt.getPropertyKey(TYPE_ID), 
						Parameter.of(MAPPED, TYPE_ID))
				.addKey(mgmt.getPropertyKey(START), 
						Parameter.of(MAPPED, START))
				.addKey(mgmt.getPropertyKey(END), 
						Parameter.of(MAPPED, END))
						

				.indexOnly(mgmt.getVertexLabel("GRefset")).buildMixedIndex(backingIndexName);
			}
			
			
						
			TitanGraphIndex giMember = mgmt.getGraphIndex(MixedIndex.Member.toString());
	
			if (giMember == null) {
				LOGGER.debug("Creating Index  {}" , "Member mixed index");
					
				mgmt.buildIndex(MixedIndex.Member.toString(), Vertex.class)
				.addKey(mgmt.getPropertyKey(ACTIVE), 
						Parameter.of(MAPPED, Properties.status.toString()))
				.addKey(mgmt.getPropertyKey(ID), 
						Parameter.of(MAPPED, Properties.sid.toString()))
				.addKey(mgmt.getPropertyKey(SCTID), 
		    				Parameter.of(MAPPED, Properties.sctid.toString()))
				.addKey(mgmt.getPropertyKey(REFERENCE_COMPONENT_ID), 
	    				Parameter.of(MAPPED, Properties.referenceComponentId.toString()))
				.addKey(mgmt.getPropertyKey(MODULE_ID), 
	    				Parameter.of(MAPPED, Properties.moduleId.toString()))
				.addKey(mgmt.getPropertyKey(MODIFIED_DATE), 
	    				Parameter.of(MAPPED, Properties.modifiedDate.toString()))
				.addKey(mgmt.getPropertyKey(MODIFIED_BY), 
	    				Parameter.of(MAPPED, Properties.modifiedBy.toString()))
				.addKey(mgmt.getPropertyKey(CREATED), 
	    				Parameter.of(MAPPED, Properties.created.toString()))
				.addKey(mgmt.getPropertyKey(CREATED_BY), 
	    				Parameter.of(MAPPED, Properties.createdBy.toString()))
				.addKey(mgmt.getPropertyKey(TYPE), 
						Parameter.of(MAPPED, TYPE))
				.addKey(mgmt.getPropertyKey(START), 
						Parameter.of(MAPPED, START))
				.addKey(mgmt.getPropertyKey(END), 
						Parameter.of(MAPPED, END))

				.indexOnly(mgmt.getVertexLabel("GMember"))
	    		.buildMixedIndex(backingIndexName);
	
			}
			
			mgmt.commit();
			
	    } catch (Exception e) {
		
	    	LOGGER.error("Management Transaction Rolledback");
	    	mgmt.rollback();
	    	e.printStackTrace();
	    	
	    } finally {
		
	    	g.shutdown();
	    }
	}
	
	
	public void updateMixedIndex() {

		TitanGraph g = openGraph(config);
	    //management api
	    TitanManagement mgmt = g.getManagementSystem();
	    
	    try {
		
		
			TitanGraphIndex giRefset = mgmt.getGraphIndex(MixedIndex.Refset.toString());
			
			if (giRefset != null) {
			
				LOGGER.debug("Updating Index  {}" , "Refset mixed index");
				
				PropertyKey[] keys = giRefset.getFieldKeys();
				
				List<PropertyKey> existingProp = Arrays.asList(keys);
				
				if (!existingProp.contains(mgmt.getPropertyKey(E_EFFECTIVE_TIME))) {

					mgmt.addIndexKey(giRefset, mgmt.getPropertyKey(E_EFFECTIVE_TIME), 
							Parameter.of(MAPPED, Properties.earliestEffectiveTime.toString()));

				}
				if (!existingProp.contains(mgmt.getPropertyKey(L_EFFECTIVE_TIME))) {

					mgmt.addIndexKey(giRefset, mgmt.getPropertyKey(L_EFFECTIVE_TIME), 
							Parameter.of(MAPPED, Properties.latestEffectiveTime.toString()));

				}
				
				if (!existingProp.contains(mgmt.getPropertyKey(EXPECTED_PUBLISH_DATE))) {

				
					mgmt.addIndexKey(giRefset, mgmt.getPropertyKey(EXPECTED_PUBLISH_DATE), 
						Parameter.of(MAPPED, EXPECTED_PUBLISH_DATE));
				}

			}
			
			
						
			TitanGraphIndex giMember = mgmt.getGraphIndex(MixedIndex.Member.toString());
	
			if (giMember != null) {
				
				LOGGER.debug("Updating Index  {}" , "Member mixed index");

				PropertyKey[] keys = giMember.getFieldKeys();
				
				List<PropertyKey> existingProp = Arrays.asList(keys);
				
				if (!existingProp.contains(mgmt.getPropertyKey(EFFECTIVE_DATE))) {
					
					mgmt.addIndexKey(giMember, mgmt.getPropertyKey(EFFECTIVE_DATE), 
							Parameter.of(MAPPED, Properties.effectiveTime.toString()));

				}
				if (!existingProp.contains(mgmt.getPropertyKey(DESC))) {

					mgmt.addIndexKey(giMember, mgmt.getPropertyKey(DESC), 
							Parameter.of(MAPPED, Properties.title.toString()));

				}
				
				if (!existingProp.contains(mgmt.getPropertyKey(PARENT_ID))) {

					mgmt.addIndexKey(giMember, mgmt.getPropertyKey(PARENT_ID), 
							Parameter.of(MAPPED, Properties.parentId.toString()));

				}
				
				
	
			}
			
			mgmt.commit();
			
	    } catch (Exception e) {
		
	    	LOGGER.error("Management Transaction Rolledback");
	    	mgmt.rollback();
	    	e.printStackTrace();
	    	
	    } finally {
		
	    	g.shutdown();
	    }
	
	}
}