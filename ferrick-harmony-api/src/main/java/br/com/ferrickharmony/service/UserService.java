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
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public UserResponseDTO create(UserRequestDTO userRequest) {
        String sanitizedEmail = normalizeEmail(userRequest.email());

        if (userRepository.existsByEmail(sanitizedEmail)) {
            throw new BusinessException("Email already exists");
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
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    @Transactional(readOnly = true)
    public UserResponseDTO findByEmail(String email) {
        return userRepository.findByEmail(normalizeEmail(email))
                .map(userMapper::toResponseDTO)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    @Transactional
    public UserResponseDTO update(UUID id, UserUpdateDTO userUpdate) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (userUpdate.email() != null && !userUpdate.email().isBlank()) {
            String sanitizedEmail = normalizeEmail(userUpdate.email());

            if (userRepository.existsByEmailAndIdNot(sanitizedEmail, id)) {
                throw new BusinessException("Email already exists");
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
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        user.setPassword(passwordUpdate.password());
        userRepository.save(user);
    }

    @Transactional
    public void deactivate(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (!user.isActive()) {
            throw new BusinessException("User is already inactive");
        }

        user.setActive(false);
        userRepository.save(user);
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

}