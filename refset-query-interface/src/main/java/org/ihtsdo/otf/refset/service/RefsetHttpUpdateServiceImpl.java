/**
 * 
 */
package org.ihtsdo.otf.refset.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Episteme Partners
 *
 */
public class RefsetHttpUpdateServiceImpl implements RefsetUpdateService {
	
	private static final Logger logger = LoggerFactory .getLogger(RefsetHttpUpdateServiceImpl.class);

	private String endpointUri;

	private ProducerTemplate template;
	
	



	@Override
	public String executeUpdate(String query) throws RefsetQueryException {
		// TODO Auto-generated method stub
		
		Exchange source = execute(query);
		
        
    	int responseCode = source.getOut().getHeader(Exchange.HTTP_RESPONSE_CODE, Integer.class);
    	String result = "Unidentified response from server";
    	if (responseCode == 200) {
			
    		result = "Update Successfull";
		}
    	logger.debug(String.format("Result %s", result));
    	
		return result;
	}
	
	
	/**
	 * @param endpointUri the endpointUri to set
	 */
	public void setEndpointUri(String endpointUri) {
		this.endpointUri = endpointUri;
	}

	/**
	 * @param template the template to set
	 */
	public void setTemplate(ProducerTemplate template) {
		this.template = template;
	}
	
	private Exchange execute(final String query) throws RefsetQueryException {
		
		logger.debug(String.format("Query %s", query));
		final Map<String, Object> headers = new HashMap<String,Object>();
	    headers.put(Exchange.HTTP_METHOD, "POST");
	    headers.put (Exchange.ACCEPT_CONTENT_TYPE, "application/sparql-results+json");
	    headers.put (Exchange.CONTENT_TYPE, "application/x-www-form-urlencoded");
	    String body = String.format("update=%s", query);
	    try {
    		
	    	body = String.format("update=%s", URLEncoder.encode(query, "UTF-8"));
			
		} catch (UnsupportedEncodingException e) {
			
			logger.info("Performing query with no encoding. It may result in error");

		}
		logger.debug(String.format(" Query %s", body));
		
	    final String formattedQuery = body; 
		Exchange exchange = template.send(endpointUri, new Processor() {
			
			@Override
			public void process(Exchange exchange) throws Exception {
	             exchange.getIn().setHeaders(headers);
	             exchange.getIn().setBody(formattedQuery);
			}
		});
		if(exchange.isFailed()) {
			
			Exception remoteException = exchange.getException();
			throw new RefsetQueryException(remoteException.getMessage(), remoteException.fillInStackTrace());
		}
		
        return exchange;
	}
	
}
