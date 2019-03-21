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
import com.github.phantauth.core.User;
import com.github.phantauth.resource.Repository;
import com.github.phantauth.resource.TenantRepository;
;
import com.github.phantauth.resource.Endpoint;
import com.github.phantauth.service.AbstractServlet;
import com.github.phantauth.token.TokenManager;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.TokenIntrospectionErrorResponse;
import com.nimbusds.oauth2.sdk.TokenIntrospectionRequest;
import com.nimbusds.oauth2.sdk.TokenIntrospectionSuccessResponse;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.oauth2.sdk.id.Subject;
import com.nimbusds.oauth2.sdk.token.AccessTokenType;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Date;

@Singleton
public class IntrospectionServlet extends AbstractServlet {

    final TokenManager tokenManager;
    final Repository<User> repository;

    @Inject
    IntrospectionServlet(final TenantRepository tenantRepository, final TokenManager tokenManager, final Repository<User> userRepository) {
        super(Endpoint.INTROSPECTION, tenantRepository);
        this.tokenManager = tokenManager;
        this.repository = userRepository;
    }

    protected HTTPResponse doGet(final HTTPRequest req) {
        final Tenant tenant = getTenant(req);

        final TokenIntrospectionRequest request;
        try {
            request = TokenIntrospectionRequest.parse(req);
        } catch (ParseException e) {
            return new TokenIntrospectionErrorResponse(e.getErrorObject()).toHTTPResponse();
        }

        TokenIntrospectionSuccessResponse.Builder builder;

        try {
            final SignedJWT jwt = SignedJWT.parse(request.getToken().getValue());
            final JWTClaimsSet claims = jwt.getJWTClaimsSet();
            final Date exp = claims.getExpirationTime();

            final boolean valid = (exp.getTime() > System.currentTimeMillis()) && tokenManager.hmacVerify(jwt);

            builder = new TokenIntrospectionSuccessResponse.Builder(valid);

            if ( valid ) {
                builder.subject(new Subject(claims.getSubject()))
                        .issuer(new Issuer(tenant.getIssuer()))
                        .tokenType(AccessTokenType.BEARER)
                        .expirationTime(exp)
                        .issueTime(claims.getIssueTime())
                        .username(repository.get(tenant, claims.getSubject()).getName())
                        .scope(new com.nimbusds.oauth2.sdk.Scope(claims.getStringClaim("scope")));
            }
        } catch (java.text.ParseException e1) {
            builder = new TokenIntrospectionSuccessResponse.Builder(false);
        }

        return builder.build().toHTTPResponse();
    }

    @Override
    protected HTTPResponse doPost(final HTTPRequest req) {
        return doGet(req);
    }
}
