package com.factory.service;

import com.factory.exception.ClientErrorException;
import com.factory.openapi.model.CreateRoleRequest;
import com.factory.openapi.model.RoleResponse;
import com.factory.persistence.users.entity.Role;
import com.factory.persistence.users.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.factory.openapi.model.Error.CodeEnum.ALREADY_EXISTS;

@Service
@ConditionalOnProperty(name = "app.config.auth-source", havingValue = "default", matchIfMissing = true)
@RequiredArgsConstructor
public class DefaultRoleService implements RolesService {

    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;

    @Override
    public RoleResponse createRole(final CreateRoleRequest request) {
        checkIfRoleExists(request);
        final var entity = toEntity(request);
        final var result = roleRepository.save(entity);
        return toDto(result);
    }

    private void checkIfRoleExists(final CreateRoleRequest request) {
        if (roleRepository.findByName(request.getName()).isPresent()) {
            throw new ClientErrorException(ALREADY_EXISTS, String.format("Role with given name: %s already exists", request.getName()));
        }
    }

    private RoleResponse toDto(final Role result) {
        return modelMapper.map(result, RoleResponse.class);
    }

    private Role toEntity(final CreateRoleRequest request) {
        return modelMapper.map(request, Role.class);
    }

    @Override
    public List<RoleResponse> getAllRoles() {
        final List<Role> roles = (List<Role>) roleRepository.findAll();
        return roles.stream()
                .map(this::toDto)
                .toList();
    }
}
