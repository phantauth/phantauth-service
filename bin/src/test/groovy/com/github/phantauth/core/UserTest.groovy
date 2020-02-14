package com.github.phantauth.core

import com.fasterxml.jackson.annotation.JsonProperty
import spock.lang.Specification

class UserTest extends Specification {

    def "test method annotations"() {

        given:
        Class clazz = User

        expect:
        Property.SUB == clazz.getMethod("getSub").getAnnotation(JsonProperty).value()
        Property.NAME == clazz.getMethod("getName").getAnnotation(JsonProperty).value()
        Property.GIVEN_NAME == clazz.getMethod("getGivenName").getAnnotation(JsonProperty).value()
        Property.FAMILY_NAME == clazz.getMethod("getFamilyName").getAnnotation(JsonProperty).value()
        Property.MIDDLE_NAME == clazz.getMethod("getMiddleName").getAnnotation(JsonProperty).value()
        Property.NICKNAME == clazz.getMethod("getNickname").getAnnotation(JsonProperty).value()
        Property.PREFERRED_USERNAME == clazz.getMethod("getPreferredUsername").getAnnotation(JsonProperty).value()
        Property.PROFILE == clazz.getMethod("getProfile").getAnnotation(JsonProperty).value()
        Property.PICTURE == clazz.getMethod("getPicture").getAnnotation(JsonProperty).value()
        Property.WEBSITE == clazz.getMethod("getWebsite").getAnnotation(JsonProperty).value()
        Property.EMAIL == clazz.getMethod("getEmail").getAnnotation(JsonProperty).value()
        Property.EMAIL_VERIFIED == clazz.getMethod("getEmailVerified").getAnnotation(JsonProperty).value()
        Property.GENDER == clazz.getMethod("getGender").getAnnotation(JsonProperty).value()
        Property.BIRTHDATE == clazz.getMethod("getBirthdate").getAnnotation(JsonProperty).value()
        Property.ZONEINFO == clazz.getMethod("getZoneinfo").getAnnotation(JsonProperty).value()
        Property.LOCALE == clazz.getMethod("getLocale").getAnnotation(JsonProperty).value()
        Property.PHONE_NUMBER == clazz.getMethod("getPhoneNumber").getAnnotation(JsonProperty).value()
        Property.PHONE_NUMBER_VERIFIED == clazz.getMethod("getPhoneNumberVerified").getAnnotation(JsonProperty).value()
        Property.UPDATED_AT == clazz.getMethod("getUpdatedAt").getAnnotation(JsonProperty).value()
        Property.ADDRESS == clazz.getMethod("getAddress").getAnnotation(JsonProperty).value()
        Property.ME == clazz.getMethod("getMe").getAnnotation(JsonProperty).value()
        Property.ID == clazz.getMethod("getId").getAnnotation(JsonProperty).value()
        Property.WEBMAIL == clazz.getMethod("getWebmail").getAnnotation(JsonProperty).value()
        Property.UID == clazz.getMethod("getUid").getAnnotation(JsonProperty).value()
        "password" == clazz.getMethod("getPassword").getAnnotation(JsonProperty).value()
    }
}
