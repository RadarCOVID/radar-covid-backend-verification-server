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
import es.gob.radarcovid.verification.vo.CCAAKpiTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KpiDto {

    @NotNull
    @Past
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy", timezone = "UTC", locale = "es-ES")
    @Schema(description = "KPI date", pattern = "dd/MM/yyyy")
    private Date date;

    @NotNull
    @Schema(description = "KPI type")
    private CCAAKpiTypeEnum type;

    @NotNull
    @Schema(description = "KPI value")
    private Integer value;

}
