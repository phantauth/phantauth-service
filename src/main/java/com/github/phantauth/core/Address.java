package com.github.phantauth.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.immutables.value.Value;

/**
 * The Address represents a physical mailing address. Implementations MAY return only a subset of the fields of an address, depending upon the information available and the End-User&#39;s privacy preferences. For example, the country and region might be returned without returning more fine-grained address information.
 */
@ApiModel(description = "The Address represents a physical mailing address. Implementations MAY return only a subset of the fields of an address, depending upon the information available and the End-User's privacy preferences. For example, the country and region might be returned without returning more fine-grained address information. ")
@Value.Immutable
@Value.Modifiable
@JsonDeserialize(builder = Address.Builder.class)
@JsonSerialize(as = Address.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public interface Address {
    /**
     * Full mailing address, formatted for display or use on a mailing label. This field MAY contain multiple lines, separated by newlines. Newlines can be represented either as a carriage return/line feed pair or as a single line feed character.
     *
     * @return formatted
     **/
    @JsonProperty("formatted")
    @ApiModelProperty(value = "Full mailing address, formatted for display or use on a mailing label. This field MAY contain multiple lines, separated by newlines. Newlines can be represented either as a carriage return/line feed pair or as a single line feed character.")
    @JsonView(Views.Standard.class)
    String getFormatted();

    /**
     * Full street address component, which MAY include house number, street name, Post Office Box, and multi-line extended street address information. This field MAY contain multiple lines, separated by newlines. Newlines can be represented either as a carriage return/line feed pair or as a single line feed character.
     *
     * @return streetAddress
     **/
    @JsonProperty("street_address")
    @ApiModelProperty(value = "Full street address component, which MAY include house number, street name, Post Office Box, and multi-line extended street address information. This field MAY contain multiple lines, separated by newlines. Newlines can be represented either as a carriage return/line feed pair or as a single line feed character.")
    @JsonView(Views.Standard.class)
    String getStreetAddress();

    /**
     * City or locality component.
     *
     * @return locality
     **/
    @JsonProperty("locality")
    @ApiModelProperty(value = "City or locality component.")
    @JsonView(Views.Standard.class)
    String getLocality();

    /**
     * State, province, prefecture, or region component.
     *
     * @return region
     **/
    @JsonProperty("region")
    @ApiModelProperty(value = "State, province, prefecture, or region component.")
    @JsonView(Views.Standard.class)
    String getRegion();

    /**
     * Zip code or postal code component.
     *
     * @return postalCode
     **/
    @JsonProperty("postal_code")
    @ApiModelProperty(value = "Zip code or postal code component.")
    @JsonView(Views.Standard.class)
    String getPostalCode();

    /**
     * Country name component.
     *
     * @return country
     **/
    @JsonProperty("country")
    @ApiModelProperty(value = "Country name component.")
    @JsonView(Views.Standard.class)
    String getCountry();

    class Builder extends AddressValue.Builder {
    }
}
