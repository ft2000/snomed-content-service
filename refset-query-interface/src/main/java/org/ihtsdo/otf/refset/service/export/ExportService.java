/**
 * 
 */
package org.ihtsdo.otf.refset.service.export;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.ihtsdo.otf.refset.domain.Member;
import org.ihtsdo.otf.refset.domain.Refset;
import org.ihtsdo.otf.refset.exception.EntityNotFoundException;
import org.ihtsdo.otf.refset.exception.ExportServiceException;
import org.ihtsdo.otf.refset.exception.RefsetServiceException;
import org.ihtsdo.otf.refset.service.RefsetBrowseService;
import org.ihtsdo.otf.snomed.domain.Concept;
import org.ihtsdo.otf.snomed.exception.ConceptServiceException;
import org.ihtsdo.otf.snomed.service.ConceptLookupService;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.supercsv.cellprocessor.FmtBool;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.constraint.UniqueHashCode;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.ICsvListWriter;
/**
 * @author Episteme Partners
 *
 */
@Service
public class ExportService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ExportService.class);
	
	@Resource( name = "browseGraphService")
	private RefsetBrowseService bService;
	
	@Autowired
	private ConceptLookupService lService;

	
	public  void getRF2Payload(ICsvListWriter lWriter, String refsetId) throws ExportServiceException, EntityNotFoundException {
		
		LOGGER.debug("Exporting refset for refsetid {}", refsetId);
		
		if (lWriter == null) {
			
			throw new ExportServiceException("No output writer available");
		}
		
		if (StringUtils.isEmpty(refsetId)) {
			
			throw new ExportServiceException("Invalid request");

		}

		try {
			//TODO header has to come from refset descriptor for now hard code
			//http://supercsv.sourceforge.net/examples_writing.html. 
			
			Refset r = bService.getRefset(refsetId);
			
            final CellProcessor[] processors = getProcessors();

			final String[] header = new String[] { "id", "effectiveTime", "active"
            		, "moduleId", "referenceComponentId", "languageCode", "typeId", "description"};
			            

            lWriter.writeHeader(header);

               
            final List<Object> refset = Arrays.asList(new Object[] { r.getId(), getDate(r.getEffectiveTime()), r.isActive()
                		, r.getModuleId(), r.getSuperRefsetTypeId(), r.getLanguageCode(), r.getTypeId(), r.getDescription()});

            lWriter.write(refset, processors);
            
            Map<String, String> refConceptIds = getConceptIds(r);
			
			Collection<String> conceptIds = refConceptIds.values();
			if (conceptIds != null && !conceptIds.isEmpty()) {
				
				Set<String> values = new HashSet<String>(conceptIds);
				Map<String, Concept> cs = lService.getConcepts(values);
				
				List<Member> ms = r.getMembers();

				for (Member m : ms) {
					
					Concept c = cs.get(m.getReferenceComponentId());
					
					List<Object> concept = Arrays.asList(new Object[] { m.getId(), getDate(m.getEffectiveTime()), c.isActive()
	                		, c.getModule(), c.getId(), r.getLanguageCode(), c.getType(), c.getLabel()});

					lWriter.write(concept, processors);
					
				}
			}
           			
		} catch (RefsetServiceException e) {

			LOGGER.error("Error during refset retrivel {}", e);
			
			throw new ExportServiceException("Error in refset retrievel during refset export");
			
		} catch (ConceptServiceException e) {

			LOGGER.error("Error during concept lookup {}", e);
			
			throw new ExportServiceException("Error in concept lookup during refset export");

		} catch (IOException e) {
			
			throw new ExportServiceException("Error in csv parsing in export");

		}
		
	}
	
	private Map<String, String> getConceptIds(Refset r) {
		
		Map<String, String> ids = new HashMap<String, String>();
		
		List<Member> ms = r.getMembers();
		
		for (Member m : ms) {
			
			ids.put(m.getId(), m.getReferenceComponentId());
			
		}
		
		
		return ids;
		
	}
	
	private CellProcessor[] getProcessors() {
        
        final CellProcessor[] processors = new CellProcessor[] { 
                new UniqueHashCode(), // id 
                new Optional(), 
                new Optional(new FmtBool("0", "1")), // active
                new Optional(), 
                new Optional(), 
                new Optional(), 
                new Optional(), 
                new Optional(), 
        };
        
        return processors;
	}
	
	private String getDate(DateTime dt) {
		
		if(dt != null) {
			
			DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyyMMdd");
			String date = dt.toString(fmt);
			
			return date;

		}

		return null;
	}
	
	
	
}
