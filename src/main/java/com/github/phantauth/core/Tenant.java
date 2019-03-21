package com.github.phantauth.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
@Value.Modifiable
@JsonSerialize(as = Tenant.class)
@JsonDeserialize(builder = Tenant.Builder.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public interface Tenant {

    String SUB = "sub";
    @JsonProperty(SUB)
    @JsonView(Views.Phantom.class)
    String getSub();

    String ISSUER = "issuer";
    @JsonProperty(ISSUER)
    @JsonView(Views.Phantom.class)
    String getIssuer();

    String WEBSITE = "website";
    @JsonProperty(WEBSITE)
    @JsonView({Views.Phantom.class, Views.Config.class})
    String getWebsite();

    String TEMPLATE = "template";
    @JsonProperty(TEMPLATE)
    @JsonView({Views.Phantom.class, Views.Config.class})
    String getTemplate();

    String FACTORY = "factory";
    @JsonProperty(FACTORY)
    @JsonView({Views.Phantom.class, Views.Config.class})
    String getFactory();

    String FACTORIES = "factories";
    @JsonProperty(FACTORIES)
    @JsonView({Views.Phantom.class, Views.Config.class})
    List<String> getFactories();

    String DEPOT = "depot";
    @JsonProperty(DEPOT)
    @JsonView({Views.Phantom.class, Views.Config.class})
    String getDepot();

    String DEPOTS = "depots";
    @JsonProperty(DEPOTS)
    @JsonView({Views.Phantom.class, Views.Config.class})
    List<String> getDepots();

    String USERINFO = "userinfo";
    @JsonProperty(USERINFO)
    @JsonView({Views.Phantom.class, Views.Config.class})
    String getUserinfo();

    String ID="@id";
    @JsonProperty(ID)
    @JsonView(Views.Meta.class)
    String getId();

    String NAME = "name";
    @JsonProperty(NAME)
    @JsonView({Views.Phantom.class, Views.Config.class})
    String getName();

    String FLAGS = "flags";
    @JsonProperty(FLAGS)
    @JsonView({Views.Phantom.class, Views.Config.class})
    String getFlags();

    String LOGO = "logo";
    @JsonProperty(LOGO)
    @JsonView({Views.Phantom.class, Views.Config.class})
    String getLogo();

    String THEME = "theme";
    @JsonProperty(THEME)
    @JsonView({Views.Phantom.class,Views.Config.class})
    String getTheme();

    String SCRIPT = "script";
    @JsonProperty(SCRIPT)
    @JsonView({Views.Phantom.class,Views.Config.class})
    String getScript();

    String SHEET = "sheet";
    @JsonProperty(SHEET)
    @JsonView(Views.Config.class)
    String getSheet();

    String SUMMARY = "summary";
    @JsonProperty(SUMMARY)
    @JsonView(Views.Phantom.class)
    String getSummary();

    String ATTRIBUTION = "attribution";
    @JsonProperty(ATTRIBUTION)
    @JsonView(Views.Phantom.class)
    String getAttribution();

    String ABOUT = "about";
    @JsonProperty(ABOUT)
    @JsonView(Views.Phantom.class)
    String getAbout();

    String DOMAIN = "domain";
    @JsonProperty(DOMAIN)
    @JsonView(Views.Phantom.class)
    boolean isDomain();

    String SUBTENANT = "subtenant";
    @JsonProperty(SUBTENANT)
    @JsonView(Views.Phantom.class)
    boolean isSubtenant();

    class Builder extends TenantValue.Builder {
    }
}
