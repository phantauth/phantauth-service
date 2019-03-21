package com.github.phantauth.service.auth


import com.github.phantauth.flow.AuthorizationFlow
import com.github.phantauth.core.Tenant
import com.github.phantauth.resource.TenantRepository
import com.github.phantauth.resource.Endpoint
import com.github.phantauth.service.auth.WellKnownServlet
import com.github.phantauth.test.ServiceLocator
import com.nimbusds.oauth2.sdk.as.AuthorizationServerConfigurationRequest
import com.nimbusds.oauth2.sdk.as.AuthorizationServerMetadata
import com.nimbusds.oauth2.sdk.http.HTTPResponse
import com.nimbusds.oauth2.sdk.id.Issuer
import com.nimbusds.openid.connect.sdk.op.OIDCProviderConfigurationRequest
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata
import org.junit.ClassRule
import spock.lang.Shared
import spock.lang.Specification

class WellKnownServletTest extends Specification {

    @Shared @ClassRule ServiceLocator locator

    Tenant tenant = locator.tenant

    def "test openid-configuration"() {

        WellKnownServlet instance = new WellKnownServlet(locator.tenantRepository, locator.authorizationFlow)

        HTTPResponse resp
        OIDCProviderMetadata meta

        when:
        resp = instance.doGet(new OIDCProviderConfigurationRequest(new Issuer(tenant.issuer)).toHTTPRequest())
        meta = OIDCProviderMetadata.parse(resp.contentAsJSONObject)
        then:
        meta.issuer == new Issuer(tenant.issuer)
        meta.registrationEndpointURI == Endpoint.REGISTER.toURI(tenant.issuer)
        meta.userInfoEndpointURI == Endpoint.USERINFO.toURI(tenant.issuer)
        meta.authorizationEndpointURI == Endpoint.AUTHORIZATION.toURI(tenant.issuer)
        meta.tokenEndpointURI == Endpoint.TOKEN.toURI(tenant.issuer)
    }

    def "test oauth-authorization-server"() {

        WellKnownServlet instance = new WellKnownServlet(locator.tenantRepository, locator.authorizationFlow)

        HTTPResponse resp
        AuthorizationServerMetadata meta

        when:
        resp = instance.doGet(new AuthorizationServerConfigurationRequest(new Issuer(tenant.issuer)).toHTTPRequest())
        meta = AuthorizationServerMetadata.parse(resp.contentAsJSONObject)
        then:
        meta.issuer == new Issuer(tenant.issuer)
        meta.registrationEndpointURI == Endpoint.REGISTER.toURI(tenant.issuer)
        meta.authorizationEndpointURI == Endpoint.AUTHORIZATION.toURI(tenant.issuer)
        meta.tokenEndpointURI == Endpoint.TOKEN.toURI(tenant.issuer)
    }

}