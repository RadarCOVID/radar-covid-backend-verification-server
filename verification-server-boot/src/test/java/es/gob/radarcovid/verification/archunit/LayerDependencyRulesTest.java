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
import es.gob.radarcovid.common.annotation.Loggable;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

@AnalyzeClasses(packages = "es.gob.covid19.radarcovid")
public class LayerDependencyRulesTest {

    @ArchTest
    static final ArchRule services_should_not_access_controllers =
            noClasses()
                    .that().resideInAPackage("..business..")
                    .should().accessClassesThat().resideInAPackage("..controller..")
                    .as("Services should not access controllers");

    @ArchTest
    static final ArchRule only_controllers_may_use_business_services =
            noClasses()
                    .that().resideOutsideOfPackage("..controller")
                    .should().accessClassesThat().resideInAnyPackage("..business")
                    .as("Only controllers may use business services");

    @ArchTest
    static final ArchRule persistence_should_not_access_services =
            noClasses()
                    .that().resideInAPackage("..persistence..")
                    .should().accessClassesThat().resideInAPackage("..business..")
                    .as("Persistence classes should not access business services");

    @ArchTest
    static final ArchRule only_mappers_should_only_be_accessed_by_daos =
            classes()
                    .that().resideInAPackage("..persistence.mapper")
                    .should().onlyBeAccessed().byAnyPackage("..persistence.impl")
                    .as("Mappers should only be accessed by DAO implementations");

    @ArchTest
    static final ArchRule services_should_only_be_accessed_by_controllers_or_other_services =
            classes()
                    .that().resideInAPackage("..business..")
                    .should().onlyBeAccessed().byAnyPackage("..controller..", "..business..")
                    .as("Services should only be accessed by controllers or other services");

    @ArchTest
    static final ArchRule services_should_only_access_persistence_or_other_services =
            classes()
                    .that().resideInAPackage("..business..")
                    .should().onlyAccessClassesThat()
                        .resideInAnyPackage("..business..",
                                            "..persistence..",
                                            "..exception",
                                            "..util..",
                                            "..security..",
                                            "..api",
                                            "..signature..",
                                            "java..",
                                            "org.springframework..",
                                            "org.slf4j..");

    @ArchTest
    static final ArchRule services_should_not_depend_on_controllers =
            noClasses()
                    .that().resideInAPackage("..business..")
                    .should().dependOnClassesThat().resideInAPackage("..controller..");

    @ArchTest
    static final ArchRule persistence_should_not_depend_on_services =
            noClasses()
                    .that().resideInAPackage("..persistence..")
                    .should().dependOnClassesThat().resideInAPackage("..business..");

    @ArchTest
    static final ArchRule services_should_only_be_depended_on_by_controllers_or_other_services =
            classes()
                    .that().resideInAPackage("..business..")
                    .should().onlyHaveDependentClassesThat().resideInAnyPackage("..controller..", "..business..");

    @ArchTest
    static final ArchRule services_should_only_depend_on_persistence_or_other_services =
            classes().that().resideInAPackage("..business..")
                    .should().onlyDependOnClassesThat().resideInAnyPackage("..business..",
                                                                           "..persistence..",
                                                                           "..util..",
                                                                           "..security..",
                                                                           "..exception",
                                                                           "..api",
                                                                           "..signature..",
                                                                           "..common.annotation..",
                                                                           "java..",
                                                                           "org.springframework..",
                                                                           "org.slf4j..");

    @ArchTest
    static final ArchRule only_controllers_or_services_should_use_Loggable_annotation =
            noMethods()
                .that().areAnnotatedWith(Loggable.class)
                .should().beDeclaredInClassesThat().resideOutsideOfPackages("..controller", "..business.impl")
                .as("Only controllers and service can have methods annotated with Loggable");

}
