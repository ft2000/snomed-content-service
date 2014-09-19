/**
 * 
 */
package org.ihtsdo.otf.refset.graph.schema;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.MapConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * @author Episteme Partners
 *
 */
public class RunRefsetSchemaCreator {

	/**
	 * @param args
	 * @throws ConfigurationException 
	 */
	public static void main(String[] args) throws ConfigurationException {
		
		Configuration config = new PropertiesConfiguration(new File("src/main/resources/titan-graph-es-dev.properties"));
		
		/*create schema*/
		
		RefsetSchemaCreator sg = new RefsetSchemaCreator();
		sg.setConfig(config);

		sg.createRefsetSchema();

	}

}
