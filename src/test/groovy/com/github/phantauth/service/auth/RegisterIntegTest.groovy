/*
 * Apache License, Version 2.0
 *
 * Copyright 2019 Ivan SZKIBA https://www.linkedin.com/in/szkiba
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.github.phantauth.service.auth

import com.github.phantauth.core.Client
import com.github.phantauth.flow.ProtectedResourceFlow
import com.github.phantauth.test.ServiceLocator
import com.github.phantauth.token.ClientTokenFactory
import com.nimbusds.oauth2.sdk.client.ClientRegistrationResponse
import com.nimbusds.oauth2.sdk.http.HTTPResponse
import com.nimbusds.openid.connect.sdk.op.OIDCProviderConfigurationRequest
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata
import com.nimbusds.openid.connect.sdk.rp.OIDCClientInformation
import com.nimbusds.openid.connect.sdk.rp.OIDCClientMetadata
import com.nimbusds.openid.connect.sdk.rp.OIDCClientRegistrationRequest
import com.nimbusds.openid.connect.sdk.rp.OIDCClientRegistrationResponseParser
import org.junit.ClassRule
import spock.lang.Shared
import spock.lang.Specification

class RegisterIntegTest extends Specification {

    @Shared @ClassRule ServiceLocator locator

    @Shared
    Client client

    @Shared
    URI registerEndpoint

    def setupSpec() {
        client = locator.client
        registerEndpoint = locator.meta.registrationEndpointURI
    }

    def "Dynamic client registration"() {

        OIDCClientRegistrationRequest request = new OIDCClientRegistrationRequest(registerEndpoint, ProtectedResourceFlow.newClientMetadata(client), null)

        when:
        ClientRegistrationResponse response = OIDCClientRegistrationResponseParser.parse(request.toHTTPRequest().send())

        then:
        response.indicatesSuccess()

        when:
        OIDCClientInformation info = response.toSuccessResponse().clientInformation
        OIDCClientMetadata meta = info.OIDCMetadata

        then:
        info.ID.toString() != null
        meta.name == client.clientName
        info.secret.value.length() == 8

        when:
        Client decoded = locator.clientTokenFactory.parseSelfieToken(info.ID.toString())

        then:
        decoded.clientName == client.clientName
    }
}
