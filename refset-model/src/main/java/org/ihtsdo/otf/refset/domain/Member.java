package org.ihtsdo.otf.refset.domain;

import org.ihtsdo.otf.snomed.domain.Concept;


public class Member extends BaseObj implements Comparable<Member>{
	
	
	
	private String referencedComponentId;
	private String memberHasPublishedState;
	private String memberHasPendingEdit;
	
	//to get the current state of referenced component
	private Concept referencedComponent;

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
	

   /**
	 * @return the memberHasPublishedState
	 */
	public String getMemberHasPublishedState() {
		return memberHasPublishedState;
	}
	/**
	 * @param memberHasPublishedState the memberHasPublishedState to set
	 */
	public void setMemberHasPublishedState(String memberHasPublishedState) {
		this.memberHasPublishedState = memberHasPublishedState;
	}
	/**
	 * @return the memberHasPendingEdit
	 */
	public String getMemberHasPendingEdit() {
		return memberHasPendingEdit;
	}
	/**
	 * @param memberHasPendingEdit the memberHasPendingEdit to set
	 */
	public void setMemberHasPendingEdit(String memberHasPendingEdit) {
		this.memberHasPendingEdit = memberHasPendingEdit;
	}
/**
	 * @return the referencedComponent
	 */
	public Concept getReferencedComponent() {
		return referencedComponent;
	}
	/**
	 * @param referencedComponent the referencedComponent to set
	 */
	public void setReferencedComponent(
			Concept referencedComponent) {
		this.referencedComponent = referencedComponent;
	}
@Override 
   public boolean equals(Object input) {
		   
	   if ( this == input ) return true;
	   if ( !(input instanceof Member) ) return false;

	   Member m = (Member)input;
	   
	   boolean outcome = ( this.uuid == m.uuid )
			   && ( this.referencedComponentId == m.referencedComponentId ) 
			   && ( this.moduleId == m.moduleId )
			   && (this.active == m.active);
	   return outcome;
	   
   }
   
   /* (non-Javadoc)
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode() {

	   return 31 * (this.uuid 
			   + this.referencedComponentId 
			   + this.moduleId
			   + new Boolean(this.active)).hashCode();
   }
   
   @Override
   public String toString() {
	   
	   return String.format( "Member [id - %s, referencedComponentId - %s, moduleId - %s, isActive - %s "
	   		+ "effectiveTime - %s, description - %s]", this.uuid, this.referencedComponentId, this.moduleId, 
	   		this.active, this.effectiveTime, this.description);
	   
   }
   
   
   /* (non-Javadoc)
    * @see java.lang.Comparable#compareTo(java.lang.Object)
    */
   public int compareTo(Member o) {

	   if (o == null) {
		
		   return 1;
	   }
	
	   return new Boolean(this.active).compareTo(new Boolean(o.active));
	   
   }   
}
