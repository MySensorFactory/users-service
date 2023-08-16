package com.factory.service;

import com.factory.exception.ClientErrorException;
import com.factory.openapi.model.CreateUserRequest;
import com.factory.openapi.model.PatchUserRequest;
import com.factory.openapi.model.UserResponse;
import com.factory.persistence.entity.User;
import com.factory.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

import static com.factory.openapi.model.Error.CodeEnum.ALREADY_EXISTS;
import static com.factory.openapi.model.Error.CodeEnum.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public UserResponse createUser(final CreateUserRequest request) {
        checkIfUserExists(request);
        var entity = toEntity(request);
        var result = userRepository.save(entity);
        return toDto(result);
    }

    private void checkIfUserExists(final CreateUserRequest user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new ClientErrorException(ALREADY_EXISTS, String.format("User with given name: %s already exists", user.getUsername()));
        }
    }

    private com.factory.persistence.entity.User toEntity(final CreateUserRequest user) {
        return modelMapper.map(user, com.factory.persistence.entity.User.class);
    }

    public UserResponse getUserById(UUID userId) {
        var result = getUser(userId);
        return toDto(result);
    }

    private com.factory.persistence.entity.User getUser(final UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ClientErrorException(NOT_FOUND, String.format("User with id %s not found", userId)));
    }

    private UserResponse toDto(final User user) {
        return modelMapper.map(user, UserResponse.class);
    }

    public UserResponse updateUser(UUID userId, PatchUserRequest request) {
        var entity = updateEntity(getUser(userId), request);
        return toDto(userRepository.save(entity));
    }

    private User updateEntity(final User existingUser, final PatchUserRequest request) {
        existingUser.setUsername(Objects.requireNonNullElse(request.getUsername(), existingUser.getUsername()));
        existingUser.setPassword(Objects.requireNonNullElse(request.getPassword(), existingUser.getPassword()));
        existingUser.setEmail(Objects.requireNonNullElse(request.getEmail(), existingUser.getEmail()));
        existingUser.setEnabled(Objects.requireNonNullElse(request.getEnabled(), existingUser.isEnabled()));
        return existingUser;
    }
}

