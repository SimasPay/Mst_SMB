package com.mfino.cashin;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.CharacterData;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.mfino.domain.ChannelCode;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.mFinoServiceProvider;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBase;
import com.mfino.fix.CmFinoFIX.CMInterswitchCashin;
import com.mfino.fix.CmFinoFIX.CMInterswitchCashinStatus;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.hibernate.Timestamp;
import com.mfino.i18n.MessageText;
import com.mfino.mce.core.util.MCEUtil;
import com.mfino.result.XMLResult;
import com.mfino.service.ChannelCodeService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SubscriberService;
import com.mfino.transactionapi.handlers.interswitch.CashinReversalHandler;
import com.mfino.transactionapi.handlers.interswitch.IntegrationCashinConfirmHandler;
import com.mfino.transactionapi.handlers.interswitch.IntegrationCashinInquiryHandler;
import com.mfino.transactionapi.handlers.interswitch.impl.InterswitchCashinStatusHandlerImpl;
import com.mfino.transactionapi.handlers.interswitch.impl.TransactionDataContainerImpl;
import com.mfino.transactionapi.result.xmlresulttypes.money.TransferInquiryXMLResult;
import com.mfino.transactionapi.result.xmlresulttypes.wallet.WalletConfirmXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.util.DateTimeUtil;

public class CashinMessageListener implements Processor {

	Logger	                           log	= LoggerFactory.getLogger(CashinMessageListener.class);

 
	public static final String	TEXT_CASHINREQUEST	   = "CashInRequest";
	public static final String	TEXT_PAYMENT_LOG_ID	   = "PaymentLogId";
	public static final String	TEXT_TARGET_MDN	       = "TargetMdn";
	public static final String	TEXT_TARGET_FIRST_NAME	= "TargetFirstName";
	public static final String	TEXT_TARGET_LAST_NAME	= "TargetLastName";
	public static final String	TEXT_AMOUNT	           = "Amount";
	public static final String	TEXT_PAYMENT_METHOD	   = "PaymentMethod";
	public static final String	TEXT_PAYMENT_REFERENCE	= "PaymentReference";
	public static final String	TEXT_TERMINAL_ID	   = "TerminalId";
	public static final String	TEXT_CHANNEL_NAME	   = "ChannelName";
	public static final String	TEXT_PAYMENT_DATE	   = "PaymentDate";
	public static final String	TEXT_INSTITUTION_ID	   = "InstitutionId";
	public static final String	TEXT_INSTITUTION_NAME	= "InstitutionName";
	public static final String	TEXT_INITIATOR_NAME	   = "InitiatorName";
	public static final String	TEXT_RECEIPT_NO	       = "ReceiptNo";
	public static final String	TEXT_INITIATOR_MDN	   = "InitiatorMdn";
	public static final String	TEXT_GETDETAILSREQUEST	= "GetDetailsRequest";
	public static final String	TEXT_MDN	           = "Mdn";
	public static final String	TEXT_REFERENCE_NUMBER	= "ReferenceNumber";
 	public static final String	TEXT_PAYMENT_CURRENCY	= "PaymentCurrency";
	public static final String	TEXT_CHANNEL_ID	= "ChannelId";
 
	private ChannelCodeService channelCodeService ;
	SubscriberMdnService subscriberMdnService ;
	SubscriberService subscriberService;

	public SubscriberService getSubscriberService() {
		return subscriberService;
	}

	public void setSubscriberService(SubscriberService subscriberService) {
		this.subscriberService = subscriberService;
	}

	public SubscriberMdnService getSubscriberMdnService() {
		return subscriberMdnService;
	}

	public void setSubscriberMdnService(SubscriberMdnService subscriberMdnService) {
		this.subscriberMdnService = subscriberMdnService;
	}

	TransactionApiValidationService transactionApiValidationService ;

	public TransactionApiValidationService getTransactionApiValidationService() {
		return transactionApiValidationService;
	}

	public void setTransactionApiValidationService(
			TransactionApiValidationService transactionApiValidationService) {
		this.transactionApiValidationService = transactionApiValidationService;
	}

	public ChannelCodeService getChannelCodeService() {
		return channelCodeService;
	}

	public void setChannelCodeService(ChannelCodeService channelCodeService) {
		this.channelCodeService = channelCodeService;
	}

	private CashinReversalHandler cashinReversalHandler;

	public CashinReversalHandler getCashinReversalHandler() {
		return cashinReversalHandler;
	}

	public void setCashinReversalHandler(CashinReversalHandler cashinReversalHandler) {
		this.cashinReversalHandler = cashinReversalHandler;
	}

	private IntegrationCashinInquiryHandler integrationCashinInquiryHandler;

	public IntegrationCashinInquiryHandler getIntegrationCashinInquiryHandler() {
		return integrationCashinInquiryHandler;
	}

	public void setIntegrationCashinInquiryHandler(
			IntegrationCashinInquiryHandler integrationCashinInquiryHandler) {
		this.integrationCashinInquiryHandler = integrationCashinInquiryHandler;
	}
	private	IntegrationCashinConfirmHandler integrationCashinConfirmHandler;
	public IntegrationCashinConfirmHandler getIntegrationCashinConfirmHandler() {
		return integrationCashinConfirmHandler;
	}

	public void setIntegrationCashinConfirmHandler(
			IntegrationCashinConfirmHandler integrationCashinConfirmHandler) {
		this.integrationCashinConfirmHandler = integrationCashinConfirmHandler;
	}

	public void process(Exchange exchange) throws Exception {
		String syncId = exchange.getIn().getHeader("synchronous_request_id").toString();
		String cashinChannel = exchange.getIn().getHeader("FrontendID").toString();
		String finalXML;

		log.info("channel header -->" + cashinChannel);
		log.info("synchronous request header -->" + syncId);

		try {
			finalXML = processCashin(exchange);
		}
		catch (InvalidXMLException ex) {
			log.error("invalid xml received from Interswitch");
			String request = exchange.getIn().getBody(String.class);
			String paylogID = getPaymentLogId(request);
			finalXML = "<CashInResponse><PaymentLogId>" + paylogID + "</PaymentLogId><ResponseCode>1"
			        + "</ResponseCode><ReferenceNumber>-1</ReferenceNumber></CashInResponse>";
		}

		exchange.getIn().setBody(finalXML);
	}

	private String constructReponseXML(Object ob, XMLResult result) throws ParserConfigurationException, TransformerConfigurationException,
	        TransformerFactoryConfigurationError, TransformerException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.newDocument();

		Element root = null;

		Element code = doc.createElement("ResponseCode");
		if (ob instanceof CMInterswitchCashin) {
			root = doc.createElement("CashInResponse");
			doc.appendChild(root);
			CMInterswitchCashin cashIn = (CMInterswitchCashin) ob;
			Element logID = doc.createElement("PaymentLogId");			
			String paymentLogID = cashIn.getPaymentLogID();
			if(cashIn.getCustReference()!=null)
			{
				paymentLogID = cashIn.getPaymentLogID()+cashIn.getCustReference();
			}
			logID.appendChild(doc.createTextNode(paymentLogID));
			root.appendChild(logID);			
			Element description = doc.createElement("Description");
			if (CmFinoFIX.NotificationCode_CashInToEMoneyCompletedToSender.toString().equals(result.getCode())
			        || CmFinoFIX.NotificationCode_AutoReverseSuccessToSource.toString().equals(result.getCode())){
				code.appendChild(doc.createTextNode("100"));
				root.appendChild(code);
			}
			else {
				if (CmFinoFIX.NotificationCode_InvalidPaymentLogID.toString().equals(result.getCode())){
					code.appendChild(doc.createTextNode("105"));
					description.appendChild(doc.createTextNode("Payment LogID is not valid"));				
				}
				else if(CmFinoFIX.NotificationCode_PartnerNotFound.toString().equals(result.getCode())){
					code.appendChild(doc.createTextNode("107"));
					description.appendChild(doc.createTextNode("Integration partner is not available for the given institutionId"));				
				}
				else if(CmFinoFIX.NotificationCode_PartnerRestriction.toString().equals(result.getCode())){
					code.appendChild(doc.createTextNode("107"));
					description.appendChild(doc.createTextNode("Integration partner for the given institutionId is Restricted"));				
				}
				else if(CmFinoFIX.NotificationCode_DestinationMDNNotFound.toString().equals(result.getCode())){
					code.appendChild(doc.createTextNode("111"));
					description.appendChild(doc.createTextNode("Target MDN not found"));				
				}
				else if(CmFinoFIX.NotificationCode_SubscriberNotRegistered.toString().equals(result.getCode())){
					code.appendChild(doc.createTextNode("111"));
					description.appendChild(doc.createTextNode("Taget MDN is not registered"));				
				}
				else if(CmFinoFIX.NotificationCode_DestinationMDNIsNotActive.toString().equals(result.getCode())){
					code.appendChild(doc.createTextNode("111"));
					description.appendChild(doc.createTextNode("Taget MDN is not active"));				
				}
				else if(CmFinoFIX.NotificationCode_DestinationMDNIsRestricted.toString().equals(result.getCode())){
					code.appendChild(doc.createTextNode("111"));
					description.appendChild(doc.createTextNode("Taget MDN is restricted"));				
				}
				else if(CmFinoFIX.NotificationCode_DestinationEMoneyPocketNotFound.equals(result.getNotificationCode())){
					code.appendChild(doc.createTextNode("111"));
					description.appendChild(doc.createTextNode("Default emoney pocket not found for the target MDN"));				
				}
				else if(CmFinoFIX.NotificationCode_ServiceNOTAvailableForPartner.equals(result.getNotificationCode())){
					code.appendChild(doc.createTextNode("107"));
					description.appendChild(doc.createTextNode("Agent service is not available for the integration partner"));				
				}
				else if(CmFinoFIX.NotificationCode_SourceDefaultBankAccountPocketNotFound.equals(result.getNotificationCode())){
					code.appendChild(doc.createTextNode("107"));
					description.appendChild(doc.createTextNode("Default Bank Pocket is not available for the integration partner"));				
				}
				else if(CmFinoFIX.NotificationCode_SenderBankPocketIsRestricted.equals(result.getNotificationCode())){
					code.appendChild(doc.createTextNode("107"));
					description.appendChild(doc.createTextNode("Bank Pocket is restricted for the integration partner"));				
				}
				else if(CmFinoFIX.NotificationCode_MoneyPocketNotActive.equals(result.getNotificationCode())){
					code.appendChild(doc.createTextNode("107"));
					description.appendChild(doc.createTextNode("Service Pocket is not active for the integration partner"));				
				}
				else if(CmFinoFIX.NotificationCode_ServiceNotAvailable.equals(result.getNotificationCode())){
					code.appendChild(doc.createTextNode("107"));
					description.appendChild(doc.createTextNode("Invalid Service for the integration partner"));				
				}
				else if(CmFinoFIX.NotificationCode_TransferAmountBelowMinimumAllowed.toString().equals(result.getCode())){
					code.appendChild(doc.createTextNode("109"));
					description.appendChild(doc.createTextNode("Transaction amount is less than minimum transaction limit"));				
				}
				else if(CmFinoFIX.NotificationCode_TransactionFailedDueToInvalidAmount.toString().equals(result.getCode())){
					code.appendChild(doc.createTextNode("109"));
					description.appendChild(doc.createTextNode("Transaction amount is not valid"));				
				}
				else if(CmFinoFIX.NotificationCode_TransactionFailedDueToTimeLimitTransactionReached.toString().equals(result.getCode())){
					code.appendChild(doc.createTextNode("110"));
					description.appendChild(doc.createTextNode("Transaction failed as time limit on transaction reached"));				
				}
				else if(CmFinoFIX.NotificationCode_TransferAmountAboveMaximumAllowed.toString().equals(result.getCode())){
					code.appendChild(doc.createTextNode("110"));
					description.appendChild(doc.createTextNode("Transaction amount is above maximum amount allowed"));				
				}
				else if(CmFinoFIX.NotificationCode_AboveDailyTransactionsCountLimit.toString().equals(result.getCode())){
					code.appendChild(doc.createTextNode("110"));
					description.appendChild(doc.createTextNode("Daily transactions count exceeded for the integration partner"));				
				}
				else if(CmFinoFIX.NotificationCode_AboveWeeklyTransactionsCountLimit.toString().equals(result.getCode())){
					code.appendChild(doc.createTextNode("110"));
					description.appendChild(doc.createTextNode("Weekly transactions count exceeded for the integration partner"));				
				}
				else if(CmFinoFIX.NotificationCode_AboveMonthlyTransactionsCountLimit.toString().equals(result.getCode())){
					code.appendChild(doc.createTextNode("110"));
					description.appendChild(doc.createTextNode("Monthly transactions count exceeded for the integration partner"));				
				}
				else if(CmFinoFIX.NotificationCode_AboveDailyExpenditureLimit.toString().equals(result.getCode())){
					code.appendChild(doc.createTextNode("110"));
					description.appendChild(doc.createTextNode("Exceeded daily expenditure limit for the integration partner"));				
				}
				else if(CmFinoFIX.NotificationCode_AboveWeeklyExpenditureLimit.toString().equals(result.getCode())){
					code.appendChild(doc.createTextNode("110"));
					description.appendChild(doc.createTextNode("Exceeded weekly expenditure limit for the integration partner"));				
				}
				else if(CmFinoFIX.NotificationCode_AboveMonthlyExpenditureLimit.toString().equals(result.getCode())){
					code.appendChild(doc.createTextNode("110"));
					description.appendChild(doc.createTextNode("Exceeded monthly expenditure limit for the integration partner"));				
				}
				else{
					code.appendChild(doc.createTextNode("106"));	
				}
				root.appendChild(code);
				root.appendChild(description);
			}
		}
		if (ob instanceof CMInterswitchCashinStatus) {
			root = doc.createElement("CashInStatusResponse");
			doc.appendChild(root);
			CMInterswitchCashinStatus cashInStatus = (CMInterswitchCashinStatus) ob;
			Element insID = doc.createElement("InstitutionId");
			insID.appendChild(doc.createTextNode(cashInStatus.getInstitutionID()));
			log.info("Cashin Status Resonse --> " + result.getCode());
			code.appendChild(doc.createTextNode(CmFinoFIX.NotificationCode_CashInToEMoneyCompletedToSender.toString().equals(result.getCode()) ? "100"
			        : "101"));
			root.appendChild(insID);
			root.appendChild(code);
		}

		
		if (result.getSctlID() != null) {
			Element refNo = doc.createElement("ReferenceNumber");
			refNo.appendChild(doc.createTextNode(result.getSctlID().toString()));
			root.appendChild(refNo);
		}

		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		DOMSource source = new DOMSource(doc);

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		StreamResult sresult = new StreamResult(stream);

		transformer.transform(source, sresult);

		String finalXML = stream.toString();
		return finalXML;
	}

	private String constructReponseXML(Object ob, String insID, String strMdn) throws ParserConfigurationException,
	        TransformerConfigurationException, TransformerFactoryConfigurationError, TransformerException, DOMException, ParseException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.newDocument();

		Element root = null;
		Element institutionID = doc.createElement("InstitutionId");
		Element customers = doc.createElement("Customers");
		Element customer = doc.createElement("Customer");
		Element code = doc.createElement("ResponseCode");
		Element mdn = doc.createElement("Mdn");
		Element alternateMdn = doc.createElement("AlternateMdn");
		Element customerDescription = doc.createElement("CustomerDescription");
		Element firstName = doc.createElement("FirstName");
		Element lastName = doc.createElement("LastName");
		Element description = doc.createElement("Description");
		Element otherName = doc.createElement("OtherName");
		Element eMail = doc.createElement("Email");
		Element phone = doc.createElement("Phone");
		Element tpcode = doc.createElement("ThirdPartyCode");
		Element amount = doc.createElement("Amount");
		Element paymentItems = doc.createElement("PaymentItems");
		Element pmtItemsItem = doc.createElement("Item");
		Element prodName = doc.createElement("ProductName");
		Element prodCode = doc.createElement("ProductCode");
		Element quantity = doc.createElement("Quantity");
		Element price = doc.createElement("Price");
		Element subtotal = doc.createElement("Subtotal");
		Element tax = doc.createElement("Tax");
		Element total = doc.createElement("Total");
		Element dob = doc.createElement("Dob");
		Element merchandizeItem = doc.createElement("Item");
		Element pin = doc.createElement("Pin");
		Element serialNr = doc.createElement("SerialNr");
		Element msg = doc.createElement("Msg");
		Element merchandize = doc.createElement("Merchandize");
		root = doc.createElement("GetDetailsResponse");
		doc.appendChild(root);
		if (ob instanceof Subscriber) {
			Subscriber subs = (Subscriber) ob;
			if(CmFinoFIX.SubscriberStatus_Active.equals(subs.getStatus()))
			{
				description.appendChild(doc.createTextNode(MessageText._("Subscriber is active and can receive funds")));
			}
			else if(CmFinoFIX.SubscriberStatus_InActive.equals(subs.getStatus()) && 
					!(CmFinoFIX.SubscriberRestrictions_Suspended.equals(subs.getRestrictions()) || 
							CmFinoFIX.SubscriberRestrictions_AbsoluteLocked.equals(subs.getRestrictions())||
								CmFinoFIX.SubscriberRestrictions_SecurityLocked.equals(subs.getRestrictions())))
					{
				description.appendChild(doc.createTextNode(MessageText._("Subscriber is active and can receive funds")));
					}
			else{
			description.appendChild(doc.createTextNode(MessageText._("Subscriber is not active and cannot receive funds")));
			}
			institutionID.appendChild(doc.createTextNode(insID));
			code.appendChild(doc.createTextNode("100"));
			mdn.appendChild(doc.createTextNode(strMdn));
			firstName.appendChild(doc.createTextNode(subs.getFirstName()));
			lastName.appendChild(doc.createTextNode(subs.getLastName()));
			eMail.appendChild(doc.createTextNode(subs.getEmail()));
			phone.appendChild(doc.createTextNode(subscriberService.normalizeMDN(strMdn)));
			tpcode.appendChild(doc.createTextNode(""));
			amount.appendChild(doc.createTextNode(""));
			dob.appendChild(doc.createTextNode(formatDOB(subs.getDateOfBirth().toString())));
			pin.appendChild(doc.createTextNode(""));
			serialNr.appendChild(doc.createTextNode(""));
			msg.appendChild(doc.createTextNode(""));
			root.appendChild(institutionID);
			
			customer.appendChild(code);
			customer.appendChild(description);			
			customer.appendChild(mdn);
			customer.appendChild(alternateMdn);
			customer.appendChild(customerDescription);
			customer.appendChild(firstName);
			customer.appendChild(lastName);
			customer.appendChild(otherName);
			customer.appendChild(eMail);
			customer.appendChild(phone);
			customer.appendChild(tpcode);
			customer.appendChild(amount);
			customer.appendChild(dob);
			pmtItemsItem.appendChild(prodName);
			pmtItemsItem.appendChild(prodCode);
			pmtItemsItem.appendChild(quantity);
			pmtItemsItem.appendChild(price);
			pmtItemsItem.appendChild(subtotal);
			pmtItemsItem.appendChild(tax);
			pmtItemsItem.appendChild(total);
			paymentItems.appendChild(pmtItemsItem);
			merchandizeItem.appendChild(pin);
			merchandizeItem.appendChild(serialNr);
			merchandizeItem.appendChild(msg);
			merchandize.appendChild(merchandizeItem);
			customer.appendChild(paymentItems);
			customer.appendChild(merchandize);
			customers.appendChild(customer);
			
			root.appendChild(customers);
			
		}
		else {
			institutionID.appendChild(doc.createTextNode(insID));
			code.appendChild(doc.createTextNode("101"));
			mdn.appendChild(doc.createTextNode(strMdn));
			firstName.appendChild(doc.createTextNode(""));
			lastName.appendChild(doc.createTextNode(""));
			dob.appendChild(doc.createTextNode(""));
		}

		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		DOMSource source = new DOMSource(doc);

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		StreamResult sresult = new StreamResult(stream);

		transformer.transform(source, sresult);

		String finalXML = stream.toString();
		return finalXML;
	}

	private String formatDOB(String dateOfBirth) throws ParseException {
		DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S");
		Date date = inputFormat.parse(dateOfBirth);
		DateFormat outputFormat = new SimpleDateFormat("ddMMyyyy");
		String outputString = outputFormat.format(date);
		return outputString;
	}

	private boolean validateXML(Document doc) {
		try {
			String testContent = doc.getDocumentElement().getNodeName();
			if (!doc.getDocumentElement().getNodeName().equals(TEXT_CASHINREQUEST)
			        || !doc.getDocumentElement().getNodeName().equals(TEXT_GETDETAILSREQUEST)) {
				return true;
			}
			else {
				return false;
			}

			// if (doc.getDocumentElement().getChildNodes().getLength() != 15) {
			// log.error("cashinrequest element doesn't have 15 children elements");
			// return false;
			// }
		}
		catch (Exception ex) {
			ex.printStackTrace();
			log.error("Exception " + ex + " occured. Validatexml failed");
			return false;
		}
	}
	private String processCashin(Exchange exchange) throws InvalidXMLException {
		String xmlDoc = exchange.getIn().getBody(String.class);
		log.info("received xml message -- >" + xmlDoc);
		mFinoServiceProvider msp =channelCodeService.getMFSPbyID(1);
;
/*		if (msp == null) {
			MfinoServiceProviderDAO mspDao = DAOFactory.getInstance().getMfinoServiceProviderDAO();
			msp = mspDao.getById(1);
		}
*/
		log.info("building cashindetails object");

		// xmlDoc = xmlDoc.replaceAll("\\s+", "");

		try {
			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = null;
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(xmlDoc));
			try {
				log.info("parsing the xml ");
				try {
					doc = db.parse(is);
					if (doc == null) {
						log.error("doc is null");
					}

				}
				catch (Exception ex) {
					log.error("Cannot parse the xml." + ex.getMessage());
				}
				log.info("validating the xml");
				if (!validateXML(doc))
					throw new InvalidXMLException("Validation of XML request failed");

				log.info("validation through");
				Element root = doc.getDocumentElement();
				log.info("root extracted");
				String strRootTag = root.getTagName();
				log.info("RootTag: " + root.getTagName());
				if (strRootTag.compareTo("CashInRequest") == 0) {

					log.info("handling Cashin Request");
					TransactionDataContainerImpl dataContainer = new TransactionDataContainerImpl();
					CMInterswitchCashin cashIn = null;
					try {
						cashIn = buildCashinDetailsObject(xmlDoc, doc, msp);

						if (cashIn == null) {
							log.error("Cashin is null .Cannot continue");
							throw new InvalidXMLException("cashin is null");
						}
						// log.info("cashin object -->"+cashIn.DumpFields());
					}
					catch (InvalidXMLException ex) {
						throw (ex);
					}
					log.info("cashIn object -->" + cashIn.DumpFields());

					
					ChannelCode cc = channelCodeService.getChannelCodeByChannelCode(cashIn.getChannelCode());
					dataContainer.setMsg(cashIn);
					
					FIXMessageHandler handler = null;
					if (cashIn.getAmount().compareTo(new BigDecimal(0)) > 0) {
						log.info("As the amount is >0 , treating this as a cashin request");
					
						XMLResult result = null;
						log.info("Passing the request to inquiry handler");
						try{
							((CMBase) dataContainer.getMsg()).setTransactionIdentifier((String)exchange.getIn().getHeader(MCEUtil.BREADCRUMB_ID));
							
							TransferInquiryXMLResult inquiryResult=(TransferInquiryXMLResult) integrationCashinInquiryHandler.preprocess(dataContainer, cc);
							if(StringUtils.isNotBlank(inquiryResult.getCode()) || inquiryResult.getNotificationCode()!=null){
								result = (XMLResult) inquiryResult;
							}
							else{
								CFIXMsg response =  integrationCashinInquiryHandler.communicate(dataContainer, cc);
								inquiryResult =  (TransferInquiryXMLResult) integrationCashinInquiryHandler.postprocess(dataContainer, cc,response,inquiryResult);
							}
							if (!CmFinoFIX.NotificationCode_BankAccountToBankAccountConfirmationPrompt.toString().equals(inquiryResult.getCode())) {
								log.info("Interswitch CashIn inquiry failed");
								result = (XMLResult) inquiryResult;
							}
							else{
								dataContainer.setTransferID(inquiryResult.getTransferID());
								dataContainer.setParentTxnID(inquiryResult.getParentTransactionID());
								dataContainer.setConfirmed(true);
									log.info("Passing the request to confirm handler");
								WalletConfirmXMLResult confirmResult = (WalletConfirmXMLResult) integrationCashinConfirmHandler.preprocess(dataContainer, cc,(String)exchange.getIn().getHeader(MCEUtil.BREADCRUMB_ID));
								if(StringUtils.isNotBlank(confirmResult.getCode()) || confirmResult.getNotificationCode()!=null){
									result = (XMLResult) confirmResult;
								}
								else{
									CFIXMsg response  = ( integrationCashinConfirmHandler).communicate(dataContainer, cc);
									confirmResult = (WalletConfirmXMLResult) integrationCashinConfirmHandler.postprocess(dataContainer, cc, response, confirmResult);
									result = (XMLResult) confirmResult;
								}
							}
						}catch(IllegalArgumentException e){
							result = new XMLResult();
							result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidData);
							result.setCode(CmFinoFIX.NotificationCode_InvalidData.toString());
						}
						log.info("got the response from the trxnAwarehandler.notificationcode=" + result.getCode());
						String finalXML = constructReponseXML(cashIn, result);
						log.info("API XML output ----> " + finalXML);
						return finalXML;
					}
					else if (cashIn.getAmount().compareTo(new BigDecimal(0)) < 0) {
						log.info("As the amount is <0 , treating this as a cashin reversal request");
 						XMLResult result = (XMLResult) cashinReversalHandler.handle(cashIn, cc,(String)exchange.getIn().getHeader(MCEUtil.BREADCRUMB_ID));
						log.info("got the response from InterswitchCashinHandler.notificationcode=" + result.getCode());
						String finalXML = constructReponseXML(cashIn, result);
						log.info("API XML output ----> " + finalXML);
						return finalXML;
					}
				}
				if (strRootTag.compareTo("GetDetailsRequest") == 0) {
					String finalXML = processGetDetails(xmlDoc, doc);
					log.info("API XML output ----> " + finalXML);
					return finalXML;
				}
				if (strRootTag.compareTo("CashInStatusRequest") == 0) {
					CMInterswitchCashinStatus cashInStatus = null;
					try {
						cashInStatus = buildCashinStatusObject(xmlDoc, doc, msp);
						if (cashInStatus == null) {
							log.error("Cashin Status is null .Cannot continue");
							throw new InvalidXMLException("cashin Status is null");
						}
					}
					catch (InvalidXMLException ex) {
						throw (ex);
					}
					log.info("cashIn Status object -->" + cashInStatus.DumpFields());
					ChannelCode cc = channelCodeService.getChannelCodeByChannelCode(cashInStatus.getChannelCode());

//					ChannelCode cc = DAOFactory.getInstance().getChannelCodeDao().getByChannelCode(cashInStatus.getChannelCode());
					InterswitchCashinStatusHandlerImpl handler = new InterswitchCashinStatusHandlerImpl(cashInStatus, cc,(String)exchange.getIn().getHeader(MCEUtil.BREADCRUMB_ID));
					XMLResult result = (XMLResult) handler.handle();

					log.info("got the response from InterswitchCashinHandler.Constructing api xml output");

					String finalXML = constructReponseXML(cashInStatus, result);
					log.info("API XML output ----> " + finalXML);
					return finalXML;
				}
			}
			catch (InvalidXMLException ex) {
				throw (ex);
			}
			catch (Exception ex) {
				log.error(ex.getMessage(), ex);
			}

		}
		catch (ParserConfigurationException ex) {
			InvalidXMLException e = new InvalidXMLException(ex);
			e.fillInStackTrace();
			throw e;
		}
		return null;
	}

	private String processGetDetails(String xmlDoc, Document doc) throws TransformerConfigurationException, ParserConfigurationException,
	        TransformerFactoryConfigurationError, TransformerException, DOMException, ParseException {
		String finalXML = null;
		NodeList nodes = doc.getElementsByTagName("GetDetailsRequest");
		Element xmlGetDetailsRequest = (Element) nodes.item(0);
		String mdn = getElementContent(xmlGetDetailsRequest, TEXT_MDN);
		String institutionID = getElementContent(xmlGetDetailsRequest, TEXT_INSTITUTION_ID);
        
	    Subscriber subscriber = subscriberMdnService.getSubscriberFromMDN(mdn);
		Integer validationResult =transactionApiValidationService.validateSubscriberAsDestinationString(mdn);
		
 		if (( CmFinoFIX.ResponseCode_Success.equals(validationResult) || CmFinoFIX.NotificationCode_DestinationMDNIsNotActive.equals(validationResult) || CmFinoFIX.NotificationCode_DestinationMDNIsRestricted.equals(validationResult))) {
 			finalXML = constructReponseXML(subscriber, institutionID, mdn);
			return finalXML;
		}
		else if(CmFinoFIX.NotificationCode_SubscriberNotRegistered.equals(validationResult))
		{
			finalXML = "<GetDetailsResponse>" + "<InstitutionId>"+institutionID+"</InstitutionId>"+"<ResponseCode>101</ResponseCode>"+"<Mdn>"+mdn+"</Mdn>"+MessageText._("<Description>KYC not applicable,Cannot receive funds</Description>")+"<firstName></firstName><lastName></lastName><Dob></Dob>"+"</GetDetailsResponse>";
			return finalXML;
		}
		else if(CmFinoFIX.NotificationCode_DestinationMDNNotFound.equals(validationResult))
		{
			finalXML = "<GetDetailsResponse>" + "<InstitutionId>"+institutionID+"</InstitutionId>"+"<ResponseCode>101</ResponseCode>"+"<Mdn>"+mdn+"</Mdn>"+MessageText._("<Description>Subscriber Not Found</Description>")+"<firstName></firstName><lastName></lastName><Dob></Dob>"+ "</GetDetailsResponse>";
			return finalXML;
		}
		finalXML = "<GetDetailsResponse>" + "<PaymentLogId>-1</PaymentLogId><ResponseCode>111</ResponseCode>" + "</GetDetailsResponse>";
		return finalXML;
	}

	private CMInterswitchCashinStatus buildCashinStatusObject(String xmlDoc, Document doc, mFinoServiceProvider msp) {
		CMInterswitchCashinStatus status = new CMInterswitchCashinStatus();
		status.setChannelCode(CmFinoFIX.SourceApplication_Interswitch.toString());// FIXME
		                                                                          // fix
		                                                                          // later
		status.setIsSecure(false);
		status.setMessageType(CmFinoFIX.MessageType_InterswitchCashin);
		status.setMSPID(msp.getID());
		status.setMSPID(1l);
		status.setReceiveTime(DateTimeUtil.getLocalTime());
		status.setIsSystemIntiatedTransaction(false);
		status.setServletPath(CmFinoFIX.ServletPath_InterswitchWeb);
		status.setSourceApplication(CmFinoFIX.SourceApplication_Interswitch);
		// status.setSourceMessage(xmlDoc);

		NodeList nodes = doc.getElementsByTagName("CashInStatusRequest");
		Element xmlCashinStatusRequest = (Element) nodes.item(0);
		status.setInstitutionID(getElementContent(xmlCashinStatusRequest, TEXT_INSTITUTION_ID));
		String paymentLogID = getElementContent(xmlCashinStatusRequest, TEXT_REFERENCE_NUMBER);
		String custReference = null;
		int index = 18;
		if(paymentLogID.length()>index)
		{
			custReference = paymentLogID.substring(index);
			paymentLogID = paymentLogID.substring(0,index);
		}
		status.setReferenceNumber(Long.parseLong(paymentLogID));
		status.setCustReference(custReference);
		return status;
	}

	private CMInterswitchCashin buildCashinDetailsObject(String xmlDoc, Document doc, mFinoServiceProvider msp) throws InvalidXMLException {
		CMInterswitchCashin details = new CMInterswitchCashin();
		
		details.setIsSecure(false);
		details.setMessageType(CmFinoFIX.MessageType_InterswitchCashin);
		details.setMSPID(msp.getID());
		details.setMSPID(1l);
		details.setReceiveTime(DateTimeUtil.getLocalTime());
		details.setIsSystemIntiatedTransaction(false);
		details.setServletPath(CmFinoFIX.ServletPath_InterswitchWeb);
		details.setSourceApplication(CmFinoFIX.SourceApplication_Interswitch);
		details.setSourceMessage(xmlDoc);

		NodeList nodes = doc.getElementsByTagName("CashInRequest");
		Element xmlCashinRequest = (Element) nodes.item(0);
		String paymentLogID = getElementContent(xmlCashinRequest, TEXT_PAYMENT_LOG_ID);
		String custReference = null;
		int index = 18;
		if(paymentLogID.length()>index)
		{
			custReference = paymentLogID.substring(index);
			paymentLogID = paymentLogID.substring(0,index);
		}
		
		details.setPaymentLogID(paymentLogID);
		details.setCustReference(custReference);
		
		details.setAmount(new BigDecimal(getElementContent(xmlCashinRequest, TEXT_AMOUNT)));
		// details.setBankCode(getElementContent(payment, TEXT_BANK_CODE));
		// details.setBankName(getElementContent(payment, TEXT_BANK_NAME));
		// details.setBranchName(getElementContent(payment,
		// TEXT_BRANCH_NAME));
		details.setChannelName(getElementContent(xmlCashinRequest, TEXT_CHANNEL_NAME));
		// details.setCollectionsAccount(getElementContent(payment,
		// TEXT_COLLECTIONS_ACCOUNT));
		// details.setCustomerAddress(getElementContent(payment,
		// TEXT_CUSTOMER_ADDRESS));
		details.setTargetFirstName(getElementContent(xmlCashinRequest, TEXT_TARGET_FIRST_NAME));
		details.setTargetLastName(getElementContent(xmlCashinRequest, TEXT_TARGET_FIRST_NAME));
		details.setDestMDN(getElementContent(xmlCashinRequest, TEXT_TARGET_MDN));
		// details.setCustReference(getElementContent(payment,
		// TEXT_CUST_REFERENCE));
		details.setInitiatorName(getElementContent(xmlCashinRequest, TEXT_INITIATOR_NAME));
		// details.setDepositSlipNumber(getElementContent(payment,
		// TEXT_DEPOSIT_SLIP_NO));
		details.setInstitutionID(getElementContent(xmlCashinRequest, TEXT_INSTITUTION_ID));
		details.setInstitutionName(getElementContent(xmlCashinRequest, TEXT_INSTITUTION_NAME));
		// details.setLocation(getElementContent(payment, TEXT_LOCATION));
		details.setInitiatorMDN(getElementContent(xmlCashinRequest, TEXT_INITIATOR_MDN));
		details.setPaymentCurrency(getElementContent(xmlCashinRequest, TEXT_PAYMENT_CURRENCY));
		String date = getElementContent(xmlCashinRequest, TEXT_PAYMENT_DATE).trim();
		details.setPaymentDate(Timestamp.fromString(date, "yyyy-MM-dd"));
		details.setPaymentMethod(getElementContent(xmlCashinRequest, TEXT_PAYMENT_METHOD));
		details.setPaymentReference(getElementContent(xmlCashinRequest, TEXT_PAYMENT_REFERENCE));
		details.setReceiptNo(getElementContent(xmlCashinRequest, TEXT_RECEIPT_NO));
		details.setSourceMDN(getElementContent(xmlCashinRequest, TEXT_TARGET_MDN));

		details.setChannelCode(getElementContent(xmlCashinRequest, TEXT_CHANNEL_ID));
		
		return details;

	}

	public static void main(String[] args) throws Exception {

		String str = "<PaymentNotificationRequest><Payments><Payment><ProductGroupCode>HTTPSERVICE"
		        + "</ProductGroupCode><PaymentLogId>5391796</PaymentLogId><CustReference>2348059376342" + "</CustReference><AlternateCustReference>";

		// System.out.println(getPaymentLogId(str));
		
		String paymentLogID = "039315121129112648121129112648";
		String custReference = null;
		if(paymentLogID.length()>18)
		{
			custReference = paymentLogID.substring(18);
			paymentLogID = paymentLogID.substring(0,18);
		}
		System.out.println("First Half:"+custReference);
		System.out.println("Last Half:"+paymentLogID);

	}

	private String getElementContent(Element element, String name) {
		NodeList name3 = element.getElementsByTagName(name);
		if (name3 == null || name3.getLength() == 0)
		{
			log.info("CashIn Request: "+name+" value: ");
			return "";
		}
		Element line3 = (Element) name3.item(0);
		String value=getCharacterDataFromElement(line3);
		log.info("CashIn Request: "+name+" value: "+value);
		return value;
		
	}

	public String getCharacterDataFromElement(Element e) {
		Node child = e.getFirstChild();
		if (child instanceof CharacterData) {
			CharacterData cd = (CharacterData) child;
			return cd.getData().trim();
		}
		return "";
	}

	public String getPaymentLogId(String xml) {
		try {
			int start = xml.indexOf("<PaymentLogId>");
			int end = xml.indexOf("</PaymentLogId>");
			return xml.substring(start + 14, end);
		}
		catch (Exception ex) {
			return "-1";
		}
	}




}
