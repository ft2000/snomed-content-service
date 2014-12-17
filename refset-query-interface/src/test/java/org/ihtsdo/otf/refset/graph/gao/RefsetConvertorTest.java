/**
 * 
 */
package org.ihtsdo.otf.refset.graph.gao;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.UUID;

import org.ihtsdo.otf.refset.domain.Member;
import org.ihtsdo.otf.refset.domain.Refset;
import org.ihtsdo.otf.refset.graph.schema.GMember;
import org.ihtsdo.otf.refset.graph.schema.GRefset;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
/**
 * @author Episteme Partners
 *
 */
public class RefsetConvertorTest {
	
	private static final String COMP_TYPE_ID = "junitComponentId";

	private static final String ID = "junitId";

	private static final String CREATED_BY = "junit";

	private static final String DESCRIPTION = "junit test";

	private static final String LANG = "en_US";

	private static final String MODULE_ID = "junitModuleId";
	
	private Refset r;
	
	private Member m;
	
	private GRefset gR;

	Iterable<GMember> members;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		
		r = new Refset();
		m = new Member();
		r.setUuid(UUID.randomUUID().toString());
		m.setUuid(UUID.randomUUID().toString());

		gR = mock(GRefset.class);
		
		when(gR.getActive()).thenReturn(1);
		when(gR.getComponentTypeId()).thenReturn(COMP_TYPE_ID);
		when(gR.getId()).thenReturn(ID);
		when(gR.getCreated()).thenReturn(System.currentTimeMillis());
		when(gR.getCreatedBy()).thenReturn(CREATED_BY);
		when(gR.getDescription()).thenReturn(DESCRIPTION);
		when(gR.getEffectiveTime()).thenReturn(System.currentTimeMillis());
		when(gR.getLanguageCode()).thenReturn(LANG);
		
		Iterable<GMember> members = mock(Iterable.class);

		when(gR.getMembers()).thenReturn(members);
		
		when(gR.getModuleId()).thenReturn(MODULE_ID);
		when(gR.getPublished()).thenReturn(1);
		
		Vertex v = mock(Vertex.class);
		
		when(gR.asVertex()).thenReturn(v);
		
		Iterable<Edge> edge = mock(Iterable.class);
		when(v.getEdges(any(Direction.class), anyString())).thenReturn(edge);
		
		
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		
	}
	
	
	@Test
	public void testConvert2Refset() {
		
		Refset r = RefsetConvertor.convert2Refset(gR);
		
		assertEquals(COMP_TYPE_ID, r.getComponentTypeId());
		
		assertEquals(LANG, r.getLanguageCode());
		assertEquals(CREATED_BY, r.getCreatedBy());
		assertEquals(DESCRIPTION, r.getDescription());
		assertEquals(MODULE_ID, r.getModuleId());
		assertEquals(ID, r.getUuid());
		assertNotNull(r.getCreated());
		assertNotNull(r.getEffectiveTime());
	}


}