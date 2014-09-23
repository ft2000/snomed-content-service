/**
 * 
 */
package org.ihtsdo.otf.refset.graph.gao;

import java.util.ArrayList;
import java.util.List;

import org.ihtsdo.otf.refset.domain.MetaData;
import org.ihtsdo.otf.refset.domain.Refset;
import org.ihtsdo.otf.refset.exception.EntityNotFoundException;
import org.ihtsdo.otf.refset.graph.RGC;
import org.ihtsdo.otf.refset.graph.RefsetGraphAccessException;
import org.ihtsdo.otf.refset.graph.RefsetGraphFactory;
import org.ihtsdo.otf.refset.graph.schema.GRefset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.google.common.collect.Iterables;
import com.thinkaurelius.titan.core.TitanGraph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.FramedGraph;
import com.tinkerpop.frames.FramedGraphFactory;
import com.tinkerpop.frames.FramedTransactionalGraph;

/**Refset Graph Access component to retrieve refsets 
 * @author Episteme Partners
 *
 */
@Repository
public class RefsetGAO {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RefsetGAO.class);
		
	private RefsetGraphFactory factory;	


	private static FramedGraphFactory fgf = new FramedGraphFactory();


	


	/**Retrieves a {@link Refset} vertex for given {@link Refset#getId()}
	 * @param id
	 * @param tg 
	 * @return
	 * @throws RefsetGraphAccessException
	 * @throws EntityNotFoundException 
	 */
	protected Vertex getRefsetVertex(String id, FramedTransactionalGraph<TitanGraph> tg) throws RefsetGraphAccessException, EntityNotFoundException {
		
		LOGGER.debug("getRefsetVertex for given refset id {}", id);

		if (StringUtils.isEmpty(id)) {
			
			throw new EntityNotFoundException();
			
		}

		Vertex rV = null;
		
		try {
			
			//TODO upgrade this search with status and effective date
			
			Iterable<Vertex> vr = tg.getVertices(RGC.ID, id);
			
			if (vr != null ) {
				
				for (Vertex v : vr) {
					
					LOGGER.debug("Refset is {} for refset id {}", v, id);
					rV = v;
					break;
					
				}
			}
						
		} catch (Exception e) {
			
			LOGGER.error("Error refset lookup for refset id {}", id, e);
			throw new RefsetGraphAccessException(e.getMessage(), e);

		}
		
		if(rV == null) 
			throw new EntityNotFoundException("Refset does not exist for given refset id");
		
		return rV;

	}

	
	
	/**Retrieves a {@link Refset}  for a given refsetId
	 * @param id
	 * @return {@link Refset}
	 * @throws RefsetGraphAccessException
	 */
	public Refset getRefset(String id) throws RefsetGraphAccessException, EntityNotFoundException {
				
		TitanGraph g = null;
		Refset r = null;
		try {
			
			g = factory.getTitanGraph();

			FramedGraph<TitanGraph> tg = fgf.create(g);
			//TODO upgrade this search with status and effective date
			Iterable<GRefset> vs = tg.getVertices(RGC.ID, id, GRefset.class);//.has(RGC.ID, Compare.EQUAL, id).limit(1).vertices(GRefset.class);
			
			for (GRefset v : vs) {
				
				r = RefsetConvertor.convert2Refsets(v);
				LOGGER.debug("Refset is {} ", r);
				r.setMetaData(RefsetConvertor.getMetaData(v.asVertex()));
				break;
			}
			g.commit();
			
		} catch (Exception e) {
			
			RefsetGraphFactory.rollback(g);			
			LOGGER.error("Error getting refset for {}", id, e);
			throw new RefsetGraphAccessException(e.getMessage(), e);

		} finally {
			
			RefsetGraphFactory.shutdown(g);
		}
		if(r == null)
			throw new EntityNotFoundException("No Refset found for given id ");
		else 
			return r;

	}
	
	public List<Refset> getRefSets(boolean published) throws RefsetGraphAccessException {
		
		TitanGraph g = null;
		List<Refset> refsets = new ArrayList<Refset>();
		
		try {
			
			g = factory.getTitanGraph();
			FramedGraph<TitanGraph> fg = fgf.create(g);
			//TODO upgrade this search with status and effective date
			
			Iterable<GRefset> vs;
			
			if (published) {
				
				vs = fg.getVertices(RGC.PUBLISHED, true, GRefset.class);
								
			} else {

				//Iterable<Result<Vertex>> publishedRefset = g.indexQuery("CPublishedGRefset", "v.published:true").limit(10).offset(1).vertices();
				//Iterable<Result<Vertex>> unPublishedRefset = g.indexQuery("CPublishedGRefset", "v.published:false").limit(10).offset(1).vertices();

				Iterable<GRefset> publishedRefset = fg.getVertices(RGC.PUBLISHED, true, GRefset.class); //fg.query().has(RGC.PUBLISHED, Compare.EQUAL, true).limit(20).vertices(GRefset.class);//fg.getVertices(RGC.PUBLISHED, true, GRefset.class);//g.getRelationType(name); (GRefset.class, new Object[]{ published });
				Iterable<GRefset> unPublishedRefset = fg.getVertices(RGC.PUBLISHED, false, GRefset.class); //fg.query().has(RGC.PUBLISHED, Compare.EQUAL, false).limit(20).vertices(GRefset.class);// fg.getVertices(RGC.PUBLISHED, false, GRefset.class);//g.getRelationType(name); (GRefset.class, new Object[]{ published });
				
				vs = Iterables.concat(publishedRefset, unPublishedRefset);
				
			}
			/*List<GRefset> grs = new ArrayList<GRefset>();
			
			for (Result<Vertex> v : vs) {
				
				GRefset r = fg.frame(v.getElement(), GRefset.class);
				grs.add(r);
			}*/
			
			
			refsets = RefsetConvertor.getRefsetss(vs);
			
		} catch (Exception e) {
			
			RefsetGraphFactory.rollback(g);			
			LOGGER.error("Error during graph ineraction", e);
			throw new RefsetGraphAccessException(e.getMessage(), e);

		} finally {
			
			RefsetGraphFactory.shutdown(g);
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
		
		TitanGraph g = null;
		MetaData md = null;
		
		try {
			
			g = factory.getTitanGraph();

			Vertex e = g.getVertex(rId);
			if( e != null) {
				
				md = new MetaData();
				md.setId(e.getId());
				md.setType(e.getClass().getSimpleName());
				//Integer version = e.getProperty("@Version");
				//md.setVersion(version);
				
			}
			
			g.commit();
			
		} catch (Exception e) {
			
			RefsetGraphFactory.rollback(g);
			LOGGER.error("Error during graph ineraction ", e);
			throw new RefsetGraphAccessException(e.getMessage(), e);

		} finally {
			
			RefsetGraphFactory.shutdown(g);
			
		}
		
		if( md == null ) 
			throw new EntityNotFoundException(String.format("No record available for given record %s", rId));
		
		return md;
	}
	
	
	/**
	 * @param factory the factory to set
	 */
	@Autowired
	public  void setFactory(RefsetGraphFactory factory) {
		
		this.factory = factory;
	}

}