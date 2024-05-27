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
