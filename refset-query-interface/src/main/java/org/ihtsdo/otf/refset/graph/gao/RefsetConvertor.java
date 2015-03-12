/**
 * 
 */
package org.ihtsdo.otf.refset.graph.gao;

import static org.ihtsdo.otf.refset.domain.RGC.*;

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
import com.tinkerpop.gremlin.java.GremlinPipeline;

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
			
			r.setUuid(vR.getId());
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
		
		if ( keys.contains(EXPECTED_PUBLISH_DATE) ) {
			
			r.setExpectedReleaseDate(new DateTime(vR.getExpectedPublishDate()));

		}
		
		if ( keys.contains(SCTID) ) {
			
			r.setSctId(vR.getSctId());

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
		
		//new fields post MVP
		if ( keys.contains(SCOPE) ) {
					
			r.setScope(vR.getScope());
		
		}
		
		if ( keys.contains(SNOMED_CT_EXT) ) {
			
			r.setSnomedCTExtension(vR.getSnomedCTExtension());
		
		}
		
		if ( keys.contains(SNOMED_CT_VERSION) ) {
			
			r.setSnomedCTVersion(vR.getSnomedCTVersion());
		
		}
		
		if ( keys.contains(ORIGIN_COUNTRY) ) {
			
			r.setOriginCountry(vR.getOriginCountry());
		
		}
		
		if ( keys.contains(IMPLEMENTATION_DETAILS) ) {
			
			r.setImplementationDetails(vR.getImplementationDetails());
		
		}
		
		if ( keys.contains(CONTRIBUTING_ORG) ) {
			
			r.setContributingOrganization(vR.getContributingOrganization());
		
		}

		if ( keys.contains(CLINICAL_DOMAIN) ) {
			
			r.setClinicalDomain(vR.getClinicalDomain());
		
		}
		
		if ( keys.contains(VIEW_COUNT) ) {
					
			r.getMatrix().setViews(vR.getViews());
				
		}
		
		if ( keys.contains(DOWNLOAD_COUNT) ) {
			
			r.getMatrix().setDownloads(vR.getDownloads());
		
		}
		
		if ( keys.contains(EXT_URL) ) {
			
			r.setExternalUrl(vR.getExternalUrl());
		
		}
		
		if ( keys.contains(EXT_CONTACT) ) {
			
			r.setExternalContact(vR.getExternalContact());
		
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

				
				//populate memberHasPublishedState flag as "0" or "1" "0" (for if this member has not been published any time) 
				//and 1 (if this member has been published before). this is used to show that this member has previous states
				Integer memberPublishedHistory = populateMemberPublishedStateHistoryFlag(vM);
				Integer memberHashPublishedState = m.isPublished() ? 1 : memberPublishedHistory;
				m.setMemberHasPublishedState(memberHashPublishedState);
				
				//populate memberHasPendingEdit flag as "0" or "1" "0" if this has no unpublished details. 1 has unpublished details
				Integer memberHasPendingEdit = m.isPublished() ? 0 : 1; 
				
				m.setMemberHasPendingEdit(memberHasPendingEdit);
				
				m.setMemberHasPublishedStateHistory(memberPublishedHistory);

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
	private static Integer populateMemberPublishedStateHistoryFlag(Vertex vM) {

		//EdgeLabel.members.toString()).outV()
		//.has(ID, T.eq, id).outE(EdgeLabel.hasState.toString())
		//.has(END, T.lte, toDate.getMillis())
		//.has(START, T.gte, fromDate.getMillis())
		//.inV().has(ACTIVE).has(TYPE, VertexType.hMember.toString()
		
		if (vM != null) {
			
			final GremlinPipeline<Vertex, Vertex> fPipe = new GremlinPipeline<Vertex, Vertex>();			
			fPipe.start(vM).outE(EdgeLabel.hasState.toString())
				.inV().has(ACTIVE).has(TYPE, VertexType.hMember.toString());
			List<Vertex> fls = fPipe.toList();
			if (!fls.isEmpty()) {
				
				return 1;
				
			}
		}
		
		return 0;

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

			m.setUuid(lId);
			
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
			
			r.setUuid(id);
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
		
		if ( keys.contains(EXPECTED_PUBLISH_DATE) ) {
			
			r.setExpectedReleaseDate(new DateTime(vR.getProperty(EXPECTED_PUBLISH_DATE)));

		}
		
		if ( keys.contains(SCTID) ) {
			
			String sctid = vR.getProperty(SCTID);

			r.setSctId(sctid);

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
		
		//new fields post MVP
		if ( keys.contains(SCOPE) ) {
			
			String scope = vR.getProperty(SCOPE);
			r.setScope(scope);
		
		}
		
		if ( keys.contains(SNOMED_CT_EXT) ) {
			
			String snomedCTExtension = vR.getProperty(SNOMED_CT_EXT);
			r.setSnomedCTExtension(snomedCTExtension);
		
		}
		
		if ( keys.contains(SNOMED_CT_VERSION) ) {
			
			String snomedCTVersion = vR.getProperty(SNOMED_CT_VERSION);
			r.setSnomedCTVersion(snomedCTVersion);
		
		}
		
		if ( keys.contains(ORIGIN_COUNTRY) ) {
			
			String originCountry = vR.getProperty(ORIGIN_COUNTRY);
			r.setOriginCountry(originCountry);
		
		}
		
		if ( keys.contains(IMPLEMENTATION_DETAILS) ) {
			
			String implementationDetails = vR.getProperty(IMPLEMENTATION_DETAILS);
			r.setImplementationDetails(implementationDetails);
		
		}
		
		if ( keys.contains(CONTRIBUTING_ORG) ) {
			
			String contributingOrganization = vR.getProperty(CONTRIBUTING_ORG);
			r.setContributingOrganization(contributingOrganization);
		
		}
		
		if ( keys.contains(CLINICAL_DOMAIN) ) {
			
			String clinicalDomain = vR.getProperty(CLINICAL_DOMAIN);
			r.setClinicalDomain(clinicalDomain);
		
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


	/**
	 * @param ls
	 * @return
	 */
	public static List<Refset> getStateRefsets(List<Vertex> ls) {
	
		List<Refset> history = new ArrayList<Refset>();
		if (ls != null && !ls.isEmpty()) {
			
			for (Vertex vR : ls) {
				
				Refset r = getRefset(vR);

				LOGGER.trace("Adding history refset as {} ", r.toString());

				history.add(r);

			}
		}
		return history;
	}


	/**
	 * @param fls
	 * @return
	 */
	public static List<Member> getStateMembers(List<Vertex> fls) {
		
		List<Member> members = new ArrayList<Member>();

		if(fls != null) {
			
			for (Vertex vM : fls) {
							
				Member m = getMember(vM);
				LOGGER.trace("Adding member as {} ", m.toString());

				members.add(m);
			
			}
			
		}
		
		Collections.sort(members);
		return members;
	}

}
