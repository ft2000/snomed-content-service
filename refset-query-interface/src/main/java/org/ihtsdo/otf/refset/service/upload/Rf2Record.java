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
package org.ihtsdo.otf.refset.service.upload;

import org.joda.time.DateTime;

/**
 *This class reflect a record in RF2 file. At moment is only applicable for simple refsets.
 *However it can be made baseclass for other refset types
 */
public class Rf2Record {
	
	private String id;
	private DateTime effectiveTime;
	private String active;
	private String moduleId;
	private String refsetId;
	private String referencedComponentId;
	
	/*metadata for change history*/
	private DateTime start;
	private DateTime end;
	private String createdBy;
	private String modifiedBy;
	
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
	 * @return the active
	 */
	public String getActive() {
		return active;
	}
	/**
	 * @param active the active to set
	 */
	public void setActive(String active) {
		this.active = active;
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
	 * @return the refsetId
	 */
	public String getRefsetId() {
		return refsetId;
	}
	/**
	 * @param refsetId the refsetId to set
	 */
	public void setRefsetId(String refsetId) {
		this.refsetId = refsetId;
	}
	/**
	 * @return the referencedComponentId
	 */
	public String getReferencedComponentId() {
		return referencedComponentId;
	}
	/**
	 * @param referencedComponentId the referencedComponentId to set
	 */
	public void setReferencedComponentId(String referencedComponentId) {
		this.referencedComponentId = referencedComponentId;
	}
	/**
	 * @return the start
	 */
	public DateTime getStart() {
		return start;
	}
	/**
	 * @param start the start to set
	 */
	public void setStart(DateTime start) {
		this.start = start;
	}
	/**
	 * @return the end
	 */
	public DateTime getEnd() {
		return end;
	}
	/**
	 * @param end the end to set
	 */
	public void setEnd(DateTime end) {
		this.end = end;
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
	
}
