package br.com.ferrickharmony.specification;

import br.com.ferrickharmony.model.Patient;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;


public class PatientSpecification {

    public static Specification<Patient> withParameters(String name, String cpf, String email, String phone) {
        return (root, query, builder) -> {
            Predicate p = builder.conjunction();

            if(StringUtils.hasText(name)) {
                p = builder.and(p, builder.like(
                        builder.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }

            if(StringUtils.hasText(cpf)) {
                p = builder.and(p, builder.like(builder.lower(root.get("cpf")), "%" + cpf + "%" ));
            }

            if(StringUtils.hasText(email)) {
                p = builder.and(p, builder.like(
                        builder.lower(root.get("email")), "%" + email.toLowerCase() + "%"));
            }

            if(StringUtils.hasText(phone)) {
                p = builder.and(p, builder.like(builder.lower(root.get("phone")), "%" + phone + "%" ));
            }
            return p;
        };
    }

}
