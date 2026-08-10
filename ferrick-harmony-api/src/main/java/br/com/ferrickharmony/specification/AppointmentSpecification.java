package br.com.ferrickharmony.specification;

import br.com.ferrickharmony.dto.appointment.AppointmentFilterDTO;
import br.com.ferrickharmony.model.Appointment;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import jakarta.persistence.criteria.Predicate;

public class AppointmentSpecification {

    public static Specification<Appointment> withFilter(AppointmentFilterDTO filter) {
        return (root, query, builder) -> {
            Predicate p = builder.conjunction();

            if (filter == null) {
                return p;
            }

            if (StringUtils.hasText(filter.patientName())) {
                p = builder.and(p, builder.like(
                        builder.lower(root.get("patient").get("name")),
                        "%" + filter.patientName().toLowerCase() + "%"));
            }

            if (StringUtils.hasText(filter.professionalName())) {
                p = builder.and(p, builder.like(
                        builder.lower(root.get("professional").get("name")),
                        "%" + filter.professionalName().toLowerCase() + "%"));
            }

            if (filter.startDate() != null) {
                p = builder.and(p, builder.greaterThanOrEqualTo(root.get("appointmentDate"), filter.startDate()));
            }

            if (filter.endDate() != null) {
                p = builder.and(p, builder.lessThanOrEqualTo(root.get("appointmentDate"), filter.endDate()));
            }

            if (filter.status() != null) {
                p = builder.and(p, builder.equal(root.get("status"), filter.status()));
            }

            return p;
        };
    }
}