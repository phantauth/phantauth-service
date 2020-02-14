package com.github.phantauth.flow

import com.nimbusds.oauth2.sdk.AuthorizationRequest
import com.nimbusds.oauth2.sdk.TokenErrorResponse
import com.nimbusds.oauth2.sdk.TokenRequest
import spock.lang.Specification

class AuthorizationFlowTest extends Specification {

    def "test DefaultFlow"() {

        when:
        AuthorizationFlow.DefaultFlow flow = new AuthorizationFlow.DefaultFlow()

        then:
        ! flow.implied((AuthorizationRequest)null)
        ! flow.implied((TokenRequest)null)

        when:
        def response = flow.handle((TokenRequest)null)

        then:
        response.class == TokenErrorResponse
    }
}
