package org.bgtrack.security;

import java.io.IOException;
import java.time.Duration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;

public class ThrottlingFilter implements Filter {

    private Bucket createNewBucket() {
         long overdraft = 75;
         Refill refill = Refill.greedy(10, Duration.ofSeconds(1));
         Bandwidth limit = Bandwidth.classic(overdraft, refill);
         return Bucket4j.builder().addLimit(limit).build();
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) 
    		throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpSession session = httpRequest.getSession(true);

        String ipAddress = getClientIp(httpRequest);
        
        System.out.println("IP ADDR:" + ipAddress);
        
        Bucket bucket = (Bucket) session.getAttribute("sgg-throttler-" + ipAddress);
        
        if (bucket == null) {
            bucket = createNewBucket();
            session.setAttribute("sgg-throttler-" + ipAddress, bucket);
        }

        System.out.println(bucket.getAvailableTokens());
        
        if (bucket.tryConsume(1)) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
            httpResponse.setContentType("text/plain");
            httpResponse.setStatus(429);
            httpResponse.getWriter().append("Too many requests");
        }
    }
    
    private static String getClientIp(HttpServletRequest request) {

        String clientIp = null;

        if (request != null) {
        	clientIp = request.getHeader("X-FORWARDED-FOR");
            
        	if (clientIp == null) {
            	clientIp = request.getRemoteAddr();
            }
        	
        }
        
        return clientIp;
    }

	public void init(FilterConfig filterConfig) throws ServletException { }

	public void destroy() { }

}

