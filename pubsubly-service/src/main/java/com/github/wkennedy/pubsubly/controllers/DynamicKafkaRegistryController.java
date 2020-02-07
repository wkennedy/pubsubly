package com.github.wkennedy.pubsubly.controllers;

import com.github.wkennedy.pubsubly.services.DynamicKafkaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/listeners/kafka")
public class DynamicKafkaRegistryController {

    private final DynamicKafkaService dynamicKafkaService;

    public DynamicKafkaRegistryController(DynamicKafkaService dynamicKafkaService) {
        this.dynamicKafkaService = dynamicKafkaService;
    }

    @PostMapping(path = "/add/topics")
    public void addTopics(@RequestParam List<String> topics) {
        dynamicKafkaService.startTopics(topics);
    }

    @PostMapping(path = "/add/topic-pattern")
    public void addTopics(@RequestParam String topicPattern) {
        dynamicKafkaService.startPattern(topicPattern);
    }

    @PostMapping(path = "/remove/topics")
    public void removeTopics(@RequestParam List<String> topics) {
        dynamicKafkaService.stopTopics(topics);
    }

    @PostMapping(path = "/remove/topic-pattern")
    public void removeTopics(@RequestParam String topicPattern) {
        dynamicKafkaService.stopPattern(topicPattern);
    }

    @PostMapping(path = "/start/default")
    public void startDefaultTopics() {
        dynamicKafkaService.startDefaultListener();
    }

    @PostMapping(path = "/stopic/default")
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
}
