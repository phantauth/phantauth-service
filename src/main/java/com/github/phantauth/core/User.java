package com.github.phantauth.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;
import com.google.common.primitives.Ints;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.immutables.value.Value;

/**
 * An OpenID Connect compatible user account data, in addition to standard claims, contains user&#39;s password.
 */
@ApiModel(description = "An OpenID Connect compatible user account data, in addition to standard claims, contains user's password. ")
@Value.Immutable
@Value.Modifiable
@JsonDeserialize(builder = User.Builder.class)
@JsonSerialize(as = User.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public interface User {
    /**
     * Subject - Identifier for the End-User at the Issuer.
     *
     * @return sub
     **/
    @JsonProperty("sub")
    @ApiModelProperty(value = "Subject - Identifier for the End-User at the Issuer. ")
    @JsonView(Views.Standard.class)
    String getSub();

    /**
     * End-User&#39;s full name in displayable form including all name parts, possibly including titles and suffixes, ordered according to the End-User&#39;s locale and preferences.
     *
     * @return name
     **/
    @JsonProperty("name")
    @ApiModelProperty(value = "End-User's full name in displayable form including all name parts, possibly including titles and suffixes, ordered according to the End-User's locale and preferences. ")
    @JsonView(Views.Standard.class)
    String getName();

    /**
     * Given name(s) or first name(s) of the End-User. Note that in some cultures, people can have multiple given names; all can be present, with the names being separated by space characters.
     *
     * @return givenName
     **/
    @JsonProperty("given_name")
    @ApiModelProperty(value = "Given name(s) or first name(s) of the End-User. Note that in some cultures, people can have multiple given names; all can be present, with the names being separated by space characters. ")
    @JsonView(Views.Standard.class)
    String getGivenName();

    /**
     * Surname(s) or last name(s) of the End-User. Note that in some cultures, people can have multiple family names or no family name; all can be present, with the names being separated by space characters.
     *
     * @return familyName
     **/
    @JsonProperty("family_name")
    @ApiModelProperty(value = "Surname(s) or last name(s) of the End-User. Note that in some cultures, people can have multiple family names or no family name; all can be present, with the names being separated by space characters. ")
    @JsonView(Views.Standard.class)
    String getFamilyName();

    /**
     * Middle name(s) of the End-User. Note that in some cultures, people can have multiple middle names; all can be present, with the names being separated by space characters. Also note that in some cultures, middle names are not used.
     *
     * @return middleName
     **/
    @JsonProperty("middle_name")
    @ApiModelProperty(value = "Middle name(s) of the End-User. Note that in some cultures, people can have multiple middle names; all can be present, with the names being separated by space characters. Also note that in some cultures, middle names are not used. ")
    @JsonView(Views.Standard.class)
    String getMiddleName();

    /**
     * Casual name of the End-User that may or may not be the same as the given_name. For instance, a nickname value of Mike might be returned alongside a given_name value of Michael.
     *
     * @return nickname
     **/
    @JsonProperty("nickname")
    @ApiModelProperty(value = "Casual name of the End-User that may or may not be the same as the given_name. For instance, a nickname value of Mike might be returned alongside a given_name value of Michael. ")
    @JsonView(Views.Standard.class)
    String getNickname();

    /**
     * Shorthand name by which the End-User wishes to be referred to at the RP, such as janedoe or j.doe. This value MAY be any valid JSON string including special characters such as @, /, or whitespace.
     *
     * @return preferredUsername
     **/
    @JsonProperty("preferred_username")
    @ApiModelProperty(value = "Shorthand name by which the End-User wishes to be referred to at the RP, such as janedoe or j.doe. This value MAY be any valid JSON string including special characters such as @, /, or whitespace. ")
    @JsonView(Views.Standard.class)
    String getPreferredUsername();

    /**
     * URL of the End-User&#39;s profile page. The contents of this Web page SHOULD be about the End-User.
     *
     * @return profile
     **/
    @JsonProperty("profile")
    @ApiModelProperty(value = "URL of the End-User's profile page. The contents of this Web page SHOULD be about the End-User. ")
    @JsonView(Views.Standard.class)
    String getProfile();

    /**
     * URL of the End-User&#39;s profile picture. This URL MUST refer to an image file (for example, a PNG, JPEG, or GIF image file), rather than to a Web page containing an image. Note that this URL SHOULD specifically reference a profile photo of the End-User suitable for displaying when describing the End-User, rather than an arbitrary photo taken by the End-User.
     *
     * @return picture
     **/
    @JsonProperty("picture")
    @ApiModelProperty(value = "URL of the End-User's profile picture. This URL MUST refer to an image file (for example, a PNG, JPEG, or GIF image file), rather than to a Web page containing an image. Note that this URL SHOULD specifically reference a profile photo of the End-User suitable for displaying when describing the End-User, rather than an arbitrary photo taken by the End-User. ")
    @JsonView(Views.Standard.class)
    String getPicture();

    /**
     * URL of the End-User&#39;s Web page or blog. This Web page SHOULD contain information published by the End-User or an organization that the End-User is affiliated with.
     *
     * @return website
     **/
    @JsonProperty("website")
    @ApiModelProperty(value = "URL of the End-User's Web page or blog. This Web page SHOULD contain information published by the End-User or an organization that the End-User is affiliated with. ")
    @JsonView(Views.Standard.class)
    String getWebsite();

    /**
     * End-User&#39;s preferred e-mail address. Its value MUST conform to the RFC 5322 [RFC5322] addr-spec syntax.
     *
     * @return email
     **/
    @JsonProperty("email")
    @ApiModelProperty(value = "End-User's preferred e-mail address. Its value MUST conform to the RFC 5322 [RFC5322] addr-spec syntax. ")
    @JsonView(Views.Standard.class)
    String getEmail();

    /**
     * True if the End-User&#39;s e-mail address has been verified; otherwise false. When this Claim Value is true, this means that the OP took affirmative steps to ensure that this e-mail address was controlled by the End-User at the time the verification was performed. The means by which an e-mail address is verified is context-specific, and dependent upon the trust framework or contractual agreements within which the parties are operating.
     *
     * @return emailVerified
     **/
    @JsonProperty("email_verified")
    @ApiModelProperty(value = "True if the End-User's e-mail address has been verified; otherwise false. When this Claim Value is true, this means that the OP took affirmative steps to ensure that this e-mail address was controlled by the End-User at the time the verification was performed. The means by which an e-mail address is verified is context-specific, and dependent upon the trust framework or contractual agreements within which the parties are operating. ")
    @JsonView(Views.Standard.class)
    Boolean getEmailVerified();

    /**
     * End-User&#39;s gender. Values defined by this specification are female and male. Other values MAY be used when neither of the defined values are applicable.
     *
     * @return gender
     **/
    @JsonProperty("gender")
    @ApiModelProperty(value = "End-User's gender. Values defined by this specification are female and male. Other values MAY be used when neither of the defined values are applicable. ")
    @JsonView(Views.Standard.class)
    String getGender();

    /**
     * End-User&#39;s birthday, represented as an ISO 8601:2004 [ISO8601‑2004] YYYY-MM-DD format. The year MAY be 0000, indicating that it is omitted. To represent only the year, YYYY format is allowed. Note that depending on the underlying platform&#39;s date related function, providing just year can result in varying month and day, so the implementers need to take this factor into account to correctly process the dates.
     *
     * @return birthdate
     **/
    @JsonProperty("birthdate")
    @ApiModelProperty(value = "End-User's birthday, represented as an ISO 8601:2004 [ISO8601‑2004] YYYY-MM-DD format. The year MAY be 0000, indicating that it is omitted. To represent only the year, YYYY format is allowed. Note that depending on the underlying platform's date related function, providing just year can result in varying month and day, so the implementers need to take this factor into account to correctly process the dates. ")
    @JsonView(Views.Standard.class)
    String getBirthdate();

    /**
     * String from zoneinfo [zoneinfo] time zone database representing the End-User&#39;s time zone. For example, Europe/Paris or America/Los_Angeles.
     *
     * @return zoneinfo
     **/
    @JsonProperty("zoneinfo")
    @ApiModelProperty(value = "String from zoneinfo [zoneinfo] time zone database representing the End-User's time zone. For example, Europe/Paris or America/Los_Angeles. ")
    @JsonView(Views.Standard.class)
    String getZoneinfo();

    /**
     * End-User&#39;s locale, represented as a BCP47 [RFC5646] language tag. This is typically an ISO 639-1 Alpha-2 [ISO639‑1] language code in lowercase and an ISO 3166-1 Alpha-2 [ISO3166‑1] country code in uppercase, separated by a dash. For example, en-US or fr-CA. As a compatibility note, some implementations have used an underscore as the separator rather than a dash, for example, en_US
     *
     * @return locale
     **/
    @JsonProperty("locale")
    @ApiModelProperty(value = "End-User's locale, represented as a BCP47 [RFC5646] language tag. This is typically an ISO 639-1 Alpha-2 [ISO639‑1] language code in lowercase and an ISO 3166-1 Alpha-2 [ISO3166‑1] country code in uppercase, separated by a dash. For example, en-US or fr-CA. As a compatibility note, some implementations have used an underscore as the separator rather than a dash, for example, en_US ")
    @JsonView(Views.Standard.class)
    String getLocale();

    /**
     * End-User&#39;s preferred telephone number. E.164 [E.164] is RECOMMENDED as the format of this Claim, for example, +1 (425) 555-1212 or +56 (2) 687 2400. If the phone number contains an extension, it is RECOMMENDED  that the extension be represented using the RFC 3966 [RFC3966] extension syntax, for example, +1 (604) 555-1234;ext&#x3D;5678.
     *
     * @return phoneNumber
     **/
    @JsonProperty("phone_number")
    @ApiModelProperty(value = "End-User's preferred telephone number. E.164 [E.164] is RECOMMENDED as the format of this Claim, for example, +1 (425) 555-1212 or +56 (2) 687 2400. If the phone number contains an extension, it is RECOMMENDED  that the extension be represented using the RFC 3966 [RFC3966] extension syntax, for example, +1 (604) 555-1234;ext=5678. ")
    @JsonView(Views.Standard.class)
    String getPhoneNumber();

    /**
     * True if the End-User&#39;s phone number has been verified; otherwise false. When this Claim Value is true, this means that the OP took affirmative steps to ensure that this phone number was controlled by the End-User at the time the verification was performed. The means by which a phone number is verified is context-specific, and dependent upon the trust framework or contractual agreements within which the parties are operating. When true, the phone_number Claim MUST be in E.164 format and any extensions MUST be represented in RFC 3966 format.
     *
     * @return phoneNumberVerified
     **/
    @JsonProperty("phone_number_verified")
    @ApiModelProperty(value = "True if the End-User's phone number has been verified; otherwise false. When this Claim Value is true, this means that the OP took affirmative steps to ensure that this phone number was controlled by the End-User at the time the verification was performed. The means by which a phone number is verified is context-specific, and dependent upon the trust framework or contractual agreements within which the parties are operating. When true, the phone_number Claim MUST be in E.164 format and any extensions MUST be represented in RFC 3966 format. ")
    @JsonView(Views.Standard.class)
    Boolean getPhoneNumberVerified();

    /**
     * Time the End-User&#39;s information was last updated. Its value is a JSON number representing the number of seconds from 1970-01-01T0:0:0Z as measured in UTC until the date/time.
     *
     * @return updatedAt
     **/
    @JsonProperty("updated_at")
    @ApiModelProperty(value = "Time the End-User's information was last updated. Its value is a JSON number representing the number of seconds from 1970-01-01T0:0:0Z as measured in UTC until the date/time. ")
    @JsonView(Views.Standard.class)
    Long getUpdatedAt();

    /**
     * End-User&#39;s preferred postal address.
     *
     * @return address
     **/
    @JsonProperty("address")
    @ApiModelProperty(value = "End-User's preferred postal address. ")
    @JsonView(Views.Standard.class)
    Address getAddress();

    /**
     * The user's (IndieAuth) profile URL.
     *
     * @return me
     **/
    @JsonProperty("me")
    @ApiModelProperty(value = "The user's profile URL.")
    @JsonView(Views.Indie.class)
    String getMe();

    /**
     * User&#39;s generated password
     *
     * @return password
     **/
    @JsonProperty("password")
    @ApiModelProperty(value = "User's generated password")
    @JsonView(Views.Phantom.class)
    String getPassword();

    @JsonProperty("@id")
    @JsonView(Views.Meta.class)
    String getId();

    @JsonProperty("webmail")
    @JsonView(Views.Phantom.class)
    String getWebmail();

    @JsonProperty("uid")
    @JsonView(Views.Phantom.class)
    @Value.Derived
    default String getUid() {
        final String value = getSub();
        return value == null ? null : BaseEncoding.base64Url().omitPadding().encode(Hashing.farmHashFingerprint64().hashString(value, Charsets.UTF_8).asBytes());
    }

    class Builder extends UserValue.Builder {
    }
}
