package com.labizy.services.login.utils;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;

/**
 * @author Amit Srivastava
 *
 */
public class ClientTracerServlet implements Filter {
	private static Logger appLogger = LoggerFactory.getLogger("com.labizy.services.login.AppLogger");
	
	private CommonUtils commonUtils;
	
	/* (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	public void init(FilterConfig arg0) throws ServletException {
	    if(appLogger.isDebugEnabled()){
	    	appLogger.debug("Inside {}", "ClientTracerServlet.init()");
	    }
	
	    commonUtils = new CommonUtils();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response,	FilterChain chain) throws IOException, ServletException {
	    if(appLogger.isDebugEnabled()){
	    	appLogger.debug("Inside {}", "ClientTracerServlet.doFilter()");
	    }
	    
	    String mdcKey = "CLIENT_TRACE_ID";
		if (request instanceof HttpServletRequest) {
			final HttpServletRequest httpRequest = (HttpServletRequest) request;
			final String clientTraceId = httpRequest.getHeader("X-CLIENT-TRACE-ID");
			
			if (! StringUtils.isEmpty(clientTraceId)){
				MDC.put(mdcKey, clientTraceId);
			}else{
				MDC.put(mdcKey, commonUtils.getUniqueGeneratedId("AUTO", null));
			}
		} else {
			MDC.put(mdcKey, commonUtils.getUniqueGeneratedId("AUTO", null));
		}
		
		if(appLogger.isInfoEnabled()){
			appLogger.info("The client tarce id is set to --> {}", MDC.get(mdcKey));
		}
		
		chain.doFilter(request, response);		
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {
	    if(appLogger.isDebugEnabled()){
	    	appLogger.debug("Inside {}", "ClientTracerServlet.destroy()");
	    }
	}
}