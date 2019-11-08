package com.github.wkennedy.pubsubly.controllers;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.wkennedy.pubsubly.models.MessageResource;
import com.github.wkennedy.pubsubly.models.Page;
import com.github.wkennedy.pubsubly.models.TextCountResource;
import com.github.wkennedy.pubsubly.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RequestMapping("/topics")
@RestController
public class TopicController {

    private final MessageService messageService;

    private final Cache<String, List<String>> topicCache;

    @Autowired
    public TopicController(MessageService statService, Cache<String, List<String>> topicCache) {
        this.messageService = statService;
        this.topicCache = topicCache;
    }

    @GetMapping
    public @ResponseBody List<Resource<String>> getTopics() {
        List<Resource<String>> resources = new ArrayList<>();
        Set<String> topics = topicCache.asMap().keySet();
        for (String topic : topics) {
            Resource<String> resource = new Resource<>(topic);
            resource.add(linkTo(methodOn(TopicController.class).getTopicData(topic)).withSelfRel());
            resources.add(resource);
        }
        return resources;
    }

    @GetMapping(value = "/{topicName}")
    public @ResponseBody List<MessageResource> getTopicData(@PathVariable String topicName) {
        List<String> messageUUIDs = topicCache.getIfPresent(topicName);
        List<MessageResource> messages = messageService.getMessageResources(messageUUIDs);
        for (MessageResource messageResource : messages) {
            if(messageResource.getLinks().isEmpty()) {
                Map<String, String> messageKeyMap = messageResource.getMessageKeyMap();
                for (String key : messageKeyMap.keySet()) {
                    messageResource.add(linkTo(methodOn(MessageController.class).getByTag(key, messageKeyMap.get(key))).withSelfRel());
                }
            }
        }
        return messages;
    }

    @GetMapping(value = "/{topicName}", params = { "pageSize", "page" })
    public @ResponseBody Page<List<MessageResource>> getTopicDataPaginated(@PathVariable String topicName,
                                                            @RequestParam("pageSize") int pageSize, @RequestParam("page") int page) {

        List<String> messageUUIDs = topicCache.getIfPresent(topicName);
        if(messageUUIDs == null) {
            return new Page<>(new ArrayList<>());
        }

        if(page < 1) {
            page = 1;
        }

        int startIndex = (page == 1) ? (page - 1) :  ((page - 1) + pageSize);
        List<MessageResource> messages = messageService.getMessageResources(messageUUIDs, startIndex, pageSize);
        //TODO clean up this loop and add paging
//        for (MessageResource messageResource : messages) {
//            if(messageResource.getLinks().isEmpty()) {
//                Map<String, String> messageKeyMap = messageResource.getMessageKeyMap();
//                for (String key : messageKeyMap.keySet()) {
//                    messageResource.add(linkTo(methodOn(MessageController.class).getByTag(key, messageKeyMap.get(key))).withSelfRel());
//                }
//            }
//        }
        Page<List<MessageResource>> pagedListHolder = new Page<>(messages);
        pagedListHolder.setPage(page);
        pagedListHolder.setPageSize(pageSize);
        pagedListHolder.setTotal(messageUUIDs.size());
        return pagedListHolder;
    }

    @GetMapping(value = "/counts")
    public @ResponseBody
    List<TextCountResource> getCounts() {
        ConcurrentMap<String, List<String>> topicCacheMap = topicCache.asMap();
        List<TextCountResource> textCounts = new ArrayList<>();
        for (String topicName : topicCacheMap.keySet()) {
            TextCountResource textCount = new TextCountResource(topicName, Integer.toString(topicCacheMap.get(topicName).size()));
            textCount.add(linkTo(methodOn(TopicController.class).getTopicData(textCount.getText())).withSelfRel());
            textCounts.add(textCount);
        }
        return textCounts;
    }

}
