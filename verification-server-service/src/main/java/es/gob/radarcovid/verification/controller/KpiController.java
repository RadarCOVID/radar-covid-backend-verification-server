/*
 * Copyright (c) 2020 Gobierno de España
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.verification.controller;

import es.gob.radarcovid.common.annotation.Loggable;
import es.gob.radarcovid.common.handler.RadarCovidHandler;
import es.gob.radarcovid.verification.api.KpiDto;
import es.gob.radarcovid.verification.api.MessageResponseDto;
import es.gob.radarcovid.verification.business.KpiService;
import es.gob.radarcovid.verification.etc.Constants;
import es.gob.radarcovid.verification.security.CCAAAuthorizationUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.concurrent.Callable;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
public class KpiController {

    public static final String KPI_ROUTE = "/kpi";

    private final KpiService service;

    @Loggable
    @Secured({Constants.AUTH_RADARCOVID, Constants.AUTH_KPI})
    @Operation(
            summary = "Saves KPI information provided by Autonomous Communities",
            description = "Saves the information about KPIs per Autonomous Communities",
            security = @SecurityRequirement(name = Constants.API_KEY_AUTH)
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "KPI information saved", content = @Content(schema = @Schema(implementation = MessageResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = MessageResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Authentication error", content = @Content(schema = @Schema(implementation = MessageResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "Exception", content = @Content(schema = @Schema(implementation = MessageResponseDto.class)))
    })
    @PostMapping(
            path = KPI_ROUTE,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Callable<ResponseEntity<?>> saveKpi(Authentication authentication,
                                               @Parameter(description = "KPI information", required = true, schema = @Schema(implementation = KpiDto.class))
                                               @Valid @RequestBody List<KpiDto> kpiDtoList) {
        String ccaa = CCAAAuthorizationUtil.getCCAAFromAuthentication(authentication);
        MDC.put(Constants.TRACKING, "SAVE_KPI|CCAA:" + ccaa + ",# ELEMENTS:" + kpiDtoList.size());
        service.saveKpi(CCAAAuthorizationUtil.isRadarCovidAuthentication(authentication), ccaa, kpiDtoList);
        return () -> RadarCovidHandler.buildResponseMessage(HttpStatus.CREATED, "OK");
    }

}
