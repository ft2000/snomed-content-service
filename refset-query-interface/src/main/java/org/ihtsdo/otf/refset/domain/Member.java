package org.ihtsdo.otf.refset.domain;

public class Member {
	
	private String id;
	
	private String moduleId;
	
	private boolean isActive;
	
	private String referenceComponentId;
	
	private String effectiveTime;
	
	/**
	 * @return the effectiveTime
	 */
	public String getEffectiveTime() {
		return effectiveTime;
	}
	/**
	 * @param effectiveTime the effectiveTime to set
	 */
	public void setEffectiveTime(String effectiveTime) {
		this.effectiveTime = effectiveTime;
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
	 * @return the isActive
	 */
	public boolean isActive() {
		return isActive;
	}
	/**
	 * @param isActive the isActive to set
	 */
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	/**
	 * @return the referenceComponentId
	 */
	public String getReferenceComponentId() {
		return referenceComponentId;
	}
	/**
	 * @param referenceComponentId the referenceComponentId to set
	 */
	public void setReferenceComponentId(String referenceComponentId) {
		this.referenceComponentId = referenceComponentId;
	}
	
   @Override 
   public boolean equals(Object input) {
		   
	   if ( this == input ) return true;
	   if ( !(input instanceof Member) ) return false;

	   Member m = (Member)input;
	   
	   return ( this.id == m.id ) && ( this.effectiveTime == m.effectiveTime ) 
			   && ( this.referenceComponentId == m.referenceComponentId ) 
			   && ( this.moduleId == m.moduleId );
	   
   }
   
   @Override
   public String toString() {
	   
	   return String.format( "Member [id - %s, referenceComponentId - %s, moduleId - %s, isActive - %s "
	   		+ "effectiveTime - %s]", this.id, this.referenceComponentId, this.moduleId, this.isActive, this.effectiveTime);
	   
   }   
}
