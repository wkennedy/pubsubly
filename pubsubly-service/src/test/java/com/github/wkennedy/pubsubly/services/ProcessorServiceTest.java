package com.github.wkennedy.pubsubly.services;

import com.github.wkennedy.pubsubly.api.Tag;
import com.github.wkennedy.pubsubly.config.PluginProcessorProperties;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ProcessorServiceTest {

    private PluginProcessorProperties pluginProcessorProperties = new PluginProcessorProperties();
    private ProcessorService processorService;

    @Before
    public void setup() {
        Tag tag = new Tag();
        tag.setValue("eventId");
        tag.setId("eventId");
        PluginProcessorProperties.Processor processor = new PluginProcessorProperties.Processor();
        processor.setId("headerProcessorPlugin");
        processor.setTags(Collections.singletonList(tag));

        pluginProcessorProperties.setProcessors(Collections.singletonList(processor));
        //TODO do we really need to set tags here?
//        pluginProcessorProperties.setTags(Collections.singletonList(tag));

        processorService = new ProcessorService(pluginProcessorProperties);
    }

    @Test
    public void getTags() {
        List<Tag> tags = processorService.getTags();
        assertEquals(1, tags.size());
        assertEquals("eventId", tags.get(0).getId());
    }

    @Test
    public void getTagsAsMap() {
        Map<String, Tag> tagsAsMap = processorService.getTagsAsMap();
        assertEquals("eventId", tagsAsMap.get("eventId").getValue());
    }

    @Test
    public void getMessageCorrelationId() {
        Tag messageCorrelationId = processorService.getMessageCorrelationId();
        assertNull(messageCorrelationId);
    }

    @Test
    public void getPrimaryMessageId() {
        Tag primaryMessageId = processorService.getPrimaryMessageId();
        assertNull(primaryMessageId);
    }
}