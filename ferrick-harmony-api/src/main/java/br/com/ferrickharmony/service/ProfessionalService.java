package br.com.ferrickharmony.service;

import br.com.ferrickharmony.dto.professional.ProfessionalRequestDTO;
import br.com.ferrickharmony.dto.professional.ProfessionalResponseDTO;
import br.com.ferrickharmony.dto.professional.ProfessionalUpdateDTO;
import br.com.ferrickharmony.exception.BusinessException;
import br.com.ferrickharmony.mapper.ProfessionalMapper;
import br.com.ferrickharmony.model.Professional;
import br.com.ferrickharmony.repository.ProfessionalRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.UUID;

import static br.com.ferrickharmony.enums.ErrorKey.*;
import static br.com.ferrickharmony.utils.EmailUtils.normalizeEmail;

@Service
@RequiredArgsConstructor
public class ProfessionalService {

    private final ProfessionalRepository professionalRepository;
    private final ProfessionalMapper professionalMapper;

    @Transactional
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

    @Transactional(readOnly = true)
    public Page<ProfessionalResponseDTO> listAll(Pageable pageable) {
        return professionalRepository.findAll(pageable)
                .map(professionalMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public Page<ProfessionalResponseDTO> findActiveProfessionals(Pageable pageable) {
        return professionalRepository.findAllByActiveTrue(pageable)
                .map(professionalMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public ProfessionalResponseDTO findById(UUID id) {
        return professionalRepository.findById(id)
                .map(professionalMapper::toResponseDTO)
                .orElseThrow(() -> new EntityNotFoundException(PROFESSIONAL_NOT_FOUND.getKey()));
    }

    @Transactional(readOnly = true)
    public ProfessionalResponseDTO findByCpf(String cpf) {
        return professionalRepository.findByCpf(cpf)
                .map(professionalMapper::toResponseDTO)
                .orElseThrow(() -> new EntityNotFoundException(PROFESSIONAL_NOT_FOUND.getKey()));
    }

    @Transactional
    public ProfessionalResponseDTO update(UUID id, ProfessionalUpdateDTO professionalUpdate) {
        Professional professional = professionalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(PROFESSIONAL_NOT_FOUND.getKey()));

        if (StringUtils.hasText(professionalUpdate.email())) {
            String sanitizedEmail = normalizeEmail(professionalUpdate.email());

            if (professionalRepository.existsByEmailAndIdNot(sanitizedEmail, id)) {
                throw new BusinessException(EMAIL_ALREADY_EXISTS.getKey());
            }
            professional.setEmail(sanitizedEmail);
        }

        if (StringUtils.hasText(professionalUpdate.cpf()) &&
                professionalRepository.existsByCpfAndIdNot(professionalUpdate.cpf(), id)) {
            throw new BusinessException(PROFESSIONAL_CPF_EXISTS.getKey());
        }

        if (StringUtils.hasText(professionalUpdate.document()) &&
                professionalRepository.existsByDocumentAndIdNot(professionalUpdate.document(), id)) {
            throw new BusinessException(PROFESSIONAL_DOCUMENT_EXISTS.getKey());
        }

        professionalMapper.updateEntityFromRequest(professional, professionalUpdate);

        professional = professionalRepository.save(professional);

        return professionalMapper.toResponseDTO(professional);
    }

}
