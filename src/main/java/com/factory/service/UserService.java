package com.factory.service;

import com.factory.exception.ClientErrorException;
import com.factory.openapi.model.CreateUserRequest;
import com.factory.openapi.model.PatchUserRequest;
import com.factory.openapi.model.Role;
import com.factory.openapi.model.UserResponse;
import com.factory.persistence.users.entity.User;
import com.factory.persistence.users.repository.RoleRepository;
import com.factory.persistence.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.factory.openapi.model.Error.CodeEnum.ALREADY_EXISTS;
import static com.factory.openapi.model.Error.CodeEnum.INVALID_INPUT;
import static com.factory.openapi.model.Error.CodeEnum.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;

    public void activateUser(final String userName) {
        var user = getUser(userName);
        user.setEnabled(true);
        userRepository.save(user);
    }

    public UserResponse createUser(final CreateUserRequest request) {
        checkIfUserExists(request);
        checkIfAllRolesExist(request.getRoles());
        var roles = getAllRolesByName(request.getRoles());
        var entity = toEntity(request);
        entity.addRoles(roles);
        var result = userRepository.save(entity);
        return toDto(result);
    }

    private Set<com.factory.persistence.users.entity.Role> getAllRolesByName(final List<Role> roles) {
        return roleRepository.findAllRolesByName(roles.stream().map(Role::getName).toList());
    }

    private void checkIfAllRolesExist(final List<Role> roles) {
        var rolesNames = roles.stream().map(Role::getName).toList();
        var areAllRolesExist = roleRepository.existsByRoleNames(rolesNames, roles.size());
        if (!areAllRolesExist) {
            throw new ClientErrorException(INVALID_INPUT, "Not every given role is registered in the system");
        }
    }

    private void checkIfUserExists(final CreateUserRequest user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new ClientErrorException(ALREADY_EXISTS, String.format("User with given name: %s already exists", user.getUsername()));
        }
    }

    private User toEntity(final CreateUserRequest user) {
        return modelMapper.map(user, User.class);
    }

    public UserResponse getUserByName(final String userName) {
        var result = getUser(userName);
        return toDto(result);
    }

    private User getUser(final String userName) {
        return userRepository.findByUsername(userName)
                .orElseThrow(() -> new ClientErrorException(NOT_FOUND, String.format("User with name %s not found", userName)));
    }

    private UserResponse toDto(final User user) {
        return modelMapper.map(user, UserResponse.class);
    }

    public UserResponse updateUser(final String userName, final PatchUserRequest request) {
        var entity = updateEntity(getUser(userName), request);
        return toDto(userRepository.save(entity));
    }

    private User updateEntity(final User existingUser, final PatchUserRequest request) {
        existingUser.setUsername(Objects.requireNonNullElse(request.getUsername(), existingUser.getUsername()));
        existingUser.setPassword(Objects.requireNonNullElse(request.getPassword(), existingUser.getPassword()));
        existingUser.setEmail(Objects.requireNonNullElse(request.getEmail(), existingUser.getEmail()));
        existingUser.setEnabled(Objects.requireNonNullElse(request.getEnabled(), existingUser.isEnabled()));
        reassignUserRoles(existingUser, request.getRoles());
        return existingUser;
    }

    private void reassignUserRoles(final User existingUser, final List<Role> roles) {
        checkIfAllRolesExist(roles);
        var roleEntities = getAllRolesByName(roles);
        existingUser.getRoles().clear();
        existingUser.addRoles(roleEntities);
    }
}

