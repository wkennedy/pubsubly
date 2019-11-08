package com.github.wkennedy.pubsubly.config;

import com.github.wkennedy.pubsubly.api.Processor;
import com.github.wkennedy.pubsubly.api.Tag;
import com.github.wkennedy.pubsubly.processors.PluginExecutor;
import com.github.wkennedy.pubsubly.processors.ProcessorPlugin;
import com.github.wkennedy.pubsubly.processors.TopicProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Configuration
public class ProcessorConfig {

    @Bean
    public PluginExecutor pluginExecutor(@Autowired TopicProcessor topicProcessor, @Autowired PluginProcessorProperties pluginProcessorProperties, @Autowired Map<String, Processor> processorMap) {
        PluginExecutor pluginExecutor = new PluginExecutor(topicProcessor);
        List<PluginProcessorProperties.Processor> processorConfigs = pluginProcessorProperties.getProcessors();

        for (PluginProcessorProperties.Processor processorConfig : processorConfigs) {
            Processor processor = processorMap.get(processorConfig.getId());
            ProcessorPlugin processorPlugin = processorPlugin();
            processorPlugin.setProcessor(processor);
            List<Tag> tags = new ArrayList<>();
            tags.addAll(processorConfig.getTags());
            tags.addAll(pluginProcessorProperties.getTags());
            processorPlugin.setTags(tags);
            pluginExecutor.addProcessorPlugin(processorPlugin);
        }

        return pluginExecutor;
    }

    @Bean
    @Scope(scopeName = "prototype")
    public ProcessorPlugin processorPlugin() {
        return new ProcessorPlugin();
    }
}
