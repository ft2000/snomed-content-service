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
public class MemberValidator implements Validator {

	/* (non-Javadoc)
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	@Override
	public boolean supports(Class<?> arg0) {
		// TODO Auto-generated method stub
		return Member.class.equals(arg0);
	}

	/* (non-Javadoc)
	 * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
	 */
	@Override
	public void validate(Object m, Errors e) {
		
        ValidationUtils.rejectIfEmpty(e, "referencedComponentId", "Referenced component id is mandatory");
        //ValidationUtils.rejectIfEmpty(e, "effectiveTime", "Effective time can not be empty");
        ValidationUtils.rejectIfEmpty(e, "moduleId", "Module Id is Mandatory");
        ValidationUtils.rejectIfEmpty(e, "published", "Published flag is mandatory");
        ValidationUtils.rejectIfEmpty(e, "active", "Active Flag is mandatory");

	}

}
