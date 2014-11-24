/**
 * 
 */
package org.ihtsdo.otf.refset.graph.gao;
import static org.ihtsdo.otf.refset.domain.RGC.TYPE;
import static org.ihtsdo.otf.refset.domain.RGC.DESC;
import static org.ihtsdo.otf.refset.domain.RGC.END;
import static org.ihtsdo.otf.refset.domain.RGC.ID;
import static org.ihtsdo.otf.refset.domain.RGC.PUBLISHED;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.ihtsdo.otf.refset.domain.Member;
import org.ihtsdo.otf.refset.domain.MetaData;
import org.ihtsdo.otf.refset.domain.Refset;
import org.ihtsdo.otf.refset.exception.EntityNotFoundException;
import org.ihtsdo.otf.refset.graph.RefsetGraphAccessException;
import org.ihtsdo.otf.refset.graph.RefsetGraphFactory;
import org.ihtsdo.otf.refset.graph.schema.GRefset;
import org.ihtsdo.otf.snomed.domain.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.google.common.collect.Iterables;
import com.thinkaurelius.titan.core.TitanGraph;
import com.thinkaurelius.titan.core.TitanIndexQuery.Result;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.FramedGraph;
import com.tinkerpop.frames.FramedGraphFactory;
import com.tinkerpop.frames.FramedTransactionalGraph;
import com.tinkerpop.gremlin.java.GremlinPipeline;

/**Refset Graph Access component to retrieve refsets 
 * @author Episteme Partners
 *
 */
@Repository
public class RefsetGAO {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RefsetGAO.class);
		
	private RefsetGraphFactory rgFactory;//refset graph factory	
	private RefsetGraphFactory sgFactory;//snomed graph factory	


	private static FramedGraphFactory fgf = new FramedGraphFactory();


	


	/**Retrieves a {@link Refset} vertex for given {@link Refset#getId()}
	 * @param id
	 * @param tg 
	 * @return
	 * @throws RefsetGraphAccessException
	 * @throws EntityNotFoundException 
	 */
	protected Vertex getRefsetVertex(String id, FramedTransactionalGraph<TitanGraph> tg) throws RefsetGraphAccessException, EntityNotFoundException {
		
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

	
	
	/**Retrieves a {@link Refset}  for a given refsetId
	 * @param id
	 * @return {@link Refset}
	 * @throws RefsetGraphAccessException
	 */
	public Refset getRefset(String id) throws RefsetGraphAccessException, EntityNotFoundException {
				
		TitanGraph g = null;
		Refset r = null;
		try {
			
			g = rgFactory.getReadOnlyGraph();

			FramedGraph<TitanGraph> tg = fgf.create(g);
			//TODO upgrade this search with status and effective date
			Iterable<GRefset> vs = tg.query().has(ID, id).has(TYPE, VertexType.refset.toString()).vertices(GRefset.class);//.has(ID, Compare.EQUAL, id).limit(1).vertices(GRefset.class);
			
			for (GRefset v : vs) {
				
				r = RefsetConvertor.convert2Refset(v);
				LOGGER.debug("Refset is {} ", r);
				r.setMetaData(RefsetConvertor.getMetaData(v.asVertex()));
				break;
			}
			RefsetGraphFactory.commit(tg);
			
			
			if(r == null)
				throw new EntityNotFoundException("No Refset found for given id ");
			
			List<Member> ms = r.getMembers();
			for (Member m : ms) {
			
				m.setDescription(getMemberDescription(m.getReferencedComponentId()));
			}
			
		} catch(EntityNotFoundException e) {
		
			RefsetGraphFactory.rollback(g);			

			LOGGER.error("entity not found for given refset id {}", id, e);

			throw e;
			
		} catch (Exception e) {
			
			RefsetGraphFactory.rollback(g);			
			LOGGER.error("Error getting refset for {}", id, e);
			throw new RefsetGraphAccessException(e.getMessage(), e);

		} finally {
			
			RefsetGraphFactory.shutdown(g);
		}
		
		return r;

	}
	
	public List<Refset> getRefSets(boolean published) throws RefsetGraphAccessException {
		
		TitanGraph g = null;
		List<Refset> refsets = new ArrayList<Refset>();
		
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
			
			refsets = RefsetConvertor.getRefsets(vs);
			RefsetGraphFactory.commit(g);

		} catch (Exception e) {
			
			RefsetGraphFactory.rollback(g);			
			LOGGER.error("Error during graph interaction", e);
			throw new RefsetGraphAccessException(e.getMessage(), e);

		} finally {
			
			RefsetGraphFactory.shutdown(g);
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
	public MetaData getMetaData(Object rId) throws RefsetGraphAccessException, EntityNotFoundException {
		
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
			
			RefsetGraphFactory.commit(g);
			
		} catch (Exception e) {
			
			RefsetGraphFactory.rollback(g);
			LOGGER.error("Error during graph interaction ", e);
			throw new RefsetGraphAccessException(e.getMessage(), e);

		} finally {
			
			RefsetGraphFactory.shutdown(g);
			
		}
		
		if( md == null ) 
			throw new EntityNotFoundException(String.format("No record available for given record %s", rId));
		
		return md;
	}
	
	protected String getMemberDescription(String referenceComponentId) throws RefsetGraphAccessException  {
		
		LOGGER.debug("getting member description for {} ", referenceComponentId);

		String label = "";

		if (StringUtils.isEmpty(referenceComponentId)) {
			
			return label;
		}

		TitanGraph g = null;
		try {
			
			
				
				g = sgFactory.getReadOnlyGraph();

				Iterable<Result<Vertex>> vs = g.indexQuery("concept","v.sctid:" + referenceComponentId).vertices();
				for (Result<Vertex> r : vs) {
									
					Vertex v = r.getElement();
					
					label = v.getProperty(Properties.title.toString());
					break;
					
				}

				
				RefsetGraphFactory.commit(g);
		
		} catch (Exception e) {
			
			RefsetGraphFactory.rollback(g);

			LOGGER.error("Error duing concept details fetch", e);
			
			throw new RefsetGraphAccessException(e.getMessage(), e);
			
		} finally {
			
			RefsetGraphFactory.shutdown(g);

		}
		
		return label;
	}
	
	
	/**validates a given description if it exist in the system
	 * @return if given description exist then true otherwise false
	 * @throws RefsetGraphAccessException
	 */
	public boolean isDescriptionExist(String description) throws RefsetGraphAccessException {
				
		TitanGraph g = null;
		boolean isExist = false;
		try {
			
			g = rgFactory.getReadOnlyGraph();

			//TODO upgrade this search with status and effective date
			Iterable<GRefset> vs = fgf.create(g).getVertices(DESC, description, GRefset.class);//g.query().has(DESC, description).limit(1).vertices();
			
			if (vs != null && vs.iterator() != null && vs.iterator().hasNext()) {
				
				isExist = true;

			}
			
			RefsetGraphFactory.commit(g);
			
			
			
		} catch (Exception e) {
			
			RefsetGraphFactory.rollback(g);			
			LOGGER.error("Error checking refset description for {}", description, e);
			throw new RefsetGraphAccessException(e.getMessage(), e);

		} finally {
			
			RefsetGraphFactory.shutdown(g);
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
	 * @param factory the factory to set
	 */
	@Resource(name = "snomedGraphFactory")
	public  void setSGFactory(RefsetGraphFactory factory) {
		
		this.sgFactory = factory;
	}



	/**
	 * @param refsetId
	 * @param page
	 * @param size
	 * @return
	 * @throws RefsetGraphAccessException 
	 * @throws EntityNotFoundException 
	 */
	public Refset getRefset(String refsetId, Integer from, Integer to) throws RefsetGraphAccessException, EntityNotFoundException {
		
		LOGGER.debug("Getting refset members from {}, to {}", from, to);
		
		TitanGraph g = null;
		Refset r = null;
		try {
			
			g = rgFactory.getReadOnlyGraph();

			FramedGraph<TitanGraph> tg = fgf.create(g);
			
			Iterable<GRefset> vRs = tg.query().has(ID, refsetId).has(TYPE, VertexType.refset.toString()).limit(1).vertices(GRefset.class);
			
			
			if (!vRs.iterator().hasNext()) {
				
				throw new EntityNotFoundException("Refset does not exist for given refset id " + refsetId);
			} 
			
			GRefset vR = vRs.iterator().next();

			r = RefsetConvertor.getRefset(vR);
			
			//get required members as per range
			GremlinPipeline<Vertex, Edge> pipe = new GremlinPipeline<Vertex, Edge>();			
			pipe.start(vR.asVertex()).inE("members").has(END, Long.MAX_VALUE).range(from, to);
			List<Edge> ls = pipe.toList();

			if (r != null) {
				
				r.setMembers(RefsetConvertor.getMembers(ls));

			} else {
				
				throw new EntityNotFoundException("Refset does not exist for given refset id " + refsetId);

			}
			
			RefsetGraphFactory.commit(g);
			
			//populate descriptions
			List<Member> ms = r.getMembers();
			for (Member m : ms) {
			
				m.setDescription(getMemberDescription(m.getReferencedComponentId()));
			}
			
			LOGGER.debug("Returning refset {} ", r);

			
		} catch (Exception e) {
			
			RefsetGraphFactory.rollback(g);			
			LOGGER.error("Error getting refset for {}", refsetId, e);
			throw new RefsetGraphAccessException(e.getMessage(), e);

		} finally {
			
			RefsetGraphFactory.shutdown(g);
		}
		
		return r;

	}



	/**
	 * @param refSetId
	 * @return
	 * @throws RefsetGraphAccessException 
	 */
	public Refset getRefsetHeader(String refSetId) throws RefsetGraphAccessException {

		
		LOGGER.debug("getRefsetHeader for {}", refSetId);
		
		TitanGraph g = null;
		Refset r = null;
		try {
			
			g = rgFactory.getReadOnlyGraph();

			FramedGraph<TitanGraph> tg = fgf.create(g);
			
			Iterable<GRefset> vRs = tg.query().has(ID, refSetId).has(TYPE, VertexType.refset.toString()).limit(1).vertices(GRefset.class);
			
			
			if (!vRs.iterator().hasNext()) {
				
				throw new EntityNotFoundException("Refset does not exist for given refset id " + refSetId);
			} 
			
			GRefset vR = vRs.iterator().next();

			GremlinPipeline<Vertex, Long> pipe = new GremlinPipeline<Vertex, Long>();
			long totalNoOfMembers = pipe.start(vR.asVertex()).inE("members").has(END, Long.MAX_VALUE).count();
			
			LOGGER.debug("total members {}", totalNoOfMembers);
			
			r = RefsetConvertor.getRefset(vR);
			r.setTotalNoOfMembers(totalNoOfMembers);
			
			LOGGER.debug("Returning refset {} ", r);

			RefsetGraphFactory.commit(g);			

			
		} catch (Exception e) {
			
			RefsetGraphFactory.rollback(g);			
			LOGGER.error("Error getting refset for {}", refSetId, e);
			throw new RefsetGraphAccessException(e.getMessage(), e);

		} finally {
			
			RefsetGraphFactory.shutdown(g);
		}
		
		return r;
	
	}
	
	public List<Refset> getRefSets(boolean published, int from, int to) throws RefsetGraphAccessException {
		
		TitanGraph g = null;
		List<Refset> refsets = Collections.emptyList();
		
		try {
			
			g = rgFactory.getReadOnlyGraph();

			GremlinPipeline<Vertex, Vertex> pipe = new GremlinPipeline<Vertex, Vertex>(g);
			
			if (published) {
								
				pipe.V().has(PUBLISHED, 1).has(TYPE, VertexType.refset.toString()).store().range(from, to);

				
			} else {
				
				pipe.V(TYPE, VertexType.refset.toString()).store().range(from, to);

			}
			

			
			List<Vertex> vs = pipe.toList();
			FramedGraph<TitanGraph> fg = fgf.create(g);
			List<GRefset> ls = new ArrayList<GRefset>();
			
			for (Vertex rV : vs) {
				
				GRefset gR = fg.getVertex(rV.getId(), GRefset.class);
				GremlinPipeline<Vertex, Long> mPipe = new GremlinPipeline<Vertex, Long>();
				long noOfMembers = mPipe.start(rV).inE("members").has(END, Long.MAX_VALUE).count();
				gR.setNoOfMembers(noOfMembers);
				ls.add(gR);
				
			}
			
			refsets = RefsetConvertor.getRefsets(ls);

					
			
			
		} catch (Exception e) {
			
			RefsetGraphFactory.rollback(g);			
			LOGGER.error("Error getting refsets for status {}", published, e);
			throw new RefsetGraphAccessException(e.getMessage(), e);
			
		} finally {
			
			RefsetGraphFactory.shutdown(g);
		}
	
		
		LOGGER.debug("Returning {} refsets  ", (refsets != null ? refsets.size() : 0));

		return refsets;

	}

}