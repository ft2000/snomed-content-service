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

/**
 *
 */
public class DiffReportRecord implements Comparable<DiffReportRecord>{

	/**
	 * Concept ID
	 */
	private String conceptId;
	
	private String conceptDescription;
	
	private String refsetId;
	
	private String refsetDescription;
	
	private String active;
	
	private String reasonCode;
	
	private String reasonDescription;
	
	private String associationRefCode;
	
	private String associationDescription;
	
	private String referenceComponentId;
	
	private String referenceComponentDescription;

	/**
	 * @return the conceptId
	 */
	public String getConceptId() {
		return conceptId;
	}

	/**
	 * @param conceptId the conceptId to set
	 */
	public void setConceptId(String conceptId) {
		this.conceptId = conceptId;
	}

	/**
	 * @return the conceptDescription
	 */
	public String getConceptDescription() {
		return conceptDescription;
	}

	/**
	 * @param conceptDescription the conceptDescription to set
	 */
	public void setConceptDescription(String conceptDescription) {
		this.conceptDescription = conceptDescription;
	}

	/**
	 * @return the refsetId
	 */
	public String getRefsetId() {
		return refsetId;
	}

	/**
	 * @param refsetId the refsetId to set
	 */
	public void setRefsetId(String refsetId) {
		this.refsetId = refsetId;
	}

	/**
	 * @return the refsetDescription
	 */
	public String getRefsetDescription() {
		return refsetDescription;
	}

	/**
	 * @param refsetDescription the refsetDescription to set
	 */
	public void setRefsetDescription(String refsetDescription) {
		this.refsetDescription = refsetDescription;
	}

	/**
	 * @return the active
	 */
	public String getActive() {
		return active;
	}

	/**
	 * @param active the active to set
	 */
	public void setActive(String active) {
		this.active = active;
	}

	/**
	 * @return the reasonCode
	 */
	public String getReasonCode() {
		return reasonCode;
	}

	/**
	 * @param reasonCode the reasonCode to set
	 */
	public void setReasonCode(String reasonCode) {
		this.reasonCode = reasonCode;
	}

	/**
	 * @return the reasonDescription
	 */
	public String getReasonDescription() {
		return reasonDescription;
	}

	/**
	 * @param reasonDescription the reasonDescription to set
	 */
	public void setReasonDescription(String reasonDescription) {
		this.reasonDescription = reasonDescription;
	}

	/**
	 * @return the associationRefCode
	 */
	public String getAssociationRefCode() {
		return associationRefCode;
	}

	/**
	 * @param associationRefCode the associationRefCode to set
	 */
	public void setAssociationRefCode(String associationRefCode) {
		this.associationRefCode = associationRefCode;
	}

	/**
	 * @return the associationDescription
	 */
	public String getAssociationDescription() {
		return associationDescription;
	}

	/**
	 * @param associationDescription the associationDescription to set
	 */
	public void setAssociationDescription(String associationDescription) {
		this.associationDescription = associationDescription;
	}

	/**
	 * @return the referenceComponentId
	 */
	public String getReferenceComponentId() {
		return referenceComponentId;
	}

	/**
	 * @param referenceComponentId the referenceComponentId to set
	 */
	public void setReferenceComponentId(String referenceComponentId) {
		this.referenceComponentId = referenceComponentId;
	}

	/**
	 * @return the referenceComponentDescription
	 */
	public String getReferenceComponentDescription() {
		return referenceComponentDescription;
	}

	/**
	 * @param referenceComponentDescription the referenceComponentDescription to set
	 */
	public void setReferenceComponentDescription(
			String referenceComponentDescription) {
		this.referenceComponentDescription = referenceComponentDescription;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(DiffReportRecord o) {
		// TODO Auto-generated method stub
		return new Long(this.refsetId).compareTo(new Long(o.refsetId));
	}


	

}
