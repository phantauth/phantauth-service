package com.github.phantauth.resource

import spock.lang.Specification

class NameTest  extends Specification {

    def "test parse"() {
        Name name;
        when:
        name = Name.parse("Joe Little~test;male")
        then:
        ! name.host
        name.instance == "test"
        name.userInfo == "joe.little"
        name.flags.gender == Flags.Gender.MALE
        name.subject == "joe.little~test;male"
        when:
        name = Name.parse("joe.little@gmail.com~foo;female")
        then:
        name.host == "gmail.com"
        name.instance == "foo"
        name.flags.gender == Flags.Gender.FEMALE
        name.userInfo == "joe.little"
        when:
        name = Name.parse("jim~foo")
        then:
        ! name.host
        name.instance == "foo"
        name.userInfo == "jim"
        name.flags.default
    }
}
