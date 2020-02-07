package com.solace.se.samples.azservicebus;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.microsoft.azure.servicebus.ExceptionPhase;
import com.microsoft.azure.servicebus.IMessage;
import com.microsoft.azure.servicebus.IMessageHandler;
import com.microsoft.azure.servicebus.MessageBody;
import com.microsoft.azure.servicebus.MessageHandlerOptions;
import com.microsoft.azure.servicebus.QueueClient;
import com.microsoft.azure.servicebus.ReceiveMode;
import com.microsoft.azure.servicebus.primitives.ConnectionStringBuilder;
import com.solacesystems.jcsmp.JCSMPException;

@Service
public class AzMsgConsumer implements InitializingBean {

	private static final Logger logger = LoggerFactory.getLogger(AzMsgConsumer.class);
	
	//Azure Service Bus Connection String of a Shared access policy that has Send & Listen permissions
	@Value("${solace.connector.servicebus.connectionstring}")
	private String connectionString;

	//Name of the Queue to consume messages from Azure Service Bus  	
	@Value("${solace.connector.servicebus.sourcequeuename}")
	private String azConsumeQueue;

	@Autowired
	private SolaceMsgPublisher solPublisher;
	
    public static QueueClient azQueueConsumer;

	//Initialize the Azure SB Publisher by creating a QueueClient to the azConsumeQueue after the Bean has been instantiated by Spring
	@Override
	public void afterPropertiesSet() throws Exception {

		azQueueConsumer = new QueueClient(new ConnectionStringBuilder(connectionString, azConsumeQueue) , ReceiveMode.PEEKLOCK);
		
		ExecutorService executorService = Executors.newSingleThreadExecutor();
	
		azQueueConsumer.registerMessageHandler( new IMessageHandler() {
				// callback invoked when the message handler loop has obtained a message
		            public CompletableFuture<Void> onMessageAsync(IMessage message) {

		            	// receives message is passed to callback
		            		
		            		try {
			            		MessageBody body = message.getMessageBody();
			            		
			            		logger.error("body" + new String(body.getBinaryData().get(0) ,StandardCharsets.UTF_8) );
		            			
								solPublisher.sendSolaceMsg(message);
								
								return CompletableFuture.completedFuture(null);
								
							} catch (JCSMPException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								return null;
							}
		                    
				        }

		                // callback invoked when the message handler has an exception to report
		                public void notifyException(Throwable throwable, ExceptionPhase exceptionPhase) {
		                	logger.error("Az Queue Consumer " + exceptionPhase + "-" + throwable.getMessage());
				        }
		        } , new MessageHandlerOptions(1, false, Duration.ofSeconds(30)) ,executorService);
		
	}
}
