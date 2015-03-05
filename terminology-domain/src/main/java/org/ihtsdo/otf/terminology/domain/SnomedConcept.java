package org.ihtsdo.otf.terminology.domain;

import java.util.List;
import java.util.Map;

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

/**
 * class to represent SNOMED concept
 */
public class SnomedConcept extends SnomedComponent {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String definitionStatus;
	private String subclassDefinitionStatus;
	private String inactivationIndicator;
	private Map<String, List<String>> associationTargets;
	private String term;
	
	/**
	 * @return the definitionStatus
	 */
	public String getDefinitionStatus() {
		return definitionStatus;
	}
	/**
	 * @param definitionStatus the definitionStatus to set
	 */
	public void setDefinitionStatus(String definitionStatus) {
		this.definitionStatus = definitionStatus;
	}
	/**
	 * @return the subclassDefinitionStatus
	 */
	public String getSubclassDefinitionStatus() {
		return subclassDefinitionStatus;
	}
	/**
	 * @param subclassDefinitionStatus the subclassDefinitionStatus to set
	 */
	public void setSubclassDefinitionStatus(String subclassDefinitionStatus) {
		this.subclassDefinitionStatus = subclassDefinitionStatus;
	}
	/**
	 * @return the inactivationIndicator
	 */
	public String getInactivationIndicator() {
		return inactivationIndicator;
	}
	/**
	 * @param inactivationIndicator the inactivationIndicator to set
	 */
	public void setInactivationIndicator(String inactivationIndicator) {
		this.inactivationIndicator = inactivationIndicator;
	}
	/**
	 * @return the associationTargets
	 */
	public Map<String, List<String>> getAssociationTargets() {
		return associationTargets;
	}
	/**
	 * @param associationTargets the associationTargets to set
	 */
	public void setAssociationTargets(Map<String, List<String>> associationTargets) {
		this.associationTargets = associationTargets;
	}
	/**
	 * @return the term
	 */
	public String getTerm() {
		return term;
	}
	/**
	 * @param term the term to set
	 */
	public void setTerm(String term) {
		this.term = term;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

        return String.format("%s(id=%s, moduleId=%s, effectivetime=%s"
        		+ "associationTargets=%s, inactivationIndicator=%s"
        		+ "subclassDefinitionStatus=%s, definitionStatus=%s, term=%s)", 
        		this.getClass().getSimpleName(), getId(), getModuleId(), getEffectiveTime(),
        		this.associationTargets, this.inactivationIndicator, this.subclassDefinitionStatus,
        		this.definitionStatus, this.term);
	}

	
}
