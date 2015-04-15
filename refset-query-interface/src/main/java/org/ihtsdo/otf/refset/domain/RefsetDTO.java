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
package org.ihtsdo.otf.refset.domain;

import java.util.Collections;
import java.util.List;

/**
 *
 */
public class RefsetDTO extends Refset {
	
	private Matrix matrix = new Matrix();
	private long totalNoOfMembers;
	
	private List<MemberDTO> members;
	
	
	
	/**
	 * @return the members
	 */
	public List<MemberDTO> getMembers() {
		
		if(members == null) {
			
			members = Collections.emptyList();
			
		}
		return members;
	}

	/**
	 * @param members the members to set
	 */
	public void setMembers(List<MemberDTO> members) {
		this.members = members;
	}


	/**
	 * @return the totalNoOfMembers
	 */
	public long getTotalNoOfMembers() {
		return totalNoOfMembers;
	}

	/**
	 * @param totalNoOfMembers the totalNoOfMembers to set
	 */
	public void setTotalNoOfMembers(long totalNoOfMembers) {
		this.totalNoOfMembers = totalNoOfMembers;
	}
	
	/**
	 * @return the matrix
	 */
	public Matrix getMatrix() {
		return matrix;
	}

	/**
	 * @param matrix the matrix to set
	 */
	public void setMatrix(Matrix matrix) {
		this.matrix = matrix;
	}
	
	@Override
	   public String toString() {
		   
		   return String.format( "Refset [id - %s, created - %s, createdBy - %s, description - %s, "
		   		+ "effectiveTime - %s,  isPublished - %s, languageCode - %s, members - %s, moduleId - %s, publishedDate - %s "
		   		+ "superRefsetTypeId - %s, type - %s, typeId - %s, description - %s, latestEffectiveTime - %s,"
		   		+ " earliestEffectiveTime -%s, scope - %s, snomedCTExtension - %s, snomedCTVersion - %s,"
		   		+ " contributingOrganization - %s, originCountry - %s, implementationDetails - %s, clinicalDomain - %s,"
		   		+ " clinicalDomainCode - %s, snomedCTExtensionNs - %s, originCountryCode - %s, externalUrl - %s, externalContact - %s "
		   		+ "]", this.uuid, this.getCreated(), this.getCreatedBy(), this.description, this.effectiveTime, this.published,
		   		this.languageCode, this.members, this.moduleId, this.publishedDate, this.getSuperRefsetTypeId(), this.getType(),
		   		this.getTypeId(), this.getDescription(), this.getEarliestEffectiveTime(), this.getLatestEffectiveTime(), this.getScope(),
		   		this.getSnomedCTExtension(), this.getSnomedCTVersion(), this.getContributingOrganization(), this.getOriginCountry(),
		   		this.getImplementationDetails(), this.getClinicalDomain(), this.getClinicalDomainCode(), this.getSnomedCTExtensionNs(),
		   		this.getOriginCountryCode(), this.getExternalUrl(), this.getExternalContact());
	   }

}
