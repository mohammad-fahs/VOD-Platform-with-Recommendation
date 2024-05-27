package com.bigdata.platform.redis.sevrice;

import com.bigdata.platform.web.app.respository.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
}
