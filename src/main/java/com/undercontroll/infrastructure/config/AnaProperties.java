package com.undercontroll.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "undercontroll.ana")
public class AnaProperties {

    private int suggestionCount = 4;
    private int suggestionTtlHours = 12;
    private int memoryTtlHours = 24;
    private int memoryWindow = 10;
}
