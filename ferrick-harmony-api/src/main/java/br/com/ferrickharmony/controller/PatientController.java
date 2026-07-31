package br.com.ferrickharmony.controller;

import br.com.ferrickharmony.dto.patient.PatientRequestDTO;
import br.com.ferrickharmony.dto.patient.PatientResponseDTO;
import br.com.ferrickharmony.service.PatientService;
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

    @GetMapping
    public ResponseEntity<Page<PatientResponseDTO>> listAll(Pageable pageable) {
        var patients = patientService.listAll(pageable);
        return ResponseEntity.ok().body(patients);
    }

    @GetMapping("/active")
    public ResponseEntity<Page<PatientResponseDTO>> listActivePatients(Pageable pageable) {
        var patients = patientService.findActivePatients(pageable);
        return ResponseEntity.ok().body(patients);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientResponseDTO> findById(@PathVariable UUID id) {
        var patient = patientService.findById(id);
        return ResponseEntity.ok().body(patient);
    }

    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<PatientResponseDTO> findByCpf(@PathVariable String cpf) {
        var patient = patientService.findByCpf(cpf);
        return ResponseEntity.ok().body(patient);
    }

}
