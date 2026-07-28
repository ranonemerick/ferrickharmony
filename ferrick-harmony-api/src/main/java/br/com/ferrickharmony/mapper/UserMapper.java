package br.com.ferrickharmony.mapper;

import br.com.ferrickharmony.dto.user.UserRequestDTO;
import br.com.ferrickharmony.dto.user.UserResponseDTO;
import br.com.ferrickharmony.dto.user.UserUpdateDTO;
import br.com.ferrickharmony.model.User;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class UserMapper {

    public User toEntity(UserRequestDTO dto) {
        if(dto == null) return null;

        return User.builder()
                .email(dto.email().trim().toLowerCase())
                .password(dto.password())
                .role(dto.role())
                .active(dto.active())
                .build();
    }

    public UserResponseDTO toResponseDTO(User entity) {
        if (entity == null) return null;

        return new UserResponseDTO(
                entity.getId(),
                entity.getEmail(),
                entity.getRole(),
                entity.isActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public void updateEntityFromRequest(User entity, UserUpdateDTO dto) {
        if(dto == null || entity == null) {
            return;
        }
        if(StringUtils.hasText(dto.email())) {
            entity.setEmail(dto.email());
        }

        if(dto.role() != null) {
            entity.setRole(dto.role());
        }

        entity.setActive(dto.active());
    }

}
