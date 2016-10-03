package com.mfino.bsim.iso8583.processor.fixtoiso;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOPackager;
import org.jpos.iso.packager.PostPackager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.bsim.iso8583.utils.DateTimeFormatter;
import com.mfino.bsim.iso8583.utils.StringUtilities;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.TransactionChargeLogDAO;
import com.mfino.domain.TransactionChargeLog;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMAdviceInquiryToBank;
import com.mfino.hibernate.Timestamp;
import com.mfino.iso8583.definitions.exceptions.AllElementsNotAvailableException;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.MfinoUtil;

@Deprecated
public class AdviceInquiryToBankProcessor extends BankRequestProcessor {

	public AdviceInquiryToBankProcessor() {
		try {
			isoMsg.setMTI("0200");
		}
		catch (ISOException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public ISOMsg process(CFIXMsg fixmsg) throws AllElementsNotAvailableException {
		// super.process(fixmsg);
		CMAdviceInquiryToBank request = (CMAdviceInquiryToBank) fixmsg;
		Timestamp ts = DateTimeUtil.getGMTTime();
		Timestamp localTS = DateTimeUtil.getLocalTime();
		Long transactionID = request.getTransactionID();
		transactionID = transactionID % 1000000;
		String fieldDE63 = constructDE63(request);
		try {
			String mpan = MfinoUtil.CheckDigitCalculation(request.getSourceMDN());
			isoMsg.set(2, mpan);

			String processingCode = null;

			if (CmFinoFIX.BankAccountType_Saving.toString().equals(request.getSourceBankAccountType()))
				processingCode = "38" + constantFieldsMap.get("SAVINGS_ACCOUNT")+"00";
			else if (CmFinoFIX.BankAccountType_Checking.toString().equals(request.getSourceBankAccountType()))
				processingCode = "38" + constantFieldsMap.get("CHECKING_ACCOUNT")+"00";

			isoMsg.set(3, processingCode);
			long amount = 0*(100);
			isoMsg.set(4,StringUtilities.leftPadWithCharacter(amount + "", 18, "0"));
			isoMsg.set(7,DateTimeFormatter.getMMDDHHMMSS(ts));
			isoMsg.set(11,StringUtilities.leftPadWithCharacter(transactionID.toString(), 6, "0"));
			isoMsg.set(12,DateTimeFormatter.getHHMMSS(localTS));
			isoMsg.set(13,DateTimeFormatter.getMMDD(localTS));
			isoMsg.set(14,DateTimeFormatter.getMMDD(ts));
			isoMsg.set(15,DateTimeFormatter.getMMDD(ts));
			isoMsg.set(18,CmFinoFIX.ISO8583_MerchantType_Delivery_Channel_For_Mobile_Phone);
			isoMsg.set(22,constantFieldsMap.get("22"));
			isoMsg.set(25,constantFieldsMap.get("25"));
			isoMsg.set(26,constantFieldsMap.get("26"));
			isoMsg.set(27, CmFinoFIX.ISO8583_AuthorizationIdentificationResponseLength_Sinarmas.toString());
			isoMsg.set(32,constantFieldsMap.get("32"));
			isoMsg.set(33,constantFieldsMap.get("32"));
			//isoMsg.set(35,request.getSourceCardPAN());
			isoMsg.set(37, StringUtilities.leftPadWithCharacter(request.getTransactionID().toString(), 12, "0"));
			isoMsg.set(41, constantFieldsMap.get("41"));
			isoMsg.set(42, StringUtilities.rightPadWithCharacter(request.getSourceMDN(), 15, " "));
			isoMsg.set(43, StringUtilities.rightPadWithCharacter("SMS MFINO", 40, " "));
			//isoMsg.set(40,service)
			isoMsg.set(49, constantFieldsMap.get("49"));
			isoMsg.set(61, StringUtilities.leftPadWithCharacter(request.getMerchantData(),20,"0") + StringUtilities.leftPadWithCharacter(request.getInvoiceNo(),32,"0") + "INQUIRY_ADVICE");
			isoMsg.set(63,fieldDE63);
			isoMsg.set(98, request.getBillerCode());
			isoMsg.set(102,request.getSourceCardPAN());
			if(request.getLanguage().equals(0))
				isoMsg.set(121,constantFieldsMap.get("english"));
			else
				isoMsg.set(121,constantFieldsMap.get("bahasa"));
		}

		catch (ISOException ex) {

		}	
		return isoMsg;
	}

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	private String constructDE63(CMAdviceInquiryToBank request) {
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
					serviceCharge = tcl.getCalculatedcharge();
				}
				if(tcl.getTransactionCharge().getChargeType().getName().equalsIgnoreCase("tax")){
					tax = tcl.getCalculatedcharge();
				}				
			}
		}

		strServiceCharge = "C" + StringUtilities.leftPadWithCharacter(serviceCharge.toBigInteger().toString(),8,"0");
		strTax = "C" + StringUtilities.leftPadWithCharacter(tax.toBigInteger().toString(),8,"0");
		de63 = StringUtilities.replaceNthBlock(de63, 'C', 12,strServiceCharge,9);
		de63 = StringUtilities.replaceNthBlock(de63, 'C', 13,strTax,9);
		return de63;
	}
	
	public static void main(String[] args) throws Exception {

		Timestamp ts = new Timestamp();
		System.out.println(String.format("%Tm%<Td%<TH%<TM%<TS", ts));
		System.out.println(String.format("%Tm%<Td", ts));
		System.out.println(String.format("%Ty%<Tm", ts));
		System.out.println(String.format("%TH%<TM%<TS", ts));
		System.out.println(String.format("%TC%<Ty%<Tm%<Td", ts));

		CMAdviceInquiryToBank msg = new CMAdviceInquiryToBank();
		msg.setSourceCardPAN("55555555");
		msg.setSourceBankAccountType(CmFinoFIX.BankAccountType_Saving.toString());
		msg.setTransactionID(12345l);

		AdviceInquiryToBankProcessor toZ = new AdviceInquiryToBankProcessor();
		ISOMsg isoMsg = toZ.process(msg);

		ISOPackager packager = new PostPackager();
		isoMsg.setPackager(packager);
		System.out.println(new String(isoMsg.pack()));

	}

}