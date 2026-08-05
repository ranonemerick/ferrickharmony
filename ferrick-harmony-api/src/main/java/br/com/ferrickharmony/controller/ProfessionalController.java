package br.com.ferrickharmony.controller;

import br.com.ferrickharmony.dto.professional.ProfessionalRequestDTO;
import br.com.ferrickharmony.dto.professional.ProfessionalResponseDTO;
import br.com.ferrickharmony.dto.professional.ProfessionalUpdateDTO;
import br.com.ferrickharmony.service.ProfessionalService;
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
@RequestMapping("/professionals")
@RequiredArgsConstructor
public class ProfessionalController {

    private final ProfessionalService professionalService;

    @PostMapping("/create")
    public ResponseEntity<ProfessionalResponseDTO> save(@RequestBody @Valid ProfessionalRequestDTO professionalRequestDTO,
                                                        UriComponentsBuilder uriBuilder) {
        ProfessionalResponseDTO professionalResponse = professionalService.create(professionalRequestDTO);
        URI uri = uriBuilder.path("/professionals/{id}").buildAndExpand(professionalResponse.id()).toUri();
        return ResponseEntity.created(uri).body(professionalResponse);
    }

    @GetMapping
    public ResponseEntity<Page<ProfessionalResponseDTO>> listAll(Pageable pageable) {
        var professionals = professionalService.listAll(pageable);
        return ResponseEntity.ok().body(professionals);
    }

    @GetMapping("/active")
    public ResponseEntity<Page<ProfessionalResponseDTO>> listActiveProfessionals(Pageable pageable) {
        var professionals = professionalService.findActiveProfessionals(pageable);
        return ResponseEntity.ok().body(professionals);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfessionalResponseDTO> findById(@PathVariable UUID id) {
        var professional = professionalService.findById(id);
        return ResponseEntity.ok().body(professional);
    }

    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<ProfessionalResponseDTO> findByCpf(@PathVariable String cpf) {
        var professional = professionalService.findByCpf(cpf);
        return ResponseEntity.ok().body(professional);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ProfessionalResponseDTO> update(@PathVariable("id") UUID id,
                                                          @RequestBody @Valid ProfessionalUpdateDTO professionalUpdate) {
        var professionalResponse = professionalService.update(id, professionalUpdate);
        return ResponseEntity.ok().body(professionalResponse);
    }

}
