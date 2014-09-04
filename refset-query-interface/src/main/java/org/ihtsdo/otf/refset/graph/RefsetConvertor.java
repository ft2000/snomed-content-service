/**
 * 
 */
package org.ihtsdo.otf.refset.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ihtsdo.otf.refset.domain.Member;
import org.ihtsdo.otf.refset.domain.Refset;
import org.ihtsdo.otf.refset.domain.RefsetType;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;

/**
 * @author Episteme Partners
 *
 */
public class RefsetConvertor {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RefsetConvertor.class);

	
	/** Extracts {@link Refset} properties in a map to be added in {@link Refset} node
	 * @param r
	 * @return
	 */
	protected static Map<String, Object> getRefsetProperties(Refset r) {
		
		LOGGER.debug("getRefsetProperties {}", r);
		
		Map<String, Object> props = new HashMap<String, Object>();
		
		if (r == null) {
			
			return props;
		}
		DateTime effDate = r.getEffectiveTime();
		
		if( effDate != null )
			props.put(RGC.EFFECTIVE_DATE, effDate.getMillis());
		
		DateTime created = r.getCreated();
		if( created != null )
			props.put(RGC.CREATED, created.getMillis());
		
		DateTime publishedDate = r.getPublishedDate();
		
		if( publishedDate != null)
			props.put(RGC.PUBLISHED_DATE, publishedDate.getMillis());
		
		if(!StringUtils.isEmpty(r.getDescription()))
				props.put(RGC.DESC, r.getDescription());
		
		if(!StringUtils.isEmpty(r.getCreatedBy()))
			props.put(RGC.CREATED_BY, r.getCreatedBy());
		
		if(!StringUtils.isEmpty(r.getSuperRefsetTypeId()))
			props.put(RGC.SUPER_REFSET_TYPE_ID, r.getSuperRefsetTypeId());

		if(!StringUtils.isEmpty(r.getLanguageCode()))
			props.put(RGC.LANG_CODE, r.getLanguageCode());
		
		if(!StringUtils.isEmpty(r.getModuleId()))
			props.put(RGC.MODULE_ID, r.getModuleId());
		
		RefsetType type = r.getType();
		if(type != null && !StringUtils.isEmpty(type.getName()))
			props.put(RGC.TYPE, r.getType().getName());

		if(!StringUtils.isEmpty(r.getTypeId()))
			props.put(RGC.TYPE_ID, r.getTypeId());
		
		props.put(RGC.PUBLISHED, r.isPublished());

		if(!StringUtils.isEmpty(r.getId()))
			props.put(RGC.ID, r.getId());//This has to be updated by real refsetId after publishing
		
		LOGGER.info("getRefsetProperties property map size {}", props.size());

		return props;
	}
	
	
	/** Extracts {@link Member} properties in a map to be added in {@link Member} node
	 * @param m
	 * @return
	 */
	protected static Map<String, Object> getMemberProperties(Member m) {
		// TODO Auto-generated method stub
		LOGGER.debug("getMemberProperties {}", m);

		Map<String, Object> props = new HashMap<String, Object>();
		if(m == null) {
			
			return props;
		}
		
		DateTime effDate = m.getEffectiveTime();
		if(effDate != null)
			props.put(RGC.EFFECTIVE_DATE, effDate.getMillis());
		
		if(!StringUtils.isEmpty(m.getModuleId()))
			props.put(RGC.MODULE_ID, m.getModuleId());
	
		if(!StringUtils.isEmpty(m.getReferenceComponentId()))
			props.put(RGC.REFERENCE_COMPONENT_ID, m.getReferenceComponentId());

		props.put(RGC.ACTIVE, m.isActive());

		if(!StringUtils.isEmpty(m.getId()))
			props.put(RGC.ID, m.getId());

		LOGGER.debug("getMemberProperties size {}", props.size());

		return props;
	}
	
	protected static Refset convert2Refset(Vertex vR) {
		
		LOGGER.debug("convert2Refset {}", vR);

		Refset r = new Refset();
		Set<String> keys = vR.getPropertyKeys();
		
		if ( keys.contains(RGC.CREATED) ) {
		
			long created = vR.getProperty(RGC.CREATED);
			r.setCreated(new DateTime(created));
		}
		
		if ( keys.contains(RGC.CREATED_BY) ) {
			
			String createdBy = vR.getProperty(RGC.CREATED_BY);
			r.setCreatedBy(createdBy);
			
		}
		
		
		if ( keys.contains(RGC.DESC) ) {
			
			String description = vR.getProperty(RGC.DESC);
			r.setDescription(description);
			
		}
		
		if ( keys.contains(RGC.EFFECTIVE_DATE) ) {
			
			long effectiveDate = vR.getProperty(RGC.EFFECTIVE_DATE);
			r.setEffectiveTime(new DateTime(effectiveDate));
			
		}
		
		if ( keys.contains(RGC.ID) ) {
			
			String id = vR.getProperty(RGC.ID);
			r.setId(id);
		}
		
		if ( keys.contains(RGC.LANG_CODE) ) {
			
			String languageCode = vR.getProperty(RGC.LANG_CODE);
			r.setLanguageCode(languageCode);
			
		}
		
		if ( keys.contains(RGC.MODULE_ID) ) {
			
			String moduleId = vR.getProperty(RGC.MODULE_ID);
			r.setModuleId(moduleId);
			
		}
		
		if ( keys.contains(RGC.PUBLISHED) ) {
			
			boolean isPublished = vR.getProperty(RGC.PUBLISHED);
			r.setPublished(isPublished);
			
		}
		
		
		if ( keys.contains(RGC.PUBLISHED_DATE) ) {
			
			long publishedDate = vR.getProperty(RGC.PUBLISHED_DATE);
			r.setPublishedDate(new DateTime(publishedDate));
			
		}
		
		
		if ( keys.contains(RGC.SUPER_REFSET_TYPE_ID) ) {
			
			String superRefsetTypeId = vR.getProperty(RGC.SUPER_REFSET_TYPE_ID);
			r.setSuperRefsetTypeId(superRefsetTypeId);
			
		}
		
		if ( keys.contains(RGC.TYPE) ) {
			
			String type = vR.getProperty(RGC.TYPE);
			
			r.setType(RefsetType.valueOf(type));
			
		}

		if ( keys.contains(RGC.TYPE_ID) ) {
			
			String typeId = vR.getProperty(RGC.TYPE_ID);
			r.setTypeId(typeId);
			
		}


		Iterable<Edge> eRs = vR.getEdges(Direction.IN, "members");
		
		if(eRs != null) {
			
			List<Member> members = new ArrayList<Member>();
			
			for (Edge eR : eRs) {
				
				Vertex vM = eR.getVertex(Direction.OUT);
				
				Set<String> mKeys = vM.getPropertyKeys();
				
				Member m = new Member();
				
				if ( mKeys.contains(RGC.ID) ) {
					
					String lId = vM.getProperty(RGC.ID);

					m.setId(lId);
					
				}
				
				if ( mKeys.contains(RGC.MODULE_ID) ) {
					
					
					String mModuleId = vM.getProperty(RGC.MODULE_ID);
					m.setModuleId(mModuleId);
					
				}
				
				
				if ( mKeys.contains(RGC.ACTIVE) ) {
					
					boolean isActive = vM.getProperty(RGC.ACTIVE);
					m.setActive(isActive);
					
				}
				
				
				if ( mKeys.contains(RGC.EFFECTIVE_DATE) ) {
					
					long effectivetime = vM.getProperty(RGC.EFFECTIVE_DATE);
					m.setEffectiveTime(new DateTime(effectivetime));
					
				}

				if ( mKeys.contains(RGC.REFERENCE_COMPONENT_ID) ) {
					
					String referenceComponentId = vM.getProperty(RGC.REFERENCE_COMPONENT_ID);
					m.setReferenceComponentId(referenceComponentId);
					
				}

				
				members.add(m);
			}
			
			r.setMembers(members);

		}

		
		LOGGER.debug("Returning Refset as {} ", r.toString());

		return r;
		
	}
	
	
	protected static List<Refset> getRefsets(Iterable<Vertex> vXs) {

		LOGGER.debug("getRefsets {}", vXs);

		List<Refset> refsets = new ArrayList<Refset>();

		for (Vertex vR : vXs) {
			

			
			Refset r = new Refset();
			Set<String> keys = vR.getPropertyKeys();
			
			if ( keys.contains(RGC.CREATED) ) {
			
				long created = vR.getProperty(RGC.CREATED);
				r.setCreated(new DateTime(created));
			}
			
			if ( keys.contains(RGC.CREATED_BY) ) {
				
				String createdBy = vR.getProperty(RGC.CREATED_BY);
				r.setCreatedBy(createdBy);
				
			}
			
			
			if ( keys.contains(RGC.DESC) ) {
				
				String description = vR.getProperty(RGC.DESC);
				r.setDescription(description);
				
			}
			
			if ( keys.contains(RGC.EFFECTIVE_DATE) ) {
				
				long effectiveDate = vR.getProperty(RGC.EFFECTIVE_DATE);
				r.setEffectiveTime(new DateTime(effectiveDate));
				
			}
			
			if ( keys.contains(RGC.ID) ) {
				
				String id = vR.getProperty(RGC.ID);
				r.setId(id);
			}
			
			if ( keys.contains(RGC.LANG_CODE) ) {
				
				String languageCode = vR.getProperty(RGC.LANG_CODE);
				r.setLanguageCode(languageCode);
				
			}
			
			if ( keys.contains(RGC.MODULE_ID) ) {
				
				String moduleId = vR.getProperty(RGC.MODULE_ID);
				r.setModuleId(moduleId);
				
			}
			
			if ( keys.contains(RGC.PUBLISHED) ) {
				
				boolean isPublished = vR.getProperty(RGC.PUBLISHED);
				r.setPublished(isPublished);
				
			}
			
			
			if ( keys.contains(RGC.PUBLISHED_DATE) ) {
				
				long publishedDate = vR.getProperty(RGC.PUBLISHED_DATE);
				r.setPublishedDate(new DateTime(publishedDate));
				
			}
			
			
			if ( keys.contains(RGC.SUPER_REFSET_TYPE_ID) ) {
				
				String superRefsetTypeId = vR.getProperty(RGC.SUPER_REFSET_TYPE_ID);
				r.setSuperRefsetTypeId(superRefsetTypeId);
				
			}
			
			if ( keys.contains(RGC.TYPE) ) {
				
				String type = vR.getProperty(RGC.TYPE);
				
				r.setType(RefsetType.valueOf(type));
				
			}

			if ( keys.contains(RGC.TYPE_ID) ) {
				
				String typeId = vR.getProperty(RGC.TYPE_ID);
				r.setTypeId(typeId);
				
			}
			
			if ( keys.contains(RGC.ACTIVE) ) {
				
				boolean active = vR.getProperty(RGC.ACTIVE);
				r.setActive(active);
				
			}
			
			LOGGER.debug("Adding Refset  {} in list ", r.toString());

			refsets.add(r);
		}
		LOGGER.debug("No of refset rerieved {}", refsets.size());
		return refsets;
	}
	
}
