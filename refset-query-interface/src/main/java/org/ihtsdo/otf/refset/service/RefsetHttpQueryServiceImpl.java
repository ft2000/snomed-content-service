/**
 * 
 */
package org.ihtsdo.otf.refset.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Episteme Partners
 *
 */
public class RefsetHttpQueryServiceImpl implements RefsetQueryService {
	
	private static final Logger logger = LoggerFactory .getLogger(RefsetHttpQueryServiceImpl.class);

	private String endpointUri;

	private ProducerTemplate template;
	


	/* (non-Javadoc)
	 * @see org.ihtsdo.otf.refset.service.RefsetQueryService#executeQuery(java.lang.String)
	 */
	@Override
	public String executeQuery(String query, String outputType) throws RefsetQueryException {
		// TODO Auto-generated method stub
		
		Exchange source = execute(query, outputType);
		
        
        try {
        	
        	String result = IOUtils.toString(source.getOut().getBody(InputStream.class));
        	logger.debug(String.format("Result %s", result));
        	
			return result;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new RefsetQueryException(e.getMessage(), e);
		}
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


	@Override
	public void executeQuery(String query, OutputStream out, String outputType)
			throws RefsetQueryException {
		
		Exchange source = execute(query, outputType);
		
		try {
			
			IOUtils.copy(source.getOut().getBody(InputStream.class), out);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new RefsetQueryException(e.getMessage(), e);
		}
	}
	
	
	private Exchange execute(final String query, final String output) throws RefsetQueryException {
		
		logger.debug(String.format("Query %s", query));
		final Map<String, Object> headers = new HashMap<String,Object>();
	    headers.put(Exchange.HTTP_METHOD, "GET");
	    headers.put(Exchange.CONTENT_TYPE, "application/xml");
	    
    	try {
    		
    		String encoded = String.format("query=%s&output=%s", URLEncoder.encode(query, "UTF-8"), output);
    		logger.debug(String.format("encoded Query %s", encoded));

    		headers.put(Exchange.HTTP_QUERY, encoded);
			
		} catch (UnsupportedEncodingException e) {
			
			logger.info("Performing query with no encoding. It may result in error");
    		String formatted = String.format("query=%s&output=%s", query, output);
    		logger.debug(String.format(" Query %s", formatted));

    		headers.put(Exchange.HTTP_QUERY, formatted);
		}

	     
		Exchange exchange = template.send(endpointUri, new Processor() {
			
			@Override
			public void process(Exchange exchange) throws Exception {
	             exchange.getIn().setHeaders(headers);				
			}
		});
		
		if(exchange.isFailed()) {
			//TODO need more sophisticated handling here
			Exception remoteException = exchange.getException();
			logger.error(remoteException.getMessage(), remoteException.fillInStackTrace());
			throw new RefsetQueryException(remoteException.getMessage(), remoteException.fillInStackTrace());
		}
		
        return exchange;
	}
	

}
