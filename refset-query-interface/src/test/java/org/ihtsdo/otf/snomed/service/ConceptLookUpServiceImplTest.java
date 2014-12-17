/**
 * 
 */
package org.ihtsdo.otf.snomed.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.Map;
import java.util.Set;

import org.apache.commons.configuration.Configuration;
import org.ihtsdo.otf.refset.common.TestGraph;
import org.ihtsdo.otf.refset.exception.EntityNotFoundException;
import org.ihtsdo.otf.refset.graph.RefsetGraphFactory;
import org.ihtsdo.otf.snomed.domain.Concept;
import org.ihtsdo.otf.snomed.exception.ConceptServiceException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Episteme Partners
 *
 */
public class ConceptLookUpServiceImplTest {
	
	private ConceptLookUpServiceImplv1_0 service;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		
		service = new ConceptLookUpServiceImplv1_0();
		
		//service.setRepositoryConfig("src/test/resources/titan-graph-es-junit.properties");
		Configuration config = TestGraph.getTestGraphConfig();
		
		RefsetGraphFactory sf = new RefsetGraphFactory(config);
		
		service.setFactory(sf);
		service.setConfig(config);
	}
	
	@BeforeClass
	public static void initialize() {
		
		TestGraph.createTestSnomedGraphSchema();
	}
	
	@AfterClass
	public static void cleanup() {
		
		TestGraph.deleteGraph();
	}
	

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link org.ihtsdo.otf.snomed.service.ConceptLookUpServiceImpl#getConcepts(java.util.List)}.
	 * @throws ConceptServiceException 
	 */
	@Test
	public void testGetConcepts() throws ConceptServiceException {

		Set<String> conceptIds = service.getConceptIds(1, 10);
		
		Map<String, Concept> concepts = service.getConcepts(conceptIds);
		
		assertNotNull(concepts);
		
		assertFalse(concepts.isEmpty());
		
		assertEquals(10, concepts.size());
		
		for (String id : conceptIds) {
			
			Concept c = concepts.get(id);
			
			assertNotNull(c);
			
			System.err.println(c);

		}
		

	}

	/**
	 * Test method for {@link org.ihtsdo.otf.snomed.service.ConceptLookUpServiceImpl#getConcept(java.lang.String)}.
	 * @throws ConceptServiceException 
	 * @throws EntityNotFoundException 
	 */
	@Test
	public void testGetConcept() throws ConceptServiceException, EntityNotFoundException {
		
		Set<String> conceptIds = service.getConceptIds(1, 1);
		Concept c = null;
		for (String id : conceptIds) {
			
			c = service.getConcept(id);
			System.err.println("---concept" + c.toString());
			break;
		}

		assertNotNull(c);
		assertNotNull(c.getEffectiveTime());
		
		assertNotNull(c.getId());
		
	}

	/**
	 * Test method for {@link org.ihtsdo.otf.snomed.service.ConceptLookUpServiceImpl#getConceptIds(int, int)}.
	 * @throws ConceptServiceException 
	 */
	@Test
	public void testGetConceptIds() throws ConceptServiceException {
		
		Set<String> conceptIds = service.getConceptIds(1, 10);
		assertEquals(10, conceptIds.size());
	}

}
