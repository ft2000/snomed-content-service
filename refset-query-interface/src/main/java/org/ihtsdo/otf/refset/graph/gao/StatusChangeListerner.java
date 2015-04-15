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

import java.util.Map;

import static org.ihtsdo.otf.refset.domain.RGC.*;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;

import com.thinkaurelius.titan.core.TitanGraph;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.util.ElementHelper;
import com.tinkerpop.blueprints.util.wrappers.event.listener.GraphChangedListener;

/**
 *Listener to capture refset state transition from unpublished to published to released
 */
public class StatusChangeListerner implements GraphChangedListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(StatusChangeListerner.class);
	
	private final TitanGraph g;
	private final String user;
	

    public StatusChangeListerner(final TitanGraph g, final String user) {
    	
        this.g = g;
        this.user = user;

    }
	/* (non-Javadoc)
	 * @see com.tinkerpop.blueprints.util.wrappers.event.listener.GraphChangedListener#edgeAdded(com.tinkerpop.blueprints.Edge)
	 */
	@Override
	public void edgeAdded(Edge arg0) {
		//nothing to do
		
	}

	/* (non-Javadoc)
	 * @see com.tinkerpop.blueprints.util.wrappers.event.listener.GraphChangedListener#edgePropertyChanged(com.tinkerpop.blueprints.Edge, java.lang.String, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void edgePropertyChanged(Edge arg0, String arg1, Object arg2,
			Object arg3) {

		//nothing to do
	}

	/* (non-Javadoc)
	 * @see com.tinkerpop.blueprints.util.wrappers.event.listener.GraphChangedListener#edgePropertyRemoved(com.tinkerpop.blueprints.Edge, java.lang.String, java.lang.Object)
	 */
	@Override
	public void edgePropertyRemoved(Edge arg0, String arg1, Object arg2) {
		//nothing to do
		
	}

	/* (non-Javadoc)
	 * @see com.tinkerpop.blueprints.util.wrappers.event.listener.GraphChangedListener#edgeRemoved(com.tinkerpop.blueprints.Edge, java.util.Map)
	 */
	@Override
	public void edgeRemoved(Edge arg0, Map<String, Object> arg1) {
		//nothing to do
		
	}

	/* (non-Javadoc)
	 * @see com.tinkerpop.blueprints.util.wrappers.event.listener.GraphChangedListener#vertexAdded(com.tinkerpop.blueprints.Vertex)
	 */
	@Override
	public void vertexAdded(Vertex arg0) {
		//nothing to do
		
	}

	/* (non-Javadoc)
	 * @see com.tinkerpop.blueprints.util.wrappers.event.listener.GraphChangedListener#vertexPropertyChanged(com.tinkerpop.blueprints.Vertex, java.lang.String, java.lang.Object, java.lang.Object)
	 */
	@Override
	@Async
	public void vertexPropertyChanged(Vertex currentVertex, String key, Object oldValue,
			Object setValue) {
		//1--->0
		LOGGER.debug("Property Change Listener Key : {},", key);
		LOGGER.debug("Property Change Listener Old Value : {}, New Value : {}", oldValue, setValue );
		if (REFSET_STATUS.equals(key) && oldValue != setValue) {
			
			//create a history version
            final Vertex vRh = g.addVertexWithLabel(g.getVertexLabel("GRefset"));
            vRh.setProperty(CREATED, new DateTime().getMillis());
            vRh.setProperty(CREATED_BY, user);
    		LOGGER.debug("Property Change Listener History Refset Vertex {}", vRh);

            ElementHelper.copyProperties(currentVertex, vRh);
            
            //mark type as history
            vRh.setProperty(TYPE, VertexType.hRefset.toString());

            Iterable<Edge> edges = currentVertex.getEdges(Direction.IN, EdgeLabel.members.toString());
            for (Edge edge : edges) {
				
        		Vertex vHm = g.addVertexWithLabel(g.getVertexLabel("GMember"));
        		LOGGER.debug("Property Change Listener History Member Vertex {}", vRh);

				Edge eH = g.addEdge(null, vHm, vRh, "members");
	            ElementHelper.copyProperties(edge, eH);
	            
	            //copy all member vertex properties to history
	            Vertex currentMember = edge.getVertex(Direction.OUT);
	            ElementHelper.copyProperties(currentMember, vHm);
	            vHm.setProperty(TYPE, VertexType.hMember.toString());

			}
            
            g.commit();
		}
		
		
	}

	/* (non-Javadoc)
	 * @see com.tinkerpop.blueprints.util.wrappers.event.listener.GraphChangedListener#vertexPropertyRemoved(com.tinkerpop.blueprints.Vertex, java.lang.String, java.lang.Object)
	 */
	@Override
	public void vertexPropertyRemoved(Vertex arg0, String arg1, Object arg2) {
		//nothing to do
		
	}

	/* (non-Javadoc)
	 * @see com.tinkerpop.blueprints.util.wrappers.event.listener.GraphChangedListener#vertexRemoved(com.tinkerpop.blueprints.Vertex, java.util.Map)
	 */
	@Override
	public void vertexRemoved(Vertex arg0, Map<String, Object> arg1) {
		//nothing to do
		
	}

}
