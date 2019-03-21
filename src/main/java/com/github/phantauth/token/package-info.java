/*
 * Apache License, Version 2.0
 *
 * Copyright 2018 Ivan SZKIBA https://www.linkedin.com/in/szkiba
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

@PhantAuthImmutablesStyle
package com.github.phantauth.token;


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
        typeAbstract = {"Abstract*", "Basic*"}, typeImmutable = "*Value", typeModifiable = "*Bean", typeImmutableEnclosing = "*")
@interface PhantAuthImmutablesStyle {
}
