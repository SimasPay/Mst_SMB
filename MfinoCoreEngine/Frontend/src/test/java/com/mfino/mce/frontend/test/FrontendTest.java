package com.mfino.mce.frontend.test;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.jpos.iso.ISOMsg;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.mfino.fix.CFIXMsg;
import com.mfino.mce.core.MCEMessage;

public class FrontendTest {
	public static void main(String[] args) throws Exception 
	{
		//CamelContext context = new DefaultCamelContext();
		ApplicationContext context = new FileSystemXmlApplicationContext("C:/mfino_code/mfs_2.5/MfinoCoreEngine/Frontend/src/test/resources/META_INF/spring/frontend_test_conf.xml");
		CamelContext camel = (CamelContext) context.getBean("frontend-test");
		
		 // create the exchange used for the communication
	    // we use the in out pattern for a synchronized exchange where we expect a response
		
	    //Exchange exchange = endpoint.createExchange(ExchangePattern.InOnly);
	    // set the input on the in body
	    // must you correct type to match the expected type of an Integer object
	    
	    
	    
	    System.out.println("*****************************************");
	    
//	    SubscriberRegistrationCashInTest testClass = new SubscriberRegistrationCashInTest();
//	    ChargeDistributionTest testClass = new ChargeDistributionTest();
//	    SettlementOfChargeTest testClass = new SettlementOfChargeTest();
//	    CashInInquiryTest testClass = new CashInInquiryTest();
//	    CashInTest testClass = new CashInTest();
	    
//	    CashOutInquiryTest testClass = new CashOutInquiryTest();
	    
	    System.out.println("Sending Message");
	    
	    /*MCEMessage mceMessage1 = new MCEMessage();
		CMSignOnToBank fixMesg = new CMSignOnToBank();
		fixMesg.setTransactionID(100000L);
		mceMessage1.setResponse(fixMesg);
		//camel.createProducerTemplate().asyncSendBody("jms:isoServerMaintenanceQueue", mceMessage1);
		camel.createProducerTemplate().asyncSendBody("jms:frontendServiceSendQueue", mceMessage1);*/
	    
		ISOMsg echo  = new ISOMsg();
		
		echo.setMTI("0800");
		echo.set (7, "1105180136");
		echo.set (11, "180136");
		echo.set (12, "180136");
		echo.set (13, "1105");
		echo.set (70, "301");
		CMBalanceInquiryToBankTest testClass = new CMBalanceInquiryToBankTest();
    	CFIXMsg requestFix = testClass.getMessage();
    	MCEMessage mceMessage = new MCEMessage();
    	mceMessage.setRequest(requestFix);
	    mceMessage.setResponse(requestFix);
		
	    ProducerTemplate template = camel.createProducerTemplate();
		template.start();
		template.asyncSendBody("jms:frontendServiceQueue?disableReplyTo=true", mceMessage);
		template.stop();
	    
	   
	    
	   /* for(int i=0;i<1;i++)
	    {
	    	CMBalanceInquiryToBankTest testClass = new CMBalanceInquiryToBankTest();
	    	CFIXMsg requestFix = testClass.getMessage();
	    	MCEMessage mceMessage = new MCEMessage();
	    	mceMessage.setRequest(requestFix);
		    mceMessage.setResponse(requestFix);
	    	camel.createProducerTemplate().asyncSendBody("vm:isoSendQueue", mceMessage);
	    }
	    */
		
		
	    //exchange.getIn().setBody(mceMessage);

	    // to send the exchange we need an producer to do it for us
	    //Producer producer = endpoint.createProducer();
	    // start the producer so it can operate
	    //producer.start();

	    // let the producer process the exchange where it does all the work in this oneline of code
	    
	    //producer.process(exchange);

	    // get the response from the out body and cast it to an integer
	    /*int response = exchange.getOut().getBody(Integer.class);
	    System.out.println("... the result is: " + response);

	    // stop and exit the client*/
	   // producer.stop();
	  //  System.exit(0);
	    //camel.stop();
	}

}
