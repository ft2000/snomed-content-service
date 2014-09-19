/**
 * 
 */
package org.ihtsdo.otf.refset.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ihtsdo.otf.refset.domain.Member;
import org.ihtsdo.otf.refset.domain.MetaData;
import org.ihtsdo.otf.refset.domain.Refset;
import org.ihtsdo.otf.refset.exception.EntityNotFoundException;
import org.ihtsdo.otf.refset.graph.schema.GMember;
import org.ihtsdo.otf.refset.graph.schema.GRefset;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.google.common.collect.Iterables;
import com.thinkaurelius.titan.core.TitanGraph;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.FramedGraph;
import com.tinkerpop.frames.FramedGraphFactory;
import com.tinkerpop.frames.FramedTransactionalGraph;

/**Graph Access component to do CRUD operation on underlying Refset graph
 * @author Episteme Partners
 *
 */
@Repository
public class RefsetGAO {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RefsetGAO.class);
	
	//need this for unit testing hence this initialization
	
	private RefsetGraphFactory factory;
	


	private static FramedGraphFactory fgf = new FramedGraphFactory();


	/**
	 * @param r a {@link Refset} with or without members
	 * @throws RefsetGraphAccessException
	 * @throws EntityNotFoundException 
	 */
	public MetaData addRefset(Refset r) throws RefsetGraphAccessException {
		
		LOGGER.debug("Adding refset {}", r);

		TitanGraph g = null;
		MetaData md = r.getMetaData();
		
		try {
			
			g = factory.getTitanGraph();
			
			FramedTransactionalGraph<TitanGraph> tg = fgf.create(g);

			Object rId = addRefsetNode(r, tg);	
			
			final Vertex rV = g.getVertex(rId);

			
			/*if members exist then add members*/
			List<Member> members = r.getMembers();
			int i = 0;
			if( !CollectionUtils.isEmpty(members) ) {
				
				for (Member m : members) {
					
					Object mId = addMemberNode(m, tg);
					
					Vertex mV = g.getVertex(mId);
					
					LOGGER.debug("Adding relationship member is part of refset as edge {}, member index {}", mV.getId(), i++);

					/*Add this member to refset*/
					Edge e = g.addEdge(null, mV, rV, "members");

					LOGGER.debug("Added relationship as edge from {} to {}", mV.getId(), rV.getId());
					
					//added effective date of relationship
					e.setProperty(RGC.EFFECTIVE_DATE, new DateTime().getMillis());
					

				}

				
			} else {
				
				LOGGER.debug("No member available for this refset to add");

			}
			
			LOGGER.info("Commiting");

			tg.commit();
			g.commit();
			
			md = getMetaData(rV.getId());
			
		} catch (Exception e) {
			
			rollback(g);			
			LOGGER.error("Error during graph ineraction", e);
			
			throw new RefsetGraphAccessException(e.getMessage(), e);
			
		} finally {
			
			shutdown(g);
			
		}
		
		return md;
	}
	
	private void shutdown(Graph g) {
		
		LOGGER.info("Shutting down graph {}", g);
		
		//if (g != null) g.shutdown();//shutdown is not required for titan.Commit should clear resources
		
	}

	/**
	 * @param r {@link Refset}
	 * @param tg {@link FramedTransactionalGraph}
	 * @return id of {@link Refset} node
	 * @throws RefsetGraphAccessException
	 * @throws EntityNotFoundException 
	 */
	private Object addRefsetNode(Refset r, FramedTransactionalGraph<TitanGraph> tg) throws RefsetGraphAccessException {
		// TODO Auto-generated method stub
		
		Object rVId;
		

		try {
			
			rVId = getRefsetNodeId(r.getId());
			
		} catch (EntityNotFoundException e) {
			
			LOGGER.debug("Refset does not exist, adding  {}", r.toString());
			
			GRefset gr = tg.addVertex("GRefset", GRefset.class);
			gr.setCreated(r.getCreated().getMillis());
			gr.setCreatedBy(r.getCreatedBy());
			gr.setDescription(r.getDescription());
			
			if (r.getEffectiveTime() != null) {
				
				gr.setEffectiveTime(r.getEffectiveTime().getMillis());

			}
			gr.setId(r.getId());
			gr.setLanguageCode(r.getLanguageCode());
			gr.setModuleId(r.getModuleId());
			gr.setPublished(r.isPublished());
			
			if (r.getPublishedDate() != null) {
				
				 gr.setPublishedDate(r.getPublishedDate().getMillis());

			}
			
			gr.setSuperRefsetTypeId(r.getSuperRefsetTypeId());
			
			if (r.getType() != null) {
				
				gr.setType(r.getType().getName());

			} 
			
			gr.setTypeId(r.getTypeId());
			
			gr.setActive(r.isActive());
			
			LOGGER.debug("Added Refset as vertex to graph {}", gr.getId());

			rVId = gr.asVertex().getId();			
		}
		
		LOGGER.debug("Refset  vertex id is {} ", rVId);

		return rVId;
	}

	/** Removes a {@link Refset} if it is not yet published
	 * or update as inactive in  graph
	 * @param r {@link Refset}
	 * @throws RefsetGraphAccessException
	 * @throws EntityNotFoundException 
	 */
	public void removeRefset(String refsetId) throws RefsetGraphAccessException, EntityNotFoundException {
		
		LOGGER.debug("removeRefset  {} ", refsetId);
		
		if (StringUtils.isEmpty(refsetId)) {
			
			throw new EntityNotFoundException();
			
		}

		TitanGraph g = null;
		
		try {
			
			g = factory.getTitanGraph();
			
			Object rVId = getRefsetNodeId(refsetId);
			Vertex refset = g.getVertex(rVId);
			
			boolean published = refset.getProperty(RGC.PUBLISHED);
			
			if (published) {
				
				LOGGER.debug("Not removing only making it inactive  {} ", refsetId);

				refset.setProperty(RGC.ACTIVE, false);
				
			} else {
				
				g.removeVertex(refset);

			}

			
			g.commit();
			
		} catch(EntityNotFoundException e) {
			
			g.rollback();
			
			throw e;
			
		} catch (Exception e) {
			
			rollback(g);
			LOGGER.error("Error during graph ineraction", e);

			throw new RefsetGraphAccessException(e.getMessage(), e);

			
		} finally {
			
			shutdown(g);
		}
	}
	


	
	

	
	/**Retrieves a {@link Member} node id for given {@link Member#getReferenceComponentId()}
	 * @param rcId
	 * @param tg 
	 * @return
	 * @throws RefsetGraphAccessException
	 * @throws EntityNotFoundException 
	 */
	private Object getMemberNodeId(String rcId, TitanGraph tg) throws RefsetGraphAccessException, EntityNotFoundException {
		
		Object result = null;
		
		if (StringUtils.isEmpty(rcId)) {
			
			throw new EntityNotFoundException("Member does not exist for given reference component id");
		}
		try {

			//TODO upgrade this search with status and effective date
			Iterable<Vertex> vr = tg.getVertices(RGC.REFERENCE_COMPONENT_ID, rcId);
			
			for (Vertex v : vr) {
				
				result = v.getId();
				break;
			}
		
			
		} catch (Exception e) {
			
			throw new RefsetGraphAccessException(e.getMessage(), e);

		}
		
		if(result == null) 
			throw new EntityNotFoundException("Record does not exist");
		
		return result;

	}
	
	private void rollback(TitanGraph g) {
		
		if (g != null) g.rollback();
		
	}

	/**Retrieves a {@link Refset} node Id
	 * @param id
	 * @return
	 * @throws RefsetGraphAccessException
	 * @throws EntityNotFoundException 
	 */
	Object getRefsetNodeId(String id) throws RefsetGraphAccessException, EntityNotFoundException {
		
		LOGGER.debug("Getting record id for given refset id {}", id);

		if (StringUtils.isEmpty(id)) {
			
			throw new EntityNotFoundException();
			
		}

		Object rVId = null;
		
		TitanGraph g = null;
		
		try {
			
			g = factory.getTitanGraph();
			//TODO upgrade this search with status and effective date
			
			Iterable<Vertex> vr = g.getVertices(RGC.ID, id);
			
			if (vr != null ) {
				
				for (Vertex v : vr) {
					
					rVId = v.getId();
					LOGGER.debug("Refset is {} and id is {}", v, rVId);
					break;
				}
			}
			
			
			g.commit();
			
		} catch (Exception e) {
			
			LOGGER.error("Error during graph ineraction", e);
			throw new RefsetGraphAccessException(e.getMessage(), e);

		} finally {
			
			shutdown(g);
		}
		
		if(rVId == null) 
			throw new EntityNotFoundException("Refset does not exist for given refset id");
		
		return rVId;

	}
	
	/** Add a {@link Member} if does not exist in graph for a given {@link Member#getReferenceComponentId()}
	 * and returns {@link Member} node id {@link Vertex#getId()}
	 * @param m
	 * @throws RefsetGraphAccessException 
	 */
	private Object addMemberNode(Member m, FramedTransactionalGraph<TitanGraph> tg) throws RefsetGraphAccessException {
		
		Object id = null;
		try {
			
			id = getMemberNodeId(m.getReferenceComponentId(), tg.getBaseGraph());

			LOGGER.debug("Member already exist as vertex to graph {}", id);

		} catch (EntityNotFoundException e) {
			
			
			GMember mV = tg.addVertex("GMember", GMember.class);
			
			mV.setActive(m.isActive());
			mV.setId(m.getId());
			mV.setModuleId(m.getModuleId());
			mV.setReferenceComponentId(m.getReferenceComponentId());
			mV.setEffectiveTime(m.getEffectiveTime().getMillis());
			
			LOGGER.debug("Added Member as vertex to graph", mV.getId());
						
			id = mV.asVertex().getId();

		}
		
		
		return id;
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
			
			g = factory.getTitanGraph();

			FramedGraph<TitanGraph> tg = fgf.create(g);
			//TODO upgrade this search with status and effective date
			Iterable<GRefset> vs = tg.getVertices(RGC.ID, id, GRefset.class);//.has(RGC.ID, Compare.EQUAL, id).limit(1).vertices(GRefset.class);
			
			for (GRefset v : vs) {
				
				r = RefsetConvertor.convert2Refsets(v);
				LOGGER.debug("Refset is {} ", r);
				r.setMetaData(getMetaData(v.asVertex().getId()));
				break;
			}
			g.commit();
			
		} catch (Exception e) {
			
			rollback(g);			
			LOGGER.error("Error during graph ineraction", e);
			throw new RefsetGraphAccessException(e.getMessage(), e);

		} finally {
			
			shutdown(g);
		}
		if(r == null)
			throw new EntityNotFoundException("No Refset found for given id ");
		else 
			return r;

	}
	
	/**Retrieves a {@link Refset}  for a given id of Refset node. This method should only be called at service layer
	 * @param nodeId
	 * @return {@link Refset}
	 * @throws RefsetGraphAccessException
	 */
	public Refset getRefsetFromNodeId(Object nodeId) throws RefsetGraphAccessException, EntityNotFoundException {
				
		TitanGraph g = null;
		Refset r = null;
		try {
			
			g = factory.getTitanGraph();

			FramedGraph<TitanGraph> tg = fgf.create(g);
			GRefset v = tg.getVertex(nodeId, GRefset.class);

			if(v != null) {
				
				r = RefsetConvertor.convert2Refsets(v);
				
				LOGGER.debug("Refset is {} ", r);
				
			}
			

			g.commit();
			
		} catch (Exception e) {
			
			rollback(g);
			LOGGER.error("Error during graph ineraction", e);
			throw new RefsetGraphAccessException(e.getMessage(), e);

		} finally {
			
			shutdown(g);
		}
		
		if(r == null)
			throw new EntityNotFoundException("No Refset found for given id ");
		else 
			return r;
	}
	

	public List<Refset> getRefSets(boolean published) throws RefsetGraphAccessException {
		
		TitanGraph g = null;
		List<Refset> refsets = new ArrayList<Refset>();
		
		try {
			
			g = factory.getTitanGraph();
			FramedGraph<TitanGraph> fg = fgf.create(g);
			//TODO upgrade this search with status and effective date
			
			Iterable<GRefset> vs;
			
			if (published) {
				
				vs = fg.getVertices(RGC.PUBLISHED, true, GRefset.class);
								
			} else {

				//Iterable<Result<Vertex>> publishedRefset = g.indexQuery("CPublishedGRefset", "v.published:true").limit(10).offset(1).vertices();
				//Iterable<Result<Vertex>> unPublishedRefset = g.indexQuery("CPublishedGRefset", "v.published:false").limit(10).offset(1).vertices();

				Iterable<GRefset> publishedRefset = fg.getVertices(RGC.PUBLISHED, true, GRefset.class); //fg.query().has(RGC.PUBLISHED, Compare.EQUAL, true).limit(20).vertices(GRefset.class);//fg.getVertices(RGC.PUBLISHED, true, GRefset.class);//g.getRelationType(name); (GRefset.class, new Object[]{ published });
				Iterable<GRefset> unPublishedRefset = fg.getVertices(RGC.PUBLISHED, false, GRefset.class); //fg.query().has(RGC.PUBLISHED, Compare.EQUAL, false).limit(20).vertices(GRefset.class);// fg.getVertices(RGC.PUBLISHED, false, GRefset.class);//g.getRelationType(name); (GRefset.class, new Object[]{ published });
				
				vs = Iterables.concat(publishedRefset, unPublishedRefset);
				
			}
			/*List<GRefset> grs = new ArrayList<GRefset>();
			
			for (Result<Vertex> v : vs) {
				
				GRefset r = fg.frame(v.getElement(), GRefset.class);
				grs.add(r);
			}*/
			
			
			refsets = RefsetConvertor.getRefsetss(vs);
			
		} catch (Exception e) {
			
			rollback(g);			
			LOGGER.error("Error during graph ineraction", e);
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
	public MetaData getMetaData(Object rId) throws RefsetGraphAccessException, EntityNotFoundException {
		
		TitanGraph g = null;
		MetaData md = null;
		
		try {
			
			g = factory.getTitanGraph();

			Vertex e = g.getVertex(rId);
			if( e != null) {
				
				md = new MetaData();
				md.setId(e.getId());
				md.setType(e.getClass().getSimpleName());
				//Integer version = e.getProperty("@Version");
				//md.setVersion(version);
				
			}
			
			g.commit();
			
		} catch (Exception e) {
			
			rollback(g);
			LOGGER.error("Error during graph ineraction ", e);
			throw new RefsetGraphAccessException(e.getMessage(), e);

		} finally {
			
			shutdown(g);
			
		}
		
		if( md == null ) 
			throw new EntityNotFoundException(String.format("No record available for given record %s", rId));
		
		return md;
	}
	
	
	/**
	 * @param r a {@link Refset} with or without members
	 * @throws RefsetGraphAccessException
	 * @throws EntityNotFoundException 
	 */
	public MetaData updateRefset(Refset r) throws RefsetGraphAccessException, EntityNotFoundException {
		
	
		TitanGraph g = null;
		MetaData md = r.getMetaData();
		
		try {
			
			g = factory.getTitanGraph();
			
			FramedTransactionalGraph<TitanGraph> tg = fgf.create(g);

			final Vertex rV = updateRefsetNode(r, tg);	
			
			
			/*if members exist then add members*/
			List<Member> members = r.getMembers();
			
			if( !CollectionUtils.isEmpty(members) ) {
				
				for (Member m : members) {
					
					Object mId = addMemberNode(m, tg);
					
					Vertex mV = g.getVertex(mId);
					
					LOGGER.debug("Adding relationship member is part of refset as edge {}", mV.getId());

					/*Add this member to refset*/
					g.addEdge(null, mV, rV, "members");

					LOGGER.debug("Added relationship as edge from {} to {}", mV.getId(), rV.getId());

				}

				
			} else {
				
				LOGGER.debug("No member available for this refset to add");

			}
			tg.commit();
			g.commit();
			
			md = getMetaData(rV.getId());
			
		} catch(EntityNotFoundException e) {
			rollback(g);
			throw e;
		}
		
		catch (Exception e) {
			
			rollback(g);			
			LOGGER.error("Error during graph ineraction", e);
			
			throw new RefsetGraphAccessException(e.getMessage(), e);
			
		} finally {
			
			shutdown(g);
			
		}
		
		return md;
	}
	
	/**Update an existing {@link Refset} node. But does not commit yet
	 * @param r {@link Refset}
	 * @param tg {@link TitanGraph}
	 * @return id of {@link Refset} node
	 * @throws RefsetGraphAccessException
	 * @throws EntityNotFoundException 
	 */
	private Vertex updateRefsetNode(Refset r, FramedTransactionalGraph<TitanGraph> tg) throws RefsetGraphAccessException, EntityNotFoundException {
		// TODO Auto-generated method stub
		
		Object rVId = getRefsetNodeId(r.getId());
		
		GRefset rV = tg.getVertex(rVId, GRefset.class);
		
		if(rV == null) {
			
			throw new EntityNotFoundException("Can not find given refset to update");
			
		} 
		
		rV.setDescription(r.getDescription());
		
		if (r.getEffectiveTime() != null) {
			
			rV.setEffectiveTime(r.getEffectiveTime().getMillis());

		}

		rV.setLanguageCode(r.getLanguageCode());
		rV.setModuleId(r.getModuleId());
		rV.setPublished(r.isPublished());
		
		if (r.getPublishedDate() != null) {
			
			 rV.setPublishedDate(r.getPublishedDate().getMillis());

		}
		
		rV.setSuperRefsetTypeId(r.getSuperRefsetTypeId());
		if (r.getType() != null) {
			
			rV.setType(r.getType().getName());

		} 
		
		rV.setActive(r.isActive());
		
		rV.setTypeId(r.getTypeId());

		return rV.asVertex();
	}
	
	
	/**
	 * @param factory the factory to set
	 */
	@Autowired
	public  void setFactory(RefsetGraphFactory factory) {
		
		this.factory = factory;
	}

	/**
	 * @param refsetId
	 * @param members
	 * @throws EntityNotFoundException 
	 * @throws RefsetGraphAccessException 
	 */
	public Map<String, String> addMembers(String refsetId, List<Member> members) throws RefsetGraphAccessException, EntityNotFoundException {
		
		Map<String, String> outcomeMap = new HashMap<String, String>(); //needed at fron end
		
		//try adding all members and accumulate error/success
		
		Object rVId = getRefsetNodeId(refsetId);
		
		FramedTransactionalGraph<TitanGraph> tg = fgf.create(factory.getTitanGraph());
		
		for (Member m : members) {
			
			Object mVId;
			
			try {
				
				mVId = getMemberNodeId(m.getReferenceComponentId(), tg.getBaseGraph());

			} catch(EntityNotFoundException e) {
				
				//add member and its relationship
				mVId = addMemberNode(m, tg);
			}
			
			//add relationship
			
			Edge e = tg.addEdge(null, tg.getVertex(mVId), tg.getVertex(rVId), "members");

			LOGGER.debug("Added relationship as edge from {} to {}", mVId, rVId);
			
			//added effective date of relationship
			e.setProperty(RGC.EFFECTIVE_DATE, new DateTime().getMillis());

			outcomeMap.put(m.getReferenceComponentId(), "Success");
		}
		
		tg.commit();
		
		
		return outcomeMap;
		
	}

}