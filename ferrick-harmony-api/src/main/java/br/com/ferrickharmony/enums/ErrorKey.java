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
    PATIENT_CPF_EXISTS("error.patient.cpf.exists"),

    /* Professional */
    PROFESSIONAL_CPF_EXISTS("error.professional.cpf.exists"),
    PROFESSIONAL_DOCUMENT_EXISTS("error.professional.document.exists"),
    PROFESSIONAL_EMAIL_EXISTS("error.professional.email.exists"),
    PROFESSIONAL_NOT_FOUND("error.professional.not.found"),
    PROFESSIONAL_ALREADY_INACTIVE("error.professional.already.inactive"),

    /* Security */
    INVALID_JWT_TOKEN("error.invalid.jwt.token");

    private final String key;

    ErrorKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
