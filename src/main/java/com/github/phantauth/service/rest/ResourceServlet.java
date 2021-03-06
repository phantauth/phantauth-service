package com.github.phantauth.service.rest;

import com.github.phantauth.core.Scope;
import com.github.phantauth.core.TokenKind;
import com.github.phantauth.core.Views;
import com.github.phantauth.exception.MissingParameterException;
import com.github.phantauth.resource.Repository;
import com.github.phantauth.core.Tenant;
import com.github.phantauth.resource.TenantRepository;
import com.github.phantauth.resource.Endpoint;
import com.github.phantauth.service.*;
import com.github.phantauth.token.StorageToken;
import com.github.phantauth.token.TokenFactory;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

class ResourceServlet<T> extends AbstractServlet {
    static final String VAR_QUERY = "query";
    static final String VAR_PARAMS = "params";
    static final String VAR_WIDGET = "widget";

    private final Repository<T> repository;
    private final TokenFactory<T> tokenFactory;
    private final TemplateManager templateManager;
    private final Class<T> resourceType;
    private final String typename;

    ResourceServlet(final Class<T> resourceType, final Endpoint endpoint, final TenantRepository tenantRepository, final Repository<T> repository, final TokenFactory<T> tokenFactory, final TemplateManager templateManager) {
        super(endpoint, tenantRepository);
        this.resourceType = resourceType;
        this.repository = repository;
        this.tokenFactory = tokenFactory;
        this.templateManager = templateManager;
        this.typename = resourceType.getSimpleName().toLowerCase();
    }

    @Override
    protected HTTPResponse handleGet(final HTTPRequest req) throws IOException {
        final Param param = Param.build(req, endpoint);
        final Tenant tenant = getTenant(req);

        if ( param.getOperation() == null ) {
            return get(tenant, param);
        } else if ( param.getOperation().equals("token") ) {
            final String scope = req.getQueryParameters().get("scope");
            final Scope[] scopes = scope == null ? Scope.values() : Scope.split(scope);
            final String audience = req.getQueryParameters().get("audience");
            final String nonce = req.getQueryParameters().get("nonce");
            return getToken(tenant, param, audience, nonce, scopes);
        } else if( param.getOperation().equals("profile")) {
            return getProfile(tenant, param);
        } else if( param.getOperation().equals("tos") || param.getOperation().equals("policy")) {
            return getLegal(tenant, param.getOperation(), param);
        } else {
            return new HTTPResponse(HTTPResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected HTTPResponse handlePost(final HTTPRequest req) throws IOException {
        final T resource = Request.body(req, resourceType);
        return cache(Response.text(tokenFactory.newSelfieToken(resource)),0);
    }

    private HTTPResponse getToken(final Tenant tenant, final Param param, final String audience, final String nonce, final Scope[] scopes)  {
        if ( tokenFactory == null ) {
            return new HTTPResponse(HTTPResponse.SC_BAD_REQUEST);
        }

        final TokenKind tokenKind = param.getArgument() == null ? null : TokenKind.valueOf(param.getArgument().toUpperCase());

        if (tokenKind == null) {
            throw new MissingParameterException("kind","path");
        }

        String value;

        switch (tokenKind) {
            case ID:
                value = tokenFactory.newIdToken(tenant, repository.get(tenant, param.getSubject()), audience, nonce, null, null, Integer.MAX_VALUE, scopes).serialize();
                break;
            case SELFIE:
                value = tokenFactory.newSelfieToken(repository.get(tenant, param.getSubject()));
                break;
            case PLAIN:
                value = tokenFactory.newPlainToken(repository.get(tenant, param.getSubject()));
                break;
            default:
                value = tokenFactory.newStorageToken(StorageToken.Builder.of(tenant, tokenKind, param.getSubject(), scopes));
                break;
        }

        return cache(Response.text(value), 0);
    }

    private HTTPResponse get(final Tenant tenant, final Param param) throws IOException {
        final HTTPResponse response = Response.json(repository.get(tenant, param.getSubject()), Views.Meta.class);
        return cache(response, param.getSubject(), (int)TimeUnit.DAYS.toSeconds(1));
    }

    private HTTPResponse getProfile(final Tenant tenant, final Param param) {
        return processTemplate(tenant, typename, param);
    }

    private HTTPResponse getLegal(final Tenant tenant, final String template, final Param param) {
        return processTemplate(tenant, template, param);
    }

    private HTTPResponse processTemplate(final Tenant tenant, final String template, final Param param) {
        final HTTPResponse response =  Response.html(templateManager.process(
                tenant,
                template,
                getTemplateParams(tenant, param))
        );
        return cache(response, param.getSubject(), (int)TimeUnit.MILLISECONDS.toSeconds(templateManager.getTemplateTTL()));
    }

    protected Pair[] getTemplateParams(final Tenant tenant, final Param param) {
        return new Pair[] {
                Pair.of(VAR_QUERY, param.getQuery()),
                Pair.of(VAR_PARAMS, param.getParams()),
                Pair.of(VAR_WIDGET, Optional.ofNullable(param.getArgument()).orElse("")),
                Pair.of(typename, repository.get(tenant, param.getSubject()))
        };
    }
}
