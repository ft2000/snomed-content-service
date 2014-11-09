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
package org.ihtsdo.otf.refset.common;

import java.io.File;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.ihtsdo.otf.refset.schema.RefsetSchema;
import org.ihtsdo.otf.snomed.schema.SnomedConceptSchema;

/**
 *
 */
public class TestGraph {
	
	private static final String INDEX =  "junit";
	public static Configuration getTestGraphConfig() throws ConfigurationException {
		
		Configuration config = new PropertiesConfiguration("src/test/resources/graph-es-junit.properties");
		
		return config;
	}
	
	public static void createTestRefsetGraphSchema() {
		/*create refset schema*/
		
		RefsetSchema rg = new RefsetSchema("src/test/resources/graph-es-junit.properties");
		
		rg.createSchema();
		rg.createMixedIndex(INDEX);

	}
	
	public static void createTestSnomedGraphSchema() {		
		/*create snomed schema*/
		SnomedConceptSchema s = new SnomedConceptSchema("src/test/resources/graph-es-junit.properties");
		s.createSchema();
		s.createIndex(INDEX);
	}
	
	public static void deleteGraph() {
		File f = new File("/tmp/berkeley");
		String[] files = f.list();
		
		if (files != null) {
			
			for (int i = 0; i < files.length; i++) {
				
				File file = new File("/tmp/berkeley/" + files[i]);
				file.delete();
				
			}
		}
		
		f.delete();
		
		f = new File("/tmp/junitindex");
		files = f.list();
		
		if (files != null) {
			
			for (int i = 0; i < files.length; i++) {
				
				File file = new File("/tmp/junitindex" + files[i]);
				file.delete();
				
			}
		}
		
		f.delete();
		
		
	}

}
