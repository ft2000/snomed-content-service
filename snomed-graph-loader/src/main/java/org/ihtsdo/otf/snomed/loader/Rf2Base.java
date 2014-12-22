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

import org.joda.time.DateTime;

/**
 *
 */
public class Rf2Base {
	
	private String id;
	private DateTime effectiveTime;
	private String active;
	private String moduleId;
	private DateTime loadedDate;
	private String loadedBy;
	private DateTime modifiedDate;
	private String modifiedBy;
	
	//type to know description/relationship/concept/refset etc. Only used at the backend
	private String vertexType;

	
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
	 * @return the loadedDate
	 */
	public DateTime getLoadedDate() {
		return loadedDate;
	}
	/**
	 * @param loadedDate the loadedDate to set
	 */
	public void setLoadedDate(DateTime loadedDate) {
		this.loadedDate = loadedDate;
	}
	/**
	 * @return the modifiedDate
	 */
	public DateTime getModifiedDate() {
		return modifiedDate;
	}
	/**
	 * @param modifiedDate the modifiedDate to set
	 */
	public void setModifiedDate(DateTime modifiedDate) {
		this.modifiedDate = modifiedDate;
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
	 * @return the loadedBy
	 */
	public String getLoadedBy() {
		return loadedBy;
	}
	/**
	 * @param loadedBy the loadedBy to set
	 */
	public void setLoadedBy(String loadedBy) {
		this.loadedBy = loadedBy;
	}
	/**
	 * @return the type
	 */
	public String getVertexType() {
		return vertexType;
	}
	/**
	 * @param type the type to set
	 */
	public void setVertexType(String type) {
		this.vertexType = type;
	}
}
