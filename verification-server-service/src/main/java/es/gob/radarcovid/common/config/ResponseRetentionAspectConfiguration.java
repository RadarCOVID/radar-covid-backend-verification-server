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

import java.util.Arrays;

import org.apache.commons.lang3.math.NumberUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import es.gob.radarcovid.common.annotation.ResponseRetention;
import es.gob.radarcovid.common.exception.RadarCovidServerException;
import lombok.extern.slf4j.Slf4j;

/**
 * Aspect in charge of controlling the minimum response time of a service.
 */
@Configuration
@ConditionalOnProperty(name = "application.response.retention.enabled", havingValue = "true", matchIfMissing = true)
@Slf4j(topic = "es.gob.radarcovid.common.annotation.ResponseRetention")
public class ResponseRetentionAspectConfiguration {

	@Autowired
	private Environment environment;
	
    @Aspect
    @Component
    public class ControllerTimeResponseControlAspect {

        @Pointcut("@annotation(responseRetention)")
        public void annotationPointCutDefinition(ResponseRetention responseRetention){
        }
        
		@Around("execution(@es.gob.radarcovid.common.annotation.ResponseRetention * *..controller..*(..)) && annotationPointCutDefinition(responseRetention)")
        public Object logAround(ProceedingJoinPoint joinPoint, ResponseRetention responseRetention) throws Throwable {

        	log.debug("************************* INIT TIME RESPONSE CONTROL *********************************");
            long start = System.currentTimeMillis();
            try {
                String className = joinPoint.getSignature().getDeclaringTypeName();
                String methodName = joinPoint.getSignature().getName();
                Object result = joinPoint.proceed();
                long elapsedTime = System.currentTimeMillis() - start;
                long responseRetentionTimeMillis = getTimeMillis(responseRetention.time());
                log.debug("Controller : Controller {}.{} () execution time : {} ms", className, methodName, elapsedTime);
                if (elapsedTime < responseRetentionTimeMillis) {
                	try {
                		Thread.sleep(responseRetentionTimeMillis - elapsedTime);
                	} catch (InterruptedException e) {
						log.warn("Controller : Controller {}.{} () Thread sleep interrupted", className, methodName);
					}
                }
                elapsedTime = System.currentTimeMillis() - start;
                log.debug("Controller : Controller {}.{} () NEW execution time : {} ms", className, methodName, elapsedTime);
                log.debug("************************* END TIME RESPONSE CONTROL **********************************");
                return result;

            } catch (IllegalArgumentException e) {
				log.error("Controller : Illegal argument {} in {} ()", Arrays.toString(joinPoint.getArgs()),
						joinPoint.getSignature().getName());
				log.debug("************************* END TIME RESPONSE CONTROL **********************************");
                throw e;
            }
        }
        
		private long getTimeMillis(String timeMillisString) {
			String stringValue = environment.getProperty(timeMillisString);
			if (NumberUtils.isCreatable(stringValue)) {
				return Long.parseLong(stringValue);
			} else {
				throw new RadarCovidServerException(HttpStatus.INTERNAL_SERVER_ERROR,
						"Invalid timeMillisString value \"" + timeMillisString + "\" - not found or cannot parse into long");
			}
		}
    }

}
