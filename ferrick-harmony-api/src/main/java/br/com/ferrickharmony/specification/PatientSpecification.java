package br.com.ferrickharmony.specification;

import br.com.ferrickharmony.dto.patient.PatientFilterDTO;
import br.com.ferrickharmony.model.Patient;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;


public class PatientSpecification {

    public static Specification<Patient> withParameters(PatientFilterDTO patient) {
        return (root, query, builder) -> {
            Predicate p = builder.conjunction();

            if(StringUtils.hasText(patient.name())) {
                p = builder.and(p, builder.like(
                        builder.lower(root.get("name")), "%" + patient.name().toLowerCase() + "%"));
            }

            if(StringUtils.hasText(patient.cpf())) {
                p = builder.and(p, builder.like(builder.lower(root.get("cpf")), "%" + patient.cpf() + "%" ));
            }

            if(StringUtils.hasText(patient.email())) {
                p = builder.and(p, builder.like(
                        builder.lower(root.get("email")), "%" + patient.email().toLowerCase() + "%"));
            }

            if(StringUtils.hasText(patient.phone())) {
                p = builder.and(p, builder.like(builder.lower(root.get("phone")), "%" + patient.phone() + "%" ));
            }
            return p;
        };
    }

}
