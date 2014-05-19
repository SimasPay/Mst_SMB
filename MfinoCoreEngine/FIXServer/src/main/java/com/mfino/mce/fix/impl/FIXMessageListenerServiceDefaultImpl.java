package com.mfino.mce.fix.impl;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import org.apache.camel.CamelContext;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.MDC;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.constants.SystemParameterKeys;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CMultiXBuffer;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBase;
import com.mfino.fix.CmFinoFIX.CMBillInquiry;
import com.mfino.fix.CmFinoFIX.CMBillPayPendingRequest;
import com.mfino.fix.CmFinoFIX.CMGetUserAPIKeyToBank;
import com.mfino.fix.CmFinoFIX.CMNFCCardLinkToCMS;
import com.mfino.fix.CmFinoFIX.CMNFCCardStatusToCMS;
import com.mfino.fix.CmFinoFIX.CMNFCCardUnlinkReversalToCMS;
import com.mfino.fix.CmFinoFIX.CMNFCCardUnlinkToCMS;
import com.mfino.fix.CmFinoFIX.CMQRPayment;
import com.mfino.fix.CmFinoFIX.CMQRPaymentInquiry;
import com.mfino.mce.core.MCEMessage;
import com.mfino.mce.core.util.MCEUtil;
import com.mfino.mce.fix.FIXMessageListenerService;
import com.mfino.service.impl.SystemParametersServiceImpl;
import com.mfino.util.EncryptionUtil;

public class FIXMessageListenerServiceDefaultImpl implements FIXMessageListenerService
{
	Log log = LogFactory.getLog(FIXMessageListenerServiceDefaultImpl.class);	
	boolean isInitialized = false;
	private Integer timeout;
	public Integer getTimeout() {
		return timeout;
	}

	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}

	public FIXMessageListenerServiceDefaultImpl(Integer timeout) {
		setTimeout(timeout);
	}
	
	public FIXMessageListenerServiceDefaultImpl() 
	{
		initialize();
	}

	public void initialize()
	{	
		if(!isInitialized)
		{
			CFIXMsg.SetFIXMsgCreator(new CmFinoFIX.CMessageCreator());
			isInitialized = true;
		}
	}
	
	@Override
	public MCEMessage processMessage(byte[] in) 
	{
		initialize();
		/*Code before using findBugs tool
		 * log.info("Type="+in);
		 */
		/*code after using findbug tool
		 * using Arrays.toString to convert the array into a readable String.
		 * log.info("Type="+Arrays.toString(in));
		 */
		
		log.info("Type="+Arrays.toString(in));
		CMultiXBuffer buffer = new CMultiXBuffer((byte[])in);
		CFIXMsg fixMesg = (CFIXMsg)CFIXMsg.fromFIX(buffer);
		log.info(fixMesg.DumpFields());
		MCEMessage mceMessage = new MCEMessage();
		mceMessage.setRequest(fixMesg);
		return mceMessage;
	}
	
	// jms queue that starts some functionality
	public static final String ACTIVEMQ_QUEUE_START_SOME_FUNCTIONALITY = "jms:fixServiceQueue?disableReplyTo=true";

	// jms queue where the result of some functionality are deposited
	public static final String ACTIVEMQ_QUEUE_RESULT_OF_SOME_FUNCTIONALITY = "jms:FixReplyQueue?disableReplyTo=true";
	
	// jms queue where the result of some functionality are deposited
	public static final String ACTIVEMQ_QUEUE_DSTV = "jms:DSTVQueue?disableReplyTo=true";
	
	public static final String ACTIVEMQ_QUEUE_VISAFONE_AIRTIME = "jms:visafoneAirtimeQueue?disableReplyTo=true";
	
	//jms queue for Inter Bank Transfer
	public static final String ACTIVEMQ_QUEUE_IBT = "jms:IBTQueue?disableReplyTo=true";
	
	// notification queue
	//TODO: move this configuration out to some properties file
	public static final String ACTIVEMQ_QUEUE_SMS = "jms:notificationQueue?disableReplyTo=true";
	
	//jms queue for Bank teller
	private static final String ACTIVEMQ_QUEUE_BANK_TELLER = "jms:bankTellerQueue?disableReplyTo=true";

	public static final String ACTIVEMQ_QUEUE_BILL_PAY = "jms:billPayQueue?disableReplyTo=true";
	
	public static final String ACTIVEMQ_QUEUE_FLASHIZ_QR_PAYMENT = "jms:flashizBillPayQueue?disableReplyTo=true";
	//jms queue for adjustments through ui
	public static final String ACTIVEMQ_QUEUE_TRANSACTION_ADJUSTMENTS = "jms:adjustmentsQueue?disableReplyTo=true";
	
	// the message header key where to find the custom correlation id
	private static final String EXCHANGE_HEADER_SYNCHRONOUS_REQUEST_ID = "synchronous_request_id";

	// defines the time in milliseconds the system should wait until it sends an timed out error. 
//	private static final int timeOut = 30000;
	
	private static final String ACTIVEMQ_AUTO_REVERSAL = "jms:autoReversalQueue?disableReplyTo=true";
	
	//hsm queue
	private static final String ACTIVEMQ_QUEUE_HSM ="jms:hsmQueue?disableReplyTo=true";
	private static final String ACTIVEMQ_QUEUE_NFC_CMS = "jms:nfcISOQueue?disableReplyTo=true";
	private static final String ACTIVEMQ_QUEUE_FLASHIZ ="jms:flashizISOQueue?disableReplyTo=true";
	/*
	 * Process HttpServletRequests received by JETTY or SERVLET component of Apache Camel. 
	 */
	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public void process(Exchange httpExchange) throws Exception {
		
		initialize();
		byte[] in = httpExchange.getIn().getBody(byte[].class);
		log.info("FIXMessageListenerServiceDefaultImpl :: process in="+in);
		/*
		 * FIXME: Refactoring issue - Need to be fixed.
		 */
/*		SystemParametersServiceImpl systemParametersServiceImpl = new SystemParametersServiceImpl();
        String strEncryptFixMessage = systemParametersServiceImpl.getString(SystemParameterKeys.ENCRYPT_FIX_MESSAGE);
*/      
		String strEncryptFixMessage = null;
		log.info("FIXMessageListenerServiceDefaultImpl : process : strEncryptFixMessage="+strEncryptFixMessage);
        if((null != strEncryptFixMessage) && ("true".equals(strEncryptFixMessage))){
    		byte[] decryptedByteArray = EncryptionUtil.decrypt(in);
    		log.info("FIXMessageListenerServiceDefaultImpl :: Type="+Arrays.toString(decryptedByteArray));
    		in = decryptedByteArray;
        }
		
		/*Code before using findBugs tool
		 * log.info("FIXMessageListenerServiceDefaultImpl :: Type="+in);
		 */
		/*code after using findbug tool
		 * using Arrays.toString to convert the array into a readable String.
		 * log.info("FIXMessageListenerServiceDefaultImpl :: Type="+Arrays.toString(in));
		 */
		CMultiXBuffer buffer = new CMultiXBuffer(in);
		MCEMessage mceMessage = new MCEMessage();
		CFIXMsg fixMesg = (CFIXMsg)CFIXMsg.fromFIX(buffer);
		mceMessage.setRequest(fixMesg);
		log.info(fixMesg.DumpFields());
		//httpExchange.getIn().setBody(fixMesg);
		
		CamelContext camelContext = httpExchange.getContext();		
		/*
		 * generate id of synchronous context
		 */
        	final String synchronousRequestId = UUID.randomUUID().toString();
		
		
		// fetch wait time
		Object waitTime = httpExchange.getIn().getHeader("waitTime");
		//camelContext.createProducerTemplate().sendBodyAndHeader(ACTIVEMQ_QUEUE_START_SOME_FUNCTIONALITY, waitTime, EXCHANGE_HEADER_SYNCHRONOUS_REQUEST_ID, synchronousRequestId);
        // call seda queue in order to call the receiveExchange method of bean in a asynchronous way
		
		Map<String, Object> header =  httpExchange.getIn().getHeaders();
		MCEUtil.setBreadCrumbId(header, ((CMBase)fixMesg).getTransactionIdentifier());
		if(StringUtils.isNotBlank(((CMBase)fixMesg).getTransactionIdentifier())){
				MDC.put("breadcrumbId", ((CMBase)fixMesg).getTransactionIdentifier());
		}
		log.info("synchronous message id "+synchronousRequestId);
		header.put(EXCHANGE_HEADER_SYNCHRONOUS_REQUEST_ID, synchronousRequestId);
		header.put("waitTime", waitTime);
		
		//send the SMS notification message to SMS service directly
		//TODO: need to move this code to a better manage
		ProducerTemplate producerTemplate = camelContext.createProducerTemplate();
		producerTemplate.start();
		if(fixMesg instanceof CmFinoFIX.CMQRPaymentInquiry || fixMesg instanceof CmFinoFIX.CMQRPayment){
			producerTemplate.sendBodyAndHeaders(ACTIVEMQ_QUEUE_FLASHIZ_QR_PAYMENT, mceMessage, header);
		}
		else if(fixMesg instanceof CmFinoFIX.CMBillPayInquiry || fixMesg instanceof CmFinoFIX.CMBillPay || fixMesg instanceof CMBillPayPendingRequest || fixMesg instanceof CMBillInquiry){
			producerTemplate.sendBodyAndHeaders(ACTIVEMQ_QUEUE_BILL_PAY, mceMessage, header);
		}
		else if(fixMesg instanceof CmFinoFIX.CMSMSNotification )
		{
			producerTemplate.sendBodyAndHeaders(ACTIVEMQ_QUEUE_SMS, mceMessage, header);
		}
		else if(fixMesg instanceof CmFinoFIX.CMInterBankFundsTransferInquiry || fixMesg instanceof CmFinoFIX.CMInterBankFundsTransfer || fixMesg instanceof CmFinoFIX.CMInterBankFundsTransferStatus || fixMesg instanceof CmFinoFIX.CMInterBankPendingCommodityTransferRequest){
			log.debug("FIXMessageListenerServiceDefaultImpl :: Inter Bank Transfer request.");
			producerTemplate.sendBodyAndHeaders(ACTIVEMQ_QUEUE_IBT, mceMessage, header);
		}
		else if (fixMesg instanceof CmFinoFIX.CMDSTVPaymentInquiry || fixMesg instanceof CmFinoFIX.CMDSTVPayment || fixMesg instanceof CmFinoFIX.CMDSTVPendingCommodityTransferRequest)
		{
			producerTemplate.sendBodyAndHeaders(ACTIVEMQ_QUEUE_DSTV, mceMessage, header);
		}
		else if (fixMesg instanceof CmFinoFIX.CMVisafoneAirtimePurchaseInquiry || fixMesg instanceof CmFinoFIX.CMVisafoneAirtimePurchase || fixMesg instanceof CmFinoFIX.CMVisafoneAirtimePendingCommodityTransferRequest)
		{
			producerTemplate.sendBodyAndHeaders(ACTIVEMQ_QUEUE_VISAFONE_AIRTIME, mceMessage, header);
		}
		else if(fixMesg instanceof CmFinoFIX.CMBankTellerCashIn || fixMesg instanceof CmFinoFIX.CMBankTellerCashInConfirm 
				|| fixMesg instanceof CmFinoFIX.CMBankTellerCashOut || fixMesg instanceof CmFinoFIX.CMBankTellerCashOutConfirm
				||fixMesg instanceof CmFinoFIX.CMTellerPendingCommodityTransferRequest)
		{
			producerTemplate.sendBodyAndHeaders(ACTIVEMQ_QUEUE_BANK_TELLER, mceMessage, header);
		}
		else if(fixMesg instanceof CmFinoFIX.CMAutoReversal){
			producerTemplate.sendBodyAndHeaders(ACTIVEMQ_AUTO_REVERSAL, mceMessage, header);
		}
		else if(fixMesg instanceof CmFinoFIX.CMTransactionAdjustments){
			producerTemplate.sendBodyAndHeaders(ACTIVEMQ_QUEUE_TRANSACTION_ADJUSTMENTS, mceMessage, header);
		}
		else if(fixMesg instanceof CmFinoFIX.CMHSMPINValidationRequest || fixMesg instanceof CmFinoFIX.CMHSMOffsetRequest || 
				fixMesg instanceof CmFinoFIX.CMHSMKeyExchangeRequest  || fixMesg instanceof CmFinoFIX.CMHSMPinBlockRequest ||
				fixMesg instanceof CmFinoFIX.CMHSMEcryptComponentsRequest){
			producerTemplate.sendBodyAndHeaders(ACTIVEMQ_QUEUE_HSM, mceMessage, header);
		}		
		else if(fixMesg instanceof CmFinoFIX.CMNFCCardLink){
			CMNFCCardLinkToCMS responseObject = new CMNFCCardLinkToCMS();
			responseObject.copy((CmFinoFIX.CMNFCCardLink) fixMesg);
			mceMessage.setResponse(responseObject);
			producerTemplate.sendBodyAndHeaders(ACTIVEMQ_QUEUE_NFC_CMS, mceMessage, header);
		}
		else if(fixMesg instanceof CmFinoFIX.CMNFCCardUnlink){
			CMNFCCardUnlinkToCMS responseObject = new CMNFCCardUnlinkToCMS();
			responseObject.copy((CmFinoFIX.CMNFCCardUnlink) fixMesg);
			mceMessage.setResponse(responseObject);
			producerTemplate.sendBodyAndHeaders(ACTIVEMQ_QUEUE_NFC_CMS, mceMessage, header);
		}
		else if(fixMesg instanceof CmFinoFIX.CMNFCCardUnlinkReversal){
			CMNFCCardUnlinkReversalToCMS responseObject = new CMNFCCardUnlinkReversalToCMS();
			responseObject.copy((CmFinoFIX.CMNFCCardUnlinkReversal) fixMesg);
			mceMessage.setResponse(responseObject);
			producerTemplate.sendBodyAndHeaders(ACTIVEMQ_QUEUE_NFC_CMS, mceMessage, header);
		}
		else if(fixMesg instanceof CmFinoFIX.CMNFCCardStatus){
			CMNFCCardStatusToCMS responseObject = new CMNFCCardStatusToCMS();
			responseObject.copy((CmFinoFIX.CMNFCCardStatus) fixMesg);
			mceMessage.setResponse(responseObject);
			producerTemplate.sendBodyAndHeaders(ACTIVEMQ_QUEUE_NFC_CMS, mceMessage, header);
		}else if(fixMesg instanceof CmFinoFIX.CMGetUserAPIKey){
			CMGetUserAPIKeyToBank responseObject = new CMGetUserAPIKeyToBank();
			responseObject.copy((CmFinoFIX.CMGetUserAPIKey) fixMesg);
			mceMessage.setResponse(responseObject);
			producerTemplate.sendBodyAndHeaders(ACTIVEMQ_QUEUE_FLASHIZ, mceMessage, header);
		}
		else
		{
			producerTemplate.sendBodyAndHeaders(ACTIVEMQ_QUEUE_START_SOME_FUNCTIONALITY, mceMessage, header);
		}
		
		producerTemplate.stop();

        // wait for result and returns null if defined request time timed out
		log.info("timeout"+timeout);
		ConsumerTemplate template = camelContext.createConsumerTemplate();
	    template.start();
		
	    CFIXMsg resultFromQueuingSystem = template.receiveBody("seda:" + synchronousRequestId, timeout,CFIXMsg.class);
        
		log.info("result from queing system "+resultFromQueuingSystem);
        if(resultFromQueuingSystem != null) 
        {
        	// tell http client the request was sucessful
        	//httpExchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
        	
        	// take the queuing result and place it to the http response
        	CMultiXBuffer buf = new CMultiXBuffer();
        	//if(resultFromQueuingSystem.getIn().getBody()!=null)
        	//{
        		resultFromQueuingSystem.toFIX(buf);

        		byte[] replyFixArray = buf.DataPtr();
        		if((null != strEncryptFixMessage) && ("true".equals(strEncryptFixMessage))){
            		byte[] encryptedByteArray = EncryptionUtil.encrypt(buf.DataPtr());
            		log.info("FIXMessageListenerServiceDefaultImpl :: replying back encryptedByteArray="+encryptedByteArray);
            		replyFixArray = encryptedByteArray;
        		}
        		
        		httpExchange.getOut().setBody(replyFixArray);
        	//}
        	//else
        	//{
        	//	httpExchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
        	//}
        	
        	//this is mandatory when using ConsumerTemplate 
        	//template.doneUoW(resultFromQueuingSystem);
        	
        } 
        else 
        {
        	// tell http client the request is timed out        	
        	httpExchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, 504);
        	
        	
        }
        template.stop();
        //remove the endpoint in case if by chance even after template stop thread is not freed up
        camelContext.removeEndpoints("seda:" + synchronousRequestId);
        MDC.remove("transactionIdentifier");
       
	}
}
