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
package org.ihtsdo.otf.refset.common;

import org.ihtsdo.otf.refset.domain.RGC;

/**
 *This enum is select case of {@link RGC} constants and can not contain any value other than defined constant in {@link RGC}
 */
public enum SearchField {

	publishedDate, languageCode, createdBy, modifiedDate, clinicalDomain, published
}
