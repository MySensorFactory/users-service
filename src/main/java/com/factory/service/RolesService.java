package com.factory.service;

import com.factory.openapi.model.CreateRoleRequest;
import com.factory.openapi.model.RoleResponse;

import java.util.List;

public interface RolesService {
    RoleResponse createRole(CreateRoleRequest request);

    List<RoleResponse> getAllRoles();
}
