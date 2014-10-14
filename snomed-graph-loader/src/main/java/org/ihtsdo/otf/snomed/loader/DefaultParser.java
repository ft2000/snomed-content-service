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

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.Options;

/**
 *
 */
public class DefaultParser extends BasicParser {
	
	protected static Options options;
	
	static {

		options = new Options();
		options.addOption("type", true, "Type of load as snapshot or rdf or full");
		options.addOption(FileType.nt.toString(), false, "Absolute path of data file - as n-triples when loading rdf");
		options.addOption("config", true, "Graph db connection configuration");
		options.addOption("bSize", true, "Batch size to commit per transaction, default is 10000 vertex");
		options.addOption(FileType.cf.toString(), true, "Absolute path of rf2 concept file");
		options.addOption(FileType.df.toString(), true, "Absolute path of rf2 description file");
		options.addOption(FileType.rf.toString(), true, "Absolute path of rf2 relationship file");
		options.addOption("subType", true, "subType fsn or synonym or definition");

		

	}


}
