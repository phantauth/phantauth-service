package com.github.phantauth.service.auth;

import com.github.phantauth.core.*;
import com.github.phantauth.core.Scope;
import com.github.phantauth.flow.AuthorizationFlow;
import com.github.phantauth.indie.IndieAuthResponseTypeValue;
import com.github.phantauth.resource.Repository;
import com.github.phantauth.resource.TenantRepository;
import com.github.phantauth.resource.Endpoint;
import com.github.phantauth.service.AbstractServlet;
import com.github.phantauth.service.TemplateManager;
import com.github.phantauth.token.StorageToken;
import com.github.phantauth.token.UserTokenFactory;
import com.google.common.flogger.FluentLogger;
import com.nimbusds.oauth2.sdk.*;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.util.URLUtils;
import com.nimbusds.openid.connect.sdk.AuthenticationRequest;
import com.nimbusds.openid.connect.sdk.ClaimsRequest;
import org.apache.commons.lang3.tuple.Pair;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Singleton
public class AuthorizationServlet extends AbstractServlet {
    private static final String TEMPLATE_SELECT = "select";
    private static final String TEMPLATE_LOGIN = "login";
    private static final String TEMPLATE_CONSENT = "consent";
    private static final String VAR_REQUEST = "request";
    private static final String VAR_DOMAIN = "domain";
    private static final String VAR_USER = "user";
    private static final String VAR_CLIENT = "client";
    private static final String PARAM_RESPONSE_TYPE = "response_type";
    private static final String PARAM_CONSENT = "consent";
    private static final String PARAM_ME = "me";
    private static final String PARAM_CODE = "code";
    private static final String PARAM_SCOPE = "scope";
    private static final String PARAM_CLAIMS = "claims";
    private static final String PARAM_LOGIN_HINT = "login_hint";
    private static final String CONSENT_CANCEL = "cancel";

    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    private final AuthorizationFlow flow;
    private final TemplateManager templateManager;
    private final Repository<User> userRepository;
    private final UserTokenFactory userTokenFactory;
    private final Repository<Client> clientRepository;
    private final Repository<Domain> domainRepository;

    @Inject
    AuthorizationServlet(final TenantRepository tenantRepository, final AuthorizationFlow flow, final TemplateManager templateManager, final Repository<User> userRepository, final UserTokenFactory userTokenFactory, final Repository<Client> clientRepository, final Repository<Domain> domainRepository) {
        super(Endpoint.AUTHORIZATION, tenantRepository);
        this.flow = flow;
        this.templateManager = templateManager;
        this.userRepository = userRepository;
        this.userTokenFactory = userTokenFactory;
        this.clientRepository = clientRepository;
        this.domainRepository = domainRepository;
    }

    private Map<String, String> appendParam(final HTTPRequest req, final String name, final String value) {
        req.setQuery(String.format("%s&%s=%s", req.getQuery(), name, value));
        return req.getQueryParameters();
    }

    boolean fixAndCheckIndie(final HTTPRequest req) {

        Map<String, String> params = req.getQueryParameters();

        // IndieAuth request?
        if (!params.containsKey(PARAM_CODE) && !params.containsKey(PARAM_ME)) {
            return false;
        }

        // IndieAuth defaults response_type to "id"
        if (params.get(PARAM_RESPONSE_TYPE) == null) {
            params = appendParam(req, PARAM_RESPONSE_TYPE, IndieAuthResponseTypeValue.ID.getValue());
        } else if (!params.get(PARAM_RESPONSE_TYPE).equals(IndieAuthResponseTypeValue.ID.getValue())) {
            return false;
        }

        // fictive indieauth scope
        if (!params.containsKey(PARAM_SCOPE)) {
            params = appendParam(req, PARAM_SCOPE, Scope.INDIEAUTH.toString());
        }

        // convert ~ to /me/
        if (params.containsKey(PARAM_ME)) {
            params.put(PARAM_ME, params.get(PARAM_ME).replace("/~", Endpoint.ME.getPath() + '/'));
            req.setQuery(URLUtils.serializeParameters(params));
            params = req.getQueryParameters();
        }

        // create login_hint from me for login page
        if (params.containsKey(PARAM_ME) && !params.containsKey(PARAM_LOGIN_HINT)) {
            final String me = params.get(PARAM_ME);
            final int idx = me.indexOf(Endpoint.ME.getPath());
            if (idx >= 0) {
                appendParam(req, PARAM_LOGIN_HINT, me.substring(idx + Endpoint.ME.getPath().length() + 1));
            }
        }

        return true;
    }

    protected HTTPResponse handleGet(final HTTPRequest req) {

        final AuthorizationRequest request;

        boolean isIndie = fixAndCheckIndie(req);

        try {
            if (isIndie) {
                request = AuthorizationRequest.parse(req);
            } else {
                mapClaimsToScopes(req);
                request = AuthenticationRequest.parse(req);
            }
        } catch (ParseException e) {
            return toResponse(e);
        }

        if (CONSENT_CANCEL.equals(request.getCustomParameter(PARAM_CONSENT))) {
            return new AuthorizationErrorResponse(request.getRedirectionURI(), OAuth2Error.ACCESS_DENIED, request.getState(), request.getResponseMode()).toHTTPResponse();
        }

        final Tenant tenant = getTenant(req);

        return impliesSelect(tenant)
                ? doSelect(tenant, request)
                : flow.impliesLogin(request)
                ? doLogin(tenant, request)
                : flow.impliesConsent(request)
                ? doConsent(tenant, request)
                : flow.handle(request).toHTTPResponse();
    }

    private boolean impliesSelect(final Tenant tenant) {
        return tenant.isDomain() && ! tenant.isSubtenant();
    }

    private HTTPResponse doSelect(final Tenant tenant, final AuthorizationRequest request) {
        final Domain domain = domainRepository.get(tenant, tenant.getSub());
        return com.github.phantauth.service.Response.html(templateManager.process(tenant, TEMPLATE_SELECT,
                Pair.of(VAR_REQUEST, request),
                Pair.of(VAR_DOMAIN, domain)
        ));
    }

    private HTTPResponse doLogin(final Tenant tenant, final AuthorizationRequest request) {
        final User user = userRepository.get(tenant, (String) null);
        return com.github.phantauth.service.Response.html(templateManager.process(tenant, TEMPLATE_LOGIN,
                Pair.of(VAR_REQUEST, request),
                Pair.of(VAR_USER, user)
        ));
    }

    private HTTPResponse doConsent(final Tenant tenant, final AuthorizationRequest request) {
        final Client client = clientRepository.get(tenant, request.getClientID().getValue());
        final StorageToken token = userTokenFactory.parseStorageToken(request.getCustomParameter(TokenKind.LOGIN.getName()), TokenKind.LOGIN);
        final User user = userRepository.get(tenant, token.getSubject());
        return com.github.phantauth.service.Response.html(templateManager.process(tenant, TEMPLATE_CONSENT,
                Pair.of(VAR_REQUEST, request),
                Pair.of(VAR_USER, user),
                Pair.of(VAR_CLIENT, client)
        ));
    }

    // quick & dirty implementation of claims request support
    private void mapClaimsToScopes(final HTTPRequest req) {
        final Map<String, String> params = req.getQueryParameters();
        if (!params.containsKey(PARAM_CLAIMS)) {
            return;
        }

        final ClaimsRequest claimsRequest;
        try {
            claimsRequest = ClaimsRequest.parse(params.get(PARAM_CLAIMS));
        } catch (ParseException e) {
            return;
        }

        final Set<String> claims = new HashSet<>();

        claims.addAll(claimsRequest.getIDTokenClaimNames(false));
        claims.addAll(claimsRequest.getUserInfoClaimNames(false));

        if (claims.isEmpty()) {
            return;
        }

        final Set<String> scopes = new HashSet<>();

        for (Scope scope : Scope.values()) {
            for (Claim claim : scope.getClaims()) {
                if (claims.contains(claim.getName())) {
                    scopes.add(scope.getName());
                    break;
                }
            }
        }

        if (params.containsKey(PARAM_SCOPE)) {
            for (Scope scope : Scope.split(params.get(PARAM_SCOPE))) {
                scopes.add(scope.getName());
            }
        }

        params.put(PARAM_SCOPE, String.join(" ", scopes));
        req.setQuery(URLUtils.serializeParameters(params));
    }

    @Override
    protected HTTPResponse handlePost(final HTTPRequest req) {
        return handleGet(req);
    }
}
