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
package org.ihtsdo.otf.snomed.service;

import org.ihtsdo.otf.snomed.domain.Concept;
import org.ihtsdo.otf.terminology.domain.SnomedConcept;
import org.joda.time.DateTime;

/**
 *
 */
public class ConceptConvertor {

	public static Concept getConcept(SnomedConcept sc) {
		
		Concept c = null;
		
		if (sc != null) {
			
			c = new Concept();
			c.setId(sc.getId());
			c.setActive(sc.isActive());
			c.setModuleId(sc.getModuleId());
			c.setLabel(sc.getTerm());
			
			if(sc.getEffectiveTime() != null)
				c.setEffectiveTime(new DateTime(sc.getEffectiveTime().getTime()));
		}
		
		return c;
		
	}
}
