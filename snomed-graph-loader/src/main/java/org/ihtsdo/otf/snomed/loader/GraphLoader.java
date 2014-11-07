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
package org.ihtsdo.otf.snomed.loader;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.UnrecognizedOptionException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;

/**
 *Loads SNOMED data in graph db.
 *Initially it just load rf2 in graph. But it is intended to 
 *load RF1, RF2 format as well as delta releases. Once this is done 
 *this note can be removed
 */
public class GraphLoader {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(GraphLoader.class);
	private static final String USAGE = "\n Usages are \n" + DefaultParser.options.toString();
	
	public static void main(String[] args) throws ParseException {
		
		DefaultParser p = new DefaultParser();
		
		CommandLine cli = null;
		try {
			
			cli = p.parse(DefaultParser.options, args);
			
		} catch (UnrecognizedOptionException e) {
			
			System.out.println(USAGE);
		}
		
		String dbConfig = cli.getOptionValue("config");
		
		validate(dbConfig, "Titan db configuration is required to initialize database");
		
		String type = cli.getOptionValue("type");
		validateType(type, "Data load type is required. Specify either SNAPSHOT or FULL or RDF or DELTA");

		
		//validate file format and type
		
		//validateFiles(cli, "No data file specified");
		
		

		TitanGraph g = null;
		try {
			
			g = openGraph(dbConfig);
			
			if (g == null) {
				
				throw new IllegalArgumentException("Could not get graph instance");
			}
			
			switch (LoadType.valueOf(type)) {
			
			case snapshot:
				Rf2SnapshotLoader loader = new Rf2SnapshotLoader(g);
				if (!StringUtils.isBlank(cli.getOptionValue("bSize"))) {
					
					loader.setBufferSize(Integer.parseInt(cli.getOptionValue("bSize")));

				}
				FileType[] fTypes = FileType.values();
				
				for (int i = 0; i < fTypes.length; i++) {
					
					if (!fTypes[i].equals(FileType.nt)) {
						
						FileType fType = fTypes[i];
						LOGGER.info("Loading file type {}", fType.toString());
						String file = cli.getOptionValue(fType.toString());
						LOGGER.info("Loading file  {}", file);

						if (!StringUtils.isBlank(file)) {
							
							loader.load(file);

						}

					}
				}
				
				break;
				
			case audit:
				Rf2SnapshotAuditor auditor = new Rf2SnapshotAuditor(g);
				if (!StringUtils.isBlank(cli.getOptionValue("bSize"))) {
					
					auditor.setBufferSize(Integer.parseInt(cli.getOptionValue("bSize")));

				}
				FileType[] afTypes = FileType.values();
				
				for (int i = 0; i < afTypes.length; i++) {
					
					if (!afTypes[i].equals(FileType.nt)) {
						
						FileType fType = afTypes[i];
						LOGGER.info("Auditing file type {}", fType.toString());
						String file = cli.getOptionValue(fType.toString());
						LOGGER.info("Auditing file  {}", file);

						if (!StringUtils.isBlank(file)) {
							
							auditor.setSubType(cli.getOptionValue("subType"));
							auditor.audit(file);

						}

					}
				}
				
				break;


			default:
				LOGGER.info("Nothing to load");
				break;
			}
			
			LOGGER.info("Finished loading");

			
		} catch (Exception e) {
			
			e.printStackTrace();
			
		} finally {
			
			if (g != null) {
				
				g.shutdown();

			}
		}
		
	}
	
	/**
	 * @param type
	 * @param string
	 */
	private static void validateType(String type, String message) {

		validate(type, message);
		
		if (!type.equalsIgnoreCase(LoadType.snapshot.toString()) && !type.equalsIgnoreCase(LoadType.audit.toString())) {
			
			message = type + " data load is not supported yet";
			
			throw new UnsupportedOperationException(message + USAGE);
		}
		
	}

	private static TitanGraph openGraph(String dbConfig) {
		
		return TitanFactory.open(dbConfig);
	}
	
	private static void validate(String input, String message) {
		
		if (StringUtils.isBlank(input)) {
			
			message = StringUtils.isBlank(message) ? "Some of the required input missing" : message;
			
			throw new IllegalArgumentException(message + USAGE);
		}
	}

}
