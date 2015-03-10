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
import java.util.TreeMap;

import javax.annotation.Resource;

import org.ihtsdo.otf.refset.exception.RefsetServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.ibm.icu.util.ULocale;

/**Service to get refset metadata not available in Term server
 * e.g.clinical domain, country extension, origin countries. These are being maintained as spring bean resources 
 *
 */
@Service(value = "refsetMetadataService")
public class RefsetMetadataService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RefsetMetadataService.class);
	
	@Resource( name = "snomed.clicnicalDomain" )
	private Map<String, String> clinicalDomain;

	@Resource( name = "snomed.extensions" )
	private Map<String, String> snomedExtensions;
	
	public Map<String, String> getClinicalDomains() {
		
		LOGGER.debug("getClinicalDomains");

		return clinicalDomain;
	}

	/**
	 * @return
	 */
	public Map<String, String> getExtensions() {

		LOGGER.debug("getExtensions");
		
		return snomedExtensions;
	}
	
	/**Loads  iso_3166 with Country name and two digit code
	 * @return
	 * @throws RefsetServiceException
	 */
	public Map<String, String> getISOCountries() throws RefsetServiceException {
		
		LOGGER.debug("getISOCountries");

		Map<String, String> originCountries = new TreeMap<String, String>();

		for (ULocale locale : ULocale.getAvailableLocales()) {
			
			if (!StringUtils.isEmpty(locale.getCountry())) {
				
				originCountries.put(locale.getDisplayCountry(), locale.getCountry());

			}
		}
		
		return originCountries;
	}

	/**Retrieves ISO-639 Languages
	 * @return
	 */
	public Map<String, String> getISOLanguages() {
		LOGGER.debug("getISOLanguages");

		Map<String, String> languages = new TreeMap<String, String>();

		for (ULocale locale : ULocale.getAvailableLocales()) {
			
			languages.put(locale.getDisplayLanguage(locale), locale.getLanguage());
		}
		
		return languages;
	}
}