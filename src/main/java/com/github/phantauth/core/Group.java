package com.github.phantauth.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import java.util.List;

public interface Group<T> {

    @JsonProperty(Property.SUB)
    @JsonView(Views.Phantom.class)
    String getSub();

    @JsonProperty(Property.NAME)
    @JsonView(Views.Phantom.class)
    String getName();

    @JsonProperty(Property.PROFILE)
    @JsonView(Views.Phantom.class)
    String getProfile();

    @JsonProperty(Property.LOGO)
    @JsonView(Views.Phantom.class)
    String getLogo();

    @JsonProperty(Property.LOGO_EMAIL)
    @JsonView(Views.Phantom.class)
    String getLogoEmail();

    @JsonProperty(Property.ID)
    @JsonView(Views.Meta.class)
    String getId();

    @JsonProperty(Property.MEMBERS)
    @JsonView(Views.Phantom.class)
    List<T> getMembers();
}
