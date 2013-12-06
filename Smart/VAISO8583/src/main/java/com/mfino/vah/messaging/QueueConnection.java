package com.mfino.vah.messaging;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;

public class QueueConnection {

	protected String	    queueName	= "CIQueue";
	private final Session	session;
	private Queue	        queue;
	private Connection connection;
	
	public QueueConnection(Connection connection, String queue) throws JMSException {
		this.connection = connection;
		this.queueName = queue;
		this.session = this.connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		this.queue = this.session.createQueue(this.queueName);
	}

	public String getQueueName() {
		return queueName;
	}

	public Session getSession() {
		return session;
	}

	public Queue getQueue() {
		return queue;
	}
	
	public MessageProducer getProducer() throws JMSException {
		MessageProducer	producer = this.session.createProducer(this.queue);
		producer.setDeliveryMode(DeliveryMode.PERSISTENT);

		return producer;		
	}
	
	public MessageConsumer getConsumer() throws JMSException {
		MessageConsumer	consumer = this.session.createConsumer(this.queue);
		return consumer;		
	}
	
	public MessageConsumer getConsumer(String synchronousRequestId) throws JMSException {
		MessageConsumer consumer = this.session.createConsumer(this.queue, "synchronous_request_id = '" + synchronousRequestId + "'");
		return consumer;		
	}	
}
