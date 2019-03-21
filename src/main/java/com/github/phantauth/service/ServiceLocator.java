/*
package com.github.phantauth.service;

import com.github.phantauth.config.Config;
import com.github.phantauth.core.Client;
import com.github.phantauth.core.Fleet;
import com.github.phantauth.core.Team;
import com.github.phantauth.core.User;
import com.github.phantauth.exception.ConfigurationException;
import com.github.phantauth.resource.producer.DNSTenantProducer;
import com.github.phantauth.resource.producer.ExternalTenantProducer;
import com.github.phantauth.resource.producer.depot.ClientDepot;
import com.github.phantauth.resource.producer.depot.UserDepot;
import com.github.phantauth.resource.producer.factory.ClientFactory;
import com.github.phantauth.resource.producer.factory.FleetFactory;
import com.github.phantauth.resource.producer.factory.TeamFactory;
import com.github.phantauth.resource.producer.factory.UserFactory;
import com.github.phantauth.resource.producer.faker.*;
import com.github.phantauth.flow.*;
import com.github.phantauth.resource.*;
import com.github.phantauth.token.ClientTokenFactory;
import com.github.phantauth.token.TenantTokenFactory;
import com.github.phantauth.token.TokenManager;
import com.github.phantauth.token.UserTokenFactory;
import com.google.common.base.Preconditions;
import org.immutables.value.Value;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.net.URI;
import java.net.URISyntaxException;

@Value.Immutable
abstract class ServiceLocator {

    @Value.Default
    Config getConfig() {
        return new Config.Builder().build();
    }

    @Value.Default
    URI getDefaultTenant() {
        try {
            return new URI(getConfig().getDefaultTenantURI());
        } catch (URISyntaxException e) {
            throw new ConfigurationException(String.format("Invalid website URI: %s", getConfig().getDefaultTenantURI()));
        }
    }

    @Value.Default
    URI getService() {
        try {
            return new URI(getConfig().getServiceURI());
        } catch (URISyntaxException e) {
            throw new ConfigurationException(String.format("Invalid service URI: %s", getConfig().getServiceURI()));
        }
    }

    @Value.Default
    URI getDeveloperPortal() {
        try {
            return new URI(getConfig().getDeveloperPortalURI());
        } catch (URISyntaxException e) {
            throw new ConfigurationException(String.format("Invalid developer portal URI: %s", getConfig().getDeveloperPortalURI()));
        }
    }

    @Value.Default
    TokenManager getTokenManager() {
        return new TokenManager(getConfig().getServiceKeys());
    }

    @Value.Default
    UserFaker getUserFactory() {
        return new UserFaker();
    }

    @Value.Default
    UserTokenFactory getUserTokenFactory() {
        return new UserTokenFactory(getTokenManager());
    }

    @Value.Default
    UserFactory getExternalUserFactory() {
        return new UserFactory();
    }

    @Value.Default
    UserDepot getExternalUserRepository() {
        return new UserDepot(getConfig().getTTL());
    }

    @Value.Default
    Repository<User> getUserRepsitory() {
        return new Repository<>(getUserTokenFactory(), getExternalUserFactory(), getExternalUserRepository(), getUserFactory());
    }

    @Value.Default
    ClientFaker getClientFactory() {
        return new ClientFaker();
    }

    @Value.Default
    ClientTokenFactory getClientTokenFactory() {
        return new ClientTokenFactory(getTokenManager());
    }

    @Value.Default
    ClientFactory getExternalClientFactory() {
        return new ClientFactory();
    }

    @Value.Default
    ClientDepot getExternalClientRepository() {
        return new ClientDepot(getConfig().getTTL());
    }

    @Value.Default
    Repository<Client> getClientRepository() {
        return new Repository<>(getClientTokenFactory(), getExternalClientFactory(), getExternalClientRepository(), getClientFactory());
    }

    @Value.Default
    TeamFaker getTeamFactory() {
        return new TeamFaker(getUserRepsitory());
    }

    @Value.Default
    TeamFactory getExternalTeamFactory() {
        return new TeamFactory();
    }

    @Value.Default
    Repository<Team> getExternalTeamRepository() {
        return getExternalUserRepository().getTeamRepository();
    }

    @Value.Default
   Repository<Team> getTeamRepository() {
        return new Repository<>(getExternalTeamFactory(), getExternalTeamRepository(), getTeamFactory());
    }

    @Value.Default
    FleetFaker getFleetFactory() {
        return new FleetFaker(getClientRepository());
    }

    @Value.Default
    FleetFactory getExternalFleetFactory() {
        return new FleetFactory();
    }

    @Value.Default
    Repository<Fleet> getExternalFleetRepository() {
        return getExternalClientRepository().getFleetRepository();
    }

    @Value.Default
   Repository<Fleet> getFleetRepository() {
        return new Repository<>(getExternalFleetFactory(), getExternalFleetRepository(), getFleetFactory());
    }

    @Value.Default
    TenantTokenFactory getTenantTokenFactory() {
        return new TenantTokenFactory(getTokenManager());
    }

    @Value.Default
    TenantRepository getTenantRepository() {
        return new TenantRepository(
                getTenantTokenFactory(),
                new DNSTenantProducer(getService(), getDefaultTenant()),
                new ExternalTenantProducer(getService(), getDefaultTenant(), getConfig().getTTL())
        );
    }

    @Value.Default
    TemplateManager getTemplateManager() {
        return new TemplateManager(getConfig().getTTL());
    }

    @Value.Default
    ProtectedResourceFlow getProtectedResourceFlow() {
        return new ProtectedResourceFlow(getTenantRepository(), getUserRepsitory(), getUserTokenFactory(), getClientRepository(), getClientTokenFactory());
    }

    @Value.Default
    AuthorizationFlow getAuthorizationFlow() {
        return new AuthorizationFlow(getAuthorizationCodeFlow(), getImplicitFlow(), getHybridFlow(), getPasswordFlow(), getRefreshTokenFlow(), getIndieaAuthFlow());
    }

    @Value.Default
    AuthorizationCodeFlow getAuthorizationCodeFlow() {
        return new AuthorizationCodeFlow(getTenantRepository(), getUserRepsitory(), getUserTokenFactory(), getClientRepository(), getClientTokenFactory());
    }

    @Value.Default
    ImplicitFlow getImplicitFlow() {
        return new ImplicitFlow(getTenantRepository(), getUserRepsitory(), getUserTokenFactory(), getClientRepository(), getClientTokenFactory());
    }

    @Value.Default
    HybridFlow getHybridFlow() {
        return new HybridFlow(getTenantRepository(), getUserRepsitory(), getUserTokenFactory(), getClientRepository(), getClientTokenFactory());
    }

    @Value.Default
    PasswordFlow getPasswordFlow() {
        return new PasswordFlow(getTenantRepository(), getUserRepsitory(), getUserTokenFactory(), getClientRepository(), getClientTokenFactory());
    }

    @Value.Default
    RefreshTokenFlow getRefreshTokenFlow() {
        return new RefreshTokenFlow(getTenantRepository(), getUserRepsitory(), getUserTokenFactory(), getClientRepository(), getClientTokenFactory());
    }

    @Value.Default
    IndieAuthFlow getIndieaAuthFlow() {
        return new IndieAuthFlow(getTenantRepository(), getUserRepsitory(), getUserTokenFactory(), getClientRepository(), getClientTokenFactory());
    }

    @SuppressWarnings("immutables:incompat")
    private static ServiceLocator instance;

    static void bind(final Config config) {
        instance = new ServiceLocatorValue.Builder().setConfig(config).build();
    }

    static void bind() {
        bind(new Config.Builder().build());
    }

    static ServiceLocator get() {
        Preconditions.checkNotNull(instance);
        return instance;
    }

    @WebListener
    public static class ContextListener implements ServletContextListener {

        @Override
        public void contextInitialized(final ServletContextEvent event) {
            bind();
        }

        @Override
        public void contextDestroyed(final ServletContextEvent event) {
            // nothing to do
        }
    }
}
*/