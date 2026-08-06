package br.com.ferrickharmony.unit.service;

import br.com.ferrickharmony.dto.professional.ProfessionalRequestDTO;
import br.com.ferrickharmony.dto.professional.ProfessionalResponseDTO;
import br.com.ferrickharmony.dto.professional.ProfessionalUpdateDTO;
import br.com.ferrickharmony.exception.BusinessException;
import br.com.ferrickharmony.mapper.ProfessionalMapper;
import br.com.ferrickharmony.model.Professional;
import br.com.ferrickharmony.repository.ProfessionalRepository;
import br.com.ferrickharmony.service.ProfessionalService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static br.com.ferrickharmony.enums.ErrorKey.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProfessionalServiceTest {

    private final String EMAIL = "professional@email.com";
    private final String UNNORMALIZED_EMAIL = "  PROFESSIONAL@Email.COM  ";
    private final String CPF = "12345678901";
    private final String DOCUMENT = "CRM-SP 12345";
    private final String NAME = "Dra. Ana Costa";
    private final String PHONE = "+5511999998888";
    private final UUID ID = UUID.randomUUID();
    private final LocalDateTime NOW = LocalDateTime.now();

    @Mock
    private ProfessionalRepository professionalRepository;

    @Mock
    private ProfessionalMapper professionalMapper;

    @InjectMocks
    private ProfessionalService professionalService;

    @Test
    void shouldCreateProfessionalWhenDataIsAvailable() {
        ProfessionalRequestDTO request = new ProfessionalRequestDTO(
                NAME, CPF, DOCUMENT, EMAIL, PHONE
        );

        Professional professional = createProfessional();
        ProfessionalResponseDTO response = createProfessionalResponse();

        when(professionalRepository.existsByCpf(CPF)).thenReturn(false);
        when(professionalRepository.existsByEmail(EMAIL)).thenReturn(false);
        when(professionalMapper.toEntity(request)).thenReturn(professional);
        when(professionalRepository.save(professional)).thenReturn(professional);
        when(professionalMapper.toResponseDTO(professional)).thenReturn(response);

        ProfessionalResponseDTO result = professionalService.create(request);

        assertNotNull(result);
        assertEquals(ID, result.id());
        assertEquals(CPF, result.cpf());
        assertEquals(DOCUMENT, result.document());
        assertEquals(EMAIL, result.email());
        assertTrue(result.active());

        verify(professionalRepository).existsByCpf(CPF);
        verify(professionalRepository).existsByEmail(EMAIL);
        verify(professionalMapper).toEntity(request);
        verify(professionalRepository).save(professional);
        verify(professionalMapper).toResponseDTO(professional);
    }

    @Test
    void shouldThrowExceptionWhenCreatingProfessionalWithExistingCpf() {
        ProfessionalRequestDTO request = new ProfessionalRequestDTO(
                NAME, CPF, DOCUMENT, EMAIL, PHONE
        );

        when(professionalRepository.existsByCpf(CPF)).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> professionalService.create(request));

        assertEquals(PROFESSIONAL_CPF_EXISTS.getKey(), exception.getMessage());

        verify(professionalRepository).existsByCpf(CPF);
        verify(professionalRepository, never()).existsByEmail(anyString());
        verify(professionalRepository, never()).save(any(Professional.class));
    }

    @Test
    void shouldThrowExceptionWhenCreatingProfessionalWithExistingEmail() {
        ProfessionalRequestDTO request = new ProfessionalRequestDTO(
                NAME, CPF, DOCUMENT, EMAIL, PHONE
        );

        when(professionalRepository.existsByCpf(CPF)).thenReturn(false);
        when(professionalRepository.existsByEmail(EMAIL)).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> professionalService.create(request));

        assertEquals(EMAIL_ALREADY_EXISTS.getKey(), exception.getMessage());

        verify(professionalRepository).existsByCpf(CPF);
        verify(professionalRepository).existsByEmail(EMAIL);
        verify(professionalRepository, never()).save(any(Professional.class));
    }

    @Test
    void shouldNormalizeEmailWhenCreatingProfessional() {
        ProfessionalRequestDTO request = new ProfessionalRequestDTO(
                NAME, CPF, DOCUMENT, UNNORMALIZED_EMAIL, PHONE
        );

        Professional professional = createProfessional();
        ProfessionalResponseDTO response = createProfessionalResponse();

        when(professionalRepository.existsByCpf(CPF)).thenReturn(false);
        when(professionalRepository.existsByEmail(EMAIL)).thenReturn(false);
        when(professionalMapper.toEntity(request)).thenReturn(professional);
        when(professionalRepository.save(professional)).thenReturn(professional);
        when(professionalMapper.toResponseDTO(professional)).thenReturn(response);

        ProfessionalResponseDTO result = professionalService.create(request);

        assertEquals(EMAIL, result.email());

        verify(professionalRepository).existsByEmail(EMAIL);
    }

    @Test
    void shouldReturnAllProfessionals() {
        Professional professional = createProfessional();
        ProfessionalResponseDTO response = createProfessionalResponse();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Professional> page = new PageImpl<>(List.of(professional));

        when(professionalRepository.findAll(pageable)).thenReturn(page);
        when(professionalMapper.toResponseDTO(professional)).thenReturn(response);

        Page<ProfessionalResponseDTO> result = professionalService.listAll(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(CPF, result.getContent().getFirst().cpf());

        verify(professionalRepository).findAll(pageable);
        verify(professionalMapper).toResponseDTO(professional);
    }

    @Test
    void shouldReturnEmptyPageWhenNoProfessionalsExist() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Professional> page = Page.empty(pageable);

        when(professionalRepository.findAll(pageable)).thenReturn(page);

        Page<ProfessionalResponseDTO> result = professionalService.listAll(pageable);

        assertTrue(result.isEmpty());

        verify(professionalRepository).findAll(pageable);
        verify(professionalMapper, never()).toResponseDTO(any());
    }

    @Test
    void shouldReturnActiveProfessionalsWhenActiveProfessionalsExist() {
        Professional professional = createProfessional();
        ProfessionalResponseDTO response = createProfessionalResponse();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Professional> page = new PageImpl<>(List.of(professional));

        when(professionalRepository.findAllByActiveTrue(pageable)).thenReturn(page);
        when(professionalMapper.toResponseDTO(professional)).thenReturn(response);

        Page<ProfessionalResponseDTO> result = professionalService.findActiveProfessionals(pageable);

        assertAll(
                () -> assertEquals(1, result.getTotalElements()),
                () -> assertEquals(ID, result.getContent().getFirst().id()),
                () -> assertEquals(CPF, result.getContent().getFirst().cpf()),
                () -> assertTrue(result.getContent().getFirst().active())
        );

        verify(professionalRepository).findAllByActiveTrue(pageable);
        verify(professionalMapper).toResponseDTO(professional);
    }

    @Test
    void shouldReturnEmptyPageWhenNoActiveProfessionalsExist() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Professional> page = Page.empty(pageable);

        when(professionalRepository.findAllByActiveTrue(pageable)).thenReturn(page);

        Page<ProfessionalResponseDTO> result = professionalService.findActiveProfessionals(pageable);

        assertTrue(result.isEmpty());

        verify(professionalRepository).findAllByActiveTrue(pageable);
        verify(professionalMapper, never()).toResponseDTO(any(Professional.class));
    }

    @Test
    void shouldReturnProfessionalWhenIdExists() {
        Professional professional = createProfessional();
        ProfessionalResponseDTO response = createProfessionalResponse();

        when(professionalRepository.findById(ID)).thenReturn(Optional.of(professional));
        when(professionalMapper.toResponseDTO(professional)).thenReturn(response);

        ProfessionalResponseDTO result = professionalService.findById(ID);

        assertAll(
                () -> assertEquals(ID, result.id()),
                () -> assertEquals(CPF, result.cpf()),
                () -> assertEquals(DOCUMENT, result.document()),
                () -> assertEquals(EMAIL, result.email()),
                () -> assertTrue(result.active())
        );

        verify(professionalRepository).findById(ID);
        verify(professionalMapper).toResponseDTO(professional);
    }

    @Test
    void shouldThrowExceptionWhenFindingProfessionalWithNonExistingId() {
        when(professionalRepository.findById(ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> professionalService.findById(ID));

        assertEquals(PROFESSIONAL_NOT_FOUND.getKey(), exception.getMessage());

        verify(professionalRepository).findById(ID);
        verify(professionalMapper, never()).toResponseDTO(any());
    }

    @Test
    void shouldReturnProfessionalWhenCpfExists() {
        Professional professional = createProfessional();
        ProfessionalResponseDTO response = createProfessionalResponse();

        when(professionalRepository.findByCpf(CPF)).thenReturn(Optional.of(professional));
        when(professionalMapper.toResponseDTO(professional)).thenReturn(response);

        assertEquals(response, professionalService.findByCpf(CPF));

        verify(professionalRepository).findByCpf(CPF);
        verify(professionalMapper).toResponseDTO(professional);
    }

    @Test
    void shouldThrowExceptionWhenFindingProfessionalWithNonExistingCpf() {
        when(professionalRepository.findByCpf(CPF)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class, () -> professionalService.findByCpf(CPF)
        );

        assertEquals(PROFESSIONAL_NOT_FOUND.getKey(), exception.getMessage());

        verify(professionalRepository).findByCpf(CPF);
        verify(professionalMapper, never()).toResponseDTO(any());
    }

    @Test
    void shouldUpdateProfessionalWhenProfessionalExists() {
        Professional professional = createProfessional();
        ProfessionalUpdateDTO updateDTO = new ProfessionalUpdateDTO(
                "Ana Costa Updated", null, null, null, null, false
        );
        ProfessionalResponseDTO response = new ProfessionalResponseDTO(
                ID, "Ana Costa Updated", CPF, DOCUMENT, EMAIL, PHONE, NOW, NOW, false
        );

        when(professionalRepository.findById(ID)).thenReturn(Optional.of(professional));
        when(professionalRepository.save(professional)).thenReturn(professional);
        when(professionalMapper.toResponseDTO(professional)).thenReturn(response);

        ProfessionalResponseDTO result = professionalService.update(ID, updateDTO);

        assertNotNull(result);
        assertEquals("Ana Costa Updated", result.name());
        assertFalse(result.active());

        verify(professionalRepository).findById(ID);
        verify(professionalMapper).updateEntityFromRequest(professional, updateDTO);
        verify(professionalRepository).save(professional);
        verify(professionalMapper).toResponseDTO(professional);
    }

    @Test
    void shouldUpdateProfessionalEmailWhenNewEmailIsAvailable() {
        Professional professional = createProfessional();
        String newEmail = "new@email.com";
        ProfessionalUpdateDTO updateDTO = new ProfessionalUpdateDTO(
                null, null, null, newEmail, null, null
        );
        ProfessionalResponseDTO response = new ProfessionalResponseDTO(
                ID, NAME, CPF, DOCUMENT, newEmail, PHONE, NOW, NOW, true
        );

        when(professionalRepository.findById(ID)).thenReturn(Optional.of(professional));
        when(professionalRepository.existsByEmailAndIdNot(newEmail, ID)).thenReturn(false);
        when(professionalRepository.save(professional)).thenReturn(professional);
        when(professionalMapper.toResponseDTO(professional)).thenReturn(response);

        ProfessionalResponseDTO result = professionalService.update(ID, updateDTO);

        assertNotNull(result);
        assertEquals(newEmail, result.email());

        verify(professionalRepository).findById(ID);
        verify(professionalRepository).existsByEmailAndIdNot(newEmail, ID);
        verify(professionalMapper).updateEntityFromRequest(professional, updateDTO);
        verify(professionalRepository).save(professional);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingProfessionalWithExistingEmail() {
        Professional professional = createProfessional();
        String existingEmail = "existing@email.com";
        ProfessionalUpdateDTO updateDTO = new ProfessionalUpdateDTO(
                null, null, null, existingEmail, null, null
        );

        when(professionalRepository.findById(ID)).thenReturn(Optional.of(professional));
        when(professionalRepository.existsByEmailAndIdNot(existingEmail, ID)).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> professionalService.update(ID, updateDTO));

        assertEquals(EMAIL_ALREADY_EXISTS.getKey(), exception.getMessage());

        verify(professionalRepository).findById(ID);
        verify(professionalRepository).existsByEmailAndIdNot(existingEmail, ID);
        verify(professionalMapper, never()).updateEntityFromRequest(any(), any());
        verify(professionalRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingProfessionalWithExistingCpf() {
        Professional professional = createProfessional();
        String existingCpf = "09876543210";
        ProfessionalUpdateDTO updateDTO = new ProfessionalUpdateDTO(
                null, existingCpf, null, null, null, null
        );

        when(professionalRepository.findById(ID)).thenReturn(Optional.of(professional));
        when(professionalRepository.existsByCpfAndIdNot(existingCpf, ID)).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> professionalService.update(ID, updateDTO));

        assertEquals(PROFESSIONAL_CPF_EXISTS.getKey(), exception.getMessage());

        verify(professionalRepository).findById(ID);
        verify(professionalRepository).existsByCpfAndIdNot(existingCpf, ID);
        verify(professionalMapper, never()).updateEntityFromRequest(any(), any());
        verify(professionalRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingProfessionalWithExistingDocument() {
        Professional professional = createProfessional();
        String existingDocument = "CRO-SP 99999";
        ProfessionalUpdateDTO updateDTO = new ProfessionalUpdateDTO(
                null, null, existingDocument, null, null, null
        );

        when(professionalRepository.findById(ID)).thenReturn(Optional.of(professional));
        when(professionalRepository.existsByDocumentAndIdNot(existingDocument, ID)).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> professionalService.update(ID, updateDTO));

        assertEquals(PROFESSIONAL_DOCUMENT_EXISTS.getKey(), exception.getMessage());

        verify(professionalRepository).findById(ID);
        verify(professionalRepository).existsByDocumentAndIdNot(existingDocument, ID);
        verify(professionalMapper, never()).updateEntityFromRequest(any(), any());
        verify(professionalRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingProfessional() {
        ProfessionalUpdateDTO updateDTO = new ProfessionalUpdateDTO(
                "Ana Costa Updated", null, null, null, null, null
        );

        when(professionalRepository.findById(ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> professionalService.update(ID, updateDTO));

        assertEquals(PROFESSIONAL_NOT_FOUND.getKey(), exception.getMessage());

        verify(professionalRepository).findById(ID);
        verify(professionalRepository, never()).existsByEmailAndIdNot(anyString(), any());
        verify(professionalMapper, never()).updateEntityFromRequest(any(), any());
        verify(professionalRepository, never()).save(any());
    }

    @Test
    void shouldDeactivateProfessionalWhenProfessionalIsActive() {
        Professional professional = createProfessional();

        when(professionalRepository.findById(ID)).thenReturn(Optional.of(professional));

        professionalService.deactivate(ID);

        assertFalse(professional.isActive());
        verify(professionalRepository).findById(ID);
    }

    @Test
    void shouldThrowExceptionWhenDeactivatingAlreadyInactiveProfessional() {
        Professional professional = createProfessional();
        professional.setActive(false);

        when(professionalRepository.findById(ID)).thenReturn(Optional.of(professional));

        BusinessException exception = assertThrows(BusinessException.class,
                () -> professionalService.deactivate(ID));

        assertEquals(PROFESSIONAL_ALREADY_INACTIVE.getKey(), exception.getMessage());

        verify(professionalRepository).findById(ID);
    }

    @Test
    void shouldThrowExceptionWhenDeactivatingNonExistingProfessional() {
        when(professionalRepository.findById(ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> professionalService.deactivate(ID));

        assertEquals(PROFESSIONAL_NOT_FOUND.getKey(), exception.getMessage());

        verify(professionalRepository).findById(ID);
    }

    private Professional createProfessional() {
        return Professional.builder()
                .id(ID)
                .name(NAME)
                .cpf(CPF)
                .document(DOCUMENT)
                .email(EMAIL)
                .phone(PHONE)
                .active(true)
                .createdAt(NOW)
                .updatedAt(NOW)
                .build();
    }

    private ProfessionalResponseDTO createProfessionalResponse() {
        return new ProfessionalResponseDTO(
                ID, NAME, CPF, DOCUMENT, EMAIL, PHONE, NOW, NOW, true
        );
    }
}