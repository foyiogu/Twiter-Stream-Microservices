package com.francis.microservices.kafka.admin.config.config;

import com.francis.microservices.config.KafkaConfigData;
import lombok.AllArgsConstructor;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.AdminClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;

import java.util.Map;

@EnableRetry
@Configuration
@AllArgsConstructor
public class KafkaAdminConfig {
    private final KafkaConfigData kafkaConfigData;

    //listing kafka topics programatically

    @Bean
    public AdminClient adminClient(){
        return AdminClient.create(Map.of(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG,
                kafkaConfigData.getBootstrapServers()));
    }
}
