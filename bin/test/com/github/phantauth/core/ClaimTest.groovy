package com.github.phantauth.core

import spock.lang.Specification

class ClaimTest extends Specification {

    def "test names"() {

        expect:
        Property.SUB == Claim.SUB.name
        Property.NAME == Claim.NAME.name
        Property.GIVEN_NAME == Claim.GIVEN_NAME.name
        Property.FAMILY_NAME == Claim.FAMILY_NAME.name
        Property.MIDDLE_NAME == Claim.MIDDLE_NAME.name
        Property.NICKNAME == Claim.NICKNAME.name
        Property.PREFERRED_USERNAME == Claim.PREFERRED_USERNAME.name
        Property.PROFILE == Claim.PROFILE.name
        Property.PICTURE == Claim.PICTURE.name
        Property.WEBSITE == Claim.WEBSITE.name
        Property.EMAIL == Claim.EMAIL.name
        Property.EMAIL_VERIFIED == Claim.EMAIL_VERIFIED.name
        Property.GENDER == Claim.GENDER.name
        Property.BIRTHDATE == Claim.BIRTHDATE.name
        Property.ZONEINFO == Claim.ZONEINFO.name
        Property.LOCALE == Claim.LOCALE.name
        Property.PHONE_NUMBER == Claim.PHONE_NUMBER.name
        Property.PHONE_NUMBER_VERIFIED == Claim.PHONE_NUMBER_VERIFIED.name
        Property.UPDATED_AT == Claim.UPDATED_AT.name
        Property.ADDRESS == Claim.ADDRESS.name
        Property.ME == Claim.ME.name
        Property.UID == Claim.UID.name
    }
}
