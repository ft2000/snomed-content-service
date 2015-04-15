/**
 * 
 */
package org.ihtsdo.otf.refset.domain;

import java.util.Collections;
import java.util.List;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author 
 *
 */
public class Refset extends BaseObj implements Comparable<Refset> {


		
	private String type;
		
	private String typeId;
	
	private String superRefsetTypeId;
	
	private String componentTypeId;
	
	private DateTime expectedReleaseDate;
	
	
	private DateTime earliestEffectiveTime;
	
	private DateTime latestEffectiveTime;
	
	
	//used when refset doesn't not have members and available on http url. see https://jira.ihtsdotools.org/browse/RMT-321
	private String externalUrl;
	private String externalContact;
	
	@JsonIgnore
	private List<Member> memberList;
	
	private String status;
	private String sctId;
	protected Integer version;

	
	/**
	 * @return the version
	 */
	public Integer getVersion() {
		return version;
	}
	/**
	 * @param version the version to set
	 */
	public void setVersion(Integer version) {
		this.version = version;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
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
	
	/**
	 * @return the members
	 */
	@JsonIgnore
	public List<Member> getMemberList() {
		
		if(memberList == null) {
			
			memberList = Collections.emptyList();
			
		}
		return memberList;
	}

	/**
	 * @param members the members to set
	 */
	@JsonIgnore
	public void setMemberList(List<Member> memberList) {
		this.memberList = memberList;
	}


	
	/**
	 * Use case of this refset
	 */
	private String scope;
	
	/**
	 * refset contributing organization
	 */
	private String contributingOrganization;
	
	
	/**Country of Origin for this refset
	 * 
	 */
	private String originCountry;
	
	/**SNOMED®CT release date
	 * 
	 */
	private String snomedCTVersion;
	
	/**
	 * SNOMED®CT extension
	 */
	private String snomedCTExtension;
	
	/**
	 * Details implementation notes
	 */
	private String implementationDetails;
	
	private String clinicalDomain;
	
	private String clinicalDomainCode;

	private String snomedCTExtensionNs;
	
	private String originCountryCode;

	
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

	
   @Override 
   public boolean equals(Object input) {
		   
	   if ( this == input ) return true;
	   if ( !(input instanceof Refset) ||  input == null) return false;
		   
	   Refset r = (Refset)input;
	   
	   return ( this.uuid == r.uuid && this.created.equals(r.getCreated()));
	   
   }
   
   /* (non-Javadoc)
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode() {
	   // TODO Auto-generated method stub
	   return (uuid + created).hashCode();
   }
   
   @Override
   public String toString() {
	   
	   return String.format( "Refset [id - %s, created - %s, createdBy - %s, description - %s, "
	   		+ "effectiveTime - %s,  isPublished - %s, languageCode - %s, moduleId - %s, publishedDate - %s "
	   		+ "superRefsetTypeId - %s, type - %s, typeId - %s, description - %s, latestEffectiveTime - %s,"
	   		+ " earliestEffectiveTime -%s, scope - %s, snomedCTExtension - %s, snomedCTVersion - %s,"
	   		+ " contributingOrganization - %s, originCountry - %s, implementationDetails - %s, clinicalDomain - %s,"
	   		+ " clinicalDomainCode - %s, snomedCTExtensionNs - %s, originCountryCode - %s, externalUrl - %s, externalContact - %s "
	   		+ "]", this.uuid, this.created, this.createdBy, this.description, this.effectiveTime, this.published,
	   		this.languageCode, this.moduleId, this.publishedDate, this.superRefsetTypeId, this.type,
	   		this.typeId, this.description, this.earliestEffectiveTime, this.latestEffectiveTime, this.scope,
	   		this.snomedCTExtension, this.snomedCTVersion, this.contributingOrganization, this.originCountry,
	   		this.implementationDetails, this.clinicalDomain, this.clinicalDomainCode, this.snomedCTExtensionNs,
	   		this.originCountryCode, this.externalUrl, this.externalContact);
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

	

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Refset o) {

		if(o == null) {
			
			return 1;
		}
		
		int outcome = this.created.compareTo(o.getCreated());
				
		return outcome;
	}

	/**
	 * @return the earliestEffectiveTime
	 */
	public DateTime getEarliestEffectiveTime() {
		return earliestEffectiveTime;
	}

	/**
	 * @param earliestEffectiveTime the earliestEffectiveTime to set
	 */
	public void setEarliestEffectiveTime(DateTime earliestEffectiveTime) {
		this.earliestEffectiveTime = earliestEffectiveTime;
	}

	/**
	 * @return the latestEffectiveTime
	 */
	public DateTime getLatestEffectiveTime() {
		return latestEffectiveTime;
	}

	/**
	 * @param latestEffectiveTime the latestEffectiveTime to set
	 */
	public void setLatestEffectiveTime(DateTime latestEffectiveTime) {
		this.latestEffectiveTime = latestEffectiveTime;
	}

	/**
	 * @return the scope
	 */
	public String getScope() {
		return scope;
	}

	/**
	 * @param scope the scope to set
	 */
	public void setScope(String scope) {
		this.scope = scope;
	}

	/**
	 * @return the contributingOrganization
	 */
	public String getContributingOrganization() {
		return contributingOrganization;
	}

	/**
	 * @param contributingOrganization the contributingOrganization to set
	 */
	public void setContributingOrganization(String contributingOrganization) {
		this.contributingOrganization = contributingOrganization;
	}

	/**
	 * @return the originCountry
	 */
	public String getOriginCountry() {
		return originCountry;
	}

	/**
	 * @param originCountry the originCountry to set
	 */
	public void setOriginCountry(String originCountry) {
		this.originCountry = originCountry;
	}

	/**
	 * @return the snomedCTVersion
	 */
	public String getSnomedCTVersion() {
		return snomedCTVersion;
	}

	/**
	 * @param snomedCTVersion the snomedCTVersion to set
	 */
	public void setSnomedCTVersion(String snomedCTVersion) {
		this.snomedCTVersion = snomedCTVersion;
	}

	/**
	 * @return the snomedCTExtension
	 */
	public String getSnomedCTExtension() {
		return snomedCTExtension;
	}

	/**
	 * @param snomedCTExtension the snomedCTExtension to set
	 */
	public void setSnomedCTExtension(String snomedCTExtension) {
		this.snomedCTExtension = snomedCTExtension;
	}

	/**
	 * @return the implementationDetails
	 */
	public String getImplementationDetails() {
		return implementationDetails;
	}

	/**
	 * @param implementationDetails the implementationDetails to set
	 */
	public void setImplementationDetails(String implementationDetails) {
		this.implementationDetails = implementationDetails;
	}

	/**
	 * @return the clinicalDomain
	 */
	public String getClinicalDomain() {
		return clinicalDomain;
	}

	/**
	 * @param clinicalDomain the clinicalDomain to set
	 */
	public void setClinicalDomain(String clinicalDomain) {
		this.clinicalDomain = clinicalDomain;
	}

	

	/**
	 * @return the externalUrl
	 */
	public String getExternalUrl() {
		return externalUrl;
	}

	/**
	 * @param externalUrl the externalUrl to set
	 */
	public void setExternalUrl(String externalUrl) {
		this.externalUrl = externalUrl;
	}

	/**
	 * @return the externalContact
	 */
	public String getExternalContact() {
		return externalContact;
	}

	/**
	 * @param externalContact the externalContact to set
	 */
	public void setExternalContact(String externalContact) {
		this.externalContact = externalContact;
	}

	/**
	 * @return the clinicalDomainCode
	 */
	public String getClinicalDomainCode() {
		return clinicalDomainCode;
	}

	/**
	 * @param clinicalDomainCode the clinicalDomainCode to set
	 */
	public void setClinicalDomainCode(String clinicalDomainCode) {
		this.clinicalDomainCode = clinicalDomainCode;
	}

	/**
	 * @return the snomedCTExtensionNs
	 */
	public String getSnomedCTExtensionNs() {
		return snomedCTExtensionNs;
	}

	/**
	 * @param snomedCTExtensionNs the snomedCTExtensionNs to set
	 */
	public void setSnomedCTExtensionNs(String snomedCTExtensionNs) {
		this.snomedCTExtensionNs = snomedCTExtensionNs;
	}

	/**
	 * @return the originCountryCode
	 */
	public String getOriginCountryCode() {
		return originCountryCode;
	}

	/**
	 * @param originCountryCode the originCountryCode to set
	 */
	public void setOriginCountryCode(String originCountryCode) {
		this.originCountryCode = originCountryCode;
	}

	

}
