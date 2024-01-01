package com.factory.service;

import com.factory.config.AppConfig;
import com.factory.exception.ClientErrorException;
import com.factory.openapi.model.CreateUserRequest;
import com.factory.openapi.model.PatchUserRequest;
import com.factory.openapi.model.Role;
import com.factory.openapi.model.UserResponse;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.modelmapper.ModelMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static com.factory.openapi.model.Error.CodeEnum.*;

@Service
@ConditionalOnProperty(name = "app.config.auth-source", havingValue = "keycloak")
@RequiredArgsConstructor
public class KeycloakUsersService implements UsersService {

    public static final String CREDENTIAL_TYPE = "password";
    private final ModelMapper modelMapper;
    private final Keycloak keycloak;
    private final AppConfig appConfig;

    @Override
    public void activateUser(final String userName) {
        var user = getUserResource(userName);
        var userRepresentation = user.toRepresentation();
        userRepresentation.setEnabled(true);
        user.update(userRepresentation);
    }

    private UserResource getUserResource(final String userName) {
        var realm = getRealmResource();
        var user = realm.users()
                .search(userName,0,1, false)
                .stream().findFirst()
                .orElseThrow(() -> new ClientErrorException(NOT_FOUND, String.format("User with name %s not found", userName)));
        return realm.users().get(user.getId());
    }

    private RealmResource getRealmResource() {
        return keycloak.realms().realm(appConfig.getUsersRealm());
    }

    @Override
    public UserResponse createUser(final CreateUserRequest request) {
        checkIfUserExists(request);
        checkIfAllRolesExist(request.getRoles());
        getRealmResource().users().create(modelMapper.map(request, UserRepresentation.class));
        var user = getUserResource(request.getUsername()).toRepresentation();
        return toDto(user);
    }

    private void checkIfAllRolesExist(final List<Role> roles) {
        var rolesNames = getRealmResource().roles().list().stream().map(RoleRepresentation::getName).toList();
        var areAllRolesExist = rolesNames.containsAll(roles.stream().map(Role::getName).toList());
        if (!areAllRolesExist) {
            throw new ClientErrorException(INVALID_INPUT, "Not every given role is registered in the system");
        }
    }

    private void checkIfUserExists(final CreateUserRequest user) {
        if (!getRealmResource()
                .users()
                .searchByUsername(user.getUsername(), true)
                .isEmpty()) {
            throw new ClientErrorException(ALREADY_EXISTS, String.format("User with given name: %s already exists", user.getUsername()));
        }
    }

    @Override
    public UserResponse getUserByName(final String userName) {
        var user = getUserResource(userName).toRepresentation();
        var result = toDto(user);
        result.setRoles(getUserResource(userName).roles().realmLevel().listAll()
                .stream().map(r -> Role.builder().name(r.getName()).build())
                .toList());
        return result;
    }

    private UserResponse toDto(final UserRepresentation user) {
        return modelMapper.map(user, UserResponse.class);
    }

    @Override
    public UserResponse updateUser(final String userName, final PatchUserRequest request) {
        var user = getUserResource(userName);
        var userRepresentation = updateRepresentation(user, request);
        changePassword(user, request);
        reassignUserRoles(user, request.getRoles());
        return toDto(userRepresentation);
    }

    private UserRepresentation updateRepresentation(final UserResource existingUser, final PatchUserRequest request) {
        var representation = existingUser.toRepresentation();
        representation.setUsername(Objects.requireNonNullElse(request.getUsername(), representation.getUsername()));
        representation.setEmail(Objects.requireNonNullElse(request.getEmail(), representation.getEmail()));
        representation.setEnabled(Objects.requireNonNullElse(request.getEnabled(), representation.isEnabled()));
        existingUser.update(representation);
        return representation;
    }

    private static void changePassword(final UserResource existingUser, final PatchUserRequest request) {
        if (Objects.nonNull(request.getPassword())) {
            var credentials = new CredentialRepresentation();
            credentials.setType(CREDENTIAL_TYPE);
            credentials.setValue(request.getPassword());
            credentials.setTemporary(false);
            existingUser.resetPassword(credentials);
        }
    }

    private void reassignUserRoles(final UserResource existingUser, final List<Role> roles) {
        var currentRoles = existingUser.roles().realmLevel().listAll();
        existingUser.roles().realmLevel().remove(currentRoles);

        var newRoles = getRealmResource().roles().list()
                .stream().filter(r -> roles.contains(Role.builder().name(r.getName()).build()))
                .toList();

        existingUser.roles().realmLevel().add(newRoles);
    }
}

