package com.factory.config;

import com.factory.openapi.model.*;
import com.factory.persistence.users.entity.User;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import com.factory.openapi.model.RoleResponse;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        var mapper = new ModelMapper();
        mapper.addConverter(createUserRequestUserConverter());
        mapper.addConverter(createUserRequestUserRepresentationConverter());
        mapper.addConverter(createUserRepresentationUserResponseConverter());
        mapper.addConverter(createRoleRepresentationRoleResponseConverter());
        mapper.addConverter(createRoleRequestRoleRepresentationConverter());
        return mapper;
    }

    private static Converter<CreateUserRequest, User> createUserRequestUserConverter() {
        return new AbstractConverter<>() {
            @Override
            protected User convert(final CreateUserRequest source) {
                if (Objects.isNull(source)) {
                    return null;
                }
                var result = new User();
                result.setEnabled(false);
                result.setPassword(source.getPassword());
                result.setEmail(source.getEmail());
                result.setUsername(source.getUsername());
                return result;
            }
        };
    }

    private static Converter<CreateUserRequest, UserRepresentation> createUserRequestUserRepresentationConverter() {
        return new AbstractConverter<>() {
            @Override
            protected UserRepresentation convert(final CreateUserRequest source) {
                var result = new UserRepresentation();
                result.setEnabled(source.getEnabled());
                result.setEmail(source.getEmail());
                result.setUsername(source.getUsername());
                var credentials = new CredentialRepresentation();
                credentials.setTemporary(false);
                credentials.setValue(source.getPassword());
                credentials.setType("password");
                result.setCredentials(List.of(credentials));
                result.setRealmRoles(source.getRoles().stream().map(Role::getName).toList());
                return result;
            }
        };
    }

    private static Converter<UserRepresentation , UserResponse> createUserRepresentationUserResponseConverter() {
        return new AbstractConverter<>() {
            @Override
            protected UserResponse convert(final UserRepresentation source) {
                return UserResponse.builder()
                        .enabled(source.isEnabled())
                        .email(source.getEmail())
                        .username(source.getUsername())
                        .id(UUID.fromString(source.getId()))
                        .build();
            }
        };
    }

    private static Converter<RoleRepresentation , RoleResponse> createRoleRepresentationRoleResponseConverter() {
        return new AbstractConverter<>() {
            @Override
            protected RoleResponse convert(final RoleRepresentation source) {
                return RoleResponse.builder()
                        .id(UUID.fromString(source.getId()))
                        .name(source.getName())
                        .build();
            }
        };
    }

    private static Converter<CreateRoleRequest , RoleRepresentation> createRoleRequestRoleRepresentationConverter() {
        return new AbstractConverter<>() {
            @Override
            protected RoleRepresentation convert(final CreateRoleRequest source) {
                var role = new RoleRepresentation();
                role.setName(source.getName());
                return role;
            }
        };
    }


}
