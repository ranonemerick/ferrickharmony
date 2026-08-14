package br.com.ferrickharmony.validation.cpf;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CPFValidator implements ConstraintValidator<ValidCPF, String> {
    @Override
    public void initialize(ValidCPF constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String cpf, ConstraintValidatorContext constraintValidatorContext) {
        if (cpf == null || !cpf.matches("\\d{11}") || cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        try {
            int[] digits = new int[11];
            for (int i = 0; i < 11; i++) {
                digits[i] = cpf.charAt(i) - '0';
            }

            int sum1 = 0;
            for (int i = 0; i < 9; i++) {
                sum1 += digits[i] * (10 - i);
            }
            int digit1 = 11 - (sum1 % 11);
            if (digit1 >= 10) {
                digit1 = 0;
            }

            int sum2 = 0;
            for (int i = 0; i < 9; i++) {
                sum2 += digits[i] * (11 - i);
            }
            sum2 += digit1 * 2;
            int digit2 = 11 - (sum2 % 11);
            if (digit2 >= 10) {
                digit2 = 0;
            }

            return digits[9] == digit1 && digits[10] == digit2;

        } catch (Exception e) {
            return false;
        }
    }
}