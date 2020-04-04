package com.github.wkennedy.pubsubly.plugins;

import com.github.wkennedy.pubsubly.api.PatternValueMonitor;
import com.github.wkennedy.pubsubly.api.Priority;
import com.github.wkennedy.pubsubly.api.SingleValueMonitor;
import com.github.wkennedy.pubsubly.api.Tag;
import com.github.wkennedy.pubsubly.services.ProcessorService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class HeaderValuePriorityProcessorPlugin {

    private final ProcessorService processorService;

    public HeaderValuePriorityProcessorPlugin(ProcessorService processorService) {
        this.processorService = processorService;
    }

    //TODO clean up this logic
    public Priority process(Map<String, String> headerKeyMap) {
        Map<String, Tag> tagsAsMap = processorService.getTagsAsMap();
        Set<String> keySet = headerKeyMap.keySet();
        Priority priority = Priority.NORMAL;
        for (String tagId : keySet) {
            Tag tag = tagsAsMap.get(tagId);
            String value = headerKeyMap.get(tagId);
            List<PatternValueMonitor> patternValueMonitors = tag.getPatternValueMonitors();
            if(patternValueMonitors != null && !patternValueMonitors.isEmpty()) {
                for (PatternValueMonitor patternValueMonitor : patternValueMonitors) {
                    Pattern pattern = Pattern.compile(patternValueMonitor.getValue());
                    Matcher matcher = pattern.matcher(value);
                    if(matcher.matches()) {
                        priority = Priority.getHighestPriority(priority, patternValueMonitor.getPriority());
                    }
                }
            }

            List<SingleValueMonitor> singleValueMonitors = tag.getSingleValueMonitors();
            if(singleValueMonitors != null && !singleValueMonitors.isEmpty()) {
                for (SingleValueMonitor singleValueMonitor : singleValueMonitors) {
                    if(singleValueMonitor.getValue().equals(value)) {
                        priority = Priority.getHighestPriority(priority, singleValueMonitor.getPriority());
                    }
                }
            }

            if(priority.isHighestOrder()) {
                return priority;
            }
        }

        return priority;
    }

}
