package com.factory.service;

import com.factory.exception.ClientErrorException;
import com.factory.openapi.model.CreateRoleRequest;
import com.factory.openapi.model.RoleResponse;
import com.factory.persistence.users.entity.Role;
import com.factory.persistence.users.repository.RoleRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private RoleService roleService;

    @Test
    public void shouldCreateRole() {
        // Given
        final var request = new CreateRoleRequest();
        final var entity = new Role();
        final var response = new RoleResponse();
        when(modelMapper.map(request, Role.class)).thenReturn(entity);
        when(roleRepository.findByName(any())).thenReturn(Optional.empty());
        when(roleRepository.save(entity)).thenReturn(entity);
        when(modelMapper.map(entity, RoleResponse.class)).thenReturn(response);

        // When
        final var result = roleService.createRole(request);

        // Then
        assertEquals(response, result);
    }

    @Test(expected = ClientErrorException.class)
    public void shouldThrowRoleAlreadyExistsWhenCreatingRole() {
        // Given
        final var request = new CreateRoleRequest();
        final String roleName = "roleName";
        request.setName(roleName);
        when(roleRepository.findByName(anyString())).thenReturn(Optional.of(new Role()));

        // When
        roleService.createRole(request);

        // Then - Expects an exception
    }

    @Test
    public void shouldGetAllRoles() {
        // Given
        final var role1 = new Role();
        final var role2 = new Role();
        final var roleResponses = List.of(new RoleResponse(), new RoleResponse());
        final List<Role> roles = new ArrayList<>();
        roles.add(role1);
        roles.add(role2);
        when(roleRepository.findAll()).thenReturn(roles);
        when(modelMapper.map(role1, RoleResponse.class)).thenReturn(roleResponses.get(0));
        when(modelMapper.map(role2, RoleResponse.class)).thenReturn(roleResponses.get(1));

        // When
        final var result = roleService.getAllRoles();

        // Then
        assertEquals(roleResponses, result);
    }
}

