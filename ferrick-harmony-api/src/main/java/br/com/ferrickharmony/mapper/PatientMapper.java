package br.com.ferrickharmony.mapper;

import br.com.ferrickharmony.dto.patient.PatientRequestDTO;
import br.com.ferrickharmony.dto.patient.PatientResponseDTO;
import br.com.ferrickharmony.model.Patient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

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

}
