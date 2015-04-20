/**
* Copyright 2014 IHTSDO
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.ihtsdo.otf.refset.service.upload;

import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.ihtsdo.otf.refset.exception.EntityNotFoundException;
import org.ihtsdo.otf.refset.exception.RefsetServiceException;
import org.ihtsdo.otf.refset.graph.RefsetGraphAccessException;
import org.ihtsdo.otf.refset.service.authoring.RefsetAuthoringServiceImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 *
 */
public class SimpleRefsetProcessorTest {
	
	private SimpleRefsetProcessor srp;
	private RefsetAuthoringServiceImpl service;
	private static List<Rf2Record> refsets = new ArrayList<Rf2Record>();
	static {
		
		
		Rf2Record rf2r = new Rf2Record();
		rf2r.setActive("1");
		rf2r.setCreatedBy("junit");
		refsets.add(rf2r);

	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		
		srp = new SimpleRefsetProcessor();
		service = mock(RefsetAuthoringServiceImpl.class);
		srp.setService(service);
		when(service.addMembers(anyListOf(Rf2Record.class), anyString(), anyString())).thenReturn(new HashMap<String, String>());
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link org.ihtsdo.otf.refset.service.upload.SimpleRefsetProcessor#process(java.util.List, java.lang.String)}.
	 * @throws EntityNotFoundException 
	 * @throws RefsetServiceException 
	 */
	@Test(expected = RefsetServiceException.class)
	public void testProcessNoData() throws RefsetServiceException, EntityNotFoundException {
		
		srp.process(new ArrayList<Rf2Record>(), "junitrefset", "junit");

	}
	
	/**
	 * Test method for {@link org.ihtsdo.otf.refset.service.upload.SimpleRefsetProcessor#process(java.util.List, java.lang.String)}.
	 * @throws EntityNotFoundException 
	 * @throws RefsetServiceException 
	 */
	@Test(expected = RefsetServiceException.class)
	public void testProcessNoRefsetId() throws RefsetServiceException, EntityNotFoundException {
		
		srp.process(new ArrayList<Rf2Record>(), null, "junit");

	}
	
	/**
	 * Test method for {@link org.ihtsdo.otf.refset.service.upload.SimpleRefsetProcessor#process(java.util.List, java.lang.String)}.
	 * @throws EntityNotFoundException 
	 * @throws RefsetServiceException 
	 * @throws RefsetGraphAccessException 
	 */
	@Test
	public void testProcess() throws RefsetServiceException, EntityNotFoundException, RefsetGraphAccessException {

		srp.process(refsets, "junit", "junit");
		
		verify(service).addMembers(anyListOf(Rf2Record.class), anyString(), anyString());
	}
	
	/**
	 * Test method for {@link org.ihtsdo.otf.refset.service.upload.SimpleRefsetProcessor#process(java.util.List, java.lang.String)}.
	 * @throws EntityNotFoundException 
	 * @throws RefsetServiceException 
	 * @throws RefsetGraphAccessException 
	 */
	@Test(expected = RefsetServiceException.class)
	public void testProcessRefsetServiceException() throws RefsetServiceException, EntityNotFoundException, RefsetGraphAccessException {

		doThrow(new RefsetGraphAccessException()).when(service).addMembers(anyListOf(Rf2Record.class), anyString(), anyString());
		
		srp.process(refsets, "junit", "junit");
		
	}
	
	/**
	 * Test method for {@link org.ihtsdo.otf.refset.service.upload.SimpleRefsetProcessor#process(java.util.List, java.lang.String)}.
	 * @throws EntityNotFoundException 
	 * @throws RefsetServiceException 
	 * @throws RefsetGraphAccessException 
	 */
	@Test(expected = EntityNotFoundException.class)
	public void testProcessEntityNotFoundException() throws RefsetServiceException, EntityNotFoundException, RefsetGraphAccessException {
		
		doThrow(new EntityNotFoundException()).when(service).addMembers(anyListOf(Rf2Record.class), anyString(), anyString());
		
		srp.process(refsets, "junit", "junit");
		
	}

}
