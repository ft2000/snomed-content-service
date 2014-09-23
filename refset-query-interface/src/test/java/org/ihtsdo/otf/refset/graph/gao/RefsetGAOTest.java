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
import org.ihtsdo.otf.refset.exception.EntityNotFoundException;
import org.ihtsdo.otf.refset.exception.RefsetServiceException;
import org.ihtsdo.otf.refset.graph.RefsetGraphAccessException;
import org.ihtsdo.otf.refset.graph.RefsetGraphFactory;
import org.ihtsdo.otf.refset.graph.gao.RefsetGAO;
import org.ihtsdo.otf.refset.graph.schema.RefsetSchemaCreator;
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
public class RefsetGAOTest {
	
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
		
		RefsetSchemaCreator sg = new RefsetSchemaCreator();
		sg.setConfig(config);
		
		sg.createRefsetSchema();
		
		
		
		
		gao = new RefsetGAO();
		f = new RefsetGraphFactory(config);
		gao.setFactory(f);
		
		aGao = new RefsetAdminGAO();
		aGao.setFactory(f);
		
		MemberGAO mGao = new MemberGAO();
		mGao.setFactory(f);
		mGao.setRefsetGao(gao);
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
	public void testGetReftest() throws RefsetServiceException, RefsetGraphAccessException, EntityNotFoundException {
		Refset i = rs.get(0);
		aGao.removeRefset(i.getId());
		aGao.addRefset(data.getRefSet(i.getId()));

		Refset r = gao.getRefset(i.getId());

		assertNotNull(r);
		assertEquals(i.getCreated().getMillis(), r.getCreated().getMillis(), 1000);
		
		assertEquals(i.getDescription(), r.getDescription());
		assertEquals(i.getModuleId(), r.getModuleId());
		assertEquals(i.getPublishedDate().getMillis(), r.getPublishedDate().getMillis(), 100);
		assertEquals(i.isPublished(), r.isPublished());
		assertEquals(i.getType(), r.getType());
		assertEquals(i.getEffectiveTime().getMillis(), r.getEffectiveTime().getMillis(), 100);

		
	}
	
	@Test(expected = EntityNotFoundException.class)
	public void testGetReftestInvalidRefsetId() throws RefsetServiceException, RefsetGraphAccessException, EntityNotFoundException {		
				
		gao.getRefset("junitId");
		
	}
	
	
	@Test
	public void testGetMetaDataAfterGetttingRefset() throws RefsetServiceException, RefsetGraphAccessException, EntityNotFoundException {		
		
		Refset i = rs.get(0);
		
		Refset rInput = data.getRefSet(i.getId());
		
		aGao.addRefset(rInput);

		Refset r = gao.getRefset(rInput.getId());
		
		assertNotNull(r);
		
		MetaData rm = r.getMetaData();
		
		assertNotNull(rm);
		assertNotNull(rm.getId());
		
	}
	
	@Test(expected = EntityNotFoundException.class)
	public void testGetMetaDataInvalidId() throws RefsetServiceException, RefsetGraphAccessException, EntityNotFoundException {		
		
		//make sure atleast one Refset exist in database to avoid schema exception
		Refset input = rs.get(0);
		
		aGao.addRefset(data.getRefSet(input.getId()));

		gao.getMetaData("someID");
		
	}
	
	
	@Test(expected = EntityNotFoundException.class)
	public void testGetReftestInvalidNodeId() throws RefsetServiceException, RefsetGraphAccessException, EntityNotFoundException {		
		Refset input = rs.get(0);
		
		aGao.addRefset(data.getRefSet(input.getId()));//to avoid NPE when there is no class available of type Refset

		gao.getRefset("junitId");
		
	}
	

}
