package com.mfino.bsim.iso8583.processor.fixtoiso;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.bsim.iso8583.utils.DateTimeFormatter;
import com.mfino.bsim.iso8583.utils.StringUtilities;
import com.mfino.crypto.CryptographyService;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.TransactionChargeLogDAO;
import com.mfino.domain.ChargeType;
import com.mfino.domain.TransactionCharge;
import com.mfino.domain.TransactionChargeLog;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMInterBankTransferInquiryToBank;
import com.mfino.hibernate.Timestamp;
import com.mfino.iso8583.definitions.exceptions.AllElementsNotAvailableException;
import com.mfino.service.CoreServiceFactory;
import com.mfino.service.MoneyService;
import com.mfino.util.DateTimeUtil;

public class InterBankTransferInquiryToBankProcessor extends BankRequestProcessor {
	public InterBankTransferInquiryToBankProcessor() {
		try {
			isoMsg.setMTI("0200");
		} catch (ISOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public ISOMsg process(CFIXMsg fixmsg)
			throws AllElementsNotAvailableException {
		CMInterBankTransferInquiryToBank msg = (CMInterBankTransferInquiryToBank) fixmsg;
		Timestamp ts = DateTimeUtil.getGMTTime();
		Timestamp localTS = DateTimeUtil.getLocalTime();
		String fieldDE63 = constructDE63(msg);
		try {
			String processingCode=null;
			isoMsg.set(2, msg.getMPan());
			if (CmFinoFIX.BankAccountType_Saving.toString().equals(msg.getSourceBankAccountType()))
				processingCode = "37" + constantFieldsMap.get("SAVINGS_ACCOUNT")+"00";
			else if (CmFinoFIX.BankAccountType_Checking.toString().equals(msg.getSourceBankAccountType()))
				processingCode = "37" + constantFieldsMap.get("CHECKING_ACCOUNT")+"00";
			isoMsg.set(3, processingCode);// TODO: need to check the processing code for interbank transfer
			MoneyService ms = CoreServiceFactory.getInstance().getMoneyService();
			BigDecimal amountWithoutCharge = ms.subtract(msg.getAmount(), msg.getServiceChargeAmount());
			long amount = amountWithoutCharge.longValue()*(100);
			isoMsg.set(4, StringUtilities.leftPadWithCharacter(amount + "", 18, "0")); // 4
			isoMsg.set(7, DateTimeFormatter.getMMDDHHMMSS(ts)); // 7
			log.info("After setting --> Timestamp" + DateTimeFormatter.getMMDDHHMMSS(ts));
			Long transactionID = msg.getTransactionID();
			transactionID = transactionID % 1000000;
			isoMsg.set(11, StringUtilities.leftPadWithCharacter(transactionID.toString(), 6, "0"));// 11
			isoMsg.set(12, DateTimeFormatter.getHHMMSS(localTS)); // 12
			isoMsg.set(13, DateTimeFormatter.getMMDD(localTS)); // 13
			isoMsg.set(15, DateTimeFormatter.getMMDD(ts)); // 15
			isoMsg.set(18, CmFinoFIX.ISO8583_MerchantType_Delivery_Channel_For_Mobile_Phone); // 18
			isoMsg.set(22, constantFieldsMap.get("22")); // 18
			isoMsg.set(25, constantFieldsMap.get("25")); // 18
			isoMsg.set(26, constantFieldsMap.get("26")); // 18
			isoMsg.set(27, CmFinoFIX.ISO8583_AuthorizationIdentificationResponseLength_Sinarmas.toString()); // 27
			isoMsg.set(32, constantFieldsMap.get("32"));// 32 source bank code (bsim ibt)
			isoMsg.set(33, constantFieldsMap.get("32"));// 33 source bank code (bsim ibt)
			isoMsg.set(37, StringUtilities.leftPadWithCharacter(msg.getTransactionID().toString(), 12, "0"));
			isoMsg.set(41, constantFieldsMap.get("41"));
			isoMsg.set(42, StringUtilities.rightPadWithCharacter(msg.getSourceMDN(), 15, " "));
			isoMsg.set(43, StringUtilities.rightPadWithCharacter("SMS MFINO", 40, " "));
			isoMsg.set(47, msg.getTransactionID().toString());
			isoMsg.set(49, constantFieldsMap.get("49"));
			isoMsg.set(52, CryptographyService.buildEncryptedPINBlock16(msg.getPin(), msg.getSourceCardPAN(), null)); 
			isoMsg.set(63,fieldDE63);
			isoMsg.set(100, msg.getBankCode().toString());// source bank code (bsim ibt)
			isoMsg.set(102,msg.getSourceCardPAN());
			isoMsg.set(103,msg.getDestCardPAN());
			
			
			if((null != msg.getLanguage()) && !(msg.getLanguage().equals(0))){
				isoMsg.set(121,constantFieldsMap.get("bahasa"));
			}
			else{
				isoMsg.set(121,constantFieldsMap.get("english"));
			}
				
			
			isoMsg.set(127, msg.getDestBankCode());  //Destination bank code / inter bank code (bsim ibt)
		}
		catch (ISOException ex) {
			log.error("TransferInquiryToBankProcessor :: process ", ex);
		}
		return isoMsg;
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	private String constructDE63(CMInterBankTransferInquiryToBank request) {
		Long sctlID = request.getServiceChargeTransactionLogID();
		TransactionChargeLogDAO tclDAO = DAOFactory.getInstance().getTransactionChargeLogDAO();
		
		BigDecimal serviceCharge = new BigDecimal(0);
		BigDecimal tax = new BigDecimal(0);
		String de63 = constantFieldsMap.get("63");
		String strServiceCharge, strTax;
		
		List <TransactionChargeLog> tclList = tclDAO.getBySCTLID(sctlID);
		if(CollectionUtils.isNotEmpty(tclList)){
			for(Iterator<TransactionChargeLog> it = tclList.iterator();it.hasNext();){
				TransactionChargeLog tcl = it.next();
				TransactionCharge txnCharge=tcl.getTransactionCharge();
				ChargeType chargeType = txnCharge.getChargeType();
				String chargeTypeName = chargeType.getName();
				if(chargeTypeName.equalsIgnoreCase("charge")){
					serviceCharge = tcl.getCalculatedCharge();
				}
				if(chargeTypeName.equalsIgnoreCase("tax")){
					tax = tcl.getCalculatedCharge();
				}				
			}
		}
		
		strServiceCharge = "C" + StringUtilities.leftPadWithCharacter(serviceCharge.toBigInteger().toString(),8,"0");
		strTax = "C" + StringUtilities.leftPadWithCharacter(tax.toBigInteger().toString(),8,"0");
		de63 = StringUtilities.replaceNthBlock(de63, 'C', 12,strServiceCharge,9);
		de63 = StringUtilities.replaceNthBlock(de63, 'C', 13,strTax,9);
		return de63;
	}
}
