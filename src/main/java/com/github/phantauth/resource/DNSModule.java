package com.github.phantauth.resource;

import com.github.phantauth.config.Config;
import com.github.phantauth.exception.ConfigurationException;
import com.google.common.collect.ImmutableSet;
import dagger.Module;
import dagger.Provides;
import org.minidns.hla.ResolverApi;
import org.minidns.record.TXT;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import javax.inject.Named;
import javax.inject.Singleton;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import java.util.function.Function;

@Module
public class DNSModule {

    private DNSModule() {
        // no instances
    }

    @Provides @Singleton @Named("txtResolver")
    static Function<String, Set<String>> provideTxtResolver(final Config config) {
        return config.isStandalone() ? new StandaloneTxtResolver(config) : new TxtResolver();
    }

    static class TxtResolver implements Function<String,Set<String>> {

        @Override
        public Set<String> apply(final String hostname) {

            final Set<TXT> answer;
            try {
                answer = ResolverApi.INSTANCE.resolve(hostname, TXT.class).getAnswers();
            } catch (RuntimeException | IOException e) {
                return null;
            }

            final ImmutableSet.Builder<String> builder = ImmutableSet.builder();

            for(TXT txt : answer) {
                builder.add(txt.getText());
            }

            return builder.build();
        }
    }

    static class StandaloneTxtResolver implements Function<String,Set<String>> {

        private final Set<String> result;

        public StandaloneTxtResolver(final Config config) {
            final TemplateEngine engine = new TemplateEngine();

            final ClassLoaderTemplateResolver testResolver = new ClassLoaderTemplateResolver();
            testResolver.setTemplateMode(TemplateMode.TEXT);

            engine.addTemplateResolver(testResolver);

            final Context context = new Context();
            context.setVariable("config", config);
            final Properties props = new Properties();

            try {
                props.load(new StringReader(engine.process("META-INF/standalone.properties", context)));
            } catch (IOException e) {
                throw new ConfigurationException("standalone.properties");
            }

            final ImmutableSet.Builder<String> builder = ImmutableSet.builder();
            for(String key : props.stringPropertyNames()) {
                final String value = System.getenv("PHANTAUTH_" + key.toUpperCase());
                builder.add(key + "=" + Optional.ofNullable(value).orElse(props.getProperty(key)));
            }

            result = builder.build();
        }

        @Override
        public Set<String> apply(final String hostname) {
            return result;
        }
    }

}
