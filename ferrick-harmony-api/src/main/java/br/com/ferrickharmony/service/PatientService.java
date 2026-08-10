package br.com.ferrickharmony.service;

import br.com.ferrickharmony.dto.patient.PatientRequestDTO;
import br.com.ferrickharmony.dto.patient.PatientResponseDTO;
import br.com.ferrickharmony.dto.patient.PatientUpdateDTO;
import br.com.ferrickharmony.exception.BusinessException;
import br.com.ferrickharmony.mapper.PatientMapper;
import br.com.ferrickharmony.model.Address;
import br.com.ferrickharmony.model.Patient;
import br.com.ferrickharmony.repository.PatientRepository;
import br.com.ferrickharmony.specification.PatientSpecification;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.UUID;

import static br.com.ferrickharmony.enums.ErrorKey.*;
import static br.com.ferrickharmony.utils.EmailUtils.normalizeEmail;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;

    @Transactional
    public PatientResponseDTO create(PatientRequestDTO patientRequest) {

        if(patientRepository.existsByCpf(patientRequest.cpf())) {
            throw new BusinessException(PATIENT_CPF_EXISTS.getKey(), patientRequest.cpf());
        }

        String sanitizedEmail = normalizeEmail(patientRequest.email());
        if (patientRepository.existsByEmail(sanitizedEmail)) {
            throw new BusinessException(EMAIL_ALREADY_EXISTS.getKey());
        }

        Patient patient = patientMapper.toEntity(patientRequest);
        patient.setEmail(sanitizedEmail);
        patient = patientRepository.save(patient);
        return patientMapper.toResponseDTO(patient);
    }

    @Transactional(readOnly = true)
    public Page<PatientResponseDTO> listAll(Pageable pageable) {
        return patientRepository.findAll(pageable)
                .map(patientMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public Page<PatientResponseDTO> findActivePatients(Pageable pageable) {
        return patientRepository.findAllByActiveTrue(pageable)
                .map(patientMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public PatientResponseDTO findById(UUID id) {
        return patientRepository.findById(id)
                .map(patientMapper::toResponseDTO)
                .orElseThrow(() -> new EntityNotFoundException(PATIENT_NOT_FOUND.getKey()));
    }

    @Transactional(readOnly = true)
    public PatientResponseDTO findByCpf(String cpf) {
        return patientRepository.findByCpf(cpf)
                .map(patientMapper::toResponseDTO)
                .orElseThrow(() -> new EntityNotFoundException(PATIENT_NOT_FOUND.getKey()));
    }

    @Transactional(readOnly = true)
    public Page<PatientResponseDTO> findByParameters(String name, String cpf, String email, String phone, Pageable page) {
        Specification<Patient> spec = PatientSpecification.withParameters(name, cpf, email, phone);
        return patientRepository.findAll(spec, page)
                .map(patientMapper::toResponseDTO);
    }

    @Transactional
    public PatientResponseDTO update(UUID id, PatientUpdateDTO patientUpdate) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(PATIENT_NOT_FOUND.getKey()));

        if (StringUtils.hasText(patientUpdate.email())) {
            String sanitizedEmail = normalizeEmail(patientUpdate.email());

            if (patientRepository.existsByEmailAndIdNot(sanitizedEmail, id)) {
                throw new BusinessException(EMAIL_ALREADY_EXISTS.getKey());
            }
            patient.setEmail(sanitizedEmail);
        }

        updateEntityFromRequest(patient, patientUpdate);

        patient = patientRepository.save(patient);

        return patientMapper.toResponseDTO(patient);
    }

    private void updateEntityFromRequest(Patient entity, PatientUpdateDTO dto) {
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
        if (!hasAddressData(dto)) return;

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

    @Transactional
    public void deactivate(UUID id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(PATIENT_NOT_FOUND.getKey()));

        if (!patient.isActive()) {
            throw new BusinessException(PATIENT_ALREADY_INACTIVE.getKey());
        }
        patient.setActive(false);
        patientRepository.save(patient);
    }

}
