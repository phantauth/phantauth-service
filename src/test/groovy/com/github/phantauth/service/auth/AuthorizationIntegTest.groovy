package com.github.phantauth.service.auth

import com.github.phantauth.core.Claim

import com.github.phantauth.core.Tenant
import com.github.phantauth.resource.Endpoint
import com.github.phantauth.test.ServiceLocator
import com.github.phantauth.test.TestClient
import com.github.phantauth.test.TestUser
import com.google.common.collect.ImmutableMap
import com.nimbusds.jwt.SignedJWT
import com.nimbusds.oauth2.sdk.AccessTokenResponse
import com.nimbusds.oauth2.sdk.AuthorizationCodeGrant
import com.nimbusds.oauth2.sdk.AuthorizationGrant
import com.nimbusds.oauth2.sdk.AuthorizationRequest
import com.nimbusds.oauth2.sdk.AuthorizationResponse
import com.nimbusds.oauth2.sdk.AuthorizationSuccessResponse
import com.nimbusds.oauth2.sdk.RefreshTokenGrant
import com.nimbusds.oauth2.sdk.ResponseType
import com.nimbusds.oauth2.sdk.Scope
import com.nimbusds.oauth2.sdk.TokenIntrospectionRequest
import com.nimbusds.oauth2.sdk.TokenIntrospectionResponse
import com.nimbusds.oauth2.sdk.TokenIntrospectionSuccessResponse
import com.nimbusds.oauth2.sdk.TokenRequest
import com.nimbusds.oauth2.sdk.TokenResponse
import com.nimbusds.oauth2.sdk.http.HTTPRequest
import com.nimbusds.oauth2.sdk.http.HTTPResponse
import com.nimbusds.oauth2.sdk.id.ClientID
import com.nimbusds.oauth2.sdk.id.State
import com.nimbusds.oauth2.sdk.token.AccessTokenType
import com.nimbusds.oauth2.sdk.token.BearerAccessToken
import com.nimbusds.oauth2.sdk.token.Tokens
import com.nimbusds.openid.connect.sdk.AuthenticationRequest
import com.nimbusds.openid.connect.sdk.AuthenticationSuccessResponse
import com.nimbusds.openid.connect.sdk.ClaimsRequest
import com.nimbusds.openid.connect.sdk.Nonce
import com.nimbusds.openid.connect.sdk.OIDCTokenResponse
import com.nimbusds.openid.connect.sdk.UserInfoRequest
import com.nimbusds.openid.connect.sdk.UserInfoResponse
import com.nimbusds.openid.connect.sdk.claims.UserInfo
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata
import com.nimbusds.openid.connect.sdk.token.OIDCTokens
import groovy.transform.CompileStatic
import org.junit.ClassRule
import spock.lang.Shared
import spock.lang.Specification

class AuthorizationIntegTest extends Specification {

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
    }

    def "Authorization code flow with login_token"() {

        ResponseType responseType = new ResponseType("code")

        AuthenticationRequest authorizationRequest = new AuthenticationRequest.Builder(responseType, scope, client.clientID, callback)
                .state(state)
                .nonce(nonce)
                .endpointURI(meta.authorizationEndpointURI)
                .customParameter("consent", consent)
                .build()

        AuthenticationSuccessResponse authenticationResponse
        OIDCTokenResponse tokenResponse
        OIDCTokens tokens
        HTTPResponse response
        UserInfoResponse userInfoResponse

        when:
        HTTPRequest request = authorizationRequest.toHTTPRequest()
        request.followRedirects = false
        request.setHeader("Cookie", "login_token=${user.loginToken}")
        response = request.send()

        then:
        response.statusCode == 302

        when:
        authenticationResponse = AuthenticationSuccessResponse.parse(response)

        then:
        authenticationResponse.indicatesSuccess()
        authenticationResponse.state == state
        authenticationResponse.authorizationCode != null

        when:
        AuthorizationGrant codeGrant = new AuthorizationCodeGrant(authenticationResponse.authorizationCode, callback)
        TokenRequest tokenRequest = new TokenRequest(meta.tokenEndpointURI, client.secretPost, codeGrant, scope)
        tokenResponse = OIDCTokenResponse.parse(tokenRequest.toHTTPRequest().send())

        then:
        tokenResponse.indicatesSuccess()

        when:
        tokens = tokenResponse.OIDCTokens

        then:
        tokens.accessToken != null
        tokens.refreshToken != null
        tokens.IDToken != null
        tokens.IDToken.JWTClaimsSet.subject == user.sub
        tokens.IDToken.JWTClaimsSet.getStringClaim("nonce") == nonce as String
        tokens.IDToken.JWTClaimsSet.getStringListClaim("aud") == [client.clientId]
        tokens.IDToken.JWTClaimsSet.getStringClaim("email") != null

        when:
        UserInfoRequest userInfoRequest = new UserInfoRequest(meta.userInfoEndpoint, tokens.accessToken as BearerAccessToken)
        userInfoResponse = UserInfoResponse.parse(userInfoRequest.toHTTPRequest().send())

        then:
        userInfoResponse.indicatesSuccess()

        when:
        UserInfo userInfo = userInfoResponse.toSuccessResponse().userInfo

        then:
        userInfo.subject.toString() == user.sub
        userInfo.email.address == user.email

        when:
        RefreshTokenGrant refreshGrant = new RefreshTokenGrant(tokens.refreshToken)
        TokenRequest refreshRequest = new TokenRequest(meta.tokenEndpointURI, client.secretPost, refreshGrant, scope)
        TokenResponse refreshResponse = OIDCTokenResponse.parse(refreshRequest.toHTTPRequest().send())

        then:
        refreshResponse.indicatesSuccess()

        when:
        OIDCTokens freshTokens = refreshResponse.OIDCTokens

        then:
        freshTokens.accessToken != null
        freshTokens.refreshToken != null
        freshTokens.IDToken != null
        freshTokens.IDToken.JWTClaimsSet.subject == user.sub
        freshTokens.IDToken.JWTClaimsSet.getStringClaim("nonce") == nonce as String
        freshTokens.IDToken.JWTClaimsSet.getStringListClaim("aud") == [client.clientId]
        freshTokens.IDToken.JWTClaimsSet.getStringClaim("email") != null

        when:
        TokenIntrospectionRequest introspectionRequest = new TokenIntrospectionRequest(meta.introspectionEndpointURI,freshTokens.accessToken);
        TokenIntrospectionResponse introspectionResponse = TokenIntrospectionResponse.parse(introspectionRequest.toHTTPRequest().send())

        then:
        introspectionResponse.indicatesSuccess()

        when:
        TokenIntrospectionSuccessResponse successResponse = introspectionResponse as TokenIntrospectionSuccessResponse

        then:
        successResponse.active
        successResponse.subject.toString() == user.sub
        successResponse.tokenType == AccessTokenType.BEARER
        successResponse.username == user.name
        successResponse.scope == scope
        successResponse.issuer == locator.meta.issuer

    }

    def "Authorization code flow with login_token and tenant"() {

        Tenant tenant = locator.tenantRepository.get("www.example.com")
        OIDCProviderMetadata meta = locator[tenant].meta

        ResponseType responseType = new ResponseType("code")

        TestClient client = locator[tenant].client
        TestUser user = locator[tenant].user

        AuthenticationRequest authorizationRequest = new AuthenticationRequest.Builder(responseType, scope, client.clientID, callback)
                .state(state)
                .nonce(nonce)
                .endpointURI(meta.authorizationEndpointURI)
                .customParameter("consent", consent)
                .build()

        AuthenticationSuccessResponse authenticationResponse
        OIDCTokenResponse tokenResponse
        OIDCTokens tokens
        HTTPResponse response
        UserInfoResponse userInfoResponse

        when:
        HTTPRequest request = authorizationRequest.toHTTPRequest()
        request.followRedirects = false
        request.setHeader("Cookie", "login_token=${user.loginToken}")
        response = request.send()

        then:
        response.statusCode == 302

        when:
        authenticationResponse = AuthenticationSuccessResponse.parse(response)

        then:
        authenticationResponse.indicatesSuccess()
        authenticationResponse.state == state
        authenticationResponse.authorizationCode != null

        when:
        AuthorizationGrant codeGrant = new AuthorizationCodeGrant(authenticationResponse.authorizationCode, callback)
        TokenRequest tokenRequest = new TokenRequest(meta.tokenEndpointURI, client.secretPost, codeGrant, scope)
        tokenResponse = OIDCTokenResponse.parse(tokenRequest.toHTTPRequest().send())

        then:
        tokenResponse.indicatesSuccess()

        when:
        tokens = tokenResponse.OIDCTokens

        then:
        tokens.accessToken != null
        tokens.refreshToken != null
        tokens.IDToken != null
        tokens.IDToken.JWTClaimsSet.subject == user.sub
        tokens.IDToken.JWTClaimsSet.getStringClaim("nonce") == nonce as String
        tokens.IDToken.JWTClaimsSet.getStringListClaim("aud") == [client.clientId]
        tokens.IDToken.JWTClaimsSet.getStringClaim("email") != null

        when:
        UserInfoRequest userInfoRequest = new UserInfoRequest(meta.userInfoEndpoint, tokens.accessToken as BearerAccessToken)
        userInfoResponse = UserInfoResponse.parse(userInfoRequest.toHTTPRequest().send())

        then:
        userInfoResponse.indicatesSuccess()

        when:
        UserInfo userInfo = userInfoResponse.toSuccessResponse().userInfo

        then:
        userInfo.subject.toString() == user.sub
        userInfo.email.address == user.email

        when:
        RefreshTokenGrant refreshGrant = new RefreshTokenGrant(tokens.refreshToken)
        TokenRequest refreshRequest = new TokenRequest(meta.tokenEndpointURI, client.secretPost, refreshGrant, scope)
        TokenResponse refreshResponse = OIDCTokenResponse.parse(refreshRequest.toHTTPRequest().send())

        then:
        refreshResponse.indicatesSuccess()

        when:
        OIDCTokens freshTokens = refreshResponse.OIDCTokens

        then:
        freshTokens.accessToken != null
        freshTokens.refreshToken != null
        freshTokens.IDToken != null
        freshTokens.IDToken.JWTClaimsSet.subject == user.sub
        freshTokens.IDToken.JWTClaimsSet.getStringClaim("nonce") == nonce as String
        freshTokens.IDToken.JWTClaimsSet.getStringListClaim("aud") == [client.clientId]
        freshTokens.IDToken.JWTClaimsSet.getStringClaim("email") != null

        when:
        TokenIntrospectionRequest introspectionRequest = new TokenIntrospectionRequest(meta.introspectionEndpointURI,freshTokens.accessToken);
        TokenIntrospectionResponse introspectionResponse = TokenIntrospectionResponse.parse(introspectionRequest.toHTTPRequest().send())

        then:
        introspectionResponse.indicatesSuccess()

        when:
        TokenIntrospectionSuccessResponse successResponse = introspectionResponse as TokenIntrospectionSuccessResponse

        then:
        successResponse.active
        successResponse.subject.toString() == user.sub
        successResponse.tokenType == AccessTokenType.BEARER
        successResponse.username == user.name
        successResponse.scope == scope
        successResponse.issuer == meta.issuer

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

    def "Hybrid flow with login_token"() {

        ResponseType responseType = new ResponseType("code", "id_token", "token")

        AuthenticationRequest authenticationRequest = new AuthenticationRequest.Builder(responseType, scope, client.clientID, callback)
                .state(state)
                .nonce(nonce)
                .endpointURI(meta.authorizationEndpointURI)
                .customParameter("consent", consent)
                .build()

        AuthenticationSuccessResponse authenticationResponse
        HTTPResponse response
        OIDCTokenResponse tokenResponse
        OIDCTokens tokens

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

        when:
        AuthorizationGrant codeGrant = new AuthorizationCodeGrant(authenticationResponse.authorizationCode, callback)
        TokenRequest tokenRequest = new TokenRequest(meta.tokenEndpointURI, client.secretPost, codeGrant, scope)
        tokenResponse = OIDCTokenResponse.parse(tokenRequest.toHTTPRequest().send())

        then:
        tokenResponse.indicatesSuccess()

        when:
        tokens = tokenResponse.OIDCTokens

        then:
        tokens.accessToken != null
        tokens.refreshToken != null
        tokens.IDToken != null
        tokens.IDToken.JWTClaimsSet.subject == user.sub
        tokens.IDToken.JWTClaimsSet.getStringClaim("nonce") == nonce as String
        tokens.IDToken.JWTClaimsSet.getStringListClaim("aud") == [client.clientId]

        when:
        RefreshTokenGrant refreshGrant = new RefreshTokenGrant(tokens.refreshToken)
        TokenRequest refreshRequest = new TokenRequest(meta.tokenEndpointURI, client.secretPost, refreshGrant, scope)
        TokenResponse refreshResponse = OIDCTokenResponse.parse(refreshRequest.toHTTPRequest().send())

        then:
        refreshResponse.indicatesSuccess()

        when:
        OIDCTokens freshTokens = refreshResponse.OIDCTokens

        then:
        freshTokens.accessToken != null
        freshTokens.refreshToken != null
        freshTokens.IDToken != null
        freshTokens.IDToken.JWTClaimsSet.subject == user.sub
        freshTokens.IDToken.JWTClaimsSet.getStringClaim("nonce") == nonce as String
        freshTokens.IDToken.JWTClaimsSet.getStringListClaim("aud") == [client.clientId]
        freshTokens.IDToken.JWTClaimsSet.getStringClaim("email") != null
    }

    def "Resource Owner Password flow"() {

        TokenRequest tokenRequest = new TokenRequest(meta.tokenEndpointURI, user.authorizationGrant, scope)

        AccessTokenResponse tokenResponse
        HTTPResponse response

        when:
        response = tokenRequest.toHTTPRequest().send()

        then:
        response.statusCode == 200

        when:
        tokenResponse = AccessTokenResponse.parse(response)

        then:
        tokenResponse.indicatesSuccess()
        tokenResponse.tokens.accessToken != null
        tokenResponse.tokens.refreshToken != null
        tokenResponse.customParameters["login_token"] != null
//        response.getHeader("Set-Cookie").startsWith("login_token=\"${tokenResponse.customParameters['login_token']}\"")
        SignedJWT.parse(tokenResponse.customParameters["login_token"] as String).JWTClaimsSet.subject == user.sub
    }

    def "Extended Resource Owner Password flow"() {

        TokenRequest tokenRequest = new TokenRequest(meta.tokenEndpointURI, client.clientID, user.authorizationGrant, scope)

        OIDCTokenResponse tokenResponse
        HTTPResponse response
        OIDCTokens tokens

        when:
        HTTPRequest request = tokenRequest.toHTTPRequest()
        request.query += "&nonce=${nonce.value}&state=${state.value}"
        response = request.send()

        then:
        response.statusCode == 200

        when:
        tokenResponse = OIDCTokenResponse.parse(response)
        tokens = tokenResponse.tokens as OIDCTokens

        then:
        tokenResponse.indicatesSuccess()
        tokens.accessToken != null
        tokens.refreshToken != null
        tokens.idToken != null
        tokens.idToken.JWTClaimsSet.subject == user.sub
        tokens.IDToken.JWTClaimsSet.getStringClaim("nonce") == nonce as String
        tokens.IDToken.JWTClaimsSet.getStringListClaim("aud") == [client.clientId]
        SignedJWT.parse(tokenResponse.customParameters["login_token"] as String).JWTClaimsSet.subject == user.sub
 //       response.getHeader("Set-Cookie").startsWith("login_token=\"${tokenResponse.customParameters['login_token']}\"")
    }

    def "IndieAuth authorization flow with login_token"() {

        ResponseType responseType = new ResponseType("id")
        Scope scope = new Scope("indieauth")
        ClientID clientId = new ClientID("https://phantauth.me/client?super.tool")

        AuthorizationRequest authorizationRequest = new AuthorizationRequest.Builder(responseType, clientId)
                .state(state)
                .scope(scope)
                .redirectionURI(callback)
                .endpointURI(meta.authorizationEndpointURI)
                .customParameter(Claim.ME.getName(), user.me)
                .customParameter("consent", scope.toString())
                .build()

        AuthorizationResponse authorizationResponse
        TokenResponse tokenResponse
        Tokens tokens
        HTTPResponse response

        when:
        HTTPRequest request = authorizationRequest.toHTTPRequest()
        request.followRedirects = false
        request.setHeader("Cookie", "login_token=${user.loginToken}")
        response = request.send()

        then:
        response.statusCode == 302

        when:
        authorizationResponse = AuthorizationResponse.parse(response)

        then:
        authorizationResponse.indicatesSuccess()
        authorizationResponse.state == state
        authorizationResponse.authorizationCode != null

        when:
        AuthorizationGrant codeGrant = new AuthorizationCodeGrant(authorizationResponse.authorizationCode, callback)
        Map<String, String> params = ImmutableMap.of("me", user.me)
        TokenRequest tokenRequest = new TokenRequest(locator.meta.tokenEndpointURI, clientId, codeGrant, scope, params)
        tokenResponse = TokenResponse.parse(tokenRequest.toHTTPRequest().send())

        then:
        tokenResponse.indicatesSuccess()
        tokenResponse.toSuccessResponse().customParameters.size() == 1
        tokenResponse.toSuccessResponse().customParameters["me"] == user.me

        when:
        tokens = tokenResponse.tokens

        then:
        tokens.accessToken != null
        tokens.refreshToken == null
        tokens.getAccessToken().scope == scope
    }

    def "IndieAuth authentication flow with login_token"() {

        ResponseType responseType = new ResponseType("id")
        Scope scope = new Scope("indieauth")
        ClientID clientId = new ClientID("https://phantauth.me/client?super.tool")

        AuthorizationRequest authorizationRequest = new AuthorizationRequest.Builder(responseType, clientId)
                .state(state)
                .scope(scope)
                .redirectionURI(callback)
                .endpointURI(meta.authorizationEndpointURI)
                .customParameter(Claim.ME.getName(), user.me)
                .customParameter("consent", scope.toString())
                .build()

        AuthorizationSuccessResponse authorizationResponse
        TokenResponse tokenResponse
        Tokens tokens
        HTTPResponse response

        when:
        HTTPRequest request = authorizationRequest.toHTTPRequest()
        request.followRedirects = false
        request.setHeader("Cookie", "login_token=${user.loginToken}")
        response = request.send()

        then:
        response.statusCode == 302

        when:
        authorizationResponse = AuthorizationResponse.parse(response) as AuthorizationSuccessResponse

        then:
        authorizationResponse.indicatesSuccess()
        authorizationResponse.state == state
        authorizationResponse.authorizationCode != null

        when:
        authorizationRequest = new AuthorizationRequest.Builder(responseType, clientId)
                .customParameter("code",authorizationResponse.authorizationCode.toString())
                .redirectionURI(callback)
                .endpointURI(meta.authorizationEndpointURI)
                .build()


        authorizationRequest.toHTTPRequest()
        response = authorizationRequest.toHTTPRequest().send()
        tokenResponse = TokenResponse.parse(response)

        then:
        tokenResponse.indicatesSuccess()
        tokenResponse.toSuccessResponse().customParameters.size() == 1
        tokenResponse.toSuccessResponse().customParameters["me"] == user.me

        when:
        tokens = tokenResponse.tokens

        then:
        tokens.accessToken != null
        tokens.refreshToken == null
    }
}
