/*
 * Copyright (c) 2020 Gobierno de España
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.verification.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import es.gob.radarcovid.verification.validation.CodeDtoConstraint;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@CodeDtoConstraint(message = "Invalid code for codeDto ${validatedValue}")
public class CodeDto implements Serializable {

    @Schema(description = "Date the patient indicates that he/she is infected", pattern = "dd/MM/yyyy")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy", timezone = "UTC", locale = "es-ES")
    private Date date;

    @Schema(description = "12 digits validation code", required = true)
    //@NotNull
    //@Pattern(regexp = "^\\d{12}$")
    //@CodeConstraint(message = "Invalid checksum for code ${validatedValue}")
    private String code;

}
