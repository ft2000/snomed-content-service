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

import org.ihtsdo.otf.refset.domain.Member;
import org.ihtsdo.otf.refset.domain.Refset;
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
 *Effective time change listener to keep track of {@link Member} effective so that
 *a desired value can be assigned to {@link Refset#setEarliestEffectiveTime() and Refset#setLatestEffectiveTime()}
 */
public class EffectiveTimeChangeListener implements GraphChangedListener {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(EffectiveTimeChangeListener.class);
	
	//this will be the id of a history refset vertex
	private final TitanGraph g;
	private final String user;
	

    public EffectiveTimeChangeListener(final TitanGraph g, final String user) {
    	
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
			
        	if (EFFECTIVE_DATE.equalsIgnoreCase(key)) {
    			
                LOGGER.debug("Adding  effective time to refset {}", setValue);
                //check current latest effective date & earliest effective date with this new value and update accordingly 
                Iterable<Vertex> rVs = cV.getVertices(Direction.OUT, EdgeLabel.members.toString());
                boolean isEffetiveTimeUpdated = false;
                
                for (Vertex rV : rVs) {
					
                	//update earliest effective time
                	if(rV.getPropertyKeys().contains(E_EFFECTIVE_TIME)) {
                		
                		Long cEEffT = rV.getProperty(E_EFFECTIVE_TIME);
                		
                		if(cEEffT != null && cEEffT > (Long)setValue) {
                			
                    		rV.setProperty(E_EFFECTIVE_TIME, (Long)setValue);
                    		isEffetiveTimeUpdated = true;
                		} 
                	} else if (setValue != null) {
                		
            			rV.setProperty(E_EFFECTIVE_TIME, setValue);
            			isEffetiveTimeUpdated = true;
            		}
                	
                	//update latest effective time
                	if(rV.getPropertyKeys().contains(L_EFFECTIVE_TIME)) {
                		
                		Long cLEffT = rV.getProperty(L_EFFECTIVE_TIME);
                		
                		if(cLEffT != null && cLEffT < (Long)setValue) {
                			
                    		rV.setProperty(L_EFFECTIVE_TIME, (Long)setValue);
                    		isEffetiveTimeUpdated = true;
                		} 
                		
                	} else if(setValue != null) {
            			
            			rV.setProperty(L_EFFECTIVE_TIME, (Long)setValue);
            			isEffetiveTimeUpdated = true;
            		}
                	
                	if (isEffetiveTimeUpdated) {
						
                		rV.setProperty(MODIFIED_BY, user);
                		rV.setProperty(MODIFIED_DATE, new DateTime().getMillis());

					}
				}
                RefsetGraphFactory.commit(g);
    		}
        	
		} catch (Exception e) {
			
            LOGGER.debug("Error during change state capture", e);

			RefsetGraphFactory.rollback(g);
			
		}
        
		

	}

	/* (non-Javadoc)
	 * @see com.tinkerpop.blueprints.util.wrappers.event.listener.GraphChangedListener#vertexPropertyRemoved(com.tinkerpop.blueprints.Vertex, java.lang.String, java.lang.Object)
	 */
	@Override
	public void vertexPropertyRemoved(Vertex cV, String key,
			Object removedValue) {
		
		//do not do anything

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
