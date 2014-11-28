/**
 * 
 */
package org.ihtsdo.otf.refset.graph.gao;

import static org.ihtsdo.otf.refset.domain.RGC.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Resource;

import org.ihtsdo.otf.refset.domain.Member;
import org.ihtsdo.otf.refset.exception.EntityNotFoundException;
import org.ihtsdo.otf.refset.graph.RefsetGraphAccessException;
import org.ihtsdo.otf.refset.graph.RefsetGraphFactory;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.thinkaurelius.titan.core.TitanGraph;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.util.wrappers.event.EventGraph;
import com.tinkerpop.frames.FramedGraphFactory;

/**Graph Access component to do CRUD operation on underlying Refset graph 
 * for {@link Member} node only
 *
 */
@Repository
public class MemberGAO {
	
	
	private RefsetGAO rGao;
	
	/**
	 * @param rGao the rGao to set
	 */
	@Autowired
	public void setRefsetGao(RefsetGAO rGao) {
		this.rGao = rGao;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(MemberGAO.class);
		
	private RefsetGraphFactory factory;

	private static FramedGraphFactory fgf = new FramedGraphFactory();
	
	/**Retrieves a {@link Member} node id for given {@link Member#getReferenceComponentId()}
	 * @param rcId
	 * @param tg 
	 * @return
	 * @throws RefsetGraphAccessException
	 * @throws EntityNotFoundException 
	 */
	protected Vertex getMemberVertex(String id, TitanGraph tg) throws RefsetGraphAccessException, EntityNotFoundException {
		
		Vertex result = null;
		
		if (StringUtils.isEmpty(id)) {
			
			throw new EntityNotFoundException("Member does not exist for given reference component id");
		}
		try {

			//TODO upgrade this search with status and effective date
			@SuppressWarnings("unchecked")
			Iterable<Vertex> vr = tg.query().has(ID, id).has(TYPE, VertexType.member.toString()).has(END, Long.MAX_VALUE).limit(1).vertices();
			
			for (Vertex v : vr) {
				
				result = v;
				break;
			}
		
			
		} catch (Exception e) {
			
			LOGGER.error("Error during member lookup for reference component id {}", id, e);
			throw new RefsetGraphAccessException(e.getMessage(), e);

		}
		
		if(result == null) 
			throw new EntityNotFoundException("Record does not exist");
		
		return result;

	}
	

	
	/** Add a {@link Member} if does not exist in graph for a given {@link Member#getReferenceComponentId()}
	 * and returns {@link Member} node id {@link Vertex#getId()}
	 * @param m
	 * @throws RefsetGraphAccessException 
	 * @throws EntityNotFoundException 
	 */
	protected Vertex addMemberNode(Member m, EventGraph<TitanGraph> eg) throws RefsetGraphAccessException, EntityNotFoundException {
		
		if (m == null || StringUtils.isEmpty(m.getReferencedComponentId())) {
			
			throw new EntityNotFoundException("Invalid member details. refset component id is mandatory in member details");
		}
		
		LOGGER.debug("Adding member {}", m);
		eg.addListener(new EffectiveTimeChangeListener(eg.getBaseGraph(), m.getModifiedBy()));
		//Vertex mV = eg.getBaseGraph().addVertexWithLabel(eg.getBaseGraph().getVertexLabel("GMember"));
		
		Vertex mV = eg.addVertex(m.getId());
		//Vertex mg = eg.getVertex(vM.getId());//, GMember.class);
		
		Integer activeFlag = m.isActive() ? 1 : 0;

		//mg.setActive(activeFlag);
		mV.setProperty(ACTIVE, activeFlag);
		mV.setProperty(ID, UUID.randomUUID().toString());

		DateTime et = m.getEffectiveTime();
		if ( et != null) {
			
			mV.setProperty(EFFECTIVE_DATE, et.getMillis());

		}
		
		//mg.setModifiedBy(m.getModifiedBy());
		mV.setProperty(MODIFIED_BY, m.getModifiedBy());
		//mg.setCreateBy(m.getCreatedBy());
		mV.setProperty(CREATED_BY, m.getCreatedBy());
		
		//mg.setCreated(new DateTime().getMillis());
		mV.setProperty(CREATED, new DateTime().getMillis());
		//mg.setModifiedDate(new DateTime().getMillis());
		mV.setProperty(MODIFIED_DATE, new DateTime().getMillis());
		//mg.setModuleId(m.getModuleId());
		mV.setProperty(MODULE_ID, m.getModuleId());
		
		Integer publishedFlag = m.isPublished() ? 1 : 0;

		//mg.setPublished(publishedFlag);
		mV.setProperty(PUBLISHED, publishedFlag);
		
		//mg.setType(VertexType.member.toString());
		mV.setProperty(TYPE, VertexType.member.toString());
		
		LOGGER.debug("Added Member as vertex to graph", mV.getId());
		return mV;
		
	}
	
	/**
	 * @param factory the factory to set
	 */
	@Resource(name = "refsetGraphFactory")
	public  void setFactory(RefsetGraphFactory factory) {
		
		this.factory = factory;
	}

	/**
	 * @param refsetId
	 * @param members
	 * @param user 
	 * @throws EntityNotFoundException 
	 * @throws RefsetGraphAccessException 
	 */
	public Map<String, String> addMembers(String refsetId, Set<Member> members, String user) throws RefsetGraphAccessException, EntityNotFoundException {
		
		Map<String, String> outcomeMap = new HashMap<String, String>(); //needed at front end
		
		
		
		EventGraph<TitanGraph> tg = null;
		
		try {
			tg = factory.getEventGraph();			
			Vertex rV = rGao.getRefsetVertex(refsetId, fgf.create(tg.getBaseGraph()));

			
			//try adding all members and accumulate error/success

			for (Member m : members) {
		
				Vertex mV;
				
				try {
					
					mV = getMemberVertex(m.getId(), tg.getBaseGraph());
					
					LOGGER.debug("Member & relation already exist  from {} to {}", mV.getId(), rV.getId());

					outcomeMap.put(m.getReferencedComponentId(), "Already exist, Not adding");

				} catch(EntityNotFoundException ex) {
					
					//add member
					mV = addMemberNode(m, tg);
					
					Edge e = tg.addEdge(null, mV, rV, "members");
					e.setProperty(REFERENCE_COMPONENT_ID, m.getReferencedComponentId());
					long start = m.getEffectiveTime() != null ? m.getEffectiveTime().getMillis() : m.getCreated().getMillis();
					e.setProperty(START, start);
					e.setProperty(END, Long.MAX_VALUE);

					LOGGER.debug("Added relationship as edge from {} to {}", mV.getId(), rV.getId());
					outcomeMap.put(m.getReferencedComponentId(), "Success");
				}				
				

			}
			
			RefsetGraphFactory.commit(tg);;

		} catch (EntityNotFoundException e) {
			
			LOGGER.error("Error during bulk member upload", e);

			RefsetGraphFactory.rollback(tg);
			
			throw e;
			
		}catch (Exception e) {
			
			LOGGER.error("Error during bulk member upload", e);

			RefsetGraphFactory.rollback(tg);
			throw new RefsetGraphAccessException("Error while adding members");
			
		} finally {
			
			RefsetGraphFactory.shutdown(tg);
		}

		return outcomeMap;
		
	}



	/**
	 * @param refsetId
	 * @param rcId
	 * @param user 
	 * @throws EntityNotFoundException 
	 * @throws RefsetGraphAccessException 
	 */
	public void removeMember(String refsetId, String rcId, String user) throws EntityNotFoundException, RefsetGraphAccessException {

		LOGGER.debug("removing member {} from refset {}", rcId, refsetId);
		EventGraph<TitanGraph> tg = null;
		if (StringUtils.isEmpty(refsetId) || StringUtils.isEmpty(rcId)) {
			
			throw new EntityNotFoundException("Invalid request, refset id and reference component id is required in member remove request");
		}
		try {
			
			tg = factory.getEventGraph();
	        tg.addListener(new MemberChangeListener(tg.getBaseGraph(), user));
	        tg.addListener(new EffectiveTimeChangeListener(tg.getBaseGraph(), user));
			Vertex rV = rGao.getRefsetVertex(refsetId, fgf.create(tg.getBaseGraph()));

			Iterable<Edge> eR = tg.getEdges(REFERENCE_COMPONENT_ID, rcId);
						
			
			for (Edge e : eR) {
				
				Vertex vIn = e.getVertex(Direction.IN);
				if (vIn != null && vIn.equals(rV)) {
					
					LOGGER.debug("Removing member relationship from refset {}", rV);
					if(!"1".equals(e.getVertex(Direction.OUT).getProperty(PUBLISHED))) {
						
						tg.removeEdge(e);

					} else {
						
						throw new EntityNotFoundException("Published member can not be deleted");
					}

					break;
				}
			}
			

			RefsetGraphFactory.commit(tg);

		} catch (EntityNotFoundException e) {
			
			LOGGER.error("Error while removing member", e);

			RefsetGraphFactory.rollback(tg);
			
			throw e;
			
		} catch (Exception e) {
			
			LOGGER.error("Error while removing member", e);

			RefsetGraphFactory.rollback(tg);
			
			throw new RefsetGraphAccessException("Error while removing members");
			
		} finally {
			
			RefsetGraphFactory.shutdown(tg);
		}
	}
		
	/**This is a transaction so we can not individually send error/success for a conceptId!! 
	 * @param refsetId
	 * @param user 
	 * @param rcId
	 * @throws EntityNotFoundException 
	 * @throws RefsetGraphAccessException 
	 */
	public Map<String, String> removeMembers(String refsetId, Set<String> rcIds, String user) throws EntityNotFoundException, RefsetGraphAccessException {

		LOGGER.debug("removing members {} from refset {}", rcIds, refsetId);
		EventGraph<TitanGraph> tg = null;
		Map<String, String> outcome = new HashMap<String, String>();
		
		try {
			
			tg = factory.getEventGraph();
	        tg.addListener(new MemberChangeListener(tg.getBaseGraph(), user));
	        tg.addListener(new EffectiveTimeChangeListener(tg.getBaseGraph(), user));

	        Vertex rV = rGao.getRefsetVertex(refsetId, fgf.create(tg.getBaseGraph()));

			Iterable<Edge> eR = rV.getEdges(Direction.IN, "members");
			
			for (Edge e : eR) {
				
				String referencedComponentId = e.getProperty(REFERENCE_COMPONENT_ID);
				
				if (!StringUtils.isEmpty(referencedComponentId) 
						&& rcIds.contains(referencedComponentId)) {
					
					LOGGER.debug("Removing member {} relationship from refset {}", e, rV);
					e.remove();
					
					outcome.put(referencedComponentId, "Success");

				}
			}

			RefsetGraphFactory.commit(tg);

		} catch (EntityNotFoundException e) {
			
			LOGGER.error("Error while removing member", e);

			RefsetGraphFactory.rollback(tg);
			
			throw e;
			
		} catch (Exception e) {
			
			LOGGER.error("Error while removing member", e);

			RefsetGraphFactory.rollback(tg);
			
			throw new RefsetGraphAccessException("Error while removing members");
			
		} finally {
			
			RefsetGraphFactory.shutdown(tg);
		}

		
		return outcome;
		
	}

	/** Update a {@link Member} and returns {@link Member} node {@link Vertex}. 
	 * If does not exist in graph for a given {@link Member#getId()} throw {@link EntityNotFoundException}
	 * @param member
	 * @throws RefsetGraphAccessException 
	 * @throws EntityNotFoundException 
	 */
	protected Vertex updateMemberNode(Member m, EventGraph<TitanGraph> tg) throws RefsetGraphAccessException, EntityNotFoundException {
		
		if (m == null || StringUtils.isEmpty(m.getId()) || StringUtils.isEmpty(m.getReferencedComponentId())) {
			
			throw new EntityNotFoundException("Invalid member details. Reference component id and member id is mandatory in member details");
		}			
        tg.addListener(new MemberChangeListener(tg.getBaseGraph(), m.getModifiedBy()));
        tg.addListener(new EffectiveTimeChangeListener(tg.getBaseGraph(), m.getModifiedBy()));

		LOGGER.debug("Updating member {}", m);
		Iterable<Vertex> vr = tg.query().has(TYPE, VertexType.member.toString()).has(ID, m.getId()).has(END, Long.MAX_VALUE).limit(1).vertices();

		if (vr != null ) {
			
			for (Vertex v : vr) {
				
				//mg = tg.getVertex(v.getId());//, GMember.class);
				LOGGER.debug("Updating member {} and vertex {} ", m, v);
				if(Integer.valueOf(1).equals(v.getProperty(PUBLISHED))) {
					
					throw new EntityNotFoundException("Member can not be updated once published");
				}
				Integer activeFlag = m.isActive() ? 1 : 0;

				v.setProperty(ACTIVE, activeFlag);
				DateTime et = m.getEffectiveTime();
				if ( et != null) {
					
					//mg.setEffectiveTime(et.getMillis());
					v.setProperty(EFFECTIVE_DATE, et.getMillis());
				}
				v.setProperty(MODIFIED_BY, m.getModifiedBy());
				v.setProperty(MODIFIED_DATE, new DateTime().getMillis());
				//mg.setModifiedBy(m.getModifiedBy());
				//mg.setModifiedDate(new DateTime().getMillis());
				if (!StringUtils.isEmpty(m.getModuleId())) {
					
					v.setProperty(MODULE_ID, m.getModuleId());

					//mg.setModuleId(m.getModuleId());

				}
				
				Integer publishedFlag = m.isPublished() ? 1 : 0;

				//mg.setPublished(publishedFlag);
				v.setProperty(PUBLISHED, publishedFlag);
				
				//mV = mg;//mg.asVertex();
				LOGGER.debug("Updated Member as vertex to graph", v.getId());	
				return v;
				
			}
		}

		
		String msg = "Member details not available for id " + m.getId();
		
		throw new EntityNotFoundException(msg);
		
		
	}

}