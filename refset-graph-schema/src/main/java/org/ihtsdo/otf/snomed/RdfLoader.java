package org.ihtsdo.otf.snomed;

import java.io.File;

import org.openrdf.sail.SailException;

import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;
import com.tinkerpop.blueprints.oupls.sail.GraphSail;
import com.tinkerpop.blueprints.oupls.sail.SailLoader;

/**
 * Hello world!
 *
 */
public class RdfLoader 
{
    public static void main( String[] args ) 
    {
    	
        System.out.println( "Loading rdf" );    	
        if (args == null || args.length != 2) {
			
        	System.err.println("Titan configuration properties is required, and file to load rdf data is required");
        	
        	throw new IllegalArgumentException("No Titan configuration");
        	
		}
        //"/Users/gouri/dev/titan-0.5.0-hadoop2/conf/titan-cassandra-es.properties"
        TitanGraph graph = TitanFactory.open(args[0]);

        GraphSail<TitanGraph> sail = new GraphSail<TitanGraph>(graph);
        
        sail.enforceUniqueStatements(true);
        
        SailLoader loader = new SailLoader(sail);
        loader.setBaseUri("http://sct.snomed.info");
        loader.setVerbose(true);
        loader.setBufferSize(1000);
        loader.setLoggingBufferSize(10000);

        try {
        	sail.initialize();
        	//"/Users/gouri/dev/titan-0.5.0-hadoop2/examples/out_Snapshot-en_INT_20140131.sorted.nq.gz"
        	
			loader.load(new File(args[1]));//new File("/tmp/_Snapshot-en_INT_20140131.rdf"));
			System.err.println("Finished loading, commiting");

			
			graph.commit();
			
        } catch (Exception e) {
        	
			System.err.println("------error in loading");
			graph.rollback();
			
			e.printStackTrace();
			
		} finally {
			
			try {
				System.out.println("Shutting down sail");

				sail.shutDown();
				
			} catch (SailException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Shutting down graph");

			graph.commit();
			//graph.shutdown();
			
		}
        
    }
}
