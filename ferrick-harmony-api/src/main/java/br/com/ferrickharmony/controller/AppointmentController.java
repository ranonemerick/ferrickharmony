package br.com.ferrickharmony.controller;

import br.com.ferrickharmony.dto.appointment.AppointmentRequestDTO;
import br.com.ferrickharmony.dto.appointment.AppointmentResponseDTO;
import br.com.ferrickharmony.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping("/create")
    public ResponseEntity<AppointmentResponseDTO> create(@RequestBody AppointmentRequestDTO appointmentRequest,
                                                         UriComponentsBuilder uriBuilder) {
        AppointmentResponseDTO appointmentResponse = appointmentService.create(appointmentRequest);
        URI uri = uriBuilder.path("/appointments/{id}").buildAndExpand(appointmentResponse.id()).toUri();
        return ResponseEntity.created(uri).body(appointmentResponse);

    }

}
