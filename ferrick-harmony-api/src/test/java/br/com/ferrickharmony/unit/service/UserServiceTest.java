package br.com.ferrickharmony.unit.service;

import br.com.ferrickharmony.dto.user.UserRequestDTO;
import br.com.ferrickharmony.dto.user.UserResponseDTO;
import br.com.ferrickharmony.enums.UserRole;
import br.com.ferrickharmony.mapper.UserMapper;
import br.com.ferrickharmony.model.User;
import br.com.ferrickharmony.repository.UserRepository;
import br.com.ferrickharmony.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    
    private final String EMAIL = "test@email.com";
    private final UUID ID = UUID.randomUUID();
    private final LocalDateTime NOW = LocalDateTime.now();
    private final String PASSWORD = "abc123";

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldCreateUserWhenEmailIsAvailable() {
        UserRequestDTO request = new UserRequestDTO("TEST@EMAIL.COM", "abc123", UserRole.ADMIN, true);

        User user = User
                .builder()
                .email(EMAIL)
                .password(PASSWORD)
                .role(UserRole.ADMIN)
                .active(true)
                .build();

        UserResponseDTO response = new UserResponseDTO(ID, EMAIL, UserRole.ADMIN, true, NOW, NOW);

        when(userRepository.existsByEmail(EMAIL)).thenReturn(false);
        when(userMapper.toEntity(request)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponseDTO(user)).thenReturn(response);

        UserResponseDTO result = userService.create(request);

        assertNotNull(result);
        assertEquals(ID, result.id());
        assertEquals(EMAIL, result.email());
        assertEquals(UserRole.ADMIN, result.role());
        assertTrue(result.active());

        verify(userRepository).existsByEmail(EMAIL);
        verify(userMapper).toEntity(request);
        verify(userRepository).save(user);
        verify(userMapper).toResponseDTO(user);
    }

    @Test
    void shouldReturnAllUsers() {
        User user = User
                .builder()
                .email(EMAIL)
                .password(PASSWORD)
                .role(UserRole.ADMIN)
                .active(true)
                .build();

        UserResponseDTO response = new UserResponseDTO(ID, EMAIL, UserRole.ADMIN, true, NOW, NOW);

        Pageable pageable = PageRequest.of(0, 10);
        Page<User> page = new PageImpl<>(List.of(user));

        when(userRepository.findAll(pageable)).thenReturn(page);
        when(userMapper.toResponseDTO(user)).thenReturn(response);

        Page<UserResponseDTO> result = userService.findAll(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(EMAIL, result.getContent().getFirst().email());
        assertEquals(UserRole.ADMIN, result.getContent().getFirst().role());

        verify(userRepository).findAll(pageable);
        verify(userMapper).toResponseDTO(user);
    }

}
