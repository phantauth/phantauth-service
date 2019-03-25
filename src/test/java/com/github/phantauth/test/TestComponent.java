package com.github.phantauth.test;

import com.github.phantauth.config.Config;
import com.github.phantauth.config.ConfigPropsModule;
import com.github.phantauth.flow.AuthorizationFlow;
import com.github.phantauth.resource.ResourceModule;
import com.github.phantauth.resource.TenantRepository;
import com.github.phantauth.resource.producer.faker.ClientFaker;
import com.github.phantauth.resource.producer.faker.UserFaker;
import com.github.phantauth.service.PhantAuthComponent;
import com.github.phantauth.service.ServiceModule;
import com.github.phantauth.service.TemplateManager;
import com.github.phantauth.service.auth.AuthModule;
import com.github.phantauth.service.rest.RestModule;
import com.github.phantauth.token.ClientTokenFactory;
import com.github.phantauth.token.UserTokenFactory;
import dagger.Component;

import javax.inject.Singleton;

@Component(modules = {ServiceModule.class, TestModule.class, ConfigPropsModule.class, RestModule.class, AuthModule.class, ResourceModule.class})
@Singleton
public interface TestComponent extends PhantAuthComponent  {
    Config getConfig();
    TenantRepository getTenantRepository();
    UserFaker getUserFaker();
    ClientFaker getClientFaker();
    ClientTokenFactory getClientTokenFactory();
    UserTokenFactory getUserTokenFactory();
    AuthorizationFlow getAuthorizationFlow();
    TemplateManager getTemplateManager();
    class Holder {
        public static TestComponent instance;
        public static void init() {
            if ( instance == null ) {
                instance = DaggerTestComponent.builder().build();
            }
        }
    }
}
