package com.solace.se.samples.azservicebus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.solacesystems.jcsmp.BytesXMLMessage;
import com.solacesystems.jcsmp.JCSMPException;
import com.solacesystems.jcsmp.XMLMessageListener;

public class SolaceMsgListener implements XMLMessageListener {

	private static final Logger logger = LoggerFactory.getLogger(SolaceMsgListener.class);
	
	private AzMsgPublisher azPublisher;
	
	public SolaceMsgListener(AzMsgPublisher azPublisher) {
		this.azPublisher = azPublisher;
	}
	
    public void onReceive(BytesXMLMessage msg) {

    	
    	try {
    		
    		logger.info("============= Received a message from SolaceQueue");
    		String PayloadAsText = new String(msg.getBytes());
    		logger.info("============= Sending message from Solace to Az Service Bus Queue - Payload ["+PayloadAsText+"]");

    		azPublisher.sendToAzQueue(PayloadAsText);
    		
	    	msg.ackMessage();
    	}
    	catch(Exception e) {
    		logger.error("Error while sending to Az Service Bus", e);
    	}
    }

    public void onException(JCSMPException e) {
        logger.info("Consumer received exception:", e);
    }

}

