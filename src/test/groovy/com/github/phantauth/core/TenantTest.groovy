package com.github.phantauth.core

import com.fasterxml.jackson.annotation.JsonProperty
import spock.lang.Specification

class TenantTest extends Specification {

    def "test method annotations"() {

        given:
        Class clazz = Tenant

        expect:
        Property.SUB == clazz.getMethod("getSub").getAnnotation(JsonProperty).value()
        Property.ISSUER == clazz.getMethod("getIssuer").getAnnotation(JsonProperty).value()
        Property.WEBSITE == clazz.getMethod("getWebsite").getAnnotation(JsonProperty).value()
        Property.TEMPLATE == clazz.getMethod("getTemplate").getAnnotation(JsonProperty).value()
        Property.FACTORY == clazz.getMethod("getFactory").getAnnotation(JsonProperty).value()
        Property.FACTORIES == clazz.getMethod("getFactories").getAnnotation(JsonProperty).value()
        Property.DEPOT == clazz.getMethod("getDepot").getAnnotation(JsonProperty).value()
        Property.DEPOTS == clazz.getMethod("getDepots").getAnnotation(JsonProperty).value()
        Property.USERINFO == clazz.getMethod("getUserinfo").getAnnotation(JsonProperty).value()
        Property.NAME == clazz.getMethod("getName").getAnnotation(JsonProperty).value()
        Property.FLAGS == clazz.getMethod("getFlags").getAnnotation(JsonProperty).value()
        Property.LOGO == clazz.getMethod("getLogo").getAnnotation(JsonProperty).value()
        Property.THEME == clazz.getMethod("getTheme").getAnnotation(JsonProperty).value()
        Property.SCRIPT == clazz.getMethod("getScript").getAnnotation(JsonProperty).value()
        Property.SHEET == clazz.getMethod("getSheet").getAnnotation(JsonProperty).value()
        Property.SUMMARY == clazz.getMethod("getSummary").getAnnotation(JsonProperty).value()
        Property.ATTRIBUTION == clazz.getMethod("getAttribution").getAnnotation(JsonProperty).value()
        Property.ABOUT == clazz.getMethod("getAbout").getAnnotation(JsonProperty).value()
        Property.DOMAIN == clazz.getMethod("isDomain").getAnnotation(JsonProperty).value()
        Property.SUBTENANT == clazz.getMethod("isSubtenant").getAnnotation(JsonProperty).value()
        Property.ID == clazz.getMethod("getId").getAnnotation(JsonProperty).value()
    }
}
