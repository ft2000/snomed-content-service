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

import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

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
	
	@Autowired
	private JdbcTemplate t;
	
	@Resource(name = "dropTables")
	Map<String, String> dropTables;

	@Resource(name = "createTables")
	Map<String, String> createTables;

	@Resource(name = "diffReport")
	String diffReport;
	
	@Autowired
	private DiffReportWriter rWriter;

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

		rWriter.writeDiffReport(drs, os);
	}
	
	
	
	public List<String> getRefsetIds(String suffix) {
		
		String qRefsetIds = "select distinct id from refsetids_replaceme".replaceAll("replaceme", suffix);

		logger.debug("Query {} ", qRefsetIds);

		return t.queryForList(qRefsetIds, String.class);
	}
	
	
}
