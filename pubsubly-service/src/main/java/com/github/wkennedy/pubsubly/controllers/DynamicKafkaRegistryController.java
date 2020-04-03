package com.github.wkennedy.pubsubly.controllers;

import com.github.wkennedy.pubsubly.services.DynamicKafkaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/listeners/kafka")
public class DynamicKafkaRegistryController {

    private final DynamicKafkaService dynamicKafkaService;

    public DynamicKafkaRegistryController(DynamicKafkaService dynamicKafkaService) {
        this.dynamicKafkaService = dynamicKafkaService;
    }

    @PostMapping(path = "/add/topics")
    public Boolean addTopics(@RequestParam List<String> topics) {
        dynamicKafkaService.startTopics(topics);
        return true;
    }

    @PostMapping(path = "/add/topic-pattern")
    public Boolean addTopics(@RequestParam String topicPattern) {
        dynamicKafkaService.startPattern(topicPattern);
        return true;
    }

    @PostMapping(path = "/remove/topics")
    public Boolean removeTopics(@RequestParam List<String> topics) {
        dynamicKafkaService.stopTopics(topics);
        return true;
    }

    @PostMapping(path = "/remove/topic-pattern")
    public Boolean removeTopics(@RequestParam String topicPattern) {
        dynamicKafkaService.stopPattern(topicPattern);
        return true;
    }

    @PostMapping(path = "/start/default")
    public void startDefaultTopics() {
        dynamicKafkaService.startDefaultListener();
    }

    @PostMapping(path = "/topics/default")
    public void stopDefaultTopics() {
        dynamicKafkaService.stopDefaultListener();
    }

    @GetMapping(path = "/added-topics")
    public Set<String> getDynamicTopics() {
        return dynamicKafkaService.getDynamicTopics();
    }

    @GetMapping(path = "/added-topic-patterns")
    public Set<String> getDynamicTopicPatterns() {
        return dynamicKafkaService.getDynamicTopics();
    }

    @GetMapping(path = "kafka-info")
    public Map<String, String> getKafkaInfo() {
        return dynamicKafkaService.getKafkaInfo();
    }
}
