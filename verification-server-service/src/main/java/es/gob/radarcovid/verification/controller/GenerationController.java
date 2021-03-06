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

import com.sun.istack.NotNull;
import es.gob.radarcovid.common.annotation.Loggable;
import es.gob.radarcovid.common.handler.RadarCovidHandler;
import es.gob.radarcovid.verification.api.CodesResultDto;
import es.gob.radarcovid.verification.api.MessageResponseDto;
import es.gob.radarcovid.verification.business.GenerationService;
import es.gob.radarcovid.verification.etc.Constants;
import es.gob.radarcovid.verification.security.CCAAAuthorizationUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Min;
import java.util.concurrent.Callable;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
public class GenerationController {

    public static final String GENERATE_ROUTE = "/generate";

    private final GenerationService service;

    @Value("${application.entities.codes.max}")
    private int maxCodes;

    @Loggable
    @Secured({Constants.AUTH_RADARCOVID, Constants.AUTH_GENERATION})
    @Operation(
            summary = "Provides n verification codes",
            description = "Generates n verification codes to be used by Autonomous Communities so they can provide codes to affected people (n <= 1000)",
            security = @SecurityRequirement(name = Constants.API_KEY_AUTH)
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Codes generated", content = @Content(schema = @Schema(implementation = CodesResultDto.class))),
            @ApiResponse(responseCode = "403", description = "Authentication error", content = @Content(schema = @Schema(implementation = MessageResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "Exception", content = @Content(schema = @Schema(implementation = MessageResponseDto.class)))
    })
    @GetMapping(
            path = GENERATE_ROUTE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Callable<ResponseEntity<?>> getCodes(Authentication authentication, @RequestParam("n") @NotNull @Min(1) Integer number) {
        String ccaa = CCAAAuthorizationUtil.getCCAAFromAuthentication(authentication);
        MDC.put(Constants.TRACKING, "GET_CODES|CCAA:" + ccaa + ",NUMBER:" + String.valueOf(number));
        if (number != null && 0 < number && number <= maxCodes) {
            CodesResultDto result = service.getCodes(CCAAAuthorizationUtil.isRadarCovidAuthentication(authentication), ccaa, number);
            if (result != null && result.getCodes() != null && !result.getCodes().isEmpty()) {
                log.info("Generados {} códigos para {}", number, ccaa);
                return () -> ResponseEntity.ok(result);
            } else {
                return () -> RadarCovidHandler.buildResponseMessage(HttpStatus.BAD_REQUEST, "");
            }
        } else {
            String message = "Number of codes (" + number + ") must be between 1 and the maximum (" + maxCodes + ")";
            log.warn(message);
            return () -> RadarCovidHandler.buildResponseMessage(HttpStatus.BAD_REQUEST, message);
        }
    }

}
