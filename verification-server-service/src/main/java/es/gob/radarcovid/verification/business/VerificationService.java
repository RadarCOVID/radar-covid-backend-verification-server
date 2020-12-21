/*
 * Copyright (c) 2020 Gobierno de España
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.verification.business;

import es.gob.radarcovid.verification.api.CodeDto;

import java.util.Optional;

public interface VerificationService {

    Optional<String> redeemCode(CodeDto codeDto, boolean efgsSharing);

    boolean redeemTan(String tan);

}
