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

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.bean.ColumnPositionMappingStrategy;
import au.com.bytecode.opencsv.bean.CsvToBean;

/**
 * @author Episteme Partners
 *
 */
public class RefsetBrowseServiceStubData {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RefsetBrowseServiceStubData.class);
	private static final String REFSET_LIST = "refset";
	private static final String MEMBER_LIST = "members";
	private static Map<String, Integer> refsetIdsAndMembers = new HashMap<String, Integer>() ;
	
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
		 */
		
		
		refsetIdsAndMembers.put("700043003", 300);
		refsetIdsAndMembers.put("450973005", 600);
		refsetIdsAndMembers.put("450971007", 900);
		refsetIdsAndMembers.put("703870008", 1200);
		refsetIdsAndMembers.put("447566000", 1500);
		refsetIdsAndMembers.put("447565001", 18000);
		refsetIdsAndMembers.put("700043013", 21000);
		refsetIdsAndMembers.put("450973015", 2400);
		refsetIdsAndMembers.put("450971017", 2700);
		refsetIdsAndMembers.put("703870018", 3000);
		refsetIdsAndMembers.put("447566010", 3300);
		refsetIdsAndMembers.put("447565011", 3600);

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
		
		ColumnPositionMappingStrategy<Refset> s = new ColumnPositionMappingStrategy<Refset>();
		
		String[] columns = new String[] {"id", "description", "created", "createdBy", "languageCode", 
				"type", "publishedDate", "effectiveTime", "moduleId", "published"}; 
		s.setColumnMapping(columns);
		s.setType(Refset.class);
		
		CsvToBean<Refset> refset = new CsvToBean<Refset>();		
	    CSVReader reader = null;
	    
		try {
			
			reader = new CSVReader(new FileReader(csv.getFile()), ',');
			List<Refset> list = refset.parse(s, reader);
			refsets.addAll(list);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOGGER.error(String.format("Error while loading refset data %s", e.getMessage()));
			throw new RefsetServiceException(e.getCause());
			
		} finally {
			
			if (reader != null) {
				
				try {
					
					reader.close();
					
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
		
		List<Refset> refSets = getRefSets().subList(0, 10);
				
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
		
		ColumnPositionMappingStrategy<Member> s = new ColumnPositionMappingStrategy<Member>();
		
		String[] columns = new String[] {"referenceComponentId", "effectiveTime", "active", "moduleId"}; 
		s.setColumnMapping(columns);
		s.setType(Member.class);
		
		CsvToBean<Member> m = new CsvToBean<Member>();		
	    CSVReader reader = null;
	    
		try {
			
			reader = new CSVReader(new FileReader(csv.getFile()), ',');
			List<Member> list = m.parse(s, reader);
			
			for (Member member : list) {
				
				member.setId(UUID.randomUUID().toString());
				members.add(member);
			}
			
			members.addAll(list);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOGGER.error(String.format("Error while loading member data %s", e.getMessage()));
			throw new RefsetServiceException(e.getCause());
			
		} finally {
			
			if (reader != null) {
				
				try {
					
					reader.close();
					
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

	/**
	 * @param csvs the csvs to set
	 */
	public void setCsv(Map<String, Resource> csvs) {
		this.csvs = csvs;
	}

}