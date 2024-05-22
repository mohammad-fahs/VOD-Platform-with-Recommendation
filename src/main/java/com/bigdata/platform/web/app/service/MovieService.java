package com.bigdata.platform.web.app.service;

import com.bigdata.platform.web.app.helper.StaticFileHelper;
import com.bigdata.platform.web.app.model.Movie;
import com.bigdata.platform.web.app.respository.MovieRepository;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class MovieService {
    private final MovieRepository movieRepository;

    public Page<Movie> getMoviesPagination(int page, int size){
        Pageable pageable =  PageRequest.of(page,size);
        return movieRepository.findAll(pageable);
    }

    public List<Movie> getMoviesList(int page, int size){
        Pageable pageable =  PageRequest.of(page,size);
        return movieRepository.findAll(pageable).get().toList();
    }

    public Movie getMovie(Long id){
        Movie movie = movieRepository.findById(id).orElse(null);
        if(Objects.nonNull(movie))
            movie.setVoteAverage(Math.round(movie.getVoteAverage() * 10.0) / 10.0);
        return movie;
    }
    public long getMoviesCount() {
        return movieRepository.count();
    }
    public void readAndSaveMoviesFromCSV() {
        List<Movie> movies = new ArrayList<>();
        List<Long> ids = new ArrayList<>();
        final int batchSize = 100;
        try (CSVReader csvReader = new CSVReader(new StringReader(StaticFileHelper.getFileContent("movies.csv")))) {
            List<String[]> records = csvReader.readAll();
            String[] headers = records.get(0); // skip the header row

            for (int i = 1; i < records.size(); i++) {
                String[] record = records.get(i);
                Movie movie = new Movie();
                movie.setId((long) Integer.parseInt(record[0]));
                movie.setTitle(record[1]);
                movie.setVoteAverage(Double.parseDouble(record[2]));
                movie.setVoteCount(Integer.parseInt(record[3]));
                movie.setRuntime(Integer.parseInt(record[4]));
                movie.setAdult(Boolean.parseBoolean(record[5]));
                movie.setBackdropPath("https://image.tmdb.org/t/p/original"+record[6]);
                movie.setImdbId(record[7]);
                movie.setOriginalLanguage(record[8]);
                movie.setOverview(record[9]);
                movie.setPosterPath("https://image.tmdb.org/t/p/original"+record[10]);
                movie.setGenres(Arrays.asList(record[11].split(", ")));
                movie.setProductionCompanies(Arrays.asList(record[12].split(", ")));
                movie.setProductionCountries(Arrays.asList(record[13].split(", ")));
                movie.setSpokenLanguages(Arrays.asList(record[14].split(", ")));
                movie.setKeywords(Arrays.asList(record[15].split(", ")));

                movies.add(movie);

                ids.add(movie.getId());
                if (movies.size() == batchSize) {
                    System.out.println("saving to the DB: "+ids);
                    movieRepository.saveAll(movies);
                    movies.clear();
                    ids.clear();
                }

            }

        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }
    }
}