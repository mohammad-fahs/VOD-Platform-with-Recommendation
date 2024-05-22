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
