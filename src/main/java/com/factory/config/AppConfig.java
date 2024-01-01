package com.factory.config;

import lombok.Data;

@Data
public class AppConfig {
    private String authSource;
    private String keycloakUrl;
    private String adminRealm;
    private String masterClientId;
    private String adminUserName;
    private String masterScope;
    private String masterClientSecret;
    private String masterUserPassword;
    private String usersRealm;
}

