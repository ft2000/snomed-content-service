/**
* Copyright 2014 IHTSDO
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.ihtsdo.otf.refset.service.diffreport;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.ihtsdo.otf.refset.api.diff.report.DiffReportController;
import org.ihtsdo.otf.refset.exception.RefsetServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

/**Service to add load refset files to mysql db then to generate refset diff report
 * this is temporary and should be converted in a permanent solution using cassandra
 *
 */
@Service
public class DiffReportService {
	private static final Logger logger = LoggerFactory.getLogger(DiffReportController.class);

	private static final String LOAD_Q = "load data local infile '%s' into table %s LINES TERMINATED BY '\r\n' ignore 1 lines;";
	private static final String LOAD_REFSET_IDENTIFIER_Q = "load data local infile '%s' into table %s;";
	
	
	@Autowired
	private JdbcTemplate t;
	
	@Resource(name = "dropTables")
	Map<String, String> dropTables;

	@Resource(name = "createTables")
	Map<String, String> createTables;

	@Resource(name = "diffReport")
	String diffReport;

	public void loadFileToTable(String file, String tName) {
		
		logger.debug("Adding file - {} data to db table - {} " , file, tName);
		
		String fQeury = String.format(LOAD_Q, file, tName);
		
		logger.debug("Adding file - query {}" , fQeury);

		t.execute(fQeury);
		
	}
	
	public void loadRefsetIdentifier(String file, String tName) {
		
		logger.debug("Adding file - {} data to db table - refsetids" , file, tName);
		
		String fQeury = String.format(LOAD_REFSET_IDENTIFIER_Q, file, tName);
		
		logger.debug("Adding file - query {}" , fQeury);

		t.execute(fQeury);
		
	}
	
	
	/**Create a tables with desired suffix
	 * @return
	 */
	public void createTables(String table, String suffix) {
		logger.debug("creating table {}", table);

		String qCreateTables = createTables.get(table).replaceAll("replaceme", suffix);
		logger.debug("creating tables query {} ", qCreateTables);

		t.execute(qCreateTables);
	}
	
	/**drop tables with given suffix
	 * @return
	 */
	public void dropTables(String table, String suffix) {
		logger.debug("dropping table {}", table);

		String qDropTables = dropTables.get(table).replaceAll("replaceme", suffix);
		logger.debug("dropping table query {} ", qDropTables);

		t.execute(qDropTables);
	}
	
	
	public List<DiffReportRecord> getDiffReportRecords(String suffix, String releaseDate) {
		
		String qDiffReport = diffReport.replaceAll("replaceme", suffix).replaceAll("releaseDate", releaseDate);

		logger.debug("Getting diff report query {} ", qDiffReport);

		return t.query(qDiffReport, new RowMapper<DiffReportRecord> () {
			/* (non-Javadoc)
			 * @see org.springframework.jdbc.core.RowMapper#mapRow(java.sql.ResultSet, int)
			 */
			@Override
			public DiffReportRecord mapRow(ResultSet rs, int rowNum)
					throws SQLException {
				DiffReportRecord dr = new DiffReportRecord();
				
				dr.setActive(rs.getString("active"));
				
				dr.setAssociationDescription(rs.getString("reasondesc"));
				
				dr.setAssociationRefCode(rs.getString("erefsetid"));
				
				dr.setConceptDescription(rs.getString("d4term"));
				
				dr.setConceptId(rs.getString("referencedcomponentid"));
				
				dr.setReasonCode(rs.getString("valueid"));
				
				dr.setReasonDescription(rs.getString("dterm"));
				
				dr.setReferenceComponentDescription(rs.getString("reasondesc3"));
				
				dr.setReferenceComponentId(rs.getString("targetcomponentid"));
				
				dr.setRefsetDescription(rs.getString("descrip"));
				
				dr.setRefsetId(rs.getString("arefsetid"));
				
				return dr;
			}
		});
	}
	
	
	public void writeDiffReport(List<DiffReportRecord> drs, OutputStream os) throws RefsetServiceException {
		
		logger.debug("Writing diff report ");

		Workbook  wb = null;
        try {
			
        	wb = new XSSFWorkbook();
            
        	writeSheet_1(wb, drs);
        	writeSheet_2(wb, drs);


            wb.write(os);
            
            
		} catch (IOException e) {
			
			logger.error("Error writing diff report ", e);
			
			throw new RefsetServiceException("Unknown error while generating diff report");
			
		} finally {
			
			
			if (wb != null) {
				
				try {
					
					wb.close();
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					logger.error("Error closing io resources ", e);
				}
			}
		}
	}

	/**
	 * @param wb
	 * @param drs
	 * @param refsetIds
	 */
	private void writeSheet_2(Workbook wb, List<DiffReportRecord> drs) {
		logger.debug("Writing diff report - sheet2 ");

		Sheet sheet = wb.createSheet("Sheet-2");
        int rowId = 0;
        //create column labels
        Row row = sheet.createRow(rowId++);
        addColumnLabels(row);

        List<String> processedCoceptRefset = new ArrayList<String>();
        
		for (DiffReportRecord dr : drs) {
			

			String processedId = dr.getConceptId()+dr.getReferenceComponentId();
			
			if (!processedCoceptRefset.contains(processedId)) {
				
				row = sheet.createRow(rowId++);

				processedCoceptRefset.add(processedId);
				
				Cell conceptId = row.createCell(0);

	            conceptId.setCellValue(dr.getConceptId());
	            
	            Cell conceptDescription = row.createCell(1);
	            conceptDescription.setCellValue(dr.getConceptDescription());
	            
	            Cell refsetId = row.createCell(2);
	            refsetId.setCellValue(dr.getRefsetId());
	            
	            Cell refsetDescription = row.createCell(3);
	            refsetDescription.setCellValue(dr.getRefsetDescription());

	            Cell active = row.createCell(4);
	            active.setCellValue(dr.getActive());

	            Cell reasonCode = row.createCell(5);
	            reasonCode.setCellValue(dr.getReasonCode());

	            Cell reasonDescription = row.createCell(6);
	            reasonDescription.setCellValue(dr.getReasonDescription());

	            Cell associationRefCode = row.createCell(7);
	            associationRefCode.setCellValue(dr.getAssociationRefCode());

	            Cell associationDescription = row.createCell(8);
	            associationDescription.setCellValue(dr.getAssociationDescription());

	            Cell referenceComponentId = row.createCell(9);
	            referenceComponentId.setCellValue(dr.getReferenceComponentId());

	            Cell referenceComponentIdDescription = row.createCell(10);
	            referenceComponentIdDescription.setCellValue(dr.getReferenceComponentDescription());

			}
			
            

		}
	
	}

	/**
	 * @param wb
	 * @param drs 
	 */
	private void writeSheet_1(Workbook wb, List<DiffReportRecord> drs) {

		logger.debug("Writing diff report - sheet1 ");

		Sheet sheet = wb.createSheet("Sheet-1");
        
        int rowId = 0;
        //create column labels
        Row row = sheet.createRow(rowId++);
        addColumnLabels(row);
        
        for (DiffReportRecord dr : drs) {
			
            row = sheet.createRow(rowId++);

            Cell conceptId = row.createCell(0);

            conceptId.setCellValue(dr.getConceptId());
            
            Cell conceptDescription = row.createCell(1);
            conceptDescription.setCellValue(dr.getConceptDescription());
            
            Cell refsetId = row.createCell(2);
            refsetId.setCellValue(dr.getRefsetId());
            
            Cell refsetDescription = row.createCell(3);
            refsetDescription.setCellValue(dr.getRefsetDescription());

            Cell active = row.createCell(4);
            active.setCellValue(dr.getActive());

            Cell reasonCode = row.createCell(5);
            reasonCode.setCellValue(dr.getReasonCode());

            Cell reasonDescription = row.createCell(6);
            reasonDescription.setCellValue(dr.getReasonDescription());

            Cell associationRefCode = row.createCell(7);
            associationRefCode.setCellValue(dr.getAssociationRefCode());

            Cell associationDescription = row.createCell(8);
            associationDescription.setCellValue(dr.getAssociationDescription());

            Cell referenceComponentId = row.createCell(9);
            referenceComponentId.setCellValue(dr.getReferenceComponentId());

            Cell referenceComponentIdDescription = row.createCell(10);
            referenceComponentIdDescription.setCellValue(dr.getReferenceComponentDescription());

		}
	}

	/**
	 * @param row
	 */
	private void addColumnLabels(Row row) {

		logger.debug("Adding column lables diff report ");

		Cell conceptId = row.createCell(0);

        conceptId.setCellValue("Concept Id");
        
        Cell conceptDescription = row.createCell(1);
        conceptDescription.setCellValue("Concept Description");
        
        Cell refsetId = row.createCell(2);
        refsetId.setCellValue("Refset Id");
        
        Cell refsetDescription = row.createCell(3);
        refsetDescription.setCellValue("Refset Description");

        Cell active = row.createCell(4);
        active.setCellValue("Active");

        Cell reasonCode = row.createCell(5);
        reasonCode.setCellValue("Reason Code");

        Cell reasonDescription = row.createCell(6);
        reasonDescription.setCellValue("Reason Description");

        Cell associationRefCode = row.createCell(7);
        associationRefCode.setCellValue("Association Ref Code");

        Cell associationDescription = row.createCell(8);
        associationDescription.setCellValue("Association Description");

        Cell referenceComponentId = row.createCell(9);
        referenceComponentId.setCellValue("Referenced Component Id");

        Cell referenceComponentIdDescription = row.createCell(10);
        referenceComponentIdDescription.setCellValue("Referenced Component Description");
	}
	
	
	
	public List<String> getRefsetIds(String suffix) {
		
		String qRefsetIds = "select distinct id from refsetids_replaceme".replaceAll("replaceme", suffix);

		logger.debug("Query {} ", qRefsetIds);

		return t.queryForList(qRefsetIds, String.class);
	}
	
	
}
