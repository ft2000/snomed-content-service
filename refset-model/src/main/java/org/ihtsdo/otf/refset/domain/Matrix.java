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

/**
 *Represent user matrix like view count, download count
 */
public class Matrix implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1006952382588900265L;
	
	int views;
	int downloads;
	/**
	 * @return the views
	 */
	public int getViews() {
		return views;
	}
	/**
	 * @param views the views to set
	 */
	public void setViews(int views) {
		this.views = views;
	}
	/**
	 * @return the downloads
	 */
	public int getDownloads() {
		return downloads;
	}
	/**
	 * @param downloads the downloads to set
	 */
	public void setDownloads(int downloads) {
		this.downloads = downloads;
	}
}
