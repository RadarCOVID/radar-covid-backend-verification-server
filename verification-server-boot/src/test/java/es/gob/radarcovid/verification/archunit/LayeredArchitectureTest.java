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

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

@AnalyzeClasses(packages = "es.gob.radarcovid")
public class LayeredArchitectureTest {

    @ArchTest
    static final ArchRule layer_dependencies_are_respected = layeredArchitecture()

        .layer("Controllers").definedBy("es.gob.radarcovid..controller..")
        .layer("Services").definedBy("es.gob.radarcovid..business..")
        .layer("Persistence").definedBy("es.gob.radarcovid..persistence..")
        .layer("Security").definedBy("es.gob.radarcovid..config..",
                                            "es.gob.radarcovid..security..")
        .layer("Mappers").definedBy("es.gob.radarcovid..persistence.mapper..")

        .whereLayer("Controllers").mayNotBeAccessedByAnyLayer()
        .whereLayer("Services").mayOnlyBeAccessedByLayers("Controllers")
        .whereLayer("Persistence").mayOnlyBeAccessedByLayers("Services", "Security")
        .whereLayer("Mappers").mayOnlyBeAccessedByLayers("Persistence");

}
