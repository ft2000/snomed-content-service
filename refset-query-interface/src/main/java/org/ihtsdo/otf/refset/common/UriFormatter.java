/**
 * 
 */
package org.ihtsdo.otf.refset.common;

import java.util.EnumMap;

/**
 * @author Episteme Partners
 *
 */
public class UriFormatter {
	
	private static EnumMap<URIFormats, String> formats;

	
	
	/**
	 * @param formats the formats to set
	 */
	public void setFormats(EnumMap<URIFormats, String> formats) {
		UriFormatter.formats = formats;
	}

	public static String getNamedGraphUri(String moduleId, String releaseId) {
		
		return String.format(formats.get(URIFormats.namedGraphUriFmt), moduleId, releaseId);
	}
	
	public static String getReleaseUri(String moduleId, String releaseId) {
		
		return String.format(formats.get(URIFormats.releaseUriFmt), moduleId, releaseId);
		
	}
	
	public static String getBaseUri() {
		
		return formats.get(URIFormats.baseUri);
		
	}
	
	public static String getConceptUri(String sctId) {
		
		return String.format(formats.get(URIFormats.conceptUriFmt), sctId);
		
	}
	
	public static String getModuletUri(String moduleId) {
		
		return String.format(formats.get(URIFormats.moduleUriFmt), moduleId);

	}
	
	

}
