package com.github.phantauth.service;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CrossOriginFilter implements Filter {
    static final String ACCESS_CONTROL_ALLOW_ORIGIN_HEADER = "Access-Control-Allow-Origin";

    @Override
    public void init(final FilterConfig filterConfig)  {
        // nothing to do
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        ((HttpServletResponse)response).setHeader(ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*");
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // nothing to do
    }
}
