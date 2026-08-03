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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static br.com.ferrickharmony.enums.ErrorKey.*;
import static br.com.ferrickharmony.utils.EmailUtils.normalizeEmail;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public UserResponseDTO create(UserRequestDTO userRequest) {
        String sanitizedEmail = normalizeEmail(userRequest.email());

        if (userRepository.existsByEmail(sanitizedEmail)) {
            throw new BusinessException(EMAIL_ALREADY_EXISTS.getKey());
        }

        User user = userMapper.toEntity(userRequest);
        user.setEmail(sanitizedEmail);
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

        userMapper.updateEntityFromRequest(user, userUpdate);
        user = userRepository.save(user);

        return userMapper.toResponseDTO(user);
    }

    @Transactional
    public void updatePassword(UUID id, UserPasswordUpdateDTO passwordUpdate) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND.getKey()));

        user.setPassword(passwordUpdate.password());
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