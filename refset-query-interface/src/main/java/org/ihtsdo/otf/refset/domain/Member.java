package org.ihtsdo.otf.refset.domain;


public class Member extends BaseObj {
	
	
	
	private String referencedComponentId;

	/**
	 * @return the referenceComponentId
	 */
	public String getReferencedComponentId() {
		return referencedComponentId;
	}
	/**
	 * @param referenceComponentId the referenceComponentId to set
	 */
	public void setReferencedComponentId(String referenceComponentId) {
		this.referencedComponentId = referenceComponentId;
	}
	

   @Override 
   public boolean equals(Object input) {
		   
	   if ( this == input ) return true;
	   if ( !(input instanceof Member) ) return false;

	   Member m = (Member)input;
	   
	   return ( this.id == m.id ) && ( this.effectiveTime == m.effectiveTime ) 
			   && ( this.referencedComponentId == m.referencedComponentId ) 
			   && ( this.moduleId == m.moduleId );
	   
   }
   
   @Override
   public String toString() {
	   
	   return String.format( "Member [id - %s, referencedComponentId - %s, moduleId - %s, isActive - %s "
	   		+ "effectiveTime - %s]", this.id, this.referencedComponentId, this.moduleId, this.active, this.effectiveTime);
	   
   }   
}
