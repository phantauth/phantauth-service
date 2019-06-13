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

    @JsonProperty(Property.SUB)
    @JsonView(Views.Phantom.class)
    String getSub();

    @JsonProperty(Property.ISSUER)
    @JsonView(Views.Phantom.class)
    String getIssuer();

    @JsonProperty(Property.WEBSITE)
    @JsonView({Views.Phantom.class, Views.Config.class})
    String getWebsite();

    @JsonProperty(Property.TEMPLATE)
    @JsonView({Views.Phantom.class, Views.Config.class})
    String getTemplate();

    @JsonProperty(Property.FACTORY)
    @JsonView({Views.Phantom.class, Views.Config.class})
    String getFactory();

    @JsonProperty(Property.FACTORIES)
    @JsonView({Views.Phantom.class, Views.Config.class})
    List<String> getFactories();

    @JsonProperty(Property.DEPOT)
    @JsonView({Views.Phantom.class, Views.Config.class})
    String getDepot();

    @JsonProperty(Property.DEPOTS)
    @JsonView({Views.Phantom.class, Views.Config.class})
    List<String> getDepots();

    @JsonProperty(Property.USERINFO)
    @JsonView({Views.Phantom.class, Views.Config.class})
    String getUserinfo();

    @JsonProperty(Property.ID)
    @JsonView(Views.Meta.class)
    String getId();

    @JsonProperty(Property.NAME)
    @JsonView({Views.Phantom.class, Views.Config.class})
    String getName();

    @JsonProperty(Property.FLAGS)
    @JsonView({Views.Phantom.class, Views.Config.class})
    String getFlags();

    @JsonProperty(Property.LOGO)
    @JsonView({Views.Phantom.class, Views.Config.class})
    String getLogo();

    @JsonProperty(Property.FAVICON)
    @JsonView({Views.Phantom.class, Views.Config.class})
    String getFavicon();

    @JsonProperty(Property.THEME)
    @JsonView({Views.Phantom.class,Views.Config.class})
    String getTheme();

    @JsonProperty(Property.SCRIPT)
    @JsonView({Views.Phantom.class,Views.Config.class})
    String getScript();

    @JsonProperty(Property.SHEET)
    @JsonView(Views.Config.class)
    String getSheet();

    @JsonProperty(Property.SUMMARY)
    @JsonView(Views.Phantom.class)
    String getSummary();

    @JsonProperty(Property.ATTRIBUTION)
    @JsonView(Views.Phantom.class)
    String getAttribution();

    @JsonProperty(Property.ABOUT)
    @JsonView(Views.Phantom.class)
    String getAbout();

    @JsonProperty(Property.DOMAIN)
    @JsonView(Views.Phantom.class)
    boolean isDomain();

    @JsonProperty(Property.SUBTENANT)
    @JsonView(Views.Phantom.class)
    boolean isSubtenant();

    class Builder extends TenantValue.BuilderBase {
    }
}
