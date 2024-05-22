package com.bigdata.platform.web.app.controller;

import com.bigdata.platform.kafka.config.RatingProducer;
import com.bigdata.platform.web.app.model.Rating;
import com.bigdata.platform.web.app.service.MovieService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class VODRestController {
    private final MovieService movieService;
    private final RatingProducer ratingProducer;


    @GetMapping(value = "/get-movies")
    public ResponseEntity<?> getMovies(@RequestParam(value = "page") Integer page, @RequestParam(value = "size") Integer size){
        try{
            return ResponseEntity.ok(movieService.getMoviesList(page,size));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping(value = "/index-all")
    public ResponseEntity<?> indexMovies(){
        try{
            movieService.readAndSaveMoviesFromCSV();
            return ResponseEntity.ok("Indexed Successfully !!");
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/generate-unique-id")
    public void generateUniqueId(HttpServletResponse response) {
        String uniqueId = UUID.randomUUID().toString();
        response.setHeader("X-Unique-ID", uniqueId);
    }

    @PostMapping("/receive-id")
    public ResponseEntity<?> receiveId(@RequestBody Rating rating) {
        try{
            return ResponseEntity.ok(ratingProducer.send(rating.generateString()));
        }catch (Exception e){
            System.out.println("failed to publish rating!");
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
