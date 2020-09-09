/*
 * Copyright (c) 2020 Gobierno de España
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.verification.signature.impl;

import es.gob.radarcovid.common.security.KeyVault;
import es.gob.radarcovid.verification.config.SecurityConfiguration;
import es.gob.radarcovid.verification.signature.CodeSignature;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Base64;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
@Slf4j
public class CodeSignatureImpl implements CodeSignature {

    public static final String ALGORITHM = "SHA512withECDSA";

    private final KeyVault keyVault;

    private Random random = new Random();

    @Override
    public String sign(boolean dummy, List<String> codes) throws SignatureException, NoSuchAlgorithmException, InvalidKeyException {
        StringBuilder stringBuilder = new StringBuilder();
        codes.stream().forEach(stringBuilder::append);

        Signature signature = Signature.getInstance(ALGORITHM);
        if (dummy && keyVault.get(SecurityConfiguration.PAIR_KEY_DUMMY) != null) {
            log.debug("Signed with dummy key");
            signature.initSign(keyVault.get(SecurityConfiguration.PAIR_KEY_DUMMY).getPrivate());
        } else {
            signature.initSign(keyVault.get(SecurityConfiguration.PAIR_KEY_RADAR).getPrivate());
        }
        signature.update(stringBuilder.toString().getBytes(StandardCharsets.UTF_8));
        byte[] signatureBytes = signature.sign();

        return Base64.getEncoder().encodeToString(signatureBytes);
    }

}
