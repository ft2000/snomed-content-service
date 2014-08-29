/**
 * 
 */
package org.ihtsdo.otf.refset.service;

import java.util.ArrayList;
import java.util.List;

import org.ihtsdo.otf.refset.domain.Member;
import org.ihtsdo.otf.refset.domain.Refset;
import org.ihtsdo.otf.refset.exception.EntityNotFoundException;
import org.ihtsdo.otf.refset.exception.RefsetServiceException;
import org.ihtsdo.otf.refset.graph.RefsetGAO;
import org.ihtsdo.otf.refset.graph.RefsetGraphAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Episteme Partners
 *
 */
@Service
public class RefsetAuthoringServiceImpl implements RefsetAuthoringService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RefsetAuthoringServiceImpl.class);
	
	@Autowired
	private RefsetGAO gao;


	/* (non-Javadoc)
	 * @see org.ihtsdo.otf.refset.service.RefsetAdminService#addRefset(org.ihtsdo.otf.refset.domain.Refset)
	 */
	@Override
	public String addRefset(Refset r) throws RefsetServiceException {
		
		LOGGER.debug("addrefset {}", r);
		
		try {
			
			gao.addRefset(r);
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
			throws RefsetServiceException {
		
		try {
			
			Refset r = gao.getRefset(refsetId);
			
			List<Member> members = new ArrayList<Member>();
			members.add(m);
			
			r.setMembers(members);

			LOGGER.debug("Adding member {} to refset {}", m, r);

			gao.addRefset(r);

			LOGGER.debug("Added member {} to refset {}", m, r);

			
		} catch (EntityNotFoundException e) {

			LOGGER.error("Error during service call", e);

			throw new RefsetServiceException(e.getMessage());
			
		} catch (RefsetGraphAccessException e) {
			// TODO Auto-generated catch block
			LOGGER.error("Error during service call", e);
			throw new RefsetServiceException(e.getMessage());

		}
		

	}

	@Override
	public String updateRefset(Refset r) throws RefsetServiceException, EntityNotFoundException {
		
		 try {
			 
			gao.updateRefset(r);
			
		} catch (RefsetGraphAccessException e) {
			
			LOGGER.error("Error during service call", e);
			throw new RefsetServiceException(e.getMessage());
			
		}
		 
		 return r.getId();
	}

}
