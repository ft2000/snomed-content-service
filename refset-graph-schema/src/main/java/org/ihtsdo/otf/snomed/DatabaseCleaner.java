package org.ihtsdo.otf.snomed;

import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;
import com.thinkaurelius.titan.core.util.TitanCleanup;

public class DatabaseCleaner {

	public static void main( String[] args ) 
    {
        System.out.println( "Starting to clear titan db. This can not be undone" );
        
        if (args == null || args.length == 0) {
			
        	System.err.println("Titan configuration properties is required to create index");
        	
        	throw new IllegalArgumentException("No Titan configuration");
        	
		}
        //"/Users/gouri/dev/titan-0.5.0-hadoop2/conf/titan-cassandra-es.properties"
        TitanGraph graph = TitanFactory.open(args[0]);


        graph.shutdown();
        TitanCleanup.clear(graph);
        
        
        graph.shutdown();
    }
}
