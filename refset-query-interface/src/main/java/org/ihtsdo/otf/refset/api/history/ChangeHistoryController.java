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
package org.ihtsdo.otf.refset.api.history;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.HashMap;
import java.util.Map;

import org.ihtsdo.otf.refset.common.Meta;
import org.ihtsdo.otf.refset.common.Result;
import org.ihtsdo.otf.refset.domain.ChangeRecord;
import org.ihtsdo.otf.refset.domain.Member;
import org.ihtsdo.otf.refset.domain.Refset;
import org.ihtsdo.otf.refset.service.RefsetChangeHistoryService;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

/**
 *
 */
@RestController
@Api(value="RefsetChangeHistory", description="Service to get refset change history overtime", position = 5)
@RequestMapping("/v1.0/refsets")
public class ChangeHistoryController {
	
	private static final Logger logger = LoggerFactory.getLogger(ChangeHistoryController.class);

	private static final String SUCCESS = "Success";
	
	private static final DateTimeFormatter FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd");

	@Autowired
	private RefsetChangeHistoryService service;
	
	@RequestMapping( method = RequestMethod.GET, value = "/{refsetId}/member/{memberId}/history",  produces = "application/json", consumes = "application/json")
	@ApiOperation( value = "Get a member history ",
			notes = "This api call is to get last 10 days of history of given members under given refset and for given range. "
					+ "It is sorted by latest first."
				+ "If user of this api want to retrieve specific dates history, they should provide from date and to date"
				+ "in the format of yyyy-mm-dd")
	@PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Result< Map<String, Object>>> getMemberHistory( @PathVariable String refsetId, 
    		@PathVariable String memberId,
    		@RequestParam( value = "fromDate", required = false) String fromDate, 
    		@RequestParam( value = "toDate", required = false) String toDate,
    		@RequestParam( value = "from", defaultValue = "0", required= false ) int from, 
    		@RequestParam( value = "to", defaultValue = "10", required = false) int to) throws Exception {
		
		logger.debug("getting member history {}", refsetId, memberId);

		Result<Map<String, Object>> r = getResult();

		ChangeRecord<Member> history = service.getMemberHistory(refsetId, memberId, getFromDate(fromDate, 10), getToDate(toDate), from, to);
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("history", history);
		r.getMeta().add( linkTo( methodOn( ChangeHistoryController.class ).getMemberHistory(refsetId, memberId,
				fromDate, toDate, from, to)).withRel("Member History"));

		r.setData(data);

		r.getMeta().setMessage(SUCCESS);
		r.getMeta().setStatus(HttpStatus.OK);

		return new ResponseEntity<Result<Map<String,Object>>>(r, HttpStatus.OK);
    }
	
	@RequestMapping( method = RequestMethod.GET, value = "/{refsetId}/history",  produces = "application/json", consumes = "application/json")
	@ApiOperation( value = "Get all members history under a refset ",
			notes = "This api call is to get last 10 days of history of all members under given refset and for given range of members. "
					+ "It is sorted by latest first."
				+ "If user of this api want to retrieve specific dates history, they should provide from date and to date"
				+ "in the format of yyyy-mm-dd")
	@PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Result< Map<String, Object>>> getAllMembersHistory( @PathVariable String refsetId,
    		@RequestParam( value = "fromDate", required = false) String fromDate, 
    		@RequestParam( value = "toDate", required = false) String toDate,
    		@RequestParam( value = "from", defaultValue = "0", required= false ) int from, 
    		@RequestParam( value = "to", defaultValue = "10", required = false) int to ) throws Exception {
		
		logger.debug("getting all members history {}", refsetId);

		Result<Map<String, Object>> r = getResult();
		
		Map<String, ChangeRecord<Member>> history = service.getAllMembersHistory(refsetId, getFromDate(fromDate, 10), getToDate(toDate), from, to);
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("history", history);
		r.getMeta().add( linkTo( methodOn( ChangeHistoryController.class ).getAllMembersHistory(refsetId, fromDate,
				toDate, from, to)).withRel("All Member History"));

		r.setData(data);

		r.getMeta().setMessage(SUCCESS);
		r.getMeta().setStatus(HttpStatus.OK);

		return new ResponseEntity<Result<Map<String,Object>>>(r, HttpStatus.OK);
    }
	
	@RequestMapping( method = RequestMethod.GET, value = "/{refsetId}/headerHistory",  produces = "application/json", consumes = "application/json")
	@ApiOperation( value = "Get a refset header history", 
		notes = "This api call retrieves refset header history by default for last 10 days. It is sorted by latest first."
				+ "If user of this api want to retrieve specific dates history, they should provide from date and to date"
				+ "in the format of yyyy-mm-dd" )
	@PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Result< Map<String, Object>>> getRefseHeaderHistory( @PathVariable String refsetId,
    		@RequestParam( value = "fromDate", required = false) String fromDate, 
    		@RequestParam( value = "toDate", required = false) String toDate,
    		@RequestParam( value = "from", defaultValue = "0", required= false ) int from, 
    		@RequestParam( value = "to", defaultValue = "10", required = false) int to) throws Exception {
		
		logger.debug("geting refset header history {}", refsetId);

		Result<Map<String, Object>> r = getResult();

		ChangeRecord<Refset> history = service.getRefsetHeaderHistory(refsetId, getFromDate(fromDate, 10), getToDate(toDate), from, to);
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("history", history);
		r.getMeta().add( linkTo( methodOn( ChangeHistoryController.class ).getRefseHeaderHistory(refsetId, fromDate,
				toDate, from, to)).withRel("Refset header history"));

		r.setData(data);

		r.getMeta().setMessage(SUCCESS);
		r.getMeta().setStatus(HttpStatus.OK);

		return new ResponseEntity<Result<Map<String,Object>>>(r, HttpStatus.OK);
    }
	
	
	
	@RequestMapping( method = RequestMethod.GET, value = "/{refsetId}/member/{memberId}/state",  produces = "application/json", consumes = "application/json")
	@ApiOperation( value = "Get a member history ",
			notes = "This api call is to get last 200 days of  state history of given members under given refset and for given range. "
					+ "It is sorted by latest first."
				+ "If user of this api want to retrieve specific dates history, they should provide from date and to date"
				+ "in the format of yyyy-mm-dd")
	@PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Result< Map<String, Object>>> getMemberStateHistory( @PathVariable String refsetId, 
    		@PathVariable String memberId,
    		@RequestParam( value = "fromDate", required = false) String fromDate, 
    		@RequestParam( value = "toDate", required = false) String toDate,
    		@RequestParam( value = "from", defaultValue = "0", required= false ) int from, 
    		@RequestParam( value = "to", defaultValue = "10", required = false) int to) throws Exception {
		
		logger.debug("getting member history {}", refsetId, memberId);

		Result<Map<String, Object>> r = getResult();

		ChangeRecord<Member> history = service.getMemberStateHistory(refsetId, memberId, getFromDate(fromDate, 200), getToDate(toDate), from, to);
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("history", history);
		r.getMeta().add( linkTo( methodOn( ChangeHistoryController.class ).getMemberStateHistory(refsetId, memberId,
				fromDate, toDate, from, to)).withRel("Member State History"));

		r.setData(data);

		r.getMeta().setMessage(SUCCESS);
		r.getMeta().setStatus(HttpStatus.OK);

		return new ResponseEntity<Result<Map<String,Object>>>(r, HttpStatus.OK);
    }
	
	@RequestMapping( method = RequestMethod.GET, value = "/{refsetId}/state",  produces = "application/json", consumes = "application/json")
	@ApiOperation( value = "Get all members state history under a refset ",
			notes = "This api call is to get last 200 days of state history of all members under given refset and for given range of members. "
					+ "It is sorted by latest first."
				+ "If user of this api want to retrieve specific dates history, they should provide from date and to date"
				+ "in the format of yyyy-mm-dd")
	@PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Result< Map<String, Object>>> getAllMembersStateHistory( @PathVariable String refsetId,
    		@RequestParam( value = "fromDate", required = false) String fromDate, 
    		@RequestParam( value = "toDate", required = false) String toDate,
    		@RequestParam( value = "from", defaultValue = "0", required= false ) int from, 
    		@RequestParam( value = "to", defaultValue = "10", required = false) int to ) throws Exception {
		
		logger.debug("getting all members history {}", refsetId);

		Result<Map<String, Object>> r = getResult();
		
		Map<String, ChangeRecord<Member>> history = service.getAllMembersStateHistory(refsetId, getFromDate(fromDate, 200), getToDate(toDate), from, to);
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("history", history);
		r.getMeta().add( linkTo( methodOn( ChangeHistoryController.class ).getAllMembersStateHistory(refsetId, fromDate,
				toDate, from, to)).withRel("All Member state History"));

		r.setData(data);

		r.getMeta().setMessage(SUCCESS);
		r.getMeta().setStatus(HttpStatus.OK);

		return new ResponseEntity<Result<Map<String,Object>>>(r, HttpStatus.OK);
    }
	
	@RequestMapping( method = RequestMethod.GET, value = "/{refsetId}/headerState",  produces = "application/json", consumes = "application/json")
	@ApiOperation( value = "Get a refset header state history", 
		notes = "This api call retrieves refset header state history by default for last 200 days. It is sorted by latest first."
				+ "If user of this api want to retrieve specific dates history, they should provide from date and to date"
				+ "in the format of yyyy-mm-dd" )
	@PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Result< Map<String, Object>>> getRefseHeaderStateHistory( @PathVariable String refsetId,
    		@RequestParam( value = "fromDate", required = false) String fromDate, 
    		@RequestParam( value = "toDate", required = false) String toDate,
    		@RequestParam( value = "from", defaultValue = "0", required= false ) int from, 
    		@RequestParam( value = "to", defaultValue = "10", required = false) int to) throws Exception {
		
		logger.debug("geting refset header history {}", refsetId);

		Result<Map<String, Object>> r = getResult();

		ChangeRecord<Refset> history = service.getRefseHeaderStateHistory(refsetId, getFromDate(fromDate, 200), getToDate(toDate), from, to);
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("history", history);
		r.getMeta().add( linkTo( methodOn( ChangeHistoryController.class ).getRefseHeaderStateHistory(refsetId, fromDate,
				toDate, from, to)).withRel("Refset header state history"));

		r.setData(data);

		r.getMeta().setMessage(SUCCESS);
		r.getMeta().setStatus(HttpStatus.OK);

		return new ResponseEntity<Result<Map<String,Object>>>(r, HttpStatus.OK);
    }

	
	/**To convert string date to {@link DateTime}
	 * @param fromDate
	 * @return
	 */
	private DateTime getFromDate(String fromDate, int daysOffset) {
		
		DateTime fromDt = new DateTime().minusDays(daysOffset);
		if (!StringUtils.isEmpty(fromDate)) {
			
			fromDt = FORMATTER.parseDateTime(fromDate);
		}
		
		return fromDt;
	}
	
	/**To convert string date to {@link DateTime}
	 * @param toDate
	 * @return
	 */
	private DateTime getToDate(String toDate) {
		
		DateTime toDt = new DateTime();
		if (!StringUtils.isEmpty(toDate)) {
			
			toDt = FORMATTER.parseDateTime(toDate);
		}
		
		return toDt;

	}
	
	/**{@link Result} container for response
	 * @return
	 */
	private Result<Map<String, Object>> getResult() {
		Result<Map<String, Object>> result = new Result<Map<String, Object>>();
		Meta m = new Meta();
		result.setMeta(m);
		return result;
	}

}
