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

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "es.gob.covid19.radarcovid")
public class InterfacesRulesTest {

    @ArchTest
    static final ArchRule interfaces_should_not_have_names_ending_with_the_word_interface =
            noClasses().that().areInterfaces().should().haveNameMatching(".*Interface");

    @ArchTest
    static final ArchRule interfaces_should_not_have_simple_class_names_containing_the_word_interface =
            noClasses().that().areInterfaces().should().haveSimpleNameContaining("Interface");

    @ArchTest
    static final ArchRule interfaces_must_not_be_placed_in_implementation_packages =
            noClasses()
                    .that().resideInAPackage("..impl")
                    .should().beInterfaces()
                    .as("Interfaces must not be placed in a package '..impl'");

    @ArchTest
    static final ArchRule implementations_must_be_placed_in_impl_packages =
            classes()
                    .that().haveNameMatching(".*Impl")
                        .and().haveNameNotMatching(".*MapperImpl")
                    .should().resideInAnyPackage("..impl")
                    .as("Implementations should reside in a package '..impl'");

}
