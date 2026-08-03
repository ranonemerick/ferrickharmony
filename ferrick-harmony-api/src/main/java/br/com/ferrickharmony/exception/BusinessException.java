package br.com.ferrickharmony.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final Object[] args;

    public BusinessException(String messageKey) {
        super(messageKey);
        this.args = null;
    }

    public BusinessException(String messageKey, Object... args) {
        super(messageKey);
        this.args = args;
    }
}