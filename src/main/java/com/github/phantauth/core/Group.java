package com.github.phantauth.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import java.util.List;

public interface Group<T> {

    String SUB = "sub";
    @JsonProperty(SUB)
    @JsonView(Views.Phantom.class)
    String getSub();

    String NAME = "name";
    @JsonProperty(NAME)
    @JsonView(Views.Phantom.class)
    String getName();

    String PROFILE = "profile";
    @JsonProperty(PROFILE)
    @JsonView(Views.Phantom.class)
    String getProfile();

    String LOGO = "logo";
    @JsonProperty(LOGO)
    @JsonView(Views.Phantom.class)
    String getLogo();

    String LOGO_EMAIL = "logo_email";
    @JsonProperty(LOGO_EMAIL)
    @JsonView(Views.Phantom.class)
    String getLogoEmail();

    String ID="@id";
    @JsonProperty(ID)
    @JsonView(Views.Meta.class)
    String getId();

    String MEMBERS = "members";
    @JsonProperty(MEMBERS)
    @JsonView(Views.Phantom.class)
    List<T> getMembers();
}
