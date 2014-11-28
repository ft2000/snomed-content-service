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

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.ihtsdo.otf.refset.exception.EntityNotFoundException;
import org.ihtsdo.otf.refset.exception.RefsetServiceException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 *
 */

public class ImportRF2ServiceTest {
	
	private static final String GOOD_FILE = "src/test/java/org/ihtsdo/otf/refset/service/upload/der2_Refset_TestSimpleFull_INT_20140731.txt";
	private static final String INVALID_FILE = "src/test/java/org/ihtsdo/otf/refset/service/upload/der2_Refset_TestSimpleFull_INT_20140731.invalid.txt";

	private ImportRF2Service service;
	private SimpleRefsetProcessor srp;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		
		service = new ImportRF2Service();
		
		srp = mock(SimpleRefsetProcessor.class);
		
		service.setSrp(srp);
		doNothing().when(srp).process(anyListOf(Rf2Record.class), anyString(), anyString());
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link org.ihtsdo.otf.refset.service.upload.ImportRF2Service#importFile(java.io.InputStream, java.lang.String)}.
	 * @throws EntityNotFoundException 
	 * @throws RefsetServiceException 
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testImportFileInvalidRequest() throws RefsetServiceException, EntityNotFoundException {
		
		service.importFile(null, "jnunit", "jnunit");
	}
	
	/**
	 * Test method for {@link org.ihtsdo.otf.refset.service.upload.ImportRF2Service#importFile(java.io.InputStream, java.lang.String)}.
	 * @throws EntityNotFoundException 
	 * @throws RefsetServiceException 
	 * @throws FileNotFoundException 
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testImportFileInvalidRequestNullOrEmptyRefsetId() throws RefsetServiceException, EntityNotFoundException, FileNotFoundException {
		
		service.importFile(new FileInputStream(GOOD_FILE), "", "jnunit");
	}
	
	/**
	 * Test method for {@link org.ihtsdo.otf.refset.service.upload.ImportRF2Service#importFile(java.io.InputStream, java.lang.String)}.
	 * @throws EntityNotFoundException 
	 * @throws RefsetServiceException 
	 * @throws FileNotFoundException 
	 */
	@Test(expected = RefsetServiceException.class)
	public void testImportFileInvalidRequestInvalidFile() throws RefsetServiceException, EntityNotFoundException, FileNotFoundException {
		
		service.importFile(new FileInputStream(INVALID_FILE), "junit", "jnunit");
	}
	
	/**
	 * Test method for {@link org.ihtsdo.otf.refset.service.upload.ImportRF2Service#importFile(java.io.InputStream, java.lang.String)}.
	 * @throws EntityNotFoundException 
	 * @throws RefsetServiceException 
	 * @throws FileNotFoundException 
	 */
	@Test
	public void testImportFile() throws RefsetServiceException, EntityNotFoundException, FileNotFoundException {
		
		service.importFile(new FileInputStream(GOOD_FILE), "junit", "jnunit");
		verify(srp).process(anyListOf(Rf2Record.class), anyString(), anyString());
	}
	
	

}
