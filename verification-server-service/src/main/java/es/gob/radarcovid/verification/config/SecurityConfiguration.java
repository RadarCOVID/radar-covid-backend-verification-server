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

import es.gob.radarcovid.common.exception.VerificationServerException;
import es.gob.radarcovid.common.security.KeyVault;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

@Configuration
@Slf4j
public class SecurityConfiguration {

    public static final String PAIR_KEY_RADAR = "radar";
    public static final String PAIR_KEY_DUMMY = "dummy";
    public static final String PAIR_KEY_ALGORITHM = "EC";

    @Value("${application.credentials.privateKey:}")
    private String credentialsPrivateKey;

    @Value("${application.credentials.publicKey:}")
    private String credentialsPublicKey;

    @Value("${application.dummy.enabled:false}")
    private boolean dummyEnabled;

    @Value("${application.dummy.privateKey:}")
    private String dummyCredentialsPrivateKey;

    @Value("${application.dummy.publicKey:}")
    private String dummyCredentialsPublicKey;

    @Bean
    KeyVault keyVault() {
        var privateKey = KeyVault.getBase64Key(credentialsPrivateKey);
        var publicKey = KeyVault.getBase64Key(credentialsPublicKey);

        var radar = new KeyVault.KeyVaultEntry(PAIR_KEY_RADAR, privateKey, publicKey, PAIR_KEY_ALGORITHM);

        try {
            if (dummyEnabled && !StringUtils.isEmpty(dummyCredentialsPrivateKey) && !StringUtils.isEmpty(dummyCredentialsPublicKey)) {
                var dummyPrivateKey = KeyVault.getBase64Key(dummyCredentialsPrivateKey);
                var dummyPublicKey = KeyVault.getBase64Key(dummyCredentialsPublicKey);

                var dummy = new KeyVault.KeyVaultEntry(PAIR_KEY_DUMMY, dummyPrivateKey, dummyPublicKey, PAIR_KEY_ALGORITHM);
                log.debug("Loaded radar & dummy keys");
                return new KeyVault(radar, dummy);
            }

            log.debug("Loaded radar keys");
            return new KeyVault(radar);
        } catch (KeyVault.PrivateKeyNoSuitableEncodingFoundException | KeyVault.PublicKeyNoSuitableEncodingFoundException e) {
            throw new VerificationServerException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

}
