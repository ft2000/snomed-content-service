/**
 * 
 */
package org.ihtsdo.otf.refset.domain;

/**
 *These constant are mapped to Refset schema
 */
public final class RGC {

	public static final String DESC = "description";
	public static final String ID = "sid";
	public static final String EFFECTIVE_DATE = "effectiveDate";
	public static final String CREATED = "created";
	public static final String SUPER_REFSET_TYPE_ID = "superRefsetTypeId";
	public static final String TYPE_ID = "typeId";
	public static final String PUBLISHED_DATE = "publishedDate";
	public static final String PUBLISHED = "published";

	public static final String TYPE = "type";
	public static final String LANG_CODE = "languageCode";
	public static final String CREATED_BY = "createdBy";
	public static final String MODULE_ID = "moduleId";
	public static final String REFERENCE_COMPONENT_ID = "referencedComponentId";
	public static final String ACTIVE = "active";
	public static final String MEMBER_TYPE_ID = "componentTypeId";
	public static final String MODIFIED_DATE = "modifiedDate";
	public static final String MODIFIED_BY = "modifiedBy";
	public static final String SCTID = "sctId";
	public static final String EXPECTED_PUBLISH_DATE = "expectedPublishDate";
	public static final String START = "start";
	public static final String END = "end";
	
	public static final String E_EFFECTIVE_TIME = "earliestEffectiveTime";
	public static final String L_EFFECTIVE_TIME = "latestEffectiveTime";
	
	public static final String PARENT_ID = "parentId";
	
	public static final String LOCK = "lock"; //used in write lock. This is to handle long running transaction.
	

	
	//new field defined post MVP
	
	/**
	 * Use case of this refset
	 */
	public static final String SCOPE = "scope";
	
	/**
	 * refset contributing organization
	 */
	public static final String  CONTRIBUTING_ORG = "contributingOrganization";
	
	
	/**Country of Origin for this refset
	 * 
	 */
	public static final String ORIGIN_COUNTRY = "originCountry";
	
	/**SNOMED®CT release date
	 * 
	 */
	public static final String SNOMED_CT_VERSION = "snomedCTVersion";
	
	/**
	 * SNOMED®CT extension
	 */
	public static final String SNOMED_CT_EXT = "snomedCTExtension";
	
	/**
	 * Details implementation notes
	 */
	public static final String IMPLEMENTATION_DETAILS = "implementationDetails";
	
	
	public static final String CLINICAL_DOMAIN = "clinicalDomain";

	public static final String VIEW_COUNT = "viewCount";
	
	public static final String DOWNLOAD_COUNT = "downloadCount";

	//field for user schema
	
	public static final String USER_NAME = "userName";

	//used when refset doesn't not have members and available on http url. see https://jira.ihtsdotools.org/browse/RMT-321
	public static final String EXT_URL = "externalUrl";
	public static final String EXT_CONTACT = "externalContact";
	
	public static final String CLINICAL_DOMAIN_CODE = "clinicalDomainCode";

	public static final String SNOMED_CT_EXT_NS = "snomedCTExtensionNs";
	
	public static final String ORIGIN_COUNTRY_CODE = "originCountryCode";



}
