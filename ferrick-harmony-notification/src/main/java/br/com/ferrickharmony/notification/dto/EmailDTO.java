package br.com.ferrickharmony.notification.dto;

public record EmailDTO(String to,
                       String subject,
                       String body) {
}
