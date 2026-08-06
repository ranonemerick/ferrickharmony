package br.com.ferrickharmony.mapper;

import br.com.ferrickharmony.dto.professional.ProfessionalRequestDTO;
import br.com.ferrickharmony.dto.professional.ProfessionalResponseDTO;
import br.com.ferrickharmony.dto.professional.ProfessionalUpdateDTO;
import br.com.ferrickharmony.model.Professional;
import org.mapstruct.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Mapper(componentModel = "spring")
public interface ProfessionalMapper {

    @Mapping(target = "active", constant = "true")
    Professional toEntity(ProfessionalRequestDTO dto);

    ProfessionalResponseDTO toResponseDTO(Professional entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(ProfessionalUpdateDTO dto, @MappingTarget Professional entity);

}
