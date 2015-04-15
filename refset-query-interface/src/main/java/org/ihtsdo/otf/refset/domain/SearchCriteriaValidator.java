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

import java.util.Set;

import org.ihtsdo.otf.refset.common.SearchCriteriaDTO;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 *
 */
@Component
public class SearchCriteriaValidator implements Validator {

	/* (non-Javadoc)
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	@Override
	public boolean supports(Class<?> clazz) {

		return SearchCriteriaDTO.class.equals(clazz);
	}

	/* (non-Javadoc)
	 * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
	 */
	@Override
	public void validate(Object target, Errors errors) {

		ValidationUtils.rejectIfEmpty(errors, "fields", "At least one search criteria is required");
		
		SearchCriteriaDTO dto = (SearchCriteriaDTO)target;
		
		Set<String> keys = dto.getFields().keySet();
		StringBuilder sb = new StringBuilder();
		boolean moreThenOne = false;
		
		for (String key : keys) {
			
			if (moreThenOne) {
				
				sb.append(",");
			}
			sb.append(key);
			moreThenOne = true;
			
		}
		if (StringUtils.isEmpty(sb.toString())) {
			
			errors.rejectValue("fields", sb.toString() + " field(s) are not valid in search criteria");

		}

	}

}
