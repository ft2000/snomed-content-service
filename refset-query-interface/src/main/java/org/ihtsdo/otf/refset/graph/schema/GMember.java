/**
 * 
 */
package org.ihtsdo.otf.refset.graph.schema;

import com.tinkerpop.frames.Incidence;
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
	@Incidence( label = "effectiveTime")
	public long getEffectiveTime();
	
	/**
	 * @param effectiveTime the effectiveTime to set
	 */
	@Incidence( label = "effectiveTime")
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
	public boolean isActive();
	
	/**
	 * @param isActive the isActive to set
	 */
	@Property(value = "active")
	public void setActive(boolean isActive);
	/**
	 * @return the referenceComponentId
	 */
	@Property(value = "referenceComponentId")
	public String getReferenceComponentId();
	/**
	 * @param referenceComponentId the referenceComponentId to set
	 */
	@Property(value = "referenceComponentId")
	public void setReferenceComponentId(String referenceComponentId);
	
	/**
	 * @return the published
	 */
	@Property(value = "published")
	public boolean isPublished();
	
	/**
	 * @param published the published to set
	 */
	@Property(value = "published")
	public void setPublished(boolean isPublished);
	

   
}
