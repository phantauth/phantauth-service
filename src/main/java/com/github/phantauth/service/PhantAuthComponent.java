package com.github.phantauth.service;

import com.github.phantauth.config.ConfigModule;
import com.github.phantauth.config.ConfigPropsModule;
import com.github.phantauth.resource.DNSModule;
import com.github.phantauth.resource.ResourceModule;
import com.github.phantauth.service.auth.AuthModule;
import com.github.phantauth.service.rest.RestModule;
import dagger.Component;

import javax.inject.Singleton;

@Component(modules = {ServiceModule.class, ConfigModule.class, ConfigPropsModule.class, RestModule.class, AuthModule.class, ResourceModule.class, DNSModule.class})
@Singleton
public interface PhantAuthComponent {
    PhantAuthServer server();
}
