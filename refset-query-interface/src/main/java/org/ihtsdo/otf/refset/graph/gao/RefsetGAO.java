/**
 * 
 */
package org.ihtsdo.otf.refset.graph.gao;
import static org.ihtsdo.otf.refset.domain.RGC.DESC;
import static org.ihtsdo.otf.refset.domain.RGC.END;
import static org.ihtsdo.otf.refset.domain.RGC.ID;
import static org.ihtsdo.otf.refset.domain.RGC.SCTID;
import static org.ihtsdo.otf.refset.domain.RGC.PUBLISHED;
import static org.ihtsdo.otf.refset.domain.RGC.TYPE;
import static org.ihtsdo.otf.refset.domain.RGC.REFSET_STATUS;
import static org.ihtsdo.otf.refset.domain.RGC.VERSION;
import static org.ihtsdo.otf.refset.graph.gao.RefsetConvertor.*;
import static org.ihtsdo.otf.refset.graph.RefsetGraphFactory.rollback;
import static org.ihtsdo.otf.refset.graph.RefsetGraphFactory.commit;
import static org.ihtsdo.otf.refset.graph.RefsetGraphFactory.shutdown;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Resource;

import org.ihtsdo.otf.refset.common.Direction;
import org.ihtsdo.otf.refset.common.SearchCriteria;
import org.ihtsdo.otf.refset.common.SearchField;
import org.ihtsdo.otf.refset.domain.MemberDTO;
import org.ihtsdo.otf.refset.domain.MetaData;
import org.ihtsdo.otf.refset.domain.RefsetDTO;
import org.ihtsdo.otf.refset.domain.RefsetStatus;
import org.ihtsdo.otf.refset.exception.EntityNotFoundException;
import org.ihtsdo.otf.refset.graph.RefsetGraphAccessException;
import org.ihtsdo.otf.refset.graph.RefsetGraphFactory;
import org.ihtsdo.otf.refset.graph.schema.GRefset;
import org.ihtsdo.otf.snomed.domain.Concept;
import org.ihtsdo.otf.snomed.exception.ConceptServiceException;
import org.ihtsdo.otf.snomed.service.ConceptLookupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.google.common.collect.Iterables;
import com.thinkaurelius.titan.core.Order;
import com.thinkaurelius.titan.core.TitanGraph;
import com.thinkaurelius.titan.core.TitanGraphQuery;
import com.tinkerpop.blueprints.Compare;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Predicate;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.FramedGraph;
import com.tinkerpop.frames.FramedGraphFactory;
import com.tinkerpop.frames.FramedGraphQuery;
import com.tinkerpop.frames.FramedTransactionalGraph;
import com.tinkerpop.gremlin.Tokens.T;
import com.tinkerpop.gremlin.java.GremlinPipeline;

/**Refset Graph Access component to retrieve {@link Refset}s and its {@link Member} 
 *
 */
@Repository
public class RefsetGAO {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RefsetGAO.class);
		
	private RefsetGraphFactory rgFactory;//refset graph factory	


	private static FramedGraphFactory fgf = new FramedGraphFactory();

	private ConceptLookupService conceptService;

	


	/**
	 * @param conceptService the conceptService to set
	 */
	@Autowired
	public void setConceptService(ConceptLookupService conceptService) {
		this.conceptService = conceptService;
	}



	/**Retrieves a {@link Refset} vertex for given {@link Refset#getId()}
	 * @param id
	 * @param tg 
	 * @return
	 * @throws RefsetGraphAccessException
	 * @throws EntityNotFoundException 
	 */
	protected Vertex getRefsetVertex(String id, FramedTransactionalGraph<TitanGraph> tg) throws RefsetGraphAccessException {
		
		LOGGER.debug("getRefsetVertex for given refset id {}", id);

		if (StringUtils.isEmpty(id)) {
			
			throw new EntityNotFoundException();
			
		}

		Vertex rV = null;
		
		try {
						
			Iterable<Vertex> vr = tg.query().has(TYPE, VertexType.refset.toString()).has(ID, id).limit(1).vertices();
			
			if (vr != null ) {
				
				for (Vertex v : vr) {
					
					LOGGER.debug("Refset is {} for refset id {}", v, id);
					rV = v;
					break;
									
				}
			}
						
		} catch (Exception e) {
			
			LOGGER.error("Error refset lookup for refset id {}", id, e);
			throw new RefsetGraphAccessException(e.getMessage(), e);

		}
		
		if(rV == null) 
			throw new EntityNotFoundException("Refset does not exist for given refset id");
		
		return rV;

	}
	
	/**
	 * @param ms
	 * @throws RefsetGraphAccessException 
	 * @throws ConceptServiceException 
	 */
	private void populateMemberDescription(List<MemberDTO> ms, String release) throws RefsetGraphAccessException, ConceptServiceException {
		
		if (ms == null || ms.isEmpty()) {
			
			return;
		}
		Set<String> rcIds = new HashSet<String>();
		
		for (MemberDTO member : ms) {
			
			rcIds.add(member.getReferencedComponentId());
			
		}
		
		Map<String, Concept> concepts = conceptService.getConcepts(rcIds, release);
		for (MemberDTO m : ms) {
		
			if(concepts.containsKey(m.getReferencedComponentId())) {
				
				Concept c = concepts.get(m.getReferencedComponentId());
				
				m.setDescription(c.getLabel());
				m.setReferencedComponent(c);
			}
		}
		
	}



	/**Use instead {@link #getRefSets(SearchCriteria)}
	 * @param published
	 * @return
	 * @throws RefsetGraphAccessException
	 */
	@Deprecated
	public List<RefsetDTO> getRefSets(boolean published) throws RefsetGraphAccessException {
		
		TitanGraph g = null;
		List<RefsetDTO> refsets = new ArrayList<RefsetDTO>();
		
		try {
			
			g = rgFactory.getReadOnlyGraph();
			FramedGraph<TitanGraph> fg = fgf.create(g);
			//TODO upgrade this search with status and effective date
			
			Iterable<GRefset> vs;
			
			if (published) {
				
				vs = fg.getVertices(PUBLISHED, 1, GRefset.class);
								
			} else {

				Iterable<GRefset> publishedRefset = fg.getVertices(PUBLISHED, 1, GRefset.class); //fg.query().has(PUBLISHED, Compare.EQUAL, true).limit(20).vertices(GRefset.class);//fg.getVertices(PUBLISHED, true, GRefset.class);//g.getRelationType(name); (GRefset.class, new Object[]{ published });
				Iterable<GRefset> unPublishedRefset = fg.getVertices(PUBLISHED, 0, GRefset.class); //fg.query().has(PUBLISHED, Compare.EQUAL, false).limit(20).vertices(GRefset.class);// fg.getVertices(PUBLISHED, false, GRefset.class);//g.getRelationType(name); (GRefset.class, new Object[]{ published });
				
				vs = Iterables.concat(publishedRefset, unPublishedRefset);
				
			}			
			
			refsets = getRefsets(vs);
			commit(g);

		} catch (Exception e) {
			
			rollback(g);			
			LOGGER.error("Error during graph interaction", e);
			throw new RefsetGraphAccessException(e.getMessage(), e);

		} finally {
			
			shutdown(g);
		}
		
		return refsets;
	}
	
	/** Utility method to retrieve meta information of a graph object
	 * for a given record id 
	 * @param rId
	 * @return
	 * @throws RefsetGraphAccessException
	 * @throws EntityNotFoundException 
	 */
	public MetaData getMetaData(Object rId) throws RefsetGraphAccessException {
		
		TitanGraph g = null;
		MetaData md = null;
		
		try {
			
			g = rgFactory.getReadOnlyGraph();

			Vertex e = g.getVertex(rId);
			if( e != null) {
				
				md = new MetaData();
				md.setId(e.getId());
				md.setType(e.getClass().getSimpleName());
				//Integer version = e.getProperty("@Version");
				//md.setVersion(version);
				
			}
			
			commit(g);
			
		} catch (Exception e) {
			
			rollback(g);
			LOGGER.error("Error during graph interaction ", e);
			throw new RefsetGraphAccessException(e.getMessage(), e);

		} finally {
			
			shutdown(g);
			
		}
		
		if( md == null ) 
			throw new EntityNotFoundException(String.format("No record available for given record %s", rId));
		
		return md;
	}
	
	
	/**validates a given description if it exist in the system
	 * @return if given description exist then true otherwise false
	 * @throws RefsetGraphAccessException
	 */
	public boolean isDescriptionExist(String description) throws RefsetGraphAccessException {
				
		TitanGraph g = null;
		boolean isExist = false;
		try {
			
			GremlinPipeline<Vertex, Vertex> rPipe = new GremlinPipeline<Vertex, Vertex>(rgFactory.getReadOnlyGraph());

			rPipe.V().has(DESC, description).has(TYPE, T.in, 
					Arrays.asList(VertexType.refset.toString(), VertexType.hRefset.toString()))
					.has(REFSET_STATUS, T.neq, RefsetStatus.deleted.toString()).range(0, 1);
			List<Vertex> vs = rPipe.toList();

			if (vs != null && vs.size() > 0 ) {
				
				isExist = true;

			}
			
			commit(g);
			
			
			
		} catch (Exception e) {
			
			rollback(g);			
			LOGGER.error("Error checking refset description for {}", description, e);
			throw new RefsetGraphAccessException(e.getMessage(), e);

		} finally {
			
			shutdown(g);
		}

		return isExist;
	}


	
	
	/**
	 * @param factory the factory to set
	 */
	@Resource(name = "refsetGraphFactory")
	public  void setRGFactory(RefsetGraphFactory factory) {
		
		this.rgFactory = factory;
	}



	/**
	 * @param refsetId
	 * @param page
	 * @param size
	 * @return
	 * @throws RefsetGraphAccessException 
	 */
	public RefsetDTO getRefset(final String refsetId, final Integer from, final Integer to, final Integer version) throws RefsetGraphAccessException {
		
		LOGGER.debug("Getting refset members from {}, to {}", from, to);
		
		TitanGraph g = null;
		RefsetDTO r = null;
		try {
			
			g = rgFactory.getReadOnlyGraph();

			FramedGraph<TitanGraph> tg = fgf.create(g);
			
			FramedGraphQuery query = tg.query().has(ID, refsetId);
			if (version > 0) {
				
				query.has(VERSION, version);
				
			} else {
				
				query.has(TYPE, VertexType.refset.toString());
			}
			
			Iterable<GRefset> vRs = query.limit(1).vertices(GRefset.class);
			
			
			if (!vRs.iterator().hasNext()) {
				
				throw new EntityNotFoundException("Refset does not exist for given refset id " + refsetId);
			} 
			
			GRefset vR = vRs.iterator().next();

			r = RefsetConvertor.getRefset(vR);
			
			//get required members as per range

			if (r != null) {
				
				GremlinPipeline<Vertex, Edge> pipe = new GremlinPipeline<Vertex, Edge>();			
				pipe.start(vR.asVertex()).inE(EdgeLabel.members.toString()).has(END, Long.MAX_VALUE).range(from, to);
				List<Edge> ls = pipe.toList();

				r.setMembers(getMembers(ls));

			} else {
				
				throw new EntityNotFoundException("Refset does not exist for given refset id " + refsetId);

			}
			
			commit(g);
			
			//populate descriptions
			populateMemberDescription(r.getMembers(), r.getSnomedCTVersion());

			
			LOGGER.trace("Returning {} ", r);//it prints large output hence trace

			
		} catch (Exception e) {
			
			rollback(g);			
			LOGGER.error("Error getting refset for {}", refsetId, e);
			throw new RefsetGraphAccessException(e.getMessage(), e);

		} finally {
			
			shutdown(g);
		}
		
		return r;

	}



	/**
	 * @param refSetId
	 * @return
	 * @throws RefsetGraphAccessException 
	 */
	public RefsetDTO getRefsetHeader(String refSetId, Integer version) throws RefsetGraphAccessException {

		
		LOGGER.debug("getRefsetHeader for {}", refSetId);
		
		TitanGraph g = null;
		RefsetDTO r = null;
		try {
			
			g = rgFactory.getReadOnlyGraph();

			FramedGraph<TitanGraph> tg = fgf.create(g);
			
			FramedGraphQuery query = tg.query().has(ID, refSetId);
			if (version > 0) {
			
				query.has(VERSION, version);
				
			} else {
				
				query.has(TYPE, VertexType.refset.toString());
			}
			
			Iterable<GRefset> vRs = query.limit(1).vertices(GRefset.class);
			
			
			if (!vRs.iterator().hasNext()) {
				
				throw new EntityNotFoundException("Refset does not exist for given refset id " + refSetId);
			} 
			
			GRefset vR = vRs.iterator().next();

			GremlinPipeline<Vertex, Long> pipe = new GremlinPipeline<Vertex, Long>();
			long totalNoOfMembers = pipe.start(vR.asVertex()).inE(EdgeLabel.members.toString()).has(END, Long.MAX_VALUE).count();
			
			LOGGER.debug("total members {}", totalNoOfMembers);
			
			r = RefsetConvertor.getRefset(vR);
			r.setTotalNoOfMembers(totalNoOfMembers);
			
			LOGGER.debug("Returning refset {} ", r);

			commit(g);			

			
		} catch(EntityNotFoundException e) {
			
			throw e;
		}
		
		catch (Exception e) {
			
			rollback(g);			
			LOGGER.error("Error getting refset for {}", refSetId, e);
			throw new RefsetGraphAccessException(e.getMessage(), e);

		} finally {
			
			shutdown(g);
		}
		
		return r;
	
	}
	
	public List<RefsetDTO> getRefSets(boolean published, int from, int to) throws RefsetGraphAccessException {
		
		TitanGraph g = null;
		List<RefsetDTO> refsets = Collections.emptyList();
		
		try {
			
			g = rgFactory.getReadOnlyGraph();

			GremlinPipeline<Vertex, Vertex> pipe = new GremlinPipeline<Vertex, Vertex>(g);
			
			if (published) {
								
				pipe.V().has(PUBLISHED, 1).has(TYPE, VertexType.refset.toString()).range(from, to);

				
			} else {
				
				pipe.V(TYPE, VertexType.refset.toString()).range(from, to);

			}
			
			List<Vertex> vs = pipe.toList();
			FramedGraph<TitanGraph> fg = fgf.create(g);
			List<GRefset> ls = new ArrayList<GRefset>();
			
			for (Vertex rV : vs) {
				
				GRefset gR = fg.getVertex(rV.getId(), GRefset.class);
				GremlinPipeline<Vertex, Long> mPipe = new GremlinPipeline<Vertex, Long>();
				long noOfMembers = mPipe.start(rV).inE(EdgeLabel.members.toString()).has(END, Long.MAX_VALUE).count();
				gR.setNoOfMembers(noOfMembers);
				ls.add(gR);
				
			}
			
			refsets = getRefsets(ls);

					
			
			
		} catch (Exception e) {
			
			rollback(g);			
			LOGGER.error("Error getting refsets for status {}", published, e);
			throw new RefsetGraphAccessException(e.getMessage(), e);
			
		} finally {
			
			shutdown(g);
		}
	
		
		LOGGER.debug("Returning {} refsets  ", (refsets != null ? refsets.size() : 0));

		return refsets;

	}
	
	/**
	 * @param criteria
	 * @return
	 * @throws RefsetGraphAccessException
	 */
	public List<RefsetDTO> getRefSets(SearchCriteria criteria) throws RefsetGraphAccessException {
		
		TitanGraph g = null;
		List<RefsetDTO> refsets = Collections.emptyList();
		
		try {
			
			g = rgFactory.getReadOnlyGraph();
			
			Map<SearchField, Object> searchBy = criteria.getFields();
			
			Set<SearchField> sFields = searchBy.keySet();
			
			TitanGraphQuery query = g.query();

			for (SearchField f : sFields) {
				
				Predicate predicate = Compare.EQUAL;
				Object value =  searchBy.get(f);
				
				if (PUBLISHED.equals(f.toString())) {
					
					value =  (Boolean)searchBy.get(f) == true ? 1 : 0;
					
				} else if (DESC.equals(f.toString())) {
				
					//predicate = Text.CONTAINS;
				}
				
				LOGGER.debug("Adding search by {} and value {}", f.toString(), value);

				query.has(f.toString(), predicate, value);
			}
			
			query.has(TYPE, VertexType.refset.toString());
			
			Map<SearchField, Direction> sortBy = criteria.getSortBy();
			
			Set<SearchField> sortFields = sortBy.keySet();
			
			for (SearchField s : sortFields) {
			
				Direction direction = sortBy.get(s);
				
				Order order = Direction.asc.equals(direction) ? Order.ASC : Order.DESC;
				LOGGER.debug("Adding order by {} and direction {}", s.toString(), order);
				query.orderBy(s.toString(), order);

				
			}
			
			//there is an issue with index query i.e. there is no range option like Gremlin pipe but Gremlin pipe does not have sorting.
			//Pipes use in-memory sorting which can be expensive at the same time sorting may not be correct as it will apply only on subset of results 
			//hence put a probable max limit and provide subset based on from - to range. 
			//This query is done on elastic search which is blazingly fast and Result is just light vertex object.
			
			query.limit(100000); 
			Iterable<Vertex> result = query.vertices();

			int from = criteria.getFrom() > 0 ? criteria.getFrom() - 1 : 0;
			
			Iterable<Vertex> start = Iterables.skip(result, from);
			
			Iterable<List<Vertex>> sublist = Iterables.partition(start, (criteria.getTo() - criteria.getFrom()));
			
			FramedGraph<TitanGraph> fg = fgf.create(g);
			List<GRefset> ls = new ArrayList<GRefset>();
			
			for (List<Vertex> rVs : sublist) {
				
				for (Vertex rV : rVs) {
					
					GRefset gR = fg.getVertex(rV.getId(), GRefset.class);
					GremlinPipeline<Vertex, Long> mPipe = new GremlinPipeline<Vertex, Long>();
					long noOfMembers = mPipe.start(rV).inE(EdgeLabel.members.toString()).has(END, Long.MAX_VALUE).count();
					gR.setNoOfMembers(noOfMembers);
					ls.add(gR);
				}
				
				break;
			}
			
			refsets = RefsetConvertor.getRefsets(ls);

					
			
			
		} catch (Exception e) {
			
			rollback(g);			
			LOGGER.error("Error getting refsets for status {}", e);
			throw new RefsetGraphAccessException(e.getMessage(), e);
			
		}
	
		
		LOGGER.debug("Returning {} refsets  ", (refsets != null ? refsets.size() : 0));

		return refsets;

	}



	/**Retrieves totalNoOfRefsets in database for given scenario
	 * 1. For myrefset it will be createdBy filter
	 * 2. For not logged in user it should be only published count
	 * @param criteria
	 * @return
	 */
	public Long totalNoOfRefset(SearchCriteria criteria) {
		
		GremlinPipeline<Vertex, Long> mPipe = new GremlinPipeline<Vertex, Long>(rgFactory.getReadOnlyGraph());
		mPipe.V(TYPE, VertexType.refset.toString());
		
		Map<SearchField, Object> searchBy = criteria.getFields();
		
		Set<SearchField> sFields = searchBy.keySet();
		
		for (SearchField f : sFields) {
			
			Object value =  searchBy.get(f);
			
			if (PUBLISHED.equals(f.toString())) {
				
				value =  (Boolean)searchBy.get(f) == true ? 1 : 0;

			}
			LOGGER.debug("Adding search by {} and value {}", f.toString(), value);
			mPipe.has(f.toString(), value);

		}
		
		Long totalNoOfRefsets = mPipe.count();

		return totalNoOfRefsets;
	}



	/**Retrieves published/released versions
	 * @param refSetId
	 * @return
	 */
	public Set<Integer> getRefsetVersions(String refSetId) {

		Set<Integer> versions = new TreeSet<Integer>();
		
		GremlinPipeline<Vertex, Vertex> rPipe = new GremlinPipeline<Vertex, Vertex>(rgFactory.getReadOnlyGraph());
		rPipe.V(ID, refSetId).has(REFSET_STATUS, T.in, 
				Arrays.asList(RefsetStatus.published.toString(), 
						RefsetStatus.released.toString()));
		List<Vertex> vs = rPipe.toList();
		
		for (Vertex v : vs) {
			
			if(v.getPropertyKeys().contains(VERSION)) {
				
				Integer version = v.getProperty(VERSION);
				versions.add(version);
			}
		}
		
		return versions;
	}



	/**
	 * @param conceptId
	 * @param version
	 * @return
	 * @throws RefsetGraphAccessException 
	 */
	public RefsetDTO getRefsetHeaderByCoceptId(String conceptId, Integer version) throws RefsetGraphAccessException {

		
		LOGGER.debug("getRefsetHeaderByCoceptId for {} and version {}", conceptId, version);
		
		TitanGraph g = null;
		RefsetDTO r = null;
		try {
			
			g = rgFactory.getReadOnlyGraph();

			FramedGraph<TitanGraph> tg = fgf.create(g);
			
			FramedGraphQuery query = tg.query().has(SCTID, conceptId).has(TYPE, VertexType.refset.toString());
			
			if (version > 0) {
			
				query.has(VERSION, version);
				
			}
			
			Iterable<GRefset> vRs = query.limit(1).vertices(GRefset.class);
			
			
			if (!vRs.iterator().hasNext()) {
				
				throw new EntityNotFoundException("Refset does not exist for given concept id " + conceptId);
			} 
			
			GRefset vR = vRs.iterator().next();

			r = RefsetConvertor.getRefset(vR);

			LOGGER.debug("Returning refset {} ", r);

			commit(g);			

			
		} catch(EntityNotFoundException e) {
			
			throw e;
		}
		
		catch (Exception e) {
			
			rollback(g);			
			LOGGER.error("Error getting refset for {}", conceptId, e);
			throw new RefsetGraphAccessException(e.getMessage(), e);

		}
		
		return r;
	
	}
	


}