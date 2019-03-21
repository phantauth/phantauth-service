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

package com.github.phantauth.service.auth;

import com.github.phantauth.core.Tenant;
import com.github.phantauth.exception.RequestMethodException;
import com.github.phantauth.flow.ProtectedResourceFlow;
import com.github.phantauth.resource.TenantRepository;
import com.github.phantauth.resource.Endpoint;
import com.github.phantauth.service.AbstractServlet;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.client.ClientRegistrationErrorResponse;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.openid.connect.sdk.rp.*;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;

@Singleton
public class RegisterServlet extends AbstractServlet {

    private final ProtectedResourceFlow flow;

    @Inject
    RegisterServlet(final TenantRepository tenantRepository, final ProtectedResourceFlow flow) {
        super(Endpoint.REGISTER, tenantRepository);
        this.flow = flow;
    }

    @Override
    protected HTTPResponse doGet(final HTTPRequest req) throws IOException {
        throw new RequestMethodException(req.getMethod().name());
    }

    @Override
    protected HTTPResponse doPost(final HTTPRequest req) {

        final OIDCClientRegistrationRequest request;
        try {
            request = OIDCClientRegistrationRequest.parse(req);
        } catch (ParseException e) {
            return new ClientRegistrationErrorResponse(e.getErrorObject()).toHTTPResponse();
        }

        final Tenant tenant = getTenant(req);

        return new OIDCClientInformationResponse(flow.registerClient(tenant, request.getOIDCClientMetadata(), req.getURL())).toHTTPResponse();
    }
}
