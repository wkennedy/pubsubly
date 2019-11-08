package com.github.wkennedy.pubsubly.plugins;


import com.github.wkennedy.pubsubly.api.Processor;
import com.github.wkennedy.pubsubly.api.Tag;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.plugin.core.Plugin;
import org.springframework.stereotype.Component;

@Component
public class HeaderProcessorPlugin implements Plugin<String>, Processor {

    public String process(Message message, @Headers MessageHeaders headers, Tag tag) {
        if(headers.containsKey(tag.getValue())) {
            return (String) headers.get(tag.getValue());
        }
        return null;
    }

    public String getName() {
        return HeaderProcessorPlugin.class.getSimpleName();
    }

    public String getVersion() {
        return "1.0.0";
    }

    public boolean supports(String delimiter) {
        return true;
    }
}

