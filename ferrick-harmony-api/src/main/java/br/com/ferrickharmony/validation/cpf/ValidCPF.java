package br.com.ferrickharmony.validation.cpf;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CPFValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCPF {

    String message() default "{error.validation.cpf.invalid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
