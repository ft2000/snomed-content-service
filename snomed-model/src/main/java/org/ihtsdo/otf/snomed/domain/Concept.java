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
package org.ihtsdo.otf.snomed.domain;

import java.io.Serializable;

import org.joda.time.DateTime;
/**
 *
 */
public class Concept implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected DateTime modified;
	
	protected String modifiedBy;
	
	protected DateTime created;
	
	protected String createdBy;
	
	protected DateTime effectiveTime;
	
	protected String version;
	
	protected String moduleId;
	
	protected boolean active;

	protected String id;
	
	protected String caseSignificanceId;

	private String label;
	
	


	
	/**
	 * @return the effectiveTime
	 */
	public DateTime getEffectiveTime() {
		return effectiveTime;
	}
	/**
	 * @param effectiveTime the effectiveTime to set
	 */
	public void setEffectiveTime(DateTime effectiveTime) {
		this.effectiveTime = effectiveTime;
	}
	
	
	/**
	 * @return the moduleId
	 */
	public String getModuleId() {
		return moduleId;
	}
	/**
	 * @param moduleId the moduleId to set
	 */
	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}
	/**
	 * @return the isActive
	 */
	public boolean isActive() {
		
		return active;
		
	}
	/**
	 * @param isActive the isActive to set
	 */
	public void setActive(boolean isActive) {
		
		this.active = isActive;
	}
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * @return the created
	 */
	public DateTime getCreated() {
		return created;
	}

	/**
	 * @param created the created to set
	 */
	public void setCreated(DateTime created) {
		this.created = created;
	}
	
	/**
	 * @return the createdBy
	 */
	public String getCreatedBy() {
		return createdBy;
	}

	/**
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	
	/**
	 * @return the modified
	 */
	public DateTime getModified() {
		return modified;
	}
	/**
	 * @param modified the modified to set
	 */
	public void setModified(DateTime modified) {
		this.modified = modified;
	}
	/**
	 * @return the modifiedBy
	 */
	public String getModifiedBy() {
		return modifiedBy;
	}
	/**
	 * @param modifiedBy the modifiedBy to set
	 */
	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}
	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}
	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}
	
	
	
	/**
	 * @param label
	 */
	public String getLabel() {
		// TODO Auto-generated method stub
		return label;
	}
	
	/**
	 * @param label
	 */
	public void setLabel(String label) {
		// TODO Auto-generated method stub
		this.label = label;
	}

}
