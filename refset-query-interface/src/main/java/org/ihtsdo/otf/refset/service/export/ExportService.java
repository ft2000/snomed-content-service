/**
 * 
 */
package org.ihtsdo.otf.refset.service.export;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.ihtsdo.otf.refset.common.Utility;
import org.ihtsdo.otf.refset.domain.Member;
import org.ihtsdo.otf.refset.domain.Refset;
import org.ihtsdo.otf.refset.exception.EntityNotFoundException;
import org.ihtsdo.otf.refset.exception.ExportServiceException;
import org.ihtsdo.otf.refset.exception.RefsetServiceException;
import org.ihtsdo.otf.refset.service.RefsetBrowseService;
import org.ihtsdo.otf.snomed.service.ConceptLookupService;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.ICsvListWriter;
/**
 * Service to support RF2 export of simple refset
 */
@Service
public class ExportService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ExportService.class);
	
	@Resource( name = "browseGraphService")
	private RefsetBrowseService bService;
	
	@Autowired
	private ConceptLookupService lService;

	
	public  void getRF2Payload(ICsvListWriter lWriter, String refsetUuId) throws ExportServiceException, EntityNotFoundException {
		
		LOGGER.debug("Exporting refset {}", refsetUuId);
		
		if (lWriter == null) {
			
			throw new ExportServiceException("No output writer available");
		}
		
		if (StringUtils.isEmpty(refsetUuId)) {
			
			throw new ExportServiceException("Invalid request");

		}

		try {
			//TODO header has to come from refset descriptor. For now hard code
			//http://supercsv.sourceforge.net/examples_writing.html. 
			
			Refset r = bService.getRefsetForExport(refsetUuId);
			
            final CellProcessor[] processors = getProcessors();
            /**
             * id	effectiveTime	active	moduleId	refsetId	referencedComponentId
             */
			final String[] header = new String[] { "id", "effectiveTime", "active"
            		, "moduleId", "refsetId", "referencedComponentId"};
			            

            lWriter.writeHeader(header);
			
			List<Member> ms = r.getMembers();
			String refsetId = StringUtils.isEmpty(r.getSctId()) ? r.getUuid() : r.getSctId();
			for (Member m : ms) {
								
				String active = m.isActive() ? "1" : "0";
				DateTime et = m.getEffectiveTime() != null ? m.getEffectiveTime() : r.getExpectedReleaseDate();
				
				Object[] rf2Record = new Object[] { m.getUuid(), Utility.getDate(et), active
                		, m.getModuleId(), refsetId, m.getReferencedComponentId()};
				
				List<Object> concept = Arrays.asList(rf2Record);

				lWriter.write(concept, processors);
				
			}
		
           			
		} catch (RefsetServiceException e) {

			LOGGER.error("Error during refset retrivel {}", e);
			
			throw new ExportServiceException("Error in refset retrievel during refset export");
			
		} catch (IOException e) {
			
			throw new ExportServiceException("Error in csv parsing in export");

		}
		
	}
	
	private CellProcessor[] getProcessors() {
        
        final CellProcessor[] processors = new CellProcessor[] { 
                new NotNull(), // id 
                new Optional(), 
                new NotNull(), 
                new NotNull(), 
                new NotNull(), 
                new NotNull(), 
        };
        
        return processors;
	}
	
}
