/**
 * 
 */
package org.ihtsdo.otf.refset.common;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * @author Episteme Partners
 *
 */
public class Utility {

	/**Convert given {@link DateTime} in yyyyMMdd format
	 * @param dt
	 * @return
	 */
	public static String getDate(DateTime dt) {
		
		if(dt != null) {
			
			DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyyMMdd");
			String date = dt.toString(fmt);
			
			return date;

		}

		return null;
	}
	
}
