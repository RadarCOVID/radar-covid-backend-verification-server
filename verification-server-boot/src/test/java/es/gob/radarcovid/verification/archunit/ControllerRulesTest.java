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

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

@AnalyzeClasses(packages = "es.gob.covid19.radarcovid")
public class ControllerRulesTest {

    @ArchTest
    static final ArchRule controllers_must_reside_in_controller_package =
            classes()
                    .that().haveNameMatching(".*Controller")
                    .should().resideInAnyPackage("..controller")
                    .as("Controllers should reside in a package '..controller'");

    @ArchTest
    static final ArchRule only_controllers_may_use_business_services =
            classes()
                    .that().haveNameMatching(".*Controller")
                    .should().accessClassesThat().resideInAnyPackage("..business")
                    .as("Only Controllers may use business services");

    @ArchTest
    static final ArchRule only_controllers_have_RestController_annotation =
            noClasses()
                .that().areAnnotatedWith(RestController.class)
                .should().resideOutsideOfPackage("..controller")
                .as("Only Controllers in '..controller' package should have RestController annotation");
}
