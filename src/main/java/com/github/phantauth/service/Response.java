package com.github.phantauth.service;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.phantauth.core.Views;
import com.github.phantauth.exception.ConfigurationException;
import com.github.phantauth.exception.PhantAuthException;
import com.google.common.base.CaseFormat;
import com.google.common.base.Strings;
import com.nimbusds.oauth2.sdk.ErrorObject;
import com.nimbusds.oauth2.sdk.http.CommonContentTypes;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.StringUtils;

import javax.mail.internet.ContentType;
import javax.mail.internet.ParseException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;

public class Response {

    private Response() {
        // no instances
    }

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .disable(MapperFeature.DEFAULT_VIEW_INCLUSION)
            .registerModule(new JavaTimeModule());

    private static final ContentType TEXT;
    private static final ContentType HTML;
    private static final ContentType JSON;
    private static final ContentType JRD;

    static {
        JSON = CommonContentTypes.APPLICATION_JSON;
        try {
            TEXT = new ContentType("text/plain");
            HTML = new ContentType("text/html");
            JRD = new ContentType("application/jrd+json");
        } catch (ParseException e) {
            throw new ConfigurationException("unable to parse standard content types");
        }
    }

    public static HTTPResponse text(final String value) {
        return text(value, TEXT);
    }

    public static HTTPResponse text(final String value, final ContentType contentType) {
        final HTTPResponse resp = new HTTPResponse(HTTPResponse.SC_OK);
        resp.setContent(value);
        resp.setContentType(contentType);
        return resp;
    }

    public static HTTPResponse html(final String value) {
        return text(value, HTML);
    }

    public static HTTPResponse json(final Object value) throws IOException {
        return json(value, null);
    }

    public static HTTPResponse json(final Object value, final Class<?> view) throws IOException {
        return text(MAPPER.writerWithView(view == null ? Views.Standard.class : view).writeValueAsString(value), JSON);
    }

    public static HTTPResponse json(final JSONObject value) {
        return text(value.toJSONString(), JSON);
    }

    public static HTTPResponse jrd(final JSONObject value) {
        return text(value.toJSONString(), JRD);
    }

    public static HTTPResponse map(final Exception exception) {
        final HTTPResponse response = new HTTPResponse(HTTPResponse.SC_SERVER_ERROR);

        final ErrorObject error;
        if ( exception instanceof PhantAuthException ) {
            error = ((PhantAuthException)exception).getErrorObject();
        } else {
            final String code = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, StringUtils.removeEnd(exception.getClass().getSimpleName(), Exception.class.getSimpleName()));
            error = new ErrorObject(code).setDescription(exception.getMessage());
        }

        response.setContentType(JSON);
        response.setContent(error.toJSONObject().toJSONString());
        return response;
    }

    public static HTTPResponse redirect(final URI uri, final String subject) {
        final HTTPResponse response = new HTTPResponse(Strings.isNullOrEmpty(subject) || subject.charAt(0) == ';' ? HttpServletResponse.SC_MOVED_TEMPORARILY : HttpServletResponse.SC_MOVED_PERMANENTLY);
        response.setLocation(uri);
        return response;
    }

}
