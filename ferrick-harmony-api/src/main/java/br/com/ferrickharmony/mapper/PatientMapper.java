package br.com.ferrickharmony.mapper;

import br.com.ferrickharmony.dto.patient.PatientRequestDTO;
import br.com.ferrickharmony.dto.patient.PatientResponseDTO;
import br.com.ferrickharmony.dto.patient.PatientUpdateDTO;
import br.com.ferrickharmony.model.Address;
import br.com.ferrickharmony.model.Patient;
import org.mapstruct.*;
import org.springframework.util.StringUtils;

@Mapper(componentModel = "spring")
public interface PatientMapper {

    @Mapping(source = "cep", target = "address.cep")
    @Mapping(source = "street", target = "address.street")
    @Mapping(source = "number", target = "address.number")
    @Mapping(source = "complement", target = "address.complement")
    @Mapping(source = "neighborhood", target = "address.neighborhood")
    @Mapping(source = "city", target = "address.city")
    @Mapping(source = "state", target = "address.state")
    Patient toEntity(PatientRequestDTO dto);

    PatientResponseDTO toResponseDTO(Patient entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "cep", target = "address.cep")
    @Mapping(source = "street", target = "address.street")
    @Mapping(source = "number", target = "address.number")
    @Mapping(source = "complement", target = "address.complement")
    @Mapping(source = "neighborhood", target = "address.neighborhood")
    @Mapping(source = "city", target = "address.city")
    @Mapping(source = "state", target = "address.state")
    void updateEntityFromRequest(PatientUpdateDTO dto, @MappingTarget Patient entity);

}
