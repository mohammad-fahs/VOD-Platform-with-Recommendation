# VOD Recommendation Bigdata

### Movies List
![Untitled](https://github.com/mohammad-fahs/VOD-Platform-with-Recommendation/assets/109221274/39f95f08-e5ac-4a34-9d6f-998a003405a5)


### Rating Service
![Untitled 1](https://github.com/mohammad-fahs/VOD-Platform-with-Recommendation/assets/109221274/daf546c5-baca-4094-855c-901ff377cdb3)



### Recommendations
![Untitled 2](https://github.com/mohammad-fahs/VOD-Platform-with-Recommendation/assets/109221274/a6c524a4-b48a-482a-b231-4dd13e1312fe)



# **Movie Recommendation System Using Spring Boot, Thymeleaf, Kafka, MongoDB, and Spark**

## **Overview**

This project aims to build a simple movie recommendation system leveraging various technologies such as Spring Boot, Thymeleaf, Apache Kafka, MongoDB, and Apache Spark. Users can rate movies on a Video On Demand (VOD) portal, and based on these ratings, a recommendation system suggests movies to users. The system consists of several components: a VOD portal for user interaction, a ratings service for handling ratings, Kafka for messaging, MongoDB for movie data storage, and Spark for machine learning.

## **System Architecture**

1. **VOD Portal**: A web application built with Spring Boot and Thymeleaf for user interaction.
2. **Ratings Service**: A microservice that receives movie ratings and sends them to Kafka.
3. **Kafka**: A messaging system that handles the streaming of ratings data.
4. **HDFS/File System**: Storage for ratings data in CSV format.
5. **MongoDB**: NoSQL database to store movie metadata.
6. **Spark**: A framework used for processing and machine learning to create the recommendation model.
7. **Docker**: Containerization platform to host Kafka, MongoDB, and Spark.

## **Detailed Description**

### **VOD Portal**

The VOD portal allows users to:

- View a list of available movies.
- Rate movies they have watched.
- Receive recommendations for other movies.

**Technologies**:

- **Spring Boot**: Backend framework.
- **Thymeleaf**: Template engine for rendering views.
- **HTML/CSS/JavaScript**: Frontend technologies.

**Endpoints**:

- **`/movies`**: Displays the list of movies.
- **`/rate`**: Endpoint to submit a rating.
- **`/recommendations`**: Displays recommended movies.

### **Ratings Service**

This service handles incoming ratings from the VOD portal and publishes them to Kafka.

**Technologies**:

- **Spring Boot**: Framework for building the microservice.
- **Kafka Producer**: To send ratings data to a Kafka topic.

**Endpoints**:

- **`/submitRating`**: Accepts POST requests with rating data.

### **Kafka**

Kafka is used to handle the streaming of ratings data. The ratings are sent to a specific Kafka topic.

**Technologies**:

- **Kafka Broker**: Manages the published messages.
- **Kafka Topic**: Dedicated topic for ratings data.

### **HDFS/File System**

Ratings data is consumed from Kafka and stored in a CSV file. This data will be used for training the recommendation model.

**Technologies**:

- **Kafka Consumer**: To read data from the Kafka topic.
- **CSV Writer**: To write data to **`ratings.csv`**.

### **MongoDB**

MongoDB is used to store metadata about movies such as movie ID, title, genre, etc.

**Technologies**:

- **MongoDB Database**: NoSQL database to store movie data.
- **MongoDB Collections**: Separate collections for movies and other relevant data.

### **Spark**

Spark is used for training the recommendation model using the ratings data.

**Technologies**:

- **`Spark MLlib`**: Machine learning library in Spark.
- **Collaborative Filtering**: Algorithm used for recommendations.

### **Docker**

Docker is used to containerize Kafka, MongoDB, and Spark, making the deployment process easier and more consistent.

**Technologies**:

- **Docker Compose**: Tool for defining and running multi-container Docker applications.

# 1- Movies Dataset cleaning and saving to MongoDB

## Mongo Docker image

```java
version: '3.8'
services:
  mongodb:
    image: mongo:latest
    ports:
      - "27017:27017"
    environment:
      - MONGO_INITDB_ROOT_USERNAME=admin
      - MONGO_INITDB_ROOT_PASSWORD=admin
    volumes:
      - mongo-data:/data/db

  mongo-express:
    image: mongo-express:latest
    restart: always
    ports:
      - "8081:8081"
    environment:
      - ME_CONFIG_MONGODB_ADMINUSERNAME=admin
      - ME_CONFIG_MONGODB_ADMINPASSWORD=admin
      - ME_CONFIG_MONGODB_SERVER=mongodb

# Define named volumes
volumes:
  mongo-data:
    driver: local
```
![Untitled 3](https://github.com/mohammad-fahs/VOD-Platform-with-Recommendation/assets/109221274/b36d6c49-6819-4839-b14e-eec1ded31ee9)



## Getting the dataset and saving to mongo DB from CSV

### Movie Model

```java
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
```

## Movie repository

```java
package com.bigdata.platform.web.app.respository;

import com.bigdata.platform.web.app.model.Movie;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MovieRepository extends MongoRepository<Movie,Long> {
}
```

### Saving from CSV To mongo DB

```java
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
```

### Checking if the movies are saved in the mogo DB

![Untitled 2](https://github.com/mohammad-fahs/VOD-Platform-with-Recommendation/assets/109221274/8e7266c0-f321-4027-b8a2-be0f434eea35)


### Display the movies using Thymeleaf

```java
    @GetMapping(value = "/movies")
    public String index(Model model){
        model.addAttribute("count", movieService.getMoviesCount());
        return "index";
    }
```

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" lang="en" class="h-100">
<head>
    <meta charset="UTF-8">
    <title>Movies</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.11.8/dist/umd/popper.min.js"></script>
    <script src="https://kit.fontawesome.com/b3d8f039cf.js" crossorigin="anonymous"></script>
    <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
    <script src="https://code.jquery.com/jquery-3.5.1.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/OwlCarousel2/2.3.4/owl.carousel.min.js"></script>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/OwlCarousel2/2.3.4/assets/owl.carousel.min.css">
    <link rel="stylesheet" th:href="@{/css/index.css}">
    <script th:src="@{js/index.js}"></script>
    <style>
        body{
            background-color: #131313 !important;
        }

        h4 {
            font-size: 1.5rem;
            margin-bottom: 10px;
        }

        .card {
            transition: 0.45s;
            border: none;
            background-color: #666C74;
            border-radius: 6px !important;
            overflow: hidden;
            border: 1px solid transparent !important;
        }
        .card:hover {
            transform: translateY(-15px);
        }
        .card img{
            border-radius: 0 !important;
            opacity: 0.9;
        }
        .card:hover img{
            opacity: 1;
        }
        .card a {
            color: inherit;
            text-decoration: none;
        }

        .card__thumb {
            overflow: hidden;
            -webkit-transition: height 0.5s;
            transition: height 0.5s;
            z-index: 20;
        }

        .card__thumb img {
            width: 100%;
            display: block;
        }

        .card:hover .card__thumb img {
            -webkit-transform: scale(1.3);
            transform: scale(1.3);
            transition-duration: 0.45s;
            transition-timing-function: ease-in-out;
            border-radius: 9px 9px 0 0;
        }

        .card__body {
            /* background-color: #ededed; */
            position: absolute;
            color: white !important;
            width: 100%;
            z-index: 30;
            bottom: 0;
            left: 0;
            right: 0;
            padding: 10px;
            padding-top: 70px;
            border-top: none;
            opacity: 0;
            transition: 0.4s;
        }

        .card:hover .card__body {
            opacity: 1;
            background-image: linear-gradient(to top, black , transparent);
            display: block;
        }

        article:hover{cursor: pointer;}
        .card__meta {
            position: absolute;
            bottom: 10px;
            left: 10px;
            z-index: 80;
            text-transform: uppercase;
            font-size: 12px;
            display: flex;
            transition: .45s;
            font-weight: bold;
            gap: 5px;
        }
        .card:hover .card__meta {
            bottom: 70px;
        }

        .card__category {
            padding: 3px 15px;
            background-color: #DB0000;
            color: #fff;
            border-radius: 3px;
            text-transform: uppercase;
        }

        .card__rating {
            padding: 2px 5px;
            border-radius: 3px;
            color: #2c0000;
            line-height: 20px;
            background-color: #ba7474;
        }

        .card__title {
            font-size: 18px;
            font-weight: 600;
        }

        .card__subtitle > span {
            display: inline-block;
            color: #14b8a6;
            font-weight: bold;
            position: relative;
        }

        .card__subtitle > span + span {
            margin-right: 14px;
        }

        .card__subtitle > span + span::after {
            content: "";
            position: absolute;
            width: 4px;
            height: 4px;
            background-color: #14b8a6;
            border-radius: 100%;
            opacity: 0.75;
            right: -10px;
            top: 50%;
            transform: translateY(-50%);
        }

        .card:hover .card__description {
            display: block;
        }

        .icon + .icon {
            padding-left: 10px;
        }
        .container {
            display: flex;
            justify-content: center;
            align-items: center;
            gap: 40px; /* Adds 40px space between the elements */
            margin: 0 auto;
            padding: 20px;
        }

        h3 {
            margin: 0;
        }

        .logo:hover {
            cursor: pointer;
        }

    </style>

</head>
<body style="margin: 30px;background-color: #f7f7f7;margin-top: 10px;">
<div class="container-fluid">
    <div class="">
        <header class="d-flex flex-wrap justify-content-center py-3 mb-4">
            <nav>
                <p class="logo" onclick="window.location.href='/movies';">
                    VOD<span>Platform<span>
                </p>

                <i class="fa fa-bars" id="menu"></i>

                <ul id="menu-box">

                    <li>GitHub</li>
                    <li>Documentation</li>
                </ul>

            </nav>
        </header>
    </div>
    <div class="row" style="">
        <div class="col-1 "></div>
        <div class="col-10 ">
            <div class="container">
                <h3 style="color: #ffffff; background: lightgrey; padding: 10px; border-radius: 8px; box-shadow: 0 1px 2px rgb(244,247,250); text-align: center; max-width: 700px;">
                    <span style="font-weight: bold; color: #100F0F;">Number of movies on this platform is</span>
                    <span style="font-weight: bold; color: #DB0000;" th:text="${count}"></span>
                </h3>
                <a href="/rate" style="text-decoration: none;">
                    <h3 style="color: #fff; background: linear-gradient(45deg, #6a11cb 0%, #2575fc 100%); padding: 10px; border-radius: 8px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2); text-align: center; max-width: 400px;">
                        <span style="font-weight: bold; color: #ffd700;">Rate Random Movies</span>
                    </h3>
                </a>
            </div>
            <div id="searchResults" class="row row-cols-5  " style="margin-top: 30px;">

            </div>
            <div>
                <nav aria-label="Page navigation example">
                    <ul class="pagination justify-content-center">
                        <li id="previous" class="page-item">
                            <a class="page-link" href="#" tabindex="-1">Previous</a>
                        </li>
                        <li id="next" class="page-item">
                            <a class="page-link" href="#">Next</a>
                        </li>
                    </ul>
                </nav>
            </div>
        <div class="col-1 "></div>
    </div>
    </div>
</div>
<script>
    const urlParams = new URLSearchParams(window.location.search);
    const page = urlParams.get('page') || '1';

    const currentPage = parseInt(urlParams.get('page')) || 1;

    // Update previous link href

    if (currentPage > 1) {
        $('#previous').empty().append(`
                      <a class="page-link" href="/movies?page=${currentPage - 1}" tabindex="-1">Previous</a>
        `);
    }else{
        $('#previous').empty().append(`
                      <a class="page-link disabled" href="#" tabindex="-1">Previous</a>
        `);
    }

    // Update next link href

    $('#next').empty().append(`
         <a class="page-link" href="/movies?page=${currentPage + 1}">Next</a>
    `)
</script>
<script>

    const  placeholder = `<div class="col">
                            <div class="card  m-1" aria-hidden="true">
                                <div class="card-body">
                                    <div class="h5 card-title placeholder-glow">
                                        <span class="placeholder col-6"></span>
                                    </div>
                                    <p class="card-text placeholder-glow">
                                      <span class="placeholder col-7"></span>
                                      <span class="placeholder col-4"></span>
                                      <span class="placeholder col-4"></span>
                                      <span class="placeholder col-6"></span>
                                      <span class="placeholder col-8"></span>
                                    </p>
                                </div>
                          </div>
                          <br>
                          <div class="card  m-1" aria-hidden="true">
                                <div class="card-body">
                                    <div class="h5 card-title placeholder-glow">
                                        <span class="placeholder col-6"></span>
                                    </div>
                                    <p class="card-text placeholder-glow">
                                      <span class="placeholder col-7"></span>
                                      <span class="placeholder col-4"></span>
                                      <span class="placeholder col-4"></span>
                                      <span class="placeholder col-6"></span>
                                      <span class="placeholder col-8"></span>
                                    </p>
                                </div>
                          </div>
                          <br>
                          <div class="card  m-1" aria-hidden="true">
                                <div class="card-body">
                                    <div class="h5 card-title placeholder-glow">
                                        <span class="placeholder col-6"></span>
                                    </div>
                                    <p class="card-text placeholder-glow">
                                      <span class="placeholder col-7"></span>
                                      <span class="placeholder col-4"></span>
                                      <span class="placeholder col-4"></span>
                                      <span class="placeholder col-6"></span>
                                      <span class="placeholder col-8"></span>
                                    </p>
                                </div>
                          </div>
                          <br>
                          <div class="card  m-1" aria-hidden="true">
                                <div class="card-body">
                                    <div class="h5 card-title placeholder-glow">
                                        <span class="placeholder col-6"></span>
                                    </div>
                                    <p class="card-text placeholder-glow">
                                      <span class="placeholder col-7"></span>
                                      <span class="placeholder col-4"></span>
                                      <span class="placeholder col-4"></span>
                                      <span class="placeholder col-6"></span>
                                      <span class="placeholder col-8"></span>
                                    </p>
                                </div>
                          </div>
                          <br>

                        </div>`;

    const getMovies = function(){

        $('#searchResults').html(placeholder + placeholder + placeholder +placeholder +placeholder +placeholder+ placeholder+placeholder+placeholder+placeholder);

        $.ajax({
            type: "GET",
            url: "/get-movies?page="+page+"&size="+35,
            success: function(data) {
                $('#searchResults').empty();
                $.each(data, function(index, document) {
                    let article = $(`
                        <article class="col">
                            <div class="card mb-4 shadow-sm">
                                <header class="card__thumb">
                                    ${document.posterPath ? `<img src="${document.posterPath}">` : ''}
                                </header>
                                <div class="card__meta">
                                    ${document.originalLanguage ? `<div class="card__category">${document.originalLanguage}</div>` : ''}
                                    ${document.voteAverage ? `<div class="card__rating">${document.voteAverage}</div>` : ''}
                                </div>
                                <div class="card__body">
                                    ${document.title ? `<h3 class="card__title" style="direction: ltr">${document.title}</h3>` : ''}
                                </div>
                            </div>
                        </article>
                    `);

                    // Add click event listener
                    article.on('click', function() {
                        window.location.href = `/movie?id=${document.id}`;
                    });

                    $('#searchResults').append(article);
                });
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.error("Error occurred: ", textStatus, errorThrown);
            }
        });
    }

    getMovies();

</script>
</body>
</html>
```

## Movies display result
![Untitled 5](https://github.com/mohammad-fahs/VOD-Platform-with-Recommendation/assets/109221274/6ac99d64-5444-441a-b407-e3d8e23b85a0)
![Untitled 6](https://github.com/mohammad-fahs/VOD-Platform-with-Recommendation/assets/109221274/f3c6639b-4f10-41ca-8e06-92452c9d736e)



# Rating and Cold Start

when user enter the website a unique ID is given to the user saved in the local storage that the rating will be saved to that ID to feed the ML Algorithm for later.

since the user has no movies rating we created an easy way to allow the user to rate some random movies by clicking on the rate it .
![Untitled 7](https://github.com/mohammad-fahs/VOD-Platform-with-Recommendation/assets/109221274/29e76a46-e71f-4190-8a33-dff09a1c7df5)
![Untitled 8](https://github.com/mohammad-fahs/VOD-Platform-with-Recommendation/assets/109221274/57f43538-ad08-4138-8378-a1e4c430c8c4)



## Kafka

Kafka Runs on a docker container with 3 instances and a zoo keeper to manage the instance 

```yaml
version: '3.7'

services:
  zookeeper:
    image: confluentinc/cp-zookeeper:latest
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_SERVER_ID: 1
    ports:
      - "2181:2181"

  kafka-1:
    image: confluentinc/cp-kafka:latest
    ports:
      - "9092:9092"
      - "29092:29092"
    environment:
      KAFKA_ADVERTISED_LISTENERS: INTERNAL://kafka-1:19092,EXTERNAL://${DOCKER_HOST_IP:-127.0.0.1}:9092,DOCKER://host.docker.internal:29092
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: INTERNAL:PLAINTEXT,EXTERNAL:PLAINTEXT,DOCKER:PLAINTEXT
      KAFKA_INTER_BROKER_LISTENER_NAME: INTERNAL
      KAFKA_ZOOKEEPER_CONNECT: "zookeeper:2181"
      KAFKA_BROKER_ID: 1
    depends_on:
      - zookeeper

  kafka-2:
    image: confluentinc/cp-kafka:latest
    ports:
      - "9093:9093"
      - "29093:29093"
    environment:
      KAFKA_ADVERTISED_LISTENERS: INTERNAL://kafka-2:19093,EXTERNAL://${DOCKER_HOST_IP:-127.0.0.1}:9093,DOCKER://host.docker.internal:29093
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: INTERNAL:PLAINTEXT,EXTERNAL:PLAINTEXT,DOCKER:PLAINTEXT
      KAFKA_INTER_BROKER_LISTENER_NAME: INTERNAL
      KAFKA_ZOOKEEPER_CONNECT: "zookeeper:2181"
      KAFKA_BROKER_ID: 2
    depends_on:
      - zookeeper

  kafka-3:
    image: confluentinc/cp-kafka:latest
    ports:
      - "9094:9094"
      - "29094:29094"
    environment:
      KAFKA_ADVERTISED_LISTENERS: INTERNAL://kafka-3:19094,EXTERNAL://${DOCKER_HOST_IP:-127.0.0.1}:9094,DOCKER://host.docker.internal:29094
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: INTERNAL:PLAINTEXT,EXTERNAL:PLAINTEXT,DOCKER:PLAINTEXT
      KAFKA_INTER_BROKER_LISTENER_NAME: INTERNAL
      KAFKA_ZOOKEEPER_CONNECT: "zookeeper:2181"
      KAFKA_BROKER_ID: 3
    depends_on:
      - zookeeper
```

## Kafka Produce Consumer pipeline

### 1- Get the Rating request form the user

```java
    @PostMapping("/receive-id")
    public ResponseEntity<?> receiveId(@RequestBody Rating rating) {
        try{
            return ResponseEntity.ok(ratingProducer.send(rating.generateString()));
        }catch (Exception e){
            System.out.println("failed to publish rating!");
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    

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
    private String id;
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

```

### Produce the rating to Kafka Topic

```java
package com.bigdata.platform.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class TopicConfig {
    @Value("${spring.kafka.topic.name}")
    String name;
    @Bean
    public NewTopic testTopic(){
        return TopicBuilder.name(name).build();
    }

}

```

```java
package com.bigdata.platform.kafka.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    public Map<String, Object> producerConfig(){
        HashMap<String,Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return props;
    }

    @Bean
    public ProducerFactory<String,String> producerFactory(){
        return new DefaultKafkaProducerFactory<>(producerConfig());
    }

    @Bean
    public KafkaTemplate<String,String> kafkaTemplate(ProducerFactory<String,String> producerFactory){
        return new KafkaTemplate<>(producerFactory);
    }

}

```

```java
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

```

### Consume the Data and save to CSV

```java
package com.bigdata.platform.kafka.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    public Map<String, Object> consumerConfig(){
        HashMap<String,Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return props;
    }

    @Bean
    public ConsumerFactory<String,String> consumerFactory(){
        return new DefaultKafkaConsumerFactory<>(consumerConfig());
    }

    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String,String>> factory(ConsumerFactory<String,String> consumerFactory){
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }

}

```

```java
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
    private final RatingRepository ratingRepository;
    @KafkaListener(topics = "rating", groupId = "ratings-group")
    public void consume(String message) throws IOException {
        try (FileWriter writer = new FileWriter("ML-Model/ratings.csv", true)) {
            writer.append(message).append("\n");
        } catch (IOException e) {
            System.out.println("error writing file");
        }
        try{
            ratingRepository.save(new Rating(message));
        }catch (Exception e){
            System.out.println("Failed to save rating");
        }
    }

}

```

### CSV File

![Untitled 9](https://github.com/mohammad-fahs/VOD-Platform-with-Recommendation/assets/109221274/a7f412db-866a-49e6-aef3-636d608cca7b)


# ML Model

the Model is ALS model we have 2 main methods in Fast API python project 

1- get trending movies which is used in case no recommendations where found for a user 

```python
from pyspark.sql import SparkSession
from pyspark.ml.evaluation import RegressionEvaluator
from pyspark.ml.recommendation import ALS
from pyspark.sql.functions import col, avg
from pyspark.ml.feature import StringIndexer
import json

def get_trending():
    spark = SparkSession.builder \
        .appName("MovieRecommender") \
        .config("spark.executor.memory", "4g") \
        .config("spark.executor.instances", "2") \
        .config("spark.executor.cores", "2") \
        .config("spark.default.parallelism", "4") \
        .config("spark.sql.shuffle.partitions", "4") \
        .getOrCreate()

    # Load the dataset
    file_path = "ratings.csv"
    ratings_df = spark.read.csv(file_path, header=True, inferSchema=True)

    # Preprocess the data
    ratings_df = ratings_df.select(
        col("userId").cast("string"),
        col("movieId").cast("integer"),
        col("rating").cast("float")
    )

    # Calculate average ratings for each movie
    movie_avg_ratings = ratings_df.groupBy("movieId").agg(avg("rating").alias("avg_rating"))

    # Show top 10 movies by average rating
    top_movies = movie_avg_ratings.orderBy(col("avg_rating").desc()).limit(5)

    # Function to recommend top movies to a new user
    def recommend_top_movies(top_movies_df, n=10):
        return top_movies_df.orderBy(col("avg_rating").desc()).limit(n)

    # Recommend top 10 movies to a new user
    top_recommendations = recommend_top_movies(top_movies, 5)
    # Collect the rows of the DataFrame
    rows = top_recommendations.collect()

    # Extract the movieId values into an array
    movie_ids = [row['movieId'] for row in rows]
    spark.stop()
    return movie_ids

```

2- get recommendations based on ALS algorithm for user in case he has ratings

```python
from pyspark.sql import SparkSession
from pyspark.ml.evaluation import RegressionEvaluator
from pyspark.ml.recommendation import ALS
from pyspark.sql.functions import col, avg
from pyspark.ml.feature import StringIndexer
import json

def get_user_recommendations():
    # Initialize Spark session
    spark = SparkSession.builder \
        .appName("MovieRecommender") \
        .config("spark.executor.memory", "4g") \
        .config("spark.executor.instances", "2") \
        .config("spark.executor.cores", "2") \
        .config("spark.default.parallelism", "4") \
        .config("spark.sql.shuffle.partitions", "4") \
        .getOrCreate()
    # Load the dataset
    file_path = "ratings.csv"
    ratings_df = spark.read.csv(file_path, header=True, inferSchema=True)
    print("Size of ratings_df:", ratings_df.count())

    # Preprocess the data
    ratings_df = ratings_df.select(
        col("userId").cast("string"),
        col("movieId").cast("integer"),
        col("rating").cast("float")
    ).cache()  # Cache the DataFrame to speed up subsequent operations

    # Convert userId from string to numeric using StringIndexer
    user_indexer = StringIndexer(inputCol="userId", outputCol="userIndex")
    indexed_df = user_indexer.fit(ratings_df).transform(ratings_df).cache()  # Cache after transformation

    # Split the data into training and test sets
    (training_df, test_df) = indexed_df.randomSplit([0.9, 0.1])

    # Configure and train the ALS model
    als = ALS(
        maxIter=10,
        regParam=0.1,
        userCol="userIndex",
        itemCol="movieId",
        ratingCol="rating",
        coldStartStrategy="drop",
        nonnegative=True
    )
    model = als.fit(training_df)
    # Generate recommendations for each user
    user_recs = model.recommendForAllUsers(5)

    # Create a mapping DataFrame from userIndex to userId
    user_id_map = indexed_df.select("userIndex", "userId").distinct().cache()  # Cache the mapping DataFrame

    # Join the recommendations with the mapping DataFrame to get userId
    user_recs_with_id = user_recs.join(user_id_map, on="userIndex", how="inner") \
        .select("userId", "recommendations")

    # Collect the results as a list of rows
    user_recs_list = user_recs_with_id.collect()

    # Generate a list of dictionaries with user ID and recommended movies
    user_recs_json = [
        {"userId": row["userId"], "recommendedMovies": [rec.movieId for rec in row["recommendations"]]}
        for row in user_recs_list
    ]
    spark.stop()
    return user_recs_json

```

### API to get the recommendations from the Python Project

```python
from fastapi import FastAPI
from pyspark.sql import SparkSession

import ALSModel

app = FastAPI()

@app.get("/recommendations")
async def get_recommendations():
    return ALSModel.get_user_recommendations()

@app.get("/trending")
async def get_trending():
    return ALSModel.get_trending()
```

## Consuming the Recommendations

the consumption is done through service which get the recommendations from HTTP request and saves them to a Redis cache which is running on docker in case we have nor recommendations the recommend API is called and is still there is no recommendations the trending are displayed  

```yaml
version: "3.3"
services:
  redis:
    image: redis:6.0.7
    container_name: redis
    restart: always
    volumes:
      - redis_volume_data:/data
    ports:
      - 6379:6379
  redis_insight:
    image: redislabs/redisinsight:latest
    container_name: redis_insight
    restart: always
    ports:
      - 8001:8001
    volumes:
      - redis_insight_volume_data:/db
volumes:
  redis_volume_data:
  redis_insight_volume_data:
```

### Get Rceommendatiosn

```java
package com.bigdata.platform.redis.sevrice;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class MLAPIService {
    private final OkHttpClient client;
    public MLAPIService(){
        this.client = new OkHttpClient();
        client.setConnectTimeout(45, TimeUnit.SECONDS);
        client.setReadTimeout(45, TimeUnit.SECONDS);
    }

    HashMap<String, List<Long>> getRecommendations() throws IOException {
        HashMap<String,List<Long>> recommendations = new HashMap<>();
        Request request = new Request.Builder()
                .url("http://127.0.0.1:8000/recommendations")
                .build();
        Response response = client.newCall(request).execute();
        JSONArray result = new JSONArray(response.body().string());
       for(int i=0 ; i< result.length() ; i++){
           JSONObject recommendation = result.getJSONObject(i);
           if(recommendation.has("userId") && recommendation.has("recommendedMovies"))
               recommendations.put(recommendation.getString("userId"), ArrayToLongList(recommendation.getJSONArray("recommendedMovies")));
       }
       return recommendations;
    }

    List<Long> getTrending() throws IOException {
        Request request = new Request.Builder()
                .url("http://127.0.0.1:8000/trending")
                .build();
        Response response = client.newCall(request).execute();
        JSONArray result = new JSONArray(response.body().string());
        return ArrayToLongList(result);
    }

    private List<Long> ArrayToLongList(JSONArray array){
        List<Long> trending = new ArrayList<>();
        for(int i=0 ; i< array.length() ; i++)
            trending.add(array.getLong(i));
        return trending;
    }

}

```

### Recommendation Service

```java
package com.bigdata.platform.redis.sevrice;

import com.bigdata.platform.web.app.respository.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final CacheService cacheService;
    private final MLAPIService mlapiService;
    private final RatingRepository ratingRepository;

    public List<Long> getUserRecommendations(String id) throws IOException {
        if(ratingRepository.existsByUserId(id)){
            System.out.println("there is data!!");
            if(cacheService.isFieldInCache(id)){
                System.out.println("from AI Model / Cache");
                return cacheService.getCachedField(id);
            }

            HashMap<String,List<Long>> recommendations = mlapiService.getRecommendations();
            for (Map.Entry<String, List<Long>> entry : recommendations.entrySet())
                cacheService.addToCache(entry.getKey(), entry.getValue());
            if(recommendations.containsKey(id)){
                System.out.println("from AI Model / NOT Cache");
                return recommendations.get(id);
            }

        }
        System.out.println("from trending no user found after running model");
        return getTrending();
    }

    private List<Long> getTrending() throws IOException {
        if(cacheService.isFieldInCache("trending"))
            return cacheService.getCachedField("trending");
        List<Long> trending = mlapiService.getTrending();
        cacheService.addToCache("trending",trending);
        return trending;
    }

    public static int trap(int[] height) {
        int trappedWater = 0, j=0;
        int bottomBlocks = 0;
        for(int i=0  ; i<height.length - 1 ; i++){
            System.out.println("i: "+i+" j: "+j);
            System.out.println("trapped: "+trappedWater+" blocks: "+bottomBlocks);
            if(height[i] <= height[i+1] && j <= height[i+1]){
                if(j == 0){
                    continue;
                }else{
                    bottomBlocks -= height[i];
                    trappedWater += (Math.min(height[j],height[i+1]) * (i-j) + bottomBlocks);
                    bottomBlocks=0;j=0;
                }

            }else{
                if(j == 0){
                    j = i;
                }else{
                    bottomBlocks -= height[i];
                }

            }
        }
        return trappedWater;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int queries = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character after reading the integer

        Stack<Integer> stack = new Stack<>();
        Stack<Integer> spareStack = new Stack<>();

        for (int i = 0; i < queries; i++) {
            String[] query = scanner.nextLine().split(" ");
            int type = Integer.parseInt(query[0]);
            switch (type) {
                case 1:
                    int element = Integer.parseInt(query[1]);
                    while(!stack.isEmpty()){
                        spareStack.push(stack.pop());
                    }
                    stack.push(element);
                    while(!spareStack.isEmpty()){
                        stack.push(spareStack.pop());
                    }
                    break;
                case 2:
                    if(!stack.isEmpty())
                        stack.pop();
                    break;
                case 3:
                    if (!stack.isEmpty()) {
                        System.out.println(stack.peek());
                    }
                    break;
                default:
                    System.out.println("Invalid query type.");
            }
        }

        scanner.close();
    }
}

```

### Redis Cache Configuration and service

```java
package com.bigdata.platform.redis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.List;

@Configuration
public class RedisConfig {

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        return new JedisConnectionFactory();
    }

    @Bean
    public RedisTemplate<String, List<Long>> redisTemplate() {
        RedisTemplate<String, List<Long>> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return redisTemplate;
    }
}

```

 

```java
package com.bigdata.platform.redis.sevrice;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class CacheService {

    private final RedisTemplate<String, List<Long>> redisTemplate;

    // Add a field to the cache
    public void addToCache(String key, List<Long> value) {
        redisTemplate.opsForValue().set(key, value);
    }

    // Check if a field is contained in the cache
    public boolean isFieldInCache(String key) {
        return redisTemplate.hasKey(key);
    }

    // Return cached field
    public List<Long> getCachedField(String key) {
        return redisTemplate.opsForValue().get(key);
    }
}

```

# Demo

A new user enter the platform that has no rating of movies 

![Untitled 10](https://github.com/mohammad-fahs/VOD-Platform-with-Recommendation/assets/109221274/1387d7a3-d1b9-46ff-9309-7e207f9492a7)


the user adds rating and then recommendations is called

![Untitled 11](https://github.com/mohammad-fahs/VOD-Platform-with-Recommendation/assets/109221274/1f24da28-9a9c-4d61-b6ee-f208e74a48ee)
