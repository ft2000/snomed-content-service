/**
* Copyright 2014 IHTSDO
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.ihtsdo.otf.refset.graph.gao;

import static org.ihtsdo.otf.refset.domain.RGC.END;
import static org.ihtsdo.otf.refset.domain.RGC.ID;
import static org.ihtsdo.otf.refset.domain.RGC.REFERENCE_COMPONENT_ID;
import static org.ihtsdo.otf.refset.domain.RGC.TYPE;
import static org.ihtsdo.otf.refset.domain.RGC.START;
import static org.ihtsdo.otf.refset.domain.RGC.ACTIVE;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.ihtsdo.otf.refset.domain.ChangeRecord;
import org.ihtsdo.otf.refset.domain.MemberDTO;
import org.ihtsdo.otf.refset.domain.RefsetDTO;
import org.ihtsdo.otf.refset.exception.EntityNotFoundException;
import org.ihtsdo.otf.refset.graph.RefsetGraphAccessException;
import org.ihtsdo.otf.refset.graph.RefsetGraphFactory;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.thinkaurelius.titan.core.TitanGraph;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.gremlin.Tokens.T;
import com.tinkerpop.gremlin.java.GremlinPipeline;

/**class address history retrieval of Members or Refset 
 *
 */
@Repository
public class HistoryGao {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(HistoryGao.class);
	private RefsetGraphFactory f;

	/**
	 * @param factory the factory to set
	 */
	@Resource(name = "refsetGraphFactory")
	public  void setFactory(RefsetGraphFactory factory) {
		
		this.f = factory;
	}

	/**
	 * @param refsetId
	 * @param from
	 * @param to
	 * @return
	 * @throws RefsetGraphAccessException
	 */
	public Map<String, ChangeRecord<MemberDTO>> getAllMembersHistory(String refsetId,
			DateTime fromDate, DateTime toDate, Integer from, Integer to) throws RefsetGraphAccessException {

		LOGGER.debug("Getting all member history for refset id {}", refsetId);

		Map<String, ChangeRecord<MemberDTO>> history = new HashMap<String, ChangeRecord<MemberDTO>>();

		TitanGraph g = null;
		
		try {
			
			g = f.getReadOnlyGraph();

			Iterable<Vertex> vRs = g.query().has(TYPE, VertexType.refset.toString()).has(ID, refsetId).limit(1).vertices();

			
			if (!vRs.iterator().hasNext()) {
				
				throw new EntityNotFoundException("Refset does not exist for given refset id " + refsetId);
			} 
			
			Vertex vR = vRs.iterator().next();
			
			GremlinPipeline<Vertex, Edge> fPipe = new GremlinPipeline<Vertex, Edge>();			
			fPipe.start(vR).inE(EdgeLabel.members.toString()).range(from, to);
			List<Edge> fls = fPipe.toList();
			for (Edge e : fls) {
				
				Vertex v = e.getVertex(Direction.OUT);
				GremlinPipeline<Vertex, Edge> mhPipe = new GremlinPipeline<Vertex, Edge>();			
				mhPipe.start(v).outE(EdgeLabel.hasState.toString())
				.has(END, T.lte, toDate.getMillis())
				.has(START, T.gte, fromDate.getMillis())
				.range(from, to);
				List<Edge> mhls = mhPipe.toList();
				
				List<MemberDTO> ms = RefsetConvertor.getHistoryMembers(mhls);
				ChangeRecord<MemberDTO> cr = new ChangeRecord<MemberDTO>();
				String rcId = e.getProperty(ID);
				cr.setRecords(ms);
				history.put(rcId, cr);

				
			}
			
		} catch (Exception e) {
			
			RefsetGraphFactory.rollback(g);			
			LOGGER.error("Error getting refsets member history", e);
			throw new RefsetGraphAccessException(e.getMessage(), e);
			
		} finally {
			
			RefsetGraphFactory.shutdown(g);
		}

		return history;
	}
	
	/**
	 * @param refsetId
	 * @param referenceComponentId
	 * @param from
	 * @param to
	 * @return
	 * @throws RefsetGraphAccessException
	 */
	public ChangeRecord<MemberDTO> getMemberHistory(String refsetId, String id, 
			DateTime fromDate, DateTime toDate, Integer from, Integer to) throws RefsetGraphAccessException {
		
		Object[] criteria = {id, fromDate, toDate, from, to};
		
		LOGGER.debug("Getting member history for refset id {} and criteria {}", refsetId, criteria);

		ChangeRecord<MemberDTO> history = new ChangeRecord<MemberDTO>();
		
		TitanGraph g = null;
		
		try {
			
			g = f.getReadOnlyGraph();

			Iterable<Vertex> vRs = g.query().has(TYPE, VertexType.refset.toString()).has(ID, refsetId).limit(1).vertices();

			
			if (!vRs.iterator().hasNext()) {
				
				throw new EntityNotFoundException("Refset does not exist for given refset id " + refsetId);
			} 
			
			Vertex vR = vRs.iterator().next();
			
			//get required members as per range
			
			
			GremlinPipeline<Vertex, Edge> fPipe = new GremlinPipeline<Vertex, Edge>();			
			fPipe.start(vR).inE(EdgeLabel.members.toString()).outV()
				.has(ID, T.eq, id).outE(EdgeLabel.hasState.toString())
				.has(END, T.lte, toDate.getMillis())
				.has(START, T.gte, fromDate.getMillis())
				.range(from, to);
			
			List<Edge> fls = fPipe.toList();

			List<MemberDTO> ms = RefsetConvertor.getHistoryMembers(fls);
			history.setRecords(ms);

		} catch (Exception e) {
			
			RefsetGraphFactory.rollback(g);			
			LOGGER.error("Error getting refsets history", e);
			throw new RefsetGraphAccessException(e.getMessage(), e);
			
		} finally {
			
			RefsetGraphFactory.shutdown(g);
		}
	
		
		LOGGER.debug("Returning {} ", history);

	
		return history;
	}
	
	/**
	 * @param refsetId
	 * @param fromDate
	 * @param toDate
	 * @return
	 * @throws RefsetGraphAccessException
	 */
	public ChangeRecord<RefsetDTO> getRefsetHeaderHistory(String refsetId,  DateTime fromDate, DateTime toDate, Integer from, Integer to) throws RefsetGraphAccessException {

		Object[] criteria = {fromDate, toDate, from, to};
		
		LOGGER.debug("Getting refset history for refset id {}, and criteria {}", refsetId, criteria);

		ChangeRecord<RefsetDTO> history = new ChangeRecord<RefsetDTO>();

		TitanGraph g = null;
		
		try {
			
			g = f.getReadOnlyGraph();
			
			Iterable<Vertex> vRs = g.query().has(TYPE, VertexType.refset.toString()).has(ID, refsetId).limit(1).vertices();

			
			if (!vRs.iterator().hasNext()) {
				
				throw new EntityNotFoundException("Refset does not exist for given refset id " + refsetId);
			} 
			
			Vertex vR = vRs.iterator().next();
			//get required members as per range
			GremlinPipeline<Vertex, Edge> rPipe = new GremlinPipeline<Vertex, Edge>();			
			rPipe.start(vR).outE(EdgeLabel.hasState.toString())
				.has(END, T.lte, toDate.getMillis())
				.has(START, T.gte, fromDate.getMillis())
				.range(from, to);
			
			List<Edge> ls = rPipe.toList();
			List<RefsetDTO> rs = RefsetConvertor.getHistoryRefsets(ls);

			history.setRecords(rs);

		} catch (Exception e) {
			
			RefsetGraphFactory.rollback(g);			
			LOGGER.error("Error getting refsets member history", e);
			throw new RefsetGraphAccessException(e.getMessage(), e);
			
		} finally {
			
			RefsetGraphFactory.shutdown(g);
		}

		return history;
	}

	/**
	 * @param refsetId
	 * @param fromDate
	 * @param toDate
	 * @param from
	 * @param to
	 * @return
	 */
	public Map<String, ChangeRecord<MemberDTO>> getAllMembersStateHistory(
			String refsetId, DateTime fromDate, DateTime toDate, int from,
			int to) throws RefsetGraphAccessException {

		LOGGER.debug("Getting all member state history for refset id {}", refsetId);

		Map<String, ChangeRecord<MemberDTO>> history = new HashMap<String, ChangeRecord<MemberDTO>>();

		TitanGraph g = null;
		
		try {
			
			g = f.getReadOnlyGraph();

			Iterable<Vertex> vRs = g.query().has(TYPE, VertexType.refset.toString()).has(ID, refsetId).limit(1).vertices();

			
			if (!vRs.iterator().hasNext()) {
				
				throw new EntityNotFoundException("Refset does not exist for given refset id " + refsetId);
			} 
			
			Vertex vR = vRs.iterator().next();
			
			GremlinPipeline<Vertex, Edge> fPipe = new GremlinPipeline<Vertex, Edge>();			
			fPipe.start(vR).inE(EdgeLabel.members.toString()).range(from, to);
			List<Edge> fls = fPipe.toList();
			for (Edge e : fls) {
				
				Vertex v = e.getVertex(Direction.OUT);
				GremlinPipeline<Vertex, Edge> mhPipe = new GremlinPipeline<Vertex, Edge>();			
				mhPipe.start(v).outE(EdgeLabel.hasState.toString())
				.has(END, T.lte, toDate.getMillis())
				.has(START, T.gte, fromDate.getMillis())
				.inV().has(ACTIVE).has(TYPE, VertexType.hMember.toString())
				.range(from, to);
				List<Edge> mhls = mhPipe.toList();
				
				List<MemberDTO> ms = RefsetConvertor.getHistoryMembers(mhls);
				ChangeRecord<MemberDTO> cr = new ChangeRecord<MemberDTO>();
				String rcId = e.getProperty(ID);
				cr.setRecords(ms);
				history.put(rcId, cr);

				
			}
			
		} catch (Exception e) {
			
			RefsetGraphFactory.rollback(g);			
			LOGGER.error("Error getting refsets member history", e);
			throw new RefsetGraphAccessException(e.getMessage(), e);
			
		} finally {
			
			RefsetGraphFactory.shutdown(g);
		}

		return history;

	}

	/**
	 * @param refsetId
	 * @param fromDate
	 * @param toDate
	 * @param from
	 * @param to
	 * @return
	 */
	public ChangeRecord<RefsetDTO> getRefsetHeaderStateHistory(String refsetId,
			DateTime fromDate, DateTime toDate, int from, int to) throws RefsetGraphAccessException {
		
		Object[] criteria = {fromDate, toDate, from, to};
		
		LOGGER.debug("Getting refset history for refset id {}, and criteria {}", refsetId, criteria);

		ChangeRecord<RefsetDTO> history = new ChangeRecord<RefsetDTO>();

		TitanGraph g = null;
		
		try {
			
			g = f.getReadOnlyGraph();
			
			Iterable<Vertex> vRs = g.query().has(TYPE, VertexType.refset.toString()).has(ID, refsetId).limit(1).vertices();

			
			if (!vRs.iterator().hasNext()) {
				
				throw new EntityNotFoundException("Refset does not exist for given refset id " + refsetId);
			} 
			
			Vertex vR = vRs.iterator().next();
			//get required members as per range
			GremlinPipeline<Vertex, Vertex> rPipe = new GremlinPipeline<Vertex, Vertex>();			
			rPipe.start(vR).outE(EdgeLabel.hasState.toString())
				.has(END, T.lte, toDate.getMillis())
				.has(START, T.gte, fromDate.getMillis()).inV().has(ACTIVE).has(TYPE, VertexType.hMember.toString())
				.range(from, to);
			
			List<Vertex> ls = rPipe.toList();
			List<RefsetDTO> rs = RefsetConvertor.getStateRefsets(ls);

			history.setRecords(rs);

		} catch (Exception e) {
			
			RefsetGraphFactory.rollback(g);			
			LOGGER.error("Error getting refsets member history", e);
			throw new RefsetGraphAccessException(e.getMessage(), e);
			
		} finally {
			
			RefsetGraphFactory.shutdown(g);
		}
		
		return history;

	}

	/**
	 * @param refsetId
	 * @param memberId
	 * @param fromDate
	 * @param toDate
	 * @param from
	 * @param to
	 * @return
	 */
	public ChangeRecord<MemberDTO> getMemberStateHistory(String refsetId,
			String id, DateTime fromDate, DateTime toDate, int from,
			int to) throws RefsetGraphAccessException {
		
		Object[] criteria = {id, fromDate, toDate, from, to};
		
		LOGGER.debug("Getting member history for refset id {} and criteria {}", refsetId, criteria);

		ChangeRecord<MemberDTO> history = new ChangeRecord<MemberDTO>();
		
		TitanGraph g = null;
		
		try {
			
			g = f.getReadOnlyGraph();

			Iterable<Vertex> vRs = g.query().has(TYPE, VertexType.refset.toString()).has(ID, refsetId).limit(1).vertices();

			
			if (!vRs.iterator().hasNext()) {
				
				throw new EntityNotFoundException("Refset does not exist for given refset id " + refsetId);
			} 
			
			Vertex vR = vRs.iterator().next();
			
			//get required members as per range
			
			
			GremlinPipeline<Vertex, Vertex> fPipe = new GremlinPipeline<Vertex, Vertex>();			
			fPipe.start(vR).inE(EdgeLabel.members.toString()).outV()
				.has(ID, T.eq, id).outE(EdgeLabel.hasState.toString())
				.has(END, T.lte, toDate.getMillis())
				.has(START, T.gte, fromDate.getMillis())
				.inV().has(ACTIVE).has(TYPE, VertexType.hMember.toString())
				.range(from, to);
			
			List<Vertex> fls = fPipe.toList();

			List<MemberDTO> ms = RefsetConvertor.getStateMembers(fls);
			history.setRecords(ms);

		} catch (Exception e) {
			
			RefsetGraphFactory.rollback(g);			
			LOGGER.error("Error getting member state history", e);
			throw new RefsetGraphAccessException(e.getMessage(), e);
			
		} finally {
			
			RefsetGraphFactory.shutdown(g);
		}
	
		
		LOGGER.debug("Returning {} ", history);

	
		return history;

	}
	
	/**
	 * @param memberId
	 * @return {@link ChangeRecord}
	 */
	public ChangeRecord<MemberDTO> getMemberStateHistory(String memberId, String refsetId) throws RefsetGraphAccessException, EntityNotFoundException {
		
		Object[] criteria = {memberId, refsetId};
		
		LOGGER.debug("Getting member history for criteria {}", criteria);

		ChangeRecord<MemberDTO> history = new ChangeRecord<MemberDTO>();
		
		TitanGraph g = null;
		
		try {
			
			g = f.getReadOnlyGraph();			
			Iterable<Vertex> vRs = g.query().has(TYPE, VertexType.refset.toString()).has(ID, refsetId).limit(1).vertices();

			
			if (!vRs.iterator().hasNext()) {
				
				throw new EntityNotFoundException("Refset does not exist for given refset id " + refsetId);
			} 
			
			Vertex vR = vRs.iterator().next();
			
			GremlinPipeline<Vertex, Vertex> pipe = new GremlinPipeline<Vertex, Vertex>(g);
			
			pipe.start(vR).inE(EdgeLabel.members.toString()).outV()
				.has(ID, memberId)
				.has(TYPE, VertexType.member.toString());
			
			List<Vertex> vMs = pipe.toList();
			
			for (Vertex vM : vMs) {
				
				MemberDTO currentMember = RefsetConvertor.getMember(vM);
				
				Iterable<Edge> edges = vM.getEdges(Direction.OUT, EdgeLabel.members.toString());
				for (Edge edge : edges) {
					
					Set<String> eKeys = edge.getPropertyKeys();
					if ( eKeys.contains(REFERENCE_COMPONENT_ID) ) {
						
						String referenceComponentId = edge.getProperty(REFERENCE_COMPONENT_ID);
						currentMember.setReferencedComponentId(referenceComponentId);
						
					}
				}
				 

				GremlinPipeline<Vertex, Vertex> fPipe = new GremlinPipeline<Vertex, Vertex>();			
				fPipe.start(vM).outE(EdgeLabel.hasState.toString())
					.inV().has(ACTIVE).has(TYPE, VertexType.hMember.toString());
				
				List<Vertex> fls = fPipe.toList();

				List<MemberDTO> ms = RefsetConvertor.getStateMembers(fls);
				
				//we need to get missing data from existing member detail. TODO create full details during state creation
				for (MemberDTO m : ms) {
					
					if (StringUtils.isEmpty(m.getModuleId())) {
						
						m.setModuleId(currentMember.getModuleId());
					}
					
					if (StringUtils.isEmpty(m.getReferencedComponentId())) {
						
						m.setReferencedComponentId(currentMember.getReferencedComponentId());
					}
									
				}
				
				history.setRecords(ms);
			}
			

		} catch (EntityNotFoundException e) { 
		
			RefsetGraphFactory.rollback(g);			
			LOGGER.error("Error getting member state history", e);

			throw e;
			
		} catch (Exception e) {
			
			RefsetGraphFactory.rollback(g);			
			LOGGER.error("Error getting member state history", e);
			throw new RefsetGraphAccessException(e.getMessage(), e);
			
		} finally {
			
			RefsetGraphFactory.shutdown(g);
		}
	
		
		LOGGER.debug("Returning {} ", history);

	
		return history;

	}

}