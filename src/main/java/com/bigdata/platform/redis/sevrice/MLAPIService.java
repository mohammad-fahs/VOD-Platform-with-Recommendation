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
