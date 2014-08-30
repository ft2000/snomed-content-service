package org.ihtsdo.otf.refset.graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.ihtsdo.otf.refset.domain.MetaData;
import org.ihtsdo.otf.refset.domain.Refset;
import org.ihtsdo.otf.refset.exception.EntityNotFoundException;
import org.ihtsdo.otf.refset.exception.RefsetServiceException;
import org.ihtsdo.otf.refset.service.RefsetBrowseServiceStubData;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/spring/appServlet/servlet-context.xml", 
		"file:src/main/webapp/WEB-INF/spring/appServlet/spring-refset-browse-service-stub-data.xml",
		"file:src/main/webapp/WEB-INF/spring/appServlet/refset-graph-server-config.xml"})
@PropertySource(value = {"file:src/main/webapp/resources/refset-dev.properties"})
public class RefsetGAOTest {
	
	static {
		System.setProperty("env", "junit");
	}
	
	@Autowired
	private RefsetGAO gao;
	
	@Autowired
	private RefsetBrowseServiceStubData data;
	
	private List<Refset> rs;
	
	@Autowired
	private RefsetGraphFactory f;
	
	@Before
	public void setUp() throws Exception {

		rs = data.getRefSets();
		assertNotNull(rs);
		assertTrue(!rs.isEmpty());

		//setup refset db
				

	}
	
	@BeforeClass
	public static void setDatabase() {

		System.setProperty("ORIENTDB_HOME", "/tmp");

	}

	@After
	public void tearDown() throws Exception {
		
		f.getOrientGraph().drop();
	}

	@Test
	public void testAddRefset() throws RefsetServiceException, RefsetGraphAccessException, EntityNotFoundException {
		
		gao.removeRefset(rs.get(0));
		gao.addRefset(rs.get(0));
		
	}
	
	@Test
	public void testAddRefsetAlreadyExist() throws RefsetServiceException, RefsetGraphAccessException, EntityNotFoundException {
		
		gao.removeRefset(rs.get(0));
		gao.addRefset(rs.get(0));
		gao.addRefset(rs.get(0));
		
		List<Refset> rs = gao.getRefSets();
		
		assertEquals(1, rs.size());
	}
	
	@Test
	public void testRemoveRefsetWhenNotExist() throws RefsetServiceException, RefsetGraphAccessException, EntityNotFoundException {

		gao.addRefset(rs.get(0));

		gao.removeRefset(new Refset());

		List<Refset> rs = gao.getRefSets();
		assertEquals(1, rs.size());

	}
	
	@Test
	public void testAddRefsetWithMember() throws RefsetServiceException, RefsetGraphAccessException, EntityNotFoundException {
		
		gao.addRefset(data.getRefSet("450973005"));

		assertEquals(true, gao.getRefsetNodeId("450973005") != null);
	}
	
	@Test
	public void loadData() throws RefsetServiceException, RefsetGraphAccessException, EntityNotFoundException {
		
		int i = 0;
		for (Refset r : rs.subList(0, 2)) {
			gao.addRefset(data.getRefSet(r.getId()));
			assertEquals(true, gao.getRefsetNodeId(r.getId()) != null);
			i++;
		}

		assertEquals(i, 2);
		
		List<Refset> result = gao.getRefSets();
		assertEquals(2, result.size());

		assertEquals(300, result.get(0).getMembers().size());
	}
	
	
	@Test
	public void testGetReftest() throws RefsetServiceException, RefsetGraphAccessException, EntityNotFoundException {
		Refset i = rs.get(0);
		
		gao.addRefset(data.getRefSet(i.getId()));

		Refset r = gao.getRefset(i.getId());

		assertNotNull(r);
		assertEquals(i.getCreated().getMillis(), r.getCreated().getMillis(), 100);
		
		assertEquals(i.getDescription(), r.getDescription());
		assertEquals(i.getModuleId(), r.getModuleId());
		assertEquals(i.getPublishedDate().getMillis(), r.getPublishedDate().getMillis(), 100);
		assertEquals(i.isPublished(), r.isPublished());
		assertEquals(i.getType(), r.getType());
		assertEquals(i.getEffectiveTime().getMillis(), r.getEffectiveTime().getMillis(), 100);

		
	}
	
	@Test(expected = EntityNotFoundException.class)
	public void testGetReftestInvalidRefsetId() throws RefsetServiceException, RefsetGraphAccessException, EntityNotFoundException {		
		
		Refset input = rs.get(0);
		
		gao.addRefset(data.getRefSet(input.getId()));

		gao.getRefset("junitId");
		
	}
	
	@Test
	public void testGetMetaDataAfterAddingRefset() throws RefsetServiceException, RefsetGraphAccessException, EntityNotFoundException {		
		
		Refset input = rs.get(0);
		
		MetaData m = gao.addRefset(data.getRefSet(input.getId()));
		
		assertNotNull(m);
		assertNotNull(m.getId());
		assertEquals(1, m.getVersion(), 0);
		assertEquals("Vertex", m.getType());
		
	}
	
	@Test
	public void testGetMetaDataAfterGetttingRefset() throws RefsetServiceException, RefsetGraphAccessException, EntityNotFoundException {		
		
		Refset input = rs.get(0);
		
		MetaData m = gao.addRefset(data.getRefSet(input.getId()));

		Refset r = gao.getRefset(input.getId());
		
		assertNotNull(r);
		
		MetaData rm = r.getMetaData();
		assertNotNull(rm);
		assertNotNull(rm.getId());
		assertEquals(m.getVersion(), rm.getVersion(), 0);
		assertEquals(m.getType(), rm.getType());
		
	}
	
	@Test(expected = EntityNotFoundException.class)
	public void testGetMetaDataInvalidId() throws RefsetServiceException, RefsetGraphAccessException, EntityNotFoundException {		
		
		gao.getMetaData("someID");
		
	}
	
	
	@Test(expected = EntityNotFoundException.class)
	public void testGetReftestInvalidNodeId() throws RefsetServiceException, RefsetGraphAccessException, EntityNotFoundException {		
		Refset input = rs.get(0);

		gao.addRefset(data.getRefSet(input.getId()));//to avoid NPE when there is no class available of type Refset

		gao.getRefset("junitId");
		
	}
	
	@Test
	public void testGetReftestForaNodeId() throws RefsetServiceException, RefsetGraphAccessException, EntityNotFoundException {		
		Refset input = rs.get(0);

		MetaData m = gao.addRefset(data.getRefSet(input.getId()));

		Refset r = gao.getRefsetFromNodeId(m.getId());
		
		assertNotNull(r);
		
		assertEquals(input.getDescription(), r.getDescription());
		assertEquals(m.getVersion(), r.getMetaData().getVersion());
		
	}
	
	
	@Test
	public void testUpdateRefset() throws RefsetServiceException, RefsetGraphAccessException, EntityNotFoundException {		
		
		Refset input = rs.get(0);
		
		MetaData m = gao.addRefset(data.getRefSet(input.getId()));

		Refset r = gao.getRefset(input.getId());
		
		assertNotNull(r);
		
		r.setDescription("Junit Update");
		gao.updateRefset(r);
		
		Refset rUpdated = gao.getRefset(input.getId());

		
		MetaData rm = r.getMetaData();
		
		MetaData rmUpdt = rUpdated.getMetaData();

		assertEquals(rm.getId(), rmUpdt.getId());
		assertEquals(m.getVersion(), rm.getVersion(), 1);
		
		assertEquals("Junit Update", rUpdated.getDescription());
		
	}
	
	@Test(expected = EntityNotFoundException.class)
	public void testUpdateRefsetInvalidRefset() throws RefsetServiceException, RefsetGraphAccessException, EntityNotFoundException {		
		
	
		gao.updateRefset(new Refset());
		

		
	}

}
