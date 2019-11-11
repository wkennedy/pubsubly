package com.github.wkennedy.pubsubly.services;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.wkennedy.pubsubly.api.Tag;
import com.github.wkennedy.pubsubly.models.Link;
import com.github.wkennedy.pubsubly.models.MessageBundle;
import com.github.wkennedy.pubsubly.models.MessageDetails;
import com.github.wkennedy.pubsubly.models.MessageFlow;
import com.github.wkennedy.pubsubly.models.MessageFlowStopover;
import com.github.wkennedy.pubsubly.models.MessageResource;
import com.github.wkennedy.pubsubly.models.MessageResourceBundle;
import com.github.wkennedy.pubsubly.models.Node;
import com.github.wkennedy.pubsubly.util.HeaderUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.github.wkennedy.pubsubly.util.HeaderUtil.getTimestamp;
import static com.github.wkennedy.pubsubly.util.HeaderUtil.getTopicName;

@Service
public class MessageService {

    private final Cache<String, MessageResource> messageCache;

    private final Map<String, Cache<String, MessageBundle>> cacheMap;

    private final Queue<MessageResource> latestMessageCache;

    private final ProcessorService processorService;

    private final Cache<String, List<String>> topicCache;

    public MessageService(Cache<String, MessageResource> messageCache, @Qualifier("cacheMap") Map<String, Cache<String, MessageBundle>> cacheMap,
                          @Qualifier("latestMessageCache") Queue<MessageResource> latestMessageCache, ProcessorService processorService, Cache<String, List<String>> topicCache) {
        this.messageCache = messageCache;
        this.cacheMap = cacheMap;
        this.latestMessageCache = latestMessageCache;
        this.processorService = processorService;
        this.topicCache = topicCache;
    }

    public MessageResourceBundle messageResources(String key, String value) {
        Cache<String, MessageBundle> cache = cacheMap.get(key);
        MessageBundle messageBundle = cache.getIfPresent(value);
        List<MessageResource> messages = getMessageResources(Objects.requireNonNull(messageBundle).getMessageUUIDs());
        return new MessageResourceBundle(messageBundle.getTag(), sortByPublishedDateReversed(messages));
    }

    public MessageDetails messageDetails(String key, String value) {
        if(StringUtils.isEmpty(key) || StringUtils.isEmpty(value)) {
            return new MessageDetails();
        }
        Cache<String, MessageBundle> cache = cacheMap.get(key);
        MessageBundle messageBundle = cache.getIfPresent(value);
        List<MessageResource> messages = getMessageResources(Objects.requireNonNull(messageBundle).getMessageUUIDs());
        Set<MessageResource> correlatedMessages = messageDetailsForCorrelation(key, messages);
        messages.addAll(correlatedMessages);
        MessageDetails messageDetails = new MessageDetails();
        List<MessageResource> sortedMessages = sortByPublishedDate(messages);
        MessageResourceBundle messageResourceBundle = new MessageResourceBundle(messageBundle.getTag(), sortedMessages);
        messageDetails.setMessageResourceBundle(messageResourceBundle);
        messageDetails.setAverageTimeBetweenTopics(getAverageTimeBetweenTopics(sortedMessages));
        messageDetails.setMessageFlow(getMessageFlow(key, value, sortedMessages));

        return messageDetails;
    }

    private Set<MessageResource> messageDetailsForCorrelation(String key, List<MessageResource> messages) {
        Tag primaryIdTag = processorService.getPrimaryMessageId();
        Tag correlationIdTag = processorService.getMessageCorrelationId();

        if(primaryIdTag == null || correlationIdTag == null) {
            return new HashSet<>();
        }

        Set<MessageResource> correlatedMessages = new HashSet<>();
        if (key.equals(primaryIdTag.getId())) {
            for (MessageResource message : messages) {
                List<MessageResource> messageResources = messageDetailsForPrimaryId(message, new ArrayList<>());
                correlatedMessages.addAll(messageResources);
            }
        } else if (key.equals(correlationIdTag.getId())) {
            for (MessageResource message : messages) {
                List<MessageResource> messageResources = messageDetailsForCorrelationId(message, new ArrayList<>());
                correlatedMessages.addAll(messageResources);
            }
        }

        return correlatedMessages;
    }

    private List<MessageResource> messageDetailsForCorrelationId(MessageResource messageResource, List<MessageResource> correlatedMessageResources) {
        Tag primaryIdTag = processorService.getPrimaryMessageId();
        Tag correlationIdTag = processorService.getMessageCorrelationId();

        if (correlatedMessageResources == null) {
            correlatedMessageResources = new ArrayList<>();
        }

        if (messageResource == null || messageResource.getMessageKeyValue(correlationIdTag.getId()) == null || correlatedMessageResources.size() > 100) {
            return correlatedMessageResources;
        }

        if (primaryIdTag != null) {
            Cache<String, MessageBundle> primaryIdCache = cacheMap.get(primaryIdTag.getId());
            String correlationId = messageResource.getMessageKeyValue(correlationIdTag.getId());

            MessageBundle parentMessageBundle = primaryIdCache.getIfPresent(correlationId);
            if (parentMessageBundle != null) {
                List<MessageResource> messageResources = getMessageResources(parentMessageBundle.getMessageUUIDs());
                correlatedMessageResources.addAll(messageResources);
                for (MessageResource resource : messageResources) {
                    messageDetailsForCorrelationId(resource, correlatedMessageResources);
                }
            }
        }

        return correlatedMessageResources;
    }

    private List<MessageResource> messageDetailsForPrimaryId(MessageResource messageResource, List<MessageResource> correlatedMessageResources) {
        if (correlatedMessageResources == null) {
            correlatedMessageResources = new ArrayList<>();
        }

        if (messageResource == null || correlatedMessageResources.size() > 100) {
            return correlatedMessageResources;
        }

        Tag primaryIdTag = processorService.getPrimaryMessageId();
        Tag correlationIdTag = processorService.getMessageCorrelationId();

        if (correlationIdTag != null) {
            Cache<String, MessageBundle> correlationIdCache = cacheMap.get(correlationIdTag.getId());
            String primaryId = messageResource.getMessageKeyValue(primaryIdTag.getId());

            MessageBundle childMessageBundle = correlationIdCache.getIfPresent(primaryId);
            if (childMessageBundle != null) {
                List<MessageResource> messageResources = getMessageResources(childMessageBundle.getMessageUUIDs());
                correlatedMessageResources.addAll(messageResources);
                for (MessageResource resource : messageResources) {
                    messageDetailsForPrimaryId(resource, correlatedMessageResources);
                }
            }
        }

        return correlatedMessageResources;
    }

    public List<MessageResource> searchMessageResourcesByPayload(String searchValue) {
        Collection<MessageResource> values = messageCache.asMap().values();
        List<MessageResource> messageResources = new ArrayList<>();
        for (MessageResource value : values) {
            if (value.getMessage().getPayload() instanceof String) {
                if (((String) value.getMessage().getPayload()).contains(searchValue)) {
                    messageResources.add(value);
                }
            }
        }

        return messageResources;
    }

    public List<MessageResource> searchMessageResources(String headerKey, String searchValue) {
        Collection<MessageResource> values = messageCache.asMap().values();
        List<MessageResource> messageResources = new ArrayList<>();
        for (MessageResource value : values) {
            if (value.getMessage().getHeaders().containsKey(headerKey)) {
                Object headerValue = value.getMessage().getHeaders().get(headerKey);
                if (headerValue instanceof String) {
                    if (((String) headerValue).contains(searchValue)) {
                        messageResources.add(value);
                    }
                }
            }
        }

        return messageResources;
    }

    public List<MessageResource> getMessageResources(Collection<String> messageUUIDs) {
        List<MessageResource> messages = new ArrayList<>();
        if (messageUUIDs != null) {
            for (String messageUUID : messageUUIDs) {
                messages.add(messageCache.getIfPresent(messageUUID));
            }
        }
        return messages;
    }

    public List<MessageResource> getMessageResources(Collection<String> messageUUIDs, Integer index, Integer size) {
        List<MessageResource> messages = new ArrayList<>();
        List<MessageResource> messageResources = getMessageResources(messageUUIDs);
        messageResources = sortByPublishedDateReversed(messageResources);
        int lastIndex = index + size;
        if (lastIndex > messageResources.size()) {
            lastIndex = messageResources.size();
        }
        for (int i = index; i < lastIndex; i++) {
            messages.add(messageResources.get(i));
        }
        return messages;
    }

    private List<MessageResource> sortByPublishedDateReversed(Collection<MessageResource> messageResources) {
        return messageResources.stream().sorted(Collections.reverseOrder(Comparator.comparing(messageResource -> {
            Message message = messageResource.getMessage();
            return (Long) Objects.requireNonNull(getTimestamp(message.getHeaders()));
        }))).collect(Collectors.toList());
    }

    private List<MessageResource> sortByPublishedDate(List<MessageResource> messageResources) {
        return messageResources.stream().sorted(Comparator.comparing(messageResource -> {
            Message message = messageResource.getMessage();
            return (Long) Objects.requireNonNull(getTimestamp(message.getHeaders()));
        })).collect(Collectors.toList());
    }

    private Long getAverageTimeBetweenTopics(List<MessageResource> sortedMessages) {
        if (sortedMessages.isEmpty()) {
            return 0L;
        }
        Long previousTime = null;
        long averageTime = 0L;
        for (MessageResource sortedMessage : sortedMessages) {
            Message message = sortedMessage.getMessage();
            Long currentTime = getTimestamp(message.getHeaders());
            if (previousTime != null) {
                averageTime += (currentTime - previousTime);
            }
            previousTime = currentTime;
        }

        return averageTime / sortedMessages.size();
    }

    private MessageFlow getMessageFlow(String type, String id, List<MessageResource> sortedMessages) {
        List<MessageFlowStopover> messageFlowDetails = new ArrayList<>();
        MessageFlow messageFlow = new MessageFlow();
        for (int i = 0; i < sortedMessages.size(); i++) {
            Message sortedMessage = sortedMessages.get(i).getMessage();
            MessageFlowStopover messageFlowStopover = new MessageFlowStopover();
            String topicName = getTopicName(sortedMessage.getHeaders());
            messageFlowStopover.setTopic(topicName);
            messageFlow.getNodes().add(new Node(topicName));
            messageFlowStopover.setTimestamp(getTimestamp(sortedMessage.getHeaders()));
            long lastTime = 0L;
            if (i != 0) {
                Message previousMessage = sortedMessages.get(i - 1).getMessage();
                if (previousMessage != null) {
                    String previousTopicName = getTopicName(previousMessage.getHeaders());
                    lastTime = getTimestamp(sortedMessage.getHeaders()) - getTimestamp(previousMessage.getHeaders());
                    messageFlowStopover.setPreviousTopic(previousTopicName);
                    if (!previousTopicName.equals(topicName)) {
                        Link link = new Link(previousTopicName, topicName);
                        messageFlow.getLinks().add(link);
                    }
                }
            }

            messageFlowStopover.setTimeSinceLastTopic(Long.toString(lastTime));
            messageFlowDetails.add(messageFlowStopover);
        }

        messageFlow.setId(id);
        messageFlow.setType(type);
        messageFlow.setMessageFlowStopovers(messageFlowDetails);
        return messageFlow;
    }

    public Flux<MessageResource> streamMessages() {
        return Flux.fromIterable(sortByPublishedDateReversed(latestMessageCache));
    }

    public Map<Map<String, Object>, List<MessageResource>> getMessageWithHeaderValues(Set<String> headerKeys, Long earliestTimeInMillis) {
        ConcurrentMap<String, MessageResource> messageResourceConcurrentMap = messageCache.asMap();
        Map<Map<String, Object>, Set<String>> results = new HashMap<>();

        if(earliestTimeInMillis == null) {
            earliestTimeInMillis = 86400000L;
        }

        Map<Map<String, Object>, List<MessageResource>> groupedMessages = new HashMap<>();
        for (String messageUUID : messageResourceConcurrentMap.keySet()) {
            MessageResource messageResource = messageResourceConcurrentMap.get(messageUUID);
            MessageHeaders messageHeaders = messageResource.getMessage().getHeaders();

            if (messageHeaders != null) {
                Long messageTime = HeaderUtil.getTimestamp(messageHeaders);
                if(messageTime < (System.currentTimeMillis() - earliestTimeInMillis)) {
                    continue;
                }
                Map<String, Object> collect = headerKeys.stream().filter(messageHeaders::containsKey).collect(Collectors.toMap(Function.identity(), messageHeaders::get));
                Set<String> messageUUIDS = results.get(collect);
                if (messageUUIDS != null) {
                    messageUUIDS.add(messageUUID);
                } else {
                    messageUUIDS = new HashSet<>();
                    messageUUIDS.add(messageUUID);
                    results.put(collect, messageUUIDS);
                }
            }
        }

        for (Map<String, Object> groupByKey : results.keySet()) {
            Set<String> messageUUIDS = results.get(groupByKey);
            if (messageUUIDS.size() > 1) {
                groupedMessages.put(groupByKey, getMessageResources(messageUUIDS));
            }
        }

        return groupedMessages;
    }

}
