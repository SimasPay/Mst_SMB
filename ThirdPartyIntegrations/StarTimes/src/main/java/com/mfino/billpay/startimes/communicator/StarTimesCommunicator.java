package com.mfino.billpay.startimes.communicator;

import java.io.StringReader;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.management.Query;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.Exchange;
import com.mfino.hibernate.Timestamp;


import com.mfino.billpayments.beans.BillPayResponse;
import com.mfino.billpayments.service.BillPaymentsBaseServiceImpl;
import com.mfino.billpayments.service.BillPaymentsService;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.IntegrationSummaryDao;
import com.mfino.domain.BillPayments;
import com.mfino.domain.IntegrationSummary;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBase;
import com.mfino.fix.CmFinoFIX.CMBillInquiry;
import com.mfino.fix.CmFinoFIX.CMBillPay;
import com.mfino.fix.CmFinoFIX.CMGetMDNBillDebtsFromOperator;
import com.mfino.mce.backend.IntegrationSummaryService;
import com.mfino.mce.core.MCEMessage;
import com.mfino.mce.core.util.BackendResponse;
import com.mfino.mce.core.util.MCEUtil;
import com.mfino.mce.core.util.NotificationCodes;
import com.mfino.mce.core.ws.WSCommunicator;
import com.mfino.billpay.startimes.util.StarTimesWSConstants;
import com.star.sms.haiwai.service.QueryBalance;

import com.star.sms.service.model.BalanceInfo;
import com.star.sms.model.haiwai.CustomerPayDto2;
import com.star.sms.model.haiwai.CustomerPayResult2;
import com.star.sms.haiwai.service.IHaiWaiElectronicPaymentService;
import com.star.sms.haiwai.service.IHaiWaiElectronicPaymentServicePortType;
import com.star.sms.model.haiwai.ObjectFactory;

import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Set;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;

import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.Binding;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;


public class StarTimesCommunicator extends BillPaymentsBaseServiceImpl {
	private IHaiWaiElectronicPaymentServicePortType starTimesService;
	private BillPaymentsService	billPaymentsService;
	private IntegrationSummaryService integrationSummaryService;
	private String payerId;
	private String payerPwd;
	private String wsdlUrl;
	private Long sctlId = null;
	String txnNumber = null;
	
	
	public String getWsdlUrl() {
		return wsdlUrl;
	}
	public void setWsdlUrl(String wsdlUrl) {
		this.wsdlUrl = wsdlUrl;
	}

	private String message="customerPay2";
	private Date txnTime;
	public Date getTxnTime() {
		return txnTime;
	}
	public void setTxnTime(Date txnTime) {
		this.txnTime = txnTime;
	}
	
	static String username = "StarCallCenter";
    static String password = "StarCallCenter";
	
    public IHaiWaiElectronicPaymentServicePortType getStarTimesService() {
		return starTimesService;
	}
	
    public void setStarTimesService(IHaiWaiElectronicPaymentServicePortType starTimesService) {
		this.starTimesService = starTimesService;
	}
	
	public IntegrationSummaryService getIntegrationSummaryService() {
		return integrationSummaryService;
	}
	public void setIntegrationSummaryService(
			IntegrationSummaryService integrationSummaryService) {
		this.integrationSummaryService = integrationSummaryService;
	}
    
	public List<Object> getParameterList(MCEMessage mceMessage) {
		log.info("StarTimesCommunicator:: getParameterList mceMessage = " + mceMessage);
		List<Object> parameterList = new ArrayList<Object>();
		boolean isQueryRequest = false;
		String smartCardCode = null;
		String sourceMDN = null;
		BillPayments billPayments = null;
		Date now = new Date();
		setTxnTime(now);		
		if(mceMessage.getRequest() instanceof CMBillInquiry){
			CMBillInquiry requestFix = (CMBillInquiry)mceMessage.getRequest();
			sctlId = requestFix.getServiceChargeTransactionLogID();
			smartCardCode=requestFix.getInvoiceNumber();
			billPayments = billPaymentsService.getBillPaymentsRecord(sctlId);
			isQueryRequest=true;
		}
		else {
			CMBase requestFix = (CMBase)mceMessage.getRequest();
			sctlId = requestFix.getServiceChargeTransactionLogID();
			sourceMDN = requestFix.getSourceMDN();
			billPayments = billPaymentsService.getBillPaymentsRecord(sctlId);
			smartCardCode=billPayments.getInvoiceNumber();			
		}
		
		double amount = 0;

		if(billPayments != null){
			BigDecimal bd = billPayments.getAmount();

			if(bd != null){
				amount = bd.doubleValue();
			}			
		}
		
		txnNumber = getTxnNumber(payerId,sctlId);
		integrationSummaryService.logIntegrationSummary(sctlId, (Long) null, "StarTimes", txnNumber, "", "", "", new Timestamp(getTxnTime()));
		
		if(isQueryRequest == true){
			this.message = "queryBalance";
			parameterList.add(txnNumber);
			parameterList.add(getPayerId());
			parameterList.add(getPayerPwd());
			parameterList.add(new String(""));
			parameterList.add(smartCardCode);
			parameterList.add(new String(""));
			parameterList.add(new String(""));			
			
		}else {
			CustomerPayDto2 in0 = new CustomerPayDto2();
			ObjectFactory of = new ObjectFactory();
			
			in0.setCustomerTel(of.createCustomerPayDto2CustomerTel(sourceMDN));
			in0.setFee(of.createCustomerPayDto2Fee(amount));
			in0.setPayerID(of.createCustomerPayDto2PayerID(getPayerId()));
			in0.setPayerPwd(of.createCustomerPayDto2PayerPwd(getPayerPwd()));
			in0.setSmartCardCode(of.createCustomerPayDto2SmartCardCode(smartCardCode));
			in0.setTransactionNo(of.createCustomerPayDto2TransactionNo(txnNumber));
			in0.setTransferTime(of.createCustomerPayDto2TransferTime(getTransactionTime()));
			parameterList.add(in0);			
		}
		
		return parameterList;
	}

	
	public MCEMessage constructReplyMessage(List<Object> wsResponse, MCEMessage requestMceMessage) {
		log.info("StarTimesCommunicator :: constructReplyMessage wsResponseElement=" + wsResponse + " requestMceMessage=" + requestMceMessage);
		MCEMessage responseMceMessage = new MCEMessage();

		CMBase requestFix = (CMBase) requestMceMessage.getRequest();

		BillPayResponse billPayResponse = new BillPayResponse();
		billPayResponse.setServiceChargeTransactionLogID(requestFix.getServiceChargeTransactionLogID());

		if (wsResponse == null) {
			billPayResponse.setResponse(CmFinoFIX.ResponseCode_Failure);
			billPayResponse.setResult(CmFinoFIX.ResponseCode_Failure);
			billPayResponse.setInResponseCode(MCEUtil.SERVICE_UNAVAILABLE);
			log.info("ProcessRequestCommunicator :: Service Unavailable");
		}
		else if (wsResponse instanceof List && wsResponse.get(0).equals(MCEUtil.SERVICE_UNAVAILABLE)) {
			billPayResponse.setResponse(CmFinoFIX.ResponseCode_Failure);
			billPayResponse.setResult(CmFinoFIX.ResponseCode_Failure);
			billPayResponse.setInResponseCode(MCEUtil.SERVICE_UNAVAILABLE);
			log.info("ProcessRequestCommunicator :: Service Unavailable");
		}
		else {
			StarTimesResponse response;
			try {
				response = StarTimesResponseParser.parse(wsResponse);
				billPayResponse.setInResponseCode(response.retn);
				billPayResponse.setDescription(response.desc);

				if (StarTimesWSConstants.OPERATION_SUCCESS.equals(response.retn)) {
					billPayResponse.setResponse(CmFinoFIX.ResponseCode_Success);
					billPayResponse.setResult(CmFinoFIX.ResponseCode_Success);
				}
				else {
					billPayResponse.setResponse(CmFinoFIX.ResponseCode_Failure);
					billPayResponse.setResult(CmFinoFIX.ResponseCode_Failure);
				}
				billPayResponse.setInfo1(response.orderCode);
			}
			catch (Exception ex) {
				log.error("received unparseable xml from mfino webservice");
				billPayResponse.setResponse(CmFinoFIX.ResponseCode_Failure);
				billPayResponse.setResult(CmFinoFIX.ResponseCode_Failure);
				billPayResponse.setInResponseCode(MCEUtil.SERVICE_UNAVAILABLE);
			}
		}

		responseMceMessage.setRequest(requestMceMessage.getRequest());
		responseMceMessage.setResponse(billPayResponse);
		return responseMceMessage;
	}
	
	private MCEMessage constructQueryReplyMessage(List<Object> wsResponse, MCEMessage mceMessage) {
		log.info("StarTimesCommunicator :: constructReplyMessage wsResponseElement=" + wsResponse + " requestMceMessage=" + mceMessage);
		MCEMessage responseMceMessage = new MCEMessage();
		
		CMBase requestFix = (CMBase)mceMessage.getRequest();
		
		BackendResponse billResponse = new BillPayResponse();
		billResponse.setServiceChargeTransactionLogID(requestFix.getServiceChargeTransactionLogID());

		if (wsResponse == null) {
			billResponse.setResult(CmFinoFIX.ResponseCode_Failure);
			billResponse.setDescription(MCEUtil.SERVICE_UNAVAILABLE);
			log.info("StarTimesCommunicator :: Service Unavailable");
		}
		else if (wsResponse instanceof List && wsResponse.get(0).equals(MCEUtil.SERVICE_UNAVAILABLE)) {
			billResponse.setResult(CmFinoFIX.ResponseCode_Failure);
			billResponse.setDescription(MCEUtil.SERVICE_UNAVAILABLE);
			log.info("StarTimesCommunicator :: Service Unavailable");
		}
		else {
			StarTimesResponse response;
			try {
				response = StarTimesResponseParser.parse(wsResponse);
				if (StarTimesWSConstants.OPERATION_SUCCESS.equals(response.retn)) {
					billResponse.setResult(CmFinoFIX.ResponseCode_Success);
				
				//billResponse.setParentTransactionID(response.getParentTransactionID());
				//billResponse.setTransactionID(response.getTransactionID());
				billResponse.setPaymentInquiryDetails(response.desc);
				billResponse.setInternalErrorCode(NotificationCodes.QueryBalanceDetails.getInternalErrorCode());
				
				if(response.balance!=null)
					billResponse.setAmount(new BigDecimal(response.balance));
				
				}
				else {
					billResponse.setResult(CmFinoFIX.ResponseCode_Failure);
				}				
			}
			catch (Exception ex) {
				log.error("received unparseable xml from mfino webservice");
				billResponse.setResult(CmFinoFIX.ResponseCode_Failure);
				billResponse.setDescription(MCEUtil.SERVICE_UNAVAILABLE);
			}
		}

		responseMceMessage.setRequest(mceMessage.getRequest());
		responseMceMessage.setResponse(billResponse);
		
		log.info("BillInquiryProcessor :: constructReplyMessage() END");
		return responseMceMessage;
	}

	public MCEMessage process(MCEMessage mceMessage) throws Exception 
	{
		log.info("StarTimesCommunicator :: process() BEGIN mceMessage="+mceMessage);
		MCEMessage replyMessage = new MCEMessage();
		String messageName = getMessageName(mceMessage);
		List<Object> response = null;
		Integer returnResponse = null;
		List<Object> params =null;
		try{
			params = getParameterList(mceMessage);
			log.info("StarTimesCommunicator messageName="+messageName+", parameters="+params);
			URL url = new URL(getWsdlUrl() + "?wsdl");
			IHaiWaiElectronicPaymentService service = new IHaiWaiElectronicPaymentService(url);
	        IHaiWaiElectronicPaymentServicePortType port = service.getIHaiWaiElectronicPaymentServiceHttpPort();
	        Binding binding = ((BindingProvider) port).getBinding();
	        List handlerList = binding.getHandlerChain();
	        if (handlerList == null) {
	            System.out.println("HandlerList is null");
	            handlerList = new ArrayList();
	        }
	        handlerList.add(new AuthHeaderLocal());
	        binding.setHandlerChain(handlerList);
	        response = wsRequest(messageName,params,port);
	        
	        log.info("StarTimesCommunicator :: messageName="+messageName+", returnResponse="+response);
		}catch(Exception e){
			log.error("StarTimesCommunicator :: Exception e=",e);
			response = handleWSCommunicationException(e);
		}    
		
		if(messageName.equalsIgnoreCase("queryBalance")){
			replyMessage = constructQueryReplyMessage(response, mceMessage);
		}else{
			replyMessage = constructReplyMessage(response, mceMessage);
			BillPayResponse res = (BillPayResponse) replyMessage.getResponse();
			integrationSummaryService.logIntegrationSummary(sctlId.longValue(), 0L, "StarTimes", txnNumber, res.getInfo1(), "", "", new Timestamp(getTxnTime()));			
		}
		
		log.info("StarTimesCommunicator :: process() END");
		return replyMessage;
	}
	
	
	public String getMessageName(MCEMessage mceMessage) {
		if(mceMessage.getRequest() instanceof CMBillInquiry){
			message = "queryBalance";
		}
		else{
			message = "customerPay2";
		}
		return message;		
	}
	
	public BillPaymentsService getBillPaymentsService() {
		return billPaymentsService;
	}

	public void setBillPaymentsService(BillPaymentsService billPaymentsService) {
		this.billPaymentsService = billPaymentsService;
	}
	
	public String getPayerId() {
		return payerId;
	}


	public void setPayerId(String payerId) {
		this.payerId = payerId;
	}


	public String getPayerPwd() {
		return payerPwd;
	}


	public void setPayerPwd(String payerPwd) {
		this.payerPwd = payerPwd;
	}
	
	private String getTxnNumber(String payerIdIn, Long sctlId) {
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyyMMddHHmmss");
	    String strDate = sdfDate.format(getTxnTime());
		String transactionNumber = payerIdIn+strDate+sctlId;
		return transactionNumber;
	}
	
	private String getTransactionTime() {
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    String strDate = sdfDate.format(getTxnTime());
		return strDate;
	}
	
	private static List<Object> wsRequest(String messageName, List<Object> parameterList, IHaiWaiElectronicPaymentServicePortType port) {
    	List<Object> wsResponseList = new ArrayList<Object>();
		if(messageName.equalsIgnoreCase("customerPay2")){
			CustomerPayResult2 rs = port.customerPay2((CustomerPayDto2)parameterList.get(0));
			wsResponseList.add(rs);
		}else if(messageName.equalsIgnoreCase("queryBalance")){
			BalanceInfo rs = port.queryBalance((String)parameterList.get(0), (String)parameterList.get(1), (String)parameterList.get(2), (String)parameterList.get(3), 
					(String)parameterList.get(4), (String)parameterList.get(5), (String)parameterList.get(6));
			wsResponseList.add(rs);
		}
		else {
			return null;
		}
		return wsResponseList;
	}
	
	public List<Object> handleWSCommunicationException(Exception e){
		List<Object> responseFromWS = new ArrayList<Object>();
		
  		if(e.getCause() instanceof SocketTimeoutException){
			responseFromWS.add(MCEUtil.SERVICE_TIME_OUT);
		}
		else{
			responseFromWS.add(MCEUtil.SERVICE_UNAVAILABLE);
		}
		
		return responseFromWS;
	}
	
	private static class AuthHeaderLocal implements SOAPHandler<SOAPMessageContext> {

        @Override
        public boolean handleMessage(SOAPMessageContext context) {
            Boolean isRequest = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
            if (isRequest) {
                try {
                    SOAPMessage soapMsg = context.getMessage();
                    SOAPEnvelope soapEnv = soapMsg.getSOAPPart().getEnvelope();
                    SOAPHeader soapHeader = soapEnv.getHeader();
                    if (soapHeader == null) {
                        soapHeader = soapEnv.addHeader();
                    }
                    QName qname1 = new QName("NAMESPACE_STARSMS", "CALLCENTER_USERNAME");
                    SOAPHeaderElement soapHeaderElement1 = soapHeader.addHeaderElement(qname1);
                    soapHeaderElement1.setTextContent(password);

                    QName qname2 = new QName("NAMESPACE_STARSMS", "CALLCENTER_PASSWORD");
                    SOAPHeaderElement soapHeaderElement2 = soapHeader.addHeaderElement(qname2);
                    soapHeaderElement2.setTextContent(password);

                    QName qname3 = new QName("NAMESPACE_STARSMS", "CALLCENTER_VERSION");
                    SOAPHeaderElement soapHeaderElement3 = soapHeader.addHeaderElement(qname3);
                    soapHeaderElement3.setTextContent("1.5");
                    soapMsg.saveChanges();
                    try {
                        soapMsg.writeTo(System.out);
                    } catch (Exception ex) {
                    }

                    soapMsg.saveChanges();
                    System.out.print(soapMsg.getSOAPHeader().toString());

                } catch (SOAPException e) {
                    System.err.println(e);
                }

            }
            return true;
        }

        @Override
        public boolean handleFault(SOAPMessageContext context) {
            return true;
        }

        @Override
        public void close(MessageContext context) {
        }

        @Override
        public Set<QName> getHeaders() {
            return null;
        }
    }

}
