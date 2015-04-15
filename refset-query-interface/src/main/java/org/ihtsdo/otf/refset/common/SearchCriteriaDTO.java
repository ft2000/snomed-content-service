/**
* Copyright 2014 IHTSDO
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.ihtsdo.otf.refset.common;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 */
public class SearchCriteriaDTO {

	@JsonProperty(required = true)
	HashMap<String, Object> fields = new HashMap<String, Object>();
	
	@JsonProperty
	HashMap<String, Object> sortBy = new HashMap<String, Object>();
	
	int from;
	int to;
	/**
	 * @return the fields
	 */
	public Map<String, Object> getFields() {
		return fields;
	}
	/**
	 * @param fields the fields to set
	 */
	public void setFields(HashMap<String, Object> fields) {
		this.fields = fields;
	}
	/**
	 * @return the sortBy
	 */
	public HashMap<String, Object> getSortBy() {
		return sortBy;
	}
	/**
	 * @param sortBy the sortBy to set
	 */
	public void setSortBy(HashMap<String, Object> sortBy) {
		this.sortBy = sortBy;
	}
	/**
	 * @return the from
	 */
	public int getFrom() {
		return from;
	}
	/**
	 * @param from the from to set
	 */
	public void setFrom(int from) {
		this.from = from;
	}
	/**
	 * @return the to
	 */
	public int getTo() {
		return to;
	}
	/**
	 * @param to the to to set
	 */
	public void setTo(int to) {
		this.to = to;
	}
	

}
