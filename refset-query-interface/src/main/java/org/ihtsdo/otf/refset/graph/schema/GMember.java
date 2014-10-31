/**
 * 
 */
package org.ihtsdo.otf.refset.graph.schema;

import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.VertexFrame;

/**
 * @author Episteme Partners
 *
 */
public interface GMember extends VertexFrame {

	/**
	 * @return the effectiveTime
	 */
	@Property( value = "effectiveTime")
	public long getEffectiveTime();
	
	/**
	 * @param effectiveTime the effectiveTime to set
	 */
	@Property( value = "effectiveTime")
	public void setEffectiveTime(long effectiveTime);
	
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
	@Property(value = "moduleId")
	public String getModuleId();
	
	/**
	 * @param moduleId the moduleId to set
	 */
	@Property(value = "moduleId")
	public void setModuleId(String moduleId) ;
	/**
	 * @return the isActive
	 */
	@Property(value = "active")
	public Integer getActive();
	
	/**
	 * @param isActive the isActive to set
	 */
	@Property(value = "active")
	public void setActive(Integer isActive);
	/**
	 * @return the referenceComponentId
	 */
	@Property(value = "referencedComponentId")
	public String getReferencedComponentId();
	/**
	 * @param referenceComponentId the referenceComponentId to set
	 */
	@Property(value = "referencedComponentId")
	public void setReferencedComponentId(String referenceComponentId);
	
	/**
	 * @return the published
	 */
	@Property(value = "published")
	public Integer getPublished();
	
	/**
	 * @param published the published to set
	 */
	@Property(value = "published")
	public void setPublished(Integer isPublished);
	
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
	 * @return the created
	 */
	@Property( value = "created")
	public long getCreated();

	/**
	 * @param modifiedDate the created to set
	 */
	@Property( value = "created")
	public void setCreated(long created);

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
	 * @return the createdBy
	 */
	@Property( value = "createdBy")
	public String getCreateBy();

	/**
	 * @param createdBy the createdBy to set
	 */
	@Property( value = "createdBy")
	public void setCreateBy(String createdBy);

	/**
	 * @param type
	 */
	@Property( value = "type")
	public void setType(String type);
	
	/**
	 * @param type
	 */
	@Property( value = "type")
	public void getType();
   
}
