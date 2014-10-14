/**
 * 
 */
package org.ihtsdo.otf.refset.domain;

import java.util.Collections;
import java.util.List;

import org.joda.time.DateTime;
/**
 * @author Episteme Partners
 *
 */
public class Refset extends BaseObj {


		
	private String type;
	
	private List<Member> members;
	
	private String typeId;
	
	private String superRefsetTypeId;
	
	private String componentTypeId;
	
	private DateTime expectedReleaseDate;
	
	
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
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
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
	   		+ "superRefsetTypeId - %s, type - %s, typeId - %s, description - %s]", this.id, this.created, this.createdBy, this.description,
	   		this.effectiveTime, this.published, this.languageCode, this.members, this.moduleId, this.publishedDate,
	   		this.superRefsetTypeId, this.type, this.typeId, this.description );
   }


	/**
	 * @return the componentTypeId
	 */
	public String getComponentTypeId() {
		return componentTypeId;
	}

	/**
	 * @param componentTypeId the componentTypeId to set
	 */
	public void setComponentTypeId(String componentTypeId) {
		this.componentTypeId = componentTypeId;
	}

	/**
	 * @return the expectedReleaseDate
	 */
	public DateTime getExpectedReleaseDate() {
		return expectedReleaseDate;
	}

	/**
	 * @param expectedReleaseDate the expectedReleaseDate to set
	 */
	public void setExpectedReleaseDate(DateTime expectedReleaseDate) {
		this.expectedReleaseDate = expectedReleaseDate;
	}


}
