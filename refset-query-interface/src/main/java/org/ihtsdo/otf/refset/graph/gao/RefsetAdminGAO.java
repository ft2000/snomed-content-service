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
import org.ihtsdo.otf.refset.service.upload.Rf2Record;
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
import com.tinkerpop.blueprints.util.wrappers.event.EventGraph;
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
					long start = r.getEffectiveTime() != null ? r.getEffectiveTime().getMillis() : new DateTime().getMillis();
					e.setProperty(START, start);
					e.setProperty(END, Long.MAX_VALUE);

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

			LOGGER.error("Error during graph interaction", e);
			
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
			
			if (r.getCreated() == null) {
				
				r.setCreated(new DateTime());
			}
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

		EventGraph<TitanGraph> g = null;
		
		try {
			
			g = factory.getEventGraph();
	        g.addListener(new RefsetHeaderChangeListener(g.getBaseGraph(), "service"));

			Vertex refset = rGao.getRefsetVertex(refsetId, fgf.create(g.getBaseGraph()));
			
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

		EventGraph<TitanGraph> g = null;
		MetaData md = r.getMetaData();
		
		try {
			
			g = factory.getEventGraph();
			
			final Vertex rV = updateRefsetNode(r, g);	
			
			LOGGER.debug("Updating members");

			/*if members exist then update members*/
			List<Member> members = r.getMembers();
			
			if( !CollectionUtils.isEmpty(members) ) {
				
				for (Member m : members) {
					
					LOGGER.debug("Updating member {}", m);

					mGao.updateMemberNode(m, g);
						
				}

				
			} else {
				
				LOGGER.debug("No member available for this refset to update");

			}
			md = RefsetConvertor.getMetaData(rV);
			
			LOGGER.debug("Commiting updated refset");

			RefsetGraphFactory.commit(g);
						
		} catch(EntityNotFoundException e) {
			
			LOGGER.error("Error during graph interaction", e);
			RefsetGraphFactory.rollback(g);
			
			throw e;
		}
		
		catch (Exception e) {
			
			RefsetGraphFactory.rollback(g);			
			LOGGER.error("Error during graph interaction", e);
			
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
	private Vertex updateRefsetNode(Refset r, EventGraph<TitanGraph> g) throws RefsetGraphAccessException, EntityNotFoundException {

		LOGGER.debug("updateRefsetNode {}", r);

		Object rVId = rGao.getRefsetVertex(r.getId(), fgf.create(g.getBaseGraph()));
		
        g.addListener(new RefsetHeaderChangeListener(g.getBaseGraph(), r.getModifiedBy()));

		Vertex rV = g.getVertex(rVId);//, GRefset.class);
		
		if(rV == null) {
			
			throw new EntityNotFoundException("Can not find given refset to update");
			
		} 
		String desc = r.getDescription();
				
		if (!StringUtils.isEmpty(desc)) {
			
			//rV.setDescription(r.getDescription());
			rV.setProperty(DESC, desc);
		}
		
		if (r.getEffectiveTime() != null) {
			
			//rV.setEffectiveTime(r.getEffectiveTime().getMillis());
			rV.setProperty(EFFECTIVE_DATE, r.getEffectiveTime().getMillis());
		}
		
		String lang = r.getLanguageCode();

		if (!StringUtils.isEmpty(lang)) {
			
			//rV.setLanguageCode(lang);
			rV.setProperty(LANG_CODE, lang);
		}
		
		String moduleId = r.getModuleId();

		if (!StringUtils.isEmpty(moduleId)) {
			
			//rV.setModuleId(moduleId);
			rV.setProperty(MODULE_ID, moduleId);
		}
		
		Integer publishedFlag = r.isPublished() ? 1 : 0;

		//rV.setPublished(publishedFlag);
		rV.setProperty(PUBLISHED, publishedFlag);
		if (r.getPublishedDate() != null) {
			
			// rV.setPublishedDate(r.getPublishedDate().getMillis());
			rV.setProperty(PUBLISHED_DATE, r.getPublishedDate().getMillis());
		}
		
		String superRefsetTypeId = r.getSuperRefsetTypeId();

		if (!StringUtils.isEmpty(superRefsetTypeId)) {
			
			///rV.setSuperRefsetTypeId(superRefsetTypeId);
			rV.setProperty(SUPER_REFSET_TYPE_ID, r.getSuperRefsetTypeId());
		}

		String compTypeId = r.getComponentTypeId();

		if (!StringUtils.isEmpty(compTypeId)) {
			
			//rV.setComponentTypeId(compTypeId);
			rV.setProperty(MEMBER_TYPE_ID, compTypeId);
		}
		
		Integer activeFlag = r.isActive() ? 1 : 0;

		//rV.setActive(activeFlag);
		rV.setProperty(ACTIVE, activeFlag);
		
		String typeId = r.getTypeId();

		if (!StringUtils.isEmpty(typeId)) {
			
			//rV.setTypeId(typeId);
			rV.setProperty(TYPE_ID, typeId);
		}

		//rV.setModifiedBy(r.getModifiedBy());
		//rV.setModifiedDate(new DateTime().getMillis());
		rV.setProperty(MODIFIED_BY, r.getModifiedBy());
		rV.setProperty(MODIFIED_DATE, new DateTime().getMillis());
		DateTime ert = r.getExpectedReleaseDate();
		if (ert != null) {
			
			//rV.setExpectedReleaseDate(ert.getMillis());
			rV.setProperty(EXPECTED_RLS_DT, ert.getMillis());
		}
		
		if (!StringUtils.isEmpty(r.getSctId())) {
			
			//rV.setSctdId(r.getSctId());
			rV.setProperty(SCTID, r.getSctId());
		}

		
		LOGGER.debug("updateRefsetNode {} finished", rV);

		return rV;//.asVertex();
	}
	
	
	/**Adds member and refset and their history.Used when importing a RF2 file
	 * @param rf2rLst
	 * @param refsetId 
	 * @return
	 * @throws EntityNotFoundException 
	 * @throws RefsetGraphAccessException 
	 */
	public Map<String, String> addMembers(List<Rf2Record> rf2rLst, String refsetId) throws EntityNotFoundException, RefsetGraphAccessException {
		
		Map<String, String> outcome = new HashMap<String, String>();
		
		EventGraph<TitanGraph> g = factory.getEventGraph();
		g.addListener(new Rf2ImportMemberChangeListener(g.getBaseGraph(), "rf2import"));
		try {
			
			Vertex rV = rGao.getRefsetVertex(refsetId, fgf.create(g.getBaseGraph()));

			for (Rf2Record r : rf2rLst) {
				
				GRefset gr = fgf.create(g).frame(rV, GRefset.class);
				if ( StringUtils.isEmpty(r.getRefsetId()) || !(r.getRefsetId().equals(gr.getId()) 
						|| r.getRefsetId().equals(gr.getSctdId())) || r.getEffectiveTime() == null) {
					
					String error = String.format("Member does not have valid refset id - %s", r.getRefsetId());
					outcome.put(r.getReferencedComponentId(), error);
					continue;
				}
			
				String desc = rGao.getMemberDescription(r.getReferencedComponentId());
				
				if (StringUtils.isEmpty(desc)) {
					
					outcome.put(r.getReferencedComponentId(), "Unknown referenced component");
					continue;
				}
				
				GremlinPipeline<Vertex, Edge> pipe = new GremlinPipeline<Vertex, Edge>();			
				pipe.start(rV).inE("members").has(REFERENCE_COMPONENT_ID, T.eq, r.getReferencedComponentId());
				List<Edge> ls = pipe.toList();
				
				if(ls.isEmpty()) {
					
					//add this member
					Vertex vM = g.getBaseGraph().addVertexWithLabel(g.getBaseGraph().getVertexLabel("GMember"));
					GMember mg = fgf.create(g).getVertex(vM.getId(), GMember.class);
					
					addMemberProperties(r, mg);
					
					LOGGER.debug("Added Member as vertex to graph", mg.getId());
					
					Edge e = fgf.create(g).addEdge(null, mg.asVertex(), rV, "members");
					e.setProperty(REFERENCE_COMPONENT_ID, r.getReferencedComponentId());
					e.setProperty(START, r.getEffectiveTime().getMillis());
					e.setProperty(END, Long.MAX_VALUE);
					
				} else {
					

					for (Edge edge : ls) {
						
						
						//do a check if an existing member exist with some other state and lesser effective time
						
						Vertex vExistingMember = edge.getVertex(Direction.OUT);
						Long currentEndDt = edge.getProperty(END);
						Member m = RefsetConvertor.getMember(vExistingMember);
						if (m.getEffectiveTime().getMillis() == r.getEffectiveTime().getMillis()
								&& (m.isActive() ? "1" : "0").equals(r.getActive())) {
							
							LOGGER.trace("Not adding this record as it already exist {}", r.getId());

							//same record so do not re-import
							outcome.put(r.getReferencedComponentId(), "Already exist, not imported again");
							break;
							
						} else if (m.getEffectiveTime().getMillis() < r.getEffectiveTime().getMillis() && currentEndDt == Long.MAX_VALUE) {
							
							//mark this relationship with an end date of current effective time
							//edge.setProperty(END, r.getEffectiveTime().getMillis());
												
							//add a new member vertex with Long.MAX_VALUE end date
							//Vertex vM = g.getBaseGraph().addVertexWithLabel(g.getBaseGraph().getVertexLabel("GMember"));
							//GMember mg = fgf.create(g).getVertex(vM.getId(), GMember.class);
							GMember mg = fgf.create(g).getVertex(vExistingMember.getId(), GMember.class);
							addMemberProperties(r, mg);
							
							LOGGER.trace("Added Member as vertex to graph with new state {}", mg.getId());
							
							//Edge e = fgf.create(g).addEdge(null, mg.asVertex(), rV, "members");
							edge.setProperty(REFERENCE_COMPONENT_ID, r.getReferencedComponentId());
							edge.setProperty(START, r.getEffectiveTime().getMillis());
							edge.setProperty(END, Long.MAX_VALUE);
							
						} else if (m.getEffectiveTime().getMillis() > r.getEffectiveTime().getMillis() && currentEndDt == Long.MAX_VALUE) {
							
							//edge.setProperty(END, r.getEffectiveTime().getMillis());
							
							//add a new member vertex with Long.MAX_VALUE end date
							Vertex vM = g.getBaseGraph().addVertexWithLabel(g.getBaseGraph().getVertexLabel("GMember"));
				            vM.setProperty(TYPE, VertexType.hMember.toString());

							//GMember mg = fgf.create(g).getVertex(vM.getId(), GMember.class);
							GMember mg = fgf.create(g).getVertex(vM.getId(), GMember.class);
							addMemberProperties(r, mg);
							
							LOGGER.trace("Added Member as vertex to graph with new state {}", mg.getId());
							
							//Edge e = fgf.create(g).addEdge(null, mg.asVertex(), rV, EdgeLabel.hasState.toString());
							Edge e = vExistingMember.addEdge(EdgeLabel.hasState.toString(), vM);
							e.setProperty(REFERENCE_COMPONENT_ID, r.getReferencedComponentId());
							e.setProperty(START, r.getEffectiveTime().getMillis());
							e.setProperty(END, r.getEffectiveTime().getMillis());

							
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
			LOGGER.error("Error during graph interaction", e);
			
			throw new RefsetGraphAccessException(e.getMessage(), e);
			
		} finally {
			
			RefsetGraphFactory.shutdown(g);
		}
		
		
		return outcome;
	}
	
	private GMember addMemberProperties(Rf2Record r, GMember mg) {
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