package com.github.phantauth.service;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class NoCacheFilter implements Filter {
    static final String NOCACHE = "must-revalidate,no-cache,no-store,max-age=0,s-maxage=0";
    static final String CACHE_CONTROL = "Cache-Control";

    @Override
    public void init(final FilterConfig filterConfig)  {
        // nothing to do
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        ((HttpServletResponse)response).setHeader(CACHE_CONTROL, NOCACHE);
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // nothing to do
    }
}
