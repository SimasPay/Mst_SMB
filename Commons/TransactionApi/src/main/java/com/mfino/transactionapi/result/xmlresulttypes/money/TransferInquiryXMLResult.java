package com.mfino.transactionapi.result.xmlresulttypes.money;

import java.text.NumberFormat;

import org.apache.commons.lang.StringUtils;

import com.mfino.constants.XMLResultConstants;
import com.mfino.result.XMLResult;
import com.mfino.util.MfinoUtil;

public class TransferInquiryXMLResult extends XMLResult {
	
	private String	name;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public TransferInquiryXMLResult() {
		super();
	}

	public void render() throws Exception {
		writeStartOfDocument();
		appendXMLTags();
		writeEndOfDocument();
	}
	
	public void appendXMLTags() throws Exception
	{
		buildMessage();

		if (getMultixResponse() != null) {
			String message = getXMlelements().get("ResponseString");
			String transferID =getXMlelements().get("transferID");
			String ptID = getXMlelements().get("parentTxnID");
			String code = getXMlelements().get("code");
			String mfaMode = getXMlelements().get("mfaMode");
			NumberFormat numberFormat = MfinoUtil.getNumberFormat();
			
			getXmlWriter().writeStartElement("message");
			getXmlWriter().writeAttribute("code", code,false);
			getXmlWriter().writeCharacters(message,false);
			getXmlWriter().writeEndElement();

			getXmlWriter().writeStartElement("transactionTime");
			getXmlWriter().writeCharacters(formatDate(getTransactionTime()),false);
			getXmlWriter().writeEndElement();
			
			if (StringUtils.isNotBlank(getReceiverAccountName())) {
				getXmlWriter().writeStartElement(XMLResultConstants.RECEIVER_ACCOUNT_NAME);
				getXmlWriter().writeCharacters(String.valueOf(getReceiverAccountName()),true);
				getXmlWriter().writeEndElement();
			}
			
			if (getDebitAmount() != null) {
				getXmlWriter().writeStartElement(XMLResultConstants.DEBIT_AMOUNT);
				getXmlWriter().writeCharacters(numberFormat.format(getDebitAmount()),true);
				getXmlWriter().writeEndElement();
			}

			if (getCreditAmount() != null) {
				getXmlWriter().writeStartElement(XMLResultConstants.CREDIT_AMOUNT);
				getXmlWriter().writeCharacters(numberFormat.format(getCreditAmount()),true);
				getXmlWriter().writeEndElement();
			}
			
			if (getServiceCharge() != null) {
				getXmlWriter().writeStartElement(XMLResultConstants.CHARGES);
				getXmlWriter().writeCharacters(numberFormat.format(getServiceCharge()),true);
				getXmlWriter().writeEndElement();
			}
			
			if(getAmount()!=null){
				getXmlWriter().writeStartElement("amount");
				getXmlWriter().writeCharacters(numberFormat.format(getAmount()),true);
				getXmlWriter().writeEndElement();
			}
			
			if((getBankName()!=null)||(!StringUtils.isBlank(getBankName()))){
				getXmlWriter().writeStartElement("destinationBank");
				getXmlWriter().writeCharacters(String.valueOf(getBankName()),true);
				getXmlWriter().writeEndElement();
			}
			
			if((getDestinationName()!=null)||(!StringUtils.isBlank(getDestinationName()))){
				getXmlWriter().writeStartElement("destinationName");
				getXmlWriter().writeCharacters(String.valueOf(getDestinationName()),true);
				getXmlWriter().writeEndElement();
			}

			if((getDestinationAccountNumber()!=null)||(!StringUtils.isBlank(getDestinationAccountNumber()))){
				getXmlWriter().writeStartElement("destinationAccountNumber");
				getXmlWriter().writeCharacters(String.valueOf(getDestinationAccountNumber()),true);
				getXmlWriter().writeEndElement();
			}
			
			if((getDestinationMDN()!=null)||(!StringUtils.isBlank(getDestinationMDN()))){
				getXmlWriter().writeStartElement("destinationMDN");
				getXmlWriter().writeCharacters(String.valueOf(getDestinationMDN()),true);
				getXmlWriter().writeEndElement();
			}
			
			if(getAdditionalInfo()!=null)
			{
			    getXmlWriter().writeStartElement("AdditionalInfo");
			    getXmlWriter().writeCharacters(getAdditionalInfo(),false);
			    getXmlWriter().writeEndElement();
			}
			
			if(getResponseMessage()!=null)
			{
			    getXmlWriter().writeStartElement("ResponseMessage");
			    getXmlWriter().writeCharacters(getResponseMessage(),false);
			    getXmlWriter().writeEndElement();
			}

			String success = getXMlelements().get("success");
			if (!StringUtils.isBlank(transferID) && success.equals("0")) {
				getXmlWriter().writeStartElement("transferID");
				getXmlWriter().writeCharacters(transferID,true);
				getXmlWriter().writeEndElement();

				getXmlWriter().writeStartElement("parentTxnID");
				getXmlWriter().writeCharacters(ptID,true);
				getXmlWriter().writeEndElement();
				
			}
			if(getSctlID()!=null){
				getXmlWriter().writeStartElement("sctlID");
				getXmlWriter().writeCharacters(getSctlID().toString(),true);
				getXmlWriter().writeEndElement();
			}
			if(getMfaMode() != null)
			{
				 getXmlWriter().writeStartElement("mfaMode");
				 getXmlWriter().writeCharacters(getMfaMode(),false);
				 getXmlWriter().writeEndElement();
			}
			
			if (getNominalAmount() != null) {
				getXmlWriter().writeStartElement(XMLResultConstants.NOMINAL_AMOUNT);
				getXmlWriter().writeCharacters(numberFormat.format(getNominalAmount()),true);
				getXmlWriter().writeEndElement();
			}
			
			if (getBillDate() != null) {
				getXmlWriter().writeStartElement("billDate");
				getXmlWriter().writeCharacters(getBillDate(),false);
				getXmlWriter().writeEndElement();
			}
			
			if (getInvoiceNo() != null) {
				getXmlWriter().writeStartElement("invoiceNo");
				getXmlWriter().writeCharacters(getInvoiceNo(),false);
				getXmlWriter().writeEndElement();
			}
			
			if(StringUtils.isNotBlank(getName())) {
				
				getXmlWriter().writeStartElement("name");
				getXmlWriter().writeCharacters(getName(),false);
				getXmlWriter().writeEndElement();
			}
		}
		else
		{
			super.render();
		}
			
	}
	
    public String getTransferIDValue(String message)
    {
    	return super.getTransferIDValue(message);
    }

}
