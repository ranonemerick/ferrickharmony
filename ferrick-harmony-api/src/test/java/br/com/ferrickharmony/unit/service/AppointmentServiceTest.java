package br.com.ferrickharmony.unit.service;

import br.com.ferrickharmony.dto.appointment.AppointmentFilterDTO;
import br.com.ferrickharmony.dto.appointment.AppointmentRequestDTO;
import br.com.ferrickharmony.dto.appointment.AppointmentResponseDTO;
import br.com.ferrickharmony.dto.appointment.AppointmentUpdateDTO;
import br.com.ferrickharmony.exception.BusinessException;
import br.com.ferrickharmony.mapper.AppointmentMapper;
import br.com.ferrickharmony.model.Appointment;
import br.com.ferrickharmony.model.Patient;
import br.com.ferrickharmony.model.Professional;
import br.com.ferrickharmony.repository.AppointmentRepository;
import br.com.ferrickharmony.repository.PatientRepository;
import br.com.ferrickharmony.repository.ProfessionalRepository;
import br.com.ferrickharmony.service.AppointmentService;
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
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static br.com.ferrickharmony.enums.AppointmentStatus.CANCELED;
import static br.com.ferrickharmony.enums.AppointmentStatus.SCHEDULED;
import static br.com.ferrickharmony.enums.ErrorKey.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AppointmentServiceTest {

    private final UUID APPOINTMENT_ID = UUID.randomUUID();
    private final UUID PATIENT_ID = UUID.randomUUID();
    private final UUID PROFESSIONAL_ID = UUID.randomUUID();
    private final String PATIENT_NAME = "Maria da Silva";
    private final String PROFESSIONAL_NAME = "Dra. Ana Costa";
    private final LocalDateTime APPOINTMENT_DATE = LocalDateTime.now().plusDays(5);
    private final String LOCATION = "Consultório 01";
    private final String NOTES = "Primeira consulta";
    private final LocalDateTime NOW = LocalDateTime.now();

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private ProfessionalRepository professionalRepository;

    @Mock
    private AppointmentMapper appointmentMapper;

    @InjectMocks
    private AppointmentService appointmentService;

    @Test
    void shouldCreateAppointmentWhenDataIsAvailable() {
        AppointmentRequestDTO request = new AppointmentRequestDTO(
                PATIENT_ID, PROFESSIONAL_ID, APPOINTMENT_DATE, LOCATION, NOTES
        );

        Patient patient = createPatient(true);
        Professional professional = createProfessional(true);
        Appointment appointment = createAppointment();
        AppointmentResponseDTO response = createAppointmentResponse();

        when(patientRepository.findById(PATIENT_ID)).thenReturn(Optional.of(patient));
        when(professionalRepository.findById(PROFESSIONAL_ID)).thenReturn(Optional.of(professional));
        when(appointmentRepository.existsByProfessionalIdAndAppointmentDateAndStatusNot(
                PROFESSIONAL_ID, APPOINTMENT_DATE, CANCELED)).thenReturn(false);
        when(appointmentMapper.toEntity(request)).thenReturn(appointment);
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);
        when(appointmentMapper.toResponseDTO(appointment)).thenReturn(response);

        AppointmentResponseDTO result = appointmentService.create(request);

        assertNotNull(result);
        assertEquals(APPOINTMENT_ID, result.id());
        assertEquals(PATIENT_ID, result.patientId());
        assertEquals(PROFESSIONAL_ID, result.professionalId());
        assertEquals(SCHEDULED, result.status());

        verify(patientRepository).findById(PATIENT_ID);
        verify(professionalRepository).findById(PROFESSIONAL_ID);
        verify(appointmentRepository).existsByProfessionalIdAndAppointmentDateAndStatusNot(
                PROFESSIONAL_ID, APPOINTMENT_DATE, CANCELED);
        verify(appointmentMapper).toEntity(request);
        verify(appointmentRepository).save(any(Appointment.class));
        verify(appointmentMapper).toResponseDTO(appointment);
    }

    @Test
    void shouldThrowExceptionWhenCreatingAppointmentWithNonExistingPatient() {
        AppointmentRequestDTO request = new AppointmentRequestDTO(
                PATIENT_ID, PROFESSIONAL_ID, APPOINTMENT_DATE, LOCATION, NOTES
        );

        when(patientRepository.findById(PATIENT_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> appointmentService.create(request));

        assertEquals(PATIENT_NOT_FOUND.getKey(), exception.getMessage());
        verify(patientRepository).findById(PATIENT_ID);
        verify(professionalRepository, never()).findById(any());
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenCreatingAppointmentWithInactivePatient() {
        AppointmentRequestDTO request = new AppointmentRequestDTO(
                PATIENT_ID, PROFESSIONAL_ID, APPOINTMENT_DATE, LOCATION, NOTES
        );

        Patient inactivePatient = createPatient(false);
        when(patientRepository.findById(PATIENT_ID)).thenReturn(Optional.of(inactivePatient));

        BusinessException exception = assertThrows(BusinessException.class,
                () -> appointmentService.create(request));

        assertEquals(PATIENT_ALREADY_INACTIVE.getKey(), exception.getMessage());
        verify(patientRepository).findById(PATIENT_ID);
        verify(professionalRepository, never()).findById(any());
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenCreatingAppointmentWithNonExistingProfessional() {
        AppointmentRequestDTO request = new AppointmentRequestDTO(
                PATIENT_ID, PROFESSIONAL_ID, APPOINTMENT_DATE, LOCATION, NOTES
        );

        Patient patient = createPatient(true);
        when(patientRepository.findById(PATIENT_ID)).thenReturn(Optional.of(patient));
        when(professionalRepository.findById(PROFESSIONAL_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> appointmentService.create(request));

        assertEquals(PROFESSIONAL_NOT_FOUND.getKey(), exception.getMessage());
        verify(patientRepository).findById(PATIENT_ID);
        verify(professionalRepository).findById(PROFESSIONAL_ID);
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenCreatingAppointmentWithInactiveProfessional() {
        AppointmentRequestDTO request = new AppointmentRequestDTO(
                PATIENT_ID, PROFESSIONAL_ID, APPOINTMENT_DATE, LOCATION, NOTES
        );

        Patient patient = createPatient(true);
        Professional inactiveProfessional = createProfessional(false);

        when(patientRepository.findById(PATIENT_ID)).thenReturn(Optional.of(patient));
        when(professionalRepository.findById(PROFESSIONAL_ID)).thenReturn(Optional.of(inactiveProfessional));

        BusinessException exception = assertThrows(BusinessException.class,
                () -> appointmentService.create(request));

        assertEquals(PROFESSIONAL_ALREADY_INACTIVE.getKey(), exception.getMessage());
        verify(patientRepository).findById(PATIENT_ID);
        verify(professionalRepository).findById(PROFESSIONAL_ID);
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenCreatingAppointmentWithScheduleConflict() {
        AppointmentRequestDTO request = new AppointmentRequestDTO(
                PATIENT_ID, PROFESSIONAL_ID, APPOINTMENT_DATE, LOCATION, NOTES
        );

        Patient patient = createPatient(true);
        Professional professional = createProfessional(true);

        when(patientRepository.findById(PATIENT_ID)).thenReturn(Optional.of(patient));
        when(professionalRepository.findById(PROFESSIONAL_ID)).thenReturn(Optional.of(professional));
        when(appointmentRepository.existsByProfessionalIdAndAppointmentDateAndStatusNot(
                PROFESSIONAL_ID, APPOINTMENT_DATE, CANCELED)).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> appointmentService.create(request));

        assertEquals(APPOINTMENT_CONFLICT.getKey(), exception.getMessage());
        verify(appointmentRepository).existsByProfessionalIdAndAppointmentDateAndStatusNot(
                PROFESSIONAL_ID, APPOINTMENT_DATE, CANCELED);
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void shouldReturnAllAppointments() {
        Appointment appointment = createAppointment();
        AppointmentResponseDTO response = createAppointmentResponse();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Appointment> page = new PageImpl<>(List.of(appointment));

        when(appointmentRepository.findAll(pageable)).thenReturn(page);
        when(appointmentMapper.toResponseDTO(appointment)).thenReturn(response);

        Page<AppointmentResponseDTO> result = appointmentService.findAll(pageable);

        assertEquals(1, result.getTotalElements());
        verify(appointmentRepository).findAll(pageable);
    }

    @Test
    void shouldReturnAppointmentWhenIdExists() {
        Appointment appointment = createAppointment();
        AppointmentResponseDTO response = createAppointmentResponse();

        when(appointmentRepository.findById(APPOINTMENT_ID)).thenReturn(Optional.of(appointment));
        when(appointmentMapper.toResponseDTO(appointment)).thenReturn(response);

        AppointmentResponseDTO result = appointmentService.findById(APPOINTMENT_ID);

        assertEquals(APPOINTMENT_ID, result.id());
        verify(appointmentRepository).findById(APPOINTMENT_ID);
    }

    @Test
    void shouldThrowExceptionWhenFindingAppointmentWithNonExistingId() {
        when(appointmentRepository.findById(APPOINTMENT_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> appointmentService.findById(APPOINTMENT_ID));

        assertEquals(APPOINTMENT_NOT_FOUND.getKey(), exception.getMessage());
        verify(appointmentRepository).findById(APPOINTMENT_ID);
    }

    @Test
    void shouldReturnAppointmentsByPatientId() {
        Appointment appointment = createAppointment();
        AppointmentResponseDTO response = createAppointmentResponse();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Appointment> page = new PageImpl<>(List.of(appointment));

        when(appointmentRepository.findByPatientId(PATIENT_ID, pageable)).thenReturn(page);
        when(appointmentMapper.toResponseDTO(appointment)).thenReturn(response);

        Page<AppointmentResponseDTO> result = appointmentService.findByPatientId(PATIENT_ID, pageable);

        assertEquals(1, result.getTotalElements());
        verify(appointmentRepository).findByPatientId(PATIENT_ID, pageable);
    }

    @Test
    void shouldReturnAppointmentsByProfessionalId() {
        Appointment appointment = createAppointment();
        AppointmentResponseDTO response = createAppointmentResponse();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Appointment> page = new PageImpl<>(List.of(appointment));

        when(appointmentRepository.findByProfessionalId(PROFESSIONAL_ID, pageable)).thenReturn(page);
        when(appointmentMapper.toResponseDTO(appointment)).thenReturn(response);

        Page<AppointmentResponseDTO> result = appointmentService.findByProfessionalId(PROFESSIONAL_ID, pageable);

        assertEquals(1, result.getTotalElements());
        verify(appointmentRepository).findByProfessionalId(PROFESSIONAL_ID, pageable);
    }

    @Test
    void shouldReturnAppointmentsByStatus() {
        Appointment appointment = createAppointment();
        AppointmentResponseDTO response = createAppointmentResponse();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Appointment> page = new PageImpl<>(List.of(appointment));

        when(appointmentRepository.findByStatus(SCHEDULED, pageable)).thenReturn(page);
        when(appointmentMapper.toResponseDTO(appointment)).thenReturn(response);

        Page<AppointmentResponseDTO> result = appointmentService.findByStatus(SCHEDULED, pageable);

        assertEquals(1, result.getTotalElements());
        verify(appointmentRepository).findByStatus(SCHEDULED, pageable);
    }

    @Test
    void shouldReturnAppointmentsByParameters() {
        Appointment appointment = createAppointment();
        AppointmentResponseDTO response = createAppointmentResponse();
        AppointmentFilterDTO filter = new AppointmentFilterDTO(
                PATIENT_NAME, null, null, null, SCHEDULED
        );
        Pageable pageable = PageRequest.of(0, 10);
        Page<Appointment> page = new PageImpl<>(List.of(appointment));

        when(appointmentRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        when(appointmentMapper.toResponseDTO(appointment)).thenReturn(response);

        Page<AppointmentResponseDTO> result = appointmentService.findByParameters(filter, pageable);

        assertEquals(1, result.getTotalElements());
        verify(appointmentRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void shouldUpdateAppointmentWithoutChangingDateAndProfessional() {
        Appointment appointment = createAppointment();
        AppointmentUpdateDTO updateDTO = new AppointmentUpdateDTO(
                null, null, "Novo Local", null, null
        );
        AppointmentResponseDTO response = createAppointmentResponse();

        when(appointmentRepository.findById(APPOINTMENT_ID)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(appointment)).thenReturn(appointment);
        when(appointmentMapper.toResponseDTO(appointment)).thenReturn(response);

        AppointmentResponseDTO result = appointmentService.update(APPOINTMENT_ID, updateDTO);

        assertNotNull(result);
        assertEquals("Novo Local", appointment.getLocation());
        verify(appointmentRepository).findById(APPOINTMENT_ID);
        verify(appointmentRepository, never()).existsByProfessionalIdAndAppointmentDateAndStatusNot(any(), any(), any());
        verify(appointmentRepository).save(appointment);
    }

    @Test
    void shouldUpdateAppointmentWithNewDateAndNoConflict() {
        Appointment appointment = createAppointment();
        LocalDateTime newDate = APPOINTMENT_DATE.plusDays(1);
        AppointmentUpdateDTO updateDTO = new AppointmentUpdateDTO(
                newDate, null, null, null, null
        );
        AppointmentResponseDTO response = createAppointmentResponse();

        when(appointmentRepository.findById(APPOINTMENT_ID)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.existsByProfessionalIdAndAppointmentDateAndStatusNot(
                PROFESSIONAL_ID, newDate, CANCELED)).thenReturn(false);
        when(appointmentRepository.save(appointment)).thenReturn(appointment);
        when(appointmentMapper.toResponseDTO(appointment)).thenReturn(response);

        AppointmentResponseDTO result = appointmentService.update(APPOINTMENT_ID, updateDTO);

        assertNotNull(result);
        verify(appointmentRepository).existsByProfessionalIdAndAppointmentDateAndStatusNot(
                PROFESSIONAL_ID, newDate, CANCELED);
        verify(appointmentRepository).save(appointment);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingAppointmentWithNewDateAndConflict() {
        Appointment appointment = createAppointment();
        LocalDateTime newDate = APPOINTMENT_DATE.plusDays(1);
        AppointmentUpdateDTO updateDTO = new AppointmentUpdateDTO(
                newDate, null, null, null, null
        );

        when(appointmentRepository.findById(APPOINTMENT_ID)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.existsByProfessionalIdAndAppointmentDateAndStatusNot(
                PROFESSIONAL_ID, newDate, CANCELED)).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> appointmentService.update(APPOINTMENT_ID, updateDTO));

        assertEquals(APPOINTMENT_CONFLICT.getKey(), exception.getMessage());
        verify(appointmentRepository).existsByProfessionalIdAndAppointmentDateAndStatusNot(
                PROFESSIONAL_ID, newDate, CANCELED);
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingAppointment() {
        AppointmentUpdateDTO updateDTO = new AppointmentUpdateDTO(
                null, null, null, null, null
        );

        when(appointmentRepository.findById(APPOINTMENT_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> appointmentService.update(APPOINTMENT_ID, updateDTO));

        assertEquals(APPOINTMENT_NOT_FOUND.getKey(), exception.getMessage());
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void shouldCancelAppointmentWhenDeleted() {
        Appointment appointment = createAppointment();

        when(appointmentRepository.findById(APPOINTMENT_ID)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(appointment)).thenReturn(appointment);

        appointmentService.delete(APPOINTMENT_ID);

        assertEquals(CANCELED, appointment.getStatus());
        verify(appointmentRepository).findById(APPOINTMENT_ID);
        verify(appointmentRepository).save(appointment);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingAppointment() {
        when(appointmentRepository.findById(APPOINTMENT_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> appointmentService.delete(APPOINTMENT_ID));

        assertEquals(APPOINTMENT_NOT_FOUND.getKey(), exception.getMessage());
        verify(appointmentRepository, never()).save(any());
    }

    // --- Helper Methods ---

    private Patient createPatient(boolean active) {
        return Patient.builder()
                .id(PATIENT_ID)
                .name(PATIENT_NAME)
                .active(active)
                .build();
    }

    private Professional createProfessional(boolean active) {
        return Professional.builder()
                .id(PROFESSIONAL_ID)
                .name(PROFESSIONAL_NAME)
                .active(active)
                .build();
    }

    private Appointment createAppointment() {
        return Appointment.builder()
                .id(APPOINTMENT_ID)
                .patient(createPatient(true))
                .professional(createProfessional(true))
                .appointmentDate(APPOINTMENT_DATE)
                .location(LOCATION)
                .status(SCHEDULED)
                .notes(NOTES)
                .createdAt(NOW)
                .updatedAt(NOW)
                .build();
    }

    private AppointmentResponseDTO createAppointmentResponse() {
        return new AppointmentResponseDTO(
                APPOINTMENT_ID, PATIENT_ID, PATIENT_NAME, PROFESSIONAL_ID, PROFESSIONAL_NAME,
                APPOINTMENT_DATE, LOCATION, SCHEDULED, NOTES, NOW
        );
    }
}