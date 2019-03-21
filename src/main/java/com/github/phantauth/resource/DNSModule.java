package com.github.phantauth.resource;

import com.google.common.collect.ImmutableSet;
import dagger.Module;
import dagger.Provides;
import org.minidns.hla.ResolverApi;
import org.minidns.record.TXT;

import javax.inject.Named;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.function.Function;

@Module
public class DNSModule {

    @Provides @Singleton @Named("txtResolver")
    static Function<String, Set<String>> provideTxtResolver() {

        return new TxtResolver();
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
}
