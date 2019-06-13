package com.github.phantauth.flow

import com.github.phantauth.core.Claim
import com.github.phantauth.core.Tenant
import com.github.phantauth.test.ServiceLocator
import com.github.phantauth.test.TestClient
import com.github.phantauth.test.TestUser
import com.google.common.collect.ImmutableMap
import com.nimbusds.jwt.SignedJWT
import com.nimbusds.oauth2.sdk.*
import com.nimbusds.oauth2.sdk.http.HTTPRequest
import com.nimbusds.oauth2.sdk.http.HTTPResponse
import com.nimbusds.oauth2.sdk.id.ClientID
import com.nimbusds.oauth2.sdk.id.State
import com.nimbusds.oauth2.sdk.token.AccessTokenType
import com.nimbusds.oauth2.sdk.token.BearerAccessToken
import com.nimbusds.oauth2.sdk.token.Tokens
import com.nimbusds.openid.connect.sdk.*
import com.nimbusds.openid.connect.sdk.claims.UserInfo
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata
import com.nimbusds.openid.connect.sdk.token.OIDCTokens
import geb.spock.GebSpec
import org.junit.ClassRule
import org.openqa.selenium.Keys
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification

class ImplicitFlowIntegTest extends GebSpec {

    @Shared @ClassRule ServiceLocator locator

    @Shared
    TestClient client

    @Shared
    TestUser user

    @Shared
    URI callback

    @Shared
    Scope scope

    @Shared
    String consent

    @Shared
    OIDCProviderMetadata meta

    State state
    Nonce nonce

    def setupSpec() {
        scope = new Scope("openid", "profile", "email", "address", "phone")
        consent = scope.toString()
        callback = new URI("http://example.com")
        user = locator.user
        client = locator.client
        meta = locator.meta
    }

    def setup() {
        state = new State()
        nonce = new Nonce()
        driver = new HtmlUnitDriver(true)
    }

    @Ignore
    def "Implicit flow"() {

        ResponseType responseType = new ResponseType("id_token","token")

        AuthenticationRequest authenticationRequest = new AuthenticationRequest.Builder(responseType, scope, client.clientID, callback)
                .state(state)
                .nonce(nonce)
                .endpointURI(meta.authorizationEndpointURI)
                .customParameter("consent", consent)
                .build()

        AuthenticationSuccessResponse authenticationResponse
        HTTPResponse response

        when:
        go authenticationRequest.toURI().toString()

        then:
        $("title").text() == "Login"

        when:
        //$("#name").value(user.sub)
        //$("#password").value(user.password)
//        $("#login").click()
        js.exec('$("#username").val("'+ user.sub +'")')
        js.exec('$("#login").click()')

        then:
        true
        print currentUrl


        when:
        HTTPRequest request = authenticationRequest.toHTTPRequest()
        request.followRedirects = false
        response = request.send()

        then:
        response.statusCode == 302

        when:
        authenticationResponse = AuthenticationSuccessResponse.parse(response)

        then:
        authenticationResponse.indicatesSuccess()
        authenticationResponse.state == state
        authenticationResponse.accessToken != null
        authenticationResponse.IDToken != null
        authenticationResponse.IDToken.JWTClaimsSet.subject == user.sub
        authenticationResponse.IDToken.JWTClaimsSet.getStringClaim("nonce") == nonce as String
        authenticationResponse.IDToken.JWTClaimsSet.getStringListClaim("aud") == [client.clientId]
    }

    def "Implicit flow with login_token"() {

        ResponseType responseType = new ResponseType("id_token","token")

        AuthenticationRequest authenticationRequest = new AuthenticationRequest.Builder(responseType, scope, client.clientID, callback)
                .state(state)
                .nonce(nonce)
                .endpointURI(meta.authorizationEndpointURI)
                .customParameter("consent", consent)
                .build()

        AuthenticationSuccessResponse authenticationResponse
        HTTPResponse response

        when:
        HTTPRequest request = authenticationRequest.toHTTPRequest()
        request.setHeader("Cookie", "login_token=${user.loginToken}")
        request.followRedirects = false
        response = request.send()

        then:
        response.statusCode == 302

        when:
        authenticationResponse = AuthenticationSuccessResponse.parse(response)

        then:
        authenticationResponse.indicatesSuccess()
        authenticationResponse.state == state
        authenticationResponse.accessToken != null
        authenticationResponse.IDToken != null
        authenticationResponse.IDToken.JWTClaimsSet.subject == user.sub
        authenticationResponse.IDToken.JWTClaimsSet.getStringClaim("nonce") == nonce as String
        authenticationResponse.IDToken.JWTClaimsSet.getStringListClaim("aud") == [client.clientId]
    }

    def "Implicit flow with login_token and claims request"() {

        ResponseType responseType = new ResponseType("id_token","token")
        Scope scope = new Scope("openid", "address", "phone")
        ClaimsRequest claims = new ClaimsRequest()
        claims.addIDTokenClaim("email")
        claims.addUserInfoClaim("name")
        claims.addUserInfoClaim("phone_number")
        AuthenticationRequest authenticationRequest = new AuthenticationRequest.Builder(responseType, scope, client.clientID, callback)
                .state(state)
                .nonce(nonce)
                .claims(claims)
                .endpointURI(meta.authorizationEndpointURI)
                .customParameter("consent", consent)
                .build()

        AuthenticationSuccessResponse authenticationResponse
        HTTPResponse response

        when:
        HTTPRequest request = authenticationRequest.toHTTPRequest()
        request.setHeader("Cookie", "login_token=${user.loginToken}")
        request.followRedirects = false
        response = request.send()

        then:
        response.statusCode == 302

        when:
        authenticationResponse = AuthenticationSuccessResponse.parse(response)

        then:
        authenticationResponse.indicatesSuccess()
        authenticationResponse.state == state
        authenticationResponse.accessToken != null
        authenticationResponse.IDToken != null
        authenticationResponse.IDToken.JWTClaimsSet.subject == user.sub
        authenticationResponse.IDToken.JWTClaimsSet.getStringClaim("nonce") == nonce as String
        authenticationResponse.IDToken.JWTClaimsSet.getStringListClaim("aud") == [client.clientId]
        authenticationResponse.IDToken.JWTClaimsSet.getStringClaim("email") != null
        authenticationResponse.IDToken.JWTClaimsSet.getStringClaim("name") != null
        authenticationResponse.IDToken.JWTClaimsSet.getStringClaim("phone_number") != null
    }
}
