package org.example.shopproject.util;

import org.springframework.stereotype.Component;

import jakarta.validation.Validator;

@Component
public class ValidationUtilImpl implements ValidationUtil{

    private final Validator validator;

    public ValidationUtilImpl(Validator validator) {
        this.validator = validator;

    }

    @Override
    public <E> boolean isValid(E e) {
        return this.validator.validate(e).isEmpty();
    }
}
