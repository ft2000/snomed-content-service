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
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
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
 * this is temporary and should be converted in a permanent solution using cassandra.
 * Also final query used is as provided by Andrew A. This query will change once solution moves
 * to cassandra
 *
 */
@Service
public class DiffReportService {
	private static final Logger logger = LoggerFactory.getLogger(DiffReportController.class);

	private static final String LOAD_Q = "load data local infile '%s' into table %s LINES TERMINATED BY '\r\n' ignore 1 lines;";
	private static final String LOAD_REFSET_IDENTIFIER_Q = "insert into refsetids_replaceme (id, descrip) select conceptd, term from descr_releaseDate"
			+ " where conceptd in (select distinct refsetid  from octmapfull_replaceme) group by conceptd;"; 

	private CellStyle style_data = null;
	
	@Autowired
	private JdbcTemplate t;
	
	@Resource(name = "dropTables")
	Map<String, String> dropTables;

	@Resource(name = "createTables")
	Map<String, String> createTables;

	@Resource(name = "diffReport")
	String diffReport;

	/**
	 * @param file
	 * @param tName
	 */
	public void loadFileToTable(String file, String tName) {
		
		logger.debug("Adding file - {} data to db table - {} " , file, tName);
		
		String fQeury = String.format(LOAD_Q, file, tName);
		
		logger.debug("Adding file - query {}" , fQeury);

		t.execute(fQeury);
		
	}
	
	/**
	 * @param file
	 * @param refsetTName
	 */
	public void loadRefsetIdentifier(String suffix, String releaseDate) {
		
		String fQeury = LOAD_REFSET_IDENTIFIER_Q.replaceAll("releaseDate", releaseDate).replaceAll("replaceme", suffix);
		
		logger.debug("Populating refsetids_{} table with - query {}" , suffix, fQeury);

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
            CellStyle style = wb.createCellStyle();
            Font font = wb.createFont();
            font.setBoldweight(Font.BOLDWEIGHT_BOLD);
            font.setBold(true);
            font.setColor(Font.COLOR_NORMAL);
            font.setFontHeightInPoints((short) 15);
            font.setFontName("Ariel");
            style.setFont(font);
            style.setShrinkToFit(false);
            style.setWrapText(false);

            style_data = wb.createCellStyle();
            style_data.setShrinkToFit(false);
            style_data.setWrapText(false);

            
            java.util.Collections.sort(drs);
        	writeSheet_1(wb, drs, style);
        	writeSheet_2(wb, drs, style);

        	
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
	 * @param style 
	 * @param refsetIds
	 */
	private void writeSheet_2(Workbook wb, List<DiffReportRecord> drs, CellStyle style) {
		logger.debug("Writing diff report - sheet2 ");

		Sheet sheet = wb.createSheet("Sheet-2");
        int rowId = 0;
        //create column labels
        Row row = sheet.createRow(rowId++);
        addColumnLabels(row, style);

        List<String> processedCoceptRefset = new ArrayList<String>();
        Cell firstCell = null;
        Cell lastCell = null;

		for (DiffReportRecord dr : drs) {
			

			String processedId = dr.getConceptId()+dr.getReferenceComponentId();
			
			if (!processedCoceptRefset.contains(processedId)) {
				
				row = sheet.createRow(rowId++);

				processedCoceptRefset.add(processedId);
				
				Cell conceptId = row.createCell(0);
	            conceptId.setCellValue(dr.getConceptId());
	            conceptId.setCellStyle(style_data);

	            if (rowId == 2) {
					
	            	firstCell = conceptId;
	            	
				} else {
					
					lastCell = conceptId;
				}

	            
	            Cell conceptDescription = row.createCell(1);
	            conceptDescription.setCellValue(dr.getConceptDescription());
	            conceptDescription.setCellStyle(style_data);

	            Cell refsetId = row.createCell(2);
	            refsetId.setCellValue(dr.getRefsetId());
	            refsetId.setCellStyle(style_data);

	            Cell refsetDescription = row.createCell(3);
	            refsetDescription.setCellValue(dr.getRefsetDescription());
	            refsetDescription.setCellStyle(style_data);

	            Cell active = row.createCell(4);
	            active.setCellValue(dr.getActive());
	            active.setCellStyle(style_data);

	            Cell reasonCode = row.createCell(5);
	            reasonCode.setCellValue(dr.getReasonCode());
	            reasonCode.setCellStyle(style_data);

	            Cell reasonDescription = row.createCell(6);
	            reasonDescription.setCellValue(dr.getReasonDescription());
	            reasonDescription.setCellStyle(style_data);

	            Cell associationRefCode = row.createCell(7);
	            associationRefCode.setCellValue(dr.getAssociationRefCode());
	            associationRefCode.setCellStyle(style_data);

	            Cell associationDescription = row.createCell(8);
	            associationDescription.setCellValue(dr.getAssociationDescription());
	            associationDescription.setCellStyle(style_data);

	            Cell referenceComponentId = row.createCell(9);
	            referenceComponentId.setCellValue(dr.getReferenceComponentId());
	            referenceComponentId.setCellStyle(style_data);

	            Cell referenceComponentIdDescription = row.createCell(10);
	            referenceComponentIdDescription.setCellValue(dr.getReferenceComponentDescription());
	            referenceComponentIdDescription.setCellStyle(style_data);

			}
			


		}
		
		if (firstCell != null && lastCell != null) {
			
			logger.debug("Appllying Autofilter ");

	        sheet.setAutoFilter(new CellRangeAddress(firstCell.getRowIndex(), lastCell.getRowIndex(), firstCell.getColumnIndex(), lastCell.getColumnIndex()));

		}
		
		for (int i = 0; i < 11; i++) {
			
            sheet.autoSizeColumn(i);

		}
	
	}

	/**
	 * @param wb
	 * @param drs 
	 * @param style 
	 */
	private void writeSheet_1(Workbook wb, List<DiffReportRecord> drs, CellStyle style) {

		logger.debug("Writing diff report - sheet1 ");

		Sheet sheet = wb.createSheet("Sheet-1");
        
        int rowId = 0;
        //create column labels
        Row row = sheet.createRow(rowId++);
        addColumnLabels(row, style);
        
        Cell firstCell = null;
        Cell lastCell = null;

        for (DiffReportRecord dr : drs) {
			
            row = sheet.createRow(rowId++);
            
            
            Cell conceptId = row.createCell(0);
            conceptId.setCellStyle(style_data);
            conceptId.setCellValue(dr.getConceptId());
            
            if (rowId == 2) {
				
            	firstCell = conceptId;
            	
			} else {
				
				lastCell = conceptId;
			}
            
            Cell conceptDescription = row.createCell(1);
            conceptDescription.setCellValue(dr.getConceptDescription());
            conceptDescription.setCellStyle(style_data);

            Cell refsetId = row.createCell(2);
            refsetId.setCellValue(dr.getRefsetId());
            refsetId.setCellStyle(style_data);

            Cell refsetDescription = row.createCell(3);
            refsetDescription.setCellValue(dr.getRefsetDescription());
            refsetDescription.setCellStyle(style_data);

            Cell active = row.createCell(4);
            active.setCellValue(dr.getActive());
            active.setCellStyle(style_data);

            Cell reasonCode = row.createCell(5);
            reasonCode.setCellValue(dr.getReasonCode());
            reasonCode.setCellStyle(style_data);

            Cell reasonDescription = row.createCell(6);
            reasonDescription.setCellValue(dr.getReasonDescription());
            reasonDescription.setCellStyle(style_data);

            Cell associationRefCode = row.createCell(7);
            associationRefCode.setCellValue(dr.getAssociationRefCode());
            associationRefCode.setCellStyle(style_data);

            Cell associationDescription = row.createCell(8);
            associationDescription.setCellValue(dr.getAssociationDescription());
            associationDescription.setCellStyle(style_data);

            Cell referenceComponentId = row.createCell(9);
            referenceComponentId.setCellValue(dr.getReferenceComponentId());
            referenceComponentId.setCellStyle(style_data);

            Cell referenceComponentIdDescription = row.createCell(10);
            referenceComponentIdDescription.setCellValue(dr.getReferenceComponentDescription());
            referenceComponentIdDescription.setCellStyle(style_data);

		}
        
        if (firstCell != null && lastCell != null) {
			
	        sheet.setAutoFilter(new CellRangeAddress(firstCell.getRowIndex(), lastCell.getRowIndex(), firstCell.getColumnIndex(), lastCell.getColumnIndex()));

		}
        
        for (int i = 0; i < 11; i++) {
			
            sheet.autoSizeColumn(i);

		}
	}

	/**
	 * @param row
	 * @param style 
	 */
	private void addColumnLabels(Row row, CellStyle style) {

		logger.debug("Adding column lables diff report ");

		
		Cell conceptId = row.createCell(0);
		conceptId.setCellStyle(style);
        conceptId.setCellValue("Concept Id");
        
        Cell conceptDescription = row.createCell(1);
        conceptDescription.setCellStyle(style);
        conceptDescription.setCellValue("Concept Description");

        Cell refsetId = row.createCell(2);
        refsetId.setCellValue("Refset Id");
        refsetId.setCellStyle(style);

        Cell refsetDescription = row.createCell(3);
        refsetDescription.setCellValue("Refset Description");
        refsetDescription.setCellStyle(style);

        Cell active = row.createCell(4);
        active.setCellValue("Active");
        active.setCellStyle(style);

        Cell reasonCode = row.createCell(5);
        reasonCode.setCellValue("Reason Code");
        reasonCode.setCellStyle(style);

        Cell reasonDescription = row.createCell(6);
        reasonDescription.setCellValue("Reason Description");
        reasonDescription.setCellStyle(style);

        Cell associationRefCode = row.createCell(7);
        associationRefCode.setCellValue("Association Ref Code");
        associationRefCode.setCellStyle(style);

        Cell associationDescription = row.createCell(8);
        associationDescription.setCellValue("Association Description");
        associationDescription.setCellStyle(style);

        Cell referenceComponentId = row.createCell(9);
        referenceComponentId.setCellValue("Referenced Component Id");
        referenceComponentId.setCellStyle(style);

        Cell referenceComponentIdDescription = row.createCell(10);
        referenceComponentIdDescription.setCellValue("Referenced Component Description");
        referenceComponentIdDescription.setCellStyle(style);

	}
	
	
	
	public List<String> getRefsetIds(String suffix) {
		
		String qRefsetIds = "select distinct id from refsetids_replaceme".replaceAll("replaceme", suffix);

		logger.debug("Query {} ", qRefsetIds);

		return t.queryForList(qRefsetIds, String.class);
	}
	
	
}
