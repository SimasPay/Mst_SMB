package com.mfino.vah.messaging;

import javax.jms.Connection;
import javax.jms.JMSException;

import org.apache.activemq.pool.PooledConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Sasi
 *
 */
public class VAJMSConnectionFactory {

	private static VAJMSConnectionFactory vaJmsConnectionFactory = null;
	
	private static Logger	log	= LoggerFactory.getLogger(VAJMSConnectionFactory.class);
	
	private String brokerUrl = "tcp://" + "localhost" + ":" + "61616";
	
	private PooledConnectionFactory pooledConnectionFactory = null;
	
	private VAJMSConnectionFactory() {
		pooledConnectionFactory = new PooledConnectionFactory(brokerUrl);
	}

	public String getBrokerUrl() {
		return brokerUrl;
	}

	public void setBrokerUrl(String brokerUrl) {
		this.brokerUrl = brokerUrl;
	}
	
	public Connection getConnection() throws JMSException{
		log.info("VAJMSConnectionFactory getConnection(*) called");
		Connection connection = pooledConnectionFactory.createConnection();
		log.info("VAJMSConnectionFactory getConnection(*->) connection="+connection);
		connection.start();
		return connection;
	}
	
	public static VAJMSConnectionFactory getConnectionFactory()
	{
		if(null == vaJmsConnectionFactory){
			vaJmsConnectionFactory =  new VAJMSConnectionFactory();
		}
		
		return vaJmsConnectionFactory;
	}
}
