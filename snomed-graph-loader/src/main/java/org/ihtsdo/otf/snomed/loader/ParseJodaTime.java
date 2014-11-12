/**
 * 
 */
package org.ihtsdo.otf.snomed.loader;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.util.CsvContext;

/**Convert valid string format yyyyMMdd to {@link DateTime}
 * @author Episteme Partners
 *
 */
public class ParseJodaTime extends CellProcessorAdaptor implements StringCellProcessor {

	private DateTimeFormatter fmt;

	/* (non-Javadoc)
	 * @see org.supercsv.cellprocessor.ift.CellProcessor#execute(java.lang.Object, org.supercsv.util.CsvContext)
	 */
	
	public Object execute(Object value, CsvContext context) {
		
		validateInputNotNull(value, context);
		final DateTime result;
				
		try {
			
			result = fmt.parseDateTime((String) value);
			
		} catch (Exception e) {
			
			throw new SuperCsvCellProcessorException( 
	        		String.format("Could not parse '%s' as a joda time", value), context, this);
			
		}
		
		return next.execute(result, context);

	}
	
	public ParseJodaTime(String format) {

		super();

		this.fmt = DateTimeFormat.forPattern(format);

	}

	public ParseJodaTime(String format, CellProcessor next) {
        
		super(next);
		this.fmt = DateTimeFormat.forPattern(format);

	}


}
