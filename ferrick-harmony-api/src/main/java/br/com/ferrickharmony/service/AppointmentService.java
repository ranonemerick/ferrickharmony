package br.com.ferrickharmony.service;

import br.com.ferrickharmony.dto.appointment.AppointmentRequestDTO;
import br.com.ferrickharmony.dto.appointment.AppointmentResponseDTO;
import br.com.ferrickharmony.enums.AppointmentStatus;
import br.com.ferrickharmony.exception.BusinessException;
import br.com.ferrickharmony.mapper.AppointmentMapper;
import br.com.ferrickharmony.model.Appointment;
import br.com.ferrickharmony.model.Patient;
import br.com.ferrickharmony.model.Professional;
import br.com.ferrickharmony.repository.AppointmentRepository;
import br.com.ferrickharmony.repository.PatientRepository;
import br.com.ferrickharmony.repository.ProfessionalRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static br.com.ferrickharmony.enums.ErrorKey.*;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final ProfessionalRepository professionalRepository;
    private final AppointmentMapper appointmentMapper;

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

        if (appointmentRepository.existsByProfessionalIdAndAppointmentDate(professional.getId(), request.appointmentDate())) {
            throw new BusinessException(APPOINTMENT_CONFLICT.getKey());
        }

        Appointment appointment = appointmentMapper.toEntity(request);
        appointment.setPatient(patient);
        appointment.setProfessional(professional);
        appointment.setStatus(AppointmentStatus.SCHEDULED);

        appointment = appointmentRepository.save(appointment);

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

}
