package com.solace.se.samples.azservicebus;

import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.microsoft.azure.servicebus.Message;
import com.microsoft.azure.servicebus.QueueClient;
import com.microsoft.azure.servicebus.ReceiveMode;
import com.microsoft.azure.servicebus.primitives.ConnectionStringBuilder;
import com.microsoft.azure.servicebus.primitives.ServiceBusException;

@Service
public class AzMsgPublisher implements InitializingBean {

	//private static final Logger logger = LoggerFactory.getLogger(AzMsgPublisher.class);
	
	//Azure Service Bus Connection String of a Shared access policy that has Send & Listen permissions
	@Value("${solace.connector.servicebus.connectionstring}")
	private String connectionString;

	//Name of the Queue to publish consumed messages from Solace to Azure Service Bus
	@Value("${solace.connector.servicebus.destqueuename}")
	private String azPubQueue;

    private QueueClient azQueuePublisher;

	//Initialize the Azure SB Publisher by creating a QueueClient to the azPubQueue after the Bean has been instantiated by Spring
	@Override
	public void afterPropertiesSet() throws Exception {

		azQueuePublisher = new QueueClient(new ConnectionStringBuilder(connectionString, azPubQueue) , ReceiveMode.PEEKLOCK);
		
	}
    
	public void sendToAzQueue(String textPayload) throws ServiceBusException, InterruptedException {
        final Message message = new Message(textPayload.getBytes(StandardCharsets.UTF_8));
        
        azQueuePublisher.send(message);
    }
}
