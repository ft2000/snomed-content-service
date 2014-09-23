/**
 * 
 */
package org.ihtsdo.otf.refset.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.ihtsdo.otf.refset.domain.Member;
import org.ihtsdo.otf.refset.domain.Refset;
import org.ihtsdo.otf.refset.exception.EntityNotFoundException;
import org.ihtsdo.otf.refset.exception.RefsetServiceException;
import org.ihtsdo.otf.refset.graph.RefsetGraphAccessException;
import org.ihtsdo.otf.refset.graph.gao.MemberGAO;
import org.ihtsdo.otf.refset.graph.gao.RefsetAdminGAO;
import org.ihtsdo.otf.refset.graph.gao.RefsetGAO;
import org.ihtsdo.otf.snomed.domain.Concept;
import org.ihtsdo.otf.snomed.exception.ConceptServiceException;
import org.ihtsdo.otf.snomed.service.ConceptLookupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @author Episteme Partners
 *
 */
@Service
public class RefsetAuthoringServiceImpl implements RefsetAuthoringService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RefsetAuthoringServiceImpl.class);
	
	@Autowired
	private RefsetGAO gao;
	
	@Autowired
	private RefsetAdminGAO adminGao;
	
	@Autowired
	private MemberGAO mGao;
	
	@Autowired
	private ConceptLookupService lService;


	/* (non-Javadoc)
	 * @see org.ihtsdo.otf.refset.service.RefsetAdminService#addRefset(org.ihtsdo.otf.refset.domain.Refset)
	 */
	@Override
	public String addRefset(Refset r) throws RefsetServiceException {
		
		LOGGER.debug("addrefset {}", r);
		
		try {
			
			adminGao.addRefset(r);
			return r.getId();
			
		} catch (RefsetGraphAccessException e) {

			LOGGER.error("Error during service call {}", e);
			throw new RefsetServiceException(e.getMessage());
			
		}

	}

	/* (non-Javadoc)
	 * @see org.ihtsdo.otf.refset.service.RefsetAdminService#addMember(java.lang.String, org.ihtsdo.otf.refset.domain.Member)
	 */
	@Override
	public void addMember(String refsetId, Member m)
			throws RefsetServiceException, EntityNotFoundException {
		
		LOGGER.debug("Adding member {} to refset {}", m, refsetId);

		if (m == null || StringUtils.isEmpty(m.getReferenceComponentId())) {
			
			throw new EntityNotFoundException("Invalid member details. Member must have reference component id");
		}
		try {
			
			Refset r = gao.getRefset(refsetId);
			
			List<Member> members = new ArrayList<Member>();
			m.setId(UUID.randomUUID().toString());
			members.add(m);
			
			
			r.setMembers(members);

			LOGGER.debug("Adding member {} to refset {}", m, r);

			adminGao.addRefset(r);

			LOGGER.debug("Added member {} to refset {}", m, r);

			
		} catch (EntityNotFoundException e) {

			LOGGER.error("Error during service call", e);

			throw e;
			
		} catch (RefsetGraphAccessException e) {
			// TODO Auto-generated catch block
			LOGGER.error("Error during service call", e);
			throw new RefsetServiceException(e.getMessage());

		}
		

	}

	@Override
	public String updateRefset(Refset r) throws RefsetServiceException, EntityNotFoundException {
		
		LOGGER.debug("updateRefset member {} to refset {}", r);

		 try {
			r = obfuscate(r); 
			adminGao.updateRefset(r);
			
		} catch (RefsetGraphAccessException e) {
			
			LOGGER.error("Error during service call", e);
			throw new RefsetServiceException(e.getMessage());
			
		}
		 
		 return r.getId();
	}

	/**Does required update checks and removes fields which can not be updated
	 * @param r
	 * @return
	 * @throws RefsetServiceException
	 */
	private Refset obfuscate(Refset r) throws RefsetServiceException {
		// TODO Auto-generated method stub
		if (r == null) {
			
			throw new RefsetServiceException("Invalid request, refset supplied can not be empty");
			
		} else if (StringUtils.isEmpty(r.getDescription())) {
			
			throw new RefsetServiceException("Invalid request, refset description is mandatory");

		}
		
		r.setCreated(null);
		r.setCreatedBy(null);
		return r;
	}

	@Override
	public void remove(String refsetId) throws RefsetServiceException,
			EntityNotFoundException {
		
		LOGGER.debug("remove refset {}", refsetId);

		try {
			 
			adminGao.removeRefset(refsetId);
			
		} catch (RefsetGraphAccessException e) {
			
			LOGGER.error("Error during service call", e);
			throw new RefsetServiceException(e.getMessage());
			
		}
		
	}

	/* (non-Javadoc)
	 * @see org.ihtsdo.otf.refset.service.RefsetAuthoringService#addMembers(java.lang.String, java.util.Set)
	 */
	@Override
	public Map<String, String> addMembers(String refsetId, Set<String> conceptIds)
			throws RefsetServiceException, EntityNotFoundException {
		

		Map<String, String> tOutcome = new HashMap<String, String>();
		
		LOGGER.debug("Adding member {} to refset {}", conceptIds, refsetId);

		try {
			
			Map<String, Concept> concepts = lService.getConcepts(conceptIds);
			
			
			List<Member> members = new ArrayList<Member>();
			
			for (String id : conceptIds) {
				
				Concept c = concepts.get(id);
				
				if (c != null) {
					
					Member m = new Member();
					m.setId(UUID.randomUUID().toString());
					m.setActive(c.isActive());
					m.setEffectiveTime(c.getEffectiveTime());
					m.setModuleId(c.getModule());
					m.setReferenceComponentId(c.getId());
					
					LOGGER.debug("Adding member {} to member list for id {}", m, id);

					members.add(m);

				} else {
					
					LOGGER.debug("Not adding member details as no concept details available for id {}", id);
					tOutcome.put(id, "Member details not available");

				}
				
			}
			
			LOGGER.debug("Adding member {} to refset {}", members, refsetId);

			Map<String, String> outcome = mGao.addMembers(refsetId, members);
			tOutcome.putAll(outcome);

			LOGGER.debug("Added member {} to refset {}");

			
		} catch (EntityNotFoundException e) {

			LOGGER.error("Error during service call", e);

			throw e;
			
		} catch (RefsetGraphAccessException e) {

			LOGGER.error("Error during service call", e);
			throw new RefsetServiceException(e.getMessage());

		} catch (ConceptServiceException e) {

			LOGGER.error("Error during service call", e);

			throw new RefsetServiceException(e.getMessage());
		}
		

		return tOutcome;
		
	}

	/* (non-Javadoc)
	 * @see org.ihtsdo.otf.refset.service.RefsetAuthoringService#removeMemberFromRefset(java.lang.String, java.lang.String)
	 */
	@Override
	public void removeMemberFromRefset(String refsetId, String rcId)
			throws RefsetServiceException, EntityNotFoundException {

		LOGGER.debug("removeMemberFromRefset member {} from refset {}", rcId, refsetId);

		if (StringUtils.isEmpty(refsetId) || StringUtils.isEmpty(rcId)) {
			
			throw new EntityNotFoundException("Invalid request check refset id and member's reference component id. Both are required");
		}
		
		
		try {
			
			mGao.removeMember(refsetId, rcId);

		} catch (EntityNotFoundException e) {

			LOGGER.error("Error during service call", e);

			throw e;
			
		} catch (RefsetGraphAccessException e) {

			LOGGER.error("Error during service call", e);
			throw new RefsetServiceException(e.getMessage());

		}
		

	}

}
