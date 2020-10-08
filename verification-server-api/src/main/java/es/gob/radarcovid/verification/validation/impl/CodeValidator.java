/*
 * Copyright (c) 2020 Gobierno de España
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.verification.validation.impl;

import es.gob.radarcovid.verification.util.CheckSumUtil;
import es.gob.radarcovid.verification.validation.CodeConstraint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Slf4j
public class CodeValidator implements ConstraintValidator<CodeConstraint, String> {

    public static final String FAKE_CODE = "112358132134";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return !StringUtils.isEmpty(value) && (FAKE_CODE.equals(value) || CheckSumUtil.validateChecksum(value));
    }
}
