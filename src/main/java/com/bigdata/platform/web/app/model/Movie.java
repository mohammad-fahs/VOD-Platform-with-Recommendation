package com.bigdata.platform.web.app.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document
public class Movie {
    @Id
    private Long id;
    private String title;
    private Double voteAverage;
    private Integer voteCount;
    private Integer runtime;
    private Boolean adult;
    private String imdbId;
    private String originalLanguage;
    private String overview;
    private String posterPath;
    private String backdropPath;
    private List<String> genres;
    private List<String> productionCountries;
    private List<String> productionCompanies;
    private List<String> spokenLanguages;
    private List<String> keywords;
}