package com.factory.service;

import com.factory.exception.ClientErrorException;
import com.factory.openapi.model.CreateUserRequest;
import com.factory.openapi.model.PatchUserRequest;
import com.factory.openapi.model.UserResponse;
import com.factory.persistence.users.entity.User;
import com.factory.persistence.users.repository.RoleRepository;
import com.factory.persistence.users.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UserService userService;

    @Test
    public void shouldCreateUser() {
        // Given
        final var request = new CreateUserRequest();
        request.setRoles(List.of());
        final var entity = new User();
        when(modelMapper.map(any(), eq(com.factory.persistence.users.entity.User.class))).thenReturn(entity);
        when(userRepository.save(any())).thenReturn(entity);
        when(modelMapper.map(any(), eq(UserResponse.class))).thenReturn(UserResponse.builder().build());
        when(roleRepository.findAllRolesByName(any())).thenReturn(Set.of());
        when(roleRepository.existsByRoleNames(any(), anyLong())).thenReturn(true);

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
    public void testGetUserByName() {
        // Given
        final var userName = UUID.randomUUID().toString();
        final var entity = new com.factory.persistence.users.entity.User();
        when(userRepository.findByUsername(userName)).thenReturn(Optional.of(entity));
        when(modelMapper.map(any(), eq(UserResponse.class))).thenReturn(UserResponse.builder().build());

        // When
        final var result = userService.getUserByName(userName);

        // Then
        assertNotNull(result);
    }

    @Test(expected = ClientErrorException.class)
    public void shouldThrowUserNotFound() {
        // Given
        final var name = UUID.randomUUID().toString();
        when(userRepository.findByUsername(name)).thenReturn(Optional.empty());

        // When
        userService.getUserByName(name);

        // Then - Expects an exception
    }

    @Test
    public void shouldUpdateUser() {
        // Given
        final var userName = UUID.randomUUID().toString();
        final var request = new PatchUserRequest();
        request.setRoles(List.of());
        final var existingEntity = new User();
        final var updatedEntity = new User();
        updatedEntity.setRoles(new HashSet<>());
        final String mail = "abc@gmail.com";
        final boolean isEnabled = true;
        final String password = "pass";
        existingEntity.setEmail(mail);
        existingEntity.setPassword(password);
        existingEntity.setUsername(userName);
        existingEntity.setEnabled(isEnabled);
        existingEntity.setRoles(new HashSet<>());
        when(userRepository.findByUsername(userName)).thenReturn(Optional.of(existingEntity));
        when(userRepository.save(any())).thenReturn(updatedEntity);
        when(modelMapper.map(any(), eq(UserResponse.class))).thenReturn(UserResponse.builder().build());
        when(roleRepository.existsByRoleNames(any(), anyLong())).thenReturn(true);

        // When
        final var result = userService.updateUser(userName, request);

        // Then
        assertNotNull(result);
    }

}