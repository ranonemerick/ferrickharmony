package br.com.ferrickharmony.service;

import br.com.ferrickharmony.dto.user.UserRequestDTO;
import br.com.ferrickharmony.dto.user.UserResponseDTO;
import br.com.ferrickharmony.model.User;
import br.com.ferrickharmony.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public UserResponseDTO create(UserRequestDTO userRequest) {
        if(userRepository.existsByEmail(userRequest.email())) {
            throw new IllegalArgumentException("Já existe um usuário cadastrado com esse email");
        }

        User user = User
                .builder()
                .email(userRequest.email().toLowerCase().trim())
                .password(userRequest.password())
                .role(userRequest.role())
                .active(userRequest.active())
                .build();

        userRepository.save(user);

        return mapToUserResponseDTO(user);
    }

    private UserResponseDTO mapToUserResponseDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                user.isActive(),
                user.getCreatedAt()
        );
    }

}
