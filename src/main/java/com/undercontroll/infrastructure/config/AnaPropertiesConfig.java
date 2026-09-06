package com.undercontroll.infrastructure.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(AnaProperties.class)
public class AnaPropertiesConfig {
}
