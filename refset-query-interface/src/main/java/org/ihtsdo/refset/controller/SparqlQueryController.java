package org.ihtsdo.refset.controller;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller to endpoint calls .
 */
@Controller
public class SparqlQueryController {
	
	private static final Logger logger = LoggerFactory.getLogger(SparqlQueryController.class);
	///TBD add service call for different endpoints and add security using spring security later
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/refset/qi/v10", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		logger.info("Welcome home! The client locale is {}.", locale);
		
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		
		String formattedDate = dateFormat.format(date);
		
		model.addAttribute("serverTime", formattedDate );
		
		return "home";
	}
	
}
