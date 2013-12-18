package com.mfino.vah.converters;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.jpos.iso.ISOMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.hibernate.Timestamp;
import com.mfino.vah.iso8583.DateTimeFormatter;

public class IsoToCashin implements IsoToNativeTransformer {

	private static Logger	log	= LoggerFactory.getLogger(IsoToCashin.class);
	private static final String ATM_CASHIN = "6011";
	private static final String MOBILE_CASHIN = "6017";
	private static final String IBANKING_CASHIN = "6014";

	
	@Override
	public String transform(ISOMsg msg) throws TransformationException {
		StringBuilder retStr = null;
		String ChannelName = "ATM";
		String InstitutionID = "HUB";
		String initiatorMDN = "019";

		try {
			String paymentLogid = msg.getValue(11).toString();
			String targetMDN = msg.getValue(103).toString();
			String amount = msg.getValue(4).toString();
			String tfName = msg.getValue(48).toString().substring(0, msg.getValue(48).toString().length() / 2).trim();
			String tlName = msg.getValue(48).toString().substring(msg.getValue(48).toString().length() / 2, msg.getValue(48).toString().length())
			        .trim();
			
			String paymentMethod = msg.getValue(18).toString();
			
			if(paymentMethod.equals(ATM_CASHIN)){
				log.info("DE18="+paymentMethod+".So txntype is "+ATM_CASHIN);
				paymentMethod = Constants.VA_CASHIN;
			}
			else if(paymentMethod.equals(MOBILE_CASHIN)){
				log.info("DE18="+paymentMethod+".So txntype is "+MOBILE_CASHIN);
				paymentMethod = Constants.DOMPET_CASHIN;
			}
			else if(paymentMethod.equals(IBANKING_CASHIN)){
				log.info("DE18="+paymentMethod+".So txntype is "+IBANKING_CASHIN);
				paymentMethod = Constants.IB_CASHIN;
			}
			else{
				log.error("DE 18 is invalid.received "+paymentMethod);
				throw new Exception("invalid de18 received");
			}
				
			String paymentReference = msg.getValue(37).toString() + "|" + msg.getValue(41).toString() + "|ATM";
			String termID = msg.getValue(41).toString();
			String paymentDate = msg.getValue(7).toString();
			String receiptNo = msg.getValue(37).toString();
			String instName = msg.getValue(32).toString();
			String instCode = msg.getValue(100).toString();

			if (StringUtils.isBlank(paymentReference) || StringUtils.isBlank(paymentLogid) || StringUtils.isBlank(targetMDN)
			        || StringUtils.isBlank(amount) || StringUtils.isBlank(tfName) || StringUtils.isBlank(tlName)
			        || StringUtils.isBlank(paymentMethod) || StringUtils.isBlank(termID) || StringUtils.isBlank(paymentDate)
			        || StringUtils.isBlank(receiptNo)) {

				log.error("One are more required fields are blank.");
				throw new Exception("One are more required fields are blank.");
			}

			BigDecimal bd = new BigDecimal(amount);
			bd = bd.divide(new BigDecimal(100));
			amount = bd.toString();

			Timestamp time = Timestamp.fromString(paymentDate, "MMddHHmmss");
			String timeStr = DateTimeFormatter.getYYMMDD(time);

			targetMDN = targetMDN.substring(4, targetMDN.length());

			retStr = new StringBuilder("<CashInRequest>");
			retStr = retStr.append("<PaymentLogId>").append(paymentLogid).append("</PaymentLogId>");
			retStr = retStr.append("<TargetMdn>").append(targetMDN).append("</TargetMdn>");
			retStr = retStr.append("<TargetFirstName>").append(tfName).append("</TargetFirstName>");
			retStr = retStr.append("<TargetLastName>").append(tlName).append("</TargetLastName>");
			retStr = retStr.append("<Amount>").append(amount).append("</Amount>");
			retStr = retStr.append("<PaymentMethod>").append(paymentMethod).append("</PaymentMethod>");
			retStr = retStr.append("<PaymentReference>").append(paymentReference).append("</PaymentReference>");
			retStr = retStr.append("<TerminalId>").append(termID).append("</TerminalId>");
			retStr = retStr.append("<ChannelName>").append(ChannelName).append("</ChannelName>");
			retStr = retStr.append("<ChannelId>").append("4").append("</ChannelId>");
			retStr = retStr.append("<PaymentDate>").append(timeStr).append("</PaymentDate>");
			retStr = retStr.append("<InstitutionId>").append(instCode).append("</InstitutionId>");
			retStr = retStr.append("<InstitutionName>").append(instName).append("</InstitutionName>");
			retStr = retStr.append("<InitiatorName>").append("ATM").append("</InitiatorName>");
			retStr = retStr.append("<ReceiptNo>").append(receiptNo).append("</ReceiptNo>");
			retStr = retStr.append("<InitiatorMdn>").append(initiatorMDN).append("</InitiatorMdn>");
			retStr = retStr.append("</CashInRequest>");

		}
		catch (Exception ex) {
			log.error("invalid isomsg received.Couldnt extract the required data.");
			TransformationException e = new TransformationException("invalid isomsg received.Couldnt extract the required data.",ex);
			e.fillInStackTrace();
			throw e;
		}
		
		log.info("final constructed string==>"+retStr);
		
		return retStr.toString();
	}
}