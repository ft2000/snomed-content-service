/**
 * 
 */
package org.ihtsdo.otf.refset.graph;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.GraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;


/** Utility class to get graph server objects so that CRUD operation can be performed on data base
 * @author Episteme Partners
 *
 */
@Component
public class RefsetGraphFactory {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RefsetGraphFactory.class);
	
	private static Configuration gsConfig;
	
	@Autowired
	private Configuration config;
	
	@Resource(name = "refsetGraphOrientdbFactory")
	private OrientGraphFactory f;
	
	@PostConstruct
	public void init() {
		
		gsConfig = config;
		
		LOGGER.info("Initializing Graph configuration {}", gsConfig);
	}
	
	
	public static Graph getGraph() {
		
         return GraphFactory.open(gsConfig);
         
         
	}
	
	/** Returns a transactional {@link OrientGraph} 
	 * @return
	 */
	public OrientGraph getOrientGraph() {
		
        OrientGraph og = f.getTx();
        LOGGER.info("Returning Transactional Graph {}", og);

        return og;
        
	}
	
	/** Returns a non transactional {@link OrientGraph} 
	 * @return
	 */
	public Graph getNoTxOrientGraph() {
		
        OrientGraphNoTx og = f.getNoTx();
        
        LOGGER.info("Returning No Transaction Graph {}", og);

        return og;
        
	}
	
	


}
