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

import org.hibernate.validator.internal.constraintvalidators.URLValidator;
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

	/* (non-Javadoc)
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	@Override
	public boolean supports(Class<?> arg0) {

		return Refset.class.equals(arg0);
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
        ValidationUtils.rejectIfEmptyOrWhitespace(e, "clinicalDomain", "Refset clinical domain is mandatory and can not be left empty");
        
        //conditional validation
        Refset r = (Refset)m;
        
        if (!StringUtils.isEmpty(r.getExternalUrl())) {
			
        	//do simple protocol check
        	if(!r.getExternalUrl().startsWith("http"))  {
        		
        		e.rejectValue("externalUrl", "Refset url must start with http(s)");
        	}

            ValidationUtils.rejectIfEmptyOrWhitespace(e, "externalContact", "Refset contact is mandatory");
            ValidationUtils.rejectIfEmptyOrWhitespace(e, "originCountry", "Refset origin country is mandatory and can not be left empty");

		}

	}

}
