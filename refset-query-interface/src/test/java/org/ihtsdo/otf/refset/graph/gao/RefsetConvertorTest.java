/**
 * 
 */
package org.ihtsdo.otf.refset.graph.gao;

import static org.junit.Assert.*;

import java.util.Map;
import java.util.UUID;

import org.ihtsdo.otf.refset.domain.Member;
import org.ihtsdo.otf.refset.domain.Refset;
import org.ihtsdo.otf.refset.graph.gao.RefsetConvertor;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Episteme Partners
 *
 */
public class RefsetConvertorTest {
	
	
	private Refset r;
	
	private Member m;
	

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		
		r = new Refset();
		m = new Member();
		r.setId(UUID.randomUUID().toString());
		m.setId(UUID.randomUUID().toString());

	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link org.ihtsdo.otf.refset.graph.gao.RefsetConvertor#getRefsetProperties(org.ihtsdo.otf.refset.domain.Refset)}.
	 */
	@Test
	public void testGetRefsetPropertiesOnlyId() {
		
		Map<String, Object> props = RefsetConvertor.getRefsetProperties(r);
		
		assertNotNull(props);
		assertEquals(2, props.size());


	}
	
	/**
	 * Test method for {@link org.ihtsdo.otf.refset.graph.gao.RefsetConvertor#getRefsetProperties(org.ihtsdo.otf.refset.domain.Refset)}.
	 */
	@Test
	public void testGetRefsetPropertiesWithCreated() {
		r = new Refset();
		r.setId(UUID.randomUUID().toString());
		r.setCreated(new DateTime());
		Map<String, Object> props = RefsetConvertor.getRefsetProperties(r);
		
		assertNotNull(props);
		assertEquals(3, props.size());

	}
	
	/**
	 * Test method for {@link org.ihtsdo.otf.refset.graph.gao.RefsetConvertor#getRefsetProperties(org.ihtsdo.otf.refset.domain.Refset)}.
	 */
	@Test
	public void testGetRefsetPropertiesWithEffectiveDate() {
		r = new Refset();
		r.setId(UUID.randomUUID().toString());
		r.setEffectiveTime(new DateTime());
		Map<String, Object> props = RefsetConvertor.getRefsetProperties(r);
		
		assertNotNull(props);
		assertEquals(3, props.size());

	}
	
	/**
	 * Test method for {@link org.ihtsdo.otf.refset.graph.gao.RefsetConvertor#getRefsetProperties(org.ihtsdo.otf.refset.domain.Refset)}.
	 */
	@Test
	public void testGetRefsetPropertiesWithDescription() {
		r = new Refset();
		r.setId(UUID.randomUUID().toString());
		r.setEffectiveTime(new DateTime());
		r.setDescription("Hi there");
		Map<String, Object> props = RefsetConvertor.getRefsetProperties(r);
		
		assertNotNull(props);
		assertEquals(4, props.size());
		
	}
	
	/**
	 * Test method for {@link org.ihtsdo.otf.refset.graph.gao.RefsetConvertor#getMemberProperties(org.ihtsdo.otf.refset.domain.Member)}.
	 */
	@Test
	public void testGetMemberProperties() {
		
		Map<String, Object> props = RefsetConvertor.getMemberProperties(m);
		
		assertNotNull(props);
		assertEquals(2, props.size());

	}

	/**
	 * Test method for {@link org.ihtsdo.otf.refset.graph.gao.RefsetConvertor#getMemberProperties(org.ihtsdo.otf.refset.domain.Member)}.
	 */
	@Test
	public void testGetMemberPropertiesWithEffectiveDate() {
		
		m = new Member();
		m.setId(UUID.randomUUID().toString());
		m.setEffectiveTime(new DateTime());
		Map<String, Object> props = RefsetConvertor.getMemberProperties(m);
		
		assertNotNull(props);
		assertEquals(3, props.size());

	}

}