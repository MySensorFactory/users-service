package com.factory.service;

import com.factory.openapi.model.CreateRoleRequest;
import com.factory.openapi.model.RoleResponse;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.modelmapper.ModelMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@ConditionalOnProperty(name = "app.config.auth-source", havingValue = "keycloak")
@RequiredArgsConstructor
public class KeycloakRolesService implements RolesService {
    private final Keycloak keycloak;
    private final ModelMapper modelMapper;

    @Override
    public RoleResponse createRole(final CreateRoleRequest request) {
        getRealmResource().roles().create(modelMapper.map(request, RoleRepresentation.class));
        var createdRole = getRealmResource().roles().list().stream().toList().stream()
                .filter(r -> r.getName().equals(request.getName()))
                .findFirst()
                .get();
        return modelMapper.map(createdRole, RoleResponse.class);
    }

    @Override
    public List<RoleResponse> getAllRoles() {
        return getRealmResource().roles().list().stream()
                .map(role -> modelMapper.map(role, RoleResponse.class))
                .toList();
    }

    private RealmResource getRealmResource() {
        return keycloak.realms().realm("SpringBootKeycloak");
    }
}
