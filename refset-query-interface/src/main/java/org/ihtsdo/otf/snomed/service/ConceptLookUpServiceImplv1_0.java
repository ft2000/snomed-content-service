/**
* Copyright 2014 IHTSDO
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.ihtsdo.otf.snomed.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.configuration.Configuration;
import org.ihtsdo.otf.refset.exception.EntityNotFoundException;
import org.ihtsdo.otf.snomed.domain.Concept;
import org.ihtsdo.otf.snomed.domain.Properties;
import org.ihtsdo.otf.snomed.domain.Relationship;
import org.ihtsdo.otf.snomed.exception.ConceptServiceException;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;
import com.thinkaurelius.titan.core.TitanIndexQuery.Result;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;

/**
 * @author Episteme Partners
 *
 */
public class ConceptLookUpServiceImplv1_0 implements ConceptLookupService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ConceptLookUpServiceImplv1_0.class);
		
	private TitanGraph graph;
		
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
		try {
			g = getGraph();
			
				for (String id : conceptIds) {
					
					try {
						
						Concept c = getConcept(id);
						concepts.put(id, c);
						
					} catch (EntityNotFoundException e) {

						concepts.put(id, null);

					}

				}
						
				
			
		} catch (Exception e) {
			
			LOGGER.error("Error duing concept details for concept map fetch", e);
			
			throw new ConceptServiceException(e);
			
		} finally {
			
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
		try {
			
				g = getGraph();
				Iterable<Vertex> vs = g.getVertices(Properties.sctid.toString(), conceptId);
				for (Vertex r : vs) {
					
					Concept c = new Concept();
					
					Vertex v = r;
					
					String sctId = v.getProperty(Properties.sctid.toString());
					c.setId(sctId);
					
					Long effectiveTime = v.getProperty(Properties.effectiveTime.toString());
					
					if (effectiveTime != null) {
						
						c.setEffectiveTime(new DateTime(effectiveTime));

					}
					
					String status = v.getProperty(Properties.status.toString());
					boolean active = "1".equals(status) ? true : false;
					c.setActive(active);
					
					String label = v.getProperty(Properties.title.toString());
					c.setLabel(label);
					
					Iterable<Edge> es = v.getEdges(Direction.OUT, Relationship.hasModule.toString());
					
					for (Edge edge : es) {

						Vertex vE = edge.getVertex(Direction.IN);
						
						if (vE != null) {
							
							String moduleId = vE.getProperty(Properties.sctid.toString());
							c.setModule(moduleId);
							break;

						}

					}
					return c;
				}

				
				
		} catch (Exception e) {
			
			LOGGER.error("Error duing concept details fetch", e);
			
			throw new ConceptServiceException(e);
			
		} finally {
			
			close(g);
		}
		
		throw new EntityNotFoundException(String.format("Invalid concept id", conceptId));

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
		try {
			
				g = getGraph();
				Iterable<Result<Vertex>> vs = g.indexQuery("concept","v.sctid:*").offset(offset).limit(limit).vertices();
				//Iterable<Vertex> vs = g.query().has("v.title").orderBy(Properties.sctid.toString(), Order.DESC).limit(limit).vertices();

				//Iterable<Vertex> vs = g.query().has(g.getVertexLabel( Types.concept.name()).getName()).limit(limit).vertices();
				for (Result<Vertex> v : vs) {
					
					String sctid = v.getElement().getProperty(Properties.sctid.toString());

					if (!StringUtils.isEmpty(sctid)) {
						
						LOGGER.trace("Adding sctid {} to concept id list ", sctid);

						conceptIds.add(sctid);

					}

				}
									
		} catch (Exception e) {
			
			LOGGER.error("Error duing concept ids fetch ", e);
			
			throw new ConceptServiceException(e);
			
		} finally {
			
			close(g);
			
		}
		LOGGER.debug("returning total {} concept ids ", conceptIds.size());

		return Collections.unmodifiableSortedSet(conceptIds);
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
	
	private TitanGraph getGraph() {
		
		TitanGraph g = this.graph;
		
		if ( g != null && g.isOpen() ) {
			
			return g;
			
		}
		
		if( config != null) {
			
			this.graph = TitanFactory.open(config);
			
			return this.graph;
		}
		
		 throw new IllegalArgumentException("Repository configuration is required");

	}
	
	
	/* (non-Javadoc)
	 * @see org.ihtsdo.otf.snomed.service.ConceptLookupService#getTypes(String)
	 */
	@Override
	public Map<String, String> getTypes(String id)
			throws ConceptServiceException {

		LOGGER.debug("getting types for given id {}", id);
		
		Map<String, String> types = new HashMap<String, String>();
		
		TitanGraph g = null;
		try {
			g = getGraph();

			    
			
					
			
		
		} catch (Exception e) {
			
			LOGGER.error("Error duing concept details for concept map fetch", e);
			
			throw new ConceptServiceException(e);
			
		} finally {
			
			close(g);
			
		}
		
		LOGGER.debug("returning total {} types ", types.size());

		return Collections.unmodifiableMap(types);
	}

	/**
	 * @param config the config to set
	 */
	public void setConfig(Configuration config) {
		this.config = config;
	}

}
