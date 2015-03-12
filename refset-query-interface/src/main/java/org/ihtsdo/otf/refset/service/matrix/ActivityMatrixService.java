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
package org.ihtsdo.otf.refset.service.matrix;

import org.ihtsdo.otf.refset.domain.RefsetRelations;
import org.ihtsdo.otf.refset.graph.RefsetGraphAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

/**
 *Service to collect matrix of user action like export, view etc
 */
@EnableAsync
@Service
public class ActivityMatrixService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ActivityMatrixService.class);
	
	private MatrixGAO mxGao;
	
	
	@Async
	public void addViewEvent(String refsetId, String userName) {
		
		try {
			
			mxGao.addMatrixEvent(refsetId, userName, RefsetRelations.viewed);
			
		} catch (RefsetGraphAccessException e) {
			
			LOGGER.error("Error while logging user view event", e);
			
		}
	}
	
	@Async
	public void addExportEvent(String refsetId, String userName) {
		
		try {
			
			mxGao.addMatrixEvent(refsetId, userName, RefsetRelations.downloaded);
			
		} catch (RefsetGraphAccessException e) {
			
			LOGGER.error("Error while logging user view event", e);
			
		}
	}

	@Autowired
	public void setMatrixGao(MatrixGAO gao) {
		
		this.mxGao = gao;
	}
}
