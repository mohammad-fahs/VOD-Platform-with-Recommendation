package com.bigdata.platform.web.app.controller;

import com.bigdata.platform.web.app.service.MovieService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class VODRestController {
    private final MovieService movieService;


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
        // Generate a unique ID
        String uniqueId = UUID.randomUUID().toString();

        // Set the unique ID as a response header
        response.setHeader("X-Unique-ID", uniqueId);
    }

    @PostMapping("/receive-id")
    public void receiveId(@RequestBody String uniqueId) {
        // Handle the received unique ID
        System.out.println("Received Unique ID: " + uniqueId);
    }
}
