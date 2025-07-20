package com.example.clientapp.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@ConfigurationProperties(prefix = "app.oauth2")
@Setter
@Getter
public class OAuth2Properties {
    private Client client;
    private Server server;

    @Getter
    @Setter
    public static class Client {
        private String clientId;
        private String clientSecret;
        private String redirectUri;
        private List<String> scopes;
    }

    @Setter
    @Getter
    public static class Server {
        private String authorizationUri;
        private String accessTokenUri;
    }
}
