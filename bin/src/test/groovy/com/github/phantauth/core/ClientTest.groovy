package com.github.phantauth.core

import com.fasterxml.jackson.annotation.JsonProperty
import spock.lang.Specification

class ClientTest extends Specification {

    def "test method annotations"() {

        given:
        Class clazz = Client

        expect:
        Property.CLIENT_ID == clazz.getMethod("getClientId").getAnnotation(JsonProperty).value()
        Property.CLIENT_SECRET == clazz.getMethod("getClientSecret").getAnnotation(JsonProperty).value()
        Property.REDIRECT_URIS == clazz.getMethod("getRedirectUris").getAnnotation(JsonProperty).value()
        Property.TOKEN_ENDPOINT_AUTH_METHOD == clazz.getMethod("getTokenEndpointAuthMethod").getAnnotation(JsonProperty).value()
        Property.GRANT_TYPES == clazz.getMethod("getGrantTypes").getAnnotation(JsonProperty).value()
        Property.RESPONSE_TYPES == clazz.getMethod("getResponseTypes").getAnnotation(JsonProperty).value()
        Property.CLIENT_NAME == clazz.getMethod("getClientName").getAnnotation(JsonProperty).value()
        Property.CLIENT_URI == clazz.getMethod("getClientUri").getAnnotation(JsonProperty).value()
        Property.LOGO_URI == clazz.getMethod("getLogoUri").getAnnotation(JsonProperty).value()
        Property.SCOPE == clazz.getMethod("getScope").getAnnotation(JsonProperty).value()
        Property.CONTACTS == clazz.getMethod("getContacts").getAnnotation(JsonProperty).value()
        Property.TOS_URI == clazz.getMethod("getTosUri").getAnnotation(JsonProperty).value()
        Property.POLICY_URI == clazz.getMethod("getPolicyUri").getAnnotation(JsonProperty).value()
        Property.JWKS_URI == clazz.getMethod("getJwksUri").getAnnotation(JsonProperty).value()
        Property.JWKS == clazz.getMethod("getJwks").getAnnotation(JsonProperty).value()
        Property.SOFTWARE_ID == clazz.getMethod("getSoftwareId").getAnnotation(JsonProperty).value()
        Property.SOFTWARE_VERSION == clazz.getMethod("getSoftwareVersion").getAnnotation(JsonProperty).value()
        Property.LOGO_EMAIL == clazz.getMethod("getLogoEmail").getAnnotation(JsonProperty).value()
        Property.ID == clazz.getMethod("getId").getAnnotation(JsonProperty).value()
    }
}
