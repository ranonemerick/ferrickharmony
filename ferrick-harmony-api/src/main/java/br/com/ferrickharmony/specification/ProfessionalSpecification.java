package br.com.ferrickharmony.specification;

import br.com.ferrickharmony.model.Professional;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import jakarta.persistence.criteria.Predicate;

public class ProfessionalSpecification {

    public static Specification<Professional> withParameters(String name, String cpf, String document, String email) {
        return (root, query, builder) -> {
            Predicate p = builder.conjunction();

            if (StringUtils.hasText(name)) {
                p = builder.and(p, builder.like(
                        builder.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }
            if (StringUtils.hasText(cpf)) {
                p = builder.and(p, builder.like(root.get("cpf"), "%" + cpf + "%"));
            }
            if (StringUtils.hasText(document)) {
                p = builder.and(p, builder.like(root.get("document"), "%" + document + "%"));
            }
            if (StringUtils.hasText(email)) {
                p = builder.and(p, builder.like(
                        builder.lower(root.get("email")), "%" + email.toLowerCase() + "%"));
            }

            return p;
        };
    }
}