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
package org.ihtsdo.otf.refset.domain;

import java.util.List;

/**
 *
 */
public class SearchResult<T> {
	
	private int totalNoOfResults;
	private int time;
	
	private List<T> records;

	/**
	 * @return the totalNoOfResults
	 */
	public int getTotalNoOfResults() {
		return totalNoOfResults;
	}
	/**
	 * @param totalNoOfResults the totalNoOfResults to set
	 */
	public void setTotalNoOfResults(int totalNoOfResults) {
		this.totalNoOfResults = totalNoOfResults;
	}
	/**
	 * @return the time
	 */
	public int getTime() {
		return time;
	}
	/**
	 * @param time the time to set
	 */
	public void setTime(int time) {
		this.time = time;
	}
	/**
	 * @return the records
	 */
	public List<T> getRecords() {
		return records;
	}
	/**
	 * @param records the records to set
	 */
	public void setRecords(List<T> records) {
		this.records = records;
	}
	
}
