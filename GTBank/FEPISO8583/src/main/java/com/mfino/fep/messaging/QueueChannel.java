package com.mfino.fep.messaging;

import java.io.Serializable;
import java.util.UUID;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.fep.FEPConfiguration;

public class QueueChannel {

	private static Logger	      log	= LoggerFactory.getLogger(QueueChannel.class);

	private final QueueConnection	senderConnection;
	private final QueueConnection	receiverConnection;
	private Connection connection = null;
	
	public QueueChannel(String senderQueue) throws JMSException{
		this.connection = JMSConnectionFactory.getConnectionFactory().getConnection();
		this.senderConnection = new QueueConnection(this.connection, senderQueue);
		this.receiverConnection = new QueueConnection(this.connection, "fepReplyQueue");
	}
	
	public String requestAndReceive(Serializable request) throws ChannelCommunicationException {
		try {
			
			String syncRequestID = UUID.randomUUID().toString();
			ObjectMessage tMsg = this.senderConnection.getSession().createObjectMessage();
			tMsg.setStringProperty("FrontendID", FEPConfiguration.getFrontEndID());
			tMsg.setStringProperty("synchronous_request_id", syncRequestID);
//			tMsg.setStringProperty("frontendOutQ", "jms:smartCashinOutQueue?disableReplyTo=true");
			tMsg.setObject(request);
			
			log.info("sending request:"+request.toString()+" to Queue:"+this.senderConnection.getQueueName()+" with sync_request_id:" + syncRequestID);			
			//create producer here and then send.
			this.senderConnection.getProducer().send(tMsg);

			return receiveResponse(syncRequestID);
		}catch (Exception ex) {
			log.error("sending message=" + request + " to queue=" + this.senderConnection.getQueueName() + " failed.");
			ChannelCommunicationException e = new ChannelCommunicationException(ex);
			e.fillInStackTrace();
			throw e;
		}
	}

	private String receiveResponse(String syncRequestID) throws Exception {
		try 
		{
			MessageConsumer consumer = this.receiverConnection.getConsumer(syncRequestID);
			TextMessage tm = (TextMessage) consumer.receive(50000);
			log.info(tm == null ? null : tm.getText() + " <--response received in Queue for sync_request_id:" + syncRequestID);
			consumer.close();
			return tm == null ? null : tm.getText();
		}
		catch (Exception ex) {
			log.error("exception occured while receiving response." + ex.getMessage());
			throw ex;
		}
	}

	public void close() throws JMSException{
		if(null != connection){
			connection.close();
		}
	}
}
