/**
 * 
 */
package org.ihtsdo.otf.refset.service.matrix;

import static org.ihtsdo.otf.refset.domain.RGC.ID;
import static org.ihtsdo.otf.refset.domain.RGC.TYPE;
import static org.ihtsdo.otf.refset.domain.RGC.USER_NAME;
import static org.ihtsdo.otf.refset.domain.RGC.CREATED;
import static org.ihtsdo.otf.refset.domain.RGC.VIEW_COUNT;
import static org.ihtsdo.otf.refset.domain.RGC.DOWNLOAD_COUNT;
import static org.ihtsdo.otf.refset.graph.RefsetGraphFactory.rollback;
import static org.ihtsdo.otf.refset.graph.RefsetGraphFactory.commit;

import javax.annotation.Resource;

import org.ihtsdo.otf.refset.domain.Refset;
import org.ihtsdo.otf.refset.domain.RefsetRelations;
import org.ihtsdo.otf.refset.exception.EntityNotFoundException;
import org.ihtsdo.otf.refset.exception.RefsetServiceException;
import org.ihtsdo.otf.refset.graph.RefsetGraphAccessException;
import org.ihtsdo.otf.refset.graph.RefsetGraphFactory;
import org.ihtsdo.otf.refset.graph.gao.VertexType;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.thinkaurelius.titan.core.TitanGraph;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;

/**Graph Access component to do CRUD operation on underlying Refset graph
 * Operation in this class supports
 * 1. Add a view edge to {@link Refset} vertex
 * 2. Add a download edge to {@link Refset} vertex
 *
 */
@Repository
public class MatrixGAO {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MatrixGAO.class);
	
	private RefsetGraphFactory factory;
	
	/**
	 * @param userName
	 * @return
	 * @throws RefsetServiceException
	 */
	private Vertex getUserVertex(String userName) throws RefsetGraphAccessException {
		
		TitanGraph g = null;
		try {
			
			g = factory.getReadOnlyGraph();
			Iterable<Vertex> vRs = g.query().has(USER_NAME, userName).has(TYPE, VertexType.user.toString()).vertices();
			
			for (Vertex vertex : vRs) {
				
				return vertex;
			}
			
			
			throw new EntityNotFoundException("Invalid user name or User name not available in the system");

			
		} catch (EntityNotFoundException e) {
			
			LOGGER.error("Error occurred while fetching user", e);
			throw e;
			
		} catch (Exception e) {
			
			LOGGER.error("Error occurred while fetching user", e);
			throw new RefsetGraphAccessException(e);
		}
	}
	
	
	
	
	
	/**
	 * @param factory the factory to set
	 */
	@Resource(name = "refsetGraphFactory")
	public  void setFactory(RefsetGraphFactory factory) {
		
		this.factory = factory;
	}

	/**Adds an named event edge specified by {@link RefsetRelations}. 
	 * Currently Download and Viewed events are captured. 
	 * @param refsetId
	 * @param userName
	 * @throws RefsetGraphAccessException 
	 */
	public void addMatrixEvent(String refsetId, String userName, RefsetRelations eventType) throws RefsetGraphAccessException {

		
		LOGGER.debug("addExportEvent for {}", refsetId);
		
		if (StringUtils.isEmpty(refsetId) || StringUtils.isEmpty(userName)) {
			
			throw new EntityNotFoundException("Refset id and User name both are mandatory");
		}
		
		TitanGraph g = null;
		try {
			
			g = factory.getTitanGraph();
			Iterable<Vertex> vRs = g.query().has(ID, refsetId).has(TYPE, VertexType.refset.toString()).vertices();
			
			Vertex vr = null;
			for (Vertex vertex : vRs) {
				
				vr = vertex;
				break;
			}
			
			if (vr == null) {
			
				throw new EntityNotFoundException("Refset is not available to add view edge");
			}
			
			Vertex uV = null;
			try {
				
				uV = getUserVertex(userName);

			} catch(EntityNotFoundException e) {
				
				LOGGER.debug("User {} not found in system. Creating it", userName);
				uV = g.addVertexWithLabel(g.getVertexLabel("User"));
				uV.setProperty(USER_NAME, userName);
				uV.setProperty(CREATED, new DateTime().getMillis());
				uV.setProperty(TYPE, VertexType.user.toString());

			}
			
			//increment count of view or download
			String countProperty = VIEW_COUNT;
			Integer count = 0;
			
			if (RefsetRelations.downloaded.equals(eventType)) {
				
				countProperty = DOWNLOAD_COUNT;
			}
			
			if(vr.getProperty(countProperty) != null) {
				
				count = vr.getProperty(countProperty);
			} 
			
			vr.setProperty(countProperty, count + 1);
			
			
			g.addVertexWithLabel("user");
			Edge eventEdge = vr.addEdge(eventType.toString(), uV);
			eventEdge.setProperty(CREATED, new DateTime().getMillis());

			
			commit(g);
			
		} catch(Exception e) {
			
			rollback(g);
			
			LOGGER.error("Error occurred while adding viewed event", e);
			throw new RefsetGraphAccessException(e);
		}
		
	
		
	}


}