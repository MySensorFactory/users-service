package com.factory.controller;

import com.factory.openapi.api.RolesApi;
import com.factory.openapi.model.CreateRoleRequest;
import com.factory.openapi.model.RoleResponse;
import com.factory.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class RoleController implements RolesApi {

    private final RoleService roleService;

    @Override
    public ResponseEntity<RoleResponse> createRole(@Valid final CreateRoleRequest request) {
        return ResponseEntity.ok(roleService.createRole(request));
    }

    @Override
    public ResponseEntity<List<RoleResponse>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }
}
