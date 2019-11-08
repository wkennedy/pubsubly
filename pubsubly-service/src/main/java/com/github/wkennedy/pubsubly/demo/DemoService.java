package com.github.wkennedy.pubsubly.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.wkennedy.pubsubly.config.KafkaProperties;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Profile("demo")
@EnableScheduling
@Component
public class DemoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DemoService.class);

    private static final String EVENT_ID = "eventId";
    private static final String EVENT_NAME = "eventName";
    private static final String CORRELATION_ID = "correlationId";

    private static final String USER_CREATED_EVENT = "USER_CREATED_EVENT";
    private static final String USER_SESSION_CREATED_EVENT = "USER_SESSION_CREATED_EVENT";
    private static final String USER_LEGACY_DATA_STORE_EVENT = "USER_LEGACY_DATA_STORE_EVENT";
    private static final String BID_CREATED_EVENT = "BID_CREATED_EVENT";
    private static final String ASK_CREATED_EVENT = "ASK_CREATED_EVENT";
    private static final String HIGH_BID_EVENT = "HIGH_BID_EVENT";
    private static final String LOW_BID_EVENT = "LOW_BID_EVENT";
    private static final String HIGH_ASK_EVENT = "HIGH_ASK_EVENT";
    private static final String LOW_ASK_EVENT = "LOW_ASK_EVENT";
    private static final String MATCH_EVENT = "MATCH_EVENT";
    private static final String REMOVE_BID_EVENT = "REMOVE_BID_EVENT";
    private static final String REMOVE_ASK_EVENT = "REMOVE_ASK_EVENT";
    private static final String ORDER_CREATED = "ORDER_CREATED";
    private static final String SHIP_EVENT = "SHIP_EVENT";

    private static final String SKU = "sku";
    private static final String YEEZY_SKU = "SKU-12345";

    private static final String ACTIVEMQ_TOPIC = "ACTIVEMQ_LEGACY_USER_DATA_TOPIC";
    private static final String KAFKA_BID_TOPIC = "DEMO-KAFKA_BID_TOPIC";
    private static final String KAFKA_ORDER_TOPIC = "DEMO-KAFKA_ORDER_TOPIC";
    private static final String KAFKA_USER_TOPIC = "DEMO-KAFKA_USER_TOPIC";
    private static final String REDIS_TOPIC = "REDIS-USER-SESSION";

    private final StringRedisTemplate stringRedisTemplate;

    private final JmsTemplate jmsTemplate;

    private final KafkaProperties kafkaProperties;

    private KafkaTemplate<?, ?> kafkaTemplate;

    private ObjectMapper objectMapper = new ObjectMapper();

    private Random randomGenerator = new Random();

    public DemoService(StringRedisTemplate stringRedisTemplate, JmsTemplate jmsTemplate, KafkaProperties kafkaProperties) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.jmsTemplate = jmsTemplate;
        this.kafkaProperties = kafkaProperties;
        this.kafkaTemplate = kafkaTemplate();
    }


    //Event flow
    //User created event - Kafka
        //user session created - Redis
        //user legacy store event - ActiveMQ
    //Bid created event - Kafka
        //new low bid event
        //new high bid event
    //bid created
        //new high bid event
    //new ask event
        //new low ask event
        //new high ask event
    //new ask event
        //match event
        //remove bid event
        //remove ask event
        //order created

    private void firstBidEvent() throws InterruptedException {
        //Bid created event - Kafka
        //new low bid event
        //new high bid event


        Map<String, Object> headers1 = new HashMap<>();
        headers1.put(EVENT_NAME, BID_CREATED_EVENT);
        String eventId1 = UUID.randomUUID().toString();
        headers1.put(EVENT_ID, eventId1);
        headers1.put(SKU, YEEZY_SKU);
        Message<String> bidCreatedMessage = MessageBuilder.createMessage("New bid entered: $100, user: Will, product: Adidas Yeezy", new MessageHeaders(headers1));
        kafkaTemplate.setDefaultTopic(KAFKA_BID_TOPIC);
        kafkaTemplate.send(bidCreatedMessage);

        long delay = randomGenerator.nextInt(2000) + 1000;
        Thread.sleep(delay);

        Map<String, Object> headers2 = new HashMap<>();
        headers2.put(EVENT_NAME, HIGH_BID_EVENT);
        String eventId2 = UUID.randomUUID().toString();
        headers2.put(EVENT_ID, eventId2);
        headers2.put(CORRELATION_ID, eventId1);
        headers2.put(SKU, YEEZY_SKU);
        Message<String> highBidEvent = MessageBuilder.createMessage("New high bid: $100, product: Adidas Yeezy", new MessageHeaders(headers2));
        kafkaTemplate.setDefaultTopic(KAFKA_BID_TOPIC);
        kafkaTemplate.send(highBidEvent);

        delay = randomGenerator.nextInt(2000) + 1000;
        Thread.sleep(delay);

        Map<String, Object> headers3 = new HashMap<>();
        headers3.put(EVENT_NAME, LOW_BID_EVENT);
        String eventId3 = UUID.randomUUID().toString();
        headers3.put(EVENT_ID, eventId3);
        headers3.put(CORRELATION_ID, eventId1);
        headers3.put(SKU, YEEZY_SKU);
        Message<String> lowBidEvent = MessageBuilder.createMessage("New low bid: $100, product: Adidas Yeezy", new MessageHeaders(headers3));
        kafkaTemplate.setDefaultTopic(KAFKA_BID_TOPIC);
        kafkaTemplate.send(lowBidEvent);
    }

    private void secondBidEvent() throws InterruptedException {
        //bid created
        //new high bid event
        long delay = randomGenerator.nextInt(2000) + 1000;
        Thread.sleep(delay);

        Map<String, Object> headers1 = new HashMap<>();
        headers1.put(EVENT_NAME, BID_CREATED_EVENT);
        String eventId1 = UUID.randomUUID().toString();
        headers1.put(EVENT_ID, eventId1);
        headers1.put(SKU, YEEZY_SKU);
        Message<String> bidCreatedMessage = MessageBuilder.createMessage("New bid entered: $200, user: Sloan, product: Adidas Yeezy", new MessageHeaders(headers1));
        kafkaTemplate.setDefaultTopic(KAFKA_BID_TOPIC);
        kafkaTemplate.send(bidCreatedMessage);

        delay = randomGenerator.nextInt(2000) + 1000;
        Thread.sleep(delay);

        Map<String, Object> headers2 = new HashMap<>();
        headers2.put(EVENT_NAME, HIGH_BID_EVENT);
        String eventId2 = UUID.randomUUID().toString();
        headers2.put(EVENT_ID, eventId2);
        headers2.put(CORRELATION_ID, eventId1);
        headers2.put(SKU, YEEZY_SKU);
        Message<String> highBidEvent = MessageBuilder.createMessage("New high bid: $200, product: Adidas Yeezy", new MessageHeaders(headers2));
        kafkaTemplate.setDefaultTopic(KAFKA_BID_TOPIC);
        kafkaTemplate.send(highBidEvent);
    }

    private void firstAskEvent() throws InterruptedException {
        //new ask event
        //new low ask event
        //new high ask event

        long delay = randomGenerator.nextInt(2000) + 1000;
        Thread.sleep(delay);

        Map<String, Object> headers1 = new HashMap<>();
        headers1.put(EVENT_NAME, ASK_CREATED_EVENT);
        String eventId1 = UUID.randomUUID().toString();
        headers1.put(EVENT_ID, eventId1);
        headers1.put(SKU, YEEZY_SKU);
        Message<String> askCreatedMessage = MessageBuilder.createMessage("New ask entered: $70, user: John, product: Adidas Yeezy", new MessageHeaders(headers1));
        kafkaTemplate.setDefaultTopic(KAFKA_BID_TOPIC);
        kafkaTemplate.send(askCreatedMessage);

        delay = randomGenerator.nextInt(2000) + 1000;
        Thread.sleep(delay);

        Map<String, Object> headers2 = new HashMap<>();
        headers2.put(EVENT_NAME, HIGH_ASK_EVENT);
        String eventId2 = UUID.randomUUID().toString();
        headers2.put(EVENT_ID, eventId2);
        headers2.put(CORRELATION_ID, eventId1);
        headers2.put(SKU, YEEZY_SKU);
        Message<String> highAskEvent = MessageBuilder.createMessage("New high ask: $70, product: Adidas Yeezy", new MessageHeaders(headers2));
        kafkaTemplate.setDefaultTopic(KAFKA_BID_TOPIC);
        kafkaTemplate.send(highAskEvent);

        delay = randomGenerator.nextInt(2000) + 1000;
        Thread.sleep(delay);

        Map<String, Object> headers3 = new HashMap<>();
        headers3.put(EVENT_NAME, LOW_ASK_EVENT);
        String eventId3 = UUID.randomUUID().toString();
        headers3.put(EVENT_ID, eventId3);
        headers3.put(CORRELATION_ID, eventId1);
        headers3.put(SKU, YEEZY_SKU);
        Message<String> lowAskEvent = MessageBuilder.createMessage("New low ask: $70, product: Adidas Yeezy", new MessageHeaders(headers3));
        kafkaTemplate.setDefaultTopic(KAFKA_BID_TOPIC);
        kafkaTemplate.send(lowAskEvent);
    }

    private void secondAskEvent() throws InterruptedException {
        //match event
        //remove bid event
        //remove ask event
        //order created

        long delay = randomGenerator.nextInt(2000) + 1000;
        Thread.sleep(delay);

        Map<String, Object> headers1 = new HashMap<>();
        headers1.put(EVENT_NAME, ASK_CREATED_EVENT);
        String eventId1 = UUID.randomUUID().toString();
        headers1.put(EVENT_ID, eventId1);
        headers1.put(SKU, YEEZY_SKU);
        Message<String> askCreatedMessage = MessageBuilder.createMessage("New ask entered: $100, user: Trevor, product: Adidas Yeezy", new MessageHeaders(headers1));
        kafkaTemplate.setDefaultTopic(KAFKA_BID_TOPIC);
        kafkaTemplate.send(askCreatedMessage);

        delay = randomGenerator.nextInt(2000) + 1000;
        Thread.sleep(delay);

        Map<String, Object> headers2 = new HashMap<>();
        headers2.put(EVENT_NAME, MATCH_EVENT);
        String eventId2 = UUID.randomUUID().toString();
        headers2.put(EVENT_ID, eventId2);
        headers2.put(CORRELATION_ID, eventId1);
        headers2.put(SKU, YEEZY_SKU);
        Message<String> highAskEvent = MessageBuilder.createMessage("Matched bid and ask: $100, product: Adidas Yeezy, Buyer: Trevor, Seller: Will", new MessageHeaders(headers2));
        kafkaTemplate.setDefaultTopic(KAFKA_BID_TOPIC);
        kafkaTemplate.send(highAskEvent);

        delay = randomGenerator.nextInt(2000) + 1000;
        Thread.sleep(delay);

        Map<String, Object> headers3 = new HashMap<>();
        headers3.put(EVENT_NAME, REMOVE_ASK_EVENT);
        String eventId3 = UUID.randomUUID().toString();
        headers3.put(EVENT_ID, eventId3);
        headers3.put(CORRELATION_ID, eventId2);
        headers3.put(SKU, YEEZY_SKU);
        Message<String> removeAskEvent = MessageBuilder.createMessage("Removing ask: $100, product: Adidas Yeezy", new MessageHeaders(headers3));
        kafkaTemplate.setDefaultTopic(KAFKA_BID_TOPIC);
        kafkaTemplate.send(removeAskEvent);

        delay = randomGenerator.nextInt(2000) + 1000;
        Thread.sleep(delay);

        Map<String, Object> headers4 = new HashMap<>();
        headers4.put(EVENT_NAME, REMOVE_BID_EVENT);
        String eventId4 = UUID.randomUUID().toString();
        headers4.put(EVENT_ID, eventId4);
        headers4.put(CORRELATION_ID, eventId2);
        headers4.put(SKU, YEEZY_SKU);
        Message<String> removeBidEvent = MessageBuilder.createMessage("Removing bid: $100, product: Adidas Yeezy", new MessageHeaders(headers4));
        kafkaTemplate.setDefaultTopic(KAFKA_BID_TOPIC);
        kafkaTemplate.send(removeBidEvent);

        delay = randomGenerator.nextInt(2000) + 1000;
        Thread.sleep(delay);

        Map<String, Object> headers5 = new HashMap<>();
        headers5.put(EVENT_NAME, ORDER_CREATED);
        String eventId5 = UUID.randomUUID().toString();
        headers5.put(EVENT_ID, eventId5);
        headers5.put(CORRELATION_ID, eventId2);
        headers5.put(SKU, YEEZY_SKU);
        Message<String> orderEvent = MessageBuilder.createMessage("Order created for Adidas Yeezy @ $100, User: Trevor", new MessageHeaders(headers5));
        kafkaTemplate.setDefaultTopic(KAFKA_ORDER_TOPIC);
        kafkaTemplate.send(orderEvent);
    }

    private void userEvents() throws InterruptedException, JsonProcessingException {
        long delay = randomGenerator.nextInt(5000) + 1000;
        //User created event - Kafka
            //user session created - Redis
            //user legacy store event - ActiveMQ
        Map<String, Object> headers1 = new HashMap<>();
        headers1.put(EVENT_NAME, USER_CREATED_EVENT);
        String eventId1 = UUID.randomUUID().toString();
        headers1.put(EVENT_ID, eventId1);
        Message<String> userCreatedMessage = MessageBuilder.createMessage("New user added", new MessageHeaders(headers1));
        kafkaTemplate.setDefaultTopic(KAFKA_USER_TOPIC);
        kafkaTemplate.send(userCreatedMessage);

        Thread.sleep(delay);

        Map<String, Object> headers2 = new HashMap<>();
        headers2.put(EVENT_NAME, USER_SESSION_CREATED_EVENT);
        String eventId2 = UUID.randomUUID().toString();
        headers2.put(EVENT_ID, eventId2);
        headers2.put(CORRELATION_ID, eventId1);
        Message<String> user_session_created_in_redis = MessageBuilder.createMessage("User session created in Redis", new MessageHeaders(headers2));
        stringRedisTemplate.convertAndSend(REDIS_TOPIC, objectMapper.writeValueAsString(user_session_created_in_redis));

        delay = randomGenerator.nextInt(5000) + 1000;
        Thread.sleep(delay);

        String eventId3 = UUID.randomUUID().toString();
        jmsTemplate.convertAndSend(ACTIVEMQ_TOPIC, "User data sent to ActiveMQ legacy topic", m -> {
            m.setStringProperty(EVENT_NAME, USER_LEGACY_DATA_STORE_EVENT);
            m.setStringProperty(EVENT_ID, eventId3);
            m.setStringProperty(CORRELATION_ID, eventId1);

            return m;
        });
    }

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        return new DefaultKafkaProducerFactory<>(kafkaProperties.buildProducerProperties());
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        KafkaTemplate<String, String> kafkaTemplate = new KafkaTemplate<>(producerFactory());
        kafkaTemplate.setDefaultTopic(KAFKA_BID_TOPIC);
        return kafkaTemplate;
    }

    @Scheduled(fixedRate = 60000)
    public void runUserEvents() {
        try {
            userEvents();
        } catch (Exception e) {
            LOGGER.error("Error sending message", e);
        }
    }

    @Scheduled(fixedRate = 60000)
    public void runBidAskEvents() {
        try {
            firstBidEvent();
            secondBidEvent();
            firstAskEvent();
            secondAskEvent();
        } catch (Exception e) {
            LOGGER.error("Error sending message", e);
        }
    }
}
