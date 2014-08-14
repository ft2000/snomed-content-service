/**
 * 
 */
package org.ihtsdo.otf.refset.controller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.IOUtils;

/**
 * @author Episteme Partners
 *
 */
public class RestData {
	
	private static String CONCEPT_1068770 = "src/test/resources/concept_106877009.json";
	private static String STATUS_1068770 = "src/test/resources/status_106877009.json";
	private static String EFFECTIVE_DATE_1068770 = "src/test/resources/effective_date_106877009.json";

	public static String getConceptDetails() throws FileNotFoundException, IOException {
		
		String json = IOUtils.toString(new FileInputStream(CONCEPT_1068770));
		return json;
		
	}
	
	public static String getStatus() throws FileNotFoundException, IOException {
		
		String json = IOUtils.toString(new FileInputStream(STATUS_1068770));
		return json;
		
	}
	
	public static String getEffectiveDate() throws FileNotFoundException, IOException {
		
		String json = IOUtils.toString(new FileInputStream(EFFECTIVE_DATE_1068770));
		return json;
		
	}

}
