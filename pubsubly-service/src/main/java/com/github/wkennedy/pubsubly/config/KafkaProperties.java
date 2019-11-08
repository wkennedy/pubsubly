package com.github.wkennedy.pubsubly.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.kafka")
public class KafkaProperties extends org.springframework.boot.autoconfigure.kafka.KafkaProperties {
}
