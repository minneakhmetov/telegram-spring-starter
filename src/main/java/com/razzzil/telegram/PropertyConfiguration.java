package com.razzzil.telegram;

import com.razzzil.telegram.configdto.BotTokenDto;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "razzzil")
public class PropertyConfiguration {

    private Map<String, BotTokenDto> bots;

    private boolean singletonBot = false;

    private List<String> basePackages = Collections.singletonList("*");


}
