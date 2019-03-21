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

package com.github.phantauth.service;


import com.github.phantauth.core.TokenKind;
import com.github.phantauth.core.Tenant;
import com.github.phantauth.resource.Name;
import com.github.phantauth.resource.TenantRepository;
import com.github.phantauth.resource.Endpoint;
import com.github.phantauth.service.NoCacheFilter;
import com.github.phantauth.service.Response;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.http.CommonContentTypes;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.http.ServletUtils;
import com.nimbusds.oauth2.sdk.util.URLUtils;
import net.minidev.json.JSONObject;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public abstract class AbstractServlet extends HttpServlet {

    protected static final String PARAM_TENANT = "tenant";
    protected static final String PARAM_ISSUER = "issuer";
    protected static final String PARAM_UI_LOCALES = "ui_locales";

    protected final Endpoint endpoint;
    protected final TenantRepository tenantRepository;

    protected AbstractServlet(final Endpoint endpoint, final TenantRepository tenantRepository) {
        this.endpoint = endpoint;
        this.tenantRepository = tenantRepository;
    }

    protected abstract HTTPResponse doGet(final HTTPRequest req) throws IOException;

    protected abstract HTTPResponse doPost(final HTTPRequest req) throws IOException;

    private String getCookieValue(final HttpServletRequest request, final String name) {
        final Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private HTTPRequest wrap(final HttpServletRequest servletRequest) throws IOException{
        final HTTPRequest request = ServletUtils.createHTTPRequest(servletRequest);

        final String cookie;
        if ( ! request.getQueryParameters().containsKey(TokenKind.LOGIN.getName()) ) {
            cookie = getCookieValue(servletRequest, TokenKind.LOGIN.getName());
        } else {
            cookie = null;
        }

        final Tenant tenant = getTenant(servletRequest);

        boolean json = request.getContentType() != null && request.getContentType().match(CommonContentTypes.APPLICATION_JSON);

        if ( json ) {
            try {
                final JSONObject params = request.getQueryAsJSONObject();
                params.put(PARAM_TENANT, tenant.getSub());
                params.put(PARAM_ISSUER, tenant.getIssuer());
                if ( cookie != null ) {
                    params.put(TokenKind.LOGIN.getName(), cookie);
                }
                // Azure AD B2C workaround
                params.remove(PARAM_UI_LOCALES);
                request.setQuery(params.toJSONString());
            } catch (ParseException e) {
                throw new IOException(e);
            }
        } else {
            final Map<String, String> params = request.getQueryParameters();
            params.put(PARAM_TENANT, tenant.getSub());
            params.put(PARAM_ISSUER, tenant.getIssuer());
            if ( cookie != null ) {
                params.put(TokenKind.LOGIN.getName(), cookie);
            }
            // Azure AD B2C workaround
            params.remove(PARAM_UI_LOCALES);
            request.setQuery(URLUtils.serializeParameters(params));
        }

        return request;
    }

    private void apply(final HTTPResponse response, HttpServletResponse servletResponse) throws IOException {
        ServletUtils.applyHTTPResponse(response, servletResponse);
    }

    @Override
    protected void doGet(final HttpServletRequest servletRequest, final HttpServletResponse servletResponse) throws IOException {
        final HTTPRequest request = wrap(servletRequest);
        HTTPResponse response;

        try {
            response = doGet(request);
        } catch (Exception e) {
            servletRequest.getServletContext().log(e.getMessage(), e);
            response = Response.map(e);
        }

        apply(response, servletResponse);
    }

    @Override
    protected void doPost(final HttpServletRequest servletRequest, final HttpServletResponse servletResponse) throws IOException {
        final HTTPRequest request = wrap(servletRequest);
        HTTPResponse response;

        try {
            response = doPost(request);
        } catch (Exception e) {
            servletRequest.getServletContext().log(e.getMessage(), e);
            response = Response.map(e);
        }

        apply(response, servletResponse);
    }

    protected Tenant getTenant(final HttpServletRequest servletRequest) {
        return tenantRepository.get(servletRequest.getParameter(PARAM_TENANT));
    }

    protected Tenant getTenant(final HTTPRequest request) {
        return tenantRepository.get(request.getQueryParameters().get(PARAM_TENANT));
    }

    protected Tenant getSubTenant(final HTTPRequest request) {
        return getSubTenant(getTenant(request));
    }

    protected Tenant getSubTenant(final HttpServletRequest servletRequest) {
        return getSubTenant(getTenant(servletRequest));
    }

    protected Tenant getSubTenant(final Tenant tenant) {
        if ( ! tenant.isSubtenant() ) {
            return tenant;
        }

        return tenantRepository.get(Name.parse(tenant.getSub()).getInstance());
    }

    protected HTTPResponse cache(final HTTPResponse response, final int maxAge) {
        final String value = maxAge <=0 ? NoCacheFilter.NOCACHE : String.format("public,max-age=%d,s-maxage=%d", maxAge, maxAge);
        response.setHeader(NoCacheFilter.CACHE_CONTROL, value);
        return response;
    }

    protected HTTPResponse cache(final HTTPResponse response, final String subject, final int maxage) {
        return cache(response, subject == null || subject.charAt(0) == ';' ? 0 : maxage);
    }
}

