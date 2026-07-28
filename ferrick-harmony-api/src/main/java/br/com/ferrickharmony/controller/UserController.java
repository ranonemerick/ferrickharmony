package br.com.ferrickharmony.controller;

import br.com.ferrickharmony.dto.user.UserPasswordUpdateDTO;
import br.com.ferrickharmony.dto.user.UserRequestDTO;
import br.com.ferrickharmony.dto.user.UserResponseDTO;
import br.com.ferrickharmony.dto.user.UserUpdateDTO;
import br.com.ferrickharmony.model.User;
import br.com.ferrickharmony.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/create")
    public ResponseEntity<UserResponseDTO> create(@RequestBody @Valid UserRequestDTO userRequestDTO,
                                                  UriComponentsBuilder uriBuilder) {
        UserResponseDTO userResponseDTO = userService.create(userRequestDTO);
        URI uri = uriBuilder.path("/users/{id}").buildAndExpand(userResponseDTO.id()).toUri();
        return ResponseEntity.created(uri).body(userResponseDTO);
    }

    @GetMapping
    public ResponseEntity<Page<UserResponseDTO>> findAll(@PageableDefault Pageable pageable) {
        var users = userService.findAll(pageable);
        return ResponseEntity.ok().body(users);
    }

    @GetMapping("/active")
    public ResponseEntity<Page<UserResponseDTO>> findAllByActive(@PageableDefault Pageable pageable) {
        var users = userService.findActiveUsers(pageable);
        return ResponseEntity.ok().body(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> findById(@PathVariable("id") UUID id) {
        var user = userService.findById(id);
        return ResponseEntity.ok().body(user);
    }

    @GetMapping("/email")
    public ResponseEntity<UserResponseDTO> findByEmail(@RequestParam("email") String email) {
        var user = userService.findByEmail(email);
        return ResponseEntity.ok().body(user);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<UserResponseDTO> update(@PathVariable("id") UUID id,
                                                  @RequestBody @Valid UserUpdateDTO UserUpdateDTO) {
        var user = userService.update(id, UserUpdateDTO);
        return ResponseEntity.ok().body(user);
    }

    @PutMapping("/update/password/{id}")
    public ResponseEntity<UserResponseDTO> updatePassword(@PathVariable("id") UUID id,
                                                          @RequestBody @Valid UserPasswordUpdateDTO passwordUpdate) {
        userService.updatePassword(id, passwordUpdate);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/deactivate/{id}")
    public ResponseEntity<UserResponseDTO> deactivate(@PathVariable("id") UUID id) {
        userService.deactivate(id);
        return ResponseEntity.noContent().build();
    }


}
