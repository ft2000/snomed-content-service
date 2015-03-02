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
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.ihtsdo.otf.refset.exception.RefsetServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**Write a excel format diff report to provided output stream
 *
 */
@Component
public class DiffReportWriter {
	private static final Logger logger = LoggerFactory.getLogger(DiffReportWriter.class);
	
	private CellStyle style_data = null;	
	
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
        	writeSheet_2(wb, drs, style);//this may be redundant and can be removed if we are generating report with the help of Term Server

        	
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
			

			String processedId = dr.getConceptId() + dr.getRefsetId() + dr.getReferenceComponentId();
			
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
	
	
}
