package com.bigdata.platform.kafka.config;

import com.bigdata.platform.web.app.model.Rating;
import com.bigdata.platform.web.app.respository.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class RatingConsumer {
    @KafkaListener(topics = "rating", groupId = "ratings-group")
    public void consume(String message) throws IOException {
        try (FileWriter writer = new FileWriter("ratings.csv", true)) {
            writer.append(message).append("\n");
        } catch (IOException e) {
            System.out.println("error writing file");
            e.printStackTrace();
        }
        System.out.println(message);
    }

}
