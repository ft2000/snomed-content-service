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
package org.ihtsdo.otf.refset.api.authentication;

import java.util.HashMap;
import java.util.Map;

import org.ihtsdo.otf.refset.common.Result;
import org.ihtsdo.otf.refset.common.Utility;
import org.ihtsdo.otf.refset.security.RefsetAuthenticationProvider;
import org.ihtsdo.otf.refset.security.RefsetIdentityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

/**Controller to provide authentication and authorization services
 *
 */
@RestController
@Api(value="User Logout service", description="Service to logout user")
public class LogoutController {
	
	private static final Logger logger = LoggerFactory.getLogger(LogoutController.class);

	private static final String SUCCESS = "Success";

	@Autowired
	RefsetAuthenticationProvider provider;
	
	@Autowired
	RefsetIdentityService service;
	
	@RequestMapping( method = RequestMethod.POST, value = "/logout",  produces = "application/json", consumes = "application/json")
	@ApiOperation( value = "Logout a user for given X-REFSET-AUTH-TOKEN ",
			notes = "This api call just clears the current security context for X-REFSET-AUTH-TOKEN ")
    public ResponseEntity<Result< Map<String, Object>>> logout() throws Exception {
		
		logger.debug("logout user {}");

		Result<Map<String, Object>> r = Utility.getResult();
		
		Map<String, Object> data = new HashMap<String, Object>();
		r.setData(data);
		SecurityContextHolder.getContext().setAuthentication(null);
		r.getMeta().setMessage(SUCCESS);
		r.getMeta().setStatus(HttpStatus.OK);

		return new ResponseEntity<Result<Map<String,Object>>>(r, HttpStatus.OK);
	}	
	

}
