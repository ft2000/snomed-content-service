/**
 * 
 */
package org.ihtsdo.otf.snomed.service;

import info.aduna.iteration.CloseableIteration;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.configuration.Configuration;
import org.ihtsdo.otf.refset.exception.EntityNotFoundException;
import org.ihtsdo.otf.snomed.domain.Concept;
import org.ihtsdo.otf.snomed.exception.ConceptServiceException;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.Binding;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.impl.EmptyBindingSet;
import org.openrdf.query.impl.MapBindingSet;
import org.openrdf.query.parser.ParsedQuery;
import org.openrdf.query.parser.sparql.SPARQLParser;
import org.openrdf.sail.Sail;
import org.openrdf.sail.SailConnection;
import org.openrdf.sail.SailException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;
import com.tinkerpop.blueprints.oupls.sail.GraphSail;

/**
 * @author Episteme Partners
 *
 */
public class ConceptLookUpServiceImpl implements ConceptLookupService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ConceptLookUpServiceImpl.class);
	
	private static final String BASE_URI = "http://sct.snomed.info/";
	
	private TitanGraph graph;
	
	private static final String Q_CONCEPT_ID = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
			+ "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
			+ "prefix owl: <http://www.w3.org/2002/07/owl#>"
			+ "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
			+ "prefix sn: <http://sct.snomed.info/#>"
			+ "SELECT DISTINCT ?x "
			+ " WHERE { "
			+ "?x ?o ?y. \n"
			+ "?x sn:description ?desc. \n"
			+ "?desc ?do ?dy \n"
			+ "} limit %d \n OFFSET %d ";
	
	private static final String Q_CONCEPT = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
			+ "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
			+ "prefix owl: <http://www.w3.org/2002/07/owl#>"
			+ "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
			+ "prefix sn: <http://sct.snomed.info/#>"
			+ "SELECT ?x ?o ?y "
			+ " WHERE { "
			+ "?x ?o ?y. \n"
			+ "?x sn:description ?desc.\n"
			+ "?desc ?do ?dy \n"
			+ "}";

	private String repositoryConfig;
	
	private Configuration config;

	/* (non-Javadoc)
	 * @see org.ihtsdo.otf.snomed.service.ConceptLookupService#getConcepts(java.util.List)
	 */
	@Override
	public Map<String, Concept> getConcepts(Set<String> conceptIds)
			throws ConceptServiceException {

		LOGGER.debug("getting concepts details for {}", conceptIds);
		
		Map<String, Concept> concepts = new HashMap<String, Concept>();
		
		TitanGraph g = null;
		Sail sail = null;
		SailConnection sc = null;
		try {
			g = getGraph();
			
			sail = getSail(g);
			
			sail.initialize();
			
			sc = sail.getConnection();
			
			SPARQLParser parser = new SPARQLParser();
			CloseableIteration<? extends BindingSet, QueryEvaluationException> sparqlResults;
			
			
			ParsedQuery sparql = parser.parseQuery(Q_CONCEPT, "http://sct.snomed.info");
			
			ValueFactory vf = sail.getValueFactory();
			
			
			for (String id : conceptIds) {
				
				if (StringUtils.isEmpty(id)) {
					
					//ignore all null keys or empty keys
					LOGGER.debug("ingoring {} for concepts details as invalid", id);

					continue;
				}
				
				URI uri = vf.createURI(BASE_URI + id);
				
				MapBindingSet bindings = new MapBindingSet();
				
				bindings.addBinding("x", uri);
				

				sparqlResults = sc.evaluate(sparql.getTupleExpr(), sparql.getDataset(), bindings, true);
				
				Concept c = getConcept(sparqlResults);
				concepts.put(id, c);
						
				
			}
		} catch (Exception e) {
			
			LOGGER.error("Error duing concept details for concept map fetch", e);
			
			throw new ConceptServiceException(e);
			
		} finally {
			
			close(sc);
			close(sail);
			close(g);
			
		}
		
		LOGGER.debug("returning total {} concepts ", concepts.size());

		return Collections.unmodifiableMap(concepts);
	}

	/* (non-Javadoc)
	 * @see org.ihtsdo.otf.snomed.service.ConceptLookupService#getConcept(java.lang.String)
	 */
	@Override
	public Concept getConcept(String conceptId) throws ConceptServiceException,
			EntityNotFoundException {
		
		LOGGER.debug("getting concept details for {} ", conceptId);

		if (StringUtils.isEmpty(conceptId)) {
			
			throw new EntityNotFoundException(String.format("Invalid concept id", conceptId));
		}
		
		TitanGraph g = null;
		Sail sail = null;
		SailConnection sc = null;
		try {
			
				g = getGraph();
				
				sail = getSail(g);
				
				sail.initialize();
				
				sc = sail.getConnection();
				
				SPARQLParser parser = new SPARQLParser();
				CloseableIteration<? extends BindingSet, QueryEvaluationException> sparqlResults;
				
				
				ParsedQuery sparql = parser.parseQuery(Q_CONCEPT, "http://sct.snomed.info");
				
				ValueFactory vf = sail.getValueFactory();
				URI uri = vf.createURI(BASE_URI + conceptId);
				
				MapBindingSet bindings = new MapBindingSet();
				
				bindings.addBinding("x", uri);
				

				sparqlResults = sc.evaluate(sparql.getTupleExpr(), sparql.getDataset(), bindings, true);
				
				return getConcept(sparqlResults);
				
				
		} catch (Exception e) {
			
			LOGGER.error("Error duing concept details fetch", e);
			
			throw new ConceptServiceException(e);
			
		} finally {
			
			close(sc);
			close(sail);
			close(g);
		}
		
	}

	/* (non-Javadoc)
	 * @see org.ihtsdo.otf.snomed.service.ConceptLookupService#getConceptIds(int, int)
	 */
	@Override
	public Set<String> getConceptIds(int offset, int limit)
			throws ConceptServiceException {
		LOGGER.debug("getting concept ids with offset {} and limit {} ", offset, limit);

		TreeSet<String> conceptIds = new TreeSet<String>();
		
		TitanGraph g = null;
		Sail sail = null;
		SailConnection sc = null;
		try {
			
				g = getGraph();
				
				sail = getSail(g);
				
				sail.initialize();
				
				sc = sail.getConnection();
				
				SPARQLParser parser = new SPARQLParser();
				CloseableIteration<? extends BindingSet, QueryEvaluationException> sparqlResults;
				
				limit = limit <= 0 ? 4000 : limit;
				
				offset = limit <= 0 ? 1 : offset;

				String query = String.format(Q_CONCEPT_ID, limit, offset);
				
				
				ParsedQuery sparql = parser.parseQuery(query, "http://sct.snomed.info");

				sparqlResults = sc.evaluate(sparql.getTupleExpr(), sparql.getDataset(), new EmptyBindingSet(), true);
				
				while (sparqlResults.hasNext()) {
					
					BindingSet binding = sparqlResults.next();
					
					if (binding.hasBinding("x")) {
						
						Value v = binding.getValue("x");
						
						if (v != null && v.stringValue().contains(BASE_URI)) {
							
							String conceptId = StringUtils.delete(v.stringValue(), BASE_URI);
							LOGGER.debug("Adding  concept ids  {} to list", conceptId);

							conceptIds.add(conceptId);

							
						}

					} else {
						
						LOGGER.debug("Binding not available"); //TODO remove later

					}
					
					
				}
				
				sparqlResults.close();
				
		} catch (Exception e) {
			
			LOGGER.error("Error duing concept ids fetch ", e);
			
			throw new ConceptServiceException(e);
			
		} finally {
			
			close(sc);
			close(sail);
			close(g);
			
		}
		LOGGER.debug("returning total {} concept ids ", conceptIds.size());

		return Collections.unmodifiableSortedSet(conceptIds);
	}

	/**
	 * @param sail
	 */
	private void close(Sail sail) {
		
		if (sail != null) {
			
			LOGGER.debug("Shutting down sail storage");
			try {
				
				sail.shutDown();
				
			} catch (SailException e) {
				// TODO Auto-generated catch block
				LOGGER.error("Error in sail shutdown", e);

			}
			
		}
		
	}

	/**
	 * @param sc
	 */
	private void close(SailConnection sc) {
		// TODO Auto-generated method stub
		
		if (sc != null) {
			
			LOGGER.debug("Shutting down sc storage");
			try {
				
				sc.close();
				
			} catch (SailException e) {
				// TODO Auto-generated catch block
				LOGGER.error("Error in saill connection closing", e);

			}
			
		}

	}

	/**
	 * @param g
	 */
	private void close(TitanGraph g) {
		
		if (g != null) {
			
			LOGGER.debug("Shutting down graph storage");
			g.shutdown();
			
		}
		
	}

	/**
	 * @param repositoryConfig the repositoryConfig to set
	 */
	public void setRepositoryConfig(String repositoryConfig) {
		this.repositoryConfig = repositoryConfig;
	}
	
	
	private Sail getSail(TitanGraph graph) {
        
        GraphSail<TitanGraph> sail = new GraphSail<TitanGraph>(graph);
		sail.enforceUniqueStatements(true);
		
		return sail;
	}
	
	private TitanGraph getGraph() {
		
		TitanGraph g = this.graph;
		
		if ( g != null && g.isOpen() ) {
			
			return g;
			
		}
		
		if( config != null) {
			
			this.graph = TitanFactory.open(config);
			
			return this.graph;
		}
		
		if (!StringUtils.isEmpty(repositoryConfig)) {
			
			this.graph = TitanFactory.open(repositoryConfig);
			
			return this.graph;

		}
		
		 throw new IllegalArgumentException("Repository configuration is required");

	}
	
	private Concept getConcept(CloseableIteration<? extends BindingSet, QueryEvaluationException> sparqlResults) throws QueryEvaluationException {
		
		Concept concept = new Concept();
		String id = null;
		while (sparqlResults.hasNext()) {
			
			BindingSet bSet = sparqlResults.next();
						
			LOGGER.trace("Binding set {}", bSet);
		    Binding o = bSet.getBinding("o");
		    Binding y = bSet.getBinding("y");
	
			if ( id == null) {
				
				Value v = bSet.getValue("x");
				id = StringUtils.delete(v.stringValue(), BASE_URI);
				concept.setId(id);
				
			}
			LOGGER.trace("Value for o {} and y {} ", o, y);

			if (o != null && y != null) {
				
				concept.addProperties(o.getValue(), y.getValue());

			}
		    
		}
		concept = StringUtils.isEmpty(concept.getId()) ? null : concept;
		
		LOGGER.debug("Returning {}", concept);

		return concept;
	}

	/**
	 * @param config the config to set
	 */
	public void setConfig(Configuration config) {
		this.config = config;
	}

}
