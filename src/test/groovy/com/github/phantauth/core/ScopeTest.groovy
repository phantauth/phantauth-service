package com.github.phantauth.core

import spock.lang.Specification

class ScopeTest extends Specification {

    def "test default"() {

        expect:
        Scope.values() == Scope.getDefaultScopes()
    }

    def "test split"() {
        expect:
        Scope.split("") == []
        Scope.split("openid") == [Scope.OPENID]
        Scope.split("openid email") == [Scope.OPENID, Scope.EMAIL]
        Scope.split("openid email profile") == [Scope.OPENID, Scope.EMAIL, Scope.PROFILE]
        Scope.split("openid email unknown") == [Scope.OPENID, Scope.EMAIL, null]
    }

    def "test parse"() {
        expect:
        Scope.parse("") == null
        Scope.parse("openid") == Scope.OPENID
        Scope.parse("email") == Scope.EMAIL
        Scope.parse("unknown") == null
    }

    def "test format"() {
        expect:
        Scope.format() == ""
        Scope.format(Scope.OPENID) == "openid"
        Scope.format(Scope.OPENID, Scope.PROFILE) == "openid profile"
        Scope.format(Scope.OPENID, Scope.PROFILE, Scope.EMAIL) == "openid profile email"
    }
}
