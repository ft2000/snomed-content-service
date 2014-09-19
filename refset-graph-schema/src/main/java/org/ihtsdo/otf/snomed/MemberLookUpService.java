/**
 * 
 */
package org.ihtsdo.otf.snomed;

import info.aduna.iteration.CloseableIteration;

import java.util.ArrayList;
import java.util.List;

import org.openrdf.model.ValueFactory;
import org.openrdf.query.Binding;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.impl.MapBindingSet;
import org.openrdf.query.parser.ParsedQuery;
import org.openrdf.query.parser.sparql.SPARQLParser;
import org.openrdf.sail.SailConnection;
import org.openrdf.sail.SailException;

import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;
import com.tinkerpop.blueprints.oupls.sail.GraphSail;

/**
 * @author gouri
 *
 */
public class MemberLookUpService {
	
	public void select() throws SailException, MalformedQueryException, QueryEvaluationException {
		
        TitanGraph graph = TitanFactory.open("/Users/gouri/dev/titan-0.5.0-hadoop2/conf/titan-cassandra-es.properties");

        
        GraphSail<TitanGraph> sail = new GraphSail<TitanGraph>(graph);
		sail.enforceUniqueStatements(true);
		try {
			
		
		sail.initialize();
		SailConnection sc = sail.getConnection();
		ValueFactory vf = sail.getValueFactory();
		
		MapBindingSet bindings = new MapBindingSet();
				
		org.openrdf.model.URI uri_1 = vf.createURI("http://sct.snomed.info/109475005");		
		bindings.addBinding("x", uri_1);

				
		SPARQLParser parser = new SPARQLParser();
		CloseableIteration<? extends BindingSet, QueryEvaluationException> sparqlResults;
		String queryString = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
				+ "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
				+ "prefix owl: <http://www.w3.org/2002/07/owl#>"
				+ "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
				+ "prefix sn: <http://sct.snomed.info/#>"
				+ "SELECT ?desc ?do ?dy"
				+ " WHERE { "
				+ "?x ?o ?y. "
				+ "?x sn:description ?desc."
				+ "?desc ?do ?dy"

				+ "}";
		
		
		ParsedQuery query = parser.parseQuery(queryString, "http://sct.snomed.info");
		
		System.out.println("\nSPARQL: " + queryString);
		
		bindings.addBinding("x", uri_1);

		sparqlResults = sc.evaluate(query.getTupleExpr(), query.getDataset(), bindings, false);

		while (sparqlResults.hasNext()) {
			
		    BindingSet bSet = sparqlResults.next();
		    System.err.println("---" + bSet);
		    Binding o = bSet.getBinding("do");
		    Binding y = bSet.getBinding("dy");

	    	System.err.println(o.getValue() + ", " + y.getValue().stringValue());

		    
		}
		
		
		System.out.println("====================\n");
		
		sparqlResults.close();
		


		sc.close();
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			
			sail.shutDown();
			
			graph.shutdown();

		}
		
		

	}
	
	public void construct() {

        TitanGraph graph = TitanFactory.open("/Users/gouri/dev/titan-0.5.0-hadoop2/conf/titan-cassandra-es.properties");

        
        GraphSail<TitanGraph> sail = new GraphSail<TitanGraph>(graph);
		sail.enforceUniqueStatements(true);
		try {
			
		
		sail.initialize();
		SailConnection sc = sail.getConnection();
		ValueFactory vf = sail.getValueFactory();
		
		MapBindingSet bindings = new MapBindingSet();
				
		org.openrdf.model.URI uri_1 = vf.createURI("http://sct.snomed.info/109475005");		
		bindings.addBinding("x", uri_1);

				
		SPARQLParser parser = new SPARQLParser();
		CloseableIteration<? extends BindingSet, QueryEvaluationException> sparqlResults;
		String queryString = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
				+ "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
				+ "prefix owl: <http://www.w3.org/2002/07/owl#>"
				+ "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
				+ "prefix sn: <http://sct.snomed.info/#>"
				+ "CONSTRUCT{"
				+ "?x ?o ?y.\n"
				+ "?desc ?do ?dy. \n"
				+ "?type ?to ?ty. \n"
				+ "}"
				+ " WHERE { "
				+ "?x ?o ?y. \n"
				+ "?x sn:description ?desc.\n"
				+ "?desc ?do ?dy .\n"
				+ "?desc sn:type ?type. \n"
				+ "?type ?to ?ty. \n"

				+ "}";
		
		
		ParsedQuery query = parser.parseQuery(queryString, "http://sct.snomed.info");
		
		System.out.println("\nSPARQL: " + queryString);
		
		bindings.addBinding("x", uri_1);

		sparqlResults = sc.evaluate(query.getTupleExpr(), query.getDataset(), bindings, false);

		while (sparqlResults.hasNext()) {
			
		    BindingSet bSet = sparqlResults.next();
		   // System.err.println("---" + bSet);
		    Binding o = bSet.getBinding("subject");
		    Binding y = bSet.getBinding("object");
		    Binding p = bSet.getBinding("predicate");

	    	System.err.println("Subject "+  o.getValue() + ", " + y.getValue().stringValue() + "," + p.getValue().stringValue());

		    
		}
		
		
		System.out.println("====================\n");
		
		sparqlResults.close();
		


		sc.close();
		} catch(Exception e) {
			
			e.printStackTrace();
			
		} finally {
			
			try {
				sail.shutDown();
				
			} catch (SailException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			graph.shutdown();

		}
		
	}

}
