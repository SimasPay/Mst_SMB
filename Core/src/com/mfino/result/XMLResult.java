/**
 * 
 */
package com.mfino.result;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.bouncycastle.crypto.params.KeyParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.domain.Bank;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.Denomination;
import com.mfino.domain.Partner;
import com.mfino.domain.Pocket;
import com.mfino.domain.SCTLSettlementMap;
import com.mfino.domain.Service;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBalanceInquiryFromBank;
import com.mfino.fix.CmFinoFIX.CMGetLastTransactionsFromBank;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.handlers.MultixResponseObject;
import com.mfino.hibernate.Timestamp;
import com.mfino.mailer.NotificationWrapper;
import com.mfino.service.MfinoService;
import com.mfino.service.NotificationMessageParserService;
import com.mfino.service.NotificationService;
import com.mfino.service.PartnerService;

/**
 * @author Deva
 * 
 */

/*
 * The following services need to set to XMLResult before it is used:
 * 1)mfinoService
 * 2)partnerService
 * 3)notificationService
 * 4)notificationMessageParserService
 * Else this class will throw a NULL POINTER EXCEPTION
 * This assumes that the xml result generated has to be written to the response
 * output stream
 */

public class XMLResult extends Result {
	
	  //Before Correcting errors reported by Findbugs:
		//	protected static Logger	    log	          = LoggerFactory.getLogger(XMLResult.class);
	
	  //After Correcting the errors reported by Findbugs
	protected static final Logger	    log	          = LoggerFactory.getLogger(XMLResult.class);

	private MfinoService mfinoService;
	
	private PartnerService partnerService;
	
	private NotificationMessageParserService notificationMessageParserService;
	
	private NotificationService notificationService;
	
	private OutputStream	    writer;

	private String	            XMLResultType;
	private Long	            TransactionID;
	private Long	            sctlID;
	private Long	            parentTransactionID;
	private String	            BankName;
	private BigDecimal	        billAmount;
	private BigDecimal	        debitAmount;
	private BigDecimal	        serviceCharge;
	private BigDecimal	        creditAmount;
	
	private Map<String, String>	myXMlelements	= new HashMap<String, String>();

	private EncryptedXMLWriter	xmlWriter;

	private List<Denomination>	denominations;

	private KeyParameter	    keyParameter;

	private Bank	            bank;

	private String	            PocketDescription;

	private Pocket	            sourcePocket;

	private String	            partnerCode;
	private String	            destinationMDN;
	private int	                numberOfTriesLeft;
	private boolean				isUnRegistered;
	
	private BigDecimal minAmount;
	private BigDecimal maxAmount;

	private String oneTimePin;
	private BigDecimal amount;
	private String institutionID;
	private String integrationName;
	private String ipAddress;
	private String additionalInfo;
	private String registrationMedium;
	private String destinationName;
	private String destinationAccountNumber;
	private String otpExpirationTime;
	private String responseStatus;
	private String transID;
	private String parentTransID;
	private String sourceMDN;
	private String cardPan;
	private String maxFavoriteCount;
	private String favoriteLabel;
	private String favoriteValue;
	private String kycLevel;
	private String cardAlias;
	private String oldCardAlias;
	private String nickName;
	private String filePath;
	
	private String publicKeyExponent;
	
	private String publicKeyModulus;
	private String idNumber;
	
	public String getPublicKeyExponent() {
		return publicKeyExponent;
	}

	public void setPublicKeyExponent(String publicKeyExponent) {
		this.publicKeyExponent = publicKeyExponent;
	}

	public String getPublicKeyModulus() {
		return publicKeyModulus;
	}

	public void setPublicKeyModulus(String publicKeyModulus) {
		this.publicKeyModulus = publicKeyModulus;
	}
		
	public String getCardPan() {
		return cardPan;
	}

	public void setCardPan(String cardPan) {
		this.cardPan = cardPan;
	}

	public String getSourceMDN() {
		return sourceMDN;
	}

	public void setSourceMDN(String sourceMDN) {
		this.sourceMDN = sourceMDN;
	}

	public String getParentTransID() {
		return parentTransID;
	}

	public void setParentTransID(String parentTransID) {
		this.parentTransID = parentTransID;
	}

	public String getTransID() {
		return transID;
	}

	public void setTransID(String transID) {
		this.transID = transID;
	}

	public String getResponseStatus() {
		return responseStatus;
	}

	public void setResponseStatus(String responseStatus) {
		this.responseStatus = responseStatus;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Pocket getSourcePocket() {
		return sourcePocket;
	}

	public void setSourcePocket(Pocket sourcePocket) {
		this.sourcePocket = sourcePocket;
	}

	public Long getTransactionID() {
		return TransactionID;
	}

	public void setTransactionID(Long transactionID) {
		TransactionID = transactionID;
	}

	public void setPocketDescription(String pocketType) {
		PocketDescription = pocketType;
	}

	private String getPocketType() {
		return PocketDescription;
	}

	public String getRegistrationMedium() {
		return registrationMedium;
	}

	public void setRegistrationMedium(String registrationMedium) {
		this.registrationMedium = registrationMedium;
	}

	public void setXMlelements(Map<String, String> xMlelements) {
		myXMlelements = xMlelements;
	}

	public Map<String, String> getXMlelements() {
		return myXMlelements;
	}

	public XMLResult() {
		getXMlelements().put("transactionTime", null);
		getXMlelements().put("message", null);
		getXMlelements().put("code", null);
		getXMlelements().put("refID", null);
	}

	// builds the message,code from the NotificationCode, or MultixResponse
	public void buildMessage() throws XMLStreamException {
		String code = null;
		String message = null;
		Pocket pocket = null;
		List<CommodityTransfer> ctList = null;
		List<SCTLSettlementMap> psList = null;
		
		List<CMGetLastTransactionsFromBank.CGEntries> lastBankTrxnsList = null;
		List<CMGetLastTransactionsFromBank.CGEntries> nfcTrxHistory = null;
		List<CMBalanceInquiryFromBank.CGEntries> nfcCardBalances = null;
		if (getPocketList() != null && getPocketList().size() > 0) {
			pocket = getPocketList().get(0);
		}
		if(getNfcCardBalances() != null && getNfcCardBalances().size() > 0)
		{
			nfcCardBalances = getNfcCardBalances();
		}
		if (getTransactionList() != null && getTransactionList().size() > 0) {
			ctList = getTransactionList();
		}
		if (getPendingSettlements() != null && getPendingSettlements().size() > 0) {
			psList = getPendingSettlements();
		}
		if (getLastBankTrxnList() != null && getLastBankTrxnList().size() > 0) {
			lastBankTrxnsList = getLastBankTrxnList();
		}
		if (getNfcTransactionHistory() != null && getNfcTransactionHistory().size() > 0) {
			nfcTrxHistory = getNfcTransactionHistory();
		}
		if (getMultixResponse() == null) {
			code = String.valueOf(getNotificationCode());
			StringBuilder messageBuilder = new StringBuilder();
			NotificationWrapper notificationWrapper = getNotificationWrapper(notificationService,CmFinoFIX.NotificationMethod_Web);
			notificationWrapper.setSourcePocket(pocket);
			notificationWrapper.setPocketType(getPocketType());
			notificationWrapper.setTransactionId(getTransactionID());
			notificationWrapper.setBankName(getBankName());
			notificationWrapper.setBank(getBank());
			notificationWrapper.setBillAmount(getBillAmount());
			notificationWrapper.setServiceCharge(getServiceCharge());
			notificationWrapper.setTransactionAmount(getDebitAmount());
			notificationWrapper.setDestMDN(getDestinationMDN());
			notificationWrapper.setNumberOfTriesLeft(getNumberOfTriesLeft());
			notificationWrapper.setPartnerCode(getPartnerCode());
			notificationWrapper.setSctlID(getSctlID());
			notificationWrapper.setMinAmount(getMinAmount());
			notificationWrapper.setMaxAmount(getMaxAmount());
			notificationWrapper.setOneTimePin(getOneTimePin());
			notificationWrapper.setAmount(getAmount());
			notificationWrapper.setValidDenominations(getValidDenominations());
			notificationWrapper.setInstitutionID(getInstitutionID());
			notificationWrapper.setIntegrationName(getIntegrationName());
			notificationWrapper.setOtpExpirationTime(getOtpExpirationTime());
			notificationWrapper.setIPAddress(getIPAddress());
			notificationWrapper.setFirstName(getFirstName());
			notificationWrapper.setLastName(getLastName());
			notificationWrapper.setReceiverAccountName(getReceiverAccountName());
			notificationWrapper.setTransID(getTransID());
			notificationWrapper.setParentTransID(getParentTransID());
			notificationWrapper.setSourceMDN(getSourceMDN());
			notificationWrapper.setCardPan(getCardPan());
			notificationWrapper.setMaxFavoriteCount(getMaxFavoriteCount());
			notificationWrapper.setFavoriteLabel(getFavoriteLabel());
			notificationWrapper.setFavoriteValue(getFavoriteValue());
			notificationWrapper.setKycLevel(getKycLevel());
			notificationWrapper.setCardAlias(getCardAlias());
			notificationWrapper.setOldCardAlias(getOldCardAlias());
			notificationWrapper.setSubscriberStatus(getStatus());
			notificationWrapper.setNickName(getNickName());
			
			if (getSCTLList() != null) {
					notificationWrapper.setServiceChargeTransactionLog(getSCTLList());
					notificationWrapper.setSourceMDN(getSCTLList().getSourceMDN());
					notificationWrapper.setReceiverMDN(getSCTLList().getDestMDN());
			}
			
			if (ctList != null) {
				for (CommodityTransfer commodityTransfer : ctList) {
					notificationWrapper.setCommodityTransfer(commodityTransfer);
					messageBuilder.append(getNotificationMessageParserService().buildMessage(notificationWrapper, false));
					messageBuilder.append("\r\n");
				}
			}
			else if(psList != null)
			{
				for (SCTLSettlementMap pendingSettlement : psList) {
					notificationWrapper.setPendingSettlement(pendingSettlement);
					notificationWrapper.setAmount(pendingSettlement.getAmount());
					Service service = getMfinoService().getByServiceID(pendingSettlement.getServiceID());
					notificationWrapper.setService(service!=null?service.getDisplayName():"");
					
					Partner partner = getPartnerService().getPartnerById(pendingSettlement.getPartnerID());
            		notificationWrapper.setTradeName(partner!=null?partner.getTradeName():"");
					messageBuilder.append(getNotificationMessageParserService().buildMessage(notificationWrapper, false));
					messageBuilder.append("\r\n");
				}
			}
			else if (lastBankTrxnsList != null) {
				for (CMGetLastTransactionsFromBank.CGEntries entry : lastBankTrxnsList) {
					notificationWrapper.setLastBankTrxnEntry(entry);
					messageBuilder.append(getNotificationMessageParserService().buildMessage(notificationWrapper, false));
					messageBuilder.append("\r\n");
				}
			}
			else if (nfcTrxHistory != null) {
				for (CMGetLastTransactionsFromBank.CGEntries entry : nfcTrxHistory) {
					notificationWrapper.setLastBankTrxnEntry(entry);
					messageBuilder.append(getNotificationMessageParserService().buildMessage(notificationWrapper, false));
					messageBuilder.append("\r\n");
				}
			}
			else if (nfcCardBalances != null) {
				for (CMBalanceInquiryFromBank.CGEntries entry : nfcCardBalances) {
					notificationWrapper.setLastNFCCheckBalanceEntry(entry);
					messageBuilder.append(getNotificationMessageParserService().buildMessage(notificationWrapper, false));
					messageBuilder.append("\r\n");
				}
			}
			else {
				messageBuilder.append(getNotificationMessageParserService().buildMessage(notificationWrapper, false));
			}
			message = messageBuilder.toString();
		}
		else {
			CMJSError response = (CMJSError) getMultixResponse();
			message = response.getErrorDescription();

			message = message.trim();
			
			getXMlelements().put("success", response.getErrorCode().toString());

			if(response.getCode() != null)
			{
				code = response.getCode().toString();
			}
			else 
			{
				code = "0";
			}
			
			getXMlelements().put("ResponseString", message);
			if (response.getParentTransactionID() != null)
				getXMlelements().put("parentTxnID", response.getParentTransactionID().toString());

			if (response.getPaymentInquiryDetails() != null)
				getXMlelements().put("billDetails", response.getPaymentInquiryDetails().toString());

			if (response.getBillPaymentReferenceID() != null) {
				String str[] = response.getBillPaymentReferenceID().split(",");
				getXMlelements().put("refID", str[0].trim());
				getXMlelements().put("amount", str[1].trim());
			}
			
			if (response.getTransferID() != null) {
				getXMlelements().put("transferID", response.getTransferID().toString());
			}
		}

		getXMlelements().put("message", message);
		getXMlelements().put("code", code);
		getXMlelements().put("transactionTime", formatDate(getTransactionTime()));


	}

	
	protected String getTransferIDValue(String message) {
		try {
			String[] str = message.split("REF");
			if (str.length > 1) {
				str = str[1].split(":");
				if (str.length > 1)
					return str[1].trim();
			}
		}
		catch (Exception ex) {
			log.error("Error parsing Transfer ID from notification message: " + ex);
			return null;
		}
		return null;
	}

	/**
	 * Writes response's opening element "response" to the xml output steam
	 * 
	 * @author Gurram Karthik
	 * 
	 * @throws Exception
	 */
	protected void writeStartOfDocument() throws Exception {
		if (writer == null) {
			log.error("Result cannot be rendered with out a writer");
			throw new Exception("Result cannot be rendered with out a writer");
		}
		getXmlWriter().writeStartDocument("1.0");
		getXmlWriter().writeStartElement("response");
	}

	/**
	 * Writes response's closing element "response" to the xml output steam, and
	 * closes the writer
	 * 
	 * @author Gurram Karthik
	 * 
	 * @throws Exception
	 */

	protected void writeEndOfDocument() throws Exception {
		getXmlWriter().writeEndElement();
		getXmlWriter().writeEndDocument();
		getXmlWriter().flush();
		getXmlWriter().close();
	}

	@Override
	/**
	 * Writes message,code and TransactionTime to the xml output steam
	 * The format of the TransactionTime : dd/MM/yy HH:mm
	 *  
	 *  @author Gurram Karthik
	 *  
	 *  @throws Exception
	 */
	public void render() throws Exception {
		buildMessage();

		getXmlWriter().writeStartElement("message");
		getXmlWriter().writeAttribute("code", getXMlelements().get("code"), false);
		getXmlWriter().writeCharacters(getXMlelements().get("message"), false);
		getXmlWriter().writeEndElement();

		if (getDetailsOfPresentTransaction() != null) {
			getXmlWriter().writeStartElement("transactionTime");
			getXmlWriter().writeCharacters(formatDate(getDetailsOfPresentTransaction().getStartTime()), false);
			getXmlWriter().writeEndElement();
		}
		else {
			getXmlWriter().writeStartElement("transactionTime");
			getXmlWriter().writeCharacters(formatDate(getTransactionTime()), false);
			getXmlWriter().writeEndElement();
		}
		
		if (getSctlID() != null) {
			getXmlWriter().writeStartElement("sctlID");
			getXmlWriter().writeCharacters(getSctlID().toString(), false);
			getXmlWriter().writeEndElement();
		}
		
		if(getMfaMode() != null)
		{
			 getXmlWriter().writeStartElement("mfaMode");
			 getXmlWriter().writeCharacters(getMfaMode(),false);
			 getXmlWriter().writeEndElement();
		}
		
		if (getResponseStatus() != null) {
			getXmlWriter().writeStartElement("responseCode");
			getXmlWriter().writeCharacters(getResponseStatus(),true);
			getXmlWriter().writeEndElement();
		}
		
		
	}

	/**
	 * @return the writer
	 */
	public OutputStream getWriter() {
		return writer;
	}

	/**
	 * @param writer
	 *            the writer to set
	 */
	public void setWriter(OutputStream writer) {
		this.writer = writer;
		XMLOutputFactory factory = XMLOutputFactory.newInstance();
		try {
			XMLStreamWriter xmlWriter = factory.createXMLStreamWriter(writer);
			EncryptedXMLWriter eWriter = EncryptedXMLWriter.createEncryptedXMLWriter(xmlWriter);
			eWriter.setKeyParameter(keyParameter);
			setXmlWriter(eWriter);
		}
		catch (XMLStreamException xmlError) {
			log.error("Error creating XML Result file: ", xmlError);
		}
	}

	public MultixResponseObject processMultixResponse() {
		MultixResponseObject responseobj = new MultixResponseObject();
		CMJSError response = (CMJSError) getMultixResponse();
		String message = response.getErrorDescription();
		String code;
		message = message.trim();

		responseobj.setSuccess(message.substring(0, 1));
		int startIndex = message.indexOf('(');
		int endIndex = message.indexOf(')');
		if (startIndex != -1 && endIndex != -1) {
			code = message.substring(startIndex + 1, endIndex);
			message = message.substring(endIndex + 1);
		}
		else {
			code = "0";
		}
		responseobj.setCode(code);
		responseobj.setMessage(message);
		if (response.getParentTransactionID() != null)
			responseobj.setParenttransactionid(response.getParentTransactionID());
		Long transid = null;
		try {
			transid = Long.parseLong(getTransferIDValue(message));
		}
		catch (Exception error) {
			log.error("Exception in getting transactionid", error);
		}
		responseobj.setTransactionId(transid);
		return responseobj;
	}

	public void setXMLResultType(String xMLResultType) {
		XMLResultType = xMLResultType;
	}

	public String getXMLResultType() {
		return XMLResultType;
	}

	protected void setXmlWriter(EncryptedXMLWriter xmlWriter) {
		this.xmlWriter = xmlWriter;
	}

	protected EncryptedXMLWriter getXmlWriter() {
		return xmlWriter;
	}

	private Integer	BankCode;

	private String billerCode;
	
	private String validDenominations;

	public String getValidDenominations() {
		return validDenominations;
	}

	public void setValidDenominations(String validDenominations) {
		this.validDenominations = validDenominations;
	}

	public void setBankCode(Integer bankCode) {
		BankCode = bankCode;
	}

	public Integer getBankCode() {
		return BankCode;
	}

	public void setBankName(String bankName) {
		BankName = bankName;
	}

	public String getBankName() {
		return BankName;
	}

	public void setBillAmount(BigDecimal billAmount) {
		this.billAmount = billAmount;
	}

	public BigDecimal getBillAmount() {
		return billAmount;
	}

	public void setBank(Bank bank) {
		this.bank = bank;
	}

	public Bank getBank() {
		return bank;
	}

	public void setDenominations(List<Denomination> denominations) {
		this.denominations = denominations;
	}

	public List<Denomination> getDenominations() {
		return denominations;
	}

	public BigDecimal getServiceCharge() {
		return serviceCharge;
	}

	public void setServiceCharge(BigDecimal serviceCharge) {
		this.serviceCharge = serviceCharge;
	}

	public BigDecimal getDebitAmount() {
		return debitAmount;
	}

	public void setDebitAmount(BigDecimal transactionAmount) {
		this.debitAmount = transactionAmount;
	}

	public BigDecimal getCreditAmount() {
		return creditAmount;
	}

	public void setCreditAmount(BigDecimal creditAmount) {
		this.creditAmount = creditAmount;
	}

	public void setPartnerCode(String partnerCode) {
		this.partnerCode = partnerCode;
	}

	public String getPartnerCode() {
		return partnerCode;
	}

	public void setBillerCode(String billerCode) {
		this.billerCode = billerCode;
	}

	public String getBillerCode() {
		return billerCode;
	}
	
	public void setParentTransactionID(Long parentTransactionID) {
		this.parentTransactionID = parentTransactionID;
	}

	public Long getParentTransactionID() {
		return parentTransactionID;
	}

	public String getDestinationMDN() {
		return destinationMDN;
	}

	public void setDestinationMDN(String destinationMDN) {
		this.destinationMDN = destinationMDN;
	}

	public int getNumberOfTriesLeft() {
		return numberOfTriesLeft;
	}

	public void setNumberOfTriesLeft(int numberOfTriesLeft) {
		this.numberOfTriesLeft = numberOfTriesLeft;
	}

	public KeyParameter getKeyParameter() {
		return keyParameter;
	}

	public void setKeyParameter(KeyParameter keyParameter) {
		this.keyParameter = keyParameter;
	}

	public Long getSctlID() {
		return sctlID;
	}

	public void setSctlID(Long sctlID) {
		this.sctlID = sctlID;
	}

	public boolean isUnRegistered() {
		return isUnRegistered;
	}

	public void setUnRegistered(boolean isUnRegistered) {
		this.isUnRegistered = isUnRegistered;
	}

	
	public BigDecimal getMinAmount() {
		return minAmount;
	}

	public void setMinAmount(BigDecimal minAmount) {
		this.minAmount = minAmount;
	}

	public BigDecimal getMaxAmount() {
		return maxAmount;
	}

	public void setMaxAmount(BigDecimal maxAmount) {
		this.maxAmount = maxAmount;
	}

	public String getOneTimePin() {
		return oneTimePin;
	}

	public void setOneTimePin(String oneTimePin) {
		this.oneTimePin = oneTimePin;
	}

	public String getInstitutionID() {
		return institutionID;
	}

	public void setInstitutionID(String institutionID) {
		this.institutionID = institutionID;
	}

	public String getIntegrationName() {
		return integrationName;
	}

	public void setIntegrationName(String integrationName) {
		this.integrationName = integrationName;
	}

	public String getIPAddress() {
		return ipAddress;
	}

	public void setIPAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getAdditionalInfo() {
		return additionalInfo;
	}

	public void setAdditionalInfo(String additionalInfo) {
		this.additionalInfo = additionalInfo;
	}
	
	public String getDestinationName() {
		return destinationName;
	}

	public void setDestinationName(String destinationName) {
		this.destinationName = destinationName;
	}
	
	public String getDestinationAccountNumber() {
		return destinationAccountNumber;
	}

	public void setDestinationAccountNumber(String destinationAccountNumber) {
		this.destinationAccountNumber = destinationAccountNumber;
	}

	/**
	 * @return the mfinoService
	 */
	public MfinoService getMfinoService() {
		return mfinoService;
	}

	/**
	 * @param mfinoService the mfinoService to set
	 */
	public void setMfinoService(MfinoService mfinoService) {
		this.mfinoService = mfinoService;
	}

	/**
	 * @return the partnerService
	 */
	public PartnerService getPartnerService() {
		return partnerService;
	}

	/**
	 * @param partnerService the partnerService to set
	 */
	public void setPartnerService(PartnerService partnerService) {
		this.partnerService = partnerService;
	}

	/**
	 * @return the notificationMessageParserService
	 */
	public NotificationMessageParserService getNotificationMessageParserService() {
		return notificationMessageParserService;
	}

	/**
	 * @param notificationMessageParserService the notificationMessageParserService to set
	 */
	public void setNotificationMessageParserService(
			NotificationMessageParserService notificationMessageParserService) {
		this.notificationMessageParserService = notificationMessageParserService;
	}

	/**
	 * @return the notificationService
	 */
	public NotificationService getNotificationService() {
		return notificationService;
	}

	/**
	 * @param notificationService the notificationService to set
	 */
	public void setNotificationService(NotificationService notificationService) {
		this.notificationService = notificationService;
	}
	

	public String getOtpExpirationTime() {
		return otpExpirationTime;
	}

	public void setOtpExpirationTime(String otpExpirationTime) {
		this.otpExpirationTime = otpExpirationTime;
	}
	
	public void setOtpExpirationTime(Timestamp otpExpirationTime) {
		this.otpExpirationTime = formatDate(otpExpirationTime);
	}

	public String getMaxFavoriteCount() {
		return maxFavoriteCount;
	}

	public void setMaxFavoriteCount(String maxFavoriteCount) {
		this.maxFavoriteCount = maxFavoriteCount;
	}

	public String getFavoriteLabel() {
		return favoriteLabel;
	}

	public void setFavoriteLabel(String favoriteLabel) {
		this.favoriteLabel = favoriteLabel;
	}

	public String getFavoriteValue() {
		return favoriteValue;
	}

	public void setFavoriteValue(String favoriteValue) {
		this.favoriteValue = favoriteValue;
	}

	/**
	 * @return the idNumber
	 */
	public String getIdNumber() {
		return idNumber;
	}

	/**
	 * @param idNumber the idNumber to set
	 */
	public void setIdNumber(String idNumber) {
		this.idNumber = idNumber;
	}

	public String getKycLevel() {
		return kycLevel;
	}

	public void setKycLevel(String kycLevel) {
		this.kycLevel = kycLevel;
	}

	public String getCardAlias() {
		return cardAlias;
	}

	public void setCardAlias(String cardAlias) {
		this.cardAlias = cardAlias;
	}

	public String getOldCardAlias() {
		return oldCardAlias;
	}

	public void setOldCardAlias(String oldCardAlias) {
		this.oldCardAlias = oldCardAlias;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

}
