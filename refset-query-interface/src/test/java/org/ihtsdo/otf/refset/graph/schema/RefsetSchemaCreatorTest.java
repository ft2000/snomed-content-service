/**
 * 
 */
package org.ihtsdo.otf.refset.graph.schema;

import static org.junit.Assert.*;

import java.io.File;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
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

		Configuration config = new PropertiesConfiguration(new File("src/main/resources/titan-graph-es-dev.properties"));
		
		/*create schema*/
		
		RefsetSchemaCreator sg = new RefsetSchemaCreator();
		sg.setConfig(config);

		//sg.createRefsetSchema();
	}

}
