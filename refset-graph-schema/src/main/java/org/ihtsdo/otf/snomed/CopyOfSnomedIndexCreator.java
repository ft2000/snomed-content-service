package org.ihtsdo.otf.snomed;

import java.net.URI;
import java.util.Set;

import org.openrdf.model.Literal;

import com.thinkaurelius.titan.core.PropertyKey;
import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;
import com.thinkaurelius.titan.core.schema.Mapping;
import com.thinkaurelius.titan.core.schema.Parameter;
import com.thinkaurelius.titan.core.schema.TitanGraphIndex;
import com.thinkaurelius.titan.core.schema.TitanManagement;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;

public class CopyOfSnomedIndexCreator {

	public static void main( String[] args ) 
    {
        System.out.println( "Starting index creation!" );
        
        if (args == null || args.length == 0) {
			
        	System.err.println("Titan configuration properties is required to create index");
        	
        	throw new IllegalArgumentException("No Titan configuration");
        	
		}
        //"/Users/gouri/dev/titan-0.5.0-hadoop2/conf/titan-cassandra-es.properties"
        TitanGraph graph = TitanFactory.open(args[0]);

        
        TitanManagement mgmt = graph.getManagementSystem();
        
        PropertyKey value = mgmt.getPropertyKey("value");
        
        if(value == null) {
        	
        	value = mgmt.makePropertyKey("value").dataType(URI.class).make();
        }    
                        


        mgmt.buildIndex("value", Vertex.class).addKey(value).buildCompositeIndex();
        
        System.err.println("Commiting value index");
        
        
        PropertyKey c = mgmt.getPropertyKey("c");

        if (c == null) {
			
        	c = mgmt.makePropertyKey("c").dataType(URI.class).make();
		}
        
        PropertyKey p = mgmt.getPropertyKey("p");

        if (p == null) {
			
        	p = mgmt.makePropertyKey("p").dataType(Literal.class).make();
		}
        
        PropertyKey pc = mgmt.getPropertyKey("pc");

        if (pc == null) {
			
        	pc = mgmt.makePropertyKey("pc").dataType(URI.class).make();
		}
        

        mgmt.buildIndex("c", Edge.class).addKey(c).buildCompositeIndex();
        mgmt.buildIndex("p", Edge.class).addKey(p).buildCompositeIndex();
        mgmt.buildIndex("pc", Edge.class).addKey(pc).buildCompositeIndex();
        
        System.err.println("Commiting c, p , pc indexes");
        mgmt.commit();
       
        mgmt = graph.getManagementSystem();

        Iterable<TitanGraphIndex>  vindexes = mgmt.getGraphIndexes(Vertex.class);
        System.out.println("Verifying all the newly created index by printing all indexes");
        for (TitanGraphIndex titanGraphIndex : vindexes) {
			
        	System.err.println("----vertext index --- " + titanGraphIndex.getName());
		}
        
        
        Iterable<TitanGraphIndex>  eindexes = mgmt.getGraphIndexes(Edge.class);
        
        for (TitanGraphIndex titanGraphIndex : eindexes) {
			
        	System.err.println("----Edge index --- " + titanGraphIndex.getName());
		}
        
        Set<String> keys = graph.getIndexedKeys(Vertex.class);

        for (String string : keys) {
        	System.err.println("--ss-vertext index --- " + string);

		}
        graph.commit();
        graph.shutdown();
    }
}
