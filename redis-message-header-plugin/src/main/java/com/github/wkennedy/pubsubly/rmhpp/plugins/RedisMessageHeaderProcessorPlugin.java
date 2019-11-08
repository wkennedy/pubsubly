package com.github.wkennedy.pubsubly.rmhpp.plugins;


import com.github.wkennedy.pubsubly.api.Processor;
import com.github.wkennedy.pubsubly.api.Tag;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

import java.util.Map;

public class RedisMessageHeaderProcessorPlugin implements Processor {
    private static final Logger log = LoggerFactory.getLogger(RedisMessageHeaderProcessorPlugin.class);
    private static final String redis_messageSource = "redis_messageSource";

    private Gson gson = new Gson();

    public String process(Message message, MessageHeaders headers, Tag tag) {
        if (!headers.containsKey(redis_messageSource)) {
            return null;
        }

        Map map;
        try {
            map = gson.fromJson((String) message.getPayload(), Map.class);
        } catch (JsonSyntaxException e) {
            return null;
        }

        if (map.containsKey("headers")) {
            Map<String, String> nestedHeaders = (Map<String, String>) map.get("headers");
            return nestedHeaders.get(tag.getValue());
        }

        return null;
    }

    public String getName() {
        return RedisMessageHeaderProcessorPlugin.class.getSimpleName();
    }

    public String getVersion() {
        return "1.0.0";
    }
}
