package com.github.phantauth.test;

import com.github.phantauth.config.Config;
import com.google.common.collect.ImmutableSet;
import dagger.Module;
import dagger.Provides;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.exceptions.TemplateEngineException;
import org.thymeleaf.exceptions.TemplateInputException;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import javax.inject.Named;
import javax.inject.Singleton;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.URI;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;

@Module
public class TestModule {

    private static final String DOMAIN = "devd.io";

    @Provides @Singleton
    static Config provideTestConfig() {
        final int servicePort = newPort();
        final int developerPortalPort = newPort();
        final Config  config = new Config.Builder()
                .setPort(servicePort)
                .setServiceURI("http://"+DOMAIN+":" +servicePort)
                .setDefaultTenantURI("http://default."+DOMAIN+":"+ servicePort)
                .setDeveloperPortalURI("http://www."+DOMAIN+":" + developerPortalPort)
                .build();
        return config;
    }

    @Provides @Singleton @Named("txtResolver")
    static Function<String, Set<String>> provideTestTxtResolver(final Config config) {

        return new TestTxtResolver(config);
    }

    public static class TestTxtResolver implements Function<String,Set<String>> {
        private final TemplateEngine engine;
        private final Config config;

        public TestTxtResolver(final Config config) {
            this.config = config;
            engine = new TemplateEngine();

            final ClassLoaderTemplateResolver testResolver = new ClassLoaderTemplateResolver();
            testResolver.setPrefix("META-INF/tenants/");
            testResolver.setTemplateMode(TemplateMode.TEXT);
            testResolver.setSuffix(".txt");

            engine.addTemplateResolver(testResolver);
        }

        @Override
        public Set<String> apply(final String hostname) {
            final int idx = hostname.indexOf('.');
            final String host = hostname.substring(0,idx);
            final Context context = new Context();
            context.setVariable("domain", hostname.substring(idx + 1));
            context.setVariable("config", config);

            try {
                return ImmutableSet.copyOf(engine.process(host, context).split("\n"));
            } catch (TemplateEngineException x) {
                return Collections.emptySet();
            }
        }
    }

    private static int newPort() {
        try {
            final ServerSocket socket = new ServerSocket(0);
            final int port = socket.getLocalPort();
            socket.close();
            return port;
        } catch (IOException x) {
            throw new IllegalStateException(x);
        }
    }
}
