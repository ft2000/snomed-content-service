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
            /**
             * id	effectiveTime	active	moduleId	refSetId	referencedComponentId
             */
			final String[] header = new String[] { "id", "effectiveTime", "active"
            		, "moduleId", "refSetId", "referenceComponentId"};
			            

            lWriter.writeHeader(header);

               
            /*final List<Object> refset = Arrays.asList(new Object[] { r.getId(), getDate(r.getEffectiveTime()), r.isActive()
                		, r.getModuleId(), r.getSuperRefsetTypeId(), r.getLanguageCode(), r.getTypeId(), r.getDescription()});

            lWriter.write(refset, processors);*/
			
			List<Member> ms = r.getMembers();

			for (Member m : ms) {
								
				String active = m.isActive() ? "1" : "0";
				List<Object> concept = Arrays.asList(new Object[] { m.getId(), Utility.getDate(m.getEffectiveTime()), active
                		, r.getModuleId(), r.getId(), m.getReferenceComponentId()});

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
                new UniqueHashCode(), // id 
                new Optional(), 
                new Optional(), 
                new Optional(), 
                new Optional(), 
                new Optional(), 
        };
        
        return processors;
	}
	
}
