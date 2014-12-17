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

import org.springframework.stereotype.Component;
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
        ValidationUtils.rejectIfEmpty(e, "moduleId", "Module Id is Mandatory");
        ValidationUtils.rejectIfEmpty(e, "typeId", "Refset type id is mandatory");
        ValidationUtils.rejectIfEmpty(e, "active", "Active Flag is mandatory");
        ValidationUtils.rejectIfEmpty(e, "componentTypeId", "Refset members type id is mandatory");
        ValidationUtils.rejectIfEmpty(e, "description", "Refset description is mandatory");
        ValidationUtils.rejectIfEmpty(e, "languageCode", "Refset language is mandatory");
        ValidationUtils.rejectIfEmpty(e, "published", "Indication of Refset is published or not is mandatory");

	}

}
