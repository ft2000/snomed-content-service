/**
 * 
 */
package org.ihtsdo.otf.refset.graph.gao;

import static org.ihtsdo.otf.refset.domain.RGC.ACTIVE;
import static org.ihtsdo.otf.refset.domain.RGC.CREATED;
import static org.ihtsdo.otf.refset.domain.RGC.CREATED_BY;
import static org.ihtsdo.otf.refset.domain.RGC.DESC;
import static org.ihtsdo.otf.refset.domain.RGC.EFFECTIVE_DATE;
import static org.ihtsdo.otf.refset.domain.RGC.EXPECTED_RLS_DT;
import static org.ihtsdo.otf.refset.domain.RGC.ID;
import static org.ihtsdo.otf.refset.domain.RGC.LANG_CODE;
import static org.ihtsdo.otf.refset.domain.RGC.MEMBER_TYPE_ID;
import static org.ihtsdo.otf.refset.domain.RGC.MODIFIED_BY;
import static org.ihtsdo.otf.refset.domain.RGC.MODIFIED_DATE;
import static org.ihtsdo.otf.refset.domain.RGC.MODULE_ID;
import static org.ihtsdo.otf.refset.domain.RGC.PUBLISHED;
import static org.ihtsdo.otf.refset.domain.RGC.PUBLISHED_DATE;
import static org.ihtsdo.otf.refset.domain.RGC.REFERENCE_COMPONENT_ID;
import static org.ihtsdo.otf.refset.domain.RGC.SCTID;
import static org.ihtsdo.otf.refset.domain.RGC.SUPER_REFSET_TYPE_ID;
import static org.ihtsdo.otf.refset.domain.RGC.TYPE_ID;
import static org.ihtsdo.otf.refset.domain.RGC.END;
import static org.ihtsdo.otf.refset.domain.RGC.E_EFFECTIVE_TIME;
import static org.ihtsdo.otf.refset.domain.RGC.L_EFFECTIVE_TIME;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.ihtsdo.otf.refset.domain.Member;
import org.ihtsdo.otf.refset.domain.MetaData;
import org.ihtsdo.otf.refset.domain.Refset;
import org.ihtsdo.otf.refset.graph.schema.GRefset;
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


	/**
	 * @param vs.
	 * @return
	 */
	public static List<Refset> getRefsets(Iterable<GRefset> vs) {

		List<Refset> refsets = new ArrayList<Refset>();

		for (GRefset gr : vs) {
			
			Set<String> keys = gr.asVertex().getPropertyKeys();
			
			if (!StringUtils.isEmpty(gr.getId()) && keys.contains(DESC) ) {
				
				Refset r = getRefset(gr);

				refsets.add(r);
				
			}
			
		}
		Collections.sort(refsets);
		Collections.reverse(refsets);

		return Collections.unmodifiableList(refsets);
	}


	/**
	 * @param v
	 * @return
	 */
	public static Refset convert2Refset(GRefset vR) {


		
		LOGGER.debug("convert2Refsets {}", vR);

		Refset r = getRefset(vR);

		Iterable<Edge> eRs = vR.asVertex().getEdges(Direction.IN, "members");
		r.setMembers(getMembers(eRs));
		
		LOGGER.trace("Returning Refset as {} ", r.toString());

		return r;
		
	
	}

	/**
	 * @param asVertex
	 * @return
	 */
	protected static Refset getRefset(GRefset vR) {
		
		Refset r = new Refset();
		Set<String> keys = vR.asVertex().getPropertyKeys();
		
		if ( keys.contains(CREATED) ) {
		
			r.setCreated(new DateTime(vR.getCreated()));
		}
		
		if ( keys.contains(CREATED_BY) ) {
			
			r.setCreatedBy(vR.getCreatedBy());
			
		}
		
		
		if ( keys.contains(DESC) ) {
			
			r.setDescription(vR.getDescription());
			
		}
		
		if ( keys.contains(EFFECTIVE_DATE) ) {
			
			r.setEffectiveTime(new DateTime(vR.getEffectiveTime()));
			
		}
		
		if ( keys.contains(ID) ) {
			
			r.setId(vR.getId());
		}
		
		if ( keys.contains(LANG_CODE) ) {
			
			r.setLanguageCode(vR.getLanguageCode());
			
		}
		
		if ( keys.contains(MODULE_ID) ) {
			
			r.setModuleId(vR.getModuleId());
			
		}
		
		if ( keys.contains(PUBLISHED) ) {
			
			r.setPublished(vR.getPublished() == 1 ? true : false);
			
		}
		
		
		if ( keys.contains(PUBLISHED_DATE) ) {
			
			r.setPublishedDate(new DateTime(vR.getPublishedDate()));
			
		}
		
		
		if ( keys.contains(SUPER_REFSET_TYPE_ID) ) {
			
			r.setSuperRefsetTypeId(vR.getSuperRefsetTypeId());
			
		}
		

		if ( keys.contains(TYPE_ID) ) {
			
			r.setTypeId(vR.getTypeId());
			
		}

		if ( keys.contains(MEMBER_TYPE_ID) ) {
			
			r.setComponentTypeId(vR.getComponentTypeId());
			
		}
		
		if ( keys.contains(MODIFIED_DATE) ) {
			
			r.setModifiedDate(new DateTime(vR.getModifiedDate()));
		}
		
		if ( keys.contains(MODIFIED_BY) ) {
			
			r.setModifiedBy(vR.getModifiedBy());
			
		}
		
		if ( keys.contains(EXPECTED_RLS_DT) ) {
			
			r.setExpectedReleaseDate(new DateTime(vR.getExpectedReleaseDate()));

		}
		
		if ( keys.contains(SCTID) ) {
			
			r.setId(vR.getSctdId());

		}
		
		if ( keys.contains(ACTIVE) ) {
			
			r.setActive(vR.getActive() == 1 ? true : false);
			
		}
		if (keys.contains("noOfMembers")) {
			
			r.setTotalNoOfMembers(vR.getNoOfMembers());

		}
		
		if ( keys.contains(E_EFFECTIVE_TIME) ) {
			
			r.setEarliestEffectiveTime(new DateTime(vR.getEarliestEffectiveTime()));

		}
		
		if ( keys.contains(L_EFFECTIVE_TIME) ) {
			
			r.setLatestEffectiveTime(new DateTime(vR.getLatestEffectiveTime()));

		}

		return r;
	}


	protected static List<Member> getMembers(Iterable<Edge> eRs ) {
		
		List<Member> members = new ArrayList<Member>();

		if(eRs != null) {
			
			
			for (Edge eR : eRs) {
				
				//only get edges which has LONG.MAX_VALUE end date
				if (eR.getPropertyKeys().contains(END)) {
					
					long end = eR.getProperty(END);
					
					if (!(end == Long.MAX_VALUE)) {
						//we have to get only latest member state not history
						LOGGER.trace("skipping {} as this member is does not have current state", eR.getProperty(ID));
						continue;
					}
					
				} else {
					
					continue;//not a valid member
				}
				
				
				Vertex vM = eR.getVertex(Direction.OUT);
				
				Member m = getMember(vM);
				
				Set<String> eKeys = eR.getPropertyKeys();
				if ( eKeys.contains(REFERENCE_COMPONENT_ID) ) {
					
					String referenceComponentId = eR.getProperty(REFERENCE_COMPONENT_ID);
					m.setReferencedComponentId(referenceComponentId);
					
				}

				


				LOGGER.trace("Adding member as {} ", m.toString());

				members.add(m);
			}
		}
		
		Collections.sort(members);
		return members;
	}
			
	
	
	/**
	 * @param vM
	 */
	protected static Member getMember(Vertex vM) {
		// TODO Auto-generated method stub
		Set<String> mKeys = vM.getPropertyKeys();
		
		Member m = new Member();
		
		if ( mKeys.contains(ID) ) {
			
			String lId = vM.getProperty(ID);

			m.setId(lId);
			
		}
		
		if ( mKeys.contains(MODULE_ID) ) {
			
			
			String mModuleId = vM.getProperty(MODULE_ID);
			m.setModuleId(mModuleId);
			
		}
		
		if (mKeys.contains(EFFECTIVE_DATE)) {
			

			long effectivetime = vM.getProperty(EFFECTIVE_DATE);
			
			LOGGER.trace("Actual effective date when this member tied to given refset is  {} ", effectivetime);

			m.setEffectiveTime(new DateTime(effectivetime));
			
		}
		
		if (mKeys.contains(PUBLISHED)) {
			

			Integer published = vM.getProperty(PUBLISHED);
			
			m.setPublished(published == 1 ? true : false);;
			
		}


		
		
		if ( mKeys.contains(ACTIVE) ) {
			
			Integer isActive = vM.getProperty(ACTIVE);
			m.setActive(isActive == 1 ? true : false);
			
		}
		
		
		if ( mKeys.contains(EFFECTIVE_DATE) ) {
			
			long effectivetime = vM.getProperty(EFFECTIVE_DATE);
			m.setEffectiveTime(new DateTime(effectivetime));
			
		}
		
		if ( mKeys.contains(MODIFIED_DATE) ) {
			
			long modified = vM.getProperty(MODIFIED_DATE);
			m.setModifiedDate(new DateTime(modified));
		}
		
		if ( mKeys.contains(MODIFIED_BY) ) {
			
			String modifiedby = vM.getProperty(MODIFIED_BY);
			m.setModifiedBy(modifiedby);
			
		}
		
		if ( mKeys.contains(CREATED) ) {
			
			m.setCreated(new DateTime(vM.getProperty(CREATED)));
		}
		
		if ( mKeys.contains(CREATED_BY) ) {
			
			String createdBy = vM.getProperty(CREATED_BY);
			m.setCreatedBy(createdBy);
			
		}
		
		return m;
	}


	/** Utility method to create {@link MetaData} object from {@link Vertex} 
	 * @param vertex
	 * @return
	 */
	protected static MetaData getMetaData(Vertex v) {
		
		MetaData md = null;
		
		if( v != null) {
			
			md = new MetaData();
			md.setId(v.getId());
			md.setType(v.getClass().getSimpleName());
			//Integer version = e.getProperty("@Version");
			//md.setVersion(version);
			
		}
		
		return md;
	}


	/**
	 * @param ls
	 * @return
	 */
	public static List<Refset> getHistoryRefsets(List<Edge> ls) {

		List<Refset> history = new ArrayList<Refset>();

		if(ls != null) {
			
			
			for (Edge eR : ls) {
				
				Vertex vR = eR.getVertex(Direction.IN);
				
				Refset r = getRefset(vR);

				LOGGER.trace("Adding history refset as {} ", r.toString());

				history.add(r);
			}
		}
		
		Collections.sort(history);
		return history;

	}


	/**
	 * @param vR
	 * @return
	 */
	private static Refset getRefset(Vertex vR) {

		if (vR == null) {
			
			return null;
		}
		
		Refset r = new Refset();
		Set<String> keys = vR.getPropertyKeys();
		
		if ( keys.contains(CREATED) ) {
		
			r.setCreated(new DateTime(vR.getProperty(CREATED)));
		}
		
		if ( keys.contains(CREATED_BY) ) {
			
			String createdBy = vR.getProperty(CREATED_BY);
			r.setCreatedBy(createdBy);
			
		}
		
		
		if ( keys.contains(DESC) ) {
			
			String description = vR.getProperty(DESC);

			r.setDescription(description);
			
		}
		
		if ( keys.contains(EFFECTIVE_DATE) ) {
			
			r.setEffectiveTime(new DateTime(vR.getProperty(EFFECTIVE_DATE)));
			
		}
		
		if ( keys.contains(ID) ) {
			
			String id = vR.getProperty(ID);
			
			r.setId(id);
		}
		
		if ( keys.contains(LANG_CODE) ) {
			
			String lang = vR.getProperty(LANG_CODE);
			r.setLanguageCode(lang);
			
		}
		
		if ( keys.contains(MODULE_ID) ) {
			
			String moduleId = vR.getProperty(MODULE_ID);
			
			r.setModuleId(moduleId);
			
		}
		
		if ( keys.contains(PUBLISHED) ) {
			
			Integer published = vR.getProperty(PUBLISHED);
			
			r.setPublished(published == 1 ? true : false);
			
		}
		
		
		if ( keys.contains(PUBLISHED_DATE) ) {
			
			r.setPublishedDate(new DateTime(vR.getProperty(PUBLISHED_DATE)));
			
		}
		
		
		if ( keys.contains(SUPER_REFSET_TYPE_ID) ) {
			
			String superRefsetTypeId = vR.getProperty(SUPER_REFSET_TYPE_ID);
			r.setSuperRefsetTypeId(superRefsetTypeId);
			
		}
		

		if ( keys.contains(TYPE_ID) ) {
			
			String typeId = vR.getProperty(TYPE_ID);
			r.setTypeId(typeId);
			
		}

		if ( keys.contains(MEMBER_TYPE_ID) ) {
			
			String memberTypeId = vR.getProperty(MEMBER_TYPE_ID);
			r.setComponentTypeId(memberTypeId);
			
		}
		
		if ( keys.contains(MODIFIED_DATE) ) {
			
			
			r.setModifiedDate(new DateTime(vR.getProperty(MODIFIED_DATE)));
		}
		
		if ( keys.contains(MODIFIED_BY) ) {
			
			String modifiedBy = vR.getProperty(MODIFIED_BY);

			r.setModifiedBy(modifiedBy);
			
		}
		
		if ( keys.contains(EXPECTED_RLS_DT) ) {
			
			r.setExpectedReleaseDate(new DateTime(vR.getProperty(EXPECTED_RLS_DT)));

		}
		
		if ( keys.contains(SCTID) ) {
			
			String sctid = vR.getProperty(SCTID);

			r.setId(sctid);

		}
		
		if ( keys.contains(ACTIVE) ) {
			
			Integer active = vR.getProperty(ACTIVE);

			r.setActive(active == 1 ? true : false);
			
		}
		
		if ( keys.contains(E_EFFECTIVE_TIME) ) {
			
			r.setEarliestEffectiveTime(new DateTime(vR.getProperty(E_EFFECTIVE_TIME)));

		}
		
		if ( keys.contains(L_EFFECTIVE_TIME) ) {
			
			r.setLatestEffectiveTime(new DateTime(vR.getProperty(L_EFFECTIVE_TIME)));

		}
		
		return r;

	}
	
protected static List<Member> getHistoryMembers(Iterable<Edge> eRs ) {
		
		List<Member> members = new ArrayList<Member>();

		if(eRs != null) {
			
			
			for (Edge eR : eRs) {
				
				Vertex vM = eR.getVertex(Direction.IN);
				
				Member m = getMember(vM);
				
				Set<String> eKeys = eR.getPropertyKeys();
				if ( eKeys.contains(REFERENCE_COMPONENT_ID) ) {
					
					String referenceComponentId = eR.getProperty(REFERENCE_COMPONENT_ID);
					m.setReferencedComponentId(referenceComponentId);
					
				}

				LOGGER.trace("Adding member as {} ", m.toString());

				members.add(m);
			}
		}
		
		Collections.sort(members);
		return members;
	}
}
