package com.mfino.fep.messaging;

import javax.jms.Connection;
import javax.jms.JMSException;

import org.apache.activemq.pool.PooledConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Sasi
 *
 */
public class JMSConnectionFactory {

	private static JMSConnectionFactory fepJmsConnectionFactory = null;
	
	private static Logger	log	= LoggerFactory.getLogger(JMSConnectionFactory.class);
	
	private String brokerUrl = "tcp://" + "localhost" + ":" + "61616";
	
	private PooledConnectionFactory pooledConnectionFactory = null;
	
	private JMSConnectionFactory() {
		pooledConnectionFactory = new PooledConnectionFactory(brokerUrl);
	}

	public String getBrokerUrl() {
		return brokerUrl;
	}

	public void setBrokerUrl(String brokerUrl) {
		this.brokerUrl = brokerUrl;
	}
	
	public Connection getConnection() throws JMSException{
		log.info("FEPJMSConnectionFactory getConnection(*) called");
		Connection connection = pooledConnectionFactory.createConnection();
		log.info("FEPJMSConnectionFactory getConnection(*->) connection="+connection);
		connection.start();
		return connection;
	}
	
	public static JMSConnectionFactory getConnectionFactory()
	{
		if(null == fepJmsConnectionFactory){
			fepJmsConnectionFactory =  new JMSConnectionFactory();
		}
		
		return fepJmsConnectionFactory;
	}
}
