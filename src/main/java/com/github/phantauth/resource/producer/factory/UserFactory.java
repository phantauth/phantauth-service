/*
 * Apache License, Version 2.0
 *
 * Copyright 2019 Ivan SZKIBA https://www.linkedin.com/in/szkiba
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.github.phantauth.resource.producer.factory;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.phantauth.core.Tenant;
import com.github.phantauth.core.User;
import com.github.phantauth.core.UserBean;
import com.github.phantauth.resource.Endpoint;
import com.github.phantauth.resource.Name;
import com.google.common.base.Strings;

import javax.inject.Inject;
import javax.inject.Named;

public class UserFactory extends AbstractFactory<User> {

    @JsonDeserialize(as=UserBean.class)
    interface UserMixin extends User {
    }

    @Inject
    public UserFactory(@Named("ttl") final long cacheTTL) {
        super(User.class, UserMixin.class, cacheTTL);
    }

    @Override
    public User get(final Tenant tenant, final Name name) {

        final UserBean user = defaults(tenant, (UserBean)super.get(tenant, name));
        return user == null ? null : user.toImmutable();
    }

    static UserBean defaults(final Tenant tenant, final UserBean user) {
        if ( user == null ) {
            return null;
        }

        if (Strings.isNullOrEmpty(user.getMe())) {
            user.setMe(Endpoint.ME.toProfile(tenant.getIssuer(), user.getSub()));
        }

        if (Strings.isNullOrEmpty(user.getId())) {
            user.setId(Endpoint.USER.toResource(tenant.getIssuer(), user.getSub()));
        }

        if (Strings.isNullOrEmpty(user.getWebsite())) {
            user.setWebsite(tenant.getWebsite());
        }

        if ( Strings.isNullOrEmpty(user.getProfile()) ) {
            user.setProfile(Endpoint.USER.toProfile(tenant.getIssuer(), user.getSub()));
        }

        return user;
    }
}
