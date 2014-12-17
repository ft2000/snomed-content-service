package org.ihtsdo.otf.refset;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class CorsFilter implements Filter {
	
	private static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
	private static final String ACCESS_CONTROL_EXPOSE_HEADERS = "Access-Control-Expose-Headers";
	
	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		
		HttpServletResponse response = (HttpServletResponse) res;
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods",
				"POST, GET, OPTIONS, DELETE");
		response.setHeader("Access-Control-Max-Age", "3600");
		response.addHeader(ACCESS_CONTROL_ALLOW_HEADERS, "x-requested-with");
		response.addHeader(ACCESS_CONTROL_ALLOW_HEADERS, "X-REFSET-PRE-AUTH-USERNAME");
		response.addHeader(ACCESS_CONTROL_ALLOW_HEADERS, "X-REFSET-PRE-AUTH-TOKEN");
		response.addHeader(ACCESS_CONTROL_ALLOW_HEADERS, "Content-Type");
		response.addHeader(ACCESS_CONTROL_EXPOSE_HEADERS, "Content-Disposition");
		response.addHeader(ACCESS_CONTROL_ALLOW_HEADERS, "X-REQ-TIME");
		response.addHeader(ACCESS_CONTROL_ALLOW_HEADERS, "X-REFSET-AUTH-TOKEN");
		response.addHeader(ACCESS_CONTROL_EXPOSE_HEADERS, "X-REFSET-AUTH-TOKEN");//need to send too

		chain.doFilter(req, res);

	}

	@Override
	public void init(FilterConfig arg0In) throws ServletException {
		// TODO Auto-generated method stub

	}


}
