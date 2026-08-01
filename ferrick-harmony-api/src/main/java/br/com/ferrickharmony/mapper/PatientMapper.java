package br.com.ferrickharmony.mapper;

import br.com.ferrickharmony.dto.patient.PatientRequestDTO;
import br.com.ferrickharmony.dto.patient.PatientResponseDTO;
import br.com.ferrickharmony.dto.patient.PatientUpdateDTO;
import br.com.ferrickharmony.model.Address;
import br.com.ferrickharmony.model.Patient;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class PatientMapper {

    public Patient toEntity(PatientRequestDTO dto) {
        if(dto == null) return null;

        Address address = Address.builder()
                .cep(dto.cep())
                .street(dto.street())
                .number(dto.number())
                .complement(dto.complement())
                .neighborhood(dto.neighborhood())
                .city(dto.city())
                .state(dto.state())
                .build();

        return Patient
                .builder()
                .name(dto.name())
                .cpf(dto.cpf())
                .email(dto.email())
                .birthDate(dto.birthDate())
                .phone(dto.phone())
                .secondaryPhone(dto.secondaryPhone())
                .address(address)
                .build();
    }

    public PatientResponseDTO toResponseDTO(Patient entity) {
        if(entity == null) return null;

        return new PatientResponseDTO(
                entity.getId(),
                entity.getName(),
                entity.getCpf(),
                entity.getEmail(),
                entity.getBirthDate(),
                entity.getPhone(),
                entity.getSecondaryPhone(),
                entity.getAddress(),
                entity.isActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public void updateEntityFromRequest(Patient entity, PatientUpdateDTO dto) {
        if (dto == null || entity == null) return;        

        if (StringUtils.hasText(dto.name())) {
            entity.setName(dto.name());
        }

        if (StringUtils.hasText(dto.email())) {
            entity.setEmail(dto.email());
        }

        if (dto.birthDate() != null) {
            entity.setBirthDate(dto.birthDate());
        }

        if (StringUtils.hasText(dto.phone())) {
            entity.setPhone(dto.phone());
        }

        if (StringUtils.hasText(dto.secondaryPhone())) {
            entity.setSecondaryPhone(dto.secondaryPhone());
        }

        if (dto.active() != null) {
            entity.setActive(dto.active());
        }

        updateAddressFromRequest(entity, dto);
    }

    private void updateAddressFromRequest(Patient entity, PatientUpdateDTO dto) {
        if (!hasAddressData(dto)) {
            return;
        }
        
        if (entity.getAddress() == null) {
            entity.setAddress(new Address());
        }

        Address address = entity.getAddress();

        if (StringUtils.hasText(dto.cep())) address.setCep(dto.cep());
        if (StringUtils.hasText(dto.street())) address.setStreet(dto.street());
        if (StringUtils.hasText(dto.number())) address.setNumber(dto.number());
        if (StringUtils.hasText(dto.complement())) address.setComplement(dto.complement());
        if (StringUtils.hasText(dto.neighborhood())) address.setNeighborhood(dto.neighborhood());
        if (StringUtils.hasText(dto.city())) address.setCity(dto.city());
        if (StringUtils.hasText(dto.state())) address.setState(dto.state());
    }

    private boolean hasAddressData(PatientUpdateDTO dto) {
        return StringUtils.hasText(dto.cep()) ||
                StringUtils.hasText(dto.street()) ||
                StringUtils.hasText(dto.number()) ||
                StringUtils.hasText(dto.complement()) ||
                StringUtils.hasText(dto.neighborhood()) ||
                StringUtils.hasText(dto.city()) ||
                StringUtils.hasText(dto.state());
    }

}
