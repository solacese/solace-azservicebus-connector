package com.solace.se.samples.azservicebus;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.microsoft.azure.servicebus.IMessage;
import com.microsoft.azure.servicebus.MessageBody;
import com.solacesystems.jcsmp.DeliveryMode;
import com.solacesystems.jcsmp.JCSMPException;
import com.solacesystems.jcsmp.JCSMPFactory;
import com.solacesystems.jcsmp.JCSMPSession;
import com.solacesystems.jcsmp.SpringJCSMPFactory;
import com.solacesystems.jcsmp.TextMessage;
import com.solacesystems.jcsmp.Topic;
import com.solacesystems.jcsmp.XMLMessageProducer;

@Service
public class SolaceMsgPublisher implements InitializingBean, DisposableBean {

	private static final Logger logger = LoggerFactory.getLogger(SolaceMsgPublisher.class);

	//HashMap to keep track of the messages that have been consumed from Azure and Published PERSISTENT to Solace, but are pending to receive an ACK/NACK from Solace
	private static ConcurrentHashMap<String, UUID> inFlightMessages  = new ConcurrentHashMap<>();
	
	private JCSMPSession session;
	private XMLMessageProducer producer;
	private Topic topic;
	
	//Name of the Solace Topic where the Consumed messages from Azure Service Bus will get published
	@Value("${solace.connector.solace.desttopicname}")
	String solTopicName;

	@Autowired 
    private SpringJCSMPFactory solaceFactory;

	//Initialize the Solace Publisher by Establishing the connection and creating an XMLProducer after the Bean has been instantiated by Spring
	@Override
	public void afterPropertiesSet() throws Exception {
		
		SolacePublishEventHandler pubEventHandler = new SolacePublishEventHandler();
		
		session = solaceFactory.createSession();
		
		producer = session.getMessageProducer(pubEventHandler);
	
		topic = JCSMPFactory.onlyInstance().createTopic(solTopicName);
		
		logger.info("##### Solace Publisher Connected. Ready to publish #####");
	}

	public void sendSolaceMsg(IMessage message) throws JCSMPException {
		
		
		MessageBody body = message.getMessageBody();

		//Assume the message payload is UTF8 encoded text
		String textPayload = new String(body.getBinaryData().get(0) ,StandardCharsets.UTF_8) ;
		String pubsubMsgId = message.getMessageId();
		UUID lockToken = message.getLockToken();
		
		TextMessage jcsmpMsg = JCSMPFactory.onlyInstance().createMessage(TextMessage.class);
	    jcsmpMsg.setText(textPayload);
	    jcsmpMsg.setDeliveryMode(DeliveryMode.PERSISTENT);
	    
	    //Use the Azure message ID as a correlation Key to match ACKS/NACKS received by the Solace broker, before ACKing the message back to Azure SB
	    jcsmpMsg.setCorrelationKey(pubsubMsgId);
	
	    //Keep track of the messages that are awaiting publish confirmation from Solace
	    inFlightMessages.put(pubsubMsgId, lockToken);
	    logger.info("inFlightMessage ID stored :" + pubsubMsgId);
	    
	    logger.info("============= Sending message from Az Service Bus to Solace Topic ["+solTopicName+"] - ID [" + pubsubMsgId +"] - Payload [" + textPayload+ "]");
	    producer.send(jcsmpMsg, topic);
	    
	}
	
	public static UUID getAzOriginalMessage(String solMessageID) {
		return inFlightMessages.get(solMessageID);
	}
	
	
	@Override
	public void destroy() throws Exception {
		
		logger.info("Destroy - Close Solace publisher");
		
		// Close Publisher
        logger.info("Close Solace publisher - Exiting.");
        session.closeSession();
			
	}
}