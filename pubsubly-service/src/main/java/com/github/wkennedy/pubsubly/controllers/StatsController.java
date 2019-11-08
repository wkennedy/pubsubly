package com.github.wkennedy.pubsubly.controllers;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.wkennedy.pubsubly.models.TextCount;
import com.github.wkennedy.pubsubly.services.StatsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

@RequestMapping("/stats")
@RestController
public class StatsController {

    private final Cache<String, List<String>> topicCache;

    private final StatsService statsService;

    public StatsController(Cache<String, List<String>> topicCache, StatsService statsService) {
        this.topicCache = topicCache;
        this.statsService = statsService;
    }

    @GetMapping(value = "/topicCount")
    public @ResponseBody
    List<TextCount> getCounts() {
        ConcurrentMap<String, List<String>> topicCacheMap = topicCache.asMap();
        List<TextCount> textCounts = new ArrayList<>();
        for (String topicName : topicCacheMap.keySet()) {
            TextCount textCount = new TextCount(topicName, Integer.toString(topicCacheMap.get(topicName).size()));
            textCounts.add(textCount);
        }
        return textCounts;
    }

    @GetMapping(value = "/countsByDate")
    public @ResponseBody
    Map<String, StatsService.CountByDay> getCountsByDay() {

        return statsService.getCountsByDay();
    }
}
