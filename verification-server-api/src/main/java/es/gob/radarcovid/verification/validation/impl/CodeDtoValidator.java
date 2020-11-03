package es.gob.radarcovid.verification.validation.impl;

import es.gob.radarcovid.verification.api.CodeDto;
import es.gob.radarcovid.verification.util.CheckSumUtil;
import es.gob.radarcovid.verification.validation.CodeDtoConstraint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Date;

@Slf4j
public class CodeDtoValidator implements ConstraintValidator<CodeDtoConstraint, CodeDto> {

    public static final String FAKE_CODE = "112358132134";

    @Override
    public boolean isValid(CodeDto codeDto, ConstraintValidatorContext constraintValidatorContext) {

        boolean result = false;

        if (codeDto != null) {
            if (FAKE_CODE.equals(codeDto.getCode())) {
                result = true;
            } else if (!StringUtils.isEmpty(codeDto.getCode())) {
                result = CheckSumUtil.validateChecksum(codeDto.getCode());
                if (result && codeDto.getDate() != null) {
                    result = codeDto.getDate().before(new Date());
                }
            }
        }

        return result;
    }
}
