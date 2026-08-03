package br.com.ferrickharmony.service;

import br.com.ferrickharmony.dto.professional.ProfessionalRequestDTO;
import br.com.ferrickharmony.dto.professional.ProfessionalResponseDTO;
import br.com.ferrickharmony.exception.BusinessException;
import br.com.ferrickharmony.mapper.ProfessionalMapper;
import br.com.ferrickharmony.model.Professional;
import br.com.ferrickharmony.repository.ProfessionalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static br.com.ferrickharmony.enums.ErrorKey.EMAIL_ALREADY_EXISTS;
import static br.com.ferrickharmony.enums.ErrorKey.PROFESSIONAL_CPF_EXISTS;
import static br.com.ferrickharmony.utils.EmailUtils.normalizeEmail;

@Service
@RequiredArgsConstructor
public class ProfessionalService {

    private final ProfessionalRepository professionalRepository;
    private final ProfessionalMapper professionalMapper;

    public ProfessionalResponseDTO create(ProfessionalRequestDTO professionalRequest) {
        if(professionalRepository.existsByCpf(professionalRequest.cpf())) {
            throw new BusinessException(PROFESSIONAL_CPF_EXISTS.getKey(), professionalRequest.cpf());
        }

        String sanitizedEmail = normalizeEmail(professionalRequest.email());
        if (professionalRepository.existsByEmail(sanitizedEmail)) {
            throw new BusinessException(EMAIL_ALREADY_EXISTS.getKey());
        }

        Professional professional = professionalMapper.toEntity(professionalRequest);
        professional.setEmail(sanitizedEmail);
        professionalRepository.save(professional);
        return professionalMapper.toResponseDTO(professional);
    }
}
