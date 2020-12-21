/*
 * Copyright (c) 2020 Gobierno de España
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.verification.config;

import es.gob.radarcovid.verification.etc.ApplicationOpenApiProperties;
import es.gob.radarcovid.verification.etc.Constants;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

    @Bean
    @ConditionalOnProperty(name = "application.openapi.security.enabled", havingValue = "true", matchIfMissing = true)
    public OpenAPI customOpenAPISecured(ApplicationOpenApiProperties properties) {
        return new OpenAPI()
                .info(new Info()
                              .title(properties.getTitle())
                              .version(properties.getVersion())
                              .description(properties.getDescription())
                              .termsOfService(properties.getTermsOfService()))
                .components(new Components()
                                    .addSecuritySchemes(Constants.API_KEY_AUTH,
                                                        new SecurityScheme()
                                                                .type(SecurityScheme.Type.APIKEY)
                                                                .scheme("bearer")
                                                                .bearerFormat("JWT")
                                                                .in(SecurityScheme.In.HEADER)
                                                                .name(Constants.AUTHORIZATION_HEADER)))
                .addServersItem(new Server().url(properties.getServer()));
    }

    @Bean
    @ConditionalOnProperty(name = "application.openapi.security.enabled", havingValue = "false")
    public OpenAPI customOpenAPINotSecured(ApplicationOpenApiProperties properties) {
        return new OpenAPI()
                .info(new Info()
                              .title(properties.getTitle())
                              .version(properties.getVersion())
                              .description(properties.getDescription())
                              .termsOfService(properties.getTermsOfService()))
                .addServersItem(new Server().url(properties.getServer()));
    }

}
