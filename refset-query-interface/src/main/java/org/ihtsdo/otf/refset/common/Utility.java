/**
 * 
 */
package org.ihtsdo.otf.refset.common;

import java.nio.file.AccessDeniedException;
import java.util.Map;

import org.ihtsdo.otf.refset.security.User;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

/**
 * Utility class to support common function required at various lavel
 */
public class Utility {

	private static final DateTimeFormatter YYYY_MM_DD_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd");
	private static final String SUCESS = "Success";

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
	
	/**{@link Result} container for response returned from controller
	 * @return
	 */
	public static Result<Map<String, Object>> getResult() {
		Result<Map<String, Object>> result = new Result<Map<String, Object>>();
		
		Meta m = new Meta();
		m.setMessage(SUCESS);
		m.setStatus(HttpStatus.OK);
		
		result.setMeta(m);

		return result;
	}
	
	/**To convert string date to {@link DateTime}
	 * @param fromDate
	 * @return
	 */
	public DateTime getFromDate(String fromDate, int daysOffset) {
		
		DateTime fromDt = new DateTime().minusDays(daysOffset);
		if (!StringUtils.isEmpty(fromDate)) {
			
			fromDt = YYYY_MM_DD_FORMATTER.parseDateTime(fromDate);
		}
		
		return fromDt;
	}
	
	/**To convert string date to {@link DateTime}
	 * @param toDate
	 * @return
	 */
	public DateTime getToDate(String toDate) {
		
		DateTime toDt = new DateTime();
		if (!StringUtils.isEmpty(toDate)) {
			
			toDt = YYYY_MM_DD_FORMATTER.parseDateTime(toDate);
		}
		
		return toDt;

	}
	
	/**
	 * @return
	 * @throws AccessDeniedException
	 */
	public static User getUserDetails() throws AccessDeniedException {
		Object auth = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		if (auth instanceof User) {
			
			User user = (User) auth;
			return user;
		}
		
		throw new AccessDeniedException("User lacks required authorization");
	}
	
}
