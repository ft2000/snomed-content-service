package org.ihtsdo.otf.refset.service;

import java.io.OutputStream;

/**
 * @author Episteme Partners
 *
 *Generic interface to call different sparql end points 
 */
public interface RefsetQueryService {
	
	public String executeQuery(String query, String outputType) throws RefsetQueryException;

	public void executeQuery(String query, OutputStream out, String outputType) throws RefsetQueryException;
	
}
