package com.solace.se.samples.azservicebus;


import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microsoft.azure.servicebus.primitives.ServiceBusException;
import com.solacesystems.jcsmp.JCSMPException;
import com.solacesystems.jcsmp.JCSMPStreamingPublishCorrelatingEventHandler;

public class SolacePublishEventHandler implements JCSMPStreamingPublishCorrelatingEventHandler {
    private static final Logger logger = LoggerFactory.getLogger(SolacePublishEventHandler.class);

    public void responseReceived(String messageID) {
        logger.info("Producer received response for msg: " + messageID);
        
    }

    public void handleError(String messageID, JCSMPException e, long timestamp) {
        logger.info("Producer received error for msg: %s@%s - %s%n", messageID, timestamp, e);
        
    }

	@Override
	public void responseReceivedEx(Object key) {
        try {
        
        	logger.info("Ex - Producer received response for msg: "+ (String)key);

        	//Use the Msg CorrelationKey to retrieve the Azure SB LockTocken from the Connector internal HashMap, and use it to ack the message back to Az Service Bus 
            UUID lockToken = SolaceMsgPublisher.getAzOriginalMessage((String) key);
			AzMsgConsumer.azQueueConsumer.complete(lockToken);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServiceBusException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//        originalMessage.ack();
		
	}
    
	@Override
	public void handleErrorEx(Object key, JCSMPException e, long timestamp) {
		
		logger.info("Ex - Producer received error for msg ID: [" +(String)key +"] :" , e);
	}

}