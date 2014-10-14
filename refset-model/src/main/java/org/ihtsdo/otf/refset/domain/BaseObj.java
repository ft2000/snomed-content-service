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
package org.ihtsdo.otf.refset.domain;

import javax.validation.constraints.NotNull;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 *
 */
public class BaseObj {
	
	
	protected String languageCode;

	protected DateTime modifiedDate;
	
	protected String modifiedBy;
	
	protected DateTime created;
	
	protected String createdBy;
	
	protected DateTime effectiveTime;
		
	protected DateTime publishedDate;
	
	protected String version;
	
	protected String moduleId;
	
	protected boolean active;

	protected String id;

	protected boolean published;
	
	@NotNull
	protected String description;
	
	@JsonIgnore
	private MetaData metaData;
	
	private String sctId;

	
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
	 * @return the publishedDate
	 */
	public DateTime getPublishedDate() {
		return publishedDate;
	}

	/**
	 * @param publishedDate the publishedDate to set
	 */
	public void setPublishedDate(DateTime publishedDate) {
		this.publishedDate = publishedDate;
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
	 * @return the published
	 */
	public boolean isPublished() {
		return published;
	}
	/**
	 * @param published the published to set
	 */
	public void setPublished(boolean published) {
		this.published = published;
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
	 * @return the meta
	 */
	public MetaData getMetaData() {
		
		return metaData == null ? new MetaData() : metaData;
	}
	
	/**
	 * @param meta the meta to set
	 */
	public void setMetaData(MetaData meta) {
		this.metaData = meta;
	}
	
	/**
	 * @return the modified
	 */
	public DateTime getModifiedDate() {
		return modifiedDate;
	}
	/**
	 * @param modified the modified to set
	 */
	public void setModifiedDate(DateTime modified) {
		this.modifiedDate = modified;
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
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * @return the languageCode
	 */
	public String getLanguageCode() {
		return languageCode;
	}

	/**
	 * @param languageCode the languageCode to set
	 */
	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}
	/**
	 * @return the sctId
	 */
	public String getSctId() {
		return sctId;
	}
	/**
	 * @param sctId the sctId to set
	 */
	public void setSctId(String sctId) {
		this.sctId = sctId;
	}

}
