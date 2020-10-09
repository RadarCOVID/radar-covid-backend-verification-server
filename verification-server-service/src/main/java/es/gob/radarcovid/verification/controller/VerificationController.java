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
import es.gob.radarcovid.common.annotation.ResponseRetention;
import es.gob.radarcovid.common.handler.RadarCovidExceptionHandler;
import es.gob.radarcovid.verification.api.CodeDto;
import es.gob.radarcovid.verification.api.MessageResponseDto;
import es.gob.radarcovid.verification.api.TanDto;
import es.gob.radarcovid.verification.api.TokenResponseDto;
import es.gob.radarcovid.verification.business.VerificationService;
import es.gob.radarcovid.verification.etc.Constants;
import es.gob.radarcovid.verification.etc.OpenApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Optional;
import java.util.concurrent.Callable;

@RestController
@RequestMapping(VerificationController.VERIFY_ROUTE)
@Validated
@RequiredArgsConstructor
@Slf4j
public class VerificationController {

    public static final String VERIFY_ROUTE = "/verify";
    /**
     * The route to the tan verification endpoint.
     */
    public static final String TAN_VERIFY_ROUTE = "/tan";
    /**
     * The route to the code verification endpoint.
     */
    public static final String CODE_VERIFY_ROUTE = "/code";

    private final VerificationService service;

    /**
     * This provided REST method verifies the verification code.
     *
     * @param codeDto - the code number, which needs to be verified {@link CodeDto}
     * @return HTTP 200, if the verification was successful. Otherwise HTTP 404.
     */
    @Loggable
    @ResponseRetention(time = "application.response.retention.time.verify.code")
    @Operation(
            summary = "Verify provided Code",
            description = "The provided Code is verified to be formerly issued by the Health Authority",
            hidden = OpenApiConstants.VERIFICATION_VERIFY_CODE_OPERATION_HIDDEN
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Code is valid and formerly issued by the Health Authority",
                    content = @Content(schema = @Schema(implementation = TokenResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = MessageResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Code could not be verified", content = @Content(schema = @Schema(implementation = MessageResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "Exception", content = @Content(schema = @Schema(implementation = MessageResponseDto.class)))})
    @PostMapping(value = CODE_VERIFY_ROUTE,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Callable<ResponseEntity<?>> verifyCode(
            @Parameter(description = "The Code to be verified", required = true, schema = @Schema(implementation = CodeDto.class)) @Valid @RequestBody CodeDto codeDto) {
        MDC.put(Constants.TRACKING, "VERIFY_CODE|CODE:" + codeDto.getCode());
        Optional<String> result = service.redeemCode(codeDto);
        if (result.isPresent()) {
            TokenResponseDto response = new TokenResponseDto(result.get());
            log.info("The Code {} is valid - JWT token {}", codeDto, response);
            return () -> {
                return ResponseEntity.ok(response);
            };
        } else {
            log.warn("The Code {} is invalid", codeDto);
            return () -> {
                return RadarCovidExceptionHandler.buildResponseMessage(HttpStatus.NOT_FOUND, "Invalid code " + codeDto.getCode());
            };
        }
    }

    /**
     * This provided REST method verifies the transaction number (TAN).
     *
     * @param tan - the transaction number, which needs to be verified {@link TanDto}
     * @return HTTP 200, if the verification was successful. Otherwise HTTP 404.
     */
    @Loggable
    @Operation(
            summary = "Verify provided Tan",
            description = "The provided Tan is verified to be formerly issued by the verification server",
            hidden = OpenApiConstants.VERIFICATION_VERIFY_TAN_OPERATION_HIDDEN
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tan is valid and formerly issued by the verification server"),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = MessageResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Tan could not be verified", content = @Content(schema = @Schema(implementation = MessageResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "Exception", content = @Content(schema = @Schema(implementation = MessageResponseDto.class)))})
    @PostMapping(value = TAN_VERIFY_ROUTE,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Callable<ResponseEntity<?>> verifyTan(
            @Parameter(description = "The Transaction Number (TAN) to be verified", required = true, schema = @Schema(implementation = TanDto.class)) @Valid @RequestBody TanDto tan) {
        MDC.put(Constants.TRACKING, "VERIFY_TAN|TAN:" + tan.getTan().substring(0,10) + "..." + tan.getTan().substring(245,256));
        boolean result = service.redeemTan(tan.getTan());
        if (result) {
            log.info("The TAN {} is valid", tan);
            return () -> {
                return RadarCovidExceptionHandler.buildResponseMessage(HttpStatus.OK, "TAN " + tan.getTan() + " verified");
            };
        } else {
            log.warn("The TAN {} is invalid", tan);
            return () -> {
                return RadarCovidExceptionHandler.buildResponseMessage(HttpStatus.NOT_FOUND, "Invalid TAN " + tan.getTan());
            };
        }
    }

}
