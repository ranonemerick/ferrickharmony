package br.com.ferrickharmony.controller;

import br.com.ferrickharmony.dto.professional.ProfessionalRequestDTO;
import br.com.ferrickharmony.dto.professional.ProfessionalResponseDTO;
import br.com.ferrickharmony.service.ProfessionalService;
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

}
