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
import org.ihtsdo.otf.refset.service.upload.Rf2Record;
import org.ihtsdo.otf.snomed.domain.Concept;
import org.ihtsdo.otf.snomed.exception.ConceptServiceException;
import org.ihtsdo.otf.snomed.service.ConceptLookupService;
import org.ihtsdo.otf.snomed.service.RefsetMetadataService;
import org.joda.time.DateTime;
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
			
			 RefsetDTO r = gao.getRefsetHeader(refsetId, -1);
			 
			 isOwner(r.getCreatedBy(), m.getCreatedBy());
						 
			 isValidUpdateState(r.getStatus(), "A new member can not be added to a already published or released refset");
			 
			
			 List<MemberDTO> members = new ArrayList<MemberDTO>();
			 m.setUuid(UUID.randomUUID().toString());
			 if(StringUtils.isEmpty(m.getDescription())) {
				
				 Concept c = lService.getConcept(m.getReferencedComponentId(), r.getSnomedCTVersion());
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

	/**
	 * @param status
	 */
	private void isValidUpdateState(String status, String message) {


		if (!RefsetStatus.inProgress.toString().equalsIgnoreCase(status)) {
			 
			 throw new UpdateDeniedException(message);
		 }
	}

	@Override
	public String updateRefset(RefsetDTO r) throws RefsetServiceException {
		
		LOGGER.debug("updateRefset {} to refset {}", r);

		 try {
			 
			 RefsetDTO dto = gao.getRefsetHeader(r.getUuid(), -1);

			 isOwner(dto.getCreatedBy(), r.getModifiedBy());
			 
			 //is snomed ct version is same as earlier.
			 validateSnomedCTVersion(dto, r);
			 r = obfuscate(r); 
			 
			 setOriginCountry(r);
			 setSnomedCTExt(r);
			 setClinicalDomain(r);
			 
			 if (!StringUtils.isEmpty(r.getSctId())) {
				 
				 r.setStatus(RefsetStatus.released.toString());
				 
			 } else {
				 
				 r.setStatus(RefsetStatus.published.toString());

			 }
						
			 adminGao.updateRefset(r);
			
		} catch (RefsetGraphAccessException e) {
			
			LOGGER.error("Error during service call", e);
			throw new RefsetServiceException(e.getMessage());
			
		}
		 
		 return r.getUuid();
	}

	/**Validate if existing status is inProgress and current SNOMED®CT version 
	 * and updated SNOMED®CT version is same
	 * @param dto
	 * @param r
	 */
	private void validateSnomedCTVersion(RefsetDTO existingRefset, RefsetDTO updatedRefset) {
		
		if(RefsetStatus.inProgress.toString().equalsIgnoreCase(existingRefset.getStatus()) 
				&& !StringUtils.isEmpty(updatedRefset.getSnomedCTVersion()) 
				&& !updatedRefset.getSnomedCTVersion().equals(existingRefset.getSnomedCTVersion())) {
			
			 throw new UpdateDeniedException("Selected SNOMED®CT version can not be changed while authoring refset");

		}
		
		
	}

	/**Checks if modifying user is same as owner of refset
	 * @param owner
	 * @param modifiedBy
	 */
	private void isOwner(String owner, String modifiedBy) {
		
		LOGGER.debug("isOwner owner : {} and modifiedBy : {}", owner, modifiedBy);

		if (StringUtils.isEmpty(owner) || !owner.equalsIgnoreCase(modifiedBy)) {
			
			 throw new UpdateDeniedException("Only an owner can update refset");

		}
		
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
			RefsetDTO r = gao.getRefsetHeader(refsetId, -1);
			
			isOwner(r.getCreatedBy(), user);
			
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
			
			RefsetDTO r = gao.getRefsetHeader(refsetId, -1);
			
			isOwner(r.getCreatedBy(), user);
			 
			isValidUpdateState(r.getStatus(), "Member can not be removed from already published/released refsets");
			
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
			
			RefsetDTO r = gao.getRefsetHeader(refsetId, -1);
			
			isOwner(r.getCreatedBy(), user);
			 
			isValidUpdateState(r.getStatus(), "Member can not be removed from already published/released refsets");
						 
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
			
			RefsetDTO r = gao.getRefsetHeader(refsetId, -1);
			
			isOwner(r.getCreatedBy(), user);
			 
			isValidUpdateState(r.getStatus(), "No new member is allowed to be added in already published/released refsets");
			
			Set<Member> members = getMembers(memberDTOs);
			
			LOGGER.debug("Adding member {} to refset {}", members, refsetId);
			//add 100 members at a time. Otherwise when large number of members being added transaction is slow due to too many vertices/edges
			for (List<Member> ms : Iterables.partition(members, 100)) {
				
				Map<String, String> outcome = mGao.addMembers(refsetId, Sets.newHashSet(ms), user, r.getSnomedCTVersion());
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

	/* (non-Javadoc)
	 * @see org.ihtsdo.otf.refset.service.authoring.RefsetAuthoringService#addMembers(java.util.List, java.lang.String, java.lang.String)
	 */
	@Override
	public Map<String, String> addMembers(List<Rf2Record> rf2rLst,
			String refsetId, String user) throws EntityNotFoundException, RefsetGraphAccessException {

		RefsetDTO r = gao.getRefsetHeader(refsetId, -1);
		
		isOwner(r.getCreatedBy(), user);
		 
		//isValidUpdateState(r.getStatus(), "No new member is allowed to be added in already published/released refsets");
		
		return adminGao.addMembers(rf2rLst, refsetId, user, r.getSnomedCTVersion());
	}

	/* (non-Javadoc)
	 * @see org.ihtsdo.otf.refset.service.authoring.RefsetAuthoringService#addMembersByConceptIds(java.lang.String, java.util.Set, java.lang.String)
	 */
	@Override
	public Map<String, String> addMembersByConceptIds(String refsetId,
			Set<String> conceptIds, String user) throws RefsetServiceException,
			EntityNotFoundException {

		RefsetDTO r;
		try {
			r = gao.getRefsetHeader(refsetId, -1);
		
			Set<MemberDTO> members = new HashSet<MemberDTO>();
			
			for (List<String> ms : Iterables.partition(conceptIds, 100)) {
				
				Set<String> ids = new HashSet<String>();
				ids.addAll(ms);
				
				Map<String, Concept> concepts = lService.getConcepts(ids, r.getSnomedCTVersion());
				
				Set<String> keys = concepts.keySet();
				for (String key : keys) {
					
					Concept c = concepts.get(key);
					if (c != null) {
						
						MemberDTO m = new MemberDTO();
						m.setUuid(UUID.randomUUID().toString());
						m.setCreated(new DateTime());
						m.setCreatedBy(user);
						m.setModifiedBy(user);
						m.setModifiedDate(new DateTime());
						m.setActive(true);
						m.setReferencedComponentId(c.getId());
						m.setDescription(c.getLabel());
						m.setModuleId(c.getModuleId());
						members.add(m);
						
					}
				}
				
			}
			
			addMembers(refsetId, members, user);
		
		} catch (RefsetGraphAccessException | ConceptServiceException e) {
			
			LOGGER.error("Error during service call", e);
			throw new RefsetServiceException(e.getMessage());
		}
		return null;
	}
	
	
	

}
