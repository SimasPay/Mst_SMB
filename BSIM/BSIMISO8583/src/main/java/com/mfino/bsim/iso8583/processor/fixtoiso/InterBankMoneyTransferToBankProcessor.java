package com.mfino.bsim.iso8583.processor.fixtoiso;

import static com.mfino.fix.CmFinoFIX.ISO8583_ProcessingCode_Sinarmas_Transfer_CashOut;

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
import com.mfino.domain.TransactionChargeLog;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMInterBankMoneyTransferToBank;
import com.mfino.hibernate.Timestamp;
import com.mfino.iso8583.definitions.exceptions.AllElementsNotAvailableException;
import com.mfino.util.DateTimeUtil;

public class InterBankMoneyTransferToBankProcessor extends BankRequestProcessor {
	public static final String	MTI	= "0200";
	public InterBankMoneyTransferToBankProcessor() {
		try {
			isoMsg.setMTI(MTI);
		} catch (ISOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public ISOMsg process(CFIXMsg fixmsg) throws AllElementsNotAvailableException {
		CMInterBankMoneyTransferToBank msg = (CMInterBankMoneyTransferToBank) fixmsg;
		Timestamp ts = msg.getTransferTime();//changed to show transfer time as to show same thing in reversal. This is GMT Time
		Timestamp localTS = DateTimeUtil.getLocalTime();
		// use the MDN of the global account
		String mdn = msg.getSourceMDNToUseForBank();
		String fieldDE63 = constructDE63(msg);
		
		if (mdn == null) {
			mdn = msg.getSourceMDN();
		}

		try {
			isoMsg.set(2, msg.getMPan());
			/*//String processingCode = getProcessingCode(msg);
			if(!msg.getProcessingCode().isEmpty() && msg.getProcessingCode().equals(constantFieldsMap.get("IBT_Inquiry_Response_Savings_Account")))
				isoMsg.set(3, constantFieldsMap.get("IBT_Savings_Account"));
			else
				isoMsg.set(3, constantFieldsMap.get("IBT"));*/
			
			String processingCode = null;
			if (CmFinoFIX.BankAccountType_Saving.toString().equals(msg.getSourceBankAccountType())){
				processingCode = "49" + constantFieldsMap.get("SAVINGS_ACCOUNT")+"00";
			if(msg.getProcessingCode()!=null){
				processingCode = "49" + constantFieldsMap.get("SAVINGS_ACCOUNT")+msg.getProcessingCode();
			}
			}
			else if (CmFinoFIX.BankAccountType_Checking.toString().equals(msg.getSourceBankAccountType())){
				processingCode = "49" + constantFieldsMap.get("CHECKING_ACCOUNT")+"00";
			if(msg.getProcessingCode()!=null){
					processingCode = "49" + constantFieldsMap.get("CHECKING_ACCOUNT")+msg.getProcessingCode();
			}
			}
			isoMsg.set(3, processingCode);
			long amount = msg.getAmount().longValue()*(100);
			isoMsg.set(4, StringUtilities.leftPadWithCharacter(amount + "", 18, "0")); // 4	
			
			isoMsg.set(7, DateTimeFormatter.getMMDDHHMMSS(ts)); // 7
			Long transactionID = msg.getTransactionID();
			transactionID = transactionID % 1000000;
			isoMsg.set(11, StringUtilities.leftPadWithCharacter(transactionID.toString(), 6, "0"));// 11
			isoMsg.set(12, DateTimeFormatter.getHHMMSS(localTS)); // 12
			isoMsg.set(13, DateTimeFormatter.getMMDD(localTS)); // 13
			
			isoMsg.set(22, constantFieldsMap.get("22")); // 18
			isoMsg.set(25, constantFieldsMap.get("25")); // 18
			isoMsg.set(26, constantFieldsMap.get("26")); // 18
			
			isoMsg.set(15, DateTimeFormatter.getMMDD(ts)); // 15
			isoMsg.set(18, CmFinoFIX.ISO8583_MerchantType_Delivery_Channel_For_Mobile_Phone); // 18
			isoMsg.set(27, CmFinoFIX.ISO8583_AuthorizationIdentificationResponseLength_Sinarmas.toString()); // 27
			isoMsg.set(32, constantFieldsMap.get("32"));// 32 source bank code (bsim ibt)
			isoMsg.set(33, constantFieldsMap.get("32"));// 33 source bank code (bsim ibt)
			//isoMsg.set(35, msg.getSourceCardPAN());
			isoMsg.set(37, StringUtilities.leftPadWithCharacter(msg.getTransactionID().toString(), 12, "0"));
			
			isoMsg.set(41, constantFieldsMap.get("41"));
			
			isoMsg.set(42, StringUtilities.rightPadWithCharacter(msg.getSourceMDN(), 15, " "));
			isoMsg.set(43, "SMS MFINO");
			isoMsg.set(47, msg.getTransactionID().toString());
			
			isoMsg.set(48, msg.getAdditionalInfo());
			isoMsg.set(49, constantFieldsMap.get("49"));
			isoMsg.set(52, CryptographyService.buildEncryptedPINBlock16(msg.getPin(), msg.getSourceCardPAN(), null)); // 
			isoMsg.set(63, fieldDE63);
			isoMsg.set(100, msg.getBankCode().toString());// source bank code (bsim ibt)
			isoMsg.set(102,msg.getSourceCardPAN());
			isoMsg.set(103,msg.getDestCardPAN());
			isoMsg.set(127, msg.getDestBankCode());
			if(msg.getLanguage().equals(0))
				   isoMsg.set(121,constantFieldsMap.get("english"));
				else
				   isoMsg.set(121,constantFieldsMap.get("bahasa"));
			
			//Destination bank code / inter bank code (bsim ibt)
		}
		catch (ISOException ex) {
			log.error("MoneyTransferToBankProcessor process ", ex);
		}catch (Exception e) {
			log.error("MoneyTransferToBankProcessor process ", e);
		}
		return isoMsg;
	}
	
	private String getProcessingCode(CMInterBankMoneyTransferToBank msg) {
		
		String processingCode = ISO8583_ProcessingCode_Sinarmas_Transfer_CashOut; //TODO: bsn ibt processing code for ibt
		return processingCode;
		
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	private String constructDE63(CMInterBankMoneyTransferToBank request) {
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
				if(tcl.getTransactionCharge().getChargeType().getName().equalsIgnoreCase("charge")){
					serviceCharge = tcl.getCalculatedCharge();
				}
				if(tcl.getTransactionCharge().getChargeType().getName().equalsIgnoreCase("tax")){
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
