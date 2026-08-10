package br.com.ferrickharmony.specification;

import br.com.ferrickharmony.dto.professional.ProfessionalFilterDTO;
import br.com.ferrickharmony.model.Professional;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import jakarta.persistence.criteria.Predicate;

public class ProfessionalSpecification {

    public static Specification<Professional> withParameters(ProfessionalFilterDTO professional) {
        return (root, query, builder) -> {
            Predicate p = builder.conjunction();

            if (StringUtils.hasText(professional.name())) {
                p = builder.and(p, builder.like(
                        builder.lower(root.get("name")), "%" + professional.name().toLowerCase() + "%"));
            }
            if (StringUtils.hasText(professional.cpf())) {
                p = builder.and(p, builder.like(root.get("cpf"), "%" + professional.cpf() + "%"));
            }
            if (StringUtils.hasText(professional.document())) {
                p = builder.and(p, builder.like(root.get("document"), "%" + professional.document() + "%"));
            }
            if (StringUtils.hasText(professional.email())) {
                p = builder.and(p, builder.like(
                        builder.lower(root.get("email")), "%" + professional.email().toLowerCase() + "%"));
            }

            return p;
        };
    }
}