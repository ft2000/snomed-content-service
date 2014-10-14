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
		
		if (args != null & args.length != 2) {
			
			System.err.println("Invalid input. Titan db configuration file with abosolute path is required to create Snomed Graph schema "
					+ "and required operation ie index or schema or print (to print schema and indexes) is mandatory");
			
			System.exit(1);
		}

		
		RefsetSchema s = new RefsetSchema(args[0]);
		
		switch (Operation.valueOf(args[1])) {
		
		case index:
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
			s.update(args[2]);
			break;

		default:
			System.out.println("No valid operation specified. Valid operation are index or schema");
			break;
		}

	}

}
