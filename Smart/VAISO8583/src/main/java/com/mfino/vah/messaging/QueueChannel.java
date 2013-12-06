package com.mfino.vah.messaging;

import java.util.UUID;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueueChannel {

	private static Logger	      log	= LoggerFactory.getLogger(QueueChannel.class);

	private final QueueConnection	senderConnection;
	private final QueueConnection	receiverConnection;
	private Connection connection = null;
	
	public QueueChannel(String senderQueue, String receiverQueueName) throws JMSException{
		this.connection = VAJMSConnectionFactory.getConnectionFactory().getConnection();
		this.senderConnection = new QueueConnection(this.connection, senderQueue);
		this.receiverConnection = new QueueConnection(this.connection, receiverQueueName);
	}
	
	public String requestAndReceive(String request) throws ChannelCommunicationException {
		try {

			log.info("sending message=" + request + " to queue=" + this.senderConnection.getQueueName());

			String syncRequestID = UUID.randomUUID().toString();
			TextMessage tMsg = this.senderConnection.getSession().createTextMessage();
			tMsg.setStringProperty("FrontendID", "137");
			tMsg.setStringProperty("synchronous_request_id", syncRequestID);
			tMsg.setStringProperty("frontendOutQ", "jms:smartCashinOutQueue?disableReplyTo=true");
			tMsg.setText(request);
			
			//create producer here and then send.
			this.senderConnection.getProducer().send(tMsg);

			return receiveResponse(syncRequestID);
		}
		catch (Exception ex) {
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
			log.info("waiting for response on response Q queueName="+this.receiverConnection.getQueueName());
			TextMessage tm = (TextMessage) consumer.receive(50000);
			log.info(tm == null ? null : tm.getText() + " response received in Queue for sync_request_id" + syncRequestID);
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
