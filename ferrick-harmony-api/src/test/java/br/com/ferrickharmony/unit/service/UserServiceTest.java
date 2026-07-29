package br.com.ferrickharmony.unit.service;

import br.com.ferrickharmony.dto.user.UserRequestDTO;
import br.com.ferrickharmony.dto.user.UserResponseDTO;
import br.com.ferrickharmony.enums.UserRole;
import br.com.ferrickharmony.exception.BusinessException;
import br.com.ferrickharmony.mapper.UserMapper;
import br.com.ferrickharmony.model.User;
import br.com.ferrickharmony.repository.UserRepository;
import br.com.ferrickharmony.service.UserService;
import jakarta.persistence.EntityNotFoundException;
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
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
        UserRequestDTO request = new UserRequestDTO("TEST@EMAIL.COM", PASSWORD, UserRole.ADMIN, true);

        User user = createUser();

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
    void shouldThrowExceptionWhenCreatingUserWithExistingEmail() {
        UserRequestDTO request = new UserRequestDTO(EMAIL, PASSWORD, UserRole.ADMIN, true);

        when(userRepository.existsByEmail(EMAIL)).thenReturn(true);

        assertEquals("Email already exists",
                assertThrows(BusinessException.class, () -> userService.create(request)).getMessage());

        verify(userRepository).existsByEmail(EMAIL);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldNormalizeEmailWhenCreatingUser() {
        UserRequestDTO request = new UserRequestDTO(
                "  TEST@Email.COM  ",
                PASSWORD,
                UserRole.ADMIN,
                true
        );

        User user = createUser();
        UserResponseDTO response = createUserResponse();

        when(userRepository.existsByEmail(EMAIL)).thenReturn(false);
        when(userMapper.toEntity(any())).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponseDTO(user)).thenReturn(response);

        UserResponseDTO result = userService.create(request);

        assertEquals(EMAIL, result.email());

        verify(userRepository).existsByEmail(EMAIL);
    }

    @Test
    void shouldReturnAllUsers() {
        User user = createUser();
        UserResponseDTO response = createUserResponse();

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

    @Test
    void shouldReturnEmptyPageWhenNoUsersExist() {

        Pageable pageable = PageRequest.of(0, 10);
        Page<User> page = Page.empty(pageable);

        when(userRepository.findAll(pageable)).thenReturn(page);

        Page<UserResponseDTO> result = userService.findAll(pageable);

        assertTrue(result.isEmpty());

        verify(userRepository).findAll(pageable);
        verify(userMapper, never()).toResponseDTO(any());
    }

    @Test
    void shouldReturnActiveUsersWhenActiveUsersExist() {
        User user = createUser();
        UserResponseDTO response = createUserResponse();

        Pageable pageable = PageRequest.of(0, 10);
        Page<User> page = new PageImpl<>(List.of(user));

        when(userRepository.findAllByActiveTrue(pageable)).thenReturn(page);
        when(userMapper.toResponseDTO(user)).thenReturn(response);

        Page<UserResponseDTO> result = userService.findActiveUsers(pageable);

        assertAll(
                () -> assertEquals(1, result.getTotalElements()),
                () -> assertEquals(ID, result.getContent().getFirst().id()),
                () -> assertEquals(EMAIL, result.getContent().getFirst().email()),
                () -> assertTrue(result.getContent().getFirst().active())
        );

        verify(userRepository).findAllByActiveTrue(pageable);
        verify(userMapper).toResponseDTO(user);
    }

    @Test
    void shouldReturnEmptyPageWhenNoActiveUsersExist() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> page = Page.empty(pageable);

        when(userRepository.findAllByActiveTrue(pageable)).thenReturn(page);

        Page<UserResponseDTO> result = userService.findActiveUsers(pageable);

        assertTrue(result.isEmpty());

        verify(userRepository).findAllByActiveTrue(pageable);
        verify(userMapper, never()).toResponseDTO(any(User.class));
    }


    @Test
    void shouldReturnUserWhenIdExists() {
        User user = createUser();
        UserResponseDTO response = createUserResponse();

        when(userRepository.findById(ID)).thenReturn(Optional.of(user));
        when(userMapper.toResponseDTO(user)).thenReturn(response);

        UserResponseDTO result = userService.findById(ID);

        assertAll(
                () -> assertEquals(ID, result.id()),
                () -> assertEquals(EMAIL, result.email()),
                () -> assertEquals(UserRole.ADMIN, result.role()),
                () -> assertTrue(result.active())
        );

        verify(userRepository).findById(ID);
        verify(userMapper).toResponseDTO(user);
    }

    @Test
    void shouldThrowExceptionWhenFindingUserWithNonExistingId() {
        when(userRepository.findById(ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> userService.findById(ID));

        assertEquals("User not found", exception.getMessage());

        verify(userRepository).findById(ID);
        verify(userMapper, never()).toResponseDTO(any());
    }

    private User createUser() {
        return User.builder()
                .id(ID)
                .email(EMAIL)
                .password(PASSWORD)
                .role(UserRole.ADMIN)
                .active(true)
                .createdAt(NOW)
                .updatedAt(NOW)
                .build();
    }

    private UserResponseDTO createUserResponse() {
        return new UserResponseDTO(ID, EMAIL, UserRole.ADMIN, true, NOW, NOW);
    }

}
