[![Uptime Robot ratio (30 days)](https://img.shields.io/uptimerobot/ratio/m782009597-d585af4bd870fb9c20ddffab.svg)](https://stats.uptimerobot.com/G99oYfRXX) [![Build Status](https://travis-ci.org/phantauth/phantauth-service.svg?branch=master)](https://travis-ci.org/phantauth/phantauth-service) [![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=phantauth_phantauth-service&metric=alert_status)](https://sonarcloud.io/dashboard?id=phantauth_phantauth-service) [![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=phantauth_phantauth-service&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=phantauth_phantauth-service) [![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=phantauth_phantauth-service&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=phantauth_phantauth-service) [![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=phantauth_phantauth-service&metric=security_rating)](https://sonarcloud.io/dashboard?id=phantauth_phantauth-service)

# PhantAuth Service

Random User Generator + OpenID Connect Provider

https://www.phantauth.net

> Like Lorem Ipsum, but for user accounts and authentication.

## TL;DR

**PhantAuth was designed to simplify testing for applications using OpenID Connect authentication by making use of random generated users.**

end point | address
--- | ---
issuer | https://phantauth.net
discovery | https://phantauth.net/.well-known/openid-configuration

credential | value
--- | ---
client_id | test.client
client_secret | UTBcWwt5

## OpenID Connect Provider

The OpenID Connect Provider of PhantAuth supports the flows listed in the OpenID Connect specifications (Hybrid, Implicit, Authorization Code), as well as the Resource Owner Password grant type, specified in the OAuth 2.0 specifications. PhantAuth as an OpenID Connect Provider can be integrated with a variety of web applications, mobil applications, and  backend applications. The integration can be either direct, as in the case of the OpenID Connect Provider, or through an authentication integration service, as in the case of Auth0 or Azure Active Directory B2C. To learn more, please go to chapter [Integration](https://doc.phantauth.net/#/integration).

Examples:

- [Direct OpenID Connect integration](https://www.phantauth.net/test/oidc)
- [Auth0 Social Connections integration](https://www.phantauth.net/test/auth0)
- [Azure Active Directory B2C integration](https://www.phantauth.net/test/azure)

## Random User Generator

The random user generator of PhantAuth can also be used separately, independent of the OpenID Connect Provider. You can generate an optional number of test users. In the knowledge of their user name, the data of the generated users can be regenerated at any time (OpenID Connect *sub* claim). The generated users have a unique, operational, disposable email address, a profile picture selected from one of the multiple pools of pictures, and the usual profile data. Custom email addresses and profile pictures may also be added. The random user generator of PhantAuth can be fully customized. Additionally, you can link an external generator to the application. For details,please go to chapter [Generator](https://doc.phantauth.net/#/generator).

Test pages:

- [Default Generator Test Page](https://phantauth.net/test/user) (embedded generator)
- [Greek Gods Generator Test Page](https://phantauth.net/_gods/test/user) (embedded generator works from Google Sheet)
- [Faker Generator Test Page](https://phantauth.net/_faker/test/user) (external generator using Javascript Faker library)
- [Chance Generator Test Page](https://phantauth.net/_chance/test/user) (external generator using Javascript Chance library)
- [Casual Generator Test Page](https://phantauth.net/_casual/test/user) (external generator using Javascript Casual library)
- [Randomuser Generator Test Page](https://phantauth.net/_randomuser/test/user) (client side generator using https://randomuser.me)
- [uinames Generator Test Page](https://phantauth.net/_uinames/test/user) (client side generator using https://uinames.com)
- [Mockaroo Generator Test Page](https://phantauth.net/_mockaroo/test/user) (client side generator using https://mockaroo.com)

Every random generated user has a profile page, which contains their profile data in a simple one-page format.

Profile examples:

- [Random Profile](https://phantauth.net/%7Ejoe.black)
- [Random Greek God Profile](https://phantauth.net/_gods/%7Ezeus)
- [Random Faker Profile](https://phantauth.net/_faker/%7Eharry.houdini)
- [Random Chance Profile](https://phantauth.net/_chance/%7Epeter.pan)
- [Random Casual Profile](https://phantauth.net/_casual/%7Ejohn.smith)

## CodeSandbox Samples

The use of the random user generator and the direct integration of  the OpenID Connect is demonstrated through a set of CodeSandbox samples. The sample applications are run directly from CodeSandbox, so the source code is easy to view, edit, and test.

Examples:

- [Random User Generator usage exampe](https://4xyj8lw394.codesandbox.io/)
- [OpenID Connect direct integration exampe](https://8z77681269.codesandbox.io/)

## Customizable Tenants

The PhantAuth is extremely versatile and customizable. You can use your own random user service, or generate users from an external .csv file or Google Sheet. You can use a set of Bootstrap themes to tailor the look and feel of the profile, morover, you can fundamentally change the same look and feel by the use of your own HTML templates. To find out more, please go to chapter [Tenant](https://doc.phantauth.net/#/tenant).

To customize the application, you need to use one or more so-called tenants. A tenant can be consiered as an independent PhantAuth service. A tenant has its own random user generator endpoints and OpenID Connect endpoints.

The tenants can be organised into so-called domains. Practically, a domain is a DNS zone, which contains the settings of the given tenant(s). The tenants as well as the domain can be configured by the use of DNS TXT records.

In addition to the default tenant, the PhantAuth Domain contains some sample tenants, which are primarily designed to demonstrate customitability, a range of hosting possibilities, and the links to external services. In most cases, using the [default tenant](https://phantauth.net) is enough.

- [PhantAuth Default](https://phantauth.net) - default tenant, based on Java Fairy library
- [Greek Gods](https://phantauth.net/_gods) - based on Google Sheet document
- [PhantAuth Faker](https://phantauth.net/_faker) - based on Javascript Faker library, hosted at https://now.sh
- [PhantAuth Chance](https://phantauth.net/_chance) - based on Javascript Chance library, hosted at https://now.sh
- [PhantAuth Casual](https://phantauth.net/_casual) - based on Javascript Casual library, hosted at https://webtask.io
- [RANDOM USER](https://phantauth.net/_randomuser) - based on https://randomuser.me service
- [uinames](https://phantauth.net/_uinames) - based on https://uinames.com service
- [Mockaroo](https://phantauth.net/_mockaroo) - based on  https://mockaroo.com service

Anyone can create the domain and the tenants. Sharing the tenants is facilitated by the [PhantAuth Shared Domain](https://shared.phantauth.net). A shared domain is connected to the [phantauth.cf](http://phantauth.cf) DNS zone, in which anyone can create tenant configuration notes by the use of the [FreeDNS](https://freedns.afraid.org/) service.

## Pricing

PhantAuth is a free open-source non-profit application. If you find this service useful and can afford, please make a small donation as a contribution to the operation costs (domain registration, service hosting, etc.)

[Donate on Ko-fi](https://ko-fi.com/Q5Q0T7C7) | [Donate on Liberapay](https://liberapay.com/szkiba/donate) | [Donate on PayPal](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=VXLCJ3EZRAE7G&source=url)
