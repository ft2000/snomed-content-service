package org.ihtsdo.otf.refset.service;

/**
 * @author Episteme Partners
 *
 *Generic interface to update refset through different sparql end points 
 */
public interface RefsetUpdateService {
	
	public String executeUpdate(String query) throws RefsetQueryException;
	
}
