package com.github.phantauth.service.auth;

import com.github.phantauth.service.AbstractServlet;
import com.google.common.collect.ImmutableSet;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.ElementsIntoSet;

import java.util.Set;

@Module
public class AuthModule {

    private AuthModule() {
        // no instances
    }

    @Provides
    @ElementsIntoSet
    static Set<AbstractServlet> provideAuthServlets(final AuthorizationServlet auth, final IntrospectionServlet intro, final JWKSServlet jwks, final RegisterServlet register, final TokenServlet token, final UserInfoServlet userinfo, final WellKnownServlet wellknown) {
        return ImmutableSet.of(auth, intro, jwks, register, token, userinfo, wellknown);
    }

}
