package br.com.ferrickharmony.enums;

public enum ErrorKey {

    /* Email */
    EMAIL_ALREADY_EXISTS("error.email.already.exists"),

    /* User */
    USER_NOT_FOUND("error.user.not.found"),
    USER_ALREADY_INACTIVE("error.user.already.inactive"),

    /* Patient */
    PATIENT_NOT_FOUND("error.patient.not.found"),
    PATIENT_ALREADY_INACTIVE("error.patient.already.inactive"),
    PATIENT_CPF_EXISTS("error.patient.cpf.exists");

    private final String key;

    ErrorKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
