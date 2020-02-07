# Solace - Azure Service Bus Connector

## Overview
This repository contains a Gradle - Spring Boot project for bridging UTF8 TextMessages from Azure Service Bus to a Solace Broker and Vice versa.

### Warning

> :warning: This project is intended to serve as a POC for demonstrating the integration between a Solace Broker and the Azure Service Bus Only. Therefore, there are several opportunities for improvement.    
> :warning: Keep in mind that this code has not been tested or coded to be PRODUCTION ready.

## Prerequisites

Solace Broker
+ Broker URL
+ VPN on the broker
+ Topic to send the messages consumed from Az Service Bus 
+ Exclusive Queue that attracts the previous topic (for validating that messages are being received in Solace) - ex: Sol.AzToSolaceQueue
+ Exclusive Queue to store messages to be sent to Az Service Bus - ex: Sol.SolaceToAzQueue
+ Client-Username with permission to publish and send guaranteed messages, that owns the  SolaceToAzQueue queue or has permission to consume it

Azure Service Bus
+ Service Bus Namespace
+ Queue to receive messages from Solace (for validating that messages are being received in Az Service Bus) - ex: az.aztosolacequeue
+ Queue to store messages to be sent to Solace - ex: az.aztosolacequeue
+ Shared access policy with claims to Send & Listen to the previous queues
+ Shared access policy Connection String
 
### 

The previous values need to be set as properties on the application.properties file:

```
solace.java.host=tcp://solace01:55555
solace.java.msg-vpn=<vpn>
solace.java.client-username=<username>
solace.java.client-password=<password>

solace.connector.servicebus.connectionstring=Endpoint=sb://<SBNameSpace>.servicebus.windows.net/;SharedAccessKeyName=<PolicyName>;SharedAccessKey=<key>

solace.connector.solace.sourcequeuename=<QueueName>
solace.connector.servicebus.destqueuename=<QueueName>

solace.connector.servicebus.sourcequeuename=<QueueName>
solace.connector.solace.desttopicname=<TopicName>
```

## Checking out

To check out the project, clone this GitHub repository:

```
git clone https://github.com/solacese/github-demo
cd <github-demo>
```

## Building and running directly with gradlew

To have gradle build and run the application just after downloading this GIT Repository, you can simply run the following command on your console: 
 
```
$ ./gradlew bootRun
```
> :information_source: Remember to set appropriate values on the application.properties file before running the command

## Building a bootJar

To have gradle create a JAR containing all the compiled classes plus all the required libs, you can run the following command on your console: 
 
```
$ ./gradlew build
```

A JAR file named solace-azservicebus-connector will be created on the ./build/libs folder. 
Once the JAR has been created simply run it using the following command:

```
$ java -jar solace-azservicebus-connector.jar
```

> :information_source: In order to provide redundancy, multiple instances can be run at the same. Since the Sol.SolaceToAzure queue is an exclusive queue, only the first instance will actively consume messages from it.
> :warning: Since the application.properties is contained within the generated JAR, changes on the configuration values will require to rerun the  ./gradlew build command

## Authors

See the list of [contributors](https://github.com/solacese/solace-azservicebus-connector/graphs/contributors) who participated in this project.


## Resources

For more information try these resources:

- Get started with Service Bus queues at: https://docs.microsoft.com/en-us/azure/service-bus-messaging/service-bus-dotnet-get-started-with-queues 
- Spring Boot Integration with Azure Service Bus at: https://spring.io/blog/2019/01/14/bootiful-azure-integration-with-azure-service-bus-4-6
- Use Azure Service Bus queues with Java to send and receive messages at: https://docs.microsoft.com/en-us/azure/service-bus-messaging/service-bus-java-how-to-use-queues
- The Solace Developer Portal website at: http://dev.solace.com
- Get a better understanding of [Solace technology](http://dev.solace.com/tech/).
- Check out the [Solace blog](http://dev.solace.com/blog/) for other interesting discussions around Solace technology
- Ask the [Solace community.](http://dev.solace.com/community/)