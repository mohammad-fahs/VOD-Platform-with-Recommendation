package com.bigdata.platform.web.app.respository;

import com.bigdata.platform.web.app.model.Movie;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface MovieRepository extends MongoRepository<Movie,Long> {
}