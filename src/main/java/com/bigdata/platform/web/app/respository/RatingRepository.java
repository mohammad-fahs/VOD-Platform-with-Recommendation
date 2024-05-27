package com.bigdata.platform.web.app.respository;

import com.bigdata.platform.web.app.model.Rating;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RatingRepository extends MongoRepository<Rating,Long> {
    boolean existsByUserId(String userId);
}
