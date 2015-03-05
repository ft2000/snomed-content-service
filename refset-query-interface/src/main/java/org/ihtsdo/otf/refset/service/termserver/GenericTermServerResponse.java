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
package org.ihtsdo.otf.refset.service.termserver;

import java.util.List;
import java.util.Map;

/**
 *Represent a generic JSON response from Term server.
 */
public class GenericTermServerResponse<T> {

	List<Map<T, T>> items;

	/**
	 * @return the items
	 */
	public List<Map<T, T>> getItems() {
		return items;
	}

	/**
	 * @param items the items to set
	 */
	public void setItems(List<Map<T, T>> items) {
		this.items = items;
	}
	
	

}
