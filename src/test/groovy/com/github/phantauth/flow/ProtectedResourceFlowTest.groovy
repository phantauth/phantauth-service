package com.github.phantauth.flow

import com.nimbusds.oauth2.sdk.AuthorizationRequest
import com.nimbusds.oauth2.sdk.TokenRequest
import spock.lang.Specification

class ProtectedResourceFlowTest extends Specification {

    def "test unused methods"() {

        ProtectedResourceFlow flow = new ProtectedResourceFlow(null,null,null,null,null)

        when:
        flow.implied((AuthorizationRequest)null)

        then:
        thrown IllegalStateException

        when:
        flow.implied((TokenRequest)null)

        then:
        thrown IllegalStateException

        when:
        flow.handle((TokenRequest)null)

        then:
        thrown IllegalStateException
    }
}
