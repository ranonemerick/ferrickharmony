package br.com.ferrickharmony.mapper;

import br.com.ferrickharmony.dto.user.UserRequestDTO;
import br.com.ferrickharmony.dto.user.UserResponseDTO;
import br.com.ferrickharmony.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "email", expression = "java(dto.email() != null ? dto.email().trim().toLowerCase() : null)")
    User toEntity(UserRequestDTO dto);

    UserResponseDTO toResponseDTO(User entity);

}


