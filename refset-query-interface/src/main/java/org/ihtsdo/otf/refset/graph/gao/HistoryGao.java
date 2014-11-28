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
import static org.ihtsdo.otf.refset.domain.RGC.TYPE;
import static org.ihtsdo.otf.refset.domain.RGC.START;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.ihtsdo.otf.refset.domain.ChangeRecord;
import org.ihtsdo.otf.refset.domain.Member;
import org.ihtsdo.otf.refset.domain.Refset;
import org.ihtsdo.otf.refset.exception.EntityNotFoundException;
import org.ihtsdo.otf.refset.graph.RefsetGraphAccessException;
import org.ihtsdo.otf.refset.graph.RefsetGraphFactory;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

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
	public Map<String, ChangeRecord<Member>> getAllMembersHistory(String refsetId,
			DateTime fromDate, DateTime toDate, Integer from, Integer to) throws RefsetGraphAccessException {

		LOGGER.debug("Getting all member history for refset id {}", refsetId);

		Map<String, ChangeRecord<Member>> history = new HashMap<String, ChangeRecord<Member>>();

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
				
				List<Member> ms = RefsetConvertor.getHistoryMembers(mhls);
				ChangeRecord<Member> cr = new ChangeRecord<Member>();
				String rcId = e.getProperty(ID);
				cr.setRecord(ms);
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
	public ChangeRecord<Member> getMemberHistory(String refsetId, String id, 
			DateTime fromDate, DateTime toDate, Integer from, Integer to) throws RefsetGraphAccessException {
		
		Object[] criteria = {id, fromDate, toDate, from, to};
		
		LOGGER.debug("Getting member history for refset id {} and criteria {}", refsetId, criteria);

		ChangeRecord<Member> history = new ChangeRecord<Member>();
		
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

			List<Member> ms = RefsetConvertor.getHistoryMembers(fls);
			history.setRecord(ms);

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
	public ChangeRecord<Refset> getRefsetHeaderHistory(String refsetId,  DateTime fromDate, DateTime toDate, Integer from, Integer to) throws RefsetGraphAccessException {

		Object[] criteria = {fromDate, toDate, from, to};
		
		LOGGER.debug("Getting refset history for refset id {}, and criteria {}", refsetId, criteria);

		ChangeRecord<Refset> history = new ChangeRecord<Refset>();

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
			List<Refset> rs = RefsetConvertor.getHistoryRefsets(ls);

			history.setRecord(rs);

		} catch (Exception e) {
			
			RefsetGraphFactory.rollback(g);			
			LOGGER.error("Error getting refsets member history", e);
			throw new RefsetGraphAccessException(e.getMessage(), e);
			
		} finally {
			
			RefsetGraphFactory.shutdown(g);
		}

		return history;
	}

}
