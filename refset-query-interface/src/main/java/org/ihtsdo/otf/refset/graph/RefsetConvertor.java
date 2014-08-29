/**
 * 
 */
package org.ihtsdo.otf.refset.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ihtsdo.otf.refset.domain.Member;
import org.ihtsdo.otf.refset.domain.Refset;
import org.ihtsdo.otf.refset.domain.RefsetType;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
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
		
		Map<String, Object> props = new HashMap<String, Object>();
		
		if (r == null) {
			
			return props;
		}
		
		if(!StringUtils.isEmpty(r.getEffectiveTime()))
			props.put(RGC.EFFECTIVE_DATE, getDateTime(r.getEffectiveTime()));
		
		if(!StringUtils.isEmpty(r.getCreated()))
			props.put(RGC.CREATED, getDateTime(r.getCreated()));
		
		if(!StringUtils.isEmpty(r.getPublishedDate()))
			props.put(RGC.PUBLISHED_DATE, getDateTime(r.getPublishedDate()));
		
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

		if(!StringUtils.isEmpty(r.getType().getName()))
			props.put(RGC.TYPE, r.getType().getName());

		if(!StringUtils.isEmpty(r.getTypeId()))
			props.put(RGC.TYPE_ID, r.getTypeId());
		
		props.put(RGC.PUBLISHED, r.isPublished());

		if(!StringUtils.isEmpty(r.getId()))
			props.put(RGC.ID, r.getId());//This has to be updated by real refsetId after publishing
		return props;
	}
	
	
	/** Extracts {@link Member} properties in a map to be added in {@link Member} node
	 * @param m
	 * @return
	 */
	protected static Map<String, Object> getMemberProperties(Member m) {
		// TODO Auto-generated method stub
		Map<String, Object> props = new HashMap<String, Object>();
		if(m == null) {
			
			return props;
		}
		if(!StringUtils.isEmpty(m.getEffectiveTime()))
			props.put(RGC.EFFECTIVE_DATE, getDateTime(m.getEffectiveTime()));
		
		if(!StringUtils.isEmpty(m.getModuleId()))
			props.put(RGC.MODULE_ID, m.getModuleId());
	
		if(!StringUtils.isEmpty(m.getReferenceComponentId()))
			props.put(RGC.REFERENCE_COMPONENT_ID, m.getReferenceComponentId());

		props.put(RGC.ACTIVE, m.isActive());

		if(!StringUtils.isEmpty(m.getId()))
			props.put(RGC.ID, m.getId());
		
		return props;
	}
	
	protected static Refset convert2Refset(Vertex vR) {
		
		Refset r = new Refset();
		
		String created = vR.getProperty(RGC.CREATED);
		r.setCreated(created);
		
		String createdBy = vR.getProperty(RGC.CREATED_BY);
		r.setCreatedBy(createdBy);
		
		String description = vR.getProperty(RGC.DESC);
		r.setDescription(description);
		
		String effectiveDate = vR.getProperty(RGC.EFFECTIVE_DATE);
		r.setEffectiveTime(effectiveDate);
		
		String id = vR.getProperty(RGC.ID);
		r.setId(id);
		
		String languageCode = vR.getProperty(RGC.LANG_CODE);
		r.setLanguageCode(languageCode);
		
		String moduleId = vR.getProperty(RGC.MODULE_ID);
		r.setModuleId(moduleId);
		
		boolean isPublished = vR.getProperty(RGC.PUBLISHED);
		r.setPublished(isPublished);
		
		String publishedDate = vR.getProperty(RGC.PUBLISHED_DATE);
		r.setPublishedDate(publishedDate);
		
		String superRefsetTypeId = vR.getProperty(RGC.SUPER_REFSET_TYPE_ID);
		r.setSuperRefsetTypeId(superRefsetTypeId);
		
		String type = vR.getProperty(RGC.TYPE);
		
		r.setType(RefsetType.valueOf(type));
		
		String typeId = vR.getProperty(RGC.TYPE_ID);
		r.setTypeId(typeId);
		
		Iterable<Edge> eRs = vR.getEdges(Direction.IN, "members");
		
		if(eRs != null) {
			
			List<Member> members = new ArrayList<Member>();
			
			for (Edge eR : eRs) {
				
				Vertex vM = eR.getVertex(Direction.OUT);
				
				Member m = new Member();
				String lId = vM.getProperty(RGC.ID);

				m.setId(lId);

				String mModuleId = vM.getProperty(RGC.MODULE_ID);
				m.setModuleId(mModuleId);
				
				boolean isActive = vM.getProperty(RGC.ACTIVE);
				m.setActive(isActive);
				
				String effectivetime = vM.getProperty(RGC.EFFECTIVE_DATE);
				m.setEffectiveTime(effectivetime);
				
				String referenceComponentId = vM.getProperty(RGC.REFERENCE_COMPONENT_ID);
				m.setReferenceComponentId(referenceComponentId);
				
				members.add(m);
			}
			
			r.setMembers(members);

		}

		
		LOGGER.debug("Returning Refset as {} ", r.toString());

		return r;
		
	}
	
	/** TODO this need revisiting
	 * @param date
	 * @return
	 */
	private static DateTime getDateTime(String date) {
		String tmp = date;
		
		if(!StringUtils.isEmpty(date) && date.contains("Z")) {
			
			tmp = date.replace("Z", "");
			
		}
		
		return LocalDateTime.parse(tmp).toDateTime(DateTimeZone.getDefault());
		
		
	}
}
