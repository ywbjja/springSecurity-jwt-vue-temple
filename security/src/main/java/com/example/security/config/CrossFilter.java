package com.example.security.config;

import org.springframework.context.annotation.Configuration;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//写一个filter对response进行过滤
//@Configuration
public class CrossFilter implements Filter {
  
    @Override  
    public void destroy() {  
        // TODO Auto-generated method stub  
          
    }  
  
    @Override  
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        response.setHeader("Access-Control-Allow-Origin", "*");    
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");    
        response.setHeader("Access-Control-Max-Age", "3600");    
        response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With,x-requested-with, Content-Type, Accept, client_id, uuid, Authorization");  
      //  response.setHeader("Access-Control-Allow-Headers", "x-requested-with");   
        chain.doFilter(req, res);   
    }  
  
    @Override  
    public void init(FilterConfig arg0) throws ServletException {
          
    }  
  
}  