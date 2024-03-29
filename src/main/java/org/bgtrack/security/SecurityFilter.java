package org.bgtrack.security;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bgtrack.utils.PropertiesLoader;

import PropertiesSelection.PropertiesSelector;

public class SecurityFilter implements Filter {
	
	private static String CSP_HEADER;
	private static String CSP_VALUE;
	
	public static final String X_FORWARDED_PROTO = "X-Forwarded-Proto";

	private static final Logger LOG = LogManager.getLogger(SecurityFilter.class);
	
	public void init(FilterConfig filterConfig) throws ServletException {
		
		try {
			
			CSP_HEADER = PropertiesLoader.getPropertyValue("CSP_HEADER", PropertiesSelector.SECURITY);
			
			String deafultSrc = PropertiesLoader.getPropertyValue("CSP_DEFAULT_SRC", PropertiesSelector.SECURITY);
			String scriptSrc = PropertiesLoader.getPropertyValue("CSP_SCRIPT_SRC", PropertiesSelector.SECURITY);
			String styleSrc = PropertiesLoader.getPropertyValue("CSP_STYLE_SRC", PropertiesSelector.SECURITY);
			String objectSrc = PropertiesLoader.getPropertyValue("CSP_OBJECT_SRC", PropertiesSelector.SECURITY);
			String imgSrc = PropertiesLoader.getPropertyValue("CSP_IMG_SRC", PropertiesSelector.SECURITY);
			
			CSP_VALUE = deafultSrc + scriptSrc + styleSrc + objectSrc + imgSrc;
			
		} catch (IOException e) {
			LOG.error("ERROR: failed to load Content Security Policy data!");
			CSP_HEADER = null;
			CSP_VALUE = null;
		}
		
	}

	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		
		if (enforceTLS(request, response)) {
			return; // redirect to https, do not continue the filter chain
		}
		
		addCSPHeader(response);
		
		inputValidation(request);
		
		filterChain.doFilter(request, response);
	}

	private boolean enforceTLS(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		if (request.getHeader(X_FORWARDED_PROTO) != null) {
			if (request.getHeader(X_FORWARDED_PROTO).indexOf("https") != 0) {
				String pathInfo = (request.getPathInfo() != null) ? request.getPathInfo() : "";
				response.sendRedirect("https://" + request.getServerName() + pathInfo);
				return true;
			}
		}
		
		return false;
		
	}
	
	private void inputValidation(HttpServletRequest request) throws ServletException {
		
		Map<String, String[]> params = request.getParameterMap();
		
		for (Map.Entry<String, String[]> param : params.entrySet()) {
			
			String paramRule = null;
			
			try {
				
				paramRule = PropertiesLoader.getPropertyValue(param.getKey(), PropertiesSelector.SECURITY);
			
			} catch (IOException e) {
				
				LOG.error("Failed to load input validation rule for param: " + param.getKey(), e);
				
			}
			
			if (!isParameterValid(param.getValue()[0], paramRule)) {
				
				request.setAttribute("invalidInput", param.getKey());
				
				throw new ServletException("Request failed input validation for param key -> value: " + param.getKey() + " -> " + param.getValue()[0]);
				
			}
			
		}
		
	}

	private boolean isParameterValid(String paramValue, String paramRule) {
				
		if (paramRule != null) {
			
			Pattern specialCharRegex = Pattern.compile(paramRule);
			
			Matcher specialCharMatcher = specialCharRegex.matcher(paramValue);
			
			if (specialCharMatcher.find()) {
				return false;
			}
			
		}
		
		return true;
		
	}

	private void addCSPHeader(HttpServletResponse response) {
		
		if (CSP_HEADER != null && CSP_VALUE != null)
			response.addHeader(CSP_HEADER, CSP_VALUE);
		
	}

	public void destroy() { }

}
