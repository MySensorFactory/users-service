package com.factory.config;

import lombok.RequiredArgsConstructor;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@ConditionalOnProperty(name = "app.config.auth-source", havingValue = "keycloak")
@RequiredArgsConstructor
@Configuration
public class KeycloakConfig {

    @Bean
    public Keycloak keycloak(final AppConfig appConfig) {
        return KeycloakBuilder.builder()
                .serverUrl(appConfig.getKeycloakUrl())
                .realm(appConfig.getAdminRealm())
                .clientId(appConfig.getMasterClientId())
                .grantType(OAuth2Constants.PASSWORD)
                .username(appConfig.getAdminUserName())
                .scope(appConfig.getMasterScope())
                .clientSecret(appConfig.getMasterClientSecret())
                .password(appConfig.getMasterUserPassword())
                .build();
    }
}
