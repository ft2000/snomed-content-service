/**
 * 
 */
package org.ihtsdo.otf.refset.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.Api;

/**
 * @author Episteme Partners
 *
 */
@RestController
@Api(value="Refset Rest Apis", description="Service to create refset")
@RequestMapping(value = "/v1.0/refset/admin")
public class RefsetAdminController {
	
}
