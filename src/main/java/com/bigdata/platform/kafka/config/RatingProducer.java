package com.bigdata.platform.kafka.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class RatingProducer {
    private final KafkaTemplate<String,String> kafkaTemplate;


    @Value("${spring.kafka.topic.name}")
    String name;
    public CompletableFuture<SendResult<String, String>> send(String rating) {
        return kafkaTemplate.send(name, rating);
    }
}
