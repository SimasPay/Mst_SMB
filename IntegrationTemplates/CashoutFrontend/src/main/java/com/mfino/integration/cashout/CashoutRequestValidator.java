package com.mfino.integration.cashout;

import java.util.List;

import javax.xml.bind.JAXBElement;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.interswitchng.techquest.Iso8583PostXml;
import com.interswitchng.techquest.Iso8583PostXml.Fields.Field127022.StructureData;

public class CashoutRequestValidator {

	private Iso8583PostXml	request;

	private static Logger	log	= LoggerFactory.getLogger(CashoutRequestValidator.class);
	private static final String WITHDRAWAL_REQUEST_MSG_TYPE = "0200";
	private static final String REVERSAL_REQUEST_MSG_TYPE = "0420";
	private static final String REVERSAL_ADDLN_REQUEST_MSG_TYPE = "0421";
	private static final String PROCESSING_CODE = "010000";
	
	public CashoutRequestValidator(Iso8583PostXml request) {
		this.request = request;
	}

	private String	CustomerID;

	public String getCustomerID() {
		return CustomerID;
	}

	public String getWithdrawlCode() {
		return withdrawlCode;
	}

	private String	withdrawlCode;

	public ValidationResult validate() {
		ValidationResult validationResult = ValidationResult.InvalidRequest;

		log.info("validating atm cashout withdrawal/reversal request : BEGIN");

		List<Iso8583PostXml.Fields> list = request.getFields();
		if (list == null || list.size() == 0)
		{
			log.error("Request is empty");
			return ValidationResult.InvalidRequest;
		}
			
		Iso8583PostXml.Fields fields = request.getFields().get(0);
		
		String msgType = (request.getMsgType()!= null) ? request.getMsgType().getValue() : null;
		if (StringUtils.isBlank(msgType)) {
			log.error("Msg Type is null");
			validationResult = ValidationResult.InvalidRequest;
		}
		else {
			log.info("Received message: "+msgType);
			if (WITHDRAWAL_REQUEST_MSG_TYPE.equals(msgType)) {
				validationResult = validateCashOutWithdrawalRequest(fields);
			}
			else if (REVERSAL_REQUEST_MSG_TYPE.equals(msgType) || REVERSAL_ADDLN_REQUEST_MSG_TYPE.equals(msgType)) {
				validationResult = validateCashOutReversalRequest(fields);
			}
			else
			{
				log.error("Invalid message type:"+msgType);
			}
		}
		log.info("validating atm cashout withdrawal/reversal request : END --> " + validationResult);
		return validationResult;
	}
	
	/**
	 * Validates CashOut withdrawal request
	 * @param fields
	 * @return
	 */
	private ValidationResult validateCashOutWithdrawalRequest(Iso8583PostXml.Fields fields) {
		log.info("Validating the Cash Out Withdrawal Request");
		dumpFields(fields);
		
		if (fields.getField003()==null|| ! PROCESSING_CODE.equals(fields.getField003().getValue()))
		{
			log.error("Invalid value for Field003");
			return ValidationResult.InvalidRequest;
		}
		if (StringUtils.isBlank(fields.getField004()))
		{
			log.error("Invalid value for Field004");
			return ValidationResult.InvalidRequest;
		}
		if (fields.getField052()==null || StringUtils.isBlank(fields.getField052().getValue()))
		{
			log.error("Invalid value for Field0052");
			return ValidationResult.InvalidRequest;
		}
		if (fields.getField127002()==null || StringUtils.isBlank(fields.getField127002().getValue()))
		{
			log.error("Invalid value for Field127002");
			return ValidationResult.InvalidRequest;
		}
		if (fields.getField007()==null || StringUtils.isBlank(fields.getField007().getValue()))
		{
			log.error("Invalid value for Field007");
			return ValidationResult.InvalidRequest;
		}
		if (fields.getField011()==null || StringUtils.isBlank(fields.getField011().getValue()))
		{
			log.error("Invalid value for Field011");
			return ValidationResult.InvalidRequest;
		}
		if (fields.getField012()==null || StringUtils.isBlank(fields.getField012().getValue()))
		{
			log.error("Invalid value for Field012");
			return ValidationResult.InvalidRequest;
		}
		if (fields.getField013()==null || StringUtils.isBlank(fields.getField013().getValue()))
		{
			log.error("Invalid value for Field013");
			return ValidationResult.InvalidRequest;
		}
		if (fields.getField028()==null || StringUtils.isBlank(fields.getField028().getValue()))
		{
			log.error("Invalid value for Field028");
			return ValidationResult.InvalidRequest;
		}
		if (fields.getField041()==null || StringUtils.isBlank(fields.getField041().getValue()))
		{
			log.error("Invalid value for Field041");
			return ValidationResult.InvalidRequest;
		}
		if (fields.getField042()==null || StringUtils.isBlank(fields.getField042().getValue()))
		{
			log.error("Invalid value for Field042");
			return ValidationResult.InvalidRequest;
		}
		if (fields.getField043()==null || StringUtils.isBlank(fields.getField043().getValue()))
		{
			log.error("Invalid value for Field043");
			return ValidationResult.InvalidRequest;
		}
		if (StringUtils.isBlank(fields.getField049()))
		{
			log.error("Invalid value for Field049");			
			return ValidationResult.InvalidRequest;
		}
		if (fields.getField127013()==null || StringUtils.isBlank(fields.getField127013().getValue()))
		{
			log.error("Invalid value for Field127013");
			return ValidationResult.InvalidRequest;
		}
		if (fields.getField127014()==null || StringUtils.isBlank(fields.getField127014().getValue()))
		{
			log.error("Invalid value for Field127014");
			return ValidationResult.InvalidRequest;
		}
		
		if(fields.getField127022()==null)
		{
			log.error("Invalid value for Field127022");
			return ValidationResult.InvalidRequest;
		}
		JAXBElement<StructureData> structureDataElement = fields.getField127022().getValue().getStructureData();
		if(structureDataElement==null)
		{
			log.error("Invalid value for Field127022: No content");
			return ValidationResult.InvalidRequest;
		}
		StructureData structureData = structureDataElement.getValue();
		if(structureData.getCustomerID()==null)
		{
			log.error("Invalid value for Field127022: No CustomerID");
			return ValidationResult.InvalidRequest;
		}
		if(structureData.getWithdrawalCode()==null)
		{
			log.error("Invalid value for Field127022: No Withdrawal Code");
			return ValidationResult.InvalidRequest;
		}
		
		this.CustomerID = structureData.getCustomerID().getValue();
		this.withdrawlCode = structureData.getWithdrawalCode().getValue();
		
		log.info("Cash Out Withdrawal Request succesfully validated");
		return ValidationResult.ValidRequest;
	}
	
	/**
	 * Validates CashOut Reversal request
	 * @param fields
	 * @return
	 */
	private ValidationResult validateCashOutReversalRequest(Iso8583PostXml.Fields fields) {
		log.info("Validating the Cash Out Reversal Request");
		dumpFields(fields);
		if (! PROCESSING_CODE.equals(fields.getField003().getValue()))
		{
			log.error("Invalid value for Field003");
			return ValidationResult.InvalidRequest;
		}
		if (StringUtils.isBlank(fields.getField004()))
		{
			log.error("Invalid value for Field004");
			return ValidationResult.InvalidRequest;
		}
		if (fields.getField041()==null || StringUtils.isBlank(fields.getField041().getValue()))
		{
			log.error("Invalid value for Field041");
			return ValidationResult.InvalidRequest;
		}
		if (StringUtils.isBlank(fields.getField127002().getValue()))
		{
			log.error("Invalid value for Field127002");
			return ValidationResult.InvalidRequest;
		}
		if (StringUtils.isBlank(fields.getField127011().getValue()))
		{
			log.error("Invalid value for Field127011");
			return ValidationResult.InvalidRequest;
		}

		log.info("Cash Out Reversal Request succesfully validated");
		return ValidationResult.ValidRequest;
	}


	public String getCharacterDataFromElement(Element e) {
		Node child = e.getFirstChild();
		if (child instanceof CharacterData) {
			CharacterData cd = (CharacterData) child;
			return cd.getData().trim();
		}
		return "";
	}

	public void dumpFields(Iso8583PostXml.Fields fields)
	{
		StringBuffer buffer = new StringBuffer();
		String lineSeparator = "\n";
		if(fields.getField002()!=null)
		{
			buffer.append("Field002: ");
			buffer.append(fields.getField002().getValue());
			buffer.append(lineSeparator);
		}
		if(fields.getField003()!=null)
		{
			buffer.append("Field003: ");
			buffer.append(fields.getField003().getValue());
			buffer.append(lineSeparator);
		}
		if(fields.getField004()!=null)
		{
			buffer.append("Field004: ");
			buffer.append(fields.getField004());
			buffer.append(lineSeparator);
		}
		if(fields.getField007()!=null)
		{
			buffer.append("Field007: ");
			buffer.append(fields.getField007().getValue());
			buffer.append(lineSeparator);
		}
		if(fields.getField011()!=null)
		{
			buffer.append("Field011: ");
			buffer.append(fields.getField011().getValue());
			buffer.append(lineSeparator);
		}
		if(fields.getField012()!=null)
		{
			buffer.append("Field012: ");
			buffer.append(fields.getField012().getValue());
			buffer.append(lineSeparator);
		}
		if(fields.getField013()!=null)
		{
			buffer.append("Field013: ");
			buffer.append(fields.getField013().getValue());
			buffer.append(lineSeparator);
		}
		if(fields.getField028()!=null)
		{
			buffer.append("Field028: ");
			buffer.append(fields.getField028().getValue());
			buffer.append(lineSeparator);
		}
		if(fields.getField039()!=null)
		{
			buffer.append("Field039: ");
			buffer.append(fields.getField039());
			buffer.append(lineSeparator);
		}
		if(fields.getField041()!=null)
		{
			buffer.append("Field041: ");
			buffer.append(fields.getField041().getValue());
			buffer.append(lineSeparator);
		}
		if(fields.getField042()!=null)
		{
			buffer.append("Field042: ");
			buffer.append(fields.getField042().getValue());
			buffer.append(lineSeparator);
		}
		if(fields.getField043()!=null)
		{
			buffer.append("Field043: ");
			buffer.append(fields.getField043().getValue());
			buffer.append(lineSeparator);
		}
		if(fields.getField049()!=null)
		{
			buffer.append("Field049: ");
			buffer.append(fields.getField049());
			buffer.append(lineSeparator);
		}
		if(fields.getField052()!=null)
		{
			buffer.append("Field052: ");
			buffer.append(fields.getField052().getValue());
			buffer.append(lineSeparator);
		}
		if(fields.getField127002()!=null)
		{
			buffer.append("Field127002: ");
			buffer.append(fields.getField127002().getValue());
			buffer.append(lineSeparator);
		}
		if(fields.getField127011()!=null)
		{
			buffer.append("Field127011: ");
			buffer.append(fields.getField127011().getValue());
			buffer.append(lineSeparator);
		}
		if(fields.getField127013()!=null)
		{
			buffer.append("Field127013: ");
			buffer.append(fields.getField127013().getValue());
			buffer.append(lineSeparator);
		}
		if(fields.getField127014()!=null)
		{
			buffer.append("Field127014: ");
			buffer.append(fields.getField127014().getValue());
			buffer.append(lineSeparator);
		}
		if(fields.getField127022()!=null)
		{
			buffer.append("Field127022: ");
			if(fields.getField127022().getValue()!=null)
			{
				JAXBElement<StructureData> structureDataElement = fields.getField127022().getValue().getStructureData();
				StructureData structureData = structureDataElement.getValue();
				if(structureData.getCustomerID()!=null)
				{
					buffer.append("CustomerID: ");
					buffer.append(structureData.getCustomerID().getValue());
				}
				if(structureData.getWithdrawalCode()!=null && structureData.getWithdrawalCode().getValue()!=null)
				{
					buffer.append("WithdrawalCode: ");
					for(int index=0; index<structureData.getWithdrawalCode().getValue().length(); index++)
					{
						buffer.append('*');
					}
				}
			}
			buffer.append(lineSeparator);
		}
		
		log.info("Fields: "+buffer.toString());
	}
}
