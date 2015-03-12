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

/**
 *
 */
public class SearchCriteria {

	Map<SearchField, Direction> sortBy = new HashMap<SearchField, Direction>();
	
	Map<SearchField, Object> fields = new HashMap<SearchField, Object>();
	
	int from = 0;
	
	int to = 10;

	/**
	 * @return the sortby
	 */
	public Map<SearchField, Direction> getSortBy() {
		return sortBy;
	}

	/**
	 * @return the fields
	 */
	public Map<SearchField, Object> getFields() {
		return fields;
	}
	
	/** Adds a sortby field ie a valid {@link SearchField} and provided {@link Direction}
	 * if no direction is provided a default {@link Direction#desc}is assumed
	 * @param field
	 * @param direction
	 */
	public void addSortBy(SearchField field, Direction direction) {
		
		Direction value = direction != null ? direction : Direction.desc;
		
		this.sortBy.put(field, value);
	}
	
	
	/**Adds a valid {@link SearchField} constant and its not null value.
	 * If value is null then field is ignored
	 * @param field
	 * @param direction
	 */
	public void addSearchField(SearchField field, Object fValue) {
		
		if (fValue != null) {
			
			this.fields.put(field, fValue);

		}
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
