package org.ihtsdo.otf.refset.graph.gao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.MapConfiguration;
import org.ihtsdo.otf.refset.domain.MetaData;
import org.ihtsdo.otf.refset.domain.Refset;
import org.ihtsdo.otf.refset.exception.EntityAlreadyExistException;
import org.ihtsdo.otf.refset.exception.EntityNotFoundException;
import org.ihtsdo.otf.refset.exception.RefsetServiceException;
import org.ihtsdo.otf.refset.graph.RefsetGraphAccessException;
import org.ihtsdo.otf.refset.graph.RefsetGraphFactory;
import org.ihtsdo.otf.refset.graph.gao.RefsetGAO;
import org.ihtsdo.otf.refset.schema.RefsetSchema;
import org.ihtsdo.otf.refset.service.RefsetBrowseServiceStubData;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { 
		"file:src/main/webapp/WEB-INF/spring/appServlet/spring-refset-browse-service-stub-data.xml"})
public class RefsetAdminGAOTest {
	
	static {
		System.setProperty("env", "junit");
	}
	
	private RefsetGAO gao;
	
	private RefsetAdminGAO aGao;
	
	@Autowired
	private RefsetBrowseServiceStubData data;
	
	private List<Refset> rs;
	
	private RefsetGraphFactory f;
	
	@Before
	public void setUp() throws Exception {
		
		
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("storage.directory", "/tmp/berkeley");
		map.put("storage.backend", "berkeleyje");
		
		
		
		Configuration config = new MapConfiguration(map);
		
		/*create schema*/
		
		RefsetSchema sg = new RefsetSchema("src/test/resources/titan-graph-es-junit.properties");
		
		sg.createSchema();
		
		gao = new RefsetGAO();
		f = new RefsetGraphFactory(config);
		gao.setRGFactory(f);
		
		MemberGAO mGao = new MemberGAO();
		mGao.setFactory(f);
		mGao.setRefsetGao(gao);
		
		
		aGao = new RefsetAdminGAO();
		aGao.setFactory(f);
		aGao.setMemberGao(mGao);
		aGao.setRefsetGao(gao);
		
		rs = data.getRefSets();
		assertNotNull(rs);
		assertTrue(!rs.isEmpty());				

	}
	
	@BeforeClass
	public static void cleanUp() {

		delete();
		
	}

	@After
	public void tearDown() throws Exception {
		
		//cleanup();

	}
	
	@AfterClass
	public  static void cleanup() throws RefsetGraphAccessException, EntityNotFoundException {
		
		delete();

	}
	
	private static void delete() {
		File f = new File("/tmp/berkeley");
		String[] files = f.list();
		
		if (files != null) {
			
			for (int i = 0; i < files.length; i++) {
				
				File file = new File("/tmp/berkeley/" + files[i]);
				System.err.println(file.getAbsolutePath());

				file.delete();
				
			}
		}
		
		f.delete();
	}

	@Test
	public void testAddRefset() throws RefsetServiceException, RefsetGraphAccessException, EntityNotFoundException, EntityAlreadyExistException {
		
		aGao.addRefset(rs.get(0));
		
		List<Refset> rs = gao.getRefSets(false);
		
		assertEquals(1, rs.size());
		
	}
	
	@Test
	public void testAddRefsetAlreadyExist() throws RefsetServiceException, RefsetGraphAccessException, EntityNotFoundException, EntityAlreadyExistException {

		aGao.addRefset(rs.get(0));
		aGao.addRefset(rs.get(0));
		
		List<Refset> rs = gao.getRefSets(false);
		
		assertEquals(1, rs.size());
	}
	
	@Test(expected = EntityNotFoundException.class)
	public void testRemoveRefsetWhenNotExist() throws RefsetServiceException, RefsetGraphAccessException, EntityNotFoundException, EntityAlreadyExistException {

		aGao.addRefset(rs.get(0));

		aGao.removeRefset(new Refset().getUuid(), "junit");

	}
	
	@Test
	public void testAddRefsetWithMember() throws RefsetServiceException, RefsetGraphAccessException, EntityNotFoundException, EntityAlreadyExistException {
		
		aGao.addRefset(data.getRefSet("450973005"));

		assertEquals(true, gao.getRefset("450973005") != null);
	}
	
	@Test
	public void loadData() throws RefsetServiceException, RefsetGraphAccessException, EntityNotFoundException, EntityAlreadyExistException {
		
		int i = 0;
		for (Refset r : rs.subList(0, 2)) {
			
			aGao.addRefset(data.getRefSet(r.getUuid()));
			
			assertEquals(true, gao.getRefset(r.getUuid()) != null);
			i++;
		}

		assertEquals(i, 2);
		
		List<Refset> result = gao.getRefSets(false);
		assertEquals(2, result.size());

		assertEquals(0, result.get(0).getMembers().size());
		
		Refset r = gao.getRefset(result.get(0).getUuid());
		
		assertEquals(300, r.getMembers().size());
		
	}

	@Test
	public void testUpdateRefset() throws RefsetServiceException, RefsetGraphAccessException, EntityNotFoundException, EntityAlreadyExistException {		
		
		Refset input = rs.get(0);

		aGao.addRefset(data.getRefSet(input.getUuid()));

		Refset r = gao.getRefset(input.getUuid());
		
		assertNotNull(r);
		
		r.setDescription("Junit Update");
		aGao.updateRefset(r);
		
		Refset rUpdated = gao.getRefset(input.getUuid());

		
		MetaData rm = r.getMetaData();
		
		MetaData rmUpdt = rUpdated.getMetaData();

		assertEquals(rm.getId(), rmUpdt.getId());
		
		assertEquals("Junit Update", rUpdated.getDescription());
		
		assertEquals(r.getMembers().size(), rUpdated.getMembers().size());
		
	}
	
	@Test(expected = EntityNotFoundException.class)
	public void testUpdateRefsetInvalidRefset() throws RefsetServiceException, RefsetGraphAccessException, EntityNotFoundException {		
		
		aGao.updateRefset(new Refset());
		

		
	}

}
