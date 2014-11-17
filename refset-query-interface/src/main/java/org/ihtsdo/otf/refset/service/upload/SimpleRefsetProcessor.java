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
package org.ihtsdo.otf.refset.service.upload;

import java.util.List;
import java.util.Map;

import org.ihtsdo.otf.refset.exception.EntityNotFoundException;
import org.ihtsdo.otf.refset.exception.RefsetServiceException;
import org.ihtsdo.otf.refset.graph.RefsetGraphAccessException;
import org.ihtsdo.otf.refset.graph.gao.RefsetAdminGAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 *
 */
@Service(value="simpleRefsetProcessor")
public class SimpleRefsetProcessor implements RefsetProcessor {
	
	private RefsetAdminGAO gao;

	/* (non-Javadoc)
	 * @see org.ihtsdo.otf.refset.service.upload.RefsetProcessor#process(java.util.List)
	 */
	@Override
	public Map<String, String> process(List<Rf2Record> rf2rLst, String refsetId) throws RefsetServiceException, EntityNotFoundException {
		
		if (StringUtils.isEmpty(refsetId) || CollectionUtils.isEmpty(rf2rLst)) {
			
			throw new RefsetServiceException("Not enough data to process. please check your request and imported file");
		}
		
		try {
			
			return gao.addMembers(rf2rLst, refsetId);
			
		} catch (RefsetGraphAccessException e) {

			throw new RefsetServiceException(e);
		}
		
	}

	/**
	 * @param gao the gao to set
	 */
	@Autowired
	public void setGao(RefsetAdminGAO gao) {
		this.gao = gao;
	}


}
