# pubsubly

![Java CI](https://github.com/wkennedy/pubsubly/workflows/Java%20CI/badge.svg?branch=master)

[![Build Status](https://travis-ci.com/wkennedy/pubsubly.svg?branch=master)](https://travis-ci.com/wkennedy/pubsubly)

## What is it?

Pubsubly is an application to track messages as they flow through a pub/sub backed message driven architecture.

It does this by allowing users to group messages by metadata through the use of pluggable Spring auto-configurations. As each message enters the system it is processed and grouped according to one of the user defined tags.

## Limitations

Currently, the only messaging platforms supported are Kafka, ActiveMQ and Redis. Another limitation is that the rendering of the message payload is limited as media types aren't currently supported. Probably the biggest limitation is that it's not scalable beyond one instance.

## Quickstart - Demo
If all you want to do is see a demo of the application, you can run Pubsubly in demo mode. 

First of all, you'll need to clone this project:

    git clone https://github.com/wkennedy/pubsubly.git

In order to run the platform all you need is Docker installed in order to use Docker Compose,

https://docs.docker.com/v17.09/engine/installation/

Then from pubsubly/docker run:

    docker-compose -f docker-compose-demo.yml up

Once everything is up and running you can navigate to http://localhost:3001 and see the Pubsubly UI interface.

## Quickstart - Local
First follow the build instructions for building pubsubly-api and pubsubly-service below ([How to build](#how-to-build)).
Once you clone the repo, if you don't already have access to Kafka brokers you can start Kafka with the docker-compose.yml file in pubsubly/docker:

    docker-compose up
    
If you aren't running Kafka locally, then you'll have update the application.yml with the broker URLs. In the application.yml in the pubsubly-service resources, update:

    spring:
      kafka:
        bootstrap-servers: localhost:9092    
        
Also update the topics you want to listen to either with a pattern OR topic names:

    kafka.topic.pattern: DEMO-.*

or

    kafka.topic.names:
    
Then you can start the server either through you Spring-Boot supported IDE or from the pubsubly-service root:

    mvn spring-boot:run
    
To run the pubsubly-ui go to the pubsubly-ui root and do:

    npm install
    npm run dev
    
Then navigate to localhost:3001 in your browser.

## Common issues

- If you have trouble with ports due to applications on your machine already utilizing those ports, please edit the ports in the docker-compose-demo.yml (or docker-compose.yml) file appropriately.

- One way to fix the offsets if they get screwed up in your container is to do the following:

  Get the container id of the Kafka service using

       docker ps
       
  Then login into that service with:

      docker exec -it <container id> /bin/bash
    
  Then go to usr/bin

      cd /usr/bin
     
  And execute

      ./kafka-consumer-groups --bootstrap-server localhost:9092 --reset-offsets --to-latest --group pubsubly-group --all-topics --execute

  You can also modify topics and delete consumer groups from here.

- If you are facing issues with Kafka brokers and old volume data and not all the data is showing up in the UI, try this:  

      docker-compose -f .\docker-compose-demo.yml up -d --force-recreate --renew-anon-volumes
  then
      docker-compose -f docker-compose-demo.yml up

  Then you might have to use volumes and uncomment the Zookeeper and Kafka volumes in the docker-compose-demo.yml file.

      #    volumes:
      #      - ./zk-single-kafka-single/zoo1/data:/data
      #      - ./zk-single-kafka-single/zoo1/datalog:/datalog
      and
      #    volumes:
      #      - ./zk-single-kafka-single/kafka1/data:/var/lib/kafka/data
   

## Demo Walk-through
This demo demonstrates the use of 3 different messaging platforms and the ability of Pubsubly to track the correlation of messages across the different systems (Kafka, Redis, ActiveMQ).

The demo runs a basic simulator that produces events. The event chains are triggered every 60 seconds and they simulate the creation of a user, the user getting a session stored in Redis and the sending of user data to a legacy system using ActiveMQ.
The simulation also shows a series of bids and asks on a product, eventually resulting in a match which creates an order.

![Image](images/home_page.PNG?raw=true)
This image shows the home page. The home page consists of a bar graph that shows the topics and the amount of messages from each topic. It also has a bubble chart that depicts the amount of messages processed per day per hour. This is to bring any attention to times of the day where message flow is high.
The "Latest messages" section is a live display of the last 50 messages processed. The charts do not automatically update, so the page needs to be refreshed to reflect the latest numbers. 


Click on the menu button and select "Topics". This will bring you to a list of all the topics available.

![Image](images/topics.PNG?raw=true)

The topics page displays the topic names along with the amount of messages processed for each topic. If you click the "eye" icon, you'll be able to see messages processed from that topic. Go ahead and click on "DEMO-KAFKA_ORDER_TOPIC".

![Image](images/topic.PNG?raw=true)

In the topic page for the "DEMO-KAFKA_ORDER_TOPIC" you can view all the order events with the newest order first. If you click the drop down error to the left of the message you can traverse through the message headers and payload. Now let's click on the correlationId values for one of the orders. This will allow us to view all the messages all the way to the message that was the root cause.

![Image](images/tags.PNG?raw=true)

The "Message Hops for correlationId" shows the topics and time from the root cause message to the final event. The "Time Since Last Topic" displays the time between when the correlationId was last seen in a topic.
The "Message for correlationId" displays the message starting with the root cause message and the events triggered by it.

Now let's go to the menu in the upper left corner and click the search button in order to go to the search screen.

![Image](images/search.PNG?raw=true)

The search page allows you to search by specific tag values, header keys and values, or text within the payload. In this example we performed a search for messages that contained the text "Yeezy".


## How to build

#### Configuration

Configuration for the pubsubly-service is done through the application.yml file in pubsubly-service main resources (pubsubly\pubsubly-service\src\main\resources\application.yml).

Configurations for Kafka, ActiveMQ, and Redis utilize Spring's autoconfigurations for these services, so use the Spring common applications to configure your application for those platforms. The properties can be found here:

https://docs.spring.io/spring-boot/docs/current/reference/html/appendix-application-properties.html

Kafka:  In order to listen to topics on Kafka, you can set the topics either as a pattern or a list of topics:

    kafka.topic.pattern: <PREFIX>.*
    or
    kafka.topic.names: TOPIC1,TOPIC2

Redis:  Redis topic pattern configuration is (configure one or the other):

    redis.topic.pattern: REDISTOPIC
    or
    redis.topic.names: REDISTOPIC1,REDISTOPIC2
    
For Redis also remove this from the autoconfigure exclude:

    org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration

ActiveMQ:  ActiveMQ requires a comma separated list:

    activemq.topic.names: TOPIC1,TOPIC2
        
The main configuration for Pubsubly is setting the plugin-processors. The processors are used to define what data you want to track. Currently there is only one built-in plugin, which is the HeaderProcessorPlugin. It allows you to track messages by specified header values.

    plugin-processors:
      tags:
        - id: eventId
          value: eventId
          description: This is the event ID
          isPrimaryMessageId: true
          display: false
        - id: correlationId
          value: correlationId
          description: This is the correlation ID
          isMessageCorrelationId: true
        - id: eventName
          value: eventName
          description: This is the event name
        - id: sku
          value: sku
          description: ID of the product
      processors:
        - id: headerProcessorPlugin
        - id: redisMessageHeaderProcessorPlugin

Let's go over the demo configuration as an example. The tags have the following properties:

- id: this is a unique identifier for the tag and is used internally for tracking messages
- value: this is the value in the message that you want to tag and track, in this example it's a header key.
- description: this is the description of the data you are tagging. It can be used for clients such as the UI.
- display: whether this tag should display in the UI
- isPrimaryMessageId: specifies if this value is used a the unique identification of the message
- isMessageCorrelationId: this is the id of the event that caused this message to be raised. Allows for tracking the root cause of the event.

The processors are the plugins you want to apply the tags too. In this example we have to processors, the headerProcessorPlugin and the redisMessageHeaderProcessorPlugin. The redisMessageHeaderProcessorPlugin is a simple example of creating a plugin for custom tag processing.
The idea of the redisMessageHeaderProcessorPlugin is to support sending a message with headers and a payload to Redis and be able to analyze the headers, even though headers aren't supported in Redis.

#### pubsubly-api
In order to build the pubsubly-api you need Java 8 (and set JAVA_HOME) or above installed and Maven. In the pubsubly-api (pubsubly/pubsubly-api) root execute:

    mvn install

If you don't or can't install Maven, you can use the included Maven wrapper:

    ./mvnw install
    
#### pubsubly-service
NOTE: the pubsubly-api is not yet pushed to Maven Central, so you need to build it yourself before building pubsubly-service. Go to pubsubly/pubsubly-api and do:

    mvn install

If you don't or can't install Maven, you can use the included Maven wrapper:

    ./mvnw install
   

Now build pubsubly-service. In order to build the pubsubly-service you need Java 8 (and set JAVA_HOME) or above installed and Maven. In the pubsubly-service (pubsubly/pubsubly-service) root execute:

    mvn install

If you don't or can't install Maven, you can use the included Maven wrapper:

    ./mvnw install

From here you can build your own docker images. In the pubsubly/pubsubly-service directory execute (replace <YOUR PREFIX> with your docker hub name):

     docker build -t <YOUR PREFIX>/pubsubly-service .

#### pubsubly-ui
In order to use the UI you need NodeJS installed (https://nodejs.org/en/download/). You don't have to build the UI in order to run it. From the pubsubly/pubsubly-ui directory you can execute:

    npm run dev

Please note that you'll need the pubsubly-service running in order for the UI to operate correctly.

From here you can build your own docker images. In the pubsubly/pubsubly-ui directory execute (replace <YOUR PREFIX> with your docker hub name):

     docker build -t <YOUR PREFIX>/pubsubly-ui .

## How to run

Now that your docker images are built, you can update the docker-compose.yml file in pubsubly/docker with your image names. For example, 

    pubsubly-service:
      image: "<YOUR PREFIX>/pubsubly-service"
    pubsubly-ui:
      image: "<YOUR PREFIX>/pubsubly-ui"

Once that's done you can run:

    docker-compose up

and then navigate to http://localhost:3001 in your browser.

## Custom Plugins

Using the pubsubly-api you can create a new plugin to process message in order to track based on specific data. For an example see: https://github.com/wkennedy/pubsubly/tree/master/redis-message-header-plugin

There are three basic parts to the plugin:

1. Create a plugin that implements com.github.wkennedy.pubsubly.api.Processor. The processor will return the value that you want to track. In the case of RedisMessageHeaderProcessorPlugin we are looking header values that are actually in the payload.

2. Create a @Configuration for your plugin. The bean name will be used as the id for your plugin-processors -> processor configuration as mentioned above.

3. Create spring.factories in /resources/META-INF of your project. This is used by Spring to autoconfigure your plugin when you include it as a dependency in the pubsubly-service.

        org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
        com.github.wkennedy.pubsubly.rmhpp.plugins.RedisMessageHeaderProcessorConfig
        
## Internal processing overview

![Image](images/pubsubly-service-processing.png?raw=true)

## Useful Endpoints

http://localhost:9000/swagger-ui.html

This is the swagger endpoint to display the API provided by the pubsubly-service.
