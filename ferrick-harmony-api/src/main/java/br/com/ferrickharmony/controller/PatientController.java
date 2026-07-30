package br.com.ferrickharmony.controller;

import br.com.ferrickharmony.dto.patient.PatientRequestDTO;
import br.com.ferrickharmony.dto.patient.PatientResponseDTO;
import br.com.ferrickharmony.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @PostMapping("/create")
    public ResponseEntity<PatientResponseDTO> save(@RequestBody @Valid PatientRequestDTO patientRequest,
                                                   UriComponentsBuilder uriBuilder) {
        PatientResponseDTO patientResponse =  patientService.create(patientRequest);
        URI uri = uriBuilder.path("/patients/{id}").buildAndExpand(patientRequest.cpf()).toUri();
        return ResponseEntity.created(uri).body(patientResponse);
    }

}
