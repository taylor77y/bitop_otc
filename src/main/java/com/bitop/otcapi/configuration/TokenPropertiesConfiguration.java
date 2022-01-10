package com.bitop.otcapi.configuration;

import com.bitop.otcapi.security.TokenProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TokenPropertiesConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "token")
    public TokenProperties tokenProperties() {
        return new TokenProperties();
    }
}