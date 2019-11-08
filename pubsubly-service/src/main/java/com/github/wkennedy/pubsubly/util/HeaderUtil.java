package com.github.wkennedy.pubsubly.util;

import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.messaging.MessageHeaders;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.Objects;

//TODO clean up this class, make it smart
public class HeaderUtil {

    private static final String kafka_receivedTimestamp = "kafka_receivedTimestamp";
    private static final String jms_timestamp = "jms_timestamp";
    private static final String generic_timestamp = "timestamp";
    private static final String jms_destination = "jms_destination";
    private static final String kafka_receivedTopic = "kafka_receivedTopic";
    private static final String redis_messageSource = "redis_messageSource";

    public static Long getTimestamp(MessageHeaders messageHeaders) {
        if(messageHeaders.containsKey(kafka_receivedTimestamp)) {
            return (Long) Objects.requireNonNull(messageHeaders.get(kafka_receivedTimestamp));
        } else if(messageHeaders.containsKey(jms_timestamp)) {
            return (Long) Objects.requireNonNull(messageHeaders.get(jms_timestamp));
        } else if(messageHeaders.containsKey(generic_timestamp)) {
            return (Long) Objects.requireNonNull(messageHeaders.get(generic_timestamp));
        }
        return System.currentTimeMillis();
    }

    public static OffsetDateTime getOffsetDateTime(MessageHeaders messageHeaders) {
        Object timestamp = null;
        if(messageHeaders.containsKey(kafka_receivedTimestamp)) {
            timestamp = Objects.requireNonNull(messageHeaders.get(kafka_receivedTimestamp));
        } else if(messageHeaders.containsKey(jms_timestamp)) {
            timestamp = Objects.requireNonNull(messageHeaders.get(jms_timestamp));
        } else if(messageHeaders.containsKey(generic_timestamp)) {
            timestamp = Objects.requireNonNull(messageHeaders.get(generic_timestamp));
        }

        OffsetDateTime offsetDateTime;
        if(timestamp instanceof String) {
            try {
                offsetDateTime = OffsetDateTime.parse((String) Objects.requireNonNull(timestamp));
            } catch(DateTimeParseException e) {
                long publishedDataMillis = Long.parseLong((String) Objects.requireNonNull(timestamp));
                offsetDateTime = OffsetDateTime.ofInstant(Instant.ofEpochMilli(publishedDataMillis), ZoneId.systemDefault());
            }
        } else {
            offsetDateTime = OffsetDateTime.ofInstant(Instant.ofEpochMilli((Long) Objects.requireNonNull(timestamp)), ZoneId.systemDefault());
        }

        return offsetDateTime;
    }

    public static String getTopicName(MessageHeaders messageHeaders) {
        if(messageHeaders.containsKey(jms_destination)) {
            return ((ActiveMQTopic) Objects.requireNonNull(messageHeaders.get(jms_destination))).getPhysicalName();
        } else if(messageHeaders.containsKey(kafka_receivedTopic)) {
            return (String) messageHeaders.get(kafka_receivedTopic);
        } else if(messageHeaders.containsKey(redis_messageSource)) {
            return (String) messageHeaders.get(redis_messageSource);
        }

        return "Unknown Topic";
    }
}
