package br.com.ferrickharmony.utils;

public class EmailUtils {

    private EmailUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

}
