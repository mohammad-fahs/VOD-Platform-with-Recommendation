package com.bigdata.platform.web.app.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Getter
@Setter
@Document
@NoArgsConstructor
@AllArgsConstructor
public class Rating {
    @Id
    private Long id;
    public String userId;
    public Integer rating;
    public Long movieId;

    public String generateString(){
        return userId+", "+movieId+", "+rating;
    }

    public Rating(String input) {
        String[] parts = input.split(", ");
        if (parts.length == 3) {
            this.userId = parts[0];
            this.movieId = Long.parseLong(parts[1]);
            this.rating = Integer.parseInt(parts[2]);
        } else {
            throw new IllegalArgumentException("Input string is not in the correct format");
        }
    }
}
