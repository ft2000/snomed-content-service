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

import org.ihtsdo.otf.snomed.service.RefsetMetadataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 *
 */
@Component
public class RefsetValidator implements Validator {

	@Autowired
	private RefsetMetadataService mdService;

	/* (non-Javadoc)
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	@Override
	public boolean supports(Class<?> arg0) {

		return RefsetDTO.class.equals(arg0);
	}

	/* (non-Javadoc)
	 * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
	 */
	@Override
	public void validate(Object m, Errors e) {
		
        //ValidationUtils.rejectIfEmpty(e, "effectiveTime", "Effective time can not be empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(e, "moduleId", "Module Id is Mandatory");
        ValidationUtils.rejectIfEmptyOrWhitespace(e, "typeId", "Refset type id is mandatory");
        ValidationUtils.rejectIfEmptyOrWhitespace(e, "active", "Active Flag is mandatory");
        ValidationUtils.rejectIfEmptyOrWhitespace(e, "componentTypeId", "Refset members type id is mandatory");
        ValidationUtils.rejectIfEmptyOrWhitespace(e, "description", "Refset description is mandatory");
        ValidationUtils.rejectIfEmptyOrWhitespace(e, "languageCode", "Refset language is mandatory");
        ValidationUtils.rejectIfEmptyOrWhitespace(e, "published", "Indication of Refset is published or not is mandatory");
        ValidationUtils.rejectIfEmptyOrWhitespace(e, "scope", "Refset use case/scope is mandatory and can not be left empty");
        //ValidationUtils.rejectIfEmpty(e, "originCountry", "Refset origin country is mandatory and can not be left empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(e, "snomedCTVersion", "SNOMED®CT release version is mandatory and can not be left empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(e, "clinicalDomainCode", "Refset clinical domain is mandatory and can not be left empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(e, "expectedReleaseDate", "Expected published date is mandatory");

        //conditional validation
        RefsetDTO r = (RefsetDTO)m;
        
        if (!StringUtils.isEmpty(r.getExternalUrl())) {
			
        	//do simple protocol check
        	if(!r.getExternalUrl().startsWith("http"))  {
        		
        		e.rejectValue("externalUrl", "Refset url must start with http(s)");
        	}

            ValidationUtils.rejectIfEmptyOrWhitespace(e, "externalContact", "Refset contact is mandatory");
            ValidationUtils.rejectIfEmptyOrWhitespace(e, "originCountryCode", "Refset origin country is mandatory and can not be left empty");

		}
        
        if (!mdService.getClinicalDomains().containsKey(r.getClinicalDomainCode())) {
			
    		e.rejectValue("clinicalDomainCode", "Provided refset clinical domain is not supported");

		}
        
        if (!StringUtils.isEmpty(r.getSnomedCTExtensionNs()) && !mdService.getExtensions().containsKey(r.getSnomedCTExtensionNs())) {
			
    		e.rejectValue("snomedCTExtensionNs", "Provided SNOMED®CT Extension namespace is not supported");

		}

        if (!StringUtils.isEmpty(r.getOriginCountryCode()) && !mdService.getISOCountries().containsValue(r.getOriginCountryCode())) {
			
    		e.rejectValue("originCountryCode", "Provided origin country code is not supported");

		}

	}

}
