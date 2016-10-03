package com.mfino.cashin;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.CharacterData;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.MfinoServiceProviderDAO;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.MfinoServiceProvider;
import com.mfino.domain.Subscriber;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMInterswitchCashin;
import com.mfino.fix.CmFinoFIX.CMInterswitchCashinStatus;
import com.mfino.hibernate.Timestamp;
import com.mfino.mce.core.util.MCEUtil;
import com.mfino.result.XMLResult;
import com.mfino.service.TransactionIdentifierService;
import com.mfino.transactionapi.handlers.interswitch.CashinReversalHandler;
import com.mfino.transactionapi.handlers.interswitch.impl.InterswitchCashinStatusHandlerImpl;
import com.mfino.util.DateTimeUtil;
import com.mfino.validators.DestMDNValidator;

public class CashinMessageListener implements Processor {

	public static MfinoServiceProvider	msp;
	Logger	                           log	= LoggerFactory.getLogger(CashinMessageListener.class);

	MfinoServiceProvider	           mfs;

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
 
 	/*@Autowired
	@Qualifier("CashinReversalHandlerImpl")*/
	private CashinReversalHandler cashinReversalHandler;
	
	private CashinHandler cashinHandler;
	
 	public CashinHandler getCashinHandler() {
		return cashinHandler;
	}

	public void setCashinHandler(CashinHandler cashinHandler) {
		this.cashinHandler = cashinHandler;
	}

	/*@Autowired
	@Qualifier("TransactionIdentifierServiceImpl")*/
	private TransactionIdentifierService transactionIdentifierService;
 	
	public TransactionIdentifierService getTransactionIdentifierService() {
		return transactionIdentifierService;
	}

	public void setTransactionIdentifierService(
			TransactionIdentifierService transactionIdentifierService) {
		this.transactionIdentifierService = transactionIdentifierService;
	}

	public void setCashinReversalHandler(CashinReversalHandler cashinReversalHandler){
		this.cashinReversalHandler = cashinReversalHandler;
	}
	
	public CashinReversalHandler getCashinReversalHandler(){
		return cashinReversalHandler;
	}
	
	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public void process(Exchange exchange) throws Exception {
		String syncId = exchange.getIn().getHeader("synchronous_request_id").toString();
		String cashinChannel = exchange.getIn().getHeader("FrontendID").toString();
		String jmsMessageID = exchange.getIn().getHeader("JMSMessageID").toString();
		String finalXML;
		
		String request = (String) exchange.getIn().getBody();
		Map<String, Object> headers = exchange.getIn().getHeaders();

		//getting the sourceMDN from the request to create the transactionIdentifier at start
		String mdnTag = getMdnTag(request);
		String uniqueIdMDN = getCustomerMDN(request,mdnTag);
		//TransactionIdentifierServiceImpl transactionIdentifierService = new TransactionIdentifierServiceImpl();
		String trxnIdentifier = transactionIdentifierService.generateTransactionIdentifier(uniqueIdMDN);
		MCEUtil.setBreadCrumbId(headers, trxnIdentifier);
		log.info("Transaction Identifier created in Smart CashinMessageListener with ID -->"+trxnIdentifier);
		
		log.info("channel header -->" + cashinChannel);
		log.info("synchronous request header -->" + syncId);
		try {
			finalXML = processCashin(exchange);
		}
		catch (InvalidXMLException ex) {
			log.error("invalid xml received from BSM");
			String str = "<CashInResponse><PaymentLogId>-1</PaymentLogId><ResponseCode>101</ResponseCode><ReferenceNumber>-1</ReferenceNumber></CashInResponse>";
			exchange.getIn().setBody(str);
			return;
		}

		// headers.clear();

		// headers.put("synchronous_request_id", syncId);
		// headers.put("FrontendID", cashinChannel);
		// headers.put("JMSMessageID", jmsMessageID);

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
			logID.appendChild(doc.createTextNode(cashIn.getPaymentLogID()));

			if (CmFinoFIX.NotificationCode_CashInToEMoneyCompletedToSender.toString().equals(result.getCode())
			        || CmFinoFIX.NotificationCode_AutoReverseSuccessToSource.toString().equals(result.getCode()))
				code.appendChild(doc.createTextNode("100"));
			else
				code.appendChild(doc.createTextNode("106"));

			root.appendChild(logID);
		}
		if (ob instanceof CMInterswitchCashinStatus) {
			root = doc.createElement("CashInStatusResponse");
			doc.appendChild(root);
			CMInterswitchCashinStatus cashInStatus = (CMInterswitchCashinStatus) ob;
			Element insID = doc.createElement("InstitutionId");
			insID.appendChild(doc.createTextNode(cashInStatus.getInstitutionID()));
			code.appendChild(doc.createTextNode(CmFinoFIX.NotificationCode_CommodityTransaferDetails.toString().equals(result.getCode()) ? "100"
			        : "101"));
			root.appendChild(insID);
		}

		root.appendChild(code);
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
		Element logID = doc.createElement("PaymentLogId");
		Element code = doc.createElement("ResponseCode");
		Element mdn = doc.createElement("Mdn");
		Element firstName = doc.createElement("firstName");
		Element lastName = doc.createElement("lastName");
		Element dob = doc.createElement("Dob");
		root = doc.createElement("GetDetailsResponse");
		doc.appendChild(root);
		if (ob instanceof Subscriber) {
			Subscriber subs = (Subscriber) ob;
			logID.appendChild(doc.createTextNode(insID));
			code.appendChild(doc.createTextNode("100"));
			mdn.appendChild(doc.createTextNode(strMdn));
			firstName.appendChild(doc.createTextNode(subs.getFirstname()));
			lastName.appendChild(doc.createTextNode(subs.getLastname()));
			dob.appendChild(doc.createTextNode(formatDOB(subs.getDateofbirth().toString())));
			root.appendChild(logID);
			root.appendChild(code);
			root.appendChild(mdn);
			root.appendChild(firstName);
			root.appendChild(lastName);
			root.appendChild(dob);
		}
		else {
			logID.appendChild(doc.createTextNode(insID));
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
		if (msp == null) {
			MfinoServiceProviderDAO mspDao = DAOFactory.getInstance().getMfinoServiceProviderDAO();
			msp = mspDao.getById(1);
		}

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
					CMInterswitchCashin cashIn = null;
					try {
						cashIn = buildCashinDetailsObject(xmlDoc, doc);

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

					ChannelCode cc = DAOFactory.getInstance().getChannelCodeDao().getByChannelCode(cashIn.getChannelCode());

					//FIXMessageHandler handler = null;
					if (cashIn.getAmount().compareTo(new BigDecimal(0)) > 0) {
						log.info("As the amount is >0 , trating this as a cashin request");						
						//handler = new CashinHandler(cashIn, cc,(String)exchange.getIn().getHeader(MCEUtil.BREADCRUMB_ID));
						XMLResult result = (XMLResult) cashinHandler.handle(cashIn, cc,(String)exchange.getIn().getHeader(MCEUtil.BREADCRUMB_ID));
						log.info("got the response from BSMCashinHandler.notificationcode=" + result.getCode());
						String finalXML = constructReponseXML(cashIn, result);
						log.info("API XML output ----> " + finalXML);
						return finalXML;
					}
					else if (cashIn.getAmount().compareTo(new BigDecimal(0)) < 0) {
						log.info("As the amount is <0 , trating this as a cashin reversal request");
 
						XMLResult result = (XMLResult) cashinReversalHandler.handle(cashIn, cc,(String)exchange.getIn().getHeader(MCEUtil.BREADCRUMB_ID));
						log.info("got the response from BSMCashinHandler.notificationcode=" + result.getCode());
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
						cashInStatus = buildCashinStatusObject(xmlDoc, doc);
						if (cashInStatus == null) {
							log.error("Cashin Status is null .Cannot continue");
							throw new InvalidXMLException("cashin Status is null");
						}
					}
					catch (InvalidXMLException ex) {
						throw (ex);
					}
					log.info("cashIn Status object -->" + cashInStatus.DumpFields());
					ChannelCode cc = DAOFactory.getInstance().getChannelCodeDao().getByChannelCode(cashInStatus.getChannelCode());
					InterswitchCashinStatusHandlerImpl handler = new InterswitchCashinStatusHandlerImpl(cashInStatus, cc,(String)exchange.getIn().getHeader(MCEUtil.BREADCRUMB_ID));
					XMLResult result = (XMLResult) handler.handle();

					log.info("got the response from BSMCashinHandler.Constructing api xml output");

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
		DestMDNValidator destMdnValidator = new DestMDNValidator(mdn);
		Integer validationResult = destMdnValidator.validate();
		if (!CmFinoFIX.ResponseCode_Success.equals(validationResult)) {
			finalXML = "<GetDetailsResponse>" + "<PaymentLogId>-1</PaymentLogId><Status>111</Status>" + "</GetDetailsResponse>";
			return finalXML;
		}
		Subscriber subscriber = destMdnValidator.getSubscriber();
		finalXML = constructReponseXML(subscriber, institutionID, mdn);
		return finalXML;
	}

	private CMInterswitchCashinStatus buildCashinStatusObject(String xmlDoc, Document doc) {
		CMInterswitchCashinStatus status = new CMInterswitchCashinStatus();
		status.setChannelCode(CmFinoFIX.SourceApplication_Interswitch.toString());// FIXME
																				  // fix
																				  // later
		status.setIsSecure(false);
		status.setMessageType(CmFinoFIX.MessageType_InterswitchCashin);
		status.setMSPID(msp.getId().longValue());
		status.setMSPID(1l);
		status.setReceiveTime(DateTimeUtil.getLocalTime());
		status.setIsSystemIntiatedTransaction(false);
		status.setServletPath(CmFinoFIX.ServletPath_InterswitchWeb);
		status.setSourceApplication(CmFinoFIX.SourceApplication_Interswitch);
		// status.setSourceMessage(xmlDoc);

		NodeList nodes = doc.getElementsByTagName("CashInStatusRequest");
		Element xmlCashinStatusRequest = (Element) nodes.item(0);
		status.setInstitutionID(getElementContent(xmlCashinStatusRequest, TEXT_INSTITUTION_ID));
		status.setReferenceNumber(new Long(getElementContent(xmlCashinStatusRequest, TEXT_REFERENCE_NUMBER)));
		return status;
	}

	private CMInterswitchCashin buildCashinDetailsObject(String xmlDoc, Document doc) throws InvalidXMLException {
		CMInterswitchCashin details = new CMInterswitchCashin();
		details.setChannelCode(CmFinoFIX.SourceApplication_Interswitch.toString());// FIXME
																				   // fix
																				   // later
		details.setIsSecure(false);
		details.setMessageType(CmFinoFIX.MessageType_InterswitchCashin);
		details.setMSPID(msp.getId().longValue());
		details.setMSPID(1l);
		details.setReceiveTime(DateTimeUtil.getLocalTime());
		details.setIsSystemIntiatedTransaction(false);
		details.setServletPath(CmFinoFIX.ServletPath_InterswitchWeb);
		details.setSourceApplication(CmFinoFIX.SourceApplication_Interswitch);
		details.setSourceMessage(xmlDoc);

		NodeList nodes = doc.getElementsByTagName("CashInRequest");
		Element xmlCashinRequest = (Element) nodes.item(0);
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
		details.setPaymentLogID(getElementContent(xmlCashinRequest, TEXT_PAYMENT_LOG_ID));
		details.setPaymentMethod(getElementContent(xmlCashinRequest, TEXT_PAYMENT_METHOD));
		details.setPaymentReference(getElementContent(xmlCashinRequest, TEXT_PAYMENT_REFERENCE));
		details.setReceiptNo(getElementContent(xmlCashinRequest, TEXT_RECEIPT_NO));
		details.setSourceMDN(getElementContent(xmlCashinRequest, TEXT_TARGET_MDN));

		return details;

	}

	public static void main(String[] args) throws Exception {

		BufferedReader br = new BufferedReader(new FileReader(new File(
		        "A:\\MFS_V2_6\\GTBank\\InterswitchCashInMock\\src\\main\\java\\com\\mfino\\gt\\interswitch\\cashin\\Test2.xml")));

		String str = "", str1 = "";

		while ((str = br.readLine()) != null) {
			str1 = str1 + str;
		}

		// CashinMessageListener listener = new CashinMessageListener();
		// CMInterswitchCashin cashin= listener.buildDetailsObject(str1);

	}

	private String getElementContent(Element element, String name) {
		NodeList name3 = element.getElementsByTagName(name);
		if (name3 == null || name3.getLength() == 0)
			return "";
		Element line3 = (Element) name3.item(0);
		return getCharacterDataFromElement(line3);
	}

	public String getCharacterDataFromElement(Element e) {
		Node child = e.getFirstChild();
		if (child instanceof CharacterData) {
			CharacterData cd = (CharacterData) child;
			return cd.getData().trim();
		}
		return "";
	}
	
	/**
	 * extracts the source mdn from the request and returns it.Incase of exception returns -1
	 * @param request
	 * @param mdnString
	 * @return
	 */
	private String getCustomerMDN(String request,String mdnString) {
		try {
			int start = request.indexOf("<"+mdnString);
			int end = request.indexOf("</"+mdnString);
			return request.substring(start + mdnString.length()+2, end);
		}
		catch (Exception ex) {
			log.error("could not extract mdn from request for transactionIdentifier");
			return "-1";
		}
	}
	
	/**
	 * returns the integration related tag that contains the sourceMDN
	 * @param request
	 * @return
	 */
	private String getMdnTag(String request) {
		String mdnTag = null;
		if(request.contains("<CustReference>")){
			mdnTag = "CustReference";
		}
		else if(request.contains("<TargetMdn>")){
			mdnTag = "TargetMdn";
		}
		else if(request.contains("<Mdn>")){
			mdnTag = "Mdn";
		}
		return mdnTag;
	}

}
