package com.wcs.ncp.common.filter;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class ConversationExceptionFilter implements Filter {

	@Override
	public void destroy() {

	}
	

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException{
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		Map<String, String[]> params=httpRequest.getParameterMap();
		if(params.containsKey("cid")){
			HttpServletRequestWrapper wrapper = new ConversationAwareRequestWrapper(httpRequest);
			String context = httpRequest.getContextPath();
			String viewId = httpRequest.getRequestURI();
			String path = viewId.replaceFirst(context, "");
			httpRequest.getRequestDispatcher(path).forward(wrapper, response);
		}else{
			chain.doFilter(request, response);	
		}
	}
	
	
	@Override
	public void init(FilterConfig config) throws ServletException {

	}
	
	
	
	
	private class ConversationAwareRequestWrapper extends HttpServletRequestWrapper {

		public ConversationAwareRequestWrapper(HttpServletRequest request) {
			super(request);
		}

		@Override
		public String getParameter(String name) {
			if ("cid".equals(name)) {
				return null;
			}
			return super.getParameter(name);
		}

		@Override
		public Map<String, String[]> getParameterMap() {
			Map<String, String[]> map = super.getParameterMap();
			map.remove("cid");
			return map;
		}

		@Override
		public Enumeration<String> getParameterNames() {
			return Collections.enumeration(getParameterMap().keySet());
		}

	}
	
	

}
