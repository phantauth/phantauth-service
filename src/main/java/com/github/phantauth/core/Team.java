package com.github.phantauth.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@Value.Modifiable
@JsonDeserialize(builder = Team.Builder.class)
@JsonSerialize(as = Team.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public interface Team extends Group<User> {

    class Builder extends TeamValue.BuilderBase {
    }
}
