package com.github.phantauth.config;

import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class ConfigModule {

    private ConfigModule() {
        // no instances
    }

    @Provides @Singleton
    static Config provideConfig() {
        return new Config.Builder().build();
    }
}
