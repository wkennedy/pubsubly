package com.github.wkennedy.pubsubly.controllers;

import com.github.wkennedy.pubsubly.api.Tag;
import com.github.wkennedy.pubsubly.models.MessageDetails;
import com.github.wkennedy.pubsubly.models.MessageResource;
import com.github.wkennedy.pubsubly.models.MessageResourceBundle;
import com.github.wkennedy.pubsubly.services.MessageService;
import com.github.wkennedy.pubsubly.services.ProcessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController("messages")
public class MessageController {

    private final MessageService messageService;
    private final ProcessorService processorService;

    @Autowired
    public MessageController(MessageService messageService, ProcessorService processorService) {
        this.messageService = messageService;
        this.processorService = processorService;
    }

    @GetMapping("/tags")
    public @ResponseBody
    List<Tag> getTags() {
        return processorService.getTags();
    }

    //TODO fix this inefficient loop
    @GetMapping(value = "/tag/{key}/{value}")
    public @ResponseBody
    MessageDetails getByTag(@PathVariable String key, @PathVariable String value) {
        MessageDetails messageStats = messageService.messageDetails(key, value);
        List<MessageResource> messages = messageStats.getMessageResourceBundle().getMessageResources();
//        Map<String, Tag> tagsAsMap = processorService.getTagsAsMap();
        for (MessageResource messageResource : messages) {
            if (messageResource.getLinks().isEmpty()) {
                Map<String, String> tagMap = messageResource.getMessageKeyMap();
                if (tagMap != null && !tagMap.isEmpty()) {
                    for (String tag : tagMap.keySet()) {
                        //avoid self reference
                        if (!key.equals(tag)) {
                            messageResource.add(linkTo(methodOn(MessageController.class).getByTag(tag, tagMap.get(tag))).withSelfRel());
                        }
                    }
                }
            }
        }

        return messageStats;
    }

    @GetMapping(value = "/messageResourceBundles/tag/{key}/{value}")
    public @ResponseBody
    MessageResourceBundle getMessageResourceBundlesByTag(@PathVariable String key, @PathVariable String value) {
        return messageService.messageResources(key, value);
    }

    @GetMapping(value = "/messageResources/tag/{key}/{value}")
    public @ResponseBody
    List<MessageResource> getMessageResourcesByTag(@PathVariable String key, @PathVariable String value) {
        return messageService.messageResources(key, value).getMessageResources();
    }

    @GetMapping(value = "/header/{key}/{value}")
    public @ResponseBody
    List<MessageResource> getByHeader(@PathVariable String key, @PathVariable String value) {
        List<MessageResource> messageResources = messageService.searchMessageResources(key, value);
        addLinks(messageResources);

        return messageResources;
    }

    @GetMapping(value = "/payload/{value}")
    public @ResponseBody
    List<MessageResource> getByPayloadValue(@PathVariable String value) {
        List<MessageResource> messageResources = messageService.searchMessageResourcesByPayload(value);
        addLinks(messageResources);

        return messageResources;
    }

    @GetMapping(value = "/messageResources/")
    public @ResponseBody Map<Map<String, Object>, List<MessageResource>> getByCommonHeaders(@RequestParam Set<String> headerKeys, @RequestParam(defaultValue = "86400000") Long millis) {
        Map<Map<String, Object>, List<MessageResource>> messageResources = messageService.getMessageWithHeaderValues(headerKeys, millis);
//        addLinks(messageResources);

        return messageResources;
    }

    private void addLinks(List<MessageResource> messageResources) {
        for (MessageResource messageResource : messageResources) {
            if (messageResource.getLinks().isEmpty()) {
                Map<String, String> tagMap = messageResource.getMessageKeyMap();
                if (tagMap != null && !tagMap.isEmpty()) {
                    for (String tag : tagMap.keySet()) {
                        messageResource.add(linkTo(methodOn(MessageController.class).getByTag(tag, tagMap.get(tag))).withSelfRel());
                    }
                }
            }
        }
    }

    @GetMapping("/messages/stream")
    public Flux<MessageResource> streamMessages() {
        return messageService.streamMessages();
    }
}
