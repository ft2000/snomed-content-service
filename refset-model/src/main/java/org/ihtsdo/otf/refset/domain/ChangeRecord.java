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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
/**
 *
 */
public class ChangeRecord<T> implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	String changeType;
	List<T> records;
	
	/**
	 * @return the changeType
	 */
	public String getChangeType() {
		return changeType;
	}
	/**
	 * @param changeType the changeType to set
	 */
	public void setChangeType(String changeType) {
		this.changeType = changeType;
	}
	
	/**
	 * @return the records
	 */
	public List<T> getRecords() {
		
		if (records == null) {
			
			records = new ArrayList<T>();
		}
		
		return records;
	}
	/**
	 * @param records the records to set
	 */
	public void setRecords(List<T> records) {
		this.records = records;
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return String.format("Change Record : change type %s changed objects %s", changeType, records);
	}
	
}
