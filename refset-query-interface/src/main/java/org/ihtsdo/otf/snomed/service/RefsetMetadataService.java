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
package org.ihtsdo.otf.snomed.service;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

/**Service to get refset metadata not available in Term server
 * e.g.clinical domain, country extension. These are being maintained as spring bean resources 
 *
 */
@Service(value = "refsetMetadataService")
public class RefsetMetadataService {
	
	@Resource( name = "snomed.clicnicalDomain" )
	private Map<String, String> clinicalDomain;

	@Resource( name = "snomed.extensions" )
	private Map<String, String> snomedExtensions;

	public Map<String, String> getClinicalDomains() {
		
		return clinicalDomain;
	}

	/**
	 * @return
	 */
	public Map<String, String> getExtensions() {

		return snomedExtensions;
	}
}
