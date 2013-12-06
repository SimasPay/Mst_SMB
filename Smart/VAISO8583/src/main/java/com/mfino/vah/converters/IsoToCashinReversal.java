package com.mfino.vah.converters;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.jpos.iso.ISOMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.hibernate.Timestamp;
import com.mfino.vah.iso8583.DateTimeFormatter;

public class IsoToCashinReversal implements IsoToNativeTransformer {

	private static Logger	log	= LoggerFactory.getLogger(IsoToCashinReversal.class);

	@Override
	public String transform(ISOMsg msg) throws TransformationException {

		StringBuilder retStr = null;
		String ChannelName = "ATM";
		String InstitutionID = "019";
		String initiatorMDN = "019";

		try {
			String targetMDN = msg.getValue(103).toString();
			String amount = msg.getValue(4).toString();
			
			String de48 = (msg.getValue(48) != null) ? msg.getValue(48).toString() : null;
			
			String tfName ="_F",tlName="L_";
			
			if((null != de48) && (!("".equals(de48.toString())))){
				tfName = msg.getValue(48).toString().substring(0, msg.getValue(48).toString().length() / 2).trim();
				tlName = msg.getValue(48).toString().substring(msg.getValue(48).toString().length() / 2, msg.getValue(48).toString().length())
			        .trim();
			}
			
			String paymentMethod = msg.getValue(18).toString();
			String paymentReference = msg.getValue(37).toString() + "|" + msg.getValue(41).toString() + "|ATM";
			String termID = msg.getValue(41).toString();
			String paymentDate = msg.getValue(7).toString();
			String receiptNo = msg.getValue(37).toString();
					
			//STAN of original msg is recieved in DE#90
			String originalMsg = msg.getValue(90).toString();
			String paymentLogid = originalMsg.substring(4, 10);//
			
			String instName = msg.getValue(32).toString();

			if (StringUtils.isBlank(paymentReference) || StringUtils.isBlank(paymentLogid) || StringUtils.isBlank(targetMDN)
			        || StringUtils.isBlank(amount) || StringUtils.isBlank(paymentMethod) || StringUtils.isBlank(termID) || StringUtils.isBlank(paymentDate)
			        || StringUtils.isBlank(receiptNo)) {

				log.error("One are more required fields are blank.");
				throw new Exception("One are more required fields are blank.");
			}

			BigDecimal bd = new BigDecimal(amount);
			bd = bd.divide(new BigDecimal(100));
			amount = bd.negate().toString();

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
			retStr = retStr.append("<PaymentDate>").append(timeStr).append("</PaymentDate>");
			retStr = retStr.append("<InstitutionId>").append(InstitutionID).append("</InstitutionId>");
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