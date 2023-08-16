package com.factory.service;

import com.factory.exception.ClientErrorException;
import com.factory.openapi.model.CreateUserRequest;
import com.factory.openapi.model.PatchUserRequest;
import com.factory.openapi.model.UserResponse;
import com.factory.persistence.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import com.factory.persistence.repository.UserRepository;
import org.modelmapper.ModelMapper;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UserService userService;

    @Test
    public void shouldCreateUser() {
        // Given
        final var request = new CreateUserRequest();
        final var entity = new User();
        when(modelMapper.map(any(), eq(com.factory.persistence.entity.User.class))).thenReturn(entity);
        when(userRepository.save(any())).thenReturn(entity);
        when(modelMapper.map(any(), eq(UserResponse.class))).thenReturn(UserResponse.builder().build());

        // When
        final var result = userService.createUser(request);

        // Then
        assertNotNull(result);
    }

    @Test(expected = ClientErrorException.class)
    public void shouldThrowUserAlreadyExistsWhenCreatingUser() {
        // Given
        final var request = new CreateUserRequest();
        final String userName = "userName";
        request.setUsername(userName);
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(new User()));

        // When
        userService.createUser(request);

        // Then - Expects an exception
    }

    @Test
    public void testGetUserById() {
        // Given
        final var userId = UUID.randomUUID();
        final var entity = new com.factory.persistence.entity.User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(entity));
        when(modelMapper.map(any(), eq(UserResponse.class))).thenReturn(UserResponse.builder().build());

        // When
        final var result = userService.getUserById(userId);

        // Then
        assertNotNull(result);
    }

    @Test(expected = ClientErrorException.class)
    public void shouldThrowUserNotFound() {
        // Given
        final var userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When
        userService.getUserById(userId);

        // Then - Expects an exception
    }

    @Test
    public void shouldUpdateUser() {
        // Given
        final var userId = UUID.randomUUID();
        final var request = new PatchUserRequest();
        final var existingEntity = new User();
        final var updatedEntity = new User();
        final String mail = "abc@gmail.com";
        final boolean isEnabled = true;
        final String password = "pass";
        final String userName = "usr";
        existingEntity.setEmail(mail);
        existingEntity.setPassword(password);
        existingEntity.setUsername(userName);
        existingEntity.setEnabled(isEnabled);
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingEntity));
        when(userRepository.save(any())).thenReturn(updatedEntity);
        when(modelMapper.map(any(), eq(UserResponse.class))).thenReturn(UserResponse.builder().build());

        // When
        final var result = userService.updateUser(userId, request);

        // Then
        assertNotNull(result);
    }

}