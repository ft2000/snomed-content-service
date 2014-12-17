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
import com.tinkerpop.blueprints.util.wrappers.event.EventGraph;
import com.tinkerpop.blueprints.util.wrappers.event.EventTransactionalGraph;
import com.tinkerpop.blueprints.util.wrappers.event.listener.ConsoleGraphChangedListener;
import com.tinkerpop.frames.FramedTransactionalGraph;


/** Utility class to get graph server objects so that CRUD operation can be performed on data base
 * @author Episteme Partners
 *
 */
public class RefsetGraphFactory {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RefsetGraphFactory.class);
	
	private static Configuration gsConfig;
	
	private Configuration config;

	private TitanGraph tg;
	
	
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
		
		if (this.tg == null) {
			
	        LOGGER.info("Create new instance of graph");

			this.tg = TitanFactory.open(config);
		}
		
		if (this.tg == null) {
			
			throw new IllegalArgumentException("Graph not initialized, please check provide confiuration");
		}
		TitanGraph tg =  this.tg;
        LOGGER.trace("Returning Transactional Graph {}", tg);

        return tg;
        
	}
	
	/** Returns a readonly {@link TitanGraph} 
	 * @return
	 */
	public TitanGraph getReadOnlyGraph() {
		
		TitanGraph tg = getTitanGraph();
		tg.buildTransaction().readOnly();
		
        LOGGER.trace("Returning readonly Graph {}", tg);

		return tg;
        
	}
	
	public static void shutdown(Graph g) {
		
		//LOGGER.info("Shutting down graph {}", g);
		
		//if (g != null) g.shutdown();//shutdown is not required for titan.Commit or rollback should clear resources
		
	}

	
	public static void rollback(Graph g) {
		
		LOGGER.debug("rollback {}", g);

		if (g instanceof TitanGraph) {
			
			if (g != null) ((TitanGraph)g).rollback();

		} else if (g instanceof FramedTransactionalGraph) {
			
			if (g != null) {
				
				@SuppressWarnings("unchecked")
				FramedTransactionalGraph<TitanGraph> framedTransactionalGraph = (FramedTransactionalGraph<TitanGraph>)g;
				framedTransactionalGraph.rollback();
			}

		} else if (g instanceof EventTransactionalGraph) {
			
			if (g != null) {
				
				@SuppressWarnings("unchecked")
				EventTransactionalGraph<TitanGraph> etg = (EventTransactionalGraph<TitanGraph>)g;
				etg.rollback();
			}

		} else {
			
			LOGGER.trace("No rollback unknown graph {}", g);

		}
		
	}
	
	public static void commit(Graph g) {
		
		LOGGER.trace("commit {}", g);
		if (g instanceof TitanGraph) {
			
			if (g != null) ((TitanGraph)g).commit();

		} else if (g instanceof FramedTransactionalGraph) {
			
			if (g != null) {
				
				@SuppressWarnings("unchecked")
				FramedTransactionalGraph<TitanGraph> ftg = (FramedTransactionalGraph<TitanGraph>)g;
				ftg.commit();
				LOGGER.trace("commit {}", g);

			}

		} else if (g instanceof EventTransactionalGraph) {
			
			if (g != null) {
				
				@SuppressWarnings("unchecked")
				EventTransactionalGraph<TitanGraph> etg = (EventTransactionalGraph<TitanGraph>)g;
				etg.commit();
				LOGGER.trace("commit {}", g);

			}

		} else {
			
			LOGGER.debug("Not committing unknown graph {}", g);

		}
  

		
	}
	
	/** Returns a transactional {@link TitanGraph} 
	 * @return
	 */
	public EventGraph<TitanGraph> getEventGraph() {
		
		if (this.tg == null) {
			
	        LOGGER.info("Create new instance of graph");

			this.tg = TitanFactory.open(config);
		}
		
		if (this.tg == null) {
			
			throw new IllegalArgumentException("Graph not initialized, please check provide confiuration");
		}
		TitanGraph tg =  this.tg;
        LOGGER.trace("Returning Transactional Graph {}", tg);
        EventGraph<TitanGraph> eg = new EventTransactionalGraph<TitanGraph>(tg);
        if (LOGGER.isDebugEnabled()) {
			
            eg.addListener(new ConsoleGraphChangedListener(eg.getBaseGraph()));

		}
        return eg;
        
	}
	


}
