package com.github.phantauth.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@Value.Modifiable
@JsonDeserialize(builder = Fleet.Builder.class)
@JsonSerialize(as = Fleet.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public interface Fleet extends Group<Client> {

    class Builder extends FleetValue.BuilderBase {
    }
}
