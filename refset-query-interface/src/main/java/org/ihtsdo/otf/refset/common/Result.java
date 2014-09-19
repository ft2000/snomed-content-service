/**
 * 
 */
package org.ihtsdo.otf.refset.common;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Episteme Partners
 *
 */
public class Result<T> {
	
	private T data;
	private Meta meta;


	/**
	 * @param meta the meta to set
	 */
	@JsonProperty("meta")
	public void setMeta(Meta meta) {
		this.meta = meta;
	}

	/**
	 * @return the data
	 */
	public T getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	@JsonProperty("content")
	public void setData(T data) {
		this.data = data;
	}
	
	public Meta getMeta() {
		return meta;
	}

	
}
