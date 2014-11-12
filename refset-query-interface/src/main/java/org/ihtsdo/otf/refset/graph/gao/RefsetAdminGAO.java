/**
 * 
 */
package org.ihtsdo.otf.refset.graph.gao;

import static org.ihtsdo.otf.refset.domain.RGC.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.ihtsdo.otf.refset.domain.Member;
import org.ihtsdo.otf.refset.domain.MetaData;
import org.ihtsdo.otf.refset.domain.Refset;
import org.ihtsdo.otf.refset.exception.EntityNotFoundException;
import org.ihtsdo.otf.refset.graph.RefsetGraphAccessException;
import org.ihtsdo.otf.refset.graph.RefsetGraphFactory;
import org.ihtsdo.otf.refset.graph.schema.GMember;
import org.ihtsdo.otf.refset.graph.schema.GRefset;
import org.ihtsdo.otf.refset.service.upload.Rf2Refset;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.thinkaurelius.titan.core.TitanGraph;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.FramedGraphFactory;
import com.tinkerpop.frames.FramedTransactionalGraph;
import com.tinkerpop.gremlin.Tokens.T;
import com.tinkerpop.gremlin.java.GremlinPipeline;

/**Graph Access component to do CRUD operation on underlying Refset graph
 * @author Episteme Partners
 *
 */
@Repository
public class RefsetAdminGAO {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RefsetAdminGAO.class);
		
	private RefsetGraphFactory factory;
	
	private MemberGAO mGao;
	
	private RefsetGAO rGao;
	


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
		FramedTransactionalGraph<TitanGraph> tg = null;
		
		try {
			
			g = factory.getTitanGraph();
			
			tg = fgf.create(g);
			

			final Vertex rV = addRefsetNode(r, tg);	
			
			
			/*if members exist then add members*/
			List<Member> members = r.getMembers();
			int i = 0;
			if( !CollectionUtils.isEmpty(members) ) {
				
				for (Member m : members) {
					
					Vertex mV = mGao.addMemberNode(m, tg);
					
					LOGGER.debug("Adding relationship member is part of refset as edge {}, member index {}", mV.getId(), i++);

					/*Add this member to refset*/
					Edge e = tg.addEdge(null, mV, rV, "members");
					e.setProperty(REFERENCE_COMPONENT_ID, m.getReferencedComponentId());

					LOGGER.debug("Added relationship as edge from {} to {}", mV.getId(), rV.getId());
				}

				
			} else {
				
				LOGGER.debug("No member available for this refset to add");

			}
			
			LOGGER.debug("Commiting");

			md = RefsetConvertor.getMetaData(rV);

			RefsetGraphFactory.commit(tg);
						
		} catch (Exception e) {
			
			RefsetGraphFactory.rollback(tg);

			LOGGER.error("Error during graph ineraction", e);
			
			throw new RefsetGraphAccessException(e.getMessage(), e);
			
		} finally {
			
			RefsetGraphFactory.shutdown(g);
			
		}
		
		return md;
	}
	

	/**
	 * @param r {@link Refset}
	 * @param tg {@link FramedTransactionalGraph}
	 * @return id of {@link Refset} node
	 * @throws RefsetGraphAccessException
	 * @throws EntityNotFoundException 
	 */
	private Vertex addRefsetNode(Refset r, FramedTransactionalGraph<TitanGraph> tg) throws RefsetGraphAccessException {

		LOGGER.debug("Adding refset node  {}", r);
		
		Vertex rV;
		

		try {
			
			rV = rGao.getRefsetVertex(r.getId(), tg);
			
			LOGGER.debug("Refset {} already exist, not adding");

			
		} catch (EntityNotFoundException e) {
			
			LOGGER.debug("Refset does not exist, adding  {}", r.toString());
			TitanGraph g = tg.getBaseGraph();
			Vertex vR = g.addVertexWithLabel(g.getVertexLabel("GRefset"));
			GRefset gr = tg.getVertex(vR.getId(), GRefset.class);
			
			gr.setCreated(r.getCreated().getMillis());
			gr.setCreatedBy(r.getCreatedBy());
			gr.setDescription(r.getDescription());
			
			if (r.getEffectiveTime() != null) {
				
				gr.setEffectiveTime(r.getEffectiveTime().getMillis());

			}
			gr.setId(r.getId());
			gr.setLanguageCode(r.getLanguageCode());
			gr.setModuleId(r.getModuleId());
			
			Integer publishedFlag = r.isPublished() ? 1 : 0;

			gr.setPublished(publishedFlag);
			
			if (r.getPublishedDate() != null) {
				
				 gr.setPublishedDate(r.getPublishedDate().getMillis());

			}
			
			gr.setSuperRefsetTypeId(r.getSuperRefsetTypeId());
			
			gr.setComponentTypeId(r.getComponentTypeId());
			gr.setTypeId(r.getTypeId());
			
			Integer activeFlag = r.isActive() ? 1 : 0;
			gr.setActive(activeFlag);
			gr.setModifiedBy(r.getModifiedBy());
			gr.setModifiedDate(new DateTime().getMillis());
			
			DateTime ert = r.getExpectedReleaseDate();
		
			if( ert != null) {
				
				gr.setExpectedReleaseDate(ert.getMillis());
			}
			if (!StringUtils.isEmpty(r.getSctId())) {
				
				gr.setSctdId(r.getSctId());

			}
			gr.setType(VertexType.refset.toString());
			
			LOGGER.debug("Added Refset as vertex to graph {}", gr.getId());

			rV = gr.asVertex();			
		}
		
		LOGGER.debug("Refset  vertex is {} ", rV);

		return rV;
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

		FramedTransactionalGraph<TitanGraph> g = null;
		
		try {
			
			g = fgf.create(factory.getTitanGraph());
			
			Vertex refset = rGao.getRefsetVertex(refsetId, g);
			
			Integer published = refset.getProperty(PUBLISHED);
			
			
			if (published == 1) {
				
				LOGGER.debug("Not removing only making it inactive  {} ", refsetId);

				refset.setProperty(ACTIVE, 0);
				
			} else {
				
				g.removeVertex(refset);

			}

			
			RefsetGraphFactory.commit(g);
			
		} catch(EntityNotFoundException e) {
			
			LOGGER.error("Error during graph interaction", e);
			RefsetGraphFactory.rollback(g);
			throw e;
			
		} catch (Exception e) {
			
			RefsetGraphFactory.rollback(g);
			LOGGER.error("Error during graph interaction", e);

			throw new RefsetGraphAccessException(e.getMessage(), e);

			
		} finally {
			
			RefsetGraphFactory.shutdown(g);
		}
	}
	
	
	/**
	 * @param r a {@link Refset} with or without members
	 * @throws RefsetGraphAccessException
	 * @throws EntityNotFoundException 
	 */
	public MetaData updateRefset(Refset r) throws RefsetGraphAccessException, EntityNotFoundException {
		
		LOGGER.debug("Updating refset {}", r);

		TitanGraph g = null;
		MetaData md = r.getMetaData();
		
		try {
			
			g = factory.getTitanGraph();
			
			FramedTransactionalGraph<TitanGraph> tg = fgf.create(g);

			final Vertex rV = updateRefsetNode(r, tg);	
			
			LOGGER.debug("Updating members");

			/*if members exist then add members*/
			List<Member> members = r.getMembers();
			
			if( !CollectionUtils.isEmpty(members) ) {
				
				for (Member m : members) {
					
					LOGGER.debug("Updating member {}", m);

					mGao.updateMemberNode(m, tg);
						
				}

				
			} else {
				
				LOGGER.debug("No member available for this refset to update");

			}
			md = RefsetConvertor.getMetaData(rV);
			
			LOGGER.debug("Commiting updated refset");

			RefsetGraphFactory.commit(tg);
			RefsetGraphFactory.commit(g);
						
		} catch(EntityNotFoundException e) {
			
			LOGGER.error("Error during graph ineraction", e);
			RefsetGraphFactory.rollback(g);
			
			throw e;
		}
		
		catch (Exception e) {
			
			RefsetGraphFactory.rollback(g);			
			LOGGER.error("Error during graph ineraction", e);
			
			throw new RefsetGraphAccessException(e.getMessage(), e);
			
		} finally {
			
			RefsetGraphFactory.shutdown(g);
			
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

		LOGGER.debug("updateRefsetNode {}", r);

		Object rVId = rGao.getRefsetVertex(r.getId(), tg);
		
		GRefset rV = tg.getVertex(rVId, GRefset.class);
		
		if(rV == null) {
			
			throw new EntityNotFoundException("Can not find given refset to update");
			
		} 
		
		rV.setDescription(r.getDescription());
		
		if (r.getEffectiveTime() != null) {
			
			rV.setEffectiveTime(r.getEffectiveTime().getMillis());

		}
		
		String lang = r.getLanguageCode();

		if (!StringUtils.isEmpty(lang)) {
			
			rV.setLanguageCode(lang);

		}
		
		String moduleId = r.getModuleId();

		if (!StringUtils.isEmpty(moduleId)) {
			
			rV.setModuleId(moduleId);

		}
		
		Integer publishedFlag = r.isPublished() ? 1 : 0;

		rV.setPublished(publishedFlag);
		
		if (r.getPublishedDate() != null) {
			
			 rV.setPublishedDate(r.getPublishedDate().getMillis());

		}
		
		String superRefsetTypeId = r.getSuperRefsetTypeId();

		if (!StringUtils.isEmpty(superRefsetTypeId)) {
			
			rV.setSuperRefsetTypeId(superRefsetTypeId);

		}

		String compTypeId = r.getComponentTypeId();

		if (!StringUtils.isEmpty(compTypeId)) {
			
			rV.setComponentTypeId(compTypeId);

		}
		
		Integer activeFlag = r.isActive() ? 1 : 0;

		rV.setActive(activeFlag);
		
		String typeId = r.getTypeId();

		if (!StringUtils.isEmpty(typeId)) {
			
			rV.setTypeId(typeId);

		}

		rV.setModifiedBy(r.getModifiedBy());
		rV.setModifiedDate(new DateTime().getMillis());

		DateTime ert = r.getExpectedReleaseDate();
		if (ert != null) {
			
			rV.setExpectedReleaseDate(ert.getMillis());

		}
		
		if (!StringUtils.isEmpty(r.getSctId())) {
			
			rV.setSctdId(r.getSctId());

		}

		
		LOGGER.debug("updateRefsetNode {} finished", rV);

		return rV.asVertex();
	}
	
	
	/**Adds member and refset and their history.
	 * @param rf2rLst
	 * @param refsetId 
	 * @return
	 * @throws EntityNotFoundException 
	 * @throws RefsetGraphAccessException 
	 */
	public Map<String, String> addMembers(List<Rf2Refset> rf2rLst, String refsetId) throws EntityNotFoundException, RefsetGraphAccessException {
		
		Map<String, String> outcome = new HashMap<String, String>();
		
		TitanGraph g = factory.getTitanGraph();
		
		try {
			
			Vertex rV = rGao.getRefsetVertex(refsetId, fgf.create(g));

			for (Rf2Refset r : rf2rLst) {
				
				GRefset gr = fgf.create(g).frame(rV, GRefset.class);
				if ( StringUtils.isEmpty(r.getRefsetId()) || !(r.getRefsetId().equals(gr.getId()) 
						|| r.getRefsetId().equals(gr.getSctdId())) ) {
					
					String error = String.format("Member does not have valid refset id - %s", r.getRefsetId());
					outcome.put(r.getReferencedComponentId(), error);
					continue;
				}
			
				String desc = rGao.getMemberDescription(r.getReferencedComponentId());
				
				if (StringUtils.isEmpty(desc)) {
					
					outcome.put(r.getReferencedComponentId(), "Unknown referenced component");
					continue;
				}
				
				rV.getEdges(Direction.OUT, "members");
				GremlinPipeline<Vertex, Edge> pipe = new GremlinPipeline<Vertex, Edge>();			
				pipe.start(rV).inE("members").has(REFERENCE_COMPONENT_ID, T.eq, r.getReferencedComponentId()).has(START, T.eq, r.getEffectiveTime().getMillis());
				List<Edge> ls = pipe.toList();
				
				if(ls.isEmpty()) {
					
					//add this member
					Vertex vM = g.addVertexWithLabel(g.getVertexLabel("GMember"));
					GMember mg = fgf.create(g).getVertex(vM.getId(), GMember.class);
					
					addMemberProperties(r, mg);
					
					LOGGER.debug("Added Member as vertex to graph", mg.getId());
					
					Edge e = fgf.create(g).addEdge(null, mg.asVertex(), rV, "members");
					e.setProperty(REFERENCE_COMPONENT_ID, r.getReferencedComponentId());
					e.setProperty(START, r.getEffectiveTime().getMillis());
					e.setProperty(END, new DateTime(Long.MAX_VALUE).getMillis());
					
				} else {
					
					for (Edge edge : ls) {
						
						Long endTime = edge.getProperty(END);
						Long expected = new DateTime(Long.MAX_VALUE).getMillis();
						
						if (endTime == expected) {
							
							//end this member
							edge.setProperty(END, r.getEffectiveTime().getMillis());
							
							//add new member vertex with new end date
							Vertex vM = g.addVertexWithLabel(g.getVertexLabel("GMember"));
							GMember mg = fgf.create(g).getVertex(vM.getId(), GMember.class);
							
							addMemberProperties(r, mg);
							
							LOGGER.debug("Added Member as vertex to graph with new state", mg.getId());
							
							Edge e = fgf.create(g).addEdge(null, mg.asVertex(), rV, "members");
							e.setProperty(REFERENCE_COMPONENT_ID, r.getReferencedComponentId());
							e.setProperty(START, r.getEffectiveTime().getMillis());
							e.setProperty(END, expected.longValue());
						}
						
					}
				}

			}
			
			outcome.put("All members", "Success");
			RefsetGraphFactory.commit(g);
			
		} catch(EntityNotFoundException e) {
			
			throw e;
			
		} catch (Exception e) {
			
			RefsetGraphFactory.rollback(g);			
			LOGGER.error("Error during graph ineraction", e);
			
			throw new RefsetGraphAccessException(e.getMessage(), e);
			
		} finally {
			
			RefsetGraphFactory.shutdown(g);
		}
		
		
		return outcome;
	}
	
	private GMember addMemberProperties(Rf2Refset r, GMember mg) {
		Integer activeFlag = "1".equals(r.getActive()) ? 1 : 0;

		mg.setActive(activeFlag);
		
		mg.setId(r.getId());

		DateTime et = r.getEffectiveTime();
		mg.setEffectiveTime(et.getMillis());

		
		mg.setModifiedBy(r.getModifiedBy());
		mg.setCreateBy(r.getCreatedBy());
		mg.setCreated(new DateTime().getMillis());
		mg.setModifiedDate(new DateTime().getMillis());
		mg.setModuleId(r.getModuleId());
		
		//everything in RF2 is published already hence make it published
		mg.setPublished(1);
		mg.setType(VertexType.member.toString());
		
		return mg;
	}
	
	
	/**
	 * @param factory the factory to set
	 */
	@Resource(name = "refsetGraphFactory")
	public  void setFactory(RefsetGraphFactory factory) {
		
		this.factory = factory;
	}

	/**
	 * @param mGao the mGao to set
	 */
	@Autowired
	public void setMemberGao(MemberGAO mGao) {
		this.mGao = mGao;
	}

	/**
	 * @param rGao the rGao to set
	 */
	@Autowired
	public void setRefsetGao(RefsetGAO rGao) {
		this.rGao = rGao;
	}

}