package com.github.phantauth.resource.producer

import spock.lang.Specification

class HashesTest extends Specification {

    def "test hash lengths"() {

        expect:
        Hashes.shortHash("foo").length() == 7
        Hashes.farmhash("foo").length() == 16
        Hashes.hashids(1).length() == 10
        Hashes.md5("foo").length() == 32
        Hashes.md5Base64Url("foo").length() == 22
        Hashes.sha256("foo").length() == 64
    }

    def "test hash values"() {

        expect:
        Hashes.shortHash("foo") == "F6JYHYY"
        Hashes.farmhash("foo") == "e383932f606f5c55"
        //Hashes.hashids(1) == "Nae32MlY6n"
        Hashes.md5("foo") == "acbd18db4cc2f85cedef654fccc4a4d8"
        Hashes.md5Base64Url("foo") == "rL0Y20zC-Fzt72VPzMSk2A"
        Hashes.sha256("foo") == "2c26b46b68ffc68ff99b453c1d30413413422d706483bfa0f98a5e886266e7ae"
    }
}
