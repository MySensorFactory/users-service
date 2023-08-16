package com.factory.controller;

import com.factory.openapi.api.UsersApi;
import com.factory.openapi.model.CreateUserRequest;
import com.factory.openapi.model.PatchUserRequest;
import com.factory.openapi.model.UserResponse;
import com.factory.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UsersController implements UsersApi {

    private final UserService userService;

    @Override
    public ResponseEntity<UserResponse> createUser(@Valid final CreateUserRequest createUserRequest) {
        return ResponseEntity.ok(userService.createUser(createUserRequest));
    }

    @Override
    public ResponseEntity<UserResponse> getUserDetails(final UUID userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @Override
    public ResponseEntity<UserResponse> updateUser(final UUID userId, @Valid final PatchUserRequest patchUserRequest) {
        return ResponseEntity.ok(userService.updateUser(userId, patchUserRequest));
    }
}
