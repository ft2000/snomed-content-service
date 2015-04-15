/**
 * 
 */
package org.ihtsdo.otf.refset.service.authoring;

import java.util.Map;
import java.util.Set;

import org.ihtsdo.otf.refset.domain.MemberDTO;
import org.ihtsdo.otf.refset.domain.MetaData;
import org.ihtsdo.otf.refset.domain.RefsetDTO;
import org.ihtsdo.otf.refset.exception.EntityAlreadyExistException;
import org.ihtsdo.otf.refset.exception.EntityNotFoundException;
import org.ihtsdo.otf.refset.exception.LockingException;
import org.ihtsdo.otf.refset.exception.RefsetServiceException;

/**Service to support {@link Refset} authoring
 * @author 
 *
 */
public interface RefsetAuthoringService {
	
	/**Method to add a {@link RefsetDTO}
	 * @param r a {@link RefsetDTO}
	 * @throws RefsetServiceException
	 * @return a {@link RefsetDTO} object stored in graph with {@link MetaData}
	 * @throws EntityAlreadyExistException 
	 */
	public String addRefset(RefsetDTO r) throws RefsetServiceException, EntityAlreadyExistException ;
	
	/**Method to add a {@link Member} to given {@link RefsetDTO}.
	 * @param refsetId
	 * @throws RefsetServiceException
	 * @throws EntityNotFoundException 
	 * @throws EntityAlreadyExistException 
	 */
	public void addMember(String refsetId, MemberDTO m) throws RefsetServiceException, EntityNotFoundException, EntityAlreadyExistException ;
	
	
	/**Method to add a {@link RefsetDTO}
	 * @param r a {@link RefsetDTO}
	 * @throws RefsetServiceException
	 * @return a {@link RefsetDTO} object stored in graph with {@link MetaData}
	 */
	public String updateRefset(RefsetDTO r) throws RefsetServiceException, EntityNotFoundException ;
	
	/**Method to add a {@link Member} to given {@link Refset}.
	 * @param refsetId
	 * @throws RefsetServiceException
	 * @throws LockingException 
	 */
	public void remove(String refsetId, String user) throws RefsetServiceException, EntityNotFoundException, LockingException ;
	
	
	/**Method to add a list of {@link Member}s to given {@link Refset}.
	 * @param refsetId
	 * @param user
	 * @return 
	 * @throws RefsetServiceException
	 * @throws EntityNotFoundException 
	 */
	public Map<String, String> addMembers(String refsetId,Set<MemberDTO> members, String user) throws RefsetServiceException, EntityNotFoundException ;
	
	/** removes a {@link Member} from {@link Refset}
	 * @param refsetId
	 * @param rcId
	 */
	public void removeMemberFromRefset(String refsetId, String rcId, String user) throws RefsetServiceException, EntityNotFoundException ;

	/**
	 * @param refsetId
	 * @param conceptIds
	 * @param user 
	 * @return
	 * @throws RefsetServiceException
	 * @throws EntityNotFoundException
	 */
	Map<String, String> removeMembers(String refsetId, Set<String> conceptIds, String user)
			throws RefsetServiceException, EntityNotFoundException;


}
