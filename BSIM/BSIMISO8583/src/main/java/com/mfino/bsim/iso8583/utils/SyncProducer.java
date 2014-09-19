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
import com.mfino.util.ConfigurationUtil;

public class SyncProducer {
	
	private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
	private static ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
	private static Logger log = LoggerFactory.getLogger(SyncProducer.class);
	
	public void produceMessage(String queueName, MCEMessage mceMessage, String transactionID) {	 
		Session session=null;
		MessageProducer producer=null;
		Connection connection=null;
		try{
			log.info("Producing Message");
			connection = connectionFactory.createConnection();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Destination destQueue = session.createQueue(queueName);
			producer = session.createProducer(destQueue);
			ObjectMessage message = session.createObjectMessage(mceMessage);
			message.setJMSCorrelationID(transactionID);
			producer.send(message);
			log.info("Successfully Sent the message");          
		}catch(JMSException e){         
			log.info("Problem with ActiveMQ" + e.getMessage());  
		}finally{
			try{
				if(null!=producer){
					producer.close();
				}
				if(null!=session){
					session.close();
				}
				if(null!=connection){
					connection.close();
				}
			}catch(Exception e){
				log.info("SyncProducer ::  Error in produceMessage() "+e.getMessage());
			}
		}
	}

	public MCEMessage consumeMessage(String queueName, String correlationID) {	
		Message message = null;
		MCEMessage mceMsg = null;
		Session session=null;
		MessageConsumer consumer=null;
		Connection connection=null;
		try{
			connection = connectionFactory.createConnection();
			connection.start();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Destination queue = session.createQueue(queueName);
			String messgeSelector = "JMSCorrelationID = '" + correlationID + "'";
			consumer = session.createConsumer(queue, messgeSelector);
			// MessageConsumer consumer = session.createConsumer(queue);
			log.info("Consumer waiting...");
			// Time out on Consumer so that it doesnt wait indefinitely
			Long timeout = ConfigurationUtil.getATMRequestTimeout();
			message = consumer.receive(timeout);
			if(null==message){
				log.error("SyncProducer :: consumeMessage Didnt receive message in "+timeout+ " milliseconds time" );
				throw new Exception();
			}
			if(message instanceof ObjectMessage){
				ObjectMessage objMessage = (ObjectMessage)message;
				log.info("jmsCorrelationID : " + objMessage.getJMSCorrelationID());
				log.info(objMessage.getObject().getClass().getName());
				mceMsg = (MCEMessage)objMessage.getObject();
			}else{
				log.info("Received message of unknown type");
			}
			log.info("Consumer received");
		}catch(JMSException e){         
			log.info("Problem creating connection to ActiveMQ"+e.getMessage());
		}catch(Exception e){
			log.info("Exception occured while connecting to ActiveMQ"+e.getMessage());
		}finally{
			try{
				if(null!=consumer){
					consumer.close();
				}
				if(null!=session){
					session.close();
				}
				if(null!=connection){
					connection.close();
				}
			}catch(Exception e){
				log.info("SyncProducer ::  Error in consumeMessage() "+e.getMessage());
			}
		}
		return mceMsg;
	}
	
	public void processReceivedMsg(Message message) throws JMSException, ISOException{
		ISOMsg isoMsg = null;
		if(message instanceof ObjectMessage){
         	ObjectMessage objMessage = (ObjectMessage)message;
         	isoMsg = (ISOMsg)objMessage.getObject();
         	log.info("jmsCorrelationID : " + objMessage.getJMSCorrelationID());
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
