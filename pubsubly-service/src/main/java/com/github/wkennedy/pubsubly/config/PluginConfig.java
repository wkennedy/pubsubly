package com.github.wkennedy.pubsubly.config;

import com.github.wkennedy.pubsubly.api.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class PluginConfig {

    @Bean
    public Map<String, Processor> processorMap(@Autowired List<Processor> processors) {
        HashMap<String, Processor> processorMap = new HashMap<>();
        for (Processor processor : processors) {
            processorMap.put(processor.getName(), processor);
        }

        return processorMap;
    }

}
