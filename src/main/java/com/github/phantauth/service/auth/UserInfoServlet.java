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

import com.github.phantauth.flow.ProtectedResourceFlow;
import com.github.phantauth.resource.TenantRepository;
import com.github.phantauth.resource.Endpoint;
import com.github.phantauth.service.AbstractServlet;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.openid.connect.sdk.UserInfoErrorResponse;
import com.nimbusds.openid.connect.sdk.UserInfoRequest;
import com.nimbusds.openid.connect.sdk.UserInfoSuccessResponse;
import com.nimbusds.openid.connect.sdk.claims.UserInfo;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class UserInfoServlet extends AbstractServlet {

    private final ProtectedResourceFlow flow;

    @Inject
    UserInfoServlet(final TenantRepository tenantRepository, final ProtectedResourceFlow flow) {
        super(Endpoint.USERINFO, tenantRepository);
        this.flow = flow;
    }

    protected HTTPResponse doGet(final HTTPRequest req) {

        final UserInfoRequest request;
        try {
            request = UserInfoRequest.parse(req);
        } catch (ParseException e) {
            return new UserInfoErrorResponse(e.getErrorObject()).toHTTPResponse();
        }

        final UserInfo user = flow.getUserInfo(getTenant(req), request.getAccessToken());

        return new UserInfoSuccessResponse(user).toHTTPResponse();
    }

    @Override
    protected HTTPResponse doPost(final HTTPRequest req) {
        return doGet(req);
    }
}
