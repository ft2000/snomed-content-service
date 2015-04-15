/**
 * 
 */
package org.ihtsdo.otf.refset.graph.gao;
import static org.ihtsdo.otf.refset.domain.RGC.EFFECTIVE_DATE;
import static org.ihtsdo.otf.refset.domain.RGC.ID;
import static org.ihtsdo.otf.refset.domain.RGC.PUBLISHED;
import static org.ihtsdo.otf.refset.domain.RGC.REFERENCE_COMPONENT_ID;
import static org.ihtsdo.otf.refset.domain.RGC.TYPE;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.ihtsdo.otf.refset.domain.MemberDTO;
import org.ihtsdo.otf.refset.domain.RefsetDTO;
import org.ihtsdo.otf.refset.exception.EntityNotFoundException;
import org.ihtsdo.otf.refset.graph.RefsetGraphAccessException;
import org.ihtsdo.otf.refset.graph.RefsetGraphFactory;
import org.ihtsdo.otf.refset.graph.schema.GRefset;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.thinkaurelius.titan.core.TitanGraph;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.FramedGraphFactory;
import com.tinkerpop.gremlin.java.GremlinPipeline;

/**Graph Access component to retrieve {@link Refset}s and its {@link Member} 
 * for export.
 */
@Repository
public class RefsetExportGAO {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RefsetExportGAO.class);
		
	private RefsetGraphFactory rgFactory;//refset graph factory	


	private static FramedGraphFactory fgf = new FramedGraphFactory();

	/**Retrieves a {@link Refset}  for a given refsetId
	 * @param id
	 * @return {@link Refset}
	 * @throws RefsetGraphAccessException
	 */
	public RefsetDTO getRefset(String id) throws RefsetGraphAccessException, EntityNotFoundException {
				
		LOGGER.debug("Geting member data for export for given refset id {} ", id);
		TitanGraph g = null;
		RefsetDTO r = null;
		try {
			
			g = rgFactory.getReadOnlyGraph();
			
			Iterable<Vertex> vRs = g.query().has(ID, id).has(TYPE, VertexType.refset.toString()).vertices();
			if(!vRs.iterator().hasNext()) {
				
				throw new EntityNotFoundException("No Refset available for given refset id");
			}
			
			Vertex vR = vRs.iterator().next();
			r = RefsetConvertor.getRefset(fgf.create(g).frame(vR, GRefset.class));
			r.setMembers(new ArrayList<MemberDTO>());
			/*export required all member which are not published yet*/
			GremlinPipeline<Vertex, Vertex> pipe = new GremlinPipeline<Vertex, Vertex>(g);
			
			pipe.start(vR).inE(EdgeLabel.members.toString()).outV()
				.has(PUBLISHED, 0)
				.has(TYPE, VertexType.member.toString());
			
			List<Vertex> vMs = pipe.toList();

			for (Vertex vM : vMs) {
				
				//check effective time. To be eligible for export Member effective time greater than > latest effective time or empty
				Object et = vM.getProperty(EFFECTIVE_DATE);
				
				if(!(et == null || new DateTime(et).isAfter(r.getLatestEffectiveTime()))) {
					
					LOGGER.debug("Not eligible for export due to effectivetime threshold : {} ", et, vM.getId());
					continue;
				}
					
				
				Iterable<Edge> edges = vM.getEdges(Direction.OUT, EdgeLabel.members.toString());
				for (Edge edge : edges) {
					
					if ( edge.getPropertyKeys().contains(REFERENCE_COMPONENT_ID) ) {
						
						MemberDTO m = RefsetConvertor.getMember(vM);

						String referenceComponentId = edge.getProperty(REFERENCE_COMPONENT_ID);
						m.setReferencedComponentId(referenceComponentId);
						
						LOGGER.debug("Adding current state of member & its detail {} ", m);
						r.getMembers().add(m);
						
						//check if this member also has a history state.
						GremlinPipeline<Vertex, Vertex> mPipe = new GremlinPipeline<Vertex, Vertex>(g);
						mPipe.start(vM).outE(EdgeLabel.hasState.toString()).inV().has(TYPE, VertexType.hMember.toString()).has(PUBLISHED, 1).range(0, 1);
						Iterable<Vertex> vHms = mPipe.toList();
						for (Vertex vHm : vHms) {
						
							MemberDTO hm = RefsetConvertor.getMember(vHm);
							MemberDTO merged = merge(hm, m);
							LOGGER.debug("Adding historical state of member & its detail {} ", merged);
							if (!r.getMembers().contains(merged)) {
								
								r.getMembers().add(merged);
								break;

							}

						}

					}

				}

			}
			
			RefsetGraphFactory.commit(g);
			
			
									
		} catch(EntityNotFoundException e) {
		
			RefsetGraphFactory.rollback(g);			

			LOGGER.error("entity not found for given refset id {}", id, e);

			throw e;
			
		} catch (Exception e) {
			
			RefsetGraphFactory.rollback(g);			
			LOGGER.error("Error getting refset for", id, e);
			throw new RefsetGraphAccessException(e.getMessage(), e);

		} finally {
			
			RefsetGraphFactory.shutdown(g);
		}
		
		return r;

	}

	
	/**
	 * @param hm
	 * @param m
	 */
	private MemberDTO merge(MemberDTO hm, MemberDTO m) {
		
		if(StringUtils.isEmpty(hm.getModuleId())) {
			
			hm.setModuleId(m.getModuleId());
		}
		
		if(StringUtils.isEmpty(hm.getReferencedComponentId())) {
			
			hm.setReferencedComponentId(m.getReferencedComponentId());
		}
		
		return hm;
		
	}


	/**
	 * @param factory the factory to set
	 */
	@Resource(name = "refsetGraphFactory")
	public  void setRGFactory(RefsetGraphFactory factory) {
		
		this.rgFactory = factory;
	}

}