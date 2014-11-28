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
import static org.ihtsdo.otf.refset.domain.RGC.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.ihtsdo.otf.refset.graph.RefsetGraphFactory;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thinkaurelius.titan.core.TitanGraph;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.util.wrappers.event.listener.GraphChangedListener;

/**
 *Listener to Member node events capture and create corresponding state nodes
 */
public class MemberChangeListener implements GraphChangedListener {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MemberChangeListener.class);
	
	//these will be the ids of  history member vertices per transaction where key is current vertex id 
	//and value is corresponding history vertex id 
	private Map<Object, Object> historyVertexIds = new ConcurrentHashMap<Object, Object>();

	//these will be the ids of  history member edge per transaction where key is history vertex id 
	//and value is corresponding edge id 
	private Map<Object, Object> historyEdgeIds = new ConcurrentHashMap<Object, Object>();
	private final TitanGraph g;
	private final String user;
	

    public MemberChangeListener(final TitanGraph g, final String user) {
    	
        this.g = g;
        this.user = user;

    }
	
	/* (non-Javadoc)
	 * @see com.tinkerpop.blueprints.util.wrappers.event.listener.GraphChangedListener#vertexAdded(com.tinkerpop.blueprints.Vertex)
	 */
	@Override
	public void vertexAdded(Vertex vertex) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.tinkerpop.blueprints.util.wrappers.event.listener.GraphChangedListener#vertexPropertyChanged(com.tinkerpop.blueprints.Vertex, java.lang.String, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void vertexPropertyChanged(Vertex cV, String key,
			Object oldValue, Object setValue) {
		

        Object type = cV.getProperty(TYPE);

        if (!VertexType.member.toString().equals(type)) {
			
        	return;
		}

        try {
			
        	if (!(oldValue != null && oldValue.equals(setValue) 
            		|| setValue != null && setValue.equals(oldValue)
            		|| setValue == oldValue)) {
				
        		createHistoryMemberNode(cV.getId());
                
                LOGGER.debug("Adding old {} value - {}  to history refset vertex {}", key, oldValue);

        		Vertex hV = g.getVertex(historyVertexIds.get(cV.getId()));
        		hV.setProperty(key, oldValue);
        		//add relation if does not exist already
        		addStateRelation(hV, cV);
        		RefsetGraphFactory.commit(g);
        		
			}
        	
        } catch (Exception e) {
			
            LOGGER.debug("Error during change state capture", e);

			RefsetGraphFactory.rollback(g);
			
		}
        
		

	}

	/**
	 * Adds history vertex up front so that any change in current refset can be added to this history refset.
     *  one per transaction there will be only on history refset. hence few meta property should be added upfront.
	 *
	 */
	private void createHistoryMemberNode(Object currentVertexId) {

		if (!historyVertexIds.containsKey(currentVertexId)) {

			LOGGER.debug("Adding history vertex");
            
            final Vertex v = g.addVertexWithLabel(g.getVertexLabel("GMember"));
            v.setProperty(TYPE, VertexType.hMember.toString());
            v.setProperty(CREATED, new DateTime().getMillis());
            v.setProperty(CREATED_BY, user);
            historyVertexIds.put(currentVertexId, v.getId());
            
            LOGGER.debug("History vertex with an id - {} was added by {}", v.getId(), user);

		} else {
		
            LOGGER.debug("Not adding history vertex as it already exist with an id - {}", historyVertexIds.get(currentVertexId), user);

		}
	}

	/* (non-Javadoc)
	 * @see com.tinkerpop.blueprints.util.wrappers.event.listener.GraphChangedListener#vertexPropertyRemoved(com.tinkerpop.blueprints.Vertex, java.lang.String, java.lang.Object)
	 */
	@Override
	public void vertexPropertyRemoved(Vertex cV, String key,
			Object removedValue) {


		Object type = cV.getProperty(TYPE);

        if (!VertexType.member.toString().equals(type)) {
			
        	return;
		}
        try {
			
        	createHistoryMemberNode(cV.getId());

    		LOGGER.debug("Adding removed {} value - {}  to history refset vertex {}", key, removedValue);
    		
    		Vertex hV = g.getVertex(historyVertexIds.get(cV.getId()));
    		hV.setProperty(key, removedValue);
    		
    		//add relation if does not exist already
    		addStateRelation(hV, cV);
    		
    		RefsetGraphFactory.commit(g);
    		
		} catch (Exception e) {
			
            LOGGER.debug("Error during change state capture", e);

			RefsetGraphFactory.rollback(g);
			
		}
        

	}

	/**
	 * @param hV
	 * @param cV
	 */
	private void addStateRelation(Vertex hV, Vertex cV) {

		if (!historyEdgeIds.containsKey(hV.getId())) {
			
			//also update sid if available to history refset.
			hV.setProperty(ID, cV.getProperty(ID));
			Edge e = cV.addEdge(EdgeLabel.hasState.toString(), hV);
			historyEdgeIds.put(hV.getId(), e.getId());
			Iterable<Edge> mES = cV.getEdges(Direction.OUT, EdgeLabel.members.toString());
			for (Edge edge : mES) {
				
	            LOGGER.debug("Adding Start date {}", edge.getProperty(START));
				
				e.setProperty(START, edge.getProperty(START));
				e.setProperty(END, new DateTime().getMillis());
				break;
			}
            LOGGER.debug("State edge with an id - {} was added by {}", e.getId(), user);

		} else {
		
            LOGGER.debug("Not adding state edge as it already exist with an id - {}", historyEdgeIds.get(hV.getId()), user);

		}

	}

	/* (non-Javadoc)
	 * @see com.tinkerpop.blueprints.util.wrappers.event.listener.GraphChangedListener#vertexRemoved(com.tinkerpop.blueprints.Vertex, java.util.Map)
	 */
	@Override
	public void vertexRemoved(Vertex vertex, Map<String, Object> props) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.tinkerpop.blueprints.util.wrappers.event.listener.GraphChangedListener#edgeAdded(com.tinkerpop.blueprints.Edge)
	 */
	@Override
	public void edgeAdded(Edge edge) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.tinkerpop.blueprints.util.wrappers.event.listener.GraphChangedListener#edgePropertyChanged(com.tinkerpop.blueprints.Edge, java.lang.String, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void edgePropertyChanged(Edge edge, String key, Object oldValue,
			Object setValue) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.tinkerpop.blueprints.util.wrappers.event.listener.GraphChangedListener#edgePropertyRemoved(com.tinkerpop.blueprints.Edge, java.lang.String, java.lang.Object)
	 */
	@Override
	public void edgePropertyRemoved(Edge edge, String key, Object removedValue) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.tinkerpop.blueprints.util.wrappers.event.listener.GraphChangedListener#edgeRemoved(com.tinkerpop.blueprints.Edge, java.util.Map)
	 */
	@Override
	public void edgeRemoved(Edge edge, Map<String, Object> props) {
		// TODO Auto-generated method stub

	}

}
