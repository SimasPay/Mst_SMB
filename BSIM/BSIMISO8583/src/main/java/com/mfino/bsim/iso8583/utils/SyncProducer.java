package com.mfino.bsim.iso8583.utils;

import java.util.UUID;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.mce.core.MCEMessage;

public class SyncProducer {
	
	private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
	private static ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
	private static Logger log = LoggerFactory.getLogger(SyncProducer.class);
	
	public void produceMessage(String queueName, MCEMessage mceMessage, String transactionID){	 
        try{
        	log.info("Producing Message");
        	Connection connection = connectionFactory.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destQueue = session.createQueue(queueName);
            MessageProducer producer = session.createProducer(destQueue);
            ObjectMessage message = session.createObjectMessage(mceMessage);
            message.setJMSCorrelationID(transactionID);
            producer.send(message);
            log.info("Successfully Sent the message");
            producer.close();
            session.close();
            connection.close();          
        }catch(JMSException e){         
            log.info("Problem with ActiveMQ");  
            e.printStackTrace();
        } 
	}

	public MCEMessage consumeMessage(String queueName, String correlationID){	
		Message message = null;
		 MCEMessage mceMsg = null;
	 try{
            Connection connection = connectionFactory.createConnection();
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination queue = session.createQueue(queueName);
            String messgeSelector = "JMSCorrelationID = '" + correlationID + "'";
            MessageConsumer consumer = session.createConsumer(queue, messgeSelector);
           // MessageConsumer consumer = session.createConsumer(queue);
            log.info("Consumer waiting...");
            message = consumer.receive();
            if(message instanceof ObjectMessage){
             	ObjectMessage objMessage = (ObjectMessage)message;
             	log.info("JMSCorrelationID : " + objMessage.getJMSCorrelationID());
             	log.info(objMessage.getObject().getClass().getName());
             	mceMsg = (MCEMessage)objMessage.getObject();
             }else{
                 log.info("Received message of unknown type");
             }
            log.info("Consumer received");
            consumer.close();
            session.close();
            connection.close();
        }catch(JMSException e){         
            log.info("Problem creating connection to ActiveMQ");
            e.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }
       return mceMsg;
    }
	
	public void processReceivedMsg(Message message) throws JMSException, ISOException{
		ISOMsg isoMsg = null;
		if(message instanceof ObjectMessage){
         	ObjectMessage objMessage = (ObjectMessage)message;
         	isoMsg = (ISOMsg)objMessage.getObject();
         	log.info("JMSCorrelationID : " + objMessage.getJMSCorrelationID());
            log.info("Received : "+isoMsg.getMTI());
         }else{
             log.info("Received message of unknown type");
         }
	}
	
	public String getCorreleationID(){
		String corrleationID = UUID.randomUUID().toString();
		log.info(corrleationID);
		return corrleationID;
	}
	
	
}
