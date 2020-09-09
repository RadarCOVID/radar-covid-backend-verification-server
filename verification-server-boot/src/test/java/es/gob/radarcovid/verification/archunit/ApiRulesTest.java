/*
 * Copyright (c) 2020 Gobierno de España
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.verification.archunit;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import java.io.Serializable;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "es.gob.covid19.radarcovid")
public class ApiRulesTest {

    @ArchTest
    static final ArchRule DTOs_must_reside_in_api_or_dommain_package =
            classes()
                    .that().haveNameMatching(".*Dto")
                    .should().resideInAnyPackage("..api", "..domain")
                    .as("DTOs should reside in a package '..api' or '..domain'");

    @ArchTest
    static final ArchRule only_DTOs_should_have_JsonInclude_annotation =
            noClasses()
                .that().areAnnotatedWith(JsonInclude.class)
                .should().resideOutsideOfPackage("..api");

    @ArchTest
    static final ArchRule DTOs_must_implement_Serializable =
            classes()
                    .that().haveNameMatching(".*Dto")
                    .should().beAssignableTo(Serializable.class)
                    .as("DTOs should be Serializable");

}
