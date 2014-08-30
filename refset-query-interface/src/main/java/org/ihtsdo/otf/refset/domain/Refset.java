/**
 * 
 */
package org.ihtsdo.otf.refset.domain;

import java.util.Collections;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author Episteme Partners
 *
 */
public class Refset {

	
	@NotNull
	private String id;
	

	@NotNull
	private String moduleId;
	
	@NotNull
	private String description;
	
	@NotNull
	private String createdBy;
	
	@NotNull
	private DateTime created;

	private String languageCode;
	
	private RefsetType type;
	
	private List<Member> members;
		
	private boolean isPublished;
	
	private DateTime publishedDate;
	
	private DateTime effectiveTime;
	
	private String typeId;
	
	private String superRefsetTypeId;
	
	@JsonIgnore
	private MetaData metaData;
	


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
	 * @return the typeId
	 */
	public String getTypeId() {
		return typeId;
	}

	/**
	 * @param typeId the typeId to set
	 */
	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}

	/**
	 * @return the superRefsetTypeId
	 */
	public String getSuperRefsetTypeId() {
		return superRefsetTypeId;
	}

	/**
	 * @param superRefsetTypeId the superRefsetTypeId to set
	 */
	public void setSuperRefsetTypeId(String superRefsetTypeId) {
		this.superRefsetTypeId = superRefsetTypeId;
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
	 * @return the type
	 */
	public RefsetType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(RefsetType type) {
		this.type = type;
	}

	/**
	 * @return the members
	 */
	public List<Member> getMembers() {
		
		if(members == null) {
			
			members = Collections.emptyList();
			
		}
		return members;
	}

	/**
	 * @param members the members to set
	 */
	public void setMembers(List<Member> members) {
		this.members = members;
	}


	/**
	 * @return the isPublished
	 */
	public boolean isPublished() {
		return isPublished;
	}

	/**
	 * @param isPublished the isPublished to set
	 */
	public void setPublished(boolean isPublished) {
		this.isPublished = isPublished;
	}








	
   @Override 
   public boolean equals(Object input) {
		   
	   if ( this == input ) return true;
	   if ( !(input instanceof Refset) ) return false;

	   Refset r = (Refset)input;
	   
	   return ( this.id == r.id );
	   
   }
   
   
   
   @Override
   public String toString() {
	   
	   return String.format( "Refset [id - %s, created - %s, createdBy - %s, description - %s, "
	   		+ "effectiveTime - %s,  isPublished - %s, languageCode - %s, members - %s, moduleId - %s, publishedDate - %s "
	   		+ "superRefsetTypeId - %s, type - %s, typeId - %s]", this.id, this.created, this.createdBy, this.description,
	   		this.effectiveTime, this.isPublished, this.languageCode, this.members, this.moduleId, this.publishedDate,
	   		this.superRefsetTypeId, this.type, this.typeId );
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

}
