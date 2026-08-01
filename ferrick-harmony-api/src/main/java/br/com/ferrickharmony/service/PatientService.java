package br.com.ferrickharmony.service;

import br.com.ferrickharmony.dto.patient.PatientRequestDTO;
import br.com.ferrickharmony.dto.patient.PatientResponseDTO;
import br.com.ferrickharmony.dto.patient.PatientUpdateDTO;
import br.com.ferrickharmony.exception.BusinessException;
import br.com.ferrickharmony.mapper.PatientMapper;
import br.com.ferrickharmony.model.Patient;
import br.com.ferrickharmony.repository.PatientRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.UUID;

import static br.com.ferrickharmony.utils.EmailUtils.normalizeEmail;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;

    @Transactional
    public PatientResponseDTO create(PatientRequestDTO patientRequest) {

        if(patientRepository.existsByCpf(patientRequest.cpf())) {
            throw new BusinessException("Patient with CPF " + patientRequest.cpf() + " already exists");
        }

        String sanitizedEmail = normalizeEmail(patientRequest.email());
        if (patientRepository.existsByEmail(sanitizedEmail)) {
            throw new BusinessException("Email already exists");
        }

        Patient patient = patientMapper.toEntity(patientRequest);
        patient.setEmail(sanitizedEmail);
        patient = patientRepository.save(patient);
        return patientMapper.toResponseDTO(patient);
    }

    public Page<PatientResponseDTO> listAll(Pageable pageable) {
        return patientRepository.findAll(pageable)
                .map(patientMapper::toResponseDTO);
    }

    public Page<PatientResponseDTO> findActivePatients(Pageable pageable) {
        return patientRepository.findAllByActiveTrue(pageable)
                .map(patientMapper::toResponseDTO);
    }

    public PatientResponseDTO findById(UUID id) {
        return patientRepository.findById(id)
                .map(patientMapper::toResponseDTO)
                .orElseThrow(() -> new EntityNotFoundException("Patient not found"));
    }

    public PatientResponseDTO findByCpf(String cpf) {
        return patientRepository.findByCpf(cpf)
                .map(patientMapper::toResponseDTO)
                .orElseThrow(() -> new EntityNotFoundException("Patient not found"));
    }

    @Transactional
    public PatientResponseDTO update(UUID id, PatientUpdateDTO patientUpdate) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Patient not found"));

        if (StringUtils.hasText(patientUpdate.email())) {
            String sanitizedEmail = normalizeEmail(patientUpdate.email());

            if (patientRepository.existsByEmailAndIdNot(sanitizedEmail, id)) {
                throw new BusinessException("Email already exists");
            }
            patient.setEmail(sanitizedEmail);
        }

        patientMapper.updateEntityFromRequest(patient, patientUpdate);

        patient = patientRepository.save(patient);

        return patientMapper.toResponseDTO(patient);
    }

}
