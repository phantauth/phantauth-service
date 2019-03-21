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
import com.github.phantauth.core.Client;
import com.github.phantauth.core.ClientBean;
import com.github.phantauth.core.Tenant;
import com.github.phantauth.resource.Endpoint;
import com.github.phantauth.resource.Name;
import com.google.common.base.Strings;

import javax.inject.Inject;
import javax.inject.Named;

public class ClientFactory extends AbstractFactory<Client> {

    @JsonDeserialize(as= ClientBean.class)
    interface ClientMixin extends Client {
    }

    @Inject
    public ClientFactory(@Named("ttl") final long cacheTTL) {
        super(Client.class, ClientMixin.class, cacheTTL);
    }

    @Override
    public Client get(final Tenant tenant, final Name name) {
        final ClientBean client = defaults(tenant, (ClientBean) super.get(tenant, name));
        return client == null ? null : client.toImmutable();
    }

    static ClientBean defaults(final Tenant tenant, final ClientBean client) {
        if ( client == null ) {
            return null;
        }

        if (Strings.isNullOrEmpty(client.getId())) {
            client.setId(Endpoint.CLIENT.toResource(tenant.getIssuer(), client.getClientId()));
        }

        if (Strings.isNullOrEmpty(client.getClientUri()) ) {
            client.setClientUri(Endpoint.CLIENT.toProfile(tenant.getIssuer(), client.getClientId()));
        }

        return client;
    }
}
