package es.gob.radarcovid.verification.validation;

import es.gob.radarcovid.verification.validation.impl.CodeDtoValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CodeDtoValidator.class)
public @interface CodeDtoConstraint {

    String message() default "Invalid codeDto";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
