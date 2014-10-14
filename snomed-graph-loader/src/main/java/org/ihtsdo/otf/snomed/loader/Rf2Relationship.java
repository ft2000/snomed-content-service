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
package org.ihtsdo.otf.snomed.loader;


/**
 *
 */
public class Rf2Relationship extends Rf2Base {
	
	private String sourceId;
	private String destinationId;
	private String relationshipGroup;
	private String typeId;
	private String characteristicTypeId;
	private String modifierId;
	

	/**
	 * @return the sourceId
	 */
	public String getSourceId() {
		return sourceId;
	}
	/**
	 * @param sourceId the sourceId to set
	 */
	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}
	/**
	 * @return the destinationId
	 */
	public String getDestinationId() {
		return destinationId;
	}
	/**
	 * @param destinationId the destinationId to set
	 */
	public void setDestinationId(String destinationId) {
		this.destinationId = destinationId;
	}
	/**
	 * @return the relationshipGroup
	 */
	public String getRelationshipGroup() {
		return relationshipGroup;
	}
	/**
	 * @param relationshipGroup the relationshipGroup to set
	 */
	public void setRelationshipGroup(String relationshipGroup) {
		this.relationshipGroup = relationshipGroup;
	}
	/**
	 * @return the typeId
	 */
	public String getTypeId() {
		return typeId;
	}
	/**
	 * @param typeId the typeId to set
	 */
	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}
	/**
	 * @return the characteristicTypeId
	 */
	public String getCharacteristicTypeId() {
		return characteristicTypeId;
	}
	/**
	 * @param characteristicTypeId the characteristicTypeId to set
	 */
	public void setCharacteristicTypeId(String characteristicTypeId) {
		this.characteristicTypeId = characteristicTypeId;
	}
	/**
	 * @return the modifierId
	 */
	public String getModifierId() {
		return modifierId;
	}
	/**
	 * @param modifierId the modifierId to set
	 */
	public void setModifierId(String modifierId) {
		this.modifierId = modifierId;
	}


}
