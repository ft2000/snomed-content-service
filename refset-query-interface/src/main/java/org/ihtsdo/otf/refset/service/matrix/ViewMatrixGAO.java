/**
 * 
 */
package org.ihtsdo.otf.refset.service.matrix;

import static org.ihtsdo.otf.refset.domain.RGC.END;
import static org.ihtsdo.otf.refset.domain.RGC.PUBLISHED;
import static org.ihtsdo.otf.refset.domain.RGC.TYPE;
import static org.ihtsdo.otf.refset.domain.RGC.VIEW_COUNT;
import static org.ihtsdo.otf.refset.graph.RefsetGraphFactory.rollback;
import static org.ihtsdo.otf.refset.graph.RefsetGraphFactory.commit;
import static org.ihtsdo.otf.refset.graph.gao.RefsetConvertor.getRefsets;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.ihtsdo.otf.refset.domain.RefsetDTO;
import org.ihtsdo.otf.refset.exception.RefsetServiceException;
import org.ihtsdo.otf.refset.graph.RefsetGraphAccessException;
import org.ihtsdo.otf.refset.graph.RefsetGraphFactory;
import org.ihtsdo.otf.refset.graph.gao.EdgeLabel;
import org.ihtsdo.otf.refset.graph.gao.VertexType;
import org.ihtsdo.otf.refset.graph.schema.GRefset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.thinkaurelius.titan.core.Order;
import com.thinkaurelius.titan.core.TitanGraph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.FramedGraph;
import com.tinkerpop.frames.FramedGraphFactory;
import com.tinkerpop.gremlin.java.GremlinPipeline;

/**Graph Access component to do read operation on underlying Refset graph
 * Operation in this class supports
 * 1. get published {@link Refset} and their view count
 * 2. get published {@link Refset} and their download count
 *
 */
@Repository
public class ViewMatrixGAO {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ViewMatrixGAO.class);
	
	private RefsetGraphFactory factory;
	
	
	/**Adds an event edge namely - viewed to refset vertex
	 * @param noOfResults 
	 * @param refsetId
	 * @param userName
	 * @throws RefsetServiceException
	 */
	protected List<RefsetDTO> getMostViewedPublishedRefsets(int noOfResults) throws RefsetGraphAccessException {
		
		LOGGER.debug("getMostViewedPublishedRefsets for {}");
		
		TitanGraph g = null;
		try {
			
			g = factory.getReadOnlyGraph();
			
			Iterable<Vertex> rVs = g.query().has(PUBLISHED, 1)
										.has(TYPE, VertexType.refset.toString())
										.has(VIEW_COUNT)
										.orderBy(VIEW_COUNT, Order.DESC)
										.limit(noOfResults).vertices();

			FramedGraph<TitanGraph> fg = new FramedGraphFactory().create(g);
			List<GRefset> ls = new ArrayList<GRefset>();
			
			for (Vertex rV : rVs) {
				
				GRefset gR = fg.getVertex(rV.getId(), GRefset.class);
				GremlinPipeline<Vertex, Long> mPipe = new GremlinPipeline<Vertex, Long>();
				long noOfMembers = mPipe.start(rV).inE(EdgeLabel.members.toString()).has(END, Long.MAX_VALUE).count();
				gR.setNoOfMembers(noOfMembers);
				ls.add(gR);
			}
			
			commit(g);
			
			return getRefsets(ls);

			
			
		} catch(Exception e) {
			
			rollback(g);
			
			LOGGER.error("Error occurred while adding viewed event", e);
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


}