/**
 * 
 */
package org.ihtsdo.otf.refset.graph;

import javax.annotation.PostConstruct;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.GraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;


/** Utility class to get graph server objects so that CRUD operation can be performed on data base
 * @author Episteme Partners
 *
 */
@Component
public class RefsetGraphFactory {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RefsetGraphFactory.class);
	
	private static final String OG_URL = "blueprints.orientdb.url";
	private static final String OG_USER = "blueprints.orientdb.username";
	private static final String OG_PASSWORD = "blueprints.orientdb.password";
	private static final String OG_CON_POOL_MIN = "blueprints.orientdb.pool.min";
	private static final String OG_CON_POOL_MAX = "blueprints.orientdb.pool.max";

	private static Configuration gsConfig;
	
	@Autowired
	private Configuration config;
	
	@PostConstruct
	public void init() {
		
		gsConfig = config;
		
		LOGGER.debug("Initializing Graph configuration");
	}
	
	
	public static Graph getGraph() {
		
         return GraphFactory.open(gsConfig);
         
         
	}
	
	public OrientGraph getOrientGraph() {
		
        return getOgFactory().getTx();
        
        
	}
	
	
	private static OrientGraphFactory getOgFactory() {
		
		return new OrientGraphFactory(gsConfig.getString(OG_URL), 
				gsConfig.getString(OG_USER), gsConfig.getString(OG_PASSWORD))
				.setupPool(gsConfig.getInt(OG_CON_POOL_MIN), 
						gsConfig.getInt(OG_CON_POOL_MAX));
	}

}
