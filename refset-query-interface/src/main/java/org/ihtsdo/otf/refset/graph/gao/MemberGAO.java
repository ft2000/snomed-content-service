/**
 * 
 */
package org.ihtsdo.otf.refset.graph.gao;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.ihtsdo.otf.refset.domain.Member;
import org.ihtsdo.otf.refset.exception.EntityNotFoundException;
import org.ihtsdo.otf.refset.graph.RGC;
import org.ihtsdo.otf.refset.graph.RefsetGraphAccessException;
import org.ihtsdo.otf.refset.graph.RefsetGraphFactory;
import org.ihtsdo.otf.refset.graph.schema.GMember;
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
import com.tinkerpop.frames.FramedGraphFactory;
import com.tinkerpop.frames.FramedTransactionalGraph;

/**Graph Access component to do CRUD operation on underlying Refset graph
 * @author Episteme Partners
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
	protected Vertex getMemberVertex(String rcId, TitanGraph tg) throws RefsetGraphAccessException, EntityNotFoundException {
		
		Vertex result = null;
		
		if (StringUtils.isEmpty(rcId)) {
			
			throw new EntityNotFoundException("Member does not exist for given reference component id");
		}
		try {

			//TODO upgrade this search with status and effective date
			Iterable<Vertex> vr = tg.getVertices(RGC.REFERENCE_COMPONENT_ID, rcId);
			
			for (Vertex v : vr) {
				
				result = v;
				break;
			}
		
			
		} catch (Exception e) {
			
			LOGGER.error("Error during member lookup for reference component id {}", rcId, e);
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
	protected Vertex addMemberNode(Member m, FramedTransactionalGraph<TitanGraph> tg) throws RefsetGraphAccessException, EntityNotFoundException {
		
		Vertex mV = null;
		if (m == null || StringUtils.isEmpty(m.getReferencedComponentId())) {
			
			throw new EntityNotFoundException("Invalid member details. refset component id is mandatory in member details");
		}
		try {
			
			mV = getMemberVertex(m.getReferencedComponentId(), tg.getBaseGraph());

			LOGGER.debug("Member already exist as vertex to graph {}", mV);

		} catch (EntityNotFoundException e) {
			
			LOGGER.debug("Member does not exist adding {}", m);

			GMember mg = tg.addVertex("GMember", GMember.class);
			
			mg.setActive(m.isActive());
			
			if (StringUtils.isEmpty(m.getId())) {
				
				mg.setId(UUID.randomUUID().toString());
				
			} else {
			
				mg.setId(m.getId());

			}
			mg.setModuleId(m.getModuleId());
			mg.setReferencedComponentId(m.getReferencedComponentId());
			
			DateTime dt = m.getEffectiveTime();
			if (dt != null) {
				
				mg.setEffectiveTime(dt.getMillis());

			}
			
						
			mV = mg.asVertex();
			LOGGER.debug("Added Member as vertex to graph", mV.getId());

		}

		return mV;
		
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
	public Map<String, String> addMembers(String refsetId, Set<Member> members) throws RefsetGraphAccessException, EntityNotFoundException {
		
		Map<String, String> outcomeMap = new HashMap<String, String>(); //needed at front end
		
		
		
		FramedTransactionalGraph<TitanGraph> tg = null;
		
		try {
			
			tg = fgf.create(factory.getTitanGraph());
			
			Vertex rV = rGao.getRefsetVertex(refsetId, tg);

			
			//try adding all members and accumulate error/success

			for (Member m : members) {
				
				Vertex mV;
				
				try {
					
					mV = getMemberVertex(m.getReferencedComponentId(), tg.getBaseGraph());

				} catch(EntityNotFoundException e) {
					
					//add member
					mV = addMemberNode(m, tg);
				}
				
				//only add relationship if does not exist already
				
				Iterable<Edge> eR = tg.getEdges(RGC.REFERENCE_COMPONENT_ID, m.getReferencedComponentId());
				
				boolean existingMember = false;
				
				for (Edge e : eR) {
					
					Vertex vIn = e.getVertex(Direction.IN);
					if (vIn != null && vIn.equals(rV)) {
						
						LOGGER.debug("Updating relationship member with published flag refset as edge {}", mV.getId());

						e.setProperty(RGC.PUBLISHED, m.isPublished());
						e.setProperty(RGC.ACTIVE, m.isActive());
						e.setProperty(RGC.MODIFIED_DATE, new DateTime().getMillis());
						e.setProperty(RGC.MODIFIED_BY, m.getModifiedBy());

						existingMember = true;
						break;
					}

				}
				
				if (!existingMember) {
					
					Edge e = tg.addEdge(null, mV, rV, "members");

					LOGGER.debug("Added relationship as edge from {} to {}", mV.getId(), rV.getId());
					
					//added effective date of relationship
					e.setProperty(RGC.EFFECTIVE_DATE, new DateTime().getMillis());
					e.setProperty(RGC.REFERENCE_COMPONENT_ID, m.getReferencedComponentId());
					e.setProperty(RGC.PUBLISHED, m.isPublished());
					e.setProperty(RGC.ACTIVE, m.isActive());
					e.setProperty(RGC.MODIFIED_DATE, new DateTime().getMillis());
					e.setProperty(RGC.MODIFIED_BY, m.getModifiedBy());

					e.setProperty(RGC.CREATED, new DateTime().getMillis());
					e.setProperty(RGC.CREATED_BY, m.getCreatedBy());


				}
				
				

				outcomeMap.put(m.getReferencedComponentId(), "Success");
			}
			
			tg.commit();

		} catch (EntityNotFoundException e) {
			
			LOGGER.error("Error during bulk member upload", e);

			if (tg != null) { tg.rollback(); }
			
			throw e;
			
		}catch (Exception e) {
			
			LOGGER.error("Error during bulk member upload", e);

			if (tg != null) { tg.rollback(); }
			
			throw new RefsetGraphAccessException("Error while adding members");
			
		} finally {
			
			RefsetGraphFactory.shutdown(tg);
		}

		return outcomeMap;
		
	}



	/**
	 * @param refsetId
	 * @param rcId
	 * @throws EntityNotFoundException 
	 * @throws RefsetGraphAccessException 
	 */
	public void removeMember(String refsetId, String rcId) throws EntityNotFoundException, RefsetGraphAccessException {

		LOGGER.debug("removing member {} from refset {}", rcId, refsetId);
		FramedTransactionalGraph<TitanGraph> tg = null;
		if (StringUtils.isEmpty(refsetId) || StringUtils.isEmpty(rcId)) {
			
			throw new EntityNotFoundException("Invalid request, refset id and reference component id is required in member remove request");
		}
		try {
			
			tg = fgf.create(factory.getTitanGraph());
			
			Vertex rV = rGao.getRefsetVertex(refsetId, tg);

			Iterable<Edge> eR = tg.getEdges(RGC.REFERENCE_COMPONENT_ID, rcId);
						
			
			for (Edge e : eR) {
				
				Vertex vIn = e.getVertex(Direction.IN);
				if (vIn != null && vIn.equals(rV)) {
					
					LOGGER.debug("Removing member relationship from refset {}", rV);
					tg.removeEdge(e);

					break;
				}
			}
			

			tg.commit();

		} catch (EntityNotFoundException e) {
			
			LOGGER.error("Error while removing member", e);

			if (tg != null) { tg.rollback(); }
			
			throw e;
			
		} catch (Exception e) {
			
			LOGGER.error("Error while removing member", e);

			if (tg != null) { tg.rollback(); }
			
			throw new RefsetGraphAccessException("Error while removing members");
			
		} finally {
			
			RefsetGraphFactory.shutdown(tg);
		}
	}
		
		/**This is a transaction so we can not individually send error/success for a conceptId!! 
		 * @param refsetId
		 * @param rcId
		 * @throws EntityNotFoundException 
		 * @throws RefsetGraphAccessException 
		 */
		public Map<String, String> removeMembers(String refsetId, Set<String> rcIds) throws EntityNotFoundException, RefsetGraphAccessException {

			LOGGER.debug("removing members {} from refset {}", rcIds, refsetId);
			TitanGraph tg = null;
			Map<String, String> outcome = new HashMap<String, String>();
			
			try {
				
				tg = factory.getTitanGraph();
				
				Vertex rV = rGao.getRefsetVertex(refsetId, fgf.create(tg));

				Iterable<Edge> eR = rV.getEdges(Direction.IN, "members");
				
				for (Edge e : eR) {
					
					String referencedComponentId = e.getProperty(RGC.REFERENCE_COMPONENT_ID);
					
					if (!StringUtils.isEmpty(referencedComponentId) 
							&& rcIds.contains(referencedComponentId)) {
						
						LOGGER.debug("Removing member {} relationship from refset {}", e, rV);
						e.remove();
						
						outcome.put(referencedComponentId, "Success");

					}
				}

				tg.commit();

			} catch (EntityNotFoundException e) {
				
				LOGGER.error("Error while removing member", e);

				if (tg != null) { tg.rollback(); }
				
				throw e;
				
			} catch (Exception e) {
				
				LOGGER.error("Error while removing member", e);

				if (tg != null) { tg.rollback(); }
				
				throw new RefsetGraphAccessException("Error while removing members");
				
			} finally {
				
				RefsetGraphFactory.shutdown(tg);
			}

			
			return outcome;
		
	}

}