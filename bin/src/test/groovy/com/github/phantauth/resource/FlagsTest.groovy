package com.github.phantauth.resource

import spock.lang.Specification

class FlagsTest extends Specification {

    def "test: defaults"() {
        when:
        Flags flags = Flags.EMPTY
        then:
        flags.mail == Flags.Mail.DETECT
        flags.avatar == Flags.Avatar.AI
        flags.gender == Flags.Gender.GUESS
        flags.locale == Locale.US
        flags.size == Flags.Size.TINY
    }

    def "test: parse"(String value, Flags.Mail mail, Flags.Avatar avatar, Flags.Gender gender, Locale locale, Flags.Size size) {
        Flags flags
        when:
        flags = Flags.parse(value)
        then:
        flags.mail == mail
        flags.avatar == avatar
        flags.gender == gender
        flags.locale == locale
        flags.size == size
        where:
        value                            | mail               | avatar               | gender               | locale    | size
        "plus"                           | Flags.Mail.PLUS    | Flags.Avatar.DEFAULT | Flags.Gender.DEFAULT | Locale.US | Flags.Size.DEFAULT
        "dot"                            | Flags.Mail.DOT     | Flags.Avatar.DEFAULT | Flags.Gender.DEFAULT | Locale.US | Flags.Size.DEFAULT
        "guess"                          | Flags.Mail.DETECT  | Flags.Avatar.DEFAULT | Flags.Gender.DEFAULT | Locale.US | Flags.Size.DEFAULT
        "noemail"                        | Flags.Mail.NOEMAIL | Flags.Avatar.DEFAULT | Flags.Gender.DEFAULT | Locale.US | Flags.Size.DEFAULT

        "tiny"                           | Flags.Mail.DEFAULT | Flags.Avatar.DEFAULT | Flags.Gender.DEFAULT | Locale.US | Flags.Size.TINY
        "small"                          | Flags.Mail.DEFAULT | Flags.Avatar.DEFAULT | Flags.Gender.DEFAULT | Locale.US | Flags.Size.SMALL
        "medium"                         | Flags.Mail.DEFAULT | Flags.Avatar.DEFAULT | Flags.Gender.DEFAULT | Locale.US | Flags.Size.MEDIUM
        "large"                          | Flags.Mail.DEFAULT | Flags.Avatar.DEFAULT | Flags.Gender.DEFAULT | Locale.US | Flags.Size.LARGE
        "huge"                           | Flags.Mail.DEFAULT | Flags.Avatar.DEFAULT | Flags.Gender.DEFAULT | Locale.US | Flags.Size.HUGE

        "noavatar"                       | Flags.Mail.DEFAULT | Flags.Avatar.NOAVATAR  | Flags.Gender.DEFAULT | Locale.US            | Flags.Size.DEFAULT
        "dice"                           | Flags.Mail.DEFAULT | Flags.Avatar.DICE      | Flags.Gender.DEFAULT | Locale.US            | Flags.Size.DEFAULT
        "ai"                             | Flags.Mail.DEFAULT | Flags.Avatar.AI        | Flags.Gender.DEFAULT | Locale.US            | Flags.Size.DEFAULT
        "photo"                          | Flags.Mail.DEFAULT | Flags.Avatar.PHOTO     | Flags.Gender.DEFAULT | Locale.US            | Flags.Size.DEFAULT
        "adorable"                       | Flags.Mail.DEFAULT | Flags.Avatar.ADORABLE  | Flags.Gender.DEFAULT | Locale.US            | Flags.Size.DEFAULT
        "notfound"                       | Flags.Mail.DEFAULT | Flags.Avatar.NOTFOUND  | Flags.Gender.DEFAULT | Locale.US            | Flags.Size.DEFAULT
        "mp"                             | Flags.Mail.DEFAULT | Flags.Avatar.MP        | Flags.Gender.DEFAULT | Locale.US            | Flags.Size.DEFAULT
        "identicon"                      | Flags.Mail.DEFAULT | Flags.Avatar.IDENTICON | Flags.Gender.DEFAULT | Locale.US            | Flags.Size.DEFAULT
        "monsterid"                      | Flags.Mail.DEFAULT | Flags.Avatar.MONSTERID | Flags.Gender.DEFAULT | Locale.US            | Flags.Size.DEFAULT
        "wavatar"                        | Flags.Mail.DEFAULT | Flags.Avatar.WAVATAR   | Flags.Gender.DEFAULT | Locale.US            | Flags.Size.DEFAULT
        "retro"                          | Flags.Mail.DEFAULT | Flags.Avatar.RETRO     | Flags.Gender.DEFAULT | Locale.US            | Flags.Size.DEFAULT
        "robohash"                       | Flags.Mail.DEFAULT | Flags.Avatar.ROBOHASH  | Flags.Gender.DEFAULT | Locale.US            | Flags.Size.DEFAULT
        "blank"                          | Flags.Mail.DEFAULT | Flags.Avatar.BLANK     | Flags.Gender.DEFAULT | Locale.US            | Flags.Size.DEFAULT

        "nogender"                       | Flags.Mail.DEFAULT | Flags.Avatar.DEFAULT   | Flags.Gender.NOGENDER| Locale.US            | Flags.Size.DEFAULT
        "guess"                          | Flags.Mail.DEFAULT | Flags.Avatar.DEFAULT   | Flags.Gender.GUESS   | Locale.US            | Flags.Size.DEFAULT
        "male"                           | Flags.Mail.DEFAULT | Flags.Avatar.DEFAULT   | Flags.Gender.MALE    | Locale.US            | Flags.Size.DEFAULT
        "female"                         | Flags.Mail.DEFAULT | Flags.Avatar.DEFAULT   | Flags.Gender.FEMALE  | Locale.US            | Flags.Size.DEFAULT

        "en_CA"                          | Flags.Mail.DEFAULT | Flags.Avatar.DEFAULT   | Flags.Gender.GUESS   | Locale.CANADA        | Flags.Size.DEFAULT
        "fr_CA"                          | Flags.Mail.DEFAULT | Flags.Avatar.DEFAULT   | Flags.Gender.GUESS   | Locale.CANADA_FRENCH | Flags.Size.DEFAULT

        "plus;male;identicon"            | Flags.Mail.PLUS    | Flags.Avatar.IDENTICON | Flags.Gender.MALE    | Locale.US            | Flags.Size.DEFAULT
        "dot;female;retro;fr_CA"         | Flags.Mail.DOT     | Flags.Avatar.RETRO     | Flags.Gender.FEMALE  | Locale.CANADA_FRENCH | Flags.Size.DEFAULT
        "plus;large"                     | Flags.Mail.PLUS    | Flags.Avatar.DEFAULT   | Flags.Gender.DEFAULT | Locale.US            | Flags.Size.LARGE
    }

    def "test format with locale"() {
        when:
        Flags flags = Flags.parse("hu_HU")

        then:
        flags.format() == "hu_HU"

        when:
        flags = Flags.parse("sketch;hu_HU")

        then:
        flags.format() == "sketch;hu_HU"
    }
}
