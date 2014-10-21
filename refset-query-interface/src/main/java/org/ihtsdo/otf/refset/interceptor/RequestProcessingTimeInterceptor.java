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
package org.ihtsdo.otf.refset.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 */
public class RequestProcessingTimeInterceptor implements HandlerInterceptor {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RequestProcessingTimeInterceptor.class);
	private static final String START_TIME = "sTime";
	private static final String TOTAL_TIME = "X-REQ-TIME";
	
	private boolean isEnabled = false;

	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.HandlerInterceptor#afterCompletion(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, java.lang.Exception)
	 */
	@Override
	public void afterCompletion(HttpServletRequest req,
			HttpServletResponse res, Object arg2, Exception e)
			throws Exception {
		
		if (isEnabled) {

			LOGGER.debug("afterCompletion");

			String tTime = Long.toString((System.currentTimeMillis() - (Long) req.getAttribute(START_TIME))/1000);
			LOGGER.debug("Total time taken in this request is {}", tTime);

		}
	}

	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.HandlerInterceptor#postHandle(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.web.servlet.ModelAndView)
	 */
	@Override
	public void postHandle(HttpServletRequest req, HttpServletResponse res,
			Object arg2, ModelAndView model) throws Exception {
		
		if (isEnabled) {
		
			String tTime = Long.toString((System.currentTimeMillis() - (Long) req.getAttribute(START_TIME))/1000);
			LOGGER.debug("Total time taken in this request is {}", tTime);
			res.addHeader(TOTAL_TIME, tTime);
		}

	}

	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.HandlerInterceptor#preHandle(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object)
	 */
	@Override
	public boolean preHandle(HttpServletRequest req, HttpServletResponse res,
			Object arg2) throws Exception {

		
		if (isEnabled) {
			
			LOGGER.debug("preHandle");

			req.setAttribute(START_TIME, System.currentTimeMillis());
			
		}
		return true;
	}

	/**
	 * @param isEnabled the isEnabled to set
	 */
	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

}
