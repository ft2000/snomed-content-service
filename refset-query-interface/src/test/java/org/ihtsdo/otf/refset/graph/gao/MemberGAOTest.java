package org.ihtsdo.otf.refset.graph.gao;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.MapConfiguration;
import org.ihtsdo.otf.refset.domain.Member;
import org.ihtsdo.otf.refset.domain.Refset;
import org.ihtsdo.otf.refset.exception.EntityNotFoundException;
import org.ihtsdo.otf.refset.graph.RefsetGraphAccessException;
import org.ihtsdo.otf.refset.graph.RefsetGraphFactory;
import org.ihtsdo.otf.refset.schema.RefsetSchema;
import org.ihtsdo.otf.refset.service.RefsetBrowseServiceStubData;
import org.joda.time.DateTime;
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
public class MemberGAOTest {
	
	static {
		System.setProperty("env", "junit");
	}
	
	private MemberGAO gao;
	
	private RefsetGAO rGao;
	
	private RefsetAdminGAO aGao;


	
	@Autowired
	private RefsetBrowseServiceStubData data;
		
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
		
		rGao = new RefsetGAO();
		f = new RefsetGraphFactory(config);
		rGao.setRGFactory(f);
			
		
		gao = new MemberGAO();
		f = new RefsetGraphFactory(config);
		gao.setFactory(f);
		gao.setRefsetGao(rGao);

		aGao = new RefsetAdminGAO();
		aGao.setFactory(f);
		aGao.setMemberGao(gao);
		aGao.setRefsetGao(rGao);				

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
	public void removeMember() throws EntityNotFoundException, RefsetGraphAccessException {
		addRefset();
		
		Refset before = rGao.getRefset("junit_1");

		int beforeSize = before.getMembers().size();
		
		assertEquals(2, beforeSize);
		
		gao.removeMember("junit_1", "101", "junit");
		
		Refset after = rGao.getRefset("junit_1");

		int afterSize = after.getMembers().size();

		assertEquals(1, afterSize);
	}

	@Test(expected = EntityNotFoundException.class)
	public void removeMemberWhenNullReferenceComponentId() throws EntityNotFoundException, RefsetGraphAccessException {
		
		gao.removeMember("junit_1", null, "junit");
	}
	
	@Test(expected = EntityNotFoundException.class)
	public void removeMemberWhenEmptyReferenceComponentId() throws EntityNotFoundException, RefsetGraphAccessException {
		
		gao.removeMember("junit_1", "", "junit");
	}
	
	@Test(expected = EntityNotFoundException.class)
	public void removeMemberWhenNullRefsetId() throws EntityNotFoundException, RefsetGraphAccessException {
		
		gao.removeMember(null, "junit_1", "junit");
	}
	
	@Test(expected = EntityNotFoundException.class)
	public void removeMemberWhenEmptyRefsetId() throws EntityNotFoundException, RefsetGraphAccessException {
		
		gao.removeMember("", "junit_1", "junit");
	}
	
	private void addRefset() throws RefsetGraphAccessException {
		
		Refset r = new Refset();
		r.setUuid("junit_1");
		r.setDescription("Junit");
		
		Member m = new Member();
		m.setReferencedComponentId("101");

		Member m_1 = new Member();
		m_1.setReferencedComponentId("102");

		List<Member> members = new ArrayList<Member>();
		members.add(m);
		members.add(m_1);

		
		r.setMembers(members);
		r.setCreated(new DateTime());
		r.setCreatedBy("junit");
		r.setModuleId("somenumber");
		aGao.addRefset(r);
		
	}


}
