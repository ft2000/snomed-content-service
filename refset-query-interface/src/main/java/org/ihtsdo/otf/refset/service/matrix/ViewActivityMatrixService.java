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

import java.util.List;

import org.ihtsdo.otf.refset.domain.RefsetDTO;
import org.ihtsdo.otf.refset.exception.RefsetServiceException;
import org.ihtsdo.otf.refset.graph.RefsetGraphAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *Service to collect matrix of user action like export, view etc
 */
@Service
public class ViewActivityMatrixService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ViewActivityMatrixService.class);
	
	private ViewMatrixGAO mxGao;
	
	
	/**Retrieves 
	 * @return
	 * @throws RefsetServiceException
	 */
	public List<RefsetDTO> getMostViewedPublishedRefsets(int noOfResults) throws RefsetServiceException {

		try {
			
			return mxGao.getMostViewedPublishedRefsets(noOfResults);
			
		} catch (RefsetGraphAccessException e) {

			LOGGER.error("Error while getting most viewed published refset", e);
			throw new RefsetServiceException(e);
		}
		
	}

	@Autowired
	public void setViewMatrixGao(ViewMatrixGAO gao) {
		
		this.mxGao = gao;
	}
}
