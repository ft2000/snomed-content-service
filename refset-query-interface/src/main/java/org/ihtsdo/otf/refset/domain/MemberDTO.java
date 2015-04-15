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

/**
 *
 */
public class MemberDTO extends Member {
	
	//to indicate if member has been published any time.
	private Integer memberHasPublishedState;
	//to indicate member has pending publish details
	private Integer memberHasPendingEdit;
	//to indicate member has any published state history
	private Integer memberHasPublishedStateHistory;
	/**
	 * @return the memberHasPublishedState
	 */
	public Integer getMemberHasPublishedState() {
		return memberHasPublishedState;
	}
	/**
	 * @param memberHasPublishedState the memberHasPublishedState to set
	 */
	public void setMemberHasPublishedState(Integer memberHasPublishedState) {
		this.memberHasPublishedState = memberHasPublishedState;
	}
	/**
	 * @return the memberHasPendingEdit
	 */
	public Integer getMemberHasPendingEdit() {
		return memberHasPendingEdit;
	}
	/**
	 * @param memberHasPendingEdit the memberHasPendingEdit to set
	 */
	public void setMemberHasPendingEdit(Integer memberHasPendingEdit) {
		this.memberHasPendingEdit = memberHasPendingEdit;
	}
	
	/**
	 * @return the memberHasPublishedStateHistory
	 */
	public Integer getMemberHasPublishedStateHistory() {
		return memberHasPublishedStateHistory;
	}
	/**
	 * @param memberHasPublishedStateHistory the memberHasPublishedStateHistory to set
	 */
	public void setMemberHasPublishedStateHistory(
			Integer memberHasPublishedStateHistory) {
		this.memberHasPublishedStateHistory = memberHasPublishedStateHistory;
	}

}
