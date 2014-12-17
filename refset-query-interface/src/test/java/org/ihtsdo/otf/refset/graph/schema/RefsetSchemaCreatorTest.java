/**
 * 
 */
package org.ihtsdo.otf.refset.graph.schema;

import org.apache.commons.configuration.ConfigurationException;
import org.ihtsdo.otf.refset.schema.RefsetSchema;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Episteme Partners
 *
 */
public class RefsetSchemaCreatorTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}
//TODO to be moved to new project
	/**
	 * Test method for {@link org.ihtsdo.otf.refset.graph.schema.RefsetSchemaCreator#createRefsetSchema()}.
	 * @throws ConfigurationException 
	 */
	@Test
	public void testCreateRefsetSchema() throws ConfigurationException {

		RefsetSchema sg = new RefsetSchema("src/test/resources/titan-graph-es-junit.properties");
		
		sg.createSchema();
	}

}
