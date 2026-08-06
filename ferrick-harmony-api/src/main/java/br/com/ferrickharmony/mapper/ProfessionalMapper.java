package br.com.ferrickharmony.mapper;

import br.com.ferrickharmony.dto.professional.ProfessionalRequestDTO;
import br.com.ferrickharmony.dto.professional.ProfessionalResponseDTO;
import br.com.ferrickharmony.model.Professional;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProfessionalMapper {

    @Mapping(target = "active", constant = "true")
    Professional toEntity(ProfessionalRequestDTO dto);

    ProfessionalResponseDTO toResponseDTO(Professional entity);

}
