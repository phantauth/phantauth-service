package com.github.phantauth.service.rest

import com.github.phantauth.test.ServiceLocator
import com.github.phantauth.test.TestUser
import geb.spock.GebSpec
import org.junit.ClassRule
import spock.lang.Shared

class UserProfileIntegTest extends GebSpec {

    @Shared @ClassRule static ServiceLocator locator = new ServiceLocator()

    def "user profile"() {
        given:
        baseUrl = locator.tenant.issuer
        TestUser user = locator.user

        when:
        go "/~${user.sub}"

        then:
        title == user.name
        $("#sub").text() == user.sub
        $(".nickname").text() == user.nickname
        $(".sex").text() == user.gender
        $(".bday").text() == user.birthdate
        $(".tz").text() == user.zoneinfo
        $(".given-name").text() == user.givenName
        $(".family-name").text() == user.familyName
        $(".email").text() == user.email
        $(".tel").text() == user.phoneNumber
        $(".country-name").text() == user.address.country
        $(".postal-code").text() == user.address.postalCode
        $(".region").text() == user.address.region
        $(".street-address").text() == user.address.streetAddress
        $(".url").first().attr("href") == user.me
    }
}
