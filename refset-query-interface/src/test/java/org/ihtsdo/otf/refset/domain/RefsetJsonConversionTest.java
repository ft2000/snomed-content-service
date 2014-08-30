/**
 * 
 */
package org.ihtsdo.otf.refset.domain;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Episteme Partners
 *
 */
public class RefsetJsonConversionTest {
	
	private static final String ID = "id";
	private static final String ID_DESC = "SCTID of a refset";
	private static final String EFFECTIVE_TIME = "effectiveTime";
	private static final String EFFECTIVE_TIME_DESC = "Date on which this refset was released";
	private static final String MODULE_ID = "moduleId";
	private static final String MODULE_ID_DESC = "Module Id";
	private static final String LANGUAGE_CODE = "languageCode";
	private static final String LANGUAGE_CODE_DESC = "Language in which refset description is being coded";
	private static final String MEMBER_ID = "memberId";
	private static final String MEMBER_ID_DESC = "Member Id";

	private static final String PUBLISHED = "isPublished";
	private static final String PUBLISHED_DESC = "Flag to indicate if this refset is published";
	
	private static final String REFSET_TYPE = "type";
	private static final String REFSET_TYPE_DESC = "Refset Type";
	
	private static final String REFSET_TYPE_ID = "typeId";
	private static final String REFSET_TYPE_ID_DESC = "Refset Type Id";

	
	private static final String REFSET_SUPER_TYPE_ID = "superRefsetTypeId";
	private static final String REFSET_SUPER_TYPE_ID_DESC = "Id of parent refset";
	

	
	private static final String PUBLISHED_DATE = "publishedDate";
	private static final String PUBLISHED_DATE_DESC = "Date at which refset was published";

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void getJson() throws JsonGenerationException, JsonMappingException, IOException {
		
		Refset r = new Refset();
		
		r.setId("700043003");
		r.setCreated( new DateTime() );
		r.setCreatedBy("Junit Refset Editor");
		r.setDescription("GP/FP health issue reference set (foundation metadata concept)");
		
		Map<String, String> descriptor = new HashMap<String, String>();
		descriptor.put(ID, ID_DESC);
		descriptor.put(MEMBER_ID, MEMBER_ID_DESC);
		descriptor.put(LANGUAGE_CODE, LANGUAGE_CODE_DESC);
		descriptor.put(MODULE_ID, MODULE_ID_DESC);
		descriptor.put(EFFECTIVE_TIME, EFFECTIVE_TIME_DESC);
		descriptor.put(PUBLISHED, PUBLISHED_DESC);
		descriptor.put(PUBLISHED_DATE, PUBLISHED_DATE_DESC);
		descriptor.put(REFSET_SUPER_TYPE_ID, REFSET_SUPER_TYPE_ID_DESC);
		descriptor.put(REFSET_TYPE, REFSET_TYPE_DESC);
		descriptor.put(REFSET_TYPE_ID, REFSET_TYPE_ID_DESC);
		
		List<Member> members = new ArrayList<Member>();
		Member m = new Member();
		m.setId("450451007");
		m.setActive(true);
		m.setReferenceComponentId("4504511107");
		m.setModuleId("900000000000207002");

		members.add(m);
		
		m = new Member();
		m.setActive(true);
		m.setReferenceComponentId("4504511107");
		m.setModuleId("900000000000207002");
		
		m.setId("450451006");
		members.add(m);
		
		m = new Member();
		m.setActive(true);
		m.setReferenceComponentId("4504511107");
		m.setModuleId("900000000000207008");
		m.setId("450451005");
		members.add(m);
		
		m = new Member();
		m.setActive(true);
		m.setReferenceComponentId("4504511107");
		m.setModuleId("900000000000207008");
		m.setId("450451017");
		members.add(m);
		
		r.setMembers(members);
		
		r.setType(RefsetType.simple);
		
		r.setModuleId("900000000000207008");
		
		r.setLanguageCode("en-GB");
		
		r.setPublished(true);
		r.setPublishedDate( new DateTime());
		
		r.setTypeId("5000");
		r.setSuperRefsetTypeId("none");

		ObjectMapper mapper = new ObjectMapper();
		
		mapper.writeValue(new File("src/test/resources/refset.json"), r);
		
	}

}
