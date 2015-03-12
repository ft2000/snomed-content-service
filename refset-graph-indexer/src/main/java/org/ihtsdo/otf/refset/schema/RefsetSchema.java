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
import static org.ihtsdo.otf.refset.domain.RGC.LOCK;
import static org.ihtsdo.otf.refset.domain.RGC.SCOPE;
import static org.ihtsdo.otf.refset.domain.RGC.ORIGIN_COUNTRY;
import static org.ihtsdo.otf.refset.domain.RGC.SNOMED_CT_EXT;
import static org.ihtsdo.otf.refset.domain.RGC.SNOMED_CT_VERSION;
import static org.ihtsdo.otf.refset.domain.RGC.CONTRIBUTING_ORG;
import static org.ihtsdo.otf.refset.domain.RGC.IMPLEMENTATION_DETAILS;
import static org.ihtsdo.otf.refset.domain.RGC.CLINICAL_DOMAIN;
import static org.ihtsdo.otf.refset.domain.RGC.USER_NAME;
import static org.ihtsdo.otf.refset.domain.RGC.VIEW_COUNT;
import static org.ihtsdo.otf.refset.domain.RGC.DOWNLOAD_COUNT;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.ihtsdo.otf.refset.domain.RefsetRelations;
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
	
		LOGGER.info("Creating Refset schema");
		
		TitanGraph g = openGraph(config);
		
		TitanManagement mgmt = g.getManagementSystem();

		try {
			
			LOGGER.info("creating required property keys");

			makePropertyKeys(mgmt);
			
			LOGGER.info("Making edge label");
			mgmt.makeEdgeLabel(RefsetRelations.members.toString());
			mgmt.makeEdgeLabel(RefsetRelations.viewed.toString());
			mgmt.makeEdgeLabel(RefsetRelations.downloaded.toString());

			createRefsetVertexLabel(mgmt);
			createMemberVertexLabel(mgmt);
			createUserVertexLabel(mgmt);
			
			createCompositIndex(mgmt);			
			createUserCompositIndex(mgmt);
			
			LOGGER.info("commiting Index & schema  ");

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
		
			LOGGER.info("Creating Index  {}" , CompositeIndex.byIdAndCreatedBy.toString());
			mgmt.buildIndex(CompositeIndex.byIdAndCreatedBy.toString(), Vertex.class)
			.addKey(mgmt.getPropertyKey(ID))
			.addKey(mgmt.getPropertyKey(CREATED_BY))
			.addKey(mgmt.getPropertyKey(TYPE))
			.buildCompositeIndex();
		}
		
		TitanGraphIndex byId = mgmt.getGraphIndex(CompositeIndex.byId.toString());
		
		if (byId == null) {
		
			LOGGER.info("Creating Index  {}" , CompositeIndex.byId.toString());
			mgmt.buildIndex(CompositeIndex.byId.toString(), Vertex.class)
			.addKey(mgmt.getPropertyKey(ID))
			.addKey(mgmt.getPropertyKey(TYPE))
			.buildCompositeIndex();
		}
		
		TitanGraphIndex byIdAndCreated = mgmt.getGraphIndex(CompositeIndex.byIdAndCreated.toString());
		
		if (byIdAndCreated == null) {
		
			LOGGER.info("Creating Index  {}" , CompositeIndex.byIdAndCreated.toString());
			mgmt.buildIndex(CompositeIndex.byIdAndCreated.toString(), Vertex.class)
			.addKey(mgmt.getPropertyKey(ID))
			.addKey(mgmt.getPropertyKey(CREATED))
			.addKey(mgmt.getPropertyKey(TYPE))
			.buildCompositeIndex();
		}
		
		TitanGraphIndex byDescription = mgmt.getGraphIndex(CompositeIndex.byDescription.toString());
		
		if (byDescription == null) {
		
			LOGGER.info("Creating Index  {}" , CompositeIndex.byDescription.toString());
			mgmt.buildIndex(CompositeIndex.byDescription.toString(), Vertex.class)
			.addKey(mgmt.getPropertyKey(DESC))
			.addKey(mgmt.getPropertyKey(TYPE))
			.buildCompositeIndex();
		}
		
		TitanGraphIndex byPublished = mgmt.getGraphIndex(CompositeIndex.byPublished.toString());
		
		if (byPublished == null) {
		
			LOGGER.info("Creating Index  {}" , CompositeIndex.byPublished.toString());
			mgmt.buildIndex(CompositeIndex.byPublished.toString(), Vertex.class)
			.addKey(mgmt.getPropertyKey(TYPE))
			.addKey(mgmt.getPropertyKey(PUBLISHED)).buildCompositeIndex();
		}
		
		TitanGraphIndex byType = mgmt.getGraphIndex(CompositeIndex.byType.toString());
		
		if (byType == null) {
		
			LOGGER.info("Creating Index  {}" , CompositeIndex.byType);
			mgmt.buildIndex(CompositeIndex.byType.toString(), Vertex.class)
			.addKey(mgmt.getPropertyKey(TYPE))
			.buildCompositeIndex();
		}
		
		TitanGraphIndex byRefComponentId = mgmt.getGraphIndex(CompositeIndex.byRefComponentId.toString());

		if (byRefComponentId == null) {
			
			LOGGER.info("Creating Index  {}" , CompositeIndex.byRefComponentId);
			mgmt.buildIndex(CompositeIndex.byRefComponentId.toString(), Edge.class)
			.addKey(mgmt.getPropertyKey(REFERENCE_COMPONENT_ID))
			.buildCompositeIndex();
		}
		
		
		TitanGraphIndex byIdEndDate = mgmt.getGraphIndex(CompositeIndex.byIdAndEndDate.toString());
		
		if (byIdEndDate == null) {
		
			LOGGER.info("Creating Index  {}" , CompositeIndex.byIdAndEndDate);
			mgmt.buildIndex(CompositeIndex.byIdAndEndDate.toString(), Vertex.class)
			.addKey(mgmt.getPropertyKey(ID))
			.addKey(mgmt.getPropertyKey(TYPE))
			.addKey(mgmt.getPropertyKey(END))
			.buildCompositeIndex();
		}
		
		TitanGraphIndex byIdStartDate = mgmt.getGraphIndex(CompositeIndex.byIdAndStartDate.toString());
		
		if (byIdStartDate == null) {
		
			LOGGER.info("Creating Index  {}" , CompositeIndex.byIdAndStartDate);
			mgmt.buildIndex(CompositeIndex.byIdAndStartDate.toString(), Vertex.class)
			.addKey(mgmt.getPropertyKey(ID))
			.addKey(mgmt.getPropertyKey(TYPE))
			.addKey(mgmt.getPropertyKey(START))
			.buildCompositeIndex();
		}
		
		TitanGraphIndex byIdEndStartDate = mgmt.getGraphIndex(CompositeIndex.byIdAndEndAndStartDate.toString());
		
		if (byIdEndStartDate == null) {
		
			LOGGER.info("Creating Index  {}" , CompositeIndex.byIdAndEndAndStartDate);
			mgmt.buildIndex(CompositeIndex.byIdAndEndAndStartDate.toString(), Vertex.class)
			.addKey(mgmt.getPropertyKey(ID))
			.addKey(mgmt.getPropertyKey(TYPE))
			.addKey(mgmt.getPropertyKey(START))
			.addKey(mgmt.getPropertyKey(END))
			.buildCompositeIndex();
		}
		
		TitanGraphIndex byEndDateAndPublished = mgmt.getGraphIndex(CompositeIndex.byEndDateAndPublished.toString());
		
		if (byEndDateAndPublished == null) {
		
			LOGGER.info("Creating Index  {}" , CompositeIndex.byEndDateAndPublished);
			mgmt.buildIndex(CompositeIndex.byEndDateAndPublished.toString(), Vertex.class)
			.addKey(mgmt.getPropertyKey(TYPE))
			.addKey(mgmt.getPropertyKey(PUBLISHED))
			.addKey(mgmt.getPropertyKey(END))
			.buildCompositeIndex();
		}
		
		TitanGraphIndex byEndDateAndType = mgmt.getGraphIndex(CompositeIndex.byEndDateAndType.toString());
		
		if (byEndDateAndType == null) {
		
			LOGGER.info("Creating Index  {}" , CompositeIndex.byEndDateAndType);
			mgmt.buildIndex(CompositeIndex.byEndDateAndType.toString(), Vertex.class)
			.addKey(mgmt.getPropertyKey(TYPE))
			.addKey(mgmt.getPropertyKey(END))
			.buildCompositeIndex();
		}
		
		TitanGraphIndex bySctIdType = mgmt.getGraphIndex(CompositeIndex.bySctIdType.toString());
		
		if (bySctIdType == null) {
		
			LOGGER.info("Creating Index  {}" , CompositeIndex.bySctIdType);
			mgmt.buildIndex(CompositeIndex.bySctIdType.toString(), Vertex.class)
			.addKey(mgmt.getPropertyKey(TYPE))
			.addKey(mgmt.getPropertyKey(SCTID))
			.buildCompositeIndex();
		}

		
	}
	
	private void createUserCompositIndex(TitanManagement mgmt) {
			
			TitanGraphIndex byUserName = mgmt.getGraphIndex(CompositeIndex.byUserName.toString());
			
			if (byUserName == null) {
			
				LOGGER.info("Creating Index  {}" , CompositeIndex.byUserName.toString());
				mgmt.buildIndex(CompositeIndex.byUserName.toString(), Vertex.class)
				.addKey(mgmt.getPropertyKey(USER_NAME))
				.buildCompositeIndex();
			}
	}

	/**
	 * @param mgmt
	 */
	private void createRefsetVertexLabel(TitanManagement mgmt) {

		if (!mgmt.containsVertexLabel("GRefset")) {
			
			LOGGER.info("Creating Vertex Label {}" , "GRefset");
			mgmt.makeVertexLabel("GRefset");

		}
		
	}
	
	/**
	 * @param mgmt
	 */
	private void createMemberVertexLabel(TitanManagement mgmt) {
		
		if (!mgmt.containsVertexLabel("GMember")) {
			
			
			LOGGER.info("Creating Vertex Label {}" , "GMember");
			mgmt.makeVertexLabel("GMember");

		}
	}
	
	/**
	 * @param mgmt
	 */
	private void createUserVertexLabel(TitanManagement mgmt) {

		if (!mgmt.containsVertexLabel("User")) {
			
			LOGGER.info("Creating Vertex Label {}" , "User");
			mgmt.makeVertexLabel("User");

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
			
			LOGGER.info("Creating Property {}", ACTIVE);

			mgmt.makePropertyKey(ACTIVE).dataType(Integer.class).make();

		} 
		
		if (!mgmt.containsRelationType(CREATED))  {

			LOGGER.info("Creating Property {}", CREATED);
			mgmt.makePropertyKey(CREATED).dataType(Long.class).make();
			
		}
		
		
		if (!mgmt.containsRelationType(CREATED_BY)) {

			LOGGER.info("Creating Property {}", CREATED_BY);

			mgmt.makePropertyKey(CREATED_BY).dataType(String.class).make();
			
		}
		
		if (!mgmt.containsRelationType(DESC)) {

			LOGGER.info("Creating Property {}", DESC);
			mgmt.makePropertyKey(DESC).dataType(String.class).make();
			
		} 
		
		if (!mgmt.containsRelationType(EFFECTIVE_DATE)) {

			LOGGER.info("Creating Property {}", EFFECTIVE_DATE);
			mgmt.makePropertyKey(EFFECTIVE_DATE).dataType(Long.class).make();
			
		} 
		
		if (!mgmt.containsRelationType(EXPECTED_PUBLISH_DATE)) {
			
			LOGGER.info("Creating Property {}", EXPECTED_PUBLISH_DATE);
			mgmt.makePropertyKey(EXPECTED_PUBLISH_DATE).dataType(Long.class).make();

		} 
		
		if ( !mgmt.containsRelationType(ID)) {

			LOGGER.info("Creating Property {}", ID);
			mgmt.makePropertyKey(ID).dataType(String.class).make();
			
		} 
		
		if (!mgmt.containsRelationType(LANG_CODE)) {
			
			LOGGER.info("Creating Property {}", LANG_CODE);
			mgmt.makePropertyKey(LANG_CODE).dataType(String.class).make();
			
		} 
		
		if (!mgmt.containsRelationType(MEMBER_TYPE_ID)) {
			
			LOGGER.info("Creating Property {}", MEMBER_TYPE_ID);
			mgmt.makePropertyKey(MEMBER_TYPE_ID).dataType(String.class).make();

		} 
		
		if (!mgmt.containsRelationType(MODULE_ID)) {
			
			LOGGER.info("Creating Property {}", MODULE_ID);
			mgmt.makePropertyKey(MODULE_ID).dataType(String.class).make();

		} 
		
		if (!mgmt.containsRelationType(MODIFIED_DATE))  {
			
			LOGGER.info("Creating Property {}", MODIFIED_DATE);
			mgmt.makePropertyKey(MODIFIED_DATE).dataType(Long.class).make();
			
		} 
		
		if (!mgmt.containsRelationType(MODIFIED_BY)) {

			LOGGER.info("Creating Property {}", MODIFIED_BY);
			mgmt.makePropertyKey(MODIFIED_BY).dataType(String.class).make();
			
		} 
		
		if (!mgmt.containsRelationType(PUBLISHED_DATE)) {
			
			LOGGER.info("Creating Property {}", PUBLISHED_DATE);
			mgmt.makePropertyKey(PUBLISHED_DATE).dataType(Long.class).make();
			
		} 
		
		if (!mgmt.containsRelationType(PUBLISHED)) {
			
			LOGGER.info("Creating Property {}", PUBLISHED);
			mgmt.makePropertyKey(PUBLISHED).dataType(Integer.class).make();
			
		} 
		
		if (!mgmt.containsRelationType(REFERENCE_COMPONENT_ID)) {
			
			LOGGER.info("Creating Property {}", REFERENCE_COMPONENT_ID);
			mgmt.makePropertyKey(REFERENCE_COMPONENT_ID).dataType(String.class).make();

		} 
		
		
		if (!mgmt.containsRelationType(SUPER_REFSET_TYPE_ID)) {
			
			LOGGER.info("Creating Property {}", SUPER_REFSET_TYPE_ID);
			mgmt.makePropertyKey(SUPER_REFSET_TYPE_ID).dataType(String.class).make();

		} 
		
		if (!mgmt.containsRelationType(SCTID)) {
			
			LOGGER.info("Creating Property {}", SCTID);
			mgmt.makePropertyKey(SCTID).dataType(String.class).make();

		} 
		
		if (!mgmt.containsRelationType(TYPE)) {
			
			LOGGER.info("Creating Property {}", TYPE);
			mgmt.makePropertyKey(TYPE).dataType(String.class).make();

		} 
		
		if (!mgmt.containsRelationType(TYPE_ID)) {

			LOGGER.info("Creating Property {}", TYPE_ID);
			mgmt.makePropertyKey(TYPE_ID).dataType(String.class).make();
			
		} 
		
		if (!mgmt.containsRelationType(START)) {

			LOGGER.info("Creating Property {}", START);
			mgmt.makePropertyKey(START).dataType(Long.class).make();
			
		} 
		
		if (!mgmt.containsRelationType(END)) {

			LOGGER.info("Creating Property {}", END);
			mgmt.makePropertyKey(END).dataType(Long.class).make();
			
		} 
		
		if (!mgmt.containsRelationType(E_EFFECTIVE_TIME)) {

			LOGGER.info("Creating Property {}", E_EFFECTIVE_TIME);
			mgmt.makePropertyKey(E_EFFECTIVE_TIME).dataType(Long.class).make();
			
		}
		
		if (!mgmt.containsRelationType(L_EFFECTIVE_TIME)) {

			LOGGER.info("Creating Property {}", L_EFFECTIVE_TIME);
			mgmt.makePropertyKey(L_EFFECTIVE_TIME).dataType(Long.class).make();
			
		}
		if (!mgmt.containsRelationType(PARENT_ID)) {

			LOGGER.info("Creating Property {}", PARENT_ID);
			mgmt.makePropertyKey(PARENT_ID).dataType(String.class).make();
			
		}
		
		if (!mgmt.containsRelationType(LOCK)) {

			LOGGER.info("Creating Property {}", LOCK);
			mgmt.makePropertyKey(LOCK).dataType(Integer.class).make();
			
		}
		
		if (!mgmt.containsRelationType(SCOPE)) {

			LOGGER.info("Creating Property {}", SCOPE);
			mgmt.makePropertyKey(SCOPE).dataType(String.class).make();
			
		}
		
		
		if (!mgmt.containsRelationType(ORIGIN_COUNTRY)) {

			LOGGER.info("Creating Property {}", ORIGIN_COUNTRY);
			mgmt.makePropertyKey(ORIGIN_COUNTRY).dataType(String.class).make();
			
		}
		
		if (!mgmt.containsRelationType(CONTRIBUTING_ORG)) {

			LOGGER.info("Creating Property {}", CONTRIBUTING_ORG);
			mgmt.makePropertyKey(CONTRIBUTING_ORG).dataType(String.class).make();
			
		}
		
		if (!mgmt.containsRelationType(SNOMED_CT_EXT)) {

			LOGGER.info("Creating Property {}", SNOMED_CT_EXT);
			mgmt.makePropertyKey(SNOMED_CT_EXT).dataType(String.class).make();
			
		}
		
		if (!mgmt.containsRelationType(SNOMED_CT_VERSION)) {

			LOGGER.info("Creating Property {}", SNOMED_CT_VERSION);
			mgmt.makePropertyKey(SNOMED_CT_VERSION).dataType(String.class).make();
			
		}
		
		if (!mgmt.containsRelationType(IMPLEMENTATION_DETAILS)) {

			LOGGER.info("Creating Property {}", IMPLEMENTATION_DETAILS);
			mgmt.makePropertyKey(IMPLEMENTATION_DETAILS).dataType(String.class).make();
			
		}
		
		if (!mgmt.containsRelationType(CLINICAL_DOMAIN)) {

			LOGGER.info("Creating Property {}", CLINICAL_DOMAIN);
			mgmt.makePropertyKey(CLINICAL_DOMAIN).dataType(String.class).make();
			
		}
		
		if (!mgmt.containsRelationType(DOWNLOAD_COUNT)) {

			LOGGER.info("Creating Property {}", DOWNLOAD_COUNT);
			mgmt.makePropertyKey(DOWNLOAD_COUNT).dataType(Integer.class).make();
			
		}
		
		if (!mgmt.containsRelationType(VIEW_COUNT)) {

			LOGGER.info("Creating Property {}", VIEW_COUNT);
			mgmt.makePropertyKey(VIEW_COUNT).dataType(Integer.class).make();
			
		}
		
		
		
		
		//for user vertex
		
		if (!mgmt.containsRelationType(USER_NAME)) {

			LOGGER.info("Creating Property {}", USER_NAME);
			mgmt.makePropertyKey(USER_NAME).dataType(String.class).make();
			
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
			
				LOGGER.info("Creating Index  {}" , "Refset mixed index");
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
				LOGGER.info("Creating Index  {}" , "Member mixed index");
					
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
			
			TitanGraphIndex user = mgmt.getGraphIndex(MixedIndex.User.toString());
			
			if (user == null) {
				LOGGER.info("Creating Index  {}" , "User mixed index");
					
				mgmt.buildIndex(MixedIndex.User.toString(), Vertex.class)
				.addKey(mgmt.getPropertyKey(USER_NAME), 
						Parameter.of(MAPPED, USER_NAME))
				.addKey(mgmt.getPropertyKey(CREATED), 
	    				Parameter.of(MAPPED, Properties.created.toString()))

				.indexOnly(mgmt.getVertexLabel("User"))
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
			
				LOGGER.info("Updating Index  {}" , "Refset mixed index");
				
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
				
				if (!existingProp.contains(mgmt.getPropertyKey(SCOPE))) {

					mgmt.addIndexKey(giRefset, mgmt.getPropertyKey(SCOPE), 
							Parameter.of(MAPPED, SCOPE));
				
				}
				if (!existingProp.contains(mgmt.getPropertyKey(CONTRIBUTING_ORG))) {

					mgmt.addIndexKey(giRefset, mgmt.getPropertyKey(CONTRIBUTING_ORG), 
							Parameter.of(MAPPED, CONTRIBUTING_ORG));

				}
				
				if (!existingProp.contains(mgmt.getPropertyKey(SNOMED_CT_EXT))) {

					mgmt.addIndexKey(giRefset, mgmt.getPropertyKey(SNOMED_CT_EXT), 
							Parameter.of(MAPPED, SNOMED_CT_EXT));

				}
				
				if (!existingProp.contains(mgmt.getPropertyKey(SNOMED_CT_VERSION))) {

					mgmt.addIndexKey(giRefset, mgmt.getPropertyKey(SNOMED_CT_VERSION), 
							Parameter.of(MAPPED, SNOMED_CT_VERSION));

				}
				
				if (!existingProp.contains(mgmt.getPropertyKey(ORIGIN_COUNTRY))) {

					mgmt.addIndexKey(giRefset, mgmt.getPropertyKey(ORIGIN_COUNTRY), 
							Parameter.of(MAPPED, ORIGIN_COUNTRY));
					
				}
				
				if (!existingProp.contains(mgmt.getPropertyKey(IMPLEMENTATION_DETAILS))) {

					mgmt.addIndexKey(giRefset, mgmt.getPropertyKey(IMPLEMENTATION_DETAILS), 
							Parameter.of(MAPPED, IMPLEMENTATION_DETAILS));

				}
				
				if (!existingProp.contains(mgmt.getPropertyKey(CLINICAL_DOMAIN))) {

					mgmt.addIndexKey(giRefset, mgmt.getPropertyKey(CLINICAL_DOMAIN), 
							Parameter.of(MAPPED, CLINICAL_DOMAIN));

				}
				
				if (!existingProp.contains(mgmt.getPropertyKey(VIEW_COUNT))) {

					mgmt.addIndexKey(giRefset, mgmt.getPropertyKey(VIEW_COUNT), 
							Parameter.of(MAPPED, VIEW_COUNT));

				}
				
				if (!existingProp.contains(mgmt.getPropertyKey(DOWNLOAD_COUNT))) {

					mgmt.addIndexKey(giRefset, mgmt.getPropertyKey(DOWNLOAD_COUNT), 
							Parameter.of(MAPPED, DOWNLOAD_COUNT));

				}
				
			}
			
			
						
			TitanGraphIndex giMember = mgmt.getGraphIndex(MixedIndex.Member.toString());
	
			if (giMember != null) {
				
				LOGGER.info("Updating Index  {}" , "Member mixed index");

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