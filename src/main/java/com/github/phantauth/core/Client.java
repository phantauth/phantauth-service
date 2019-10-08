package com.github.phantauth.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.immutables.value.Value;

import javax.annotation.Nullable;
import java.util.List;

/**
 * OAuth2 client metadata. Registered clients have a set of metadata values associated with their client identifier at an authorization service, such as the list of valid redirection URIs or a display name.
 */
@ApiModel(description = "OAuth2 client metadata. Registered clients have a set of metadata values associated with their client identifier at an authorization service, such as the list of valid redirection URIs or a display name. ")
@Value.Immutable
@Value.Modifiable
@JsonSerialize(as = Client.class)
@JsonDeserialize(builder = Client.Builder.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public interface Client {

    /**
     * OAuth 2.0 client identifier string.  It SHOULD NOT be currently valid for any other registered client, though an authorization service MAY issue the same client identifier to multiple instances of a registered client at its discretion.
     *
     * @return clientId
     **/
    @JsonProperty(Property.CLIENT_ID)
    @ApiModelProperty(value = "OAuth 2.0 client identifier string.  It SHOULD NOT be currently valid for any other registered client, though an authorization service MAY issue the same client identifier to multiple instances of a registered client at its discretion. ")
    @JsonView(Views.Standard.class)
    String getClientId();

    /**
     * OAuth 2.0 client secret string.  If issued, this MUST be unique for each \&quot;client_id\&quot; and SHOULD be unique for multiple instances of a client using the same \&quot;client_id\&quot;.  This value is used by confidential clients to authenticate to the token endpoint, as described in OAuth 2.0 [RFC6749], Section 2.3.1.
     *
     * @return clientSecret
     **/
    @JsonProperty(Property.CLIENT_SECRET)
    @ApiModelProperty(value = "OAuth 2.0 client secret string.  If issued, this MUST be unique for each \"client_id\" and SHOULD be unique for multiple instances of a client using the same \"client_id\".  This value is used by confidential clients to authenticate to the token endpoint, as described in OAuth 2.0 [RFC6749], Section 2.3.1. ")
    @JsonView(Views.Standard.class)
    String getClientSecret();

    /**
     * Array of redirection URI strings for use in redirect-based flows such as the authorization code and implicit flows.  As required by Section 2 of OAuth 2.0 [RFC6749], clients using flows with redirection MUST register their redirection URI values. Authorization servers that support dynamic registration for redirect-based flows MUST implement support for this metadata value.
     *
     * @return redirectUris
     **/
    @JsonProperty(Property.REDIRECT_URIS)
    @ApiModelProperty(value = "Array of redirection URI strings for use in redirect-based flows such as the authorization code and implicit flows.  As required by Section 2 of OAuth 2.0 [RFC6749], clients using flows with redirection MUST register their redirection URI values. Authorization servers that support dynamic registration for redirect-based flows MUST implement support for this metadata value. ")
    @JsonView(Views.Standard.class)
    List<String> getRedirectUris();

    /**
     * String indicator of the requested authentication method for the token endpoint.  Values defined by this specification are:  *  \&quot;none\&quot;: The client is a public client as defined in OAuth 2.0,    Section 2.1, and does not have a client secret.  *  \&quot;client_secret_post\&quot;: The client uses the HTTP POST parameters    as defined in OAuth 2.0, Section 2.3.1.  *  \&quot;client_secret_basic\&quot;: The client uses HTTP Basic as defined in    OAuth 2.0, Section 2.3.1.  Additional values can be defined via the IANA \&quot;OAuth Token Endpoint Authentication Methods\&quot; registry established in Section 4.2.  Absolute URIs can also be used as values for this parameter without being registered.  If unspecified or omitted, the default is \&quot;client_secret_basic\&quot;, denoting the HTTP Basic authentication scheme as specified in Section 2.3.1 of OAuth 2.0.
     *
     * @return tokenEndpointAuthMethod
     **/
    @JsonProperty(Property.TOKEN_ENDPOINT_AUTH_METHOD)
    @ApiModelProperty(value = "String indicator of the requested authentication method for the token endpoint.  Values defined by this specification are:  *  \"none\": The client is a public client as defined in OAuth 2.0,    Section 2.1, and does not have a client secret.  *  \"client_secret_post\": The client uses the HTTP POST parameters    as defined in OAuth 2.0, Section 2.3.1.  *  \"client_secret_basic\": The client uses HTTP Basic as defined in    OAuth 2.0, Section 2.3.1.  Additional values can be defined via the IANA \"OAuth Token Endpoint Authentication Methods\" registry established in Section 4.2.  Absolute URIs can also be used as values for this parameter without being registered.  If unspecified or omitted, the default is \"client_secret_basic\", denoting the HTTP Basic authentication scheme as specified in Section 2.3.1 of OAuth 2.0. ")
    @JsonView(Views.Standard.class)
    String getTokenEndpointAuthMethod();

    /**
     * Array of OAuth 2.0 grant type strings that the client can use at the token endpoint.  These grant types are defined as follows:  *  \&quot;authorization_code\&quot;: The authorization code grant type defined    in OAuth 2.0, Section 4.1.  *  \&quot;implicit\&quot;: The implicit grant type defined in OAuth 2.0,    Section 4.2.  *  \&quot;password\&quot;: The resource owner password credentials grant type    defined in OAuth 2.0, Section 4.3.  *  \&quot;client_credentials\&quot;: The client credentials grant type defined    in OAuth 2.0, Section 4.4.  *  \&quot;refresh_token\&quot;: The refresh token grant type defined in OAuth    2.0, Section 6.  *  \&quot;urn:ietf:params:oauth:grant-type:jwt-bearer\&quot;: The JWT Bearer    Token Grant Type defined in OAuth JWT Bearer Token Profiles    [RFC7523].  *  \&quot;urn:ietf:params:oauth:grant-type:saml2-bearer\&quot;: The SAML 2.0    Bearer Assertion Grant defined in OAuth SAML 2 Bearer Token    Profiles [RFC7522].  If the token endpoint is used in the grant type, the value of this parameter MUST be the same as the value of the \&quot;grant_type\&quot; parameter passed to the token endpoint defined in the grant type definition.  Authorization servers MAY allow for other values as defined in the grant type extension process described in OAuth 2.0, Section 4.5.  If omitted, the default behavior is that the client will use only the \&quot;authorization_code\&quot; Grant Type.
     *
     * @return grantTypes
     **/
    @JsonProperty(Property.GRANT_TYPES)
    @ApiModelProperty(value = "Array of OAuth 2.0 grant type strings that the client can use at the token endpoint.  These grant types are defined as follows:  *  \"authorization_code\": The authorization code grant type defined    in OAuth 2.0, Section 4.1.  *  \"implicit\": The implicit grant type defined in OAuth 2.0,    Section 4.2.  *  \"password\": The resource owner password credentials grant type    defined in OAuth 2.0, Section 4.3.  *  \"client_credentials\": The client credentials grant type defined    in OAuth 2.0, Section 4.4.  *  \"refresh_token\": The refresh token grant type defined in OAuth    2.0, Section 6.  *  \"urn:ietf:params:oauth:grant-type:jwt-bearer\": The JWT Bearer    Token Grant Type defined in OAuth JWT Bearer Token Profiles    [RFC7523].  *  \"urn:ietf:params:oauth:grant-type:saml2-bearer\": The SAML 2.0    Bearer Assertion Grant defined in OAuth SAML 2 Bearer Token    Profiles [RFC7522].  If the token endpoint is used in the grant type, the value of this parameter MUST be the same as the value of the \"grant_type\" parameter passed to the token endpoint defined in the grant type definition.  Authorization servers MAY allow for other values as defined in the grant type extension process described in OAuth 2.0, Section 4.5.  If omitted, the default behavior is that the client will use only the \"authorization_code\" Grant Type. ")
    @JsonView(Views.Standard.class)
    List<String> getGrantTypes();

    /**
     * Array of the OAuth 2.0 response type strings that the client can use at the authorization endpoint.  These response types are defined as follows:  *  \&quot;code\&quot;: The authorization code response type defined in OAuth    2.0, Section 4.1.  *  \&quot;token\&quot;: The implicit response type defined in OAuth 2.0,    Section 4.2.  If the authorization endpoint is used by the grant type, the value of this parameter MUST be the same as the value of the \&quot;response_type\&quot; parameter passed to the authorization endpoint defined in the grant type definition.  Authorization servers MAY allow for other values as defined in the grant type extension process is described in OAuth 2.0, Section 4.5.  If omitted, the default is that the client will use only the \&quot;code\&quot; response type.
     *
     * @return responseTypes
     **/
    @JsonProperty(Property.RESPONSE_TYPES)
    @ApiModelProperty(value = "Array of the OAuth 2.0 response type strings that the client can use at the authorization endpoint.  These response types are defined as follows:  *  \"code\": The authorization code response type defined in OAuth    2.0, Section 4.1.  *  \"token\": The implicit response type defined in OAuth 2.0,    Section 4.2.  If the authorization endpoint is used by the grant type, the value of this parameter MUST be the same as the value of the \"response_type\" parameter passed to the authorization endpoint defined in the grant type definition.  Authorization servers MAY allow for other values as defined in the grant type extension process is described in OAuth 2.0, Section 4.5.  If omitted, the default is that the client will use only the \"code\" response type. ")
    @JsonView(Views.Standard.class)
    List<String> getResponseTypes();

    /**
     * Human-readable string name of the client to be presented to the end-user during authorization.  If omitted, the authorization service MAY display the raw \&quot;client_id\&quot; value to the end-user instead.  It is RECOMMENDED that clients always send this field. The value of this field MAY be internationalized, as described in Section 2.2.
     *
     * @return clientName
     **/
    @JsonProperty(Property.CLIENT_NAME)
    @ApiModelProperty(value = "Human-readable string name of the client to be presented to the end-user during authorization.  If omitted, the authorization service MAY display the raw \"client_id\" value to the end-user instead.  It is RECOMMENDED that clients always send this field. The value of this field MAY be internationalized, as described in Section 2.2. ")
    @JsonView(Views.Standard.class)
    String getClientName();

    /**
     * URL string of a web page providing information about the client. If present, the service SHOULD display this URL to the end-user in a clickable fashion.  It is RECOMMENDED that clients always send this field.  The value of this field MUST point to a valid web page.  The value of this field MAY be internationalized, as described in Section 2.2.
     *
     * @return clientUri
     **/
    @JsonProperty(Property.CLIENT_URI)
    @ApiModelProperty(value = "URL string of a web page providing information about the client. If present, the service SHOULD display this URL to the end-user in a clickable fashion.  It is RECOMMENDED that clients always send this field.  The value of this field MUST point to a valid web page.  The value of this field MAY be internationalized, as described in Section 2.2. ")
    @JsonView(Views.Standard.class)
    @Nullable
    String getClientUri();

    /**
     * URL string that references a logo for the client.  If present, the service SHOULD display this image to the end-user during approval. The value of this field MUST point to a valid image file.  The value of this field MAY be internationalized, as described in Section 2.2.
     *
     * @return logoUri
     **/
    @JsonProperty(Property.LOGO_URI)
    @ApiModelProperty(value = "URL string that references a logo for the client.  If present, the service SHOULD display this image to the end-user during approval. The value of this field MUST point to a valid image file.  The value of this field MAY be internationalized, as described in Section 2.2. ")
    @JsonView(Views.Standard.class)
    @Nullable
    String getLogoUri();

    /**
     * String containing a space-separated list of scope values (as described in Section 3.3 of OAuth 2.0 [RFC6749]) that the client can use when requesting access tokens.  The semantics of values in this list are service specific.  If omitted, an authorization service MAY register a client with a default set of scopes.
     *
     * @return scope
     **/
    @JsonProperty(Property.SCOPE)
    @ApiModelProperty(value = "String containing a space-separated list of scope values (as described in Section 3.3 of OAuth 2.0 [RFC6749]) that the client can use when requesting access tokens.  The semantics of values in this list are service specific.  If omitted, an authorization service MAY register a client with a default set of scopes. ")
    @JsonView(Views.Standard.class)
    String getScope();

    /**
     * Array of strings representing ways to contact people responsible for this client, typically email addresses.  The authorization service MAY make these contact addresses available to end-users for support requests for the client.  See Section 6 for information on Privacy Considerations.
     *
     * @return contacts
     **/
    @JsonProperty(Property.CONTACTS)
    @ApiModelProperty(value = "Array of strings representing ways to contact people responsible for this client, typically email addresses.  The authorization service MAY make these contact addresses available to end-users for support requests for the client.  See Section 6 for information on Privacy Considerations. ")
    @JsonView(Views.Standard.class)
    List<String> getContacts();

    /**
     * URL string that points to a human-readable terms of service document for the client that describes a contractual relationship between the end-user and the client that the end-user accepts when authorizing the client.  The authorization service SHOULD display this URL to the end-user if it is provided.  The value of this field MUST point to a valid web page.  The value of this field MAY be internationalized, as described in Section 2.2.
     *
     * @return tosUri
     **/
    @JsonProperty(Property.TOS_URI)
    @ApiModelProperty(value = "URL string that points to a human-readable terms of service document for the client that describes a contractual relationship between the end-user and the client that the end-user accepts when authorizing the client.  The authorization service SHOULD display this URL to the end-user if it is provided.  The value of this field MUST point to a valid web page.  The value of this field MAY be internationalized, as described in Section 2.2. ")
    @JsonView(Views.Standard.class)
    @Nullable
    String getTosUri();

    /**
     * URL string that points to a human-readable privacy policy document that describes how the deployment organization collects, uses, retains, and discloses personal data.  The authorization service SHOULD display this URL to the end-user if it is provided.  The value of this field MUST point to a valid web page.  The value of this field MAY be internationalized, as described in Section 2.2.
     *
     * @return policyUri
     **/
    @JsonProperty(Property.POLICY_URI)
    @ApiModelProperty(value = "URL string that points to a human-readable privacy policy document that describes how the deployment organization collects, uses, retains, and discloses personal data.  The authorization service SHOULD display this URL to the end-user if it is provided.  The value of this field MUST point to a valid web page.  The value of this field MAY be internationalized, as described in Section 2.2. ")
    @JsonView(Views.Standard.class)
    @Nullable
    String getPolicyUri();

    /**
     * URL string referencing the client&#39;s JSON Web Key (JWK) Set [RFC7517] document, which contains the client&#39;s public keys.  The value of this field MUST point to a valid JWK Set document.  These keys can be used by higher-level protocols that use signing or encryption.  For instance, these keys might be used by some applications for validating signed requests made to the token endpoint when using JWTs for client authentication [RFC7523].  Use of this parameter is preferred over the \&quot;jwks\&quot; parameter, as it allows for easier key rotation.  The \&quot;jwks_uri\&quot; and \&quot;jwks\&quot; parameters MUST NOT both be present in the same request or response.
     *
     * @return jwksUri
     **/
    @JsonProperty(Property.JWKS_URI)
    @ApiModelProperty(value = "URL string referencing the client's JSON Web Key (JWK) Set [RFC7517] document, which contains the client's public keys.  The value of this field MUST point to a valid JWK Set document.  These keys can be used by higher-level protocols that use signing or encryption.  For instance, these keys might be used by some applications for validating signed requests made to the token endpoint when using JWTs for client authentication [RFC7523].  Use of this parameter is preferred over the \"jwks\" parameter, as it allows for easier key rotation.  The \"jwks_uri\" and \"jwks\" parameters MUST NOT both be present in the same request or response. ")
    @JsonView(Views.Standard.class)
    String getJwksUri();

    /**
     * Client&#39;s JSON Web Key Set [RFC7517] document value, which contains the client&#39;s public keys.  The value of this field MUST be a JSON object containing a valid JWK Set.  These keys can be used by higher-level protocols that use signing or encryption.  This parameter is intended to be used by clients that cannot use the \&quot;jwks_uri\&quot; parameter, such as native clients that cannot host public URLs.  The \&quot;jwks_uri\&quot; and \&quot;jwks\&quot; parameters MUST NOT both be present in the same request or response.
     *
     * @return jwks
     **/
    @JsonProperty(Property.JWKS)
    @ApiModelProperty(value = "Client's JSON Web Key Set [RFC7517] document value, which contains the client's public keys.  The value of this field MUST be a JSON object containing a valid JWK Set.  These keys can be used by higher-level protocols that use signing or encryption.  This parameter is intended to be used by clients that cannot use the \"jwks_uri\" parameter, such as native clients that cannot host public URLs.  The \"jwks_uri\" and \"jwks\" parameters MUST NOT both be present in the same request or response. ")
    @JsonView(Views.Standard.class)
    String getJwks();

    /**
     * A unique identifier string (e.g., a Universally Unique Identifier (UUID)) assigned by the client developer or software publisher used by registration endpoints to identify the client software to be dynamically registered.  Unlike \&quot;client_id\&quot;, which is issued by the authorization service and SHOULD vary between instances, the \&quot;software_id\&quot; SHOULD remain the same for all instances of the client software.  The \&quot;software_id\&quot; SHOULD remain the same across multiple updates or versions of the same piece of software.  The value of this field is not intended to be human readable and is usually opaque to the client and authorization service.
     *
     * @return softwareId
     **/
    @JsonProperty(Property.SOFTWARE_ID)
    @ApiModelProperty(value = "A unique identifier string (e.g., a Universally Unique Identifier (UUID)) assigned by the client developer or software publisher used by registration endpoints to identify the client software to be dynamically registered.  Unlike \"client_id\", which is issued by the authorization service and SHOULD vary between instances, the \"software_id\" SHOULD remain the same for all instances of the client software.  The \"software_id\" SHOULD remain the same across multiple updates or versions of the same piece of software.  The value of this field is not intended to be human readable and is usually opaque to the client and authorization service. ")
    @JsonView(Views.Standard.class)
    @Nullable
    String getSoftwareId();

    /**
     * A version identifier string for the client software identified by \&quot;software_id\&quot;.  The value of the \&quot;software_version\&quot; SHOULD change on any update to the client software identified by the same \&quot;software_id\&quot;.  The value of this field is intended to be compared using string equality matching and no other comparison semantics are defined by this specification.  The value of this field is outside the scope of this specification, but it is not intended to be human readable and is usually opaque to the client and authorization service.  The definition of what constitutes an update to client software that would trigger a change to this value is specific to the software itself and is outside the scope of this specification.
     *
     * @return softwareVersion
     **/
    @JsonProperty(Property.SOFTWARE_VERSION)
    @ApiModelProperty(value = "A version identifier string for the client software identified by \"software_id\".  The value of the \"software_version\" SHOULD change on any update to the client software identified by the same \"software_id\".  The value of this field is intended to be compared using string equality matching and no other comparison semantics are defined by this specification.  The value of this field is outside the scope of this specification, but it is not intended to be human readable and is usually opaque to the client and authorization service.  The definition of what constitutes an update to client software that would trigger a change to this value is specific to the software itself and is outside the scope of this specification. ")
    @JsonView(Views.Standard.class)
    @Nullable
    String getSoftwareVersion();

    @JsonProperty(Property.LOGO_EMAIL)
    @JsonView(Views.Phantom.class)
    @Nullable
    String getLogoEmail();


    @JsonProperty(Property.ID)
    @JsonView(Views.Meta.class)
    String getId();

    class Builder extends ClientValue.BuilderBase {
    }
}
