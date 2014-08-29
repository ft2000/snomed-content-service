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
import org.ihtsdo.otf.refset.exception.EntityNotFoundException;
import org.ihtsdo.otf.refset.exception.RefsetServiceException;
import org.ihtsdo.otf.refset.graph.RefsetGAO;
import org.ihtsdo.otf.refset.graph.RefsetGraphAccessException;
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
	private RefsetGAO gao;
	
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
		when(gao.getRefset(anyString())).thenReturn(refset);
		

	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link org.ihtsdo.otf.refset.service.RefsetAuthoringServiceImpl#addRefset(org.ihtsdo.otf.refset.domain.Refset)}.
	 * @throws RefsetServiceException 
	 * @throws RefsetGraphAccessException 
	 * @throws EntityNotFoundException 
	 */
	@Test
	public void testAddRefset() throws RefsetServiceException, RefsetGraphAccessException, EntityNotFoundException {

		Refset r = new Refset();
		service.addRefset(r);
		
		verify(gao).addRefset(any(Refset.class));
	}
	

	/**
	 * Test method for {@link org.ihtsdo.otf.refset.service.RefsetAuthoringServiceImpl#addMember(java.lang.String, org.ihtsdo.otf.refset.domain.Member)}.
	 * @throws RefsetServiceException 
	 * @throws RefsetGraphAccessException 
	 * @throws EntityNotFoundException 
	 */
	@Test
	public void testAddMember() throws RefsetServiceException, EntityNotFoundException, RefsetGraphAccessException {
		
		service.addMember("someRefSetId", new Member());
		verify(gao).addRefset(any(Refset.class));
		verify(gao).getRefset(anyString());
		
	}
	
	/**
	 * Test method for {@link org.ihtsdo.otf.refset.service.RefsetAuthoringServiceImpl#addMember(java.lang.String, org.ihtsdo.otf.refset.domain.Member)}.
	 * @throws RefsetServiceException 
	 * @throws RefsetGraphAccessException 
	 * @throws EntityNotFoundException 
	 */
	@Test(expected = RefsetServiceException.class)
	public void testAddMemberWhenGivenRefsetIdNotAvailable() throws RefsetServiceException, EntityNotFoundException, RefsetGraphAccessException {

		doThrow(new EntityNotFoundException("Junit refset id does not exist")).when(gao).getRefset(anyString());

		service.addMember("someRefSetId", new Member());
		verify(gao).getRefset(anyString());

	}
	
	/**
	 * Test method for {@link org.ihtsdo.otf.refset.service.RefsetAuthoringServiceImpl#addMember(java.lang.String, org.ihtsdo.otf.refset.domain.Member)}.
	 * @throws RefsetServiceException 
	 * @throws RefsetGraphAccessException 
	 * @throws EntityNotFoundException 
	 */
	@Test(expected = RefsetServiceException.class)
	public void testAddMemberRefsetGraphAccessException() throws RefsetServiceException, EntityNotFoundException, RefsetGraphAccessException {

		doThrow(new RefsetGraphAccessException("Junit refset graph exception")).when(gao).getRefset(anyString());

		service.addMember("someRefSetId", new Member());

	}
	
	/**
	 * Test method for {@link org.ihtsdo.otf.refset.service.RefsetAuthoringServiceImpl#addMember(java.lang.String, org.ihtsdo.otf.refset.domain.Member)}.
	 * @throws RefsetServiceException 
	 * @throws EntityNotFoundException 
	 * @throws RefsetGraphAccessException 
	 */
	@Test(expected = RefsetServiceException.class)
	public void testAddMemberRefsetGraphAccessExceptionDuringAddRefsetCall() throws RefsetServiceException, EntityNotFoundException, RefsetGraphAccessException {

		doThrow(new RefsetGraphAccessException("Junit refset graph exception")).when(gao).addRefset(any(Refset.class));

		service.addMember("someRefSetId", new Member());

	}
	
	/**
	 * Test method for {@link org.ihtsdo.otf.refset.service.RefsetAuthoringServiceImpl#updateRefset(org.ihtsdo.otf.refset.domain.Refset)}.
	 * @throws RefsetServiceException 
	 * @throws RefsetGraphAccessException 
	 * @throws EntityNotFoundException 
	 */
	@Test
	public void testUpdateRefset() throws RefsetServiceException, RefsetGraphAccessException, EntityNotFoundException {

		Refset r = new Refset();
		service.updateRefset(r);
		
		verify(gao).updateRefset(any(Refset.class));
	}
	
	/**
	 * Test method for {@link org.ihtsdo.otf.refset.service.RefsetAuthoringServiceImpl#updateRefset(org.ihtsdo.otf.refset.domain.Refset)}.
	 * @throws RefsetServiceException 
	 * @throws RefsetGraphAccessException 
	 * @throws EntityNotFoundException 
	 */
	@Test(expected = RefsetServiceException.class)
	public void testUpdateRefsetGraphAccessEXception() throws RefsetServiceException, RefsetGraphAccessException, EntityNotFoundException {

		doThrow(new RefsetGraphAccessException("Junit refset graph exception")).when(gao).updateRefset(any(Refset.class));

		Refset r = new Refset();
		service.updateRefset(r);
		
	}
	
	/**
	 * Test method for {@link org.ihtsdo.otf.refset.service.RefsetAuthoringServiceImpl#updateRefset(org.ihtsdo.otf.refset.domain.Refset)}.
	 * @throws RefsetServiceException 
	 * @throws RefsetGraphAccessException 
	 * @throws EntityNotFoundException 
	 */
	@Test(expected = EntityNotFoundException.class)
	public void testUpdateRefsetEntityNotFoundException() throws RefsetServiceException, RefsetGraphAccessException, EntityNotFoundException {

		doThrow(new EntityNotFoundException("Junit refset not found")).when(gao).updateRefset(any(Refset.class));

		Refset r = new Refset();
		service.updateRefset(r);
		
	}


}
