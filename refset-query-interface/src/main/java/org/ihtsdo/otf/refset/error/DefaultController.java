package org.ihtsdo.otf.refset.error;

import javax.servlet.http.HttpServletRequest;

import org.ihtsdo.otf.refset.exception.InvalidServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mangofactory.swagger.annotations.ApiIgnore;

/**
 * Handles requests not mapped with in the application.
 */
@Controller
@ApiIgnore
public class DefaultController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultController.class);
	
	@RequestMapping(value = "/v1.0/**")
	public String handleUnknownRequest(HttpServletRequest req) {
		
		String message = String.format("There is no service available for request "
				+ "path %s and %s method type", req.getRequestURI(), req.getMethod());
		LOGGER.info("{}.", message);
		
		throw new InvalidServiceException(message);
		
	}
	
}
