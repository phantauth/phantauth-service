package com.github.phantauth.resource.producer.faker

import com.github.phantauth.core.TenantBean
import com.github.phantauth.core.User
import com.github.phantauth.core.Tenant
import com.github.phantauth.resource.Name
import spock.lang.Specification

class UserFakerTest extends Specification {
    UserFaker faker
    Tenant tenant

    void setup() {
        tenant = new TenantBean(sub: "tenant", issuer: "http://localhost")

        faker = new UserFaker()
    }

    def "test newUser: properties"() {
        User user
        when:
        user = faker.get(tenant, "dummy_user_name")

        then:
        user != null
        user.sub == "dummy_user_name"
        user.preferredUsername == (user.givenName[0] + user.familyName).toLowerCase()
        user.gender in ['male','female', 'unknown']
        user.nickname == user.givenName
        user.familyName.length() > 1
        user.givenName.length() > 1
        user.name.length() > 3
        user.phoneNumber.length() > 1
        user.profile.startsWith(tenant.issuer as String)
        user.profile.endsWith("/${user.sub}/profile")
        user.email.endsWith('@mailinator.com')
        user.email.startsWith(user.sub)
        user.picture.startsWith("https://www.gravatar.com/avatar/")
        user.me.startsWith("${tenant.issuer}/~")
        //user.picture.indexOf("identicon") > 0 || user.picture.indexOf("d=https://avatars.phatauth.me/") > 0
    }

    def "test newUser: deterministic"() {
        User user
        when:
        user = faker.get(tenant,"dummy_user_name")

        then:
        faker.get(tenant, "dummy_user_name") == user
        where:
        i << (1..10)
    }
}
