package com.factory.controller;

import com.factory.openapi.api.UsersApi;
import com.factory.openapi.model.CreateUserRequest;
import com.factory.openapi.model.PatchUserRequest;
import com.factory.openapi.model.UserResponse;
import com.factory.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class UsersController implements UsersApi {

    private final UserService userService;

    @Override
    public ResponseEntity<Void> activateUser(final String userName) {
        userService.activateUser(userName);
        return null;
    }

    @Override
    public ResponseEntity<UserResponse> createUser(@Valid final CreateUserRequest createUserRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(createUserRequest));
    }

    @Override
    public ResponseEntity<UserResponse> getUserDetails(final String userName) {
        return ResponseEntity.ok(userService.getUserByName(userName));
    }

    @Override
    public ResponseEntity<UserResponse> updateUser(final String userName, @Valid final PatchUserRequest patchUserRequest) {
        return ResponseEntity.ok(userService.updateUser(userName, patchUserRequest));
    }
}
