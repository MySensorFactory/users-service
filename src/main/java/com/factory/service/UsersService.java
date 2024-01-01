package com.factory.service;

import com.factory.openapi.model.CreateUserRequest;
import com.factory.openapi.model.PatchUserRequest;
import com.factory.openapi.model.UserResponse;

public interface UsersService {
    void activateUser(String userName);

    UserResponse createUser(CreateUserRequest request);

    UserResponse getUserByName(String userName);

    UserResponse updateUser(String userName, PatchUserRequest request);
}
