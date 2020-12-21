/*
 * Copyright (c) 2020 Gobierno de España
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.common.handler;

import com.auth0.jwt.exceptions.JWTVerificationException;
import es.gob.radarcovid.common.exception.RadarCovidServerException;
import es.gob.radarcovid.common.security.KeyVault;
import es.gob.radarcovid.verification.api.MessageResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import javax.naming.AuthenticationException;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.security.spec.InvalidKeySpecException;

@RestControllerAdvice
@Slf4j
public class RadarCovidHandler {

    /**
     * This method handles Bad Requests.
     *
     * @param ex the thrown exception
     * @param wr the WebRequest
     */
    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            ServletRequestBindingException.class,
            HttpMediaTypeNotSupportedException.class
    })
    public ResponseEntity<MessageResponseDto> bindingExceptions(Exception ex, WebRequest wr) {
        log.error("Binding failed {}", wr.getDescription(false), ex);
        return buildResponseMessage(HttpStatus.BAD_REQUEST, "Binding failed");
    }

    /**
     * This method handles Validation Exceptions.
     *
     * @return ResponseEntity<?> returns Bad Request
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<MessageResponseDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest wr) {
        StringBuilder errors = new StringBuilder();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            errors.append(error.getDefaultMessage());
        });
        log.error("Invalid request: {}", errors);
        return buildResponseMessage(HttpStatus.BAD_REQUEST, "Validation failed");
    }

    @ExceptionHandler({
            ConstraintViolationException.class
    })
    public ResponseEntity<MessageResponseDto> handleConstraintViolationException(ConstraintViolationException ex, WebRequest wr) {
        log.error("Validation exceptions: {}", ex.getConstraintViolations());
        return buildResponseMessage(HttpStatus.BAD_REQUEST, "Validation failed");
    }

    @ExceptionHandler({
            ValidationException.class
    })
    public ResponseEntity<MessageResponseDto> handleValidationExceptions(ValidationException ex, WebRequest wr) {
        log.error("Validation exceptions: {}", ex.getMessage());
        return buildResponseMessage(HttpStatus.BAD_REQUEST, "Validation failed");
    }

    /**
     * This method handles Validation Exceptions.
     *
     * @param exception the thrown exception
     * @return ResponseEntity<?> returns a HTTP Status
     */
    @ExceptionHandler(RadarCovidServerException.class)
    public ResponseEntity<MessageResponseDto> handleVerificationServerExceptions(RadarCovidServerException exception) {
        log.warn("The verification server response preventation due to: {}", exception.getMessage());
        return buildResponseMessage(exception.getHttpStatus(), "Validation failed");
    }

    @ExceptionHandler({
            JWTVerificationException.class,
            InvalidKeySpecException.class,
            KeyVault.PublicKeyNoSuitableEncodingFoundException.class
    })
    public ResponseEntity<MessageResponseDto> handleInvalidKeys(Exception ex, WebRequest wr) {
        log.error("Invalid key: {}", ex.getMessage());
        return buildResponseMessage(HttpStatus.FORBIDDEN, "Invalid key");
    }

    @ExceptionHandler({
            AccessDeniedException.class,
            AuthenticationException.class,
            InsufficientAuthenticationException.class
    })
    public ResponseEntity<MessageResponseDto> handleAccessDeniedException(AccessDeniedException ex, WebRequest wr) {
        log.error("Access denied: {}", wr.getDescription(false));
        return buildResponseMessage(HttpStatus.FORBIDDEN, "Access denied to " + wr.getContextPath());
    }

    /**
     * This method handles unknown Exceptions and Server Errors.
     *
     * @param ex the thrown exception
     * @param wr the WebRequest
     */
    @ExceptionHandler({Exception.class})
    public ResponseEntity<MessageResponseDto> handleUnknownException(Exception ex, WebRequest wr) {
        log.error("Unable to handle {}", wr.getDescription(false), ex);
        return buildResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
    }

    public static ResponseEntity<MessageResponseDto> buildResponseMessage(HttpStatus httpStatus, Exception ex) {
        return buildResponseMessage(httpStatus, ex.getMessage());
    }

    public static ResponseEntity<MessageResponseDto> buildResponseMessage(HttpStatus httpStatus, String message) {
        MessageResponseDto messageResponse = MessageResponseDto.builder().code(httpStatus.value()).message(message).build();
        return ResponseEntity.status(httpStatus).body(messageResponse);
    }

}
