package com.github.phantauth.service.rest

import com.github.phantauth.resource.Endpoint
import com.github.phantauth.test.ServiceLocator
import com.nimbusds.oauth2.sdk.http.HTTPRequest
import com.nimbusds.oauth2.sdk.http.HTTPResponse
import org.junit.ClassRule
import spock.lang.Shared
import spock.lang.Specification

class AvatarServletTest extends Specification {

    @Shared @ClassRule ServiceLocator locator

    @Shared
    URL endpoint

    def setupSpec() {
        endpoint = Endpoint.AVATAR.toURI(locator.config.serviceURI).toURL()
    }

    def "test without arg"() {
        HTTPRequest request
        HTTPResponse response

        when:
        request = new HTTPRequest(HTTPRequest.Method.GET, endpoint)
        response = request.send()

        then:
        response.statusCode == 302
        response.getHeader("Cache-Control") == "must-revalidate,no-cache,no-store,max-age=0,s-maxage=0"
    }

    def "test without arg with flags"() {
        HTTPRequest request
        HTTPResponse response

        when:
        request = new HTTPRequest(HTTPRequest.Method.GET, new URL(endpoint.toString() +   ";dummy"))
        response = request.send()

        then:
        response.statusCode == 302
        response.getHeader("Cache-Control") == "must-revalidate,no-cache,no-store,max-age=0,s-maxage=0"
        response.getHeader("Location").contains("unknown")

        when:
        request = new HTTPRequest(HTTPRequest.Method.GET, new URL(endpoint.toString() +   ";male"))
        response = request.send()

        then:
        response.statusCode == 302
        response.getHeader("Cache-Control") == "must-revalidate,no-cache,no-store,max-age=0,s-maxage=0"
        response.getHeader("Location").contains("male")

        when:
        request = new HTTPRequest(HTTPRequest.Method.GET, new URL(endpoint.toString() +   ";female"))
        response = request.send()

        then:
        response.statusCode == 302
        response.getHeader("Cache-Control") == "must-revalidate,no-cache,no-store,max-age=0,s-maxage=0"
        response.getHeader("Location").contains("female")
    }

    def "test with arg"() {
        HTTPRequest request
        HTTPResponse response

        when:
        request = new HTTPRequest(HTTPRequest.Method.GET, new URL(endpoint.toString() +   "/dummy"))
        response = request.send()

        then:
        response.statusCode == 301
        response.getHeader("Cache-Control") == "public,max-age=86400,s-maxage=86400"
    }
}
