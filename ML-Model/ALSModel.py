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
