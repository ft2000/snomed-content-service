package org.ihtsdo.otf.refset.domain;


public class Member extends BaseObj implements Comparable<Member>{
	
	
	
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
	   
	   return ( this.id == m.id )
			   && ( this.referencedComponentId == m.referencedComponentId ) 
			   && ( this.moduleId == m.moduleId )
			   && (this.active == m.active);
	   
   }
   
   /* (non-Javadoc)
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode() {

	   return (this.id 
			   + this.referencedComponentId 
			   + this.moduleId
			   + new Boolean(this.active)).hashCode();
   }
   
   @Override
   public String toString() {
	   
	   return String.format( "Member [id - %s, referencedComponentId - %s, moduleId - %s, isActive - %s "
	   		+ "effectiveTime - %s, description - %s]", this.id, this.referencedComponentId, this.moduleId, 
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
