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

import org.ihtsdo.otf.im.domain.IHTSDOUser;
import org.ihtsdo.otf.refset.common.Result;
import org.ihtsdo.otf.refset.common.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

/**Controller to provide authentication and authorization services
 *
 */
@RestController
@Api(value="Authentication", description="Service to authenticate & authorize user and get details of user")
@RequestMapping("/v1/refsets")
public class UserController {
	
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	private static final String SUCCESS = "Success";

	@RequestMapping( method = RequestMethod.POST, value = "/getUserDetails",  produces = "application/json", consumes = "application/json")
	@ApiOperation( value = "Retrieves user details for an already authenticated user.",
			notes = "Call to get user profile details")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<Result< Map<String, Object>>> login() throws Exception {
		
		logger.debug("authenticating user {}");

		Result<Map<String, Object>> r = Utility.getResult();
		Map<String, Object> data = new HashMap<String, Object>();
		IHTSDOUser u = org.ihtsdo.otf.refset.common.Utility.getUserDetails();
		data.put("user", u);
		r.setData(data);

		r.getMeta().setMessage(SUCCESS);
		r.getMeta().setStatus(HttpStatus.OK);

		return new ResponseEntity<Result<Map<String,Object>>>(r, HttpStatus.OK);
    }	
	

}