package com.github.phantauth.core

import com.fasterxml.jackson.annotation.JsonProperty
import spock.lang.Specification

class AddressTest extends Specification {

    def "test method annotations"() {

        given:
        Class clazz = Address

        expect:
        Property.FORMATTED == clazz.getMethod("getFormatted").getAnnotation(JsonProperty).value()
        Property.STREET_ADDRESS == clazz.getMethod("getStreetAddress").getAnnotation(JsonProperty).value()
        Property.LOCALITY == clazz.getMethod("getLocality").getAnnotation(JsonProperty).value()
        Property.REGION == clazz.getMethod("getRegion").getAnnotation(JsonProperty).value()
        Property.POSTAL_CODE == clazz.getMethod("getPostalCode").getAnnotation(JsonProperty).value()
        Property.COUNTRY == clazz.getMethod("getCountry").getAnnotation(JsonProperty).value()
    }
}
