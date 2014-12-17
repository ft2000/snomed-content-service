/**
 * 
 */
package org.ihtsdo.otf.refset.security;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ihtsdo.otf.refset.common.Meta;
import org.ihtsdo.otf.refset.common.Result;
import org.ihtsdo.otf.refset.error.ErrorInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Episteme Partners
 *
 */
public class RefsetAccessDeniedHandler implements AccessDeniedHandler {

	@Override
	public void handle(HttpServletRequest request,
			HttpServletResponse res,
			AccessDeniedException e) throws IOException,
			ServletException {
		// TODO Auto-generated method stub
		
	    res.setContentType(MediaType.APPLICATION_JSON_VALUE);
		Result<Map<String, Object>> result = new Result<Map<String, Object>>();
		Meta meta = new Meta();
		meta.setMessage(e.getMessage());
		meta.setStatus(HttpStatus.UNAUTHORIZED);
		
		ErrorInfo errorInfo = new ErrorInfo(e.getMessage(), Integer.toString(org.apache.commons.httpclient.HttpStatus.SC_UNAUTHORIZED));
		meta.setErrorInfo(errorInfo);

		result.setMeta(meta);
		
		
        PrintWriter out = res.getWriter();
        
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(Include.NON_EMPTY);
        out.print(mapper.writeValueAsString(result));
        out.close();

		
	}

}
