package com.github.phantauth.core

import com.fasterxml.jackson.annotation.JsonProperty
import spock.lang.Specification

class GroupTest extends Specification {

    def "test method annotations"() {

        given:
        Class clazz = Group

        expect:
        Property.SUB == clazz.getMethod("getSub").getAnnotation(JsonProperty).value()
        Property.NAME == clazz.getMethod("getName").getAnnotation(JsonProperty).value()
        Property.PROFILE == clazz.getMethod("getProfile").getAnnotation(JsonProperty).value()
        Property.LOGO == clazz.getMethod("getLogo").getAnnotation(JsonProperty).value()
        Property.LOGO_EMAIL == clazz.getMethod("getLogoEmail").getAnnotation(JsonProperty).value()
        Property.ID == clazz.getMethod("getId").getAnnotation(JsonProperty).value()
        Property.MEMBERS == clazz.getMethod("getMembers").getAnnotation(JsonProperty).value()
    }
}
