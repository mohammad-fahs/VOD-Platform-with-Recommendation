package com.bigdata.platform.web.app.controller;

import com.bigdata.platform.web.app.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequiredArgsConstructor
public class MoviesUIController {
    private final MovieService movieService;

    @GetMapping(value = "/movies")
    public String index(Model model){
        model.addAttribute("count", movieService.getMoviesCount());
        return "index";
    }

    @GetMapping(value = "/movie")
    public String movieDisplay(@RequestParam(value = "id")Long id, Model model){
        model.addAttribute("movie", movieService.getMovie(id));
        return "movie";
    }
}
