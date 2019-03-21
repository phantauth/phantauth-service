package com.github.phantauth.resource;

import com.github.phantauth.core.*;
import com.github.phantauth.resource.producer.DNSDomainProducer;
import com.github.phantauth.resource.producer.depot.ClientDepot;
import com.github.phantauth.resource.producer.depot.UserDepot;
import com.github.phantauth.resource.producer.factory.ClientFactory;
import com.github.phantauth.resource.producer.factory.FleetFactory;
import com.github.phantauth.resource.producer.factory.TeamFactory;
import com.github.phantauth.resource.producer.factory.UserFactory;
import com.github.phantauth.resource.producer.faker.ClientFaker;
import com.github.phantauth.resource.producer.faker.FleetFaker;
import com.github.phantauth.resource.producer.faker.TeamFaker;
import com.github.phantauth.resource.producer.faker.UserFaker;
import com.github.phantauth.token.ClientTokenFactory;
import com.github.phantauth.token.UserTokenFactory;
import dagger.Module;
import dagger.Provides;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.*;
import java.util.function.Function;

@Module
public class ResourceModule {

    @Provides @Singleton
    static Repository<User> provideUserRepository(final UserTokenFactory tokenFactory, final UserFactory factory, final UserDepot depot, final UserFaker faker) {
        return new Repository<>(tokenFactory, factory, depot, faker);
    }

    @Provides @Singleton
    static Producer<User> provideUserProducer(final UserTokenFactory tokenFactory, final UserFactory factory, final UserDepot depot, final UserFaker faker) {
        return provideUserRepository(tokenFactory, factory, depot, faker);
    }

    @Provides @Singleton
    static Repository<Client> provideClientRepository(final ClientTokenFactory tokenFactory, final ClientFactory factory, final ClientDepot depot, final ClientFaker faker) {
        return new Repository<>(tokenFactory, factory, depot, faker);
    }

    @Provides @Singleton
    static Producer<Client> provideClientProducer(final ClientTokenFactory tokenFactory, final ClientFactory factory, final ClientDepot depot, final ClientFaker faker) {
        return provideClientRepository(tokenFactory, factory, depot, faker);
    }

    @Provides @Singleton
    static Repository<Team> provideTeamRepository(final TeamFactory factory, final UserDepot depot, final TeamFaker faker) {
        return new Repository<>(factory, depot.getTeamDepot(), faker);
    }

    @Provides @Singleton
    static Repository<Fleet> provideFleetRepository(final FleetFactory factory, final ClientDepot depot, final FleetFaker faker) {
        return new Repository<>(factory, depot.getFleetDepot(), faker);
    }

    @Provides @Singleton
    static Repository<Domain> provideDomainRepository(final DNSDomainProducer producer) {
        return new Repository<>(producer);
    }

    @Provides @Singleton @Named("txtMapper")
    static Function<String, Map<String, Object>> provideTxtMapper(@Named("txtResolver") final Function<String, Set<String>> resolver) {

        return new TxtMapper(resolver);
    }

    public static class TxtMapper implements Function<String, Map<String,Object>> {

        final Function<String, Set<String>> resolver;

        public TxtMapper(final Function<String, Set<String>> resolver) {
            this.resolver = resolver;
        }


        @Override
        public Map<String, Object> apply(final String hostname) {
            final Set<String> answer = resolver.apply(hostname);

            if ( answer == null ) {
                return null;
            }

            final Map<String,Object> props = new HashMap<>();

            for(String txt : answer) {
                StringTokenizer tokenizer = new StringTokenizer(txt, "=");
                if ( tokenizer.hasMoreTokens() ) {
                    final String key = tokenizer.nextToken();
                    if ( tokenizer.hasMoreTokens() ) {
                        final String value = tokenizer.nextToken("").substring(1);
                        props.put(key, newPropValue(value, props.get(key)));
                    }
                }
            }
            return props;
        }

        Object newPropValue(final Object newValue, final Object oldValue) {
            if ( oldValue == null ) {
                return newValue;
            }
            List<Object> list;

            if ( List.class.isAssignableFrom(oldValue.getClass()) ) {
                list = (List<Object>)oldValue;
            } else {
                list =  new ArrayList<>();
                list.add(oldValue);
            }
            list.add(newValue);
            return list;
        }
    }

}
