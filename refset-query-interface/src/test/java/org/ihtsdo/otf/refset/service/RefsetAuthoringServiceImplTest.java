/**
 * 
 */
package org.ihtsdo.otf.refset.service;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.List;

import javax.annotation.Resource;

import org.ihtsdo.otf.refset.domain.Member;
import org.ihtsdo.otf.refset.domain.MetaData;
import org.ihtsdo.otf.refset.domain.Refset;
import org.ihtsdo.otf.refset.exception.EntityAlreadyExistException;
import org.ihtsdo.otf.refset.exception.EntityNotFoundException;
import org.ihtsdo.otf.refset.exception.RefsetServiceException;
import org.ihtsdo.otf.refset.graph.RefsetGraphAccessException;
import org.ihtsdo.otf.refset.graph.gao.RefsetAdminGAO;
import org.ihtsdo.otf.refset.graph.gao.RefsetGAO;
import org.ihtsdo.otf.refset.service.authoring.RefsetAuthoringServiceImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * @author Episteme Partners
 *
 */
public class RefsetAuthoringServiceImplTest {
	
	
	@InjectMocks
	@Resource
	private RefsetAuthoringServiceImpl service;
	
	@Mock
	private RefsetAdminGAO gao;
	
	@Mock
	private RefsetGAO rGao;
	
	@Mock
	Refset refset;
	
	@Mock
	List<Refset> refsets;
	
	MetaData md;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		
		md = new MetaData();
		
		MockitoAnnotations.initMocks(this);
		
		when(gao.addRefset(any(Refset.class))).thenReturn(md);
		when(gao.updateRefset(any(Refset.class))).thenReturn(md);

		when(refset.getCreatedBy()).thenReturn("Junit");
		when(rGao.getRefset(anyString())).thenReturn(refset);
		when(refset.getDescription()).thenReturn("junit test refset");
		

	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link org.ihtsdo.otf.refset.service.authoring.RefsetAuthoringServiceImpl#addRefset(org.ihtsdo.otf.refset.domain.Refset)}.
	 * @throws RefsetServiceException 
	 * @throws RefsetGraphAccessException 
	 * @throws EntityNotFoundException 
	 * @throws EntityAlreadyExistException 
	 */
	@Test
	public void testAddRefset() throws RefsetServiceException, RefsetGraphAccessException, EntityNotFoundException, EntityAlreadyExistException {

		Refset r = new Refset();
		service.addRefset(r);
		
		verify(gao).addRefset(any(Refset.class));
	}
	

	/**
	 * Test method for {@link org.ihtsdo.otf.refset.service.authoring.RefsetAuthoringServiceImpl#addMember(java.lang.String, org.ihtsdo.otf.refset.domain.Member)}.
	 * @throws RefsetServiceException 
	 * @throws RefsetGraphAccessException 
	 * @throws EntityNotFoundException 
	 * @throws EntityAlreadyExistException 
	 */
	@Test
	public void testAddMember() throws RefsetServiceException, EntityNotFoundException, RefsetGraphAccessException, EntityAlreadyExistException {
		Member m  = new Member();
		m.setReferencedComponentId("someid");
		service.addMember("someRefSetId", m);
		verify(gao).addRefset(any(Refset.class));
		verify(rGao).getRefset(anyString());
		
	}
	
	/**
	 * Test method for {@link org.ihtsdo.otf.refset.service.authoring.RefsetAuthoringServiceImpl#addMember(java.lang.String, org.ihtsdo.otf.refset.domain.Member)}.
	 * @throws RefsetServiceException 
	 * @throws RefsetGraphAccessException 
	 * @throws EntityNotFoundException 
	 * @throws EntityAlreadyExistException 
	 */
	@Test(expected = EntityNotFoundException.class)
	public void testAddMemberWhenGivenRefsetIdNotAvailable() throws RefsetServiceException, EntityNotFoundException, RefsetGraphAccessException, EntityAlreadyExistException {

		doThrow(new EntityNotFoundException("Junit refset id does not exist")).when(rGao).getRefset(anyString());
		Member m  = new Member();
		m.setReferencedComponentId("someid");

		service.addMember("someRefSetId", m);
		verify(rGao).getRefset(anyString());

	}
	
	/**
	 * Test method for {@link org.ihtsdo.otf.refset.service.authoring.RefsetAuthoringServiceImpl#addMember(java.lang.String, org.ihtsdo.otf.refset.domain.Member)}.
	 * @throws RefsetServiceException 
	 * @throws RefsetGraphAccessException 
	 * @throws EntityNotFoundException 
	 * @throws EntityAlreadyExistException 
	 */
	@Test(expected = RefsetServiceException.class)
	public void testAddMemberRefsetGraphAccessException() throws RefsetServiceException, EntityNotFoundException, RefsetGraphAccessException, EntityAlreadyExistException {

		doThrow(new RefsetGraphAccessException("Junit refset graph exception")).when(rGao).getRefset(anyString());
		Member m  = new Member();
		m.setReferencedComponentId("someid");

		service.addMember("someRefSetId", m);

	}
	
	/**
	 * Test method for {@link org.ihtsdo.otf.refset.service.authoring.RefsetAuthoringServiceImpl#addMember(java.lang.String, org.ihtsdo.otf.refset.domain.Member)}.
	 * @throws RefsetServiceException 
	 * @throws EntityNotFoundException 
	 * @throws RefsetGraphAccessException 
	 * @throws EntityAlreadyExistException 
	 */
	@Test(expected = RefsetServiceException.class)
	public void testAddMemberRefsetGraphAccessExceptionDuringAddRefsetCall() throws RefsetServiceException, EntityNotFoundException, RefsetGraphAccessException, EntityAlreadyExistException {

		doThrow(new RefsetGraphAccessException("Junit refset graph exception")).when(gao).addRefset(any(Refset.class));
		Member m  = new Member();
		m.setReferencedComponentId("someid");

		service.addMember("someRefSetId", m);

	}
	
	/**
	 * Test method for {@link org.ihtsdo.otf.refset.service.authoring.RefsetAuthoringServiceImpl#updateRefset(org.ihtsdo.otf.refset.domain.Refset)}.
	 * @throws RefsetServiceException 
	 * @throws RefsetGraphAccessException 
	 * @throws EntityNotFoundException 
	 */
	@Test
	public void testUpdateRefset() throws RefsetServiceException, RefsetGraphAccessException, EntityNotFoundException {

		
		service.updateRefset(refset);
		
		verify(gao).updateRefset(any(Refset.class));
	}
	
	/**
	 * Test method for {@link org.ihtsdo.otf.refset.service.authoring.RefsetAuthoringServiceImpl#updateRefset(org.ihtsdo.otf.refset.domain.Refset)}.
	 * @throws RefsetServiceException 
	 * @throws RefsetGraphAccessException 
	 * @throws EntityNotFoundException 
	 */
	@Test(expected = RefsetServiceException.class)
	public void testUpdateRefsetGraphAccessEXception() throws RefsetServiceException, RefsetGraphAccessException, EntityNotFoundException {

		doThrow(new RefsetGraphAccessException("Junit refset graph exception")).when(gao).updateRefset(any(Refset.class));

		service.updateRefset(refset);
		
	}
	
	/**
	 * Test method for {@link org.ihtsdo.otf.refset.service.authoring.RefsetAuthoringServiceImpl#updateRefset(org.ihtsdo.otf.refset.domain.Refset)}.
	 * @throws RefsetServiceException 
	 * @throws RefsetGraphAccessException 
	 * @throws EntityNotFoundException 
	 */
	@Test(expected = EntityNotFoundException.class)
	public void testUpdateRefsetEntityNotFoundException() throws RefsetServiceException, RefsetGraphAccessException, EntityNotFoundException {

		doThrow(new EntityNotFoundException("Junit refset not found")).when(gao).updateRefset(any(Refset.class));

		service.updateRefset(refset);
		
	}


}
