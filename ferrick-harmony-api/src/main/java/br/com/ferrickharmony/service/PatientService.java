package br.com.ferrickharmony.service;

import br.com.ferrickharmony.dto.patient.PatientRequestDTO;
import br.com.ferrickharmony.dto.patient.PatientResponseDTO;
import br.com.ferrickharmony.exception.BusinessException;
import br.com.ferrickharmony.mapper.PatientMapper;
import br.com.ferrickharmony.model.Patient;
import br.com.ferrickharmony.repository.PatientRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

}
