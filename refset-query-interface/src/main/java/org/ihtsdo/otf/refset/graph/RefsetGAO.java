/**
 * 
 */
package org.ihtsdo.otf.refset.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ihtsdo.otf.refset.domain.Member;
import org.ihtsdo.otf.refset.domain.MetaData;
import org.ihtsdo.otf.refset.domain.Refset;
import org.ihtsdo.otf.refset.exception.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientElement;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

/**Graph Access component to do CRUD operation on underlying Refset graph
 * @author Episteme Partners
 *
 */
@Repository
public class RefsetGAO {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RefsetGAO.class);
	
	private static final String REFSET_CLASS_NAME = "Refset";
	private static final String MEMBER_CLASS_NAME = "Member";

	private static final String DOT = ".";

	
	@Autowired
	private RefsetGraphFactory factory;

	/**
	 * @param r a {@link Refset} with or without members
	 * @throws RefsetGraphAccessException
	 * @throws EntityNotFoundException 
	 */
	public MetaData addRefset(Refset r) throws RefsetGraphAccessException {
		
		LOGGER.debug("Adding refset {}", r);

		OrientGraph g = null;
		MetaData md = r.getMetaData();
		
		try {
			
			g = factory.getOrientGraph();

			Object rId = addRefsetNode(r, g);	
			
			final Vertex rV = g.getVertex(rId);

			
			/*if members exist then add members*/
			List<Member> members = r.getMembers();
			int i = 0;
			if( !CollectionUtils.isEmpty(members) ) {
				
				for (Member m : members) {
					
					Object mId = addMemberNode(m, g);
					
					Vertex mV = g.getVertex(mId);
					
					LOGGER.info("Adding relationship member is part of refset as edge {}, counter      ---------------  {}", mV.getId(), i++);

					/*Add this member to refset*/
					g.addEdge(null, mV, rV, "members");

					LOGGER.debug("Added relationship as edge from {} to {}", mV.getId(), rV.getId());

				}

				
			} else {
				
				LOGGER.debug("No member available for this refset to add");

			}
			
			LOGGER.info("Commiting");

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
	
	private void shutdown(OrientGraph g) {
		
		LOGGER.info("Shutting down graph {}", g);
		
		if (g != null) g.shutdown();
		
	}

	/**
	 * @param r {@link Refset}
	 * @param g {@link OrientGraph}
	 * @return id of {@link Refset} node
	 * @throws RefsetGraphAccessException
	 * @throws EntityNotFoundException 
	 */
	private Object addRefsetNode(Refset r, OrientGraph g) throws RefsetGraphAccessException {
		// TODO Auto-generated method stub
		
		Object rVId;
		

		try {
			
			rVId = getRefsetNodeId(r.getId());
			
		} catch (EntityNotFoundException e) {
			
			LOGGER.debug("Refset does not exist, adding  {}", r.toString());
			OrientGraph ig = null;
			
			try {
				
				ig= factory.getOrientGraph();
				final Vertex rV = ig.addVertex("class:Refset", RefsetConvertor.getRefsetProperties(r));
				LOGGER.debug("Added Refset as vertex to graph {}", rV.getId());

				rVId = rV.getId();
				ig.commit();
				
			} catch (Exception ex) {
				
				rollback(ig);
				throw new RefsetGraphAccessException(ex);
			} finally {
				
				shutdown(ig);
			}
			
			
			
		}
		
		LOGGER.debug("Refset  vertex id is {} ", rVId);

		return rVId;
	}

	/** Removes a {@link Refset} from graph
	 * @param r {@link Refset}
	 * @throws RefsetGraphAccessException
	 */
	public void removeRefset(Refset r) throws RefsetGraphAccessException {
		
		OrientGraph g = null;
		
		try {
			
			g = factory.getOrientGraph();
			
			//TODO upgrade this search with status and effective date
			
			try {
				//indirect check if that class exist
				g.countVertices(REFSET_CLASS_NAME);

			} catch (IllegalArgumentException e) {
				
				//This is when no node exist with that class name
				LOGGER.error("Error during removal of refset -: {}", e.getMessage());
				
				return;
			}

			Iterable<Vertex> vRs = g.getVertices(  REFSET_CLASS_NAME + DOT + RGC.ID, r.getId());

		
			for (Vertex vR : vRs) {
				
				LOGGER.debug("Removing Refset vertex from graph {}", vR.getId());
				g.removeVertex(vR);

			}
			
			g.commit();
			
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
	 * @return
	 * @throws RefsetGraphAccessException
	 * @throws EntityNotFoundException 
	 */
	private Object getMemberNodeId(String rcId) throws RefsetGraphAccessException, EntityNotFoundException {
		
		Object result = null;
		
		OrientGraph g = null;
		
		try {
			
			g = factory.getOrientGraph();
			
			//TODO upgrade this search with status and effective date
			Iterable<Vertex> vs = g.getVertices(MEMBER_CLASS_NAME + DOT + RGC.REFERENCE_COMPONENT_ID, rcId);
			if( vs != null) {
				
				for (Vertex v : vs) {
					
					Boolean isActive = v.getProperty(RGC.ACTIVE);
					if(isActive) {
						LOGGER.debug("Member {} already exist", v.getProperty(RGC.ID));
						result = v.getId();
						break;
					}
				}
			}
			g.commit();
			
		} catch (NullPointerException e) {
			//this will occur first time when there is no member class node
			LOGGER.error("Member class does not exist {}", e);
			rollback(g);
			throw new EntityNotFoundException("Record does not exist");
			
		} catch (Exception e) {
			
			rollback(g);
			throw new RefsetGraphAccessException(e.getMessage(), e);

		} finally {
			
			shutdown(g);
		}
		
		if(result == null) 
			throw new EntityNotFoundException("Record does not exist");
		
		return result;

	}
	
	private void rollback(OrientGraph g) {
		
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

		Object rVId = null;
		
		OrientGraph g = null;
		
		try {
			
			g = factory.getOrientGraph();

			//TODO upgrade this search with status and effective date
			Iterable<Vertex> vs = g.getVertices(REFSET_CLASS_NAME + DOT + RGC.ID, id);
			
			for (Vertex v : vs) {
				
				String result = v.getProperty(RGC.ID);
				LOGGER.debug("Refset is {} ", v);

				if(id.equalsIgnoreCase(result)) {
					
					LOGGER.debug("Refset {} already exist", v.getProperty(RGC.ID));

					rVId = v.getId();
					break;
				};
			}
			g.commit();
			
		} catch (NullPointerException e) {

			LOGGER.error("Error during graph ineraction", e);
			//carry on
			rollback(g);
		}
		catch (Exception e) {
			
			rollback(g);			
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
	private Object addMemberNode(Member m, OrientGraph g) throws RefsetGraphAccessException {
		
		Object id = null;
		try {
			
			id = getMemberNodeId(m.getReferenceComponentId());

			LOGGER.debug("Member already exist as vertex to graph {}", id);

		} catch (EntityNotFoundException e) {
			
			//ADD a new Entity
			OrientGraph ig = null;
			try {
				
				ig = factory.getOrientGraph();
				Vertex mV = ig.addVertex("class:Member", RefsetConvertor.getMemberProperties(m));
				
				LOGGER.debug("Added Member as vertex to graph", mV.getId());
							
				id = mV.getId();
				ig.commit();
				
			} catch (Exception ex) {
				
				rollback(ig);
				throw new RefsetGraphAccessException(ex);
				
			} finally {
				
				shutdown(ig);
				
			}
		}
		
		
		return id;
	}
	
	
	/**Retrieves a {@link Refset}  for a given refsetId
	 * @param id
	 * @return {@link Refset}
	 * @throws RefsetGraphAccessException
	 */
	public Refset getRefset(String id) throws RefsetGraphAccessException, EntityNotFoundException {
				
		OrientGraph g = null;
		Refset r = null;
		try {
			
			g = factory.getOrientGraph();

			//TODO upgrade this search with status and effective date
			Iterable<Vertex> vs = g.getVertices(REFSET_CLASS_NAME + DOT + RGC.ID, id);
			
			for (Vertex v : vs) {
				
				r = RefsetConvertor.convert2Refset(v);
				LOGGER.debug("Refset is {} ", r);
				r.setMetaData(getMetaData(v.getId()));
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
				
		OrientGraph g = null;
		Refset r = null;
		try {
			
			g = factory.getOrientGraph();

			Vertex v = g.getVertex(nodeId);

			if(v != null) {
				
				r = RefsetConvertor.convert2Refset(v);
				LOGGER.debug("Refset is {} ", r);
				MetaData md = new MetaData();
				md.setId(v.getId());
				md.setType("vertex");
				Integer version = v.getProperty("@Version");
				md.setVersion(version);
				r.setMetaData(md);
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
	
	

	/**
	 * @param f the f to set
	 */
	public void setF(RefsetGraphFactory f) {
		this.factory = f;
	}

	public List<Refset> getRefSets() throws RefsetGraphAccessException {
		
		OrientGraph g = null;
		
		List<Refset> refsets = new ArrayList<Refset>();
		try {
			
			g = factory.getOrientGraph();

			//TODO upgrade this search with status and effective date
			Iterable<Vertex> vs = g.getVerticesOfClass(REFSET_CLASS_NAME);
			
			for (Vertex v : vs) {
				
				Refset r = RefsetConvertor.convert2Refset(v);
				LOGGER.debug("Refset is {} ", r);
				refsets.add(r);
			}
			g.commit();
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
		
		OrientGraph g = null;
		MetaData md = null;
		
		try {
			
			g = factory.getOrientGraph();

			OrientElement e = g.getElement(rId);
			if( e != null) {
				
				md = new MetaData();
				md.setId(e.getId());
				md.setType(e.getElementType());
				Integer version = e.getProperty("@Version");
				md.setVersion(version);
				
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
		
	
		OrientGraph g = null;
		MetaData md = r.getMetaData();
		
		try {
			
			g = factory.getOrientGraph();

			final Vertex rV = updateRefsetNode(r, g);	
			
			
			/*if members exist then add members*/
			List<Member> members = r.getMembers();
			
			if( !CollectionUtils.isEmpty(members) ) {
				
				for (Member m : members) {
					
					Object mId = addMemberNode(m, g);
					
					Vertex mV = g.getVertex(mId);
					
					LOGGER.debug("Adding relationship member is part of refset as edge {}", mV.getId());

					/*Add this member to refset*/
					g.addEdge(null, mV, rV, "members");

					LOGGER.debug("Added relationship as edge from {} to {}", mV.getId(), rV.getId());

				}

				
			} else {
				
				LOGGER.debug("No member available for this refset to add");

			}
			
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
	 * @param g {@link OrientGraph}
	 * @return id of {@link Refset} node
	 * @throws RefsetGraphAccessException
	 * @throws EntityNotFoundException 
	 */
	private Vertex updateRefsetNode(Refset r, OrientGraph g) throws RefsetGraphAccessException, EntityNotFoundException {
		// TODO Auto-generated method stub
		
		Object rVId = getRefsetNodeId(r.getId());
		
		Vertex rV = g.getVertex(rVId);
		
		if(rV == null) {
			
			throw new EntityNotFoundException("Can not find given refset to update");
			
		} 
		
		Map<String, Object> fields = RefsetConvertor.getRefsetProperties(r);
		
		if( !CollectionUtils.isEmpty(fields) ) {
			
			Set<String> keys = fields.keySet();
			for (String k : keys) {
				
				rV.setProperty(k, fields.get(k));

			}
			
		}

		return rV;
	}
	

}
