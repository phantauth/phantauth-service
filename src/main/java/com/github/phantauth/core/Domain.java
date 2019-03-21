package com.github.phantauth.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@Value.Modifiable
@JsonDeserialize(builder = Domain.Builder.class)
@JsonSerialize(as = Domain.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public interface Domain extends Group<Tenant> {

    class Builder extends DomainValue.Builder {
    }
}
