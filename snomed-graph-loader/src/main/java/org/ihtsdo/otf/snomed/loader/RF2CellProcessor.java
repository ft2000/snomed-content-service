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

import org.ihtsdo.otf.snomed.loader.ParseJodaTime;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;

/**
 *
 */
public class RF2CellProcessor {


	protected static CellProcessor[] getConceptCellProcessor() {
		
		final CellProcessor[] processors = new CellProcessor[] { 
				new NotNull(), // sctid
				new NotNull(new ParseJodaTime("yyyyMMdd")), // effectiveTime
				new NotNull(), // status
				new NotNull(), //moduleId
				new NotNull() //definitionStatusId
		};
		
		return processors;
	}
	
	protected static CellProcessor[] getDescriptionCellProcessor() {
		
		//id	effectiveTime	active	moduleId	conceptId	languageCode	typeId	term	caseSignificanceId

		final CellProcessor[] processors = new CellProcessor[] { 
				new NotNull(), // sctid
				new NotNull(new ParseJodaTime("yyyyMMdd")), // effectiveTime
				new NotNull(), // status
				new NotNull(), //moduleId
				new NotNull(), //conceptId
				new NotNull(), //languageCode
				new NotNull(), //typeId
				new NotNull(), //term
				new NotNull(), //caseSignificanceId

		};
		
		return processors;
	}
	
	protected static CellProcessor[] getRelationshipCellProcessor() {
		//id	effectiveTime	active	moduleId	sourceId	destinationId	relationshipGroup	typeId	characteristicTypeId	modifierId

		final CellProcessor[] processors = new CellProcessor[] { 
				new NotNull(), // sctid
				new NotNull(new ParseJodaTime("yyyyMMdd")), // effectiveTime
				new NotNull(), // status
				new NotNull(), //moduleId
				new NotNull(), //sourceId
				new NotNull(), //destinationId
				new NotNull(), //relationshipGroup
				new NotNull(), //typeId
				new NotNull(), //characteristicTypeId
				new NotNull(), //modifierId
		};
		
		return processors;
	}
	
	
	

}