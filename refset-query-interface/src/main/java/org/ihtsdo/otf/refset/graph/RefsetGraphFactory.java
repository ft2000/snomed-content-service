/**
 * 
 */
package org.ihtsdo.otf.refset.graph;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.GraphFactory;


/** Utility class to get graph server objects so that CRUD operation can be performed on data base
 * @author Episteme Partners
 *
 */
public class RefsetGraphFactory {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RefsetGraphFactory.class);
	
	private static Configuration gsConfig;
	
	private Configuration config;
	
	
	public RefsetGraphFactory(Configuration config) {
		
		this.config = config;
	}
	

	
	public Graph getGraph() {
		
         return GraphFactory.open(gsConfig);
         
         
	}
	
	/** Returns a transactional {@link TitanGraph} 
	 * @return
	 */
	public TitanGraph getTitanGraph() {
		
		TitanGraph og = TitanFactory.open(config);
        LOGGER.info("Returning Transactional Graph {}", og);

        return og;
        
	}
	


}
