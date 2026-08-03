package br.com.ferrickharmony.unit.service;

import br.com.ferrickharmony.dto.patient.PatientRequestDTO;
import br.com.ferrickharmony.dto.patient.PatientResponseDTO;
import br.com.ferrickharmony.dto.patient.PatientUpdateDTO;
import br.com.ferrickharmony.exception.BusinessException;
import br.com.ferrickharmony.mapper.PatientMapper;
import br.com.ferrickharmony.model.Patient;
import br.com.ferrickharmony.repository.PatientRepository;
import br.com.ferrickharmony.service.PatientService;
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

import java.time.LocalDate;
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
public class PatientServiceTest {

    private final String EMAIL = "patient@email.com";
    private final String UNNORMALIZED_EMAIL = "  PATIENT@Email.COM  ";
    private final String CPF = "12345678901";
    private final String NAME = "Carlos Eduardo";
    private final String PHONE = "+5511999998888";
    private final LocalDate BIRTH_DATE = LocalDate.of(1985, 10, 22);
    private final UUID ID = UUID.randomUUID();
    private final LocalDateTime NOW = LocalDateTime.now();

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private PatientMapper patientMapper;

    @InjectMocks
    private PatientService patientService;

    @Test
    void shouldCreatePatientWhenDataIsAvailable() {
        PatientRequestDTO request = new PatientRequestDTO(
                NAME, CPF, EMAIL, BIRTH_DATE, PHONE, null, null, null, null, null, null, null, null
        );

        Patient patient = createPatient();
        PatientResponseDTO response = createPatientResponse();

        when(patientRepository.existsByCpf(CPF)).thenReturn(false);
        when(patientRepository.existsByEmail(EMAIL)).thenReturn(false);
        when(patientMapper.toEntity(request)).thenReturn(patient);
        when(patientRepository.save(patient)).thenReturn(patient);
        when(patientMapper.toResponseDTO(patient)).thenReturn(response);

        PatientResponseDTO result = patientService.create(request);

        assertNotNull(result);
        assertEquals(ID, result.id());
        assertEquals(CPF, result.cpf());
        assertEquals(EMAIL, result.email());
        assertTrue(result.active());

        verify(patientRepository).existsByCpf(CPF);
        verify(patientRepository).existsByEmail(EMAIL);
        verify(patientMapper).toEntity(request);
        verify(patientRepository).save(patient);
        verify(patientMapper).toResponseDTO(patient);
    }

    @Test
    void shouldThrowExceptionWhenCreatingPatientWithExistingCpf() {
        PatientRequestDTO request = new PatientRequestDTO(
                NAME, CPF, EMAIL, BIRTH_DATE, PHONE, null, null, null, null, null, null, null, null
        );

        when(patientRepository.existsByCpf(CPF)).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> patientService.create(request));

        assertEquals(PATIENT_CPF_EXISTS.getKey(), exception.getMessage());

        verify(patientRepository).existsByCpf(CPF);
        verify(patientRepository, never()).existsByEmail(anyString());
        verify(patientRepository, never()).save(any(Patient.class));
    }

    @Test
    void shouldThrowExceptionWhenCreatingPatientWithExistingEmail() {
        PatientRequestDTO request = new PatientRequestDTO(
                NAME, CPF, EMAIL, BIRTH_DATE, PHONE, null, null, null, null, null, null, null, null
        );

        when(patientRepository.existsByCpf(CPF)).thenReturn(false);
        when(patientRepository.existsByEmail(EMAIL)).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> patientService.create(request));

        assertEquals(EMAIL_ALREADY_EXISTS.getKey(), exception.getMessage());

        verify(patientRepository).existsByCpf(CPF);
        verify(patientRepository).existsByEmail(EMAIL);
        verify(patientRepository, never()).save(any(Patient.class));
    }

    @Test
    void shouldNormalizeEmailWhenCreatingPatient() {
        PatientRequestDTO request = new PatientRequestDTO(
                NAME, CPF, UNNORMALIZED_EMAIL, BIRTH_DATE, PHONE, null, null, null, null, null, null, null, null
        );

        Patient patient = createPatient();
        PatientResponseDTO response = createPatientResponse();

        when(patientRepository.existsByCpf(CPF)).thenReturn(false);
        when(patientRepository.existsByEmail(EMAIL)).thenReturn(false);
        when(patientMapper.toEntity(request)).thenReturn(patient);
        when(patientRepository.save(patient)).thenReturn(patient);
        when(patientMapper.toResponseDTO(patient)).thenReturn(response);

        PatientResponseDTO result = patientService.create(request);

        assertEquals(EMAIL, result.email());

        verify(patientRepository).existsByEmail(EMAIL);
    }

    @Test
    void shouldReturnAllPatients() {
        Patient patient = createPatient();
        PatientResponseDTO response = createPatientResponse();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Patient> page = new PageImpl<>(List.of(patient));

        when(patientRepository.findAll(pageable)).thenReturn(page);
        when(patientMapper.toResponseDTO(patient)).thenReturn(response);

        Page<PatientResponseDTO> result = patientService.listAll(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(CPF, result.getContent().getFirst().cpf());

        verify(patientRepository).findAll(pageable);
        verify(patientMapper).toResponseDTO(patient);
    }

    @Test
    void shouldReturnEmptyPageWhenNoPatientsExist() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Patient> page = Page.empty(pageable);

        when(patientRepository.findAll(pageable)).thenReturn(page);

        Page<PatientResponseDTO> result = patientService.listAll(pageable);

        assertTrue(result.isEmpty());

        verify(patientRepository).findAll(pageable);
        verify(patientMapper, never()).toResponseDTO(any());
    }

    @Test
    void shouldReturnActivePatientsWhenActivePatientsExist() {
        Patient patient = createPatient();
        PatientResponseDTO response = createPatientResponse();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Patient> page = new PageImpl<>(List.of(patient));

        when(patientRepository.findAllByActiveTrue(pageable)).thenReturn(page);
        when(patientMapper.toResponseDTO(patient)).thenReturn(response);

        Page<PatientResponseDTO> result = patientService.findActivePatients(pageable);

        assertAll(
                () -> assertEquals(1, result.getTotalElements()),
                () -> assertEquals(ID, result.getContent().getFirst().id()),
                () -> assertEquals(CPF, result.getContent().getFirst().cpf()),
                () -> assertTrue(result.getContent().getFirst().active())
        );

        verify(patientRepository).findAllByActiveTrue(pageable);
        verify(patientMapper).toResponseDTO(patient);
    }

    @Test
    void shouldReturnEmptyPageWhenNoActivePatientsExist() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Patient> page = Page.empty(pageable);

        when(patientRepository.findAllByActiveTrue(pageable)).thenReturn(page);

        Page<PatientResponseDTO> result = patientService.findActivePatients(pageable);

        assertTrue(result.isEmpty());

        verify(patientRepository).findAllByActiveTrue(pageable);
        verify(patientMapper, never()).toResponseDTO(any(Patient.class));
    }

    @Test
    void shouldReturnPatientWhenIdExists() {
        Patient patient = createPatient();
        PatientResponseDTO response = createPatientResponse();

        when(patientRepository.findById(ID)).thenReturn(Optional.of(patient));
        when(patientMapper.toResponseDTO(patient)).thenReturn(response);

        PatientResponseDTO result = patientService.findById(ID);

        assertAll(
                () -> assertEquals(ID, result.id()),
                () -> assertEquals(CPF, result.cpf()),
                () -> assertEquals(EMAIL, result.email()),
                () -> assertTrue(result.active())
        );

        verify(patientRepository).findById(ID);
        verify(patientMapper).toResponseDTO(patient);
    }

    @Test
    void shouldThrowExceptionWhenFindingPatientWithNonExistingId() {
        when(patientRepository.findById(ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> patientService.findById(ID));

        assertEquals(PATIENT_NOT_FOUND.getKey(), exception.getMessage());

        verify(patientRepository).findById(ID);
        verify(patientMapper, never()).toResponseDTO(any());
    }

    @Test
    void shouldReturnPatientWhenCpfExists() {
        Patient patient = createPatient();
        PatientResponseDTO response = createPatientResponse();

        when(patientRepository.findByCpf(CPF)).thenReturn(Optional.of(patient));
        when(patientMapper.toResponseDTO(patient)).thenReturn(response);

        assertEquals(response, patientService.findByCpf(CPF));

        verify(patientRepository).findByCpf(CPF);
        verify(patientMapper).toResponseDTO(patient);
    }

    @Test
    void shouldThrowExceptionWhenFindingPatientWithNonExistingCpf() {
        when(patientRepository.findByCpf(CPF)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class, () -> patientService.findByCpf(CPF)
        );

        assertEquals(PATIENT_NOT_FOUND.getKey(), exception.getMessage());

        verify(patientRepository).findByCpf(CPF);
        verify(patientMapper, never()).toResponseDTO(any());
    }

    @Test
    void shouldUpdatePatientWhenPatientExists() {
        Patient patient = createPatient();
        PatientUpdateDTO updateDTO = new PatientUpdateDTO(
                "Carlos Updated", null, null, null, null, null, null, null, null, null, null, null, false
        );
        PatientResponseDTO response = new PatientResponseDTO(
                ID, "Carlos Updated", CPF, EMAIL, BIRTH_DATE, PHONE, null, null, false, NOW, NOW
        );

        when(patientRepository.findById(ID)).thenReturn(Optional.of(patient));
        when(patientRepository.save(patient)).thenReturn(patient);
        when(patientMapper.toResponseDTO(patient)).thenReturn(response);

        PatientResponseDTO result = patientService.update(ID, updateDTO);

        assertNotNull(result);
        assertEquals("Carlos Updated", result.name());
        assertFalse(result.active());

        verify(patientRepository).findById(ID);
        verify(patientMapper).updateEntityFromRequest(patient, updateDTO);
        verify(patientRepository).save(patient);
        verify(patientMapper).toResponseDTO(patient);
    }

    @Test
    void shouldUpdatePatientEmailWhenNewEmailIsAvailable() {
        Patient patient = createPatient();
        String newEmail = "new@email.com";
        PatientUpdateDTO updateDTO = new PatientUpdateDTO(
                null, newEmail, null, null, null, null, null, null, null, null, null, null, null
        );
        PatientResponseDTO response = new PatientResponseDTO(
                ID, NAME, CPF, newEmail, BIRTH_DATE, PHONE, null, null, true, NOW, NOW
        );

        when(patientRepository.findById(ID)).thenReturn(Optional.of(patient));
        when(patientRepository.existsByEmailAndIdNot(newEmail, ID)).thenReturn(false);
        when(patientRepository.save(patient)).thenReturn(patient);
        when(patientMapper.toResponseDTO(patient)).thenReturn(response);

        PatientResponseDTO result = patientService.update(ID, updateDTO);

        assertNotNull(result);
        assertEquals(newEmail, result.email());

        verify(patientRepository).findById(ID);
        verify(patientRepository).existsByEmailAndIdNot(newEmail, ID);
        verify(patientMapper).updateEntityFromRequest(patient, updateDTO);
        verify(patientRepository).save(patient);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingPatientWithExistingEmail() {
        Patient patient = createPatient();
        String existingEmail = "existing@email.com";
        PatientUpdateDTO updateDTO = new PatientUpdateDTO(
                null, existingEmail, null, null, null, null, null, null, null, null, null, null, null
        );

        when(patientRepository.findById(ID)).thenReturn(Optional.of(patient));
        when(patientRepository.existsByEmailAndIdNot(existingEmail, ID)).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> patientService.update(ID, updateDTO));

        assertEquals(EMAIL_ALREADY_EXISTS.getKey(), exception.getMessage());

        verify(patientRepository).findById(ID);
        verify(patientRepository).existsByEmailAndIdNot(existingEmail, ID);
        verify(patientMapper, never()).updateEntityFromRequest(any(), any());
        verify(patientRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingPatient() {
        PatientUpdateDTO updateDTO = new PatientUpdateDTO(
                "Carlos Updated", null, null, null, null, null, null, null, null, null, null, null, null
        );

        when(patientRepository.findById(ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> patientService.update(ID, updateDTO));

        assertEquals(PATIENT_NOT_FOUND.getKey(), exception.getMessage());

        verify(patientRepository).findById(ID);
        verify(patientRepository, never()).existsByEmailAndIdNot(anyString(), any());
        verify(patientMapper, never()).updateEntityFromRequest(any(), any());
        verify(patientRepository, never()).save(any());
    }

    @Test
    void shouldDeactivatePatientWhenPatientIsActive() {
        Patient patient = createPatient();

        when(patientRepository.findById(ID)).thenReturn(Optional.of(patient));

        patientService.deactivate(ID);

        assertFalse(patient.isActive());
        verify(patientRepository).findById(ID);
        verify(patientRepository).save(patient);
    }

    @Test
    void shouldThrowExceptionWhenDeactivatingAlreadyInactivePatient() {
        Patient patient = createPatient();
        patient.setActive(false);

        when(patientRepository.findById(ID)).thenReturn(Optional.of(patient));

        BusinessException exception = assertThrows(BusinessException.class,
                () -> patientService.deactivate(ID));

        assertEquals(PATIENT_ALREADY_INACTIVE.getKey(), exception.getMessage());

        verify(patientRepository).findById(ID);
        verify(patientRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenDeactivatingNonExistingPatient() {
        when(patientRepository.findById(ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> patientService.deactivate(ID));

        assertEquals(PATIENT_NOT_FOUND.getKey(), exception.getMessage());

        verify(patientRepository).findById(ID);
        verify(patientRepository, never()).save(any());
    }

    private Patient createPatient() {
        return Patient.builder()
                .id(ID)
                .name(NAME)
                .cpf(CPF)
                .email(EMAIL)
                .birthDate(BIRTH_DATE)
                .phone(PHONE)
                .active(true)
                .createdAt(NOW)
                .updatedAt(NOW)
                .build();
    }

    private PatientResponseDTO createPatientResponse() {
        return new PatientResponseDTO(
                ID, NAME, CPF, EMAIL, BIRTH_DATE, PHONE, null, null, true, NOW, NOW
        );
    }
}