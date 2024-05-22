package com.bigdata.platform.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class TopicConfig {
    @Value("${spring.kafka.topic.name}")
    String name;
    @Bean
    public NewTopic testTopic(){
        return TopicBuilder.name(name).build();
    }

}
