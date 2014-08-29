/**
 * 
 */
package org.ihtsdo.otf.refset.domain;

/**
 * Class to represent metadata details of a domain object namely
 * Version, RecordId etc. These are used at service layer
 * @author Episteme Partners
 *
 */
public final class MetaData {
	
	private Object Id;
	
	private Integer version;
	
	private String type;

	/**
	 * @return the id
	 */
	public Object getId() {
		return Id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Object id) {
		Id = id;
	}

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
	

}
