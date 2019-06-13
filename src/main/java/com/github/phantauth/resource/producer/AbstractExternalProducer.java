package com.github.phantauth.resource.producer;

import com.damnhandy.uri.template.UriTemplate;
import com.github.phantauth.resource.Name;
import com.github.phantauth.resource.Producer;
import com.google.common.base.Strings;

import java.util.Collection;

public abstract class AbstractExternalProducer<T> implements Producer<T> {
    protected static final String PARAM_KIND = "kind";
    protected static final String PARAM_NAME = "name";

    protected final Class<T> type;
    protected final String typeName;

    protected AbstractExternalProducer(final Class<T> type) {
        this.type = type;
        this.typeName = type.getSimpleName().toLowerCase();
    }

    protected boolean hasTemplate(final String value, final Collection<String> supported) {
        return ! Strings.isNullOrEmpty(value) && supported.contains(typeName);
    }

    protected UriTemplate templateFrom(final String value, final Collection<String> supported) {
        return hasTemplate(value, supported) ? UriTemplate.fromTemplate(value) : null;
    }

    protected String expand(final UriTemplate template, final Name name) {
        return template
                .set(PARAM_KIND, typeName)
                .set(PARAM_NAME, name.getSubject()).expand();
    }

    protected String expand(final UriTemplate template) {
        return template
                .set(PARAM_KIND, typeName).expand();
    }
}
