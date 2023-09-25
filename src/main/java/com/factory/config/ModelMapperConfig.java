package com.factory.config;

import com.factory.openapi.model.CreateUserRequest;
import com.factory.persistence.users.entity.User;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        var mapper = new ModelMapper();
        mapper.addConverter(createUserRequestUserConverter());
        return mapper;
    }

    private static Converter<CreateUserRequest, User> createUserRequestUserConverter() {
        return new AbstractConverter<>() {
            @Override
            protected User convert(CreateUserRequest source) {
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
}
