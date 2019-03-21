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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.phantauth.core.*;
import com.github.phantauth.resource.Endpoint;
import com.github.phantauth.resource.Name;
import com.google.common.base.Strings;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

public class TeamFactory extends AbstractFactory<Team> {

    @JsonDeserialize(as= TeamBean.class)
    interface TeamMixin extends Team {
    }

    @Inject
    public TeamFactory(@Named("ttl") final long cacheTTL) {
        super(Team.class, TeamMixin.class, cacheTTL);
    }

    @Override
    public Team get(final Tenant tenant, final Name name) {
        final TeamBean team = defaults(tenant, (TeamBean)super.get(tenant, name));

        return team == null ? null : team.toImmutable();
    }

    static TeamBean defaults(final Tenant tenant, final TeamBean team) {

        if ( team == null ) {
            return null;
        }

        if (Strings.isNullOrEmpty(team.getId())) {
            team.setId(Endpoint.TEAM.toResource(tenant.getIssuer(), team.getSub()));
        }

        if (Strings.isNullOrEmpty(team.getProfile())) {
            team.setProfile(Endpoint.TEAM.toProfile(tenant.getIssuer(), team.getSub()));
        }

        for(User user: team.getMembers()) {
            UserFactory.defaults(tenant, (UserBean) user);
        }

        return team;
    }
}
