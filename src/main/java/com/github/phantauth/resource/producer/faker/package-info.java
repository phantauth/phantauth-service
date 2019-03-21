@PhantAuthFakerImmutablesStyle
package com.github.phantauth.resource.producer.faker;

import org.immutables.value.Value;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@SuppressWarnings("WeakerAccess")
@Target({ElementType.PACKAGE, ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
@Value.Style(
        get = {"is*", "get*"},
        init = "set*",
        builder = "new",
        create = "new",
        validationMethod = Value.Style.ValidationMethod.NONE,
        visibility = Value.Style.ImplementationVisibility.PUBLIC, builderVisibility = Value.Style.BuilderVisibility.PACKAGE,
        typeAbstract = {"Abstract*", "Basic*"}, typeImmutable = "*Value", typeModifiable = "*Bean")
@interface PhantAuthFakerImmutablesStyle {
}
