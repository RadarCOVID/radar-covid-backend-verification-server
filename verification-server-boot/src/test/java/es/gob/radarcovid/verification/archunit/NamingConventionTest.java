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
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

@AnalyzeClasses(packages = "es.gob.covid19.radarcovid")
public class NamingConventionTest {

    @ArchTest
    static ArchRule controllers_should_be_suffixed =
            classes()
                    .that().resideInAPackage("..controller")
                           .and().areAnnotatedWith(RestController.class)
                    .should().haveSimpleNameEndingWith("Controller");

    @ArchTest
    static ArchRule classes_named_controller_should_be_in_a_controller_package =
            classes()
                    .that().haveSimpleNameEndingWith("Controller")
                    .should().resideInAPackage("..controller");

    @ArchTest
    static ArchRule configuration_should_be_suffixed =
            classes()
                    .that().resideInAPackage("..config")
                    .and().areAnnotatedWith(Configuration.class)
                    .should().haveSimpleNameEndingWith("Configuration");

    @ArchTest
    static ArchRule configuration_annotated_should_be_suffixed =
            classes()
                    .that().areAnnotatedWith(Configuration.class)
                    .should().haveSimpleNameEndingWith("Configuration");

    @ArchTest
    static ArchRule classes_named_configuration_should_be_in_a_config_package =
            classes()
                    .that().haveSimpleNameEndingWith("Configuration")
                    .should().resideInAPackage("..config");

    @ArchTest
    static ArchRule repositories_should_be_suffixed =
            classes()
                    .that().resideInAPackage("..persistence.repository")
                    .and().areAnnotatedWith(Repository.class)
                    .should().haveSimpleNameEndingWith("Repository");

    @ArchTest
    static ArchRule classes_named_repository_should_be_in_a_repository_package =
            classes()
                    .that().haveSimpleNameEndingWith("Repository")
                    .should().resideInAPackage("..persistence.repository");

}
