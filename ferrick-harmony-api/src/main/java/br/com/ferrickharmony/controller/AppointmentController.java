package br.com.ferrickharmony.controller;

import br.com.ferrickharmony.dto.appointment.AppointmentFilterDTO;
import br.com.ferrickharmony.dto.appointment.AppointmentRequestDTO;
import br.com.ferrickharmony.dto.appointment.AppointmentResponseDTO;
import br.com.ferrickharmony.dto.appointment.AppointmentUpdateDTO;
import br.com.ferrickharmony.enums.AppointmentStatus;
import br.com.ferrickharmony.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping("/create")
    public ResponseEntity<AppointmentResponseDTO> create(@Valid @RequestBody AppointmentRequestDTO appointmentRequest,
                                                         UriComponentsBuilder uriBuilder) {
        AppointmentResponseDTO appointmentResponse = appointmentService.create(appointmentRequest);
        URI uri = uriBuilder.path("/appointments/{id}").buildAndExpand(appointmentResponse.id()).toUri();
        return ResponseEntity.created(uri).body(appointmentResponse);
    }

    @GetMapping
    public ResponseEntity<Page<AppointmentResponseDTO>> findAll(Pageable pageable) {
        Page<AppointmentResponseDTO> appointments = appointmentService.findAll(pageable);
        return ResponseEntity.ok().body(appointments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponseDTO> findById(@PathVariable UUID id) {
        AppointmentResponseDTO appointment = appointmentService.findById(id);
        return ResponseEntity.ok().body(appointment);
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<Page<AppointmentResponseDTO>> findByPatientId(@PathVariable UUID patientId, Pageable pageable) {
        Page<AppointmentResponseDTO> appointments = appointmentService.findByPatientId(patientId, pageable);
        return ResponseEntity.ok().body(appointments);
    }

    @GetMapping("/professional/{professionalId}")
    public ResponseEntity<Page<AppointmentResponseDTO>> findByProfessionalId(@PathVariable UUID professionalId, Pageable pageable) {
        Page<AppointmentResponseDTO> appointments = appointmentService.findByProfessionalId(professionalId, pageable);
        return ResponseEntity.ok().body(appointments);
    }

    @GetMapping("/status")
    public ResponseEntity<Page<AppointmentResponseDTO>> findByStatus(@RequestParam AppointmentStatus status, Pageable pageable) {
        Page<AppointmentResponseDTO> appointments = appointmentService.findByStatus(status, pageable);
        return ResponseEntity.ok().body(appointments);
    }

    @GetMapping("/parameters")
    public ResponseEntity<Page<AppointmentResponseDTO>> findByParameters(AppointmentFilterDTO filter, Pageable pageable) {
        Page<AppointmentResponseDTO> appointments = appointmentService.findByParameters(filter, pageable);
        return ResponseEntity.ok().body(appointments);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppointmentResponseDTO> update(@PathVariable UUID id,
                                                         @Valid @RequestBody AppointmentUpdateDTO appointmentUpdate) {
        AppointmentResponseDTO appointmentResponse = appointmentService.update(id, appointmentUpdate);
        return ResponseEntity.ok().body(appointmentResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        appointmentService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
