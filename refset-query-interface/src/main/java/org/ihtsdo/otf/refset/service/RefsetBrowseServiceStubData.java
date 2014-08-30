/**
 * 
 */
package org.ihtsdo.otf.refset.service;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.ihtsdo.otf.refset.domain.Member;
import org.ihtsdo.otf.refset.domain.Refset;
import org.ihtsdo.otf.refset.exception.RefsetServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.supercsv.cellprocessor.ParseBool;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.dozer.CsvDozerBeanReader;
import org.supercsv.io.dozer.ICsvDozerBeanReader;
import org.supercsv.prefs.CsvPreference;

/**
 * @author Episteme Partners
 *
 */
public class RefsetBrowseServiceStubData {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RefsetBrowseServiceStubData.class);
	private static final String REFSET_LIST = "refset";
	private static final String MEMBER_LIST = "members";
	private static Map<String, Integer> refsetIdsAndMembers = new HashMap<String, Integer>() ;
	private static final String[] REFSET_MAPPING = new String[] {"id", "description", "created", "createdBy", "languageCode", 
			"type", "publishedDate", "effectiveTime", "moduleId", "published"};
	private static String[] MEMBER_MAPPING = new String[] {"referenceComponentId", "effectiveTime", "active", "moduleId"}; 

	static 
	{
		/**
		 * 700043003
			450973005
			450971007
			703870008
			447566000
			447565001
			700043013
			450973015
			450971017
			703870018
			447566010
			447565011
			
			700043023
450973025
450971027
703870028
447566020
447565021
700043033
450973035
450971037
703870038
447566030
447565031
700043043
450973045
450971047
703870048
447566040
447565041
700043053
450973055
450971057
703870058
447566050
447565051
700043063
450973065
450971067
703870068
447566060
447565061
700043073
450973075
450971077
703870078
447566070
447565071
700043083
450973085
450971087
703870088
447566080
447565081
700043093
450973095
450971097
703870098
447566090

		 */
		
		
		refsetIdsAndMembers.put("700043003", 300);
		refsetIdsAndMembers.put("450973005", 600);
		refsetIdsAndMembers.put("450971007", 900);
		refsetIdsAndMembers.put("703870008", 1200);
		refsetIdsAndMembers.put("447566000", 1500);
		refsetIdsAndMembers.put("447565001", 1800);
		refsetIdsAndMembers.put("700043013", 2100);
		refsetIdsAndMembers.put("450973015", 2400);
		refsetIdsAndMembers.put("450971017", 2700);
		refsetIdsAndMembers.put("703870018", 3000);
		refsetIdsAndMembers.put("447566010", 3300);
		refsetIdsAndMembers.put("447565011", 3600);
		
		refsetIdsAndMembers.put("700043023", 300);
		refsetIdsAndMembers.put("450973025", 600);
		refsetIdsAndMembers.put("450971027", 900);
		refsetIdsAndMembers.put("703870028", 1200);
		refsetIdsAndMembers.put("447566020", 1500);
		refsetIdsAndMembers.put("447565021", 1800);
		
		refsetIdsAndMembers.put("700043033", 2100);
		refsetIdsAndMembers.put("450973035", 2400);
		refsetIdsAndMembers.put("450971037", 2700);
		refsetIdsAndMembers.put("703870038", 3000);
		refsetIdsAndMembers.put("447566030", 3300);
		refsetIdsAndMembers.put("447565031", 3600);

		
		refsetIdsAndMembers.put("700043043", 2100);
		refsetIdsAndMembers.put("450973045", 2400);
		refsetIdsAndMembers.put("450971047", 2700);
		refsetIdsAndMembers.put("703870048", 3000);
		refsetIdsAndMembers.put("447566040", 3300);
		refsetIdsAndMembers.put("447565041", 3600);
		
		refsetIdsAndMembers.put("700043053", 2100);
		refsetIdsAndMembers.put("450973055", 2400);
		refsetIdsAndMembers.put("450971057", 2700);
		refsetIdsAndMembers.put("703870058", 3000);
		refsetIdsAndMembers.put("447566050", 3300);
		refsetIdsAndMembers.put("447565051", 3600);
		
		refsetIdsAndMembers.put("700043063", 2100);
		refsetIdsAndMembers.put("450973065", 2400);
		refsetIdsAndMembers.put("450971067", 2700);
		refsetIdsAndMembers.put("703870068", 3000);
		refsetIdsAndMembers.put("447566060", 3300);
		refsetIdsAndMembers.put("447565061", 3600);
		
		refsetIdsAndMembers.put("700043073", 2100);
		refsetIdsAndMembers.put("450973075", 2400);
		refsetIdsAndMembers.put("450971077", 2700);
		refsetIdsAndMembers.put("703870078", 3000);
		refsetIdsAndMembers.put("447566070", 3300);
		refsetIdsAndMembers.put("447565071", 3600);
		
		refsetIdsAndMembers.put("700043083", 2100);
		refsetIdsAndMembers.put("450973085", 2400);
		refsetIdsAndMembers.put("450971087", 2700);
		refsetIdsAndMembers.put("703870088", 3000);
		refsetIdsAndMembers.put("447566080", 3300);
		refsetIdsAndMembers.put("447565081", 3600);
		
		refsetIdsAndMembers.put("700043093", 2100);
		refsetIdsAndMembers.put("450973095", 2400);
		refsetIdsAndMembers.put("450971097", 2700);
		refsetIdsAndMembers.put("703870098", 3000);
		refsetIdsAndMembers.put("447566090", 3300);
		refsetIdsAndMembers.put("447565091", 3600);

	}

	
	private Map<String, Resource> csvs;
	
	/**Generate a dummy list of {@link Refset}
	 * @return
	 * @throws RefsetServiceException
	 */
	public   List<Refset> getRefSets() throws RefsetServiceException {
		
		Assert.notNull(csvs);
		Resource csv = csvs.get(REFSET_LIST);
		Assert.notNull(csv);
		
		List<Refset> refsets = new ArrayList<Refset>();
		
		ICsvDozerBeanReader s = null;
		
		try {
			
			s = new CsvDozerBeanReader(new FileReader(csv.getFile()), CsvPreference.STANDARD_PREFERENCE);
			 
			s.getHeader(true); // ignore the header
			s.configureBeanMapping(Refset.class, REFSET_MAPPING);
			Refset r = null;
			while( (r = s.read(Refset.class, getRefSetProcessors())) != null ) {
	           
				refsets.add(r);

			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOGGER.error(String.format("Error while loading refset data %s", e.getMessage()));
			throw new RefsetServiceException(e.getCause());
			
		} finally {
			
			if (s != null) {
				
				try {
					
					s.close();
					
				} catch (IOException e) {
					
					// TODO Auto-generated catch block
					LOGGER.error(String.format("Error closing IO resource "));
					
				}

			}
		}
	    
		
		
		return Collections.unmodifiableList(refsets);
	}
	
	/**Generate a dummy list of {@link Refset}
	 * @return
	 * @throws RefsetServiceException
	 */
	public   Refset getRefSet(String refSetId) throws RefsetServiceException {
		
		List<Refset> refSets = getRefSets();
				
		Refset result = null;
		
		for (Refset refset : refSets) {
			
			if(refset.getId().equalsIgnoreCase(refSetId)) {
								
				result = refset;
				break;
			}
			
		}
		
		if( result == null ) {
			
			throw new RefsetServiceException("Invalid refsetId");
		
		}
		
		
		Assert.notNull(csvs);
		Resource csv = csvs.get(MEMBER_LIST);
		Assert.notNull(csv);
		
		List<Member> members = new ArrayList<Member>();
		
		ICsvDozerBeanReader s = null;
		
		try {
			
			s = new CsvDozerBeanReader(new FileReader(csv.getFile()), CsvPreference.STANDARD_PREFERENCE);
			 
			s.getHeader(true); // ignore the header
			s.configureBeanMapping(Member.class, MEMBER_MAPPING);
			Member m = null;
			while( (m = s.read(Member.class, getMemberProcessors())) != null ) {
	            m.setId(UUID.randomUUID().toString());
	            members.add(m);

			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOGGER.error(String.format("Error while loading member data %s", e.getMessage()));
			throw new RefsetServiceException(e.getCause());
			
		} finally {
			
			if (s != null) {
				
				try {
					
					s.close();
					
				} catch (IOException e) {
					
					// TODO Auto-generated catch block
					LOGGER.error(String.format("Error closing IO resource "));
					
				}

			}
		}
	    
		List<Member> ms = members.subList(refsetIdsAndMembers.get(result.getId()) - 300, refsetIdsAndMembers.get(result.getId()));
		result.setMembers(Collections.unmodifiableList(ms));
		return result;
	}
	
	
	private static CellProcessor[] getRefSetProcessors() {
		
		/**String[] columns = new String[] {"id", "description", "created", "createdBy", "languageCode", 
				"type", "publishedDate", "effectiveTime", "moduleId", "published"};*/
		final CellProcessor[] processors = new CellProcessor[] { 
				new NotNull(), // id
				new NotNull(), // description
				new NotNull(new ParseJodaTime("yyyyMMdd")), // publishedDate
				new NotNull(), // createdBy
				new NotNull(), // languageCode
				new NotNull(), // type
				new NotNull(new ParseJodaTime("yyyyMMdd")), // publishedDate
				new NotNull(new ParseJodaTime("yyyyMMdd")), // publishedDate
				new NotNull(), // moduleId
				new NotNull(new ParseBool()), // published
				
		};
		
		return processors;
	}
	
	private static CellProcessor[] getMemberProcessors() {
		
		/**	private static String[] MEMBER_MAPPING = new String[] {"referenceComponentId", "effectiveTime", "active", "moduleId"}; */
		final CellProcessor[] processors = new CellProcessor[] { 
				
				new NotNull(), // referenceComponentId
				new NotNull(new ParseJodaTime("yyyyMMdd")), // effectiveTime
				new NotNull(new ParseBool()), // active
				new NotNull(), // moduleId

		};
		
		return processors;
	}

	/**
	 * @param csvs the csvs to set
	 */
	public void setCsv(Map<String, Resource> csvs) {
		this.csvs = csvs;
	}

}