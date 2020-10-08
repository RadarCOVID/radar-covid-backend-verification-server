/*
 * Copyright (c) 2020 Gobierno de España
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.common.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

/**
 * Aspecto encargado de realizar el log de rendimiento de los Controllers.
 */
@Configuration
@ConditionalOnProperty(name = "application.log.enabled", havingValue = "true", matchIfMissing = true)
@Slf4j(topic = "es.gob.radarcovid.common.annotation.Loggable")
public class ControllerLoggerAspectConfiguration {

    public static final String RESPONSE_NAME_AT_ATTRIBUTES = ServletRequestAttributes.class
            .getName() + ".ATTRIBUTE_NAME";

    @Aspect
    @Component
    public class ControllerLoggerAspect {
        @Before("execution(@es.gob.radarcovid.common.annotation.Loggable * *..controller..*(..))")
        public void logBefore(JoinPoint joinPoint) {

            if (log.isDebugEnabled()) {
                log.debug("************************* INIT CONTROLLER *********************************");
                log.debug("Controller : Entering in Method : {}", joinPoint.getSignature().getDeclaringTypeName());
                log.debug("Controller : Method :             {}", joinPoint.getSignature().getName());
                log.debug("Controller : Arguments :          {}", Arrays.toString(joinPoint.getArgs()));
                log.debug("Controller : Target class :       {}", joinPoint.getTarget().getClass().getName());

                HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                        .currentRequestAttributes()).getRequest();
                if (null != request) {
                    log.debug("Controller : Start Header Section of request ");
                    log.debug("Controller : Method Type : {}", request.getMethod());
                    Enumeration<String> headerNames = request.getHeaderNames();
                    while (headerNames.hasMoreElements()) {
                        String headerName = headerNames.nextElement();
                        String headerValue = request.getHeader(headerName);
                        if (!headerName.startsWith("x-forwarded")) {
                        	log.debug("Controller : [Header Name]:{}|[Header Value]:{}", headerName, headerValue);
                        }
                    }
                    log.debug("Controller : Request Path info : {}", request.getServletPath());
                    log.debug("Controller : End Header Section of request ");
                }
            }
        }

        @AfterReturning(pointcut = "execution(@es.gob.radarcovid.common.annotation.Loggable * *..controller..*(..))", returning = "result")
        public void logAfter(JoinPoint joinPoint, Object result) {
            log.debug("************************* END CONTROLLER **********************************");
        }

        @AfterThrowing(pointcut = "execution(@es.gob.radarcovid.common.annotation.Loggable * *..controller..*(..))", throwing = "exception")
        public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
            log.error("Controller : An exception has been thrown in {} ()", joinPoint.getSignature().getName());
            log.error("Controller : Cause : {}", exception.getCause());
            log.debug("************************* END CONTROLLER **********************************");
        }

        @Around("execution(@es.gob.radarcovid.common.annotation.Loggable * *..controller..*(..))")
        public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {

            long start = System.currentTimeMillis();
            try {
                String className = joinPoint.getSignature().getDeclaringTypeName();
                String methodName = joinPoint.getSignature().getName();
                Object result = joinPoint.proceed();
                long elapsedTime = System.currentTimeMillis() - start;
                log.debug("Controller : Controller {}.{} () execution time: {} ms", className, methodName, elapsedTime);

                if ((null != result) && (result instanceof ResponseEntity) && log.isDebugEnabled()) {

                    Object dataReturned = ((ResponseEntity<?>) result).getBody();
                    HttpStatus returnedStatus = ((ResponseEntity<?>) result).getStatusCode();
                    HttpHeaders headers = ((ResponseEntity<?>) result).getHeaders();
                    log.debug("Controller : Controller Return value :  <{}, {}>", returnedStatus, dataReturned);
                    log.debug("Controller : Start Header Section of response ");
                    if ((headers != null) && (!headers.isEmpty())) {
                        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
                            log.debug("Controller : [Header Name]:{}|[Header Value]:{}", entry.getKey(),
                                      entry.getValue());
                        }
                    }
                    log.debug("Controller : End Header Section of response ");
                }

                return result;

            } catch (IllegalArgumentException e) {
                log.error("Controller : Illegal argument {} in {}()", Arrays.toString(joinPoint.getArgs()),
                          joinPoint.getSignature().getName(), e);
                throw e;
            }
        }

    }

}
