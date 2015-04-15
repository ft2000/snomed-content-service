/**
 * 
 */
package org.ihtsdo.otf.refset.service.authoring;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.ihtsdo.otf.refset.domain.Member;
import org.ihtsdo.otf.refset.domain.MemberDTO;
import org.ihtsdo.otf.refset.domain.RefsetDTO;
import org.ihtsdo.otf.refset.domain.RefsetStatus;
import org.ihtsdo.otf.refset.exception.EntityAlreadyExistException;
import org.ihtsdo.otf.refset.exception.EntityNotFoundException;
import org.ihtsdo.otf.refset.exception.LockingException;
import org.ihtsdo.otf.refset.exception.RefsetServiceException;
import org.ihtsdo.otf.refset.exception.UpdateDeniedException;
import org.ihtsdo.otf.refset.graph.RefsetGraphAccessException;
import org.ihtsdo.otf.refset.graph.gao.MemberGAO;
import org.ihtsdo.otf.refset.graph.gao.RefsetAdminGAO;
import org.ihtsdo.otf.refset.graph.gao.RefsetGAO;
import org.ihtsdo.otf.snomed.domain.Concept;
import org.ihtsdo.otf.snomed.exception.ConceptServiceException;
import org.ihtsdo.otf.snomed.service.ConceptLookupService;
import org.ihtsdo.otf.snomed.service.RefsetMetadataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * @author 
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
	
	@Autowired
	private RefsetMetadataService mdService;


	/* (non-Javadoc)
	 * @see org.ihtsdo.otf.refset.service.RefsetAdminService#addRefset(org.ihtsdo.otf.refset.domain.Refset)
	 */
	@Override
	public String addRefset(RefsetDTO r) throws RefsetServiceException, EntityAlreadyExistException {
		
		LOGGER.debug("addrefset {}", r);
		
		try {
			
			setOriginCountry(r);
			setSnomedCTExt(r);
			setClinicalDomain(r);
			
			adminGao.addRefset(r);
			return r.getUuid();
			
		} catch (RefsetGraphAccessException e) {

			LOGGER.error("Error during service call", e);
			throw new RefsetServiceException(e.getMessage());
			
		}

	}

	/* (non-Javadoc)
	 * @see org.ihtsdo.otf.refset.service.RefsetAdminService#addMember(java.lang.String, org.ihtsdo.otf.refset.domain.Member)
	 */
	@Override
	public void addMember(String refsetId, MemberDTO m)
			throws RefsetServiceException, EntityAlreadyExistException {
		
		LOGGER.debug("Adding member {} to refset {}", m, refsetId);

		if (m == null || StringUtils.isEmpty(m.getReferencedComponentId())) {
			
			throw new EntityNotFoundException("Invalid member details. Member must have reference component id");
		}
		try {
			
			 String owner = gao.getOwner(refsetId);

			 if (StringUtils.isEmpty(owner) || !owner.equalsIgnoreCase(m.getCreatedBy())) {
				 
				 throw new UpdateDeniedException("Only an owner can remove member from refset");
			 }
			
			 RefsetDTO r = gao.getRefsetHeader(refsetId, -1);
			
			 List<MemberDTO> members = new ArrayList<MemberDTO>();
			 m.setUuid(UUID.randomUUID().toString());
			 if(StringUtils.isEmpty(m.getDescription())) {
				
				 Concept c = lService.getConcept(m.getReferencedComponentId());
				 m.setDescription(c.getLabel());
			 }
			 members.add(m);
			
			
			 r.setMembers(members);

			 LOGGER.debug("Adding member {} to refset {}", m, r);

			 adminGao.addRefset(r);

			 LOGGER.debug("Added member {} to refset {}", m, r);

			
		} catch (RefsetGraphAccessException e) {
			// TODO Auto-generated catch block
			LOGGER.error("Error during service call", e);
			throw new RefsetServiceException(e.getMessage());

		} catch (ConceptServiceException e) {
			LOGGER.error("Error during service call", e);
			throw new RefsetServiceException(e.getMessage());
		}
		

	}

	@Override
	public String updateRefset(RefsetDTO r) throws RefsetServiceException {
		
		LOGGER.debug("updateRefset member {} to refset {}", r);

		 try {
			 
			 String owner = gao.getOwner(r.getUuid());

			 if (StringUtils.isEmpty(owner) || !owner.equalsIgnoreCase(r.getModifiedBy())) {
				 
				 throw new UpdateDeniedException("Only an owner can remove member from refset");
			 }
			 r = obfuscate(r); 
			 
			 setOriginCountry(r);
			 setSnomedCTExt(r);
			 setClinicalDomain(r);
			 
			 if (!StringUtils.isEmpty(r.getSctId())) {
				 
				 r.setStatus(RefsetStatus.released.toString());
				 
			 } else if(r.isPublished()) {
				 
				 r.setStatus(RefsetStatus.published.toString());

			 }
						
			 adminGao.updateRefset(r);
			
		} catch (RefsetGraphAccessException e) {
			
			LOGGER.error("Error during service call", e);
			throw new RefsetServiceException(e.getMessage());
			
		}
		 
		 return r.getUuid();
	}

	/**Does required update checks and removes fields which can not be updated
	 * @param r
	 * @return
	 * @throws RefsetServiceException
	 */
	private RefsetDTO obfuscate(RefsetDTO r) throws RefsetServiceException {
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
	public void remove(String refsetId, String user) throws RefsetServiceException, LockingException {
		
		LOGGER.debug("remove refset {}", refsetId);

		try {
			
			String owner = gao.getOwner(refsetId);
			if (StringUtils.isEmpty(owner) || !owner.equalsIgnoreCase(user)) {
				
				throw new UpdateDeniedException("Refset can not be deleted as it is not owned by you");
			}
			 
			adminGao.lock(refsetId);
			
			adminGao.removeRefset(refsetId, user);
			
		} catch (RefsetGraphAccessException e) {
			
			try {
				
				adminGao.removeLock(refsetId);
				
			} catch (RefsetGraphAccessException e1) {
				
				LOGGER.error("Lock on refset {} can not be removed. "
						+ "One has to manually remove to that lock ie delete lock property on this refset", refsetId, e1);

			}
			
			LOGGER.error("Error during service call", e);
			
			throw new RefsetServiceException(e.getMessage());
			
		}
		
	}


	/* (non-Javadoc)
	 * @see org.ihtsdo.otf.refset.service.RefsetAuthoringService#removeMemberFromRefset(java.lang.String, java.lang.String)
	 */
	@Override
	public void removeMemberFromRefset(String refsetId, String rcId, String user)
			throws RefsetServiceException {

		LOGGER.debug("removeMemberFromRefset member {} from refset {}", rcId, refsetId);

		if (StringUtils.isEmpty(refsetId) || StringUtils.isEmpty(rcId)) {
			
			throw new EntityNotFoundException("Invalid request check refset id and member's reference component id. Both are required");
		}
		
		
		
		try {
			
			String owner = gao.getOwner(refsetId);
			if (StringUtils.isEmpty(owner) || !owner.equalsIgnoreCase(user)) {
				
				throw new UpdateDeniedException("Only an owner can remove member from refset");
			}
			
			mGao.removeMember(refsetId, rcId, user);

		} catch (RefsetGraphAccessException e) {

			LOGGER.error("Error during service call", e);
			throw new RefsetServiceException(e.getMessage());

		}
		

	}
	
	
	/* (non-Javadoc)
	 * @see org.ihtsdo.otf.refset.service.RefsetAuthoringService#addMembers(java.lang.String, java.util.Set, java.lang.String)
	 */
	@Override
	public Map<String, String> removeMembers(String refsetId, Set<String> conceptIds, String user)
			throws RefsetServiceException {
		

		Map<String, String> tOutcome = new HashMap<String, String>();
		
		LOGGER.debug("Removing members {} from refset {}", conceptIds, refsetId);

		try {
			
			String owner = gao.getOwner(refsetId);
			if (StringUtils.isEmpty(owner) || !owner.equalsIgnoreCase(user)) {
				
				throw new UpdateDeniedException("Only an owner can remove member from refset");
			}
			
			Map<String, String> outcome = mGao.removeMembers(refsetId, conceptIds, user);
			
			tOutcome.putAll(outcome);


			
		} catch (RefsetGraphAccessException e) {

			LOGGER.error("Error during service call", e);
			throw new RefsetServiceException(e.getMessage());

		}
		

		return tOutcome;
		
	}
	
	/* (non-Javadoc)
	 * @see org.ihtsdo.otf.refset.service.RefsetAuthoringService#addMembers(java.lang.String, java.util.Set)
	 */
	@Override
	public Map<String, String> addMembers(String refsetId, Set<MemberDTO> memberDTOs, String user)
			throws RefsetServiceException {
		

		Map<String, String> tOutcome = new HashMap<String, String>();
		
		LOGGER.debug("Adding members {} to refset {}", memberDTOs, refsetId);

		try {			
			
			String owner = gao.getOwner(refsetId);
			if (StringUtils.isEmpty(owner) || !owner.equalsIgnoreCase(user)) {
				
				throw new UpdateDeniedException("Only an owner of refset can add new member");
			}
			
			Set<Member> members = getMembers(memberDTOs);
			
			LOGGER.debug("Adding member {} to refset {}", members, refsetId);
			//add 100 members at a time. Otherwise when large number of members being added transaction is slow due to too many vertices/edges
			for (List<Member> ms : Iterables.partition(members, 100)) {
				
				Map<String, String> outcome = mGao.addMembers(refsetId, Sets.newHashSet(ms), user);
				tOutcome.putAll(outcome);
			}


			LOGGER.debug("Added member to refset");

			
		} catch (RefsetGraphAccessException e) {

			LOGGER.error("Error during service call", e);
			throw new RefsetServiceException(e.getMessage());

		} 
		

		return tOutcome;
		
	}
	
	/**
	 * @param memberDTOs
	 * @return
	 */
	private Set<Member> getMembers(Set<MemberDTO> memberDTOs) {

		Set<Member> members = new HashSet<Member>();
		
		members.addAll(memberDTOs);
		return members;
	}

	/**
	 * @param r
	 */
	private void setOriginCountry(RefsetDTO r) {
		
		if (r != null && !StringUtils.isEmpty(r.getOriginCountryCode())) {
			
			r.setOriginCountry(mdService.getISOCountries().get(r.getOriginCountryCode()));
		}
	}
	
	private void setSnomedCTExt(RefsetDTO r) {
		
		if (r != null && !StringUtils.isEmpty(r.getSnomedCTExtensionNs())) {
			
			r.setSnomedCTExtension(mdService.getExtensions().get(r.getSnomedCTExtensionNs()));
		}
	}
	
	private void setClinicalDomain(RefsetDTO r) {
		
		if (r != null && !StringUtils.isEmpty(r.getClinicalDomainCode())) {
			
			r.setClinicalDomain(mdService.getClinicalDomains().get(r.getClinicalDomainCode()));
		}
	}

}
