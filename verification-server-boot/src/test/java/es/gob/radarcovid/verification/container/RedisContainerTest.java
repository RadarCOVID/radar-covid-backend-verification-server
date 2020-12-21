/*
 * Copyright (c) 2020 Gobierno de España
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.verification.container;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.junit.ClassRule;
import org.springframework.boot.test.context.TestConfiguration;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
public class RedisContainerTest {
	
	private static final DockerImageName REDIS_IMAGE_NAME = DockerImageName.parse("redis:alpine");
	private static final Integer REDIS_PORT = 6379;
	
	@ClassRule
	public static GenericContainer<?> redis =  new GenericContainer<>(REDIS_IMAGE_NAME).withExposedPorts(REDIS_PORT);
	
	@PostConstruct
	public void postConstruct() {
		redis.start();
		System.setProperty("spring.redis.host", redis.getContainerIpAddress());
		System.setProperty("spring.redis.port", redis.getFirstMappedPort() + "");
	}

	@PreDestroy
	public void preDestroy() {
		redis.stop();
	}

}
