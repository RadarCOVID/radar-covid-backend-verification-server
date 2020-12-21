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

import es.gob.radarcovid.common.config.JwtAuthenticationEntryPoint;
import es.gob.radarcovid.common.security.KeyVault;
import es.gob.radarcovid.verification.controller.GenerationController;
import es.gob.radarcovid.verification.controller.KpiController;
import es.gob.radarcovid.verification.controller.VerificationController;
import es.gob.radarcovid.verification.etc.RadarCovidProperties;
import es.gob.radarcovid.verification.persistence.CCAAAuthorizationDao;
import es.gob.radarcovid.verification.persistence.CCAADao;
import es.gob.radarcovid.verification.security.JwtAuthorizationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final KeyVault keyVault;
    private final CCAADao ccaaDao;
    private final CCAAAuthorizationDao authorizationDao;
    private final RadarCovidProperties properties;

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver resolver;

    @Autowired
    private JwtAuthenticationEntryPoint unauthorizedHandler;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // @formatter:off
        http
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.NEVER)
                .and()
            .headers()
                .and()
            .addFilterAfter(new JwtAuthorizationFilter(keyVault, ccaaDao, authorizationDao, resolver, properties),
                                                       UsernamePasswordAuthenticationFilter.class)
            .authorizeRequests()
                .antMatchers(HttpMethod.POST, VerificationController.VERIFY_ROUTE + "**").permitAll()
                .antMatchers(HttpMethod.POST, KpiController.KPI_ROUTE + "**").authenticated()
                .antMatchers(HttpMethod.GET, GenerationController.GENERATE_ROUTE + "**").authenticated()
                .anyRequest().permitAll()
                .and()
            .exceptionHandling().authenticationEntryPoint(unauthorizedHandler)
                .and()
            .csrf().disable()
            .cors();
        // @formatter.on
    }

}
