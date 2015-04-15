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
package org.ihtsdo.otf.refset.schema;

/**Class to help in Snomed graph schema and index creation
 *
 */
public class RefsetIndexer {


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		if (args != null & args.length < 2) {
			
			System.err.println("Invalid input. Titan db configuration file with abosolute path is required to create Snomed Graph schema "
					+ "and required operation ie index or schema or print (to print schema and indexes) is mandatory");
			
			System.exit(1);
		}

		
		RefsetSchema s = new RefsetSchema(args[0]);
		
		switch (Operation.valueOf(args[1])) {
		
		case index:
			s.createMixedIndex(null);//default index name
			s.printIndexes();
			break;
			
		case schema:
			s.createSchema();
			s.printSchema();
			break;
			
		case print:	
			s.printIndexes();
			s.printSchema();
			break;
			
		case update:
			s.updateMixedIndex();
			break;
		case repair:
			if(args.length != 3) {
				
				System.err.println("To repair a index, an index name is required beside graph configurations. "
						+ "Use print option to see available indexes "
						+ "To repair see doc @ http://s3.thinkaurelius.com/docs/titan/0.5.0/reindex.html"
						+ " for more details");
				break;
			}
			s.repairIndex(args[2]);
			break;
		default:
			System.out.println("No valid operation specified. Valid operation are index or schema");
			break;
		}

	}

}
