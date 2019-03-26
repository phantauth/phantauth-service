package com.github.phantauth.service;

import com.github.phantauth.config.Config;
import com.github.phantauth.resource.Endpoint;
import org.eclipse.jetty.rewrite.handler.*;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.eclipse.jetty.util.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.util.EnumSet;
import java.util.Set;

@Singleton
public class PhantAuthServer extends Server {

    private static final Logger logger = LoggerFactory.getLogger(PhantAuthServer.class);

    @Inject
    PhantAuthServer(@Named("port") final int port, @Named("serviceURI") final URI serviceURI, @Named("defaultTenantURI") final URI defaultTenantURI, final Set<AbstractServlet> servlets) {
        super(port);

        for(Connector connector : getConnectors()) {
            for(ConnectionFactory factory  : connector.getConnectionFactories()) {
                if(factory instanceof HttpConnectionFactory) {
                    ((HttpConnectionFactory)factory).getHttpConfiguration().setSendServerVersion(false);
                }
            }
        }

        final ServletContextHandler servletContextHandler = newServletContextHandler(servlets);
        final ResourceHandler resHandler = newResourceHandler(servletContextHandler);
        final RewriteHandler rewriteHandler = newRewriteHandler(resHandler, serviceURI, defaultTenantURI);

        setHandler(new HandlerList(rewriteHandler, resHandler, servletContextHandler));
    }

    private ServletContextHandler newServletContextHandler(final Set<AbstractServlet> servlets) {
        final ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        servletContextHandler.setContextPath("/");
        servletContextHandler.addFilter(new FilterHolder(new CrossOriginFilter()), "/*", EnumSet.of(DispatcherType.REQUEST));
        servletContextHandler.addFilter(new FilterHolder(new NoCacheFilter()), "/auth/*", EnumSet.of(DispatcherType.REQUEST));
        addServlets(servletContextHandler, servlets);
        return servletContextHandler;
    }

    private ResourceHandler newResourceHandler(final Handler baseHandler) {
        final ResourceHandler resHandler = new ResourceHandler();
        resHandler.setDirAllowed(false);
        resHandler.setBaseResource(Resource.newClassPathResource("/docroot/"));
        resHandler.setHandler(baseHandler);
        return resHandler;
    }

    private RewriteHandler newRewriteHandler(final Handler baseHandler, final URI serviceURI, final URI defaultTenantURI) {
        final RewriteHandler rewriteHandler = new RewriteHandler();
        rewriteHandler.setHandler(baseHandler);

        final RedirectPatternRule faviconRule = new RedirectPatternRule("*favicon.ico", defaultTenantURI + "/logo/phantauth-favicon.png");
        faviconRule.setTerminating(true);
        rewriteHandler.addRule(faviconRule);

        // tenant name from _tenantname
        final RewriteRegexRule tenantRule = new RewriteRegexRule("^/_([^/]+)(?:/?)(.*)$","/$2?tenant=$1");
        rewriteHandler.addRule(tenantRule);

        // tenant name from default domain
        final RewriteRegexRule domainRule = new RewriteRegexRule("^/_(?:/?)(.*)$","/$1?tenant=" + serviceURI.getHost());
        rewriteHandler.addRule(domainRule);

        // user profile from ~username
        final RewriteRegexRule userRule = new RewriteRegexRule("^/~([^/]*)(/.*)?$","/me/$1;/profile$2");
        rewriteHandler.addRule(userRule);

        final RewritePatternRule indexRule = new RewritePatternRule("", Endpoint.INDEX.getPath());
        rewriteHandler.addRule(indexRule);

        if ( serviceURI.toString().endsWith(Config.DEFAULT_DOMAIN) ) {
            final ResponsePatternRule robotsRule = new ResponsePatternRule("/robots.txt", String.valueOf(HttpServletResponse.SC_NOT_FOUND), "Not Found");
            rewriteHandler.addRule(robotsRule);
        }

        return rewriteHandler;
    }

    private void addServlets(final ServletContextHandler handler, final Set<AbstractServlet> servlets) {
        for(AbstractServlet servlet : servlets) {
            final ServletHolder holder = new ServletHolder(servlet);
            handler.addServlet(holder, servlet.endpoint.getPathSpec());
        }
    }

    private int execute() {
        try {
            start();
            join();
        } catch (Exception ex) {
            logger.error("Error occurred while starting Jetty", ex);
            return 1;
        } finally {
            destroy();
        }
        return 0;
    }

    public static void main(String[] args) {
        System.setProperty("http.agent", PhantAuthServer.class.getSimpleName());

        PhantAuthServer server = DaggerPhantAuthComponent.builder().build().server();
        System.exit(server.execute());
    }
}
