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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

/** 
 * @author Episteme Partners
 *
 */
public class RefsetAuthenticationEntryPoint implements AuthenticationEntryPoint {
	
	private String preAuthTokenKey;
	private String userKey;

	/**
	 * @param userKey the userKey to set
	 */
	public void setUserKey(String userKey) {
		this.userKey = userKey;
	}

	/**
	 * @param preAuthTokenKey the preAuthTokenKey to set
	 */
	public void setPreAuthTokenKey(String preAuthTokenKey) {
		this.preAuthTokenKey = preAuthTokenKey;
	}

	@Override
	public void commence(HttpServletRequest req,
			HttpServletResponse res, AuthenticationException e)
			throws IOException, ServletException {
		
	    res.setContentType(MediaType.APPLICATION_JSON_VALUE);
		Result<Map<String, Object>> result = new Result<Map<String, Object>>();
		Meta meta = new Meta();
		meta.setMessage(e.getMessage());
		meta.setStatus(HttpStatus.UNAUTHORIZED);
		result.setMeta(meta);
		
		
        PrintWriter out = res.getWriter();
        
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(Include.NON_EMPTY);
        out.print(mapper.writeValueAsString(result));
        out.close();
	}

}
