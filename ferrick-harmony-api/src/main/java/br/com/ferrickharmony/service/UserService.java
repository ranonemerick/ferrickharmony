package br.com.ferrickharmony.service;

import br.com.ferrickharmony.dto.user.UserPasswordUpdateDTO;
import br.com.ferrickharmony.dto.user.UserRequestDTO;
import br.com.ferrickharmony.dto.user.UserResponseDTO;
import br.com.ferrickharmony.dto.user.UserUpdateDTO;
import br.com.ferrickharmony.exception.BusinessException;
import br.com.ferrickharmony.mapper.UserMapper;
import br.com.ferrickharmony.model.User;
import br.com.ferrickharmony.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.UUID;

import static br.com.ferrickharmony.enums.ErrorKey.*;
import static br.com.ferrickharmony.utils.EmailUtils.normalizeEmail;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponseDTO create(UserRequestDTO userRequest) {
        String sanitizedEmail = normalizeEmail(userRequest.email());

        if (userRepository.existsByEmail(sanitizedEmail)) {
            throw new BusinessException(EMAIL_ALREADY_EXISTS.getKey());
        }

        User user = userMapper.toEntity(userRequest);
        user.setEmail(sanitizedEmail);
        user.setPassword(passwordEncoder.encode(userRequest.password()));
        user = userRepository.save(user);

        return userMapper.toResponseDTO(user);
    }

    @Transactional(readOnly = true)
    public Page<UserResponseDTO> findAll(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public Page<UserResponseDTO> findActiveUsers(Pageable pageable) {
        return userRepository.findAllByActiveTrue(pageable)
                .map(userMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public UserResponseDTO findById(UUID id) {
        return userRepository.findById(id)
                .map(userMapper::toResponseDTO)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND.getKey()));
    }

    @Transactional(readOnly = true)
    public UserResponseDTO findByEmail(String email) {
        return userRepository.findByEmail(normalizeEmail(email))
                .map(userMapper::toResponseDTO)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND.getKey()));
    }

    @Transactional
    public UserResponseDTO update(UUID id, UserUpdateDTO userUpdate) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND.getKey()));

        if (userUpdate.email() != null && !userUpdate.email().isBlank()) {
            String sanitizedEmail = normalizeEmail(userUpdate.email());

            if (userRepository.existsByEmailAndIdNot(sanitizedEmail, id)) {
                throw new BusinessException(EMAIL_ALREADY_EXISTS.getKey());
            }
            user.setEmail(sanitizedEmail);
        }

        updateEntityFromRequest(user, userUpdate);
        user = userRepository.save(user);

        return userMapper.toResponseDTO(user);
    }

    private void updateEntityFromRequest(User entity, UserUpdateDTO dto) {
        if(dto == null || entity == null) return;

        if(StringUtils.hasText(dto.email())) {
            entity.setEmail(dto.email());
        }

        if(dto.role() != null) {
            entity.setRole(dto.role());
        }
        entity.setActive(dto.active());
    }

    @Transactional
    public void updatePassword(UUID id, UserPasswordUpdateDTO passwordUpdate) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND.getKey()));

        String passwordEncode = passwordEncoder.encode(passwordUpdate.password());
        user.setPassword(passwordEncode);
        userRepository.save(user);
    }

    @Transactional
    public void deactivate(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND.getKey()));

        if (!user.isActive()) {
            throw new BusinessException(USER_ALREADY_INACTIVE.getKey());
        }

        user.setActive(false);
        userRepository.save(user);
    }

}