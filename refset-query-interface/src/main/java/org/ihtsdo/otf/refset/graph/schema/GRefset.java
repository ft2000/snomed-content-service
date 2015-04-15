/**
 * 
 */
package org.ihtsdo.otf.refset.graph.schema;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.frames.Adjacency;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.VertexFrame;

/**
 *
 */

public interface GRefset extends VertexFrame {
	
	
	/**
	 * @return the expectedPublishDate
	 */
	@Property( value = "expectedPublishDate")
	public long getExpectedPublishDate();

	/**
	 * @param expectedPublishDate the expectedPublishDate to set
	 */
	@Property( value = "expectedPublishDate")
	public void setExpectedPublishDate(long expectedPublishDate);
	
	/**
	 * @return the releasedDate
	 */
	@Property( value = "created")
	public long getCreated();

	/**
	 * @param created the created to set
	 */
	@Property( value = "created")
	public void setCreated(long created);

	/**
	 * @return the publishedDate
	 */
	@Property( value = "publishedDate")
	public long getPublishedDate();

	/**
	 * @param publishedDate the publishedDate to set
	 */
	@Property( value = "publishedDate")
	public void setPublishedDate(long publishedDate);

	/**
	 * @return the effectiveTime
	 */
	@Property( value = "effectiveDate")
	public long getEffectiveTime();

	/**
	 * @param effectiveTime the effectiveTime to set
	 */
	@Property( value = "effectiveDate")
	public void setEffectiveTime(long effectiveTime);

	/**
	 * @return the typeId
	 */
	@Property( value = "typeId")
	public String getTypeId();

	/**
	 * @param typeId the typeId to set
	 */
	@Property( value = "typeId")
	public void setTypeId(String typeId);

	/**
	 * @return the superRefsetTypeId
	 */
	@Property( value = "superRefsetTypeId")
	public String getSuperRefsetTypeId();

	/**
	 * @param superRefsetTypeId the superRefsetTypeId to set
	 */
	@Property( value = "superRefsetTypeId")
	public void setSuperRefsetTypeId(String superRefsetTypeId);

	/**
	 * @return the id
	 */
	@Property( value = "sid")
	public String getId();

	/**
	 * @param id the id to set
	 */
	@Property( value = "sid")
	public void setId(String id);

	/**
	 * @return the moduleId
	 */
	@Property( value = "moduleId")
	public String getModuleId();

	/**
	 * @param moduleId the moduleId to set
	 */
	@Property( value = "moduleId")
	public void setModuleId(String moduleId);

	/**
	 * @return the description
	 */
	@Property( value = "description")
	public String getDescription() ;

	/**
	 * @param description the description to set
	 */
	@Property( value = "description")
	public void setDescription(String description) ;

	/**
	 * @return the createdBy
	 */
	@Property( value = "createdBy")
	public String getCreatedBy();

	/**
	 * @param createdBy the createdBy to set
	 */
	@Property( value = "createdBy")
	public void setCreatedBy(String createdBy);


	/**
	 * @return the languageCode
	 */
	@Property( value = "languageCode")
	public String getLanguageCode();

	/**
	 * @param languageCode the languageCode to set
	 */
	@Property( value = "languageCode")
	public void setLanguageCode(String languageCode);

	/**
	 * @return the type
	 */
	@Property( value = "type")
	public String getType();

	/**
	 * @param type the type to set
	 */
	@Property( value = "type")
	public void setType(String type);

	/**
	 * @return the members
	 */
	@Adjacency( label = "members")
	public Iterable<GMember> getMembers();

	/**
	 * @param members the members to set
	 */
	@Adjacency( label = "members", direction = Direction.OUT)
	public void setMembers(Iterable<GMember> members);


	/**
	 * @return the isPublished
	 */
	@Property( value = "published")
	public Integer getPublished();

	/**
	 * @param isPublished the isPublished to set
	 */
	@Property( value = "published")
	public void setPublished(Integer isPublished);
	
	
	/**
	 * @return the active
	 */
	@Property( value = "active")
	public Integer getActive();

	/**
	 * @param active the active to set
	 */
	@Property( value = "active")
	public void setActive(Integer active);
	
	/**
	 * @return the componentTypeId
	 */
	@Property( value = "componentTypeId")
	public String getComponentTypeId();

	/**
	 * @param componentTypeId the componentTypeId to set
	 */
	@Property( value = "componentTypeId")
	public void setComponentTypeId(String componentTypeId);
	
	/**
	 * @return the modifiedDate
	 */
	@Property( value = "modifiedDate")
	public long getModifiedDate();

	/**
	 * @param modifiedDate the modifiedDate to set
	 */
	@Property( value = "modifiedDate")
	public void setModifiedDate(long modifiedDate);
	
	/**
	 * @return the modifiedBy
	 */
	@Property( value = "modifiedBy")
	public String getModifiedBy();

	/**
	 * @param modifiedBy the modifiedBy to set
	 */
	@Property( value = "modifiedBy")
	public void setModifiedBy(String modifiedBy);

	/**
	 * @return the sctId
	 */
	@Property( value = "sctId")
	public String getSctId();

	/**
	 * @param id the id to set
	 */
	@Property( value = "sctId")
	public void setSctId(String sctId);
	
	/**
	 * @return the id
	 */
	@Property( value = "noOfMembers")
	public Long getNoOfMembers();

	/**
	 * @param id the id to set
	 */
	@Property( value = "noOfMembers")
	public void setNoOfMembers(Long noOfMembers);
	
	/**
	 * @return the earliestEffectiveTime
	 */
	@Property( value = "earliestEffectiveTime")
	public long getEarliestEffectiveTime();

	/**
	 * @param earliestEffectiveTime the earliestEffectiveTime to set
	 */
	@Property( value = "earliestEffectiveTime")
	public void setEarliestEffectiveTime(long earliestEffectiveTime);
	
	/**
	 * @return the latestEffectiveTime
	 */
	@Property( value = "latestEffectiveTime")
	public long getLatestEffectiveTime();

	/**
	 * @param latestEffectiveTime the latestEffectiveTime to set
	 */
	@Property( value = "latestEffectiveTime")
	public void setLatestEffectiveTime(long latestEffectiveTime);
	
	/**
	 * @return the scope
	 */
	@Property( value = "scope")
	public String getScope();

	/**
	 * @param scope the scope to set
	 */
	@Property( value = "scope")
	public void setScope(String scope);

	/**
	 * @return the contributingOrganization
	 */
	@Property( value = "contributingOrganization")
	public String getContributingOrganization();
	/**
	 * @param contributingOrganization the contributingOrganization to set
	 */
	@Property( value = "contributingOrganization")
	public void setContributingOrganization(String contributingOrganization);
	
	/**
	 * @return the originCountry
	 */
	@Property( value = "originCountry")
	public String getOriginCountry();

	/**
	 * @param originCountry the originCountry to set
	 */
	@Property( value = "originCountry")
	public void setOriginCountry(String originCountry);

	/**
	 * @return the snomedCTVersion
	 */
	@Property( value = "snomedCTVersion")
	public String getSnomedCTVersion();

	/**
	 * @param snomedCTVersion the snomedCTVersion to set
	 */
	@Property( value = "snomedCTVersion")
	public void setSnomedCTVersion(String snomedCTVersion);

	/**
	 * @return the snomedCTExtension
	 */
	@Property( value = "snomedCTExtension")
	public String getSnomedCTExtension();

	/**
	 * @param snomedCTExtension the snomedCTExtension to set
	 */
	@Property( value = "snomedCTExtension")
	public void setSnomedCTExtension(String snomedCTExtension);

	/**
	 * @return the implementationDetails
	 */
	@Property( value = "implementationDetails")
	public String getImplementationDetails();

	/**
	 * @param implementationDetails the implementationDetails to set
	 */
	@Property( value = "implementationDetails")
	public void setImplementationDetails(String implementationDetails);

	/**
	 * @return the clinicalDomain
	 */
	@Property( value = "clinicalDomain")
	public String getClinicalDomain();
	/**
	 * @param clinicalDomain the clinicalDomain to set
	 */
	@Property( value = "clinicalDomain")
	public void setClinicalDomain(String clinicalDomain);
	

	/**
	 * @return the views
	 */
	
	@Property( value = "viewCount")
	public int getViews();
	/**
	 * @param views the views to set
	 */
	@Property( value = "viewCount")
	public void setViews(int views);
	/**
	 * @return the downloads
	 */
	@Property( value = "downloadCount")
	public int getDownloads();
	/**
	 * @param downloads the downloads to set
	 */
	@Property( value = "downloadCount")
	public void setDownloads(int downloads);
	
	/**
	 * @return the externalUrl
	 */
	@Property( value = "externalUrl")
	public String getExternalUrl();

	/**
	 * @param externalUrl the externalUrl to set
	 */
	@Property( value = "externalUrl")
	public void setExternalUrl(String externalUrl);

	/**
	 * @return the externalContact
	 */
	@Property( value = "externalContact")
	public String getExternalContact();

	/**
	 * @param externalContact the externalContact to set
	 */
	@Property( value = "externalContact")
	public void setExternalContact(String externalContact);
	
	/**
	 * @return the clinicalDomainCode
	 */
	@Property( value = "clinicalDomainCode")
	public String getClinicalDomainCode();

	/**
	 * @param clinicalDomainCode the clinicalDomainCode to set
	 */
	@Property( value = "clinicalDomainCode")
	public void setClinicalDomainCode(String clinicalDomainCode);

	/**
	 * @return the snomedCTExtensionNs
	 */
	@Property( value = "snomedCTExtensionNs")
	public String getSnomedCTExtensionNs();

	/**
	 * @param snomedCTExtensionNs the snomedCTExtensionNs to set
	 */
	@Property( value = "snomedCTExtensionNs")
	public void setSnomedCTExtensionNs(String snomedCTExtensionNs);

	/**
	 * @return the originCountryCode
	 */
	@Property( value = "originCountryCode")
	public String getOriginCountryCode();

	/**
	 * @param originCountryCode the originCountryCode to set
	 */
	@Property( value = "originCountryCode")
	public void setOriginCountryCode(String originCountryCode);
	
	
	/**
	 * @return the status
	 */
	@Property( value = "refsetStatus")
	public String getStatus();
	
	/**
	 * @param status the status to set
	 */
	@Property( value = "refsetStatus")
	public void setStatus(String status);
	
	/**
	 * @return the version
	 */
	@Property( value = "version")
	public Integer getVersion();
	
	/**
	 * @param version the version to set
	 */
	@Property( value = "version")
	public void setVersion(Integer version);



}
