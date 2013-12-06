package com.mfino.zenith.dstv.test;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Producer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.mfino.fix.CFIXMsg;
import com.mfino.mce.core.MCEMessage;

public class DSTVTest {

	public static void main(String[] args) throws Exception 
	{
		//CamelContext context = new DefaultCamelContext();
		ApplicationContext context = new FileSystemXmlApplicationContext("C:/code_new/mfs_2.5_new/Zenith/ZenithDSTV/src/test/resources/META-INF/spring/dstv_test_context.xml");
		CamelContext camel = (CamelContext) context.getBean("dstv-test");
		Endpoint endpoint = camel.getEndpoint("jms:wsTestCommunication?disableReplyTo=true");
		 // create the exchange used for the communication
	    // we use the in out pattern for a synchronized exchange where we expect a response
	    Exchange exchange = endpoint.createExchange(ExchangePattern.InOnly);
	    // set the input on the in body
	    // must you correct type to match the expected type of an Integer object
	    
	    MCEMessage mceMessage = new MCEMessage();
	    
	    System.out.println("*****************************************");
	    
//	    SubscriberRegistrationCashInTest testClass = new SubscriberRegistrationCashInTest();
//	    ChargeDistributionTest testClass = new ChargeDistributionTest();
//	    SettlementOfChargeTest testClass = new SettlementOfChargeTest();
//	    CashInInquiryTest testClass = new CashInInquiryTest();
//	    CashInTest testClass = new CashInTest();
//	    CashOutInquiryTest testClass = new CashOutInquiryTest();
//	    CashOutTest testClass = new CashOutTest();
//	    BalanceInquiryTest testClass = new BalanceInquiryTest();

//	    TransferInquiryTest testClass = new TransferInquiryTest();
	    
	    
//	    CFIXMsg requestFix = testClass.getMessage();
//	    mceMessage.setRequest(requestFix);
	    
	    
	    
	    exchange.getIn().setBody(mceMessage);

	    // to send the exchange we need an producer to do it for us
	    Producer producer = endpoint.createProducer();
	    // start the producer so it can operate
	    producer.start();

	    // let the producer process the exchange where it does all the work in this oneline of code
	    System.out.println("Sending Message");
	    producer.process(exchange);

	    // get the response from the out body and cast it to an integer
	    /*int response = exchange.getOut().getBody(Integer.class);
	    System.out.println("... the result is: " + response);

	    // stop and exit the client
	    producer.stop();*/
	}
}

