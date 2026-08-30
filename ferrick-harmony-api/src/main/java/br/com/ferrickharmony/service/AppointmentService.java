package br.com.ferrickharmony.service;

import br.com.ferrickharmony.dto.appointment.AppointmentFilterDTO;
import br.com.ferrickharmony.dto.appointment.AppointmentRequestDTO;
import br.com.ferrickharmony.dto.appointment.AppointmentResponseDTO;
import br.com.ferrickharmony.dto.appointment.AppointmentUpdateDTO;
import br.com.ferrickharmony.dto.email.EmailDTO;
import br.com.ferrickharmony.enums.AppointmentStatus;
import br.com.ferrickharmony.exception.BusinessException;
import br.com.ferrickharmony.mapper.AppointmentMapper;
import br.com.ferrickharmony.model.Appointment;
import br.com.ferrickharmony.model.Patient;
import br.com.ferrickharmony.model.Professional;
import br.com.ferrickharmony.producer.UserProducer;
import br.com.ferrickharmony.repository.AppointmentRepository;
import br.com.ferrickharmony.repository.PatientRepository;
import br.com.ferrickharmony.repository.ProfessionalRepository;
import br.com.ferrickharmony.specification.AppointmentSpecification;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static br.com.ferrickharmony.enums.AppointmentStatus.CANCELED;
import static br.com.ferrickharmony.enums.ErrorKey.*;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final ProfessionalRepository professionalRepository;
    private final AppointmentMapper appointmentMapper;
    private final UserProducer producer;
    private final MessageSource messageSource;

    @Transactional
    public AppointmentResponseDTO create(AppointmentRequestDTO request) {

        Patient patient = patientRepository.findById(request.patientId())
                .orElseThrow(() -> new EntityNotFoundException(PATIENT_NOT_FOUND.getKey()));

        if (!patient.isActive()) {
            throw new BusinessException(PATIENT_ALREADY_INACTIVE.getKey());
        }

        Professional professional = professionalRepository.findById(request.professionalId())
                .orElseThrow(() -> new EntityNotFoundException(PROFESSIONAL_NOT_FOUND.getKey()));

        if (!professional.isActive()) {
            throw new BusinessException(PROFESSIONAL_ALREADY_INACTIVE.getKey());
        }

        if (appointmentRepository.existsByProfessionalIdAndAppointmentDateAndStatusNot(professional.getId(),
                request.appointmentDate(), CANCELED)) {
            throw new BusinessException(APPOINTMENT_CONFLICT.getKey());
        }

        Appointment appointment = appointmentMapper.toEntity(request);
        appointment.setPatient(patient);
        appointment.setProfessional(professional);
        appointment.setStatus(AppointmentStatus.SCHEDULED);

        appointment = appointmentRepository.save(appointment);

        String subject = messageSource.getMessage(
                "email.appointment.confirmed.subject",
                null,
                LocaleContextHolder.getLocale()
        );

        sendEmailNotification(patient, subject, appointment);

        return appointmentMapper.toResponseDTO(appointment);
    }

    @Transactional(readOnly = true)
    public Page<AppointmentResponseDTO> findAll(Pageable pageable) {
        return appointmentRepository.findAll(pageable).map(appointmentMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public AppointmentResponseDTO findById(UUID id) {
        return appointmentRepository.findById(id)
                .map(appointmentMapper::toResponseDTO)
                .orElseThrow(() -> new EntityNotFoundException(APPOINTMENT_NOT_FOUND.getKey()));
    }

    @Transactional(readOnly = true)
    public Page<AppointmentResponseDTO> findByPatientId(UUID patientId, Pageable pageable) {
        return appointmentRepository.findByPatientId(patientId, pageable)
                .map(appointmentMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public Page<AppointmentResponseDTO> findByProfessionalId(UUID professionalId, Pageable pageable) {
        return appointmentRepository.findByProfessionalId(professionalId, pageable)
                .map(appointmentMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public Page<AppointmentResponseDTO> findByStatus(AppointmentStatus status, Pageable pageable) {
        return appointmentRepository.findByStatus(status, pageable)
                .map(appointmentMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public Page<AppointmentResponseDTO> findByParameters(AppointmentFilterDTO filter, Pageable pageable) {
        return appointmentRepository.findAll(AppointmentSpecification.withFilter(filter), pageable)
                .map(appointmentMapper::toResponseDTO);

    }

    @Transactional
    public AppointmentResponseDTO update(UUID id, AppointmentUpdateDTO appointmentUpdate) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(APPOINTMENT_NOT_FOUND.getKey()));

        boolean changingDate = appointmentUpdate.appointmentDate() != null &&
                !appointmentUpdate.appointmentDate().equals(appointment.getAppointmentDate());

        boolean changingProfessional = appointmentUpdate.professionalId() != null &&
                !appointmentUpdate.professionalId().equals(appointment.getProfessional().getId());

        if (changingDate || changingProfessional) {
            UUID targetProfessionalId = changingProfessional
                    ? appointmentUpdate.professionalId()
                    : appointment.getProfessional().getId();

            LocalDateTime targetDate = changingDate
                    ? appointmentUpdate.appointmentDate()
                    : appointment.getAppointmentDate();

            if (appointmentRepository.existsByProfessionalIdAndAppointmentDateAndStatusNot(
                    targetProfessionalId, targetDate, CANCELED)) {
                throw new BusinessException(APPOINTMENT_CONFLICT.getKey());
            }
        }

        updateEntityFromRequest(appointment, appointmentUpdate);

        appointment = appointmentRepository.save(appointment);

        return appointmentMapper.toResponseDTO(appointment);
    }

    private void updateEntityFromRequest(Appointment entity, AppointmentUpdateDTO appointmentUpdate) {
        if (appointmentUpdate == null || entity == null) return;

        if (appointmentUpdate.appointmentDate() != null) {
            entity.setAppointmentDate(appointmentUpdate.appointmentDate());
        }

        if (appointmentUpdate.professionalId() != null) {
            Professional professional = professionalRepository.findById(appointmentUpdate.professionalId())
                    .orElseThrow(() -> new EntityNotFoundException(PROFESSIONAL_NOT_FOUND.getKey()));
            entity.setProfessional(professional);
        }

        if (StringUtils.hasText(appointmentUpdate.location())) {
            entity.setLocation(appointmentUpdate.location());
        }

        if (StringUtils.hasText(appointmentUpdate.notes())) {
            entity.setNotes(appointmentUpdate.notes());
        }

        if (appointmentUpdate.status() != null) {
            entity.setStatus(appointmentUpdate.status());
        }
    }

    @Transactional
    public void delete(UUID id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(APPOINTMENT_NOT_FOUND.getKey()));
        appointment.setStatus(CANCELED);
        appointmentRepository.save(appointment);
    }

    private void sendEmailNotification(Patient patient, String subject, Appointment appointment) {
        Map<String, Object> variables = Map.of(
                "patientName", patient.getName(),
                "professionalName", appointment.getProfessional().getName(),
                "date", appointment.getAppointmentDate().toString(),
                "location", appointment.getLocation() != null ? appointment.getLocation() : "Ferrick Harmony"
        );

        EmailDTO emailDTO = new EmailDTO(
                patient.getId(),
                patient.getEmail(),
                subject,
                null,
                "appointment-email",
                variables
        );

        producer.publishEmailMessage(emailDTO);
    }

}
